package uk.nhs.digital.gossmigrator.model.hippo;

import org.apache.commons.lang3.StringUtils;
import uk.nhs.digital.gossmigrator.model.goss.GossContent;

import java.util.List;

public class Service extends HippoImportable {

    private String seoSummary;
    private String title;
    private String summary;
    private String shortSummary;

    // Do not initialise HippoRichText objects.  Template needs nulls to decide on commas in list separators
    private List<HippoRichText> topTasks;
    private HippoRichText introduction;
    private List<Section> sections;
    private HippoRichText contactDetails;

    private Service(GossContent gossContent) {
        super(gossContent.getHeading(), gossContent.getJcrPath(), gossContent.getJcrNodeName());

        seoSummary = gossContent.getSummary();
        title = gossContent.getHeading();
        summary = gossContent.getIntroduction();
        shortSummary = gossContent.getSummary();

        ParsedArticleText parsedArticleText = new ParsedArticleText(gossContent.getId(), gossContent.getText());
        introduction = parsedArticleText.getIntroduction();
        sections = parsedArticleText.getSections();
        topTasks = parsedArticleText.getTopTasks();
        contactDetails = parsedArticleText.getContactDetails();
    }

    public static Service getInstance(GossContent gossContent){
        return new Service(gossContent);
    }

    @SuppressWarnings("unused")
    public String getSeoSummary() {
        return seoSummary;
    }

    @SuppressWarnings("unused")
    public String getTitle() {
        return title;
    }

    @SuppressWarnings("unused")
    public String getSummary() {
        return summary;
    }

    @SuppressWarnings("unused")
    public String getShortSummary() {
        return shortSummary;
    }

    @SuppressWarnings("unused")
    public List<HippoRichText> getTopTasks() {
        return topTasks;
    }

    @SuppressWarnings("unused")
    public HippoRichText getIntroduction() {
        return introduction;
    }

    @SuppressWarnings("unused")
    public List<Section> getSections() {
        return sections;
    }

    @SuppressWarnings("unused")
    public HippoRichText getContactDetails() {
        return contactDetails;
    }
}