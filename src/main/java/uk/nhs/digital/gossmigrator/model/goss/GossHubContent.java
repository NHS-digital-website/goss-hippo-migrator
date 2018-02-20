package uk.nhs.digital.gossmigrator.model.goss;

import org.json.simple.JSONObject;
import uk.nhs.digital.gossmigrator.misc.GossExportHelper;
import uk.nhs.digital.gossmigrator.model.goss.enums.ContentType;
import uk.nhs.digital.gossmigrator.model.goss.enums.GossExportFieldNames;

import java.util.List;

public class GossHubContent extends GossServiceContent{

    private GossContentExtra extra;
    private String introduction;

    private GossHubContent(JSONObject gossJson, long gossExportFileLine) {
        super(gossJson, gossExportFileLine);
        contentType = ContentType.HUB;
        extra = new GossContentExtra(gossJson, GossExportFieldNames.EXTRA, id);
        introduction = getString(gossJson, INTRO, id);
    }

    public static GossHubContent getInstance(JSONObject gossJson, long gossExportFileLine){
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