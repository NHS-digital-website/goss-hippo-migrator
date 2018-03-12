package uk.nhs.digital.gossmigrator.model.goss;

import org.json.simple.JSONObject;

import static uk.nhs.digital.gossmigrator.GossImporter.digitalData;
import static uk.nhs.digital.gossmigrator.misc.GossExportHelper.getString;
import static uk.nhs.digital.gossmigrator.model.goss.enums.ContentType.GENERAL;
import static uk.nhs.digital.gossmigrator.model.goss.enums.GossExportFieldNames.INTRO;

public class GossGeneralContent extends GossContent{

    private String introduction;
    private String documentType;

    private GossGeneralContent(JSONObject gossJson, long gossExportFileLine){
        super(gossJson, gossExportFileLine, GENERAL);
        introduction = getString(gossJson, INTRO, id);
        documentType = digitalData.getGeneralDocumentTypeMap().get(id);
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

    public String getDocumentType() {
        return documentType;
    }
}
