package uk.nhs.digital.gossmigrator.model.goss;

import org.json.simple.JSONObject;
import uk.nhs.digital.gossmigrator.misc.GossExportHelper;
import uk.nhs.digital.gossmigrator.model.goss.enums.GossExportFieldNames;

import java.util.Date;

import static uk.nhs.digital.gossmigrator.model.goss.enums.DateFormatEnum.GOSS_SHOR_FORMAT;
import static uk.nhs.digital.gossmigrator.model.goss.enums.GossExportFieldNames.COVENDDATE;
import static uk.nhs.digital.gossmigrator.model.goss.enums.GossExportFieldNames.COVSTARTDATE;
import static uk.nhs.digital.gossmigrator.model.goss.enums.GossExportFieldNames.PUBDATE;

public class GossContentExtra {

    private final Date coverageStart;
    private final Date coverageEnd;
    private final Date publicationDate;

    public GossContentExtra(JSONObject gossJson, GossExportFieldNames fieldName, long gossId){
        JSONObject extra = (JSONObject)gossJson.get(fieldName.getName());
        this.coverageStart = GossExportHelper.getDate(extra, COVSTARTDATE, gossId, GOSS_SHOR_FORMAT);
        this.coverageEnd = GossExportHelper.getDate(extra, COVENDDATE, gossId, GOSS_SHOR_FORMAT);
        this.publicationDate = GossExportHelper.getDate(extra, PUBDATE, gossId, GOSS_SHOR_FORMAT);
    }

    public Date getCoverageStart() {
        return coverageStart;
    }

    public Date getCoverageEnd() {
        return coverageEnd;
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    // TODO default to what?
    private boolean includeRelatedArticles = false;
    private boolean includeChildArticles = false;

    public boolean isIncludeRelatedArticles() {
        return includeRelatedArticles;
    }

    public void setIncludeRelatedArticles(boolean includeRelatedArticles) {
        this.includeRelatedArticles = includeRelatedArticles;
    }

    public boolean isIncludeChildArticles() {
        return includeChildArticles;
    }

    public void setIncludeChildArticles(boolean includeChildArticles) {
        this.includeChildArticles = includeChildArticles;
    }
}
