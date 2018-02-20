package uk.nhs.digital.gossmigrator.model.goss;

import org.json.simple.JSONObject;
import uk.nhs.digital.gossmigrator.misc.GossExportHelper;
import uk.nhs.digital.gossmigrator.model.goss.enums.GossExportFieldNames;

import java.util.Date;
import java.util.List;

import static uk.nhs.digital.gossmigrator.model.goss.enums.DateFormatEnum.GOSS_SHOR_FORMAT;
import static uk.nhs.digital.gossmigrator.model.goss.enums.GossExportFieldNames.*;

public class GossContentExtra {

    private Date coverageStart;
    private Date coverageEnd;
    private Date publicationDate;
    private String title;
    private List<Long> componentIds;

    public GossContentExtra(JSONObject gossJson, GossExportFieldNames fieldName, long gossId){
        JSONObject extra = (JSONObject)gossJson.get(fieldName.getName());
        this.coverageStart = GossExportHelper.getDate(extra, COVSTARTDATE, gossId, GOSS_SHOR_FORMAT);
        this.coverageEnd = GossExportHelper.getDate(extra, COVENDDATE, gossId, GOSS_SHOR_FORMAT);
        this.publicationDate = GossExportHelper.getDate(extra, PUBDATE, gossId, GOSS_SHOR_FORMAT);
        title = GossExportHelper.getString(extra, EXTRA_TITLE, gossId);
        componentIds = GossExportHelper.getLongList(extra, GossExportFieldNames.COMPONENTS, gossId);
    }

    public GossContentExtra() {

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

    public String getTitle() {
        return title;
    }

    public List<Long> getComponentIds() {
        return componentIds;
    }
}
