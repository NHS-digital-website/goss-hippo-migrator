package uk.nhs.digital.gossmigrator.model.hippo;

import uk.nhs.digital.gossmigrator.misc.ArticleFinder;
import uk.nhs.digital.gossmigrator.misc.TextHelper;
import uk.nhs.digital.gossmigrator.model.goss.GossHubContent;
import uk.nhs.digital.gossmigrator.model.goss.enums.ContentType;

import java.util.List;

public class Hub extends HippoImportable {

    private List<String> componentPaths;
    private String listTitle;
    private HippoRichText body;

    private Hub(GossHubContent gossContent) {
        super(gossContent);
        setLive(gossContent);
        id = gossContent.getId();
        title = TextHelper.escapeForJson(gossContent.getHeading());
        seoSummary = TextHelper.escapeForJson(gossContent.getIntroduction());
        summary = TextHelper.escapeForJson(gossContent.getIntroduction());
        shortSummary = TextHelper.escapeForJson(gossContent.getIntroduction());

        ParsedArticleText parsedArticleText = new ParsedArticleText(gossContent.getId(), gossContent.getText(), ContentType.HUB);
        body = parsedArticleText.getDefaultNode();
        componentPaths = ArticleFinder.findArticlePathsByArticleId(
                gossContent.getExtra().getComponentIds(), "Getting components.", id);
        listTitle = gossContent.getExtra().getTitle();
        component = parsedArticleText.getComponent();
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
}
