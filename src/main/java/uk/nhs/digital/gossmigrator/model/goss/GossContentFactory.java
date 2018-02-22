package uk.nhs.digital.gossmigrator.model.goss;

import org.json.simple.JSONObject;
import uk.nhs.digital.gossmigrator.GossImporter;
import uk.nhs.digital.gossmigrator.model.goss.enums.ContentType;

import static uk.nhs.digital.gossmigrator.config.TemplateConfig.GENERAL_ID;
import static uk.nhs.digital.gossmigrator.config.TemplateConfig.PUBLICATION_ID;
import static uk.nhs.digital.gossmigrator.misc.GossExportHelper.getIdOrError;
import static uk.nhs.digital.gossmigrator.misc.GossExportHelper.getLong;
import static uk.nhs.digital.gossmigrator.model.goss.enums.GossExportFieldNames.ID;
import static uk.nhs.digital.gossmigrator.model.goss.enums.GossExportFieldNames.TEMPLATE_ID;

public class GossContentFactory {

    /*
     * Factory method to populate Publications and Services.
     */
    public static GossContent generateGossContent(JSONObject gossJson, long gossExportFileLine){
        GossContent content = null;
        ContentType type;
        Long id = getIdOrError(gossJson, ID);

        Long templateId = getLong(gossJson, TEMPLATE_ID, id);
        if(PUBLICATION_ID.equals(templateId)) {
            type = ContentType.PUBLICATION;
        }else if(GENERAL_ID.equals(templateId)){
            type = ContentType.GENERAL;
        }else if (GossImporter.gossData.getContentTypeMap().get(templateId) != null){
            type = GossImporter.gossData.getContentTypeMap().get(templateId);
        }else{
            type = ContentType.GENERAL;
        }

        switch (type){
            case PUBLICATION:
                content = GossPublicationContent.getInstance(gossJson, gossExportFileLine);
                break;
            case SERVICE:
                content = GossServiceContent.getInstance(gossJson, gossExportFileLine);
                break;
            case HUB:
                content = GossHubContent.getInstance(gossJson, gossExportFileLine);
                break;
            case GENERAL:
                content = GossGeneralContent.getInstance(gossJson, gossExportFileLine);
                break;
        }
        return content;
    }

    /*
     * Factory method to populate Series.
     */
    public static GossContent generateSeriesContent(Long id, String heading, String summary){
        return GossSeriesContent.getInstance(id, heading, summary);
    }
}
