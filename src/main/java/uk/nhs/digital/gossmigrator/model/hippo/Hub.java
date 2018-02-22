package uk.nhs.digital.gossmigrator.model.hippo;

import uk.nhs.digital.gossmigrator.model.goss.GossHubContent;
import uk.nhs.digital.gossmigrator.model.goss.enums.ContentType;

import java.util.List;

public class Hub extends HippoImportable {

    private List<Long> componentIds;
    private String listTitle;

    private Hub(GossHubContent gossContent){
        super(gossContent);
        id = gossContent.getId();
        title = gossContent.getHeading();
        seoSummary = gossContent.getIntroduction();
        summary = gossContent.getIntroduction();
        shortSummary = gossContent.getIntroduction();

        ParsedArticleText parsedArticleText = new ParsedArticleText(gossContent.getId(), gossContent.getText(), ContentType.HUB);
        sections = parsedArticleText.getSections();
        componentIds = gossContent.getExtra().getComponentIds();
        listTitle = gossContent.getExtra().getTitle();
        component = parsedArticleText.getComponent();
    }

    public static Hub getInstance(GossHubContent hubContent){
        return new Hub(hubContent);
    }

    @SuppressWarnings("unused")
    public List<Long> getComponentIds() {
        return componentIds;
    }

    @SuppressWarnings("unused")
    public String getListTitle() {
        return listTitle;
    }

}
