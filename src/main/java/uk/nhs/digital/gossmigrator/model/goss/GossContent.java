package uk.nhs.digital.gossmigrator.model.goss;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.digital.gossmigrator.GossImporter;
import uk.nhs.digital.gossmigrator.misc.GossExportHelper;
import uk.nhs.digital.gossmigrator.misc.TextHelper;
import uk.nhs.digital.gossmigrator.model.goss.enums.ContentType;
import uk.nhs.digital.gossmigrator.model.goss.enums.GossExportFieldNames;
import uk.nhs.digital.gossmigrator.model.goss.enums.GossMetaType;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static uk.nhs.digital.gossmigrator.misc.GossExportHelper.*;
import static uk.nhs.digital.gossmigrator.model.goss.enums.DateFormatEnum.GOSS_LONG_FORMAT;
import static uk.nhs.digital.gossmigrator.model.goss.enums.GossExportFieldNames.*;

public class GossContent implements Comparable<GossContent> {
    private final static Logger LOGGER = LoggerFactory.getLogger(GossContent.class);

    // Fields read from Goss export.
    String heading;
    protected Long id;
    protected String summary;
    Long parentId;
    protected String text;
    ContentType contentType;
    private String friendlyUrl;
    private Long templateId;
    private Date displayEndDate;
    private String display;

    //Content should be imported only if true
    private Boolean relevantContentFlag = false;
    private List<GossContentMeta> metaList = new ArrayList<>();
    List<String> warnings = new ArrayList<>();
    GossContentExtra extra;
    Date displayDate;

    // Non Goss sourced variables
    Integer depth;
    String jcrParentPath;
    String jcrNodeName;
    private int childrenCount;

    /*
     * Constructor that populates from an article node in Goss export.
     *
     * @param gossJson           Goss article
     * @param gossExportFileLine Source line in export for logging.
     */
    public GossContent(JSONObject gossJson, long gossExportFileLine, ContentType contentType) {
        id = getIdOrError(gossJson, ID);

        this.contentType = contentType;
        LOGGER.debug("Populating GossContentId:{}, File Line:{}", id, gossExportFileLine);
        displayDate = GossExportHelper.getDate(gossJson, DISPLAY_DATE, id, GOSS_LONG_FORMAT);
        displayEndDate = GossExportHelper.getDate(gossJson, DISPLAY_END_DATE, id, GOSS_LONG_FORMAT);
        heading = getString(gossJson, HEADING, id);
        summary = getString(gossJson, SUMMARY, id);
        parentId = getLong(gossJson, PARENTID, id);
        text = getString(gossJson, TEXT, id);
        friendlyUrl = getString(gossJson, FRIENDLY_URL, id);
        templateId = getLong(gossJson, TEMPLATE_ID, id);
        display = getString(gossJson, DISPLAY, id);
        displayEndDate = GossExportHelper.getDate(gossJson, DISPLAY_END_DATE, id, GOSS_LONG_FORMAT);

        if (StringUtils.isEmpty(friendlyUrl)) {
            jcrNodeName = TextHelper.toLowerCaseDashedValue(heading);
        } else {
            jcrNodeName = friendlyUrl;
        }

        JSONArray metaJson = (JSONArray) gossJson.get(GossExportFieldNames.META_DATA.getName());
        if (null != metaJson) {
            processMetaNode(metaJson);
        }

        if (contentType.isExpectExtraNode()) {
            processExtraNode(gossJson);
        }
    }

    private void processExtraNode(JSONObject gossJson) {
        Object etcid = gossJson.get(EXTRA_OBJECT_ID.getName());

        // Not all documents have an extra section.
        if (etcid instanceof Long) {
            extra = new GossContentExtra(gossJson, EXTRA, id);
        } else {
            LOGGER.error("{} with no extra node. Id:{}", contentType.name(), id);
            extra = new GossContentExtra();
        }
    }

    List<GossContentMeta> getMetaByGroup(GossMetaType gossMetaType) {
        List<GossContentMeta> list = new ArrayList<>();
        for (GossContentMeta metaObject : metaList) {
            if (metaObject.getGroup().equals(gossMetaType.getGroup())) {
                list.add(metaObject);
            }
        }
        return list;
    }

    private void processMetaNode(JSONArray metaJson) {
        for (Object metaObject : metaJson) {
            JSONObject meta = (JSONObject) metaObject;
            String metaGroup = GossExportHelper.getString(meta, GossExportFieldNames.META_DATA_GROUP, id);
            GossMetaType gossMetaType = GossMetaType.getByGroup(metaGroup);
            if (null == gossMetaType) {
                LOGGER.warn("Unexpected goss meta group:{}. Article id:{}."
                        , metaGroup, id);
                warnings.add("Unexpected goss meta group: " + metaGroup);
            }
            String gossMetaValue = getString(meta, GossExportFieldNames.META_DATA_VALUE, id);
            String gossMetaName = getString(meta, GossExportFieldNames.META_DATA_VALUE, id);

            metaList.add(new GossContentMeta(gossMetaName, gossMetaValue, metaGroup));
        }
    }

    /*
     * No-Arg Constructor to populate Series.
     */
    public GossContent() {
    }

    @Override
    public int compareTo(GossContent o) {
        if (o.getDepth() > depth) return -1;
        if (depth > o.getDepth()) return 1;
        return 0;
    }

    @Override
    public String toString() {
        return "GossContent{" +
                "id=" + id +
                ", heading='" + heading + '\'' +
                '}';
    }

    String getValuesAsCsvList(List<GossContentMeta> sourceList) {
        return getValuesAsCsvList(sourceList, -1);
    }

    private String getValuesAsCsvList(List<GossContentMeta> sourceList, int maxExpectedValues) {
        StringBuilder csvList = new StringBuilder();
        boolean isFirstRow = true;
        int count = 0;
        if (null != sourceList) {
            for (GossContentMeta item : sourceList) {
                if (maxExpectedValues < count && maxExpectedValues > 0) {
                    break;
                }

                String hippoValue = GossImporter.metadataMapping.getHippoValue(item);
                if (!StringUtils.isEmpty(hippoValue)) {
                    if (!isFirstRow) {
                        csvList.append(", ");
                    }
                    csvList.append("\"").append(hippoValue).append("\"");
                    isFirstRow = false;
                }
                count++;
            }
        }

        if (count == 0) {
            return "\"\"";
        }

        return csvList.toString();
    }

    String getValuesAsCsvList(List<GossContentMeta> sourceList, int maxExpectedValues, String context) {
        if (sourceList.size() > maxExpectedValues) {
            LOGGER.warn("More than expected number of meta items.  ArticleId:{}. Expected max:{}, got:{}, context:{}"
                    , id, maxExpectedValues, sourceList.size(), context);
        }
        return getValuesAsCsvList(sourceList, maxExpectedValues);
    }

    /*
     * Adds one additional level for Services/General folder structure
     */
    public String getModifiedPath() {
        return Paths.get(jcrParentPath, jcrNodeName, jcrNodeName).toString();
    }


    /**
     * This is the raw string from the database containing each text area.
     * Each text area is separated into textbody tags with the ID property referencing its name.
     *
     * @return Html text.
     */
    public String getText() {
        return text;
    }

    public int getChildrenCount() {
        return childrenCount;
    }

    public void setChildrenCount(int childrenCount) {
        this.childrenCount = childrenCount;
    }

    public Long getId() {
        return id;
    }

    public Long getParentId() {
        return parentId;
    }

    public String getHeading() {
        return heading;
    }

    public String getSummary() {
        return summary;
    }

    public void setJcrParentPath(String jcrParentPath) {
        this.jcrParentPath = jcrParentPath;
    }

    public String getJcrNodeName() {
        return jcrNodeName;
    }

    public String getJcrPath() {
        return Paths.get(jcrParentPath, jcrNodeName).toString();
    }

    public String getJcrParentPath() {
        return jcrParentPath;
    }

    public Integer getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public ContentType getContentType() {
        return contentType;
    }

    @SuppressWarnings("unused")
    public String getFriendlyUrl() {
        return friendlyUrl;
    }

    public Long getTemplateId() {
        return templateId;
    }

    /**
     * Date representing when this article will stop displaying
     *
     * @return Date
     */
    @SuppressWarnings("unused")
    public Date getDisplayEndDate() {
        return displayEndDate;
    }

    /**
     * Either set as on or off if displayed or hidden.
     *
     * @return on, off or hidden.
     */
    @SuppressWarnings("unused")
    public String getDisplay() {
        return display;
    }

    public Boolean isRelevantContentFlag() {
        return relevantContentFlag;
    }

    public void setRelevantContentFlag(Boolean relevantContentFlag) {
        this.relevantContentFlag = relevantContentFlag;
    }
}
