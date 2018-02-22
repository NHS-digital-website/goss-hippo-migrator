package uk.nhs.digital.gossmigrator.model.goss;

import org.json.simple.JSONObject;

import static uk.nhs.digital.gossmigrator.misc.GossExportHelper.getLong;
import static uk.nhs.digital.gossmigrator.misc.GossExportHelper.getString;
import static uk.nhs.digital.gossmigrator.model.goss.enums.ContentType.GENERAL;
import static uk.nhs.digital.gossmigrator.model.goss.enums.GossExportFieldNames.INTRO;
import static uk.nhs.digital.gossmigrator.model.goss.enums.GossExportFieldNames.TEMPLATE_ID;

public class GossGeneralContent extends GossContent{

    private String introduction;
    private Long templateId;

    private GossGeneralContent(JSONObject gossJson, long gossExportFileLine){
        super(gossJson, gossExportFileLine);
        contentType = GENERAL;
        introduction = getString(gossJson, INTRO, id);
        templateId = getLong(gossJson, TEMPLATE_ID, id);
    }

    /*
     * Factory method to generate a GossServiceContent
     */
    public static GossGeneralContent getInstance(JSONObject gossJson, long gossExportFileLine){
        return new GossGeneralContent(gossJson, gossExportFileLine);
    }

    public String getIntroduction() {
        return introduction;
    }

    public Long getTemplateId() {
        return templateId;
    }
}
