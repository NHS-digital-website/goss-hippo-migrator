package uk.nhs.digital.gossmigrator;

import static uk.nhs.digital.gossmigrator.misc.GossExportHelper.getIdOrError;
import static uk.nhs.digital.gossmigrator.misc.GossExportHelper.getLong;
import static uk.nhs.digital.gossmigrator.model.goss.enums.GossExportFieldNames.ID;
import static uk.nhs.digital.gossmigrator.model.goss.enums.GossExportFieldNames.TEMPLATE_ID;
import static uk.nhs.digital.gossmigrator.model.goss.enums.GossSourceFile.CONTENT;
import static uk.nhs.digital.gossmigrator.model.goss.enums.GossSourceFile.DIGITAL;

import org.json.simple.JSONObject;
import uk.nhs.digital.gossmigrator.model.goss.*;
import uk.nhs.digital.gossmigrator.model.goss.enums.ContentType;

public class GossContentFactory {

    /*
     * Factory method to populate Goss content.
     */
    public static GossContent generateGossContent(GossProcessedData data, JSONObject gossJson, long gossExportFileLine) {
        GossContent content = null;
        ContentType type;

        Long id = getIdOrError(gossJson, ID);
        Long templateId = getLong(gossJson, TEMPLATE_ID, id);

        if (DIGITAL.equals(data.getType())) {
            if (GossImporter.digitalData.getContentTypeMap().get(id) != null) {
                type = GossImporter.digitalData.getContentTypeMap().get(id);
            } else if (GossImporter.digitalData.getContentTypeMap().get(templateId) != null) {
                type = GossImporter.digitalData.getContentTypeMap().get(templateId);
            } else {
                type = ContentType.GENERAL;
            }

            switch (type) {
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
                case LIST_PAGE:
                    content = GossListPageContent.getInstance(gossJson, gossExportFileLine);
                    break;
                case REDIRECT:
                    content = GossRedirectContent.getInstance(gossJson, gossExportFileLine);
                    break;
            }


        } else if (CONTENT.equals(data.getType()) && templateId != null && templateId.equals(22L)) {
            content = GossRedirectContent.getInstance(gossJson, gossExportFileLine);
        }

        return content;
    }

    /*
     * Factory method to populate Series.
     */
    public static GossContent generateSeriesContent(Long id, String heading, String summary) {
        return GossSeriesContent.getInstance(id, heading, summary);
    }
}
