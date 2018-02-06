package uk.nhs.digital.gossmigrator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.digital.gossmigrator.model.goss.GossContent;
import uk.nhs.digital.gossmigrator.model.goss.GossContentList;
import uk.nhs.digital.gossmigrator.model.goss.GossProcessedData;
import uk.nhs.digital.gossmigrator.model.hippo.HippoImportable;
import uk.nhs.digital.gossmigrator.model.hippo.Publication;
import uk.nhs.digital.gossmigrator.model.hippo.Series;
import uk.nhs.digital.gossmigrator.model.hippo.Service;

import java.nio.file.Paths;
import java.util.*;

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
                hippoContent = new Service(gossContent);
                break;
            case PUBLICATION:
                hippoContent = new Publication(gossContent);
                Long publicationId = ((Publication)hippoContent).getId();
                Long seriesId = gossData.getPublicationSeriesMap().get(publicationId);
                if(seriesId != null) {
                    Optional<GossContent> matchingSeries = gossData.getSeriesContentList().stream().
                            filter(s -> s.getId() == seriesId).findFirst();
                    GossContent matchingSeriesGoss = matchingSeries.orElse(null);

                    if(matchingSeriesGoss != null){
                        hippoContent.setJcrPath(Paths.get(matchingSeriesGoss.getJcrParentPath(),hippoContent.getJcrNodeName(),"content").toString());
                        hippoContent.setJcrNodeName("content");
                    }
                }
                break;
            case SERIES:
                hippoContent = new Series(gossContent);
                break;
            default:
                LOGGER.error("Goss ID:{}, Unknown content type:{}", gossContent.getId(), gossContent.getContentType());
        }
        return hippoContent;
    }
}
