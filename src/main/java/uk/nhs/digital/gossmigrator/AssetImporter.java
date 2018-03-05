package uk.nhs.digital.gossmigrator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.digital.gossmigrator.Report.AssetReportWriter;
import uk.nhs.digital.gossmigrator.config.Config;
import uk.nhs.digital.gossmigrator.misc.FolderHelper;
import uk.nhs.digital.gossmigrator.misc.GossExportHelper;
import uk.nhs.digital.gossmigrator.model.hippo.Asset;
import uk.nhs.digital.gossmigrator.model.hippo.AssetReportable;
import uk.nhs.digital.gossmigrator.model.hippo.HippoImportable;
import uk.nhs.digital.gossmigrator.model.hippo.Image;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static uk.nhs.digital.gossmigrator.config.Config.*;
import static uk.nhs.digital.gossmigrator.config.Constants.OUTPUT_FILE_TYPE_SUFFIX;

public class AssetImporter {

    private final static Logger LOGGER = LoggerFactory.getLogger(AssetImporter.class);

    private List<HippoImportable> importableAssetItems = new ArrayList<>();

    public void createAssetHippoImportables() {
        FolderHelper.cleanFolder(Paths.get(ASSET_TARGET_FOLDER), OUTPUT_FILE_TYPE_SUFFIX);
        if (!Paths.get(ASSET_SOURCE_FOLDER).toFile().exists()) {
            LOGGER.warn("Assets file path does not exist:{}", Paths.get(ASSET_SOURCE_FOLDER));
        } else {
            try {
                Files.walk(Paths.get(ASSET_SOURCE_FOLDER)).filter(p -> p.toFile().isFile())
                        .forEach(this::createAsset);
            } catch (IOException e) {
                LOGGER.error("Failed reading Asset files.", e);
                throw new RuntimeException(e);
            }
        }
    }

    private void createAsset(Path file) {
        // Remove the local source file path and replace with the jcr prefix.
        int sourcePathParts = Paths.get(Config.ASSET_SOURCE_FOLDER).getNameCount();
        String subPart = file.subpath(sourcePathParts, file.getNameCount()).toString();

        // Create the Asset model object and add to importables.
        String jcrDir;
        HippoImportable a;
        if (GossExportHelper.isImage(subPart)) {
            jcrDir = JCR_GALLERY_ROOT;
            a = new Image(file.getFileName().toString()
                    , Paths.get(jcrDir, subPart).toString(), file);
        } else if (GossExportHelper.isSupportedAsset(subPart)) {
            jcrDir = JCR_ASSET_ROOT;
            a = new Asset(file.getFileName().toString()
                    , Paths.get(jcrDir, subPart).toString(), file);
        } else {
            LOGGER.warn("Unsupported file type {}", subPart);
            jcrDir = JCR_ASSET_ROOT;
            a = new Asset(file.getFileName().toString()
                    , Paths.get(jcrDir, subPart).toString(), file);
        }
        importableAssetItems.add(a);
        AssetReportWriter.addAssetRow((AssetReportable) a);
    }

    public void writeHippoAssetImportables() {
        ImportableFileWriter writer = new ImportableFileWriter();
        writer.writeImportableFiles(importableAssetItems);
    }

}
