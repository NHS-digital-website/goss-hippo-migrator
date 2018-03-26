package uk.nhs.digital.gossmigrator.model.hippo;

import uk.nhs.digital.gossmigrator.misc.TextHelper;
import uk.nhs.digital.gossmigrator.model.goss.GossLink;
import uk.nhs.digital.gossmigrator.model.goss.GossListPageContent;
import uk.nhs.digital.gossmigrator.model.goss.enums.ContentType;

import java.util.Set;

public class ListPage extends HippoImportable {

    private HippoRichText body;
    private Set<String> internalLinks;
    private Set<GossLink> externalLinks;

    private ListPage(GossListPageContent gossContent) {
        super(gossContent);
        id = gossContent.getId();
        title = gossContent.getHeading();
        seoSummary = TextHelper.escapeForJson(gossContent.getSummary());
        summary = TextHelper.escapeForJson(gossContent.getIntroduction());
        shortSummary = TextHelper.escapeForJson(gossContent.getSummary());
        ParsedArticleText parsedArticleText = new ParsedArticleText(gossContent.getId(), gossContent.getTemplateId(), gossContent.getText(), ContentType.LIST_PAGE);
        body = parsedArticleText.getDefaultNode();
        internalLinks = gossContent.getInternalArticles();
        externalLinks = gossContent.getExternalArticles();
    }

    /*
     * Factory method to generate an instance
     */
    public static ListPage getInstance(GossListPageContent gossContent) {
        return new ListPage(gossContent);
    }

    @SuppressWarnings("unused") // Used in template.
    public HippoRichText getBody() {
        return body;
    }

    @SuppressWarnings("unused")
    public Set<String> getInternalLinks() {
        return internalLinks;
    }

    @SuppressWarnings("unused")
    public Set<GossLink> getExternalLinks() {
        return externalLinks;
    }
}
