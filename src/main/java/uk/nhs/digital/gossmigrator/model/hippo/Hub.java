package uk.nhs.digital.gossmigrator.model.hippo;

import uk.nhs.digital.gossmigrator.misc.ArticleFinder;
import uk.nhs.digital.gossmigrator.misc.TextHelper;
import uk.nhs.digital.gossmigrator.model.goss.GossHubContent;
import uk.nhs.digital.gossmigrator.model.goss.GossLink;
import uk.nhs.digital.gossmigrator.model.goss.enums.ContentType;

import java.util.List;
import java.util.Set;

public class Hub extends HippoImportable {

    private List<String> componentPaths;
    private String listTitle;
    private HippoRichText body;
    private Set<String> internalLinks;
    private Set<GossLink> externalLinks;

    private Hub(GossHubContent gossContent) {
        super(gossContent);
        setLive(gossContent);
        id = gossContent.getId();
        title = TextHelper.escapeForJson(gossContent.getHeading());
        seoSummary = TextHelper.escapeForJson(gossContent.getSummary());
        summary = TextHelper.escapeForJson(gossContent.getIntroduction());
        shortSummary = TextHelper.escapeForJson(gossContent.getSummary());

        ParsedArticleText parsedArticleText = new ParsedArticleText(gossContent.getId(), gossContent.getText(), ContentType.HUB);
        body = parsedArticleText.getDefaultNode();
        componentPaths = ArticleFinder.findArticlePathsByArticleId(
                gossContent.getExtra().getComponentIds(), "Getting components.", id);
        listTitle = gossContent.getExtra().getTitle();
        component = parsedArticleText.getComponent();
        internalLinks = gossContent.getInternalArticles();
        externalLinks = gossContent.getExternalArticles();
    }

    public static Hub getInstance(GossHubContent hubContent) {
        return new Hub(hubContent);
    }

    @SuppressWarnings("unused")
    public List<String> getComponentPaths() {
        return componentPaths;
    }

    @SuppressWarnings("unused")
    public String getListTitle() {
        return listTitle;
    }

    @SuppressWarnings("unused")
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
