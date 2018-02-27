package uk.nhs.digital.gossmigrator.model.goss;

import org.json.simple.JSONObject;
import uk.nhs.digital.gossmigrator.GossImporter;
import uk.nhs.digital.gossmigrator.model.goss.enums.ContentType;

import java.util.HashSet;
import java.util.Set;

public class GossListPageContent extends GossContent {

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
        Set<String> included = new HashSet<>();

        if (null != extra) {
            // Children.
            if (extra.isIncludeChildArticles()) {
                for (Long child : getChildren()) {
                    addIfNotNull(included, getArticlePathFactoringInRedirects(child));
                }
            }
            // Meta matches.
            if (extra.isIncludeMetaArticles()) {
                for (Long match : GossImporter.gossData.getArticlesContentList().getArticlesMatchingMeta(getMetaList(), getId())) {
                    addIfNotNull(included, getArticlePathFactoringInRedirects(match));
                }
            }
            // Related.
            if (extra.isIncludeRelatedArticles()) {
                for (Long article : relatedArticles) {
                    addIfNotNull(included, getArticlePathFactoringInRedirects(article));
                }
            }

        }
        return included.size() > 0 ? included : null;
    }

    private void addIfNotNull(Set addTo, Object value){
        if(null != value){
            //noinspection unchecked
            addTo.add(value);
        }
    }

    public Set<GossLink> getExternalArticles() {
        Set<GossLink> links = new HashSet<>();

        if (extra != null) {
            // Children.
            if (extra.isIncludeChildArticles()) {
                for (Long child : getChildren()) {
                    addIfNotNull(links, getLinksFactoringInRedirects(child));
                }
            }
            // Meta matches.
            if (extra.isIncludeMetaArticles()) {
                for (Long match : GossImporter.gossData.getArticlesContentList().getArticlesMatchingMeta(getMetaList(), getId())) {
                    addIfNotNull(links, getLinksFactoringInRedirects(match));
                }
            }
            // Related.
            if (extra.isIncludeRelatedArticles()) {
                for (Long article : relatedArticles) {
                    addIfNotNull(links, getLinksFactoringInRedirects(article));
                }
            }
        }
        return links.size() > 0 ? links : null;
    }

    private String getArticlePathFactoringInRedirects(Long linkedArticleId) {
        GossContent content = GossImporter.gossData.getArticlesContentMap().get(linkedArticleId);
        if (content.getContentType() == ContentType.REDIRECT) {
            GossRedirectContent redirect = (GossRedirectContent) content;
            if (redirect.getRelatedArticles().size() == 0) {
                // Its a link to an external article i.e. url
                return null;
            } else {
                // Don't know if need recursion.  But might be redirect pointing to redirect.
                return getArticlePathFactoringInRedirects(redirect.getRelatedArticles().get(0));
            }
        } else {
            return content.getJcrPath();
        }
    }

    private GossLink getLinksFactoringInRedirects(Long linkedArticleId) {
        GossContent content = GossImporter.gossData.getArticlesContentMap().get(linkedArticleId);
        if (content.getContentType() == ContentType.REDIRECT) {
            GossRedirectContent redirect = (GossRedirectContent) content;
            if (redirect.getLink() != null) {
                // Its a link to an external article i.e. url
                return redirect.getLink();
            } else if (redirect.getRelatedArticles().size() == 1) {
                // Don't know if need recursion.  But might be redirect pointing to redirect.
                return getLinksFactoringInRedirects(redirect.getRelatedArticles().get(0));
            }
        }
        // If not a redirect then no link.
        return null;
    }
}
