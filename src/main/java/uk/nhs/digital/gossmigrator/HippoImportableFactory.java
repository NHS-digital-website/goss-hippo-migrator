package uk.nhs.digital.gossmigrator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.digital.gossmigrator.Report.GeneralReportWriter;
import uk.nhs.digital.gossmigrator.Report.HubReportWriter;
import uk.nhs.digital.gossmigrator.Report.PublicationReportWriter;
import uk.nhs.digital.gossmigrator.Report.ServicesReportWriter;
import uk.nhs.digital.gossmigrator.model.goss.*;
import uk.nhs.digital.gossmigrator.model.hippo.*;

import java.util.ArrayList;
import java.util.List;

public class HippoImportableFactory {

    private final static Logger LOGGER = LoggerFactory.getLogger(HippoImportableFactory.class);

    public List<HippoImportable> populateHippoContent(GossProcessedData gossData) {
        LOGGER.debug("Begin populating hippo content from Goss content.");
        List<HippoImportable> importableContentItems = new ArrayList<>();
        for (GossContent gossContent : gossData.getArticlesContentList()) {
            importableContentItems.add(generateHippoImportable(gossData, gossContent));
        }
        for (GossContent gossContent : gossData.getSeriesContentList()) {
            importableContentItems.add(generateHippoImportable(gossData, gossContent));
        }
        return importableContentItems;
    }


    private HippoImportable generateHippoImportable(GossProcessedData gossData, GossContent gossContent){
        HippoImportable hippoContent = null;
        switch (gossContent.getContentType()) {
            case SERVICE:
                hippoContent = Service.getInstance((GossServiceContent) gossContent);
                ServicesReportWriter.addServiceRow((Service)hippoContent);
                break;
            case PUBLICATION:
                hippoContent = Publication.getInstance(gossData, (GossPublicationContent) gossContent);
                PublicationReportWriter.addPublicationRow((Publication)hippoContent);
                break;
            case SERIES:
                hippoContent = Series.getInstance((GossSeriesContent) gossContent);
                break;
            case HUB:
                hippoContent = Hub.getInstance((GossHubContent)gossContent);
                HubReportWriter.addHubRow((Hub)hippoContent);
                break;
            case GENERAL:
                hippoContent = General.getInstance((GossGeneralContent) gossContent);
                GeneralReportWriter.addGeneralRow((General)hippoContent);
                break;
            default:
                LOGGER.error("Goss ID:{}, Unknown content type:{}", gossContent.getId(), gossContent.getContentType());
        }
        return hippoContent;
    }
}
