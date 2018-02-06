package uk.nhs.digital.gossmigrator.misc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.digital.gossmigrator.GossImporter;

import java.io.File;
import java.nio.file.Path;

public class FolderHelper {

    private final static Logger LOGGER = LoggerFactory.getLogger(FolderHelper.class);

    /**
     * Remove .json files from folder or create folder if not exists.
     * If non json files in folder log warning.
     * Does not delete recursively.
     * Create Directory if not exists.
     */
    public static void cleanFolder(Path folder, String fileExtension) {
        File f = folder.toFile();
        // Check exists.
        if (f.exists()) {
            // Check is folder.
            if (f.isFile()) {
                LOGGER.error("Expected {} to be a directory not a file.", f);
            } else {
                for (File toDelete : f.listFiles()) {
                    if (toDelete.getName().endsWith(fileExtension)) {
                        toDelete.delete();
                    }
                }
            }
        } else {
            f.mkdir();
        }
    }
}
