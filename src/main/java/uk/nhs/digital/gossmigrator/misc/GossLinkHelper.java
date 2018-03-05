package uk.nhs.digital.gossmigrator.misc;

import uk.nhs.digital.gossmigrator.GossImporter;
import uk.nhs.digital.gossmigrator.model.goss.GossContent;
import uk.nhs.digital.gossmigrator.model.goss.GossContentExtra;
import uk.nhs.digital.gossmigrator.model.goss.GossLink;
import uk.nhs.digital.gossmigrator.model.goss.GossRedirectContent;
import uk.nhs.digital.gossmigrator.model.goss.enums.ContentType;

import java.util.HashSet;
import java.util.Set;

public class GossLinkHelper {

    private GossContent gossContent;

    public GossLinkHelper(GossContent gossContent) {
        this.gossContent = gossContent;
    }

    public Set<String> getInternalArticles() {
        Set<String> included = new HashSet<>();

        GossContentExtra extra = gossContent.getExtra();
         if (null != extra) {
            // Children.
            if (extra.isIncludeChildArticles()) {
                for (Long child : gossContent.getChildren()) {
                    addIfNotNull(included, getArticlePathFactoringInRedirects(child));
                }
            }
            // Meta matches.
            if (extra.isIncludeMetaArticles()) {
                for (Long match : GossImporter.gossData.getArticlesContentList()
                        .getArticlesMatchingMeta(gossContent.getMetaList(), gossContent.getId())) {
                    addIfNotNull(included, getArticlePathFactoringInRedirects(match));
                }
            }
            // Related.
            if (extra.isIncludeRelatedArticles()) {
                for (Long article : gossContent.getRelatedArticles()) {
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

        GossContentExtra extra = gossContent.getExtra();

        if (extra != null) {
            // Children.
            if (extra.isIncludeChildArticles()) {
                for (Long child : gossContent.getChildren()) {
                    addIfNotNull(links, getLinksFactoringInRedirects(child));
                }
            }
            // Meta matches.
            if (extra.isIncludeMetaArticles()) {
                for (Long match : GossImporter.gossData.getArticlesContentList()
                        .getArticlesMatchingMeta(gossContent.getMetaList(), gossContent.getId())) {
                    addIfNotNull(links, getLinksFactoringInRedirects(match));
                }
            }
            // Related.
            if (extra.isIncludeRelatedArticles()) {
                for (Long article : gossContent.getRelatedArticles()) {
                    addIfNotNull(links, getLinksFactoringInRedirects(article));
                }
            }
        }
        return links.size() > 0 ? links : null;
    }

    private String getArticlePathFactoringInRedirects(Long linkedArticleId) {
        GossContent content = GossImporter.gossData.getArticlesContentList().getById(linkedArticleId);
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
        GossContent content = GossImporter.gossData.getArticlesContentList().getById(linkedArticleId);
        if (content.getContentType() == ContentType.REDIRECT) {
            GossRedirectContent redirect = (GossRedirectContent) content;
            if (redirect.getLink() != null) {
                // Its a link to an external article i.e. url
                return redirect.getLink();
            }else if (redirect.getRelatedArticles().size() == 1) {
                // Don't know if need recursion.  But might be redirect pointing to redirect.
                return getLinksFactoringInRedirects(redirect.getRelatedArticles().get(0));
            }
        }
        // If not a redirect then no link.
        return null;
    }
}
