package uk.nhs.digital.gossmigrator.model.hippo;

import uk.nhs.digital.gossmigrator.misc.TextHelper;
import uk.nhs.digital.gossmigrator.model.goss.GossServiceContent;
import uk.nhs.digital.gossmigrator.model.goss.enums.ContentType;

import java.util.List;

public class Service extends HippoImportable {

    private String servicePath;

    // Do not initialise HippoRichText objects.  Template needs nulls to decide on commas in list separators
    private List<HippoRichText> topTasks;
    private HippoRichText introduction;
    private HippoRichText contactDetails;

    protected Service(GossServiceContent gossContent) {
        super(gossContent.getHeading(), gossContent.getJcrPath(), gossContent.getJcrNodeName());
        id = gossContent.getId();

        seoSummary = TextHelper.escapeForJson(gossContent.getSummary());
        title = TextHelper.escapeForJson(gossContent.getHeading());
        summary = TextHelper.escapeForJson(gossContent.getIntroduction());
        shortSummary = TextHelper.escapeForJson(gossContent.getSummary());
        servicePath = gossContent.getModifiedPath();

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
        return new Service(gossContent);

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



  }