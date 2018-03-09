package uk.nhs.digital.gossmigrator.model.hippo;

import uk.nhs.digital.gossmigrator.model.goss.GossContent;
import uk.nhs.digital.gossmigrator.model.goss.GossFolder;

public class Folder extends HippoImportable {
    private Folder(String localizedName, String jcrPath, String jcrNodeName, GossContent content) {
        super(localizedName, jcrPath, jcrNodeName);
        id = content.getId();
    }

    public static HippoImportable getInstance(GossFolder gossContent) {
        return new Folder(gossContent.getHeading(), gossContent.getJcrPath(), gossContent.getJcrNodeName(), gossContent);
    }
}
