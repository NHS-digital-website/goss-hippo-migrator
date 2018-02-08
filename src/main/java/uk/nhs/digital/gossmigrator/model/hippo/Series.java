package uk.nhs.digital.gossmigrator.model.hippo;

import uk.nhs.digital.gossmigrator.misc.TextHelper;
import uk.nhs.digital.gossmigrator.model.goss.GossContent;
import uk.nhs.digital.gossmigrator.model.goss.GossSeriesContent;

import static uk.nhs.digital.gossmigrator.misc.TextHelper.trimAndStripLeadingTrailingQuotes;

public class Series extends HippoImportable {

    private final String title;
    private final String summary;

    private Series(GossSeriesContent gossContent) {
        super(trimAndStripLeadingTrailingQuotes(gossContent.getHeading()), gossContent.getJcrPath(), gossContent.getJcrNodeName());
        this.title = trimAndStripLeadingTrailingQuotes(gossContent.getHeading());
        this.summary = trimAndStripLeadingTrailingQuotes(gossContent.getSummary());
    }

    public static Series getInstance(GossSeriesContent gossContent){
        return new Series(gossContent);
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }
}

