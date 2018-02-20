package uk.nhs.digital.gossmigrator.model.hippo;

import uk.nhs.digital.gossmigrator.model.goss.GossSeriesContent;

import static uk.nhs.digital.gossmigrator.misc.TextHelper.trimAndStripLeadingTrailingQuotes;

public class Series extends HippoImportable {

    private Series(GossSeriesContent gossContent) {
        super(trimAndStripLeadingTrailingQuotes(gossContent.getHeading()), gossContent.getJcrPath(), gossContent.getJcrNodeName());
        title = trimAndStripLeadingTrailingQuotes(gossContent.getHeading());
        summary = trimAndStripLeadingTrailingQuotes(gossContent.getSummary());
    }

    public static Series getInstance(GossSeriesContent gossContent){
        return new Series(gossContent);
    }

}

