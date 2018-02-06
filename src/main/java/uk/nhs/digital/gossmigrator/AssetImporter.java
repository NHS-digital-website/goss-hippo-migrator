package uk.nhs.digital.gossmigrator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.digital.gossmigrator.config.Config;
import uk.nhs.digital.gossmigrator.misc.FolderHelper;
import uk.nhs.digital.gossmigrator.model.hippo.Asset;
import uk.nhs.digital.gossmigrator.model.hippo.HippoImportable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static uk.nhs.digital.gossmigrator.config.Config.ASSET_SOURCE_FOLDER;
import static uk.nhs.digital.gossmigrator.config.Config.ASSET_TARGET_FOLDER;
import static uk.nhs.digital.gossmigrator.config.Config.JCR_ASSET_ROOT;
import static uk.nhs.digital.gossmigrator.config.Constants.OUTPUT_FILE_TYPE_SUFFIX;

public class AssetImporter {

    private final static Logger LOGGER = LoggerFactory.getLogger(AssetImporter.class);

    List<HippoImportable> importableAssetItems = new ArrayList<>();


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
        Asset a = new Asset(file.getFileName().toString(), JCR_ASSET_ROOT + subPart, file);
        importableAssetItems.add(a);
    }

    public void writeHippoAssetImportables() {
        ImportableFileWriter writer = new ImportableFileWriter();
        writer.writeImportableFiles(importableAssetItems, Paths.get(ASSET_TARGET_FOLDER));
    }

}
