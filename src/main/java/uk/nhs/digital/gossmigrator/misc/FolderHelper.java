package uk.nhs.digital.gossmigrator.misc;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.digital.gossmigrator.config.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FolderHelper {

    private final static Logger LOGGER = LoggerFactory.getLogger(FolderHelper.class);

    public static void cleanFolder(Path folder) {
        FileUtils.deleteQuietly(folder.toFile());
        try {
            Files.createDirectories(folder);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
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

    public static void zipFolder(String folder, String toDirectory) {
        // Ensure target exists
        try {
            Files.createDirectories(Paths.get(toDirectory));
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            return;
        }

        // Validate folder from.
        if (!Paths.get(folder, "exim").toFile().exists()) {
            LOGGER.error("Expected folder exim under {} for correct zip format", folder);
        } else {
            try (ZipOutputStream zipFile = new ZipOutputStream(new FileOutputStream(
                    Paths.get(toDirectory, Constants.Output.ZIP_FILE_NAME).toString()))) {

                Path srcPath = Paths.get(folder, Constants.Output.JSON_DIR);

                compressDirectoryToZipfile(srcPath.getParent().toString(), srcPath.getFileName().toString(), zipFile);
                srcPath = Paths.get(folder, Constants.Output.ASSET_DIR);
                if (!srcPath.toFile().exists()) {
                    Files.createDirectories(srcPath);
                }
                compressDirectoryToZipfile(srcPath.getParent().toString(), srcPath.getFileName().toString(), zipFile);
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    private static void compressDirectoryToZipfile(String rootDir, String sourceDir, ZipOutputStream out) throws IOException {
        String dir = Paths.get(rootDir, sourceDir).toString();
        for (File file : Objects.requireNonNull(new File(dir).listFiles())) {
            if (file.isDirectory()) {
                compressDirectoryToZipfile(rootDir, Paths.get(sourceDir, file.getName()).toString(), out);
            } else {
                ZipEntry entry = new ZipEntry(Paths.get(sourceDir, file.getName()).toString());
                out.putNextEntry(entry);
                try (FileInputStream in = new FileInputStream(Paths.get(rootDir, sourceDir, file.getName()).toString())) {
                    IOUtils.copy(in, out);
                }
            }
        }
    }

    public static void makeDummyDataBinFile(String parentDirectory) {
        Path folderToCreate = Paths.get(parentDirectory, "attachments");
        Path fileToCreate = Paths.get(parentDirectory, "attachments", "data.bin");
        byte[] emptyContent = "".getBytes();
        try {
            Files.createDirectories(folderToCreate);
            Files.write(fileToCreate, emptyContent);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public static void moveFileToFolderAndCreateTree(Path from, Path to){
        try {
            Files.createDirectories(Paths.get(to.toFile().getParent()));
            Files.move(from, to);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
