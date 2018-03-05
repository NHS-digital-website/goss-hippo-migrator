package uk.nhs.digital.gossmigrator.model.hippo;

import org.apache.tika.Tika;
import uk.nhs.digital.gossmigrator.misc.TextHelper;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Asset extends HippoImportable implements AssetReportable {

    String filePath;
    Path sourceFilePath;
    String lastModifiedDate;

    public Asset(String localizedName, String jcrPath, Path sourceFile, Long mediaId) {
        super(localizedName, jcrPath, localizedName);
        this.filePath = "file:///" + sourceFile.toString();
        id = mediaId;
        lastModifiedDate = "2018-01-19T10:07:03.592Z";
        sourceFilePath = sourceFile;
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
}
