package uk.nhs.digital.gossmigrator.model.hippo;

import org.apache.tika.Tika;
import uk.nhs.digital.gossmigrator.config.Config;
import uk.nhs.digital.gossmigrator.model.goss.GossFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class FileImportable extends HippoImportable {
    private String filePath;
    private Path sourceFilePath;
    private String lastModifiedDate;
    private long fileSize;
    private Path sourceFilePathRelative;

    FileImportable(String localizedName, String jcrPath, Path sourceFile, GossFile gossFile) {
        super(localizedName, jcrPath, localizedName);

        id = gossFile.getId();
        lastModifiedDate = "2018-01-19T10:07:03.592Z";
        sourceFilePath = sourceFile;
        fileSize = gossFile.getSize();
        int firstPart = Paths.get(Config.ASSET_SOURCE_FOLDER).getNameCount();
        sourceFilePathRelative = sourceFilePath.subpath(firstPart, sourceFilePath.getNameCount());
        this.filePath = "file://{IMPORT_PACKAGE_ATTACHMENTS_LOCATION}//" + sourceFilePathRelative.toString();
    }

    public String getFilePath() {
        return filePath;
    }

    @SuppressWarnings("unused") // Used in template
    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    @SuppressWarnings("unused") // Used in template
    public String getMimeType() {
        try {
            Tika tika = new Tika();
            return tika.detect(sourceFilePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Path getSourceFilePath() {
        return sourceFilePath;
    }

    public long getFileSize() {
        return fileSize;
    }

    public Path getSourceFilePathRelative() {
        return sourceFilePathRelative;
    }
}
