package uk.nhs.digital.gossmigrator.model.hippo;

import org.apache.tika.Tika;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Asset extends HippoImportable {

    String filePath;
    String lastModifiedDate;

    public Asset(String localizedName, String jcrPath, Path sourceFile) {
        super(localizedName, jcrPath);
        this.filePath = "file:///" + sourceFile.toString();
        lastModifiedDate = "2018-01-19T10:07:03.592Z";
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
            return tika.detect(Paths.get(getFilePath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
