package uk.nhs.digital.gossmigrator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.digital.gossmigrator.model.goss.*;
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
                hippoContent = Service.getInstance((GossServiceContent) gossContent);
                break;
            case PUBLICATION:
                hippoContent = Publication.getInstance(gossData, (GossPublicationContent) gossContent);
                break;
            case SERIES:
                hippoContent = Series.getInstance((GossSeriesContent) gossContent);
                break;
            default:
                LOGGER.error("Goss ID:{}, Unknown content type:{}", gossContent.getId(), gossContent.getContentType());
        }
        return hippoContent;
    }
}
