package uk.nhs.digital.gossmigrator.model.hippo;

import uk.nhs.digital.gossmigrator.GossImporter;
import uk.nhs.digital.gossmigrator.model.goss.GossServiceContent;
import uk.nhs.digital.gossmigrator.model.goss.enums.ContentType;

import java.util.List;

public class Service extends HippoImportable {

    private String servicePath;

    // Do not initialise HippoRichText objects.  Template needs nulls to decide on commas in list separators
    private List<HippoRichText> topTasks;
    private HippoRichText introduction;
    private HippoRichText contactDetails;
    private HippoRichText component;

    protected Service(GossServiceContent gossContent) {
        super(gossContent.getHeading(), gossContent.getJcrPath(), gossContent.getJcrNodeName());
        id = gossContent.getId();

        seoSummary = gossContent.getSummary();
        title = gossContent.getHeading();
        summary = gossContent.getIntroduction();
        shortSummary = gossContent.getSummary();
        servicePath = gossContent.getServicePath();

        ParsedArticleText parsedArticleText = new ParsedArticleText(gossContent.getId(), gossContent.getText(), ContentType.SERVICE);
        introduction = parsedArticleText.getIntroduction();
        sections = parsedArticleText.getSections();
        topTasks = parsedArticleText.getTopTasks();
        contactDetails = parsedArticleText.getContactDetails();
        component = parsedArticleText.getComponent();
    }

    /*
     * Factory method to generate a Service instance
     */
    public static Service getInstance(GossServiceContent gossContent) {
        Service service = new Service(gossContent);
        return service;
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
    public HippoRichText getContactDetails() {
        return contactDetails;
    }

    @SuppressWarnings("unused")
    public String getServicePath() {
        return servicePath;
    }

    @SuppressWarnings("unused")
    public HippoRichText getComponent() {
        return component;
    }
}