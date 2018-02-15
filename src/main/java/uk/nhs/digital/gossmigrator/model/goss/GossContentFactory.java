package uk.nhs.digital.gossmigrator.model.goss;

import org.json.simple.JSONObject;

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
        GossContent content;
        Long id = getIdOrError(gossJson, ID);
        Long templateId = getLong(gossJson, TEMPLATE_ID, id);
        if (templateId != null && templateId.equals(PUBLICATION_ID)) {
            content = GossPublicationContent.getInstance(gossJson, gossExportFileLine);
        } else if(templateId != null && templateId.equals(18L))            {
            content = new GossHubContent(gossJson, gossExportFileLine);
        }else{
            content = GossServiceContent.getInstance(gossJson, gossExportFileLine);
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
