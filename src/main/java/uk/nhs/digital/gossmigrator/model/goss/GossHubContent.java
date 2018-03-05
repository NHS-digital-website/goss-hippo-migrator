package uk.nhs.digital.gossmigrator.model.goss;

import org.json.simple.JSONObject;
import uk.nhs.digital.gossmigrator.misc.GossExportHelper;
import uk.nhs.digital.gossmigrator.misc.GossLinkHelper;
import uk.nhs.digital.gossmigrator.model.goss.enums.GossExportFieldNames;

import java.util.Set;

import static uk.nhs.digital.gossmigrator.model.goss.enums.ContentType.HUB;

public class GossHubContent extends GossServiceContent {

    private String introduction;

    private GossLinkHelper linkHelper = new GossLinkHelper(this);

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

    public Set<String> getInternalArticles() {
        return linkHelper.getInternalArticles();
    }


    public Set<GossLink> getExternalArticles() {
        return linkHelper.getExternalArticles();
    }

}