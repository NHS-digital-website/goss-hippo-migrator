package uk.nhs.digital.gossmigrator.model.goss;

import org.json.simple.JSONObject;
import uk.nhs.digital.gossmigrator.misc.GossLinkHelper;
import uk.nhs.digital.gossmigrator.model.goss.enums.ContentType;

import java.util.Set;

public class GossListPageContent extends GossContent {

    private GossLinkHelper linkHelper = new GossLinkHelper(this);

    private GossListPageContent(JSONObject gossJson, long gossExportFileLine) {
        super(gossJson, gossExportFileLine, ContentType.LIST_PAGE);
    }

    /*
     * Factory method to generate a GossListPageContent
     */
    public static GossListPageContent getInstance(JSONObject gossJson, long gossExportFileLine) {
        return new GossListPageContent(gossJson, gossExportFileLine);
    }

    public Set<String> getInternalArticles() {
        return linkHelper.getInternalArticles();
    }


    public Set<GossLink> getExternalArticles() {
        return linkHelper.getExternalArticles();
    }

}
