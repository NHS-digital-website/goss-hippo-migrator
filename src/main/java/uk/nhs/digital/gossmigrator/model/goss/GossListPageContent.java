package uk.nhs.digital.gossmigrator.model.goss;

import org.json.simple.JSONObject;
import uk.nhs.digital.gossmigrator.model.goss.enums.ContentType;

import java.util.ArrayList;
import java.util.List;

public class GossListPageContent extends GossContent {

    List<Long> includedArticleIds = new ArrayList<>();

    private GossListPageContent(JSONObject gossJson, long gossExportFileLine) {
        super(gossJson, gossExportFileLine, ContentType.LIST_PAGE);
    }

    /*
     * Factory method to generate a GossListPageContent
     */
    public static GossListPageContent getInstance(JSONObject gossJson, long gossExportFileLine) {
        return new GossListPageContent(gossJson, gossExportFileLine);
    }

}
