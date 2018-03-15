package uk.nhs.digital.gossmigrator.model.hippo;

import uk.nhs.digital.gossmigrator.config.Config;
import uk.nhs.digital.gossmigrator.model.goss.GossFile;

import java.nio.file.Paths;

public class S3File extends FileImportable {

    String s3ExternalStorageRef;
    String s3Url;
    String displayName;

    S3File(GossFile gossFile) {
        super(gossFile.getFileName(), "", Paths.get(gossFile.getFilePathOnDisk()), gossFile);
        s3ExternalStorageRef = gossFile.getRelativeFilePath();
        s3Url = Config.S3_ROOT_URL + gossFile.getRelativeFilePath();
        displayName = null != gossFile.getDisplayText() ? gossFile.getDisplayText() : gossFile.getFileName();

    }

    public String getS3ExternalStorageRef(){
        return s3ExternalStorageRef;
    }

    public String getS3Url() {
        return s3Url;
    }

    public String getDisplayName() {
        return displayName;
    }
}
