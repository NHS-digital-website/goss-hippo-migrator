package uk.nhs.digital.gossmigrator.model.goss;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.digital.gossmigrator.model.goss.enums.ContentType;

import static uk.nhs.digital.gossmigrator.model.goss.enums.GossExportFieldNames.LINKS;

public class GossRedirectContent extends GossContent {

    private final static Logger LOGGER = LoggerFactory.getLogger(GossRedirectContent.class);
    private GossLink link;

    private GossRedirectContent(JSONObject gossJson, long gossExportFileLine) {
        super(gossJson, gossExportFileLine, ContentType.REDIRECT);
        processLinksNode(gossJson);
    }

    private void processLinksNode(JSONObject gossJson) {
        JSONArray linksJson = (JSONArray) gossJson.get(LINKS.getName());
        if (null != linksJson) {
            if (linksJson.size() == 1) {
                link = new GossLink((JSONObject) linksJson.get(0));
            }else{
                LOGGER.warn("Redirect article:{} has {} links.  Expected 1.", id, linksJson.size());
                warnings.add("Article:" + id + " is a redirect with " + linksJson.size() + " links.  Expected 1.");
            }
        } else {
            LOGGER.warn("Redirect article:{} has no Link node.", id);
            warnings.add("Article:" + id + " is a redirect with no Link.");
        }
    }

    /*
     * Factory method to generate a GossRedirectContent
     */
    public static GossRedirectContent getInstance(JSONObject gossJson, long gossExportFileLine) {
        return new GossRedirectContent(gossJson, gossExportFileLine);
    }

    public GossLink getLink() {
        return link;
    }
}
