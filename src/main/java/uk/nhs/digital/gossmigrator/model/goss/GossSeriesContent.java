package uk.nhs.digital.gossmigrator.model.goss;

import uk.nhs.digital.gossmigrator.config.Config;
import uk.nhs.digital.gossmigrator.misc.TextHelper;

import java.nio.file.Paths;

import static uk.nhs.digital.gossmigrator.model.goss.enums.ContentType.SERIES;

public class GossSeriesContent extends GossContent {

    private GossSeriesContent(Long id, String heading, String summary) {
        contentType = SERIES;
        this.id = id;
        this.heading = heading;
        this.summary = summary;
        setRelevantContentFlag(true);
        // Currently the jcr node containing the series content has to be called content.
        // This is the convention used by RPS project.
        jcrNodeName = "content";
        jcrParentPath =
                Paths.get(Config.JCR_PUBLICATION_ROOT
                        , TextHelper.toLowerCaseDashedValue(this.heading)).toString();
        depth = 1;
        parentId = this.id;  // No parent doc for series.
    }

    /*
     * Factory method to generate a GossSeriesContent
     */
    public static GossSeriesContent getInstance(Long id, String heading, String summary) {
        return new GossSeriesContent(id, heading, summary);
    }
}
