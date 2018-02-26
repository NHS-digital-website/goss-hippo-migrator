package uk.nhs.digital.gossmigrator.model.goss;

import org.json.simple.JSONObject;
import uk.nhs.digital.gossmigrator.misc.GossExportHelper;
import uk.nhs.digital.gossmigrator.model.goss.enums.GossExportFieldNames;

import java.util.Date;
import java.util.List;

import static uk.nhs.digital.gossmigrator.misc.GossExportHelper.getBoolean;
import static uk.nhs.digital.gossmigrator.model.goss.enums.DateFormatEnum.GOSS_SHOR_FORMAT;
import static uk.nhs.digital.gossmigrator.model.goss.enums.GossExportFieldNames.*;

public class GossContentExtra {

    private Date coverageStart;
    private Date coverageEnd;
    private Date publicationDate;
    private String title;
    private List<Long> componentIds;
    private String publicationId;
    private boolean includeMetaArticles;
    private boolean includeRelatedArticles = false;
    private boolean includeChildArticles = false;

    GossContentExtra(JSONObject gossJson, GossExportFieldNames fieldName, long gossId){
        JSONObject extra = (JSONObject)gossJson.get(fieldName.getName());
        coverageStart = GossExportHelper.getDate(extra, COVSTARTDATE, gossId, GOSS_SHOR_FORMAT);
        coverageEnd = GossExportHelper.getDate(extra, COVENDDATE, gossId, GOSS_SHOR_FORMAT);
        publicationDate = GossExportHelper.getDate(extra, PUBDATE, gossId, GOSS_SHOR_FORMAT);
        includeChildArticles = getBoolean(extra, EXTRA_INCLUDE_CHILD, false);
        includeRelatedArticles = getBoolean(extra, EXTRA_INCLUDE_RELATED, false);
        includeMetaArticles = getBoolean(extra, EXTRA_INCLUDE_META, false);
        publicationId = GossExportHelper.getString(extra, PUBID, gossId);
        title = GossExportHelper.getString(extra, EXTRA_TITLE, gossId);
        componentIds = GossExportHelper.getLongList(extra, GossExportFieldNames.COMPONENTS, gossId);
    }

    GossContentExtra() {

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

    public boolean isIncludeRelatedArticles() {
        return includeRelatedArticles;
    }

    public boolean isIncludeChildArticles() {
        return includeChildArticles;
    }

    public String getTitle() {
        return title;
    }

    public List<Long> getComponentIds() {
        return componentIds;
    }

    public String getPublicationId() {
        return publicationId;
    }

    public boolean isIncludeMetaArticles() {
        return includeMetaArticles;
    }
}
