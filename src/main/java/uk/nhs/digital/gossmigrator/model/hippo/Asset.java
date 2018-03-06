package uk.nhs.digital.gossmigrator.model.hippo;

import uk.nhs.digital.gossmigrator.model.goss.GossFile;

import java.nio.file.Path;

public class Asset extends FileImportable implements AssetReportable {

    public Asset(String localizedName, String jcrPath, Path sourceFile, GossFile gossFile) {
        super(localizedName, jcrPath, sourceFile, gossFile);
    }
}
