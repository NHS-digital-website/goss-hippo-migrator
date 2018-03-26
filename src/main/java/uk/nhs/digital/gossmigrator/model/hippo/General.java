package uk.nhs.digital.gossmigrator.model.hippo;

import uk.nhs.digital.gossmigrator.misc.TextHelper;
import uk.nhs.digital.gossmigrator.model.goss.GossGeneralContent;
import uk.nhs.digital.gossmigrator.model.goss.enums.ContentType;

public class General extends HippoImportable {

    private Long templateId;
    private String type = "";

    protected General(GossGeneralContent gossContent) {
        super(gossContent.getHeading(), gossContent.getJcrPath(), gossContent.getJcrNodeName());
        setLive(gossContent);
        id = gossContent.getId();
        templateId = gossContent.getTemplateId();
        if(gossContent.getDocumentType() != null){
            type = gossContent.getDocumentType().toLowerCase().replace(' ', '-');
        }
        title = TextHelper.escapeForJson(gossContent.getHeading());
        seoSummary = TextHelper.escapeForJson(gossContent.getSummary());
        summary = TextHelper.escapeForJson(gossContent.getIntroduction());
        shortSummary = TextHelper.escapeForJson(gossContent.getSummary());


        ParsedArticleText parsedArticleText = new ParsedArticleText(gossContent.getId(), gossContent.getTemplateId(), gossContent.getText(), ContentType.GENERAL);
        sections = parsedArticleText.getSections();
        component = parsedArticleText.getComponent();
    }

    /*
     * Factory method to generate an instance
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
