package uk.nhs.digital.gossmigrator;

import freemarker.core.JSONOutputFormat;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.digital.gossmigrator.config.Config;
import uk.nhs.digital.gossmigrator.config.Constants;
import uk.nhs.digital.gossmigrator.misc.TextHelper;
import uk.nhs.digital.gossmigrator.model.hippo.FileImportable;
import uk.nhs.digital.gossmigrator.model.hippo.Folder;
import uk.nhs.digital.gossmigrator.model.hippo.HippoImportable;
import uk.nhs.digital.gossmigrator.model.hippo.Redirect;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import static uk.nhs.digital.gossmigrator.config.Config.*;
import static uk.nhs.digital.gossmigrator.config.Constants.OUTPUT_FILE_TYPE_SUFFIX;
import static uk.nhs.digital.gossmigrator.config.Constants.Output.JSON_DIR;

public class ImportableFileWriter {
    private final static Logger LOGGER = LoggerFactory.getLogger(ImportableFileWriter.class);

    private static Configuration cfg;
    private long assetBytesCopied = 0;
    private Integer assetOutputFolder = 0;

    void writeImportableFiles(final List<? extends HippoImportable> importableItems, String outputFolder, boolean isDigital) {
        LOGGER.info("Writing content to:{}", outputFolder);

        for (int i = 1; i <= importableItems.size(); i++) {
            Path targetDir;
            final HippoImportable importableItem = importableItems.get(i - 1);
            if (Config.CONTENT_TARGET_FOLDER.equals(outputFolder)) {
                if(importableItem instanceof Folder){
                    targetDir = Paths.get(FOLDERS_TARGET_FOLDER, JSON_DIR);
                } else if(importableItem instanceof Redirect){
                  if(isDigital){
                      targetDir = Paths.get(URLREWRITE_DIGITAL_TARGET_FOLDER, JSON_DIR);
                  }else{
                      targetDir = Paths.get(URLREWRITE_CONTENT_TARGET_FOLDER, JSON_DIR);
                  }
                } else if (importableItem.isLive()) {
                    targetDir = Paths.get(LIVE_CONTENT_TARGET_FOLDER, JSON_DIR);
                } else {
                    targetDir = Paths.get(NON_LIVE_CONTENT_TARGET_FOLDER, JSON_DIR);
                }
            } else if (importableItem instanceof FileImportable) {
                // See if need to move to new output folder
                FileImportable fileImportable = (FileImportable) importableItem;
                if (assetBytesCopied + fileImportable.getFileSize() >
                        Config.MAX_ASSET_SIZE_MB_IN_ZIP * 1024 * 1024) {
                    assetOutputFolder++;
                    assetBytesCopied = 0;
                } else {
                    assetBytesCopied = assetBytesCopied + fileImportable.getFileSize();
                }

                targetDir = Paths.get(outputFolder, assetOutputFolder.toString(), JSON_DIR);
                try {
                    Path to = Paths.get(outputFolder, assetOutputFolder.toString(),
                            Constants.Output.ASSET_DIR, fileImportable.getSourceFilePathRelative().toString());
                    if(!to.toFile().getParentFile().exists()){
                        Files.createDirectories(to.toFile().getParentFile().toPath());
                    }
                    Files.copy(fileImportable.getSourceFilePath(),to);
                } catch (IOException e) {
                    LOGGER.error("Failed copying file:{}", fileImportable.getSourceFilePath(), e);
                }
            } else {
                LOGGER.error("Unexpected importable to write.  So far either content or assets.");
                targetDir = Paths.get(outputFolder);
            }

            writeImportableFile(
                    importableItem,
                    getFileName(i, importableItem),
                    targetDir
            );

        }
        LOGGER.info("Wrote {} files.", importableItems.size());
    }

    private void writeImportableFile(final HippoImportable importableItem,
                                     final String fileName,
                                     final Path targetDir) {

        try {

            Path targetFilePath = Paths.get(targetDir.toString(), fileName);
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }

            final String itemTypeName = importableItem.getClass().getSimpleName().toLowerCase();

            final Template template = getFreemarkerConfiguration()
                    .getTemplate(itemTypeName + ".json.ftl");

            final Writer writer = new StringWriter();

            template.process(new HashMap<String, Object>() {{
                put(itemTypeName, importableItem);
            }}, writer);

            final String importableFileContent = writer.toString();

            LOGGER.debug("Writing:{}", targetFilePath);

            Files.write(targetFilePath, importableFileContent.getBytes());

        } catch (final Exception e) {
            // If we fail with one file, make a note of the document that failed and carry on
            //   migrationReport.logError(e, "Failed to write out item:", "Item will not be imported", importableItem.toString());

            LOGGER.error("Failed writing file:{}" + importableItem.toString(), e);
        }
    }

    private static Configuration getFreemarkerConfiguration() {
        if (cfg == null) {
            cfg = new Configuration(Configuration.VERSION_2_3_26);
            cfg.setClassForTemplateLoading(ImportableFileWriter.class, "/templates");
            cfg.setDefaultEncoding("UTF-8");
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            cfg.setLogTemplateExceptions(false);
            cfg.setOutputFormat(JSONOutputFormat.INSTANCE);
        }

        return cfg;
    }

    private static String getFileName(final int i, final HippoImportable importableItem) {

        return String.format(
                "%06d%s_%06d_%s%s_%s" + OUTPUT_FILE_TYPE_SUFFIX,
                i,
                StringUtils.leftPad("", 1, '_'),
                importableItem.getId(),
                importableItem.getClass().getSimpleName().toUpperCase(),
                "",
                TextHelper.toLowerCaseDashedValue(importableItem.getLocalizedName())
        );
    }

    public Integer getAssetOutputFolder() {
        return assetOutputFolder;
    }
}
