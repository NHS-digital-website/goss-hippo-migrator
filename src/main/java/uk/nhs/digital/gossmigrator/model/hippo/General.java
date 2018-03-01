package uk.nhs.digital.gossmigrator.model.hippo;

import uk.nhs.digital.gossmigrator.model.goss.GossGeneralContent;
import uk.nhs.digital.gossmigrator.model.goss.enums.ContentType;

public class General extends HippoImportable {

    private Long templateId;
    private String type;

    protected General(GossGeneralContent gossContent) {
        super(gossContent.getHeading(), gossContent.getJcrPath(), gossContent.getJcrNodeName());
        id = gossContent.getId();
        templateId = gossContent.getTemplateId();
        type = gossContent.getDocumentType();

        title = gossContent.getHeading();
        seoSummary = gossContent.getIntroduction();
        summary = gossContent.getIntroduction();
        shortSummary = gossContent.getIntroduction();

        ParsedArticleText parsedArticleText = new ParsedArticleText(gossContent.getId(), gossContent.getText(), ContentType.HUB);
        sections = parsedArticleText.getSections();
        component = parsedArticleText.getComponent();
    }

    /*
     * Factory method to generate a Service instance
     */
    public static General getInstance(GossGeneralContent gossContent) {
        return new General(gossContent);

    }

    @SuppressWarnings("unused")
    public Long getTemplateId() {
        return templateId;
    }

    public String getType() {
        return type;
    }
}
