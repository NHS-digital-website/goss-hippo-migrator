package uk.nhs.digital.gossmigrator.model.goss;

import org.json.simple.JSONObject;
import uk.nhs.digital.gossmigrator.misc.GossExportHelper;
import uk.nhs.digital.gossmigrator.model.goss.enums.GossExportFieldNames;

import static uk.nhs.digital.gossmigrator.model.goss.enums.GossExportFieldNames.LINK_ADDRESS;
import static uk.nhs.digital.gossmigrator.model.goss.enums.GossExportFieldNames.LINK_ID;

public class GossLink {
    private Long id;
    private String address;

    public GossLink(JSONObject linkJson) {
        id = GossExportHelper.getIdOrError(linkJson, LINK_ID);
        address = GossExportHelper.getString(linkJson, LINK_ADDRESS, id);
    }

    public Long getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }
}
