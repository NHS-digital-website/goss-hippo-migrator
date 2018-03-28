package uk.nhs.digital.gossmigrator.model.goss;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.digital.gossmigrator.misc.GossExportHelper;
import uk.nhs.digital.gossmigrator.model.goss.enums.ContentType;

import static uk.nhs.digital.gossmigrator.model.goss.enums.ContentType.REDIRECT;
import static uk.nhs.digital.gossmigrator.model.goss.enums.GossExportFieldNames.*;

public class GossRedirectContent extends GossContent {

    private final static Logger LOGGER = LoggerFactory.getLogger(GossRedirectContent.class);
    private GossLink link;

    private GossRedirectContent(JSONObject gossJson, long gossExportFileLine) {
        super(gossJson, gossExportFileLine, REDIRECT);
        processLinksNode(gossJson);
        if (relatedArticles.size() > 1) {
            LOGGER.warn("Redirect article:{} has {} Article links.  Expected 1.", id, relatedArticles.size());
            warnings.add("Article:" + id + " is a redirect with " + relatedArticles.size() + " Article links.  Expected 1.");
        }
        if (null == link && relatedArticles.size() == 0) {
            LOGGER.warn("Redirect:{} has no external or article link", id);
            warnings.add("Redirect has no internal or external link.");
        }
        if (null != link && relatedArticles.size() > 0) {
            LOGGER.warn("Redirect:{} has both internal and external link.", id);
        }
    }

    private void processLinksNode(JSONObject gossJson) {
        JSONArray linksJson = (JSONArray) gossJson.get(LINKS.getName());
        if (null != linksJson) {
            if (linksJson.size() >= 1) {
                link = new GossLink((JSONObject) linksJson.get(0));
                // Redirects seem to use the redirect values rather than the links data.
                link.setDisplayText(heading);
                link.setDescription(summary);
                if (linksJson.size() > 1) {
                    LOGGER.warn("Redirect article:{} has {} links.  Expected 1. Using first link.", id, linksJson.size());
                    warnings.add("Article:" + id + " is a redirect with " + linksJson.size() + " links.  Expected 1.");
                }
            }
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
