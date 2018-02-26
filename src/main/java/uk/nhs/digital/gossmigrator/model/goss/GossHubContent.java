package uk.nhs.digital.gossmigrator.model.goss;

import org.json.simple.JSONObject;
import uk.nhs.digital.gossmigrator.misc.GossExportHelper;
import uk.nhs.digital.gossmigrator.model.goss.enums.ContentType;
import uk.nhs.digital.gossmigrator.model.goss.enums.GossExportFieldNames;

import static uk.nhs.digital.gossmigrator.model.goss.enums.ContentType.HUB;
import static uk.nhs.digital.gossmigrator.model.goss.enums.GossExportFieldNames.EXTRA_OBJECT_ID;

public class GossHubContent extends GossServiceContent {

    private String introduction;

    private GossHubContent(JSONObject gossJson, long gossExportFileLine) {
        super(gossJson, gossExportFileLine, HUB);
        introduction = GossExportHelper.getString(gossJson, GossExportFieldNames.INTRO, id);
    }

    public static GossHubContent getInstance(JSONObject gossJson, long gossExportFileLine) {
        return new GossHubContent(gossJson, gossExportFileLine);
    }

    public GossContentExtra getExtra() {
        return extra;
    }

    @Override
    public String getIntroduction() {
        return introduction;
    }

}