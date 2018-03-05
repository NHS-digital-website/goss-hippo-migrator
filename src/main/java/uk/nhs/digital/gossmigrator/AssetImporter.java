package uk.nhs.digital.gossmigrator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.digital.gossmigrator.Report.AssetReportWriter;
import uk.nhs.digital.gossmigrator.config.Config;
import uk.nhs.digital.gossmigrator.misc.FolderHelper;
import uk.nhs.digital.gossmigrator.misc.GossExportHelper;
import uk.nhs.digital.gossmigrator.model.goss.GossFile;
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
            for (GossFile file : GossImporter.gossData.getGossFileMap().values()) {
                if (file.getReferences().size() > 0) {
                    createAsset(file);
                }
            }
        }
    }

    private void createAsset(GossFile file) {


        // Create the Asset model object and add to importables.
        HippoImportable a;
        if (GossExportHelper.isImage(file.getFileName().toLowerCase())) {
            a = new Image(file.getFileName()
                    , file.getJcrPath(), Paths.get(file.getFilePathOnDisk()), file.getId());
        } else if (GossExportHelper.isSupportedAsset(file.getFileName().toLowerCase())) {
            a = new Asset(file.getFileName()
                    , file.getJcrPath(), Paths.get(file.getFilePathOnDisk()), file.getId());
        } else {
            LOGGER.warn("Unsupported file type {}", file.getFileName());
            a = new Asset(file.getFileName()
                    , file.getJcrPath(), Paths.get(file.getFilePathOnDisk()), file.getId());
        }
        importableAssetItems.add(a);
        AssetReportWriter.addAssetRow((AssetReportable) a);

    }

    public void writeHippoAssetImportables() {
        ImportableFileWriter writer = new ImportableFileWriter();
        writer.writeImportableFiles(importableAssetItems, Config.ASSET_TARGET_FOLDER);
    }

}
