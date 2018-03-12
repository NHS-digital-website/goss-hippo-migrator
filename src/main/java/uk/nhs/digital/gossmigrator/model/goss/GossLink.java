package uk.nhs.digital.gossmigrator.model.goss;

import org.json.simple.JSONObject;
import uk.nhs.digital.gossmigrator.GossImporter;
import uk.nhs.digital.gossmigrator.misc.GossExportHelper;

import static uk.nhs.digital.gossmigrator.model.goss.enums.GossExportFieldNames.*;

public class GossLink {
    private Long id;
    private String address;
    private String displayText;
    private String description;

    public GossLink(JSONObject linkJson) {
        id = GossExportHelper.getIdOrError(linkJson, LINK_ID);
        address = GossExportHelper.getString(linkJson, LINK_ADDRESS, id);
        displayText = GossExportHelper.getString(linkJson, LINK_DISPLAY_TEXT, id);
        description = GossExportHelper.getString(linkJson, LINK_DESCRIPTION, id);
    }

    public Long getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public String getDisplayText() {
        return displayText;
    }

    public String getDescription() {
        return description;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}