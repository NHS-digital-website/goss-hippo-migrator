package uk.nhs.digital.gossmigrator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.digital.gossmigrator.Report.AssetReportWriter;
import uk.nhs.digital.gossmigrator.config.Config;
import uk.nhs.digital.gossmigrator.misc.GossExportHelper;
import uk.nhs.digital.gossmigrator.model.goss.GossContent;
import uk.nhs.digital.gossmigrator.model.goss.GossFile;
import uk.nhs.digital.gossmigrator.model.hippo.Asset;
import uk.nhs.digital.gossmigrator.model.hippo.AssetReportable;
import uk.nhs.digital.gossmigrator.model.hippo.HippoImportable;
import uk.nhs.digital.gossmigrator.model.hippo.Image;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static uk.nhs.digital.gossmigrator.config.Config.ASSET_SOURCE_FOLDER;

public class AssetImporter {

    private final static Logger LOGGER = LoggerFactory.getLogger(AssetImporter.class);

    private List<HippoImportable> importableAssetItems = new ArrayList<>();

    private long totalFileSize = 0;

    public void createAssetHippoImportables() {
        if (!Paths.get(ASSET_SOURCE_FOLDER).toFile().exists()) {
            LOGGER.warn("Assets file path does not exist:{}", Paths.get(ASSET_SOURCE_FOLDER));
        } else {
            for (GossFile file : GossImporter.digitalData.getGossFileMap().values()) {
                if (file.getReferences().size() > 0) {
                    boolean hasImportedReference = false;

                    for(Long articleId : file.getReferences()){
                        GossContent article = GossImporter.digitalData.getArticlesContentList().getById(articleId);
                        if(article.isRelevantContentFlag()){
                            hasImportedReference = true;
                            break;
                        }
                    }
                    if (hasImportedReference) {
                        if (file.getSize() > 1024 * 1024 * 10) {
                            StringBuilder refs = new StringBuilder();
                            boolean first = true;
                            for (Long ref : file.getReferences()) {
                                if (!first) {
                                    refs.append(", ");
                                }
                                refs.append(ref);
                                first = false;
                            }
                            LOGGER.warn("MediaId:{}, File {} > 10Mb. Is:{}Mb, Referenced by Articles[{}]", file.getId(), file.getFilePathOnDisk(), file.getSize() / (1024 * 1024), refs.toString());
                        } else {
                            totalFileSize = totalFileSize + file.getSize();
                            createAsset(file);
                        }
                    }
                }
            }
        }
        LOGGER.info("Total asset size: {}Mb", totalFileSize / (1024 * 1024));
        System.out.println("Total file size:" + totalFileSize / (1024 * 1024) + "Mb.");
    }

    private void createAsset(GossFile file) {

        // Create the Asset model object and add to importables.
        HippoImportable a;
        if (GossExportHelper.isImage(file.getFileName().toLowerCase())) {
            a = new Image(file.getFileName()
                    , file.getJcrPath(), Paths.get(file.getFilePathOnDisk()), file);
        } else if (GossExportHelper.isSupportedAsset(file.getFileName().toLowerCase())) {
            a = new Asset(file.getFileName()
                    , file.getJcrPath(), Paths.get(file.getFilePathOnDisk()), file);
        } else {
            LOGGER.warn("Unsupported file type {}.  This is probably fine.", file.getFileName());
            a = new Asset(file.getFileName()
                    , file.getJcrPath(), Paths.get(file.getFilePathOnDisk()), file);
        }
        importableAssetItems.add(a);
        AssetReportWriter.addAssetRow((AssetReportable) a);
    }

    public int writeHippoAssetImportables() {
        ImportableFileWriter writer = new ImportableFileWriter();
        writer.writeImportableFiles(importableAssetItems, Config.ASSET_TARGET_FOLDER);
        return writer.getAssetOutputFolder();
    }

}
