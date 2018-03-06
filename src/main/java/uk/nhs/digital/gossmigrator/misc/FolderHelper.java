package uk.nhs.digital.gossmigrator.misc;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.digital.gossmigrator.config.Config;
import uk.nhs.digital.gossmigrator.config.Constants;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

    public static void zipFolder(String folder){
        // Validate folder.
        if(!Paths.get(folder, "exim").toFile().exists()){
            LOGGER.error("Expected folder exim under {} for correct zip format", folder);
        }else{
            try(ZipOutputStream zipFile = new ZipOutputStream(new FileOutputStream(
                    Paths.get(folder, Constants.Output.ZIP_FILE_NAME).toString()))) {

                Path srcPath = Paths.get(folder, Constants.Output.JSON_DIR);

                compressDirectoryToZipfile(srcPath.getParent().toString(), srcPath.getFileName().toString(), zipFile);
                srcPath = Paths.get(folder, Constants.Output.ASSET_DIR);
                if(!srcPath.toFile().exists()){
                    Files.createDirectories(srcPath);
                }
                compressDirectoryToZipfile(srcPath.getParent().toString(), srcPath.getFileName().toString(), zipFile);
            }catch (IOException e){

            }
        }
    }

    private static void compressDirectoryToZipfile(String rootDir, String sourceDir, ZipOutputStream out) throws IOException, FileNotFoundException {
        String dir = Paths.get(rootDir, sourceDir).toString();
        for (File file : new File(dir).listFiles()) {
            if (file.isDirectory()) {
                compressDirectoryToZipfile(rootDir, Paths.get(sourceDir,file.getName()).toString(), out);
            } else {
                ZipEntry entry = new ZipEntry(Paths.get(sourceDir,file.getName()).toString());
                out.putNextEntry(entry);
                try(FileInputStream in = new FileInputStream(Paths.get(rootDir, sourceDir, file.getName()).toString())) {
                    IOUtils.copy(in, out);
                }
            }
        }
    }
}
