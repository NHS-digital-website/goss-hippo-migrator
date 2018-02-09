package uk.nhs.digital.gossmigrator.misc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
                        //noinspection ResultOfMethodCallIgnored
                        toDelete.delete();
                    }
                }
            }
        } else {
            //noinspection ResultOfMethodCallIgnored
            f.mkdir();
        }
    }

    public static String dosToUnixPath(String path) {
        // Remove windows drive.
        String returnValue;

        String[] windowsPathParts = path.split(":");
        if (windowsPathParts.length == 2) {
            returnValue = windowsPathParts[1];
        } else {
            returnValue = windowsPathParts[0];
        }

        // Turn windows slashes into unix.
        return returnValue.replaceAll("\\\\", "/");
    }
}
