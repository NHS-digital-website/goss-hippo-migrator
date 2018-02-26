package uk.nhs.digital.gossmigrator.model.hippo;

import uk.nhs.digital.gossmigrator.model.goss.GossContent;
import uk.nhs.digital.gossmigrator.model.goss.GossListPageContent;

public class ListPage extends HippoImportable {

    Long templateId;

    private ListPage(GossContent gossContent) {
        super(gossContent);
    }

    /*
     * Factory method to generate a Service instance
     */
    public static ListPage getInstance(GossListPageContent gossContent) {
        return new ListPage(gossContent);
    }

    public Long getTemplateId() {
        return templateId;
    }
}
