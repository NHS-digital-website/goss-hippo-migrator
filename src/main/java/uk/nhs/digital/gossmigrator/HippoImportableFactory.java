package uk.nhs.digital.gossmigrator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.digital.gossmigrator.Report.*;
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
            if(gossContent.isRelevantContentFlag()){
                importableContentItems.addAll(generateHippoImportable(gossData, gossContent));
            }else{
                NonRevelantReportWriter.addNonRelevantRow(gossContent);
            }
        }
        return importableContentItems;
    }


    private List<HippoImportable> generateHippoImportable(GossProcessedData gossData, GossContent gossContent){
        List<HippoImportable> importables = new ArrayList<>();

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
            case REDIRECT:
                hippoContent = Redirect.getInstance((GossRedirectContent) gossContent);
                if(!((Redirect)hippoContent).getRuleTo().isEmpty()){
                    RedirectReportWriter.addRedirectRow(gossData.getType().getDescription(), (Redirect) hippoContent);
                }
                break;
            case LIST_PAGE:
                hippoContent = ListPage.getInstance((GossListPageContent) gossContent);
                ListPageReportWriter.addRow((ListPage) hippoContent);
                break;
            case FOLDER:
                hippoContent = Folder.getInstance((GossFolder) gossContent);
                break;
            default:
                LOGGER.error("Goss ID:{}, Unknown content type:{}", gossContent.getId(), gossContent.getContentType());
        }
        if(!(hippoContent instanceof Redirect) || !((Redirect)hippoContent).getRuleTo().isEmpty()){
            importables.add(hippoContent);
        }

        if(gossContent.getFriendlyUrl() != null && !gossContent.getFriendlyUrl().isEmpty()){
            Redirect friendlyUrlRedirect = Redirect.getFriendlyUrlInstance(gossContent);
            if(!friendlyUrlRedirect.getRuleTo().isEmpty()){
                importables.add(friendlyUrlRedirect);
                RedirectReportWriter.addRedirectRow(gossData.getType().getDescription(), friendlyUrlRedirect);
            }
        }

        return importables;
    }
}