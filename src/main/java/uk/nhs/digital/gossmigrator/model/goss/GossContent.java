package uk.nhs.digital.gossmigrator.model.goss;

import static uk.nhs.digital.gossmigrator.misc.GossExportHelper.*;
import static uk.nhs.digital.gossmigrator.model.goss.enums.DateFormatEnum.GOSS_LONG_FORMAT;
import static uk.nhs.digital.gossmigrator.model.goss.enums.GossExportFieldNames.*;

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
import java.util.*;

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
    private String status;

    //Content should be imported only if true
    Boolean relevantContentFlag = false;
    private List<GossContentMeta> metaList = new ArrayList<>();
    List<String> warnings = new ArrayList<>();
    GossContentExtra extra;
    Date displayDate;
    List<Long> relatedArticles = new ArrayList<>();
    private String introduction;

    // Non Goss sourced variables
    Integer depth;
    String jcrParentPath;
    String jcrNodeName;
    private Set<Long> children = new HashSet<>();

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
        status = getString(gossJson, STATUS, id);
        introduction = getString(gossJson, INTRO, id);
        jcrNodeName = TextHelper.toLowerCaseDashedValue(heading);

        JSONArray metaJson = (JSONArray) gossJson.get(GossExportFieldNames.META_DATA.getName());
        if (null != metaJson) {
            processMetaNode(metaJson);
        }

        if (contentType.isExpectExtraNode()) {
            processExtraNode(gossJson);
        }

        if (contentType.isReadArticlesNode()) {
            JSONArray articlesJson = (JSONArray) gossJson.get(ARTICLES_ARRAY.getName());
            for (Object article : articlesJson) {
                relatedArticles.add(GossExportHelper.getLong((JSONObject) article, ID, id));
            }
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
        // Write folders first.
        if (contentType == ContentType.FOLDER && o.contentType != ContentType.FOLDER) return -1;
        if (contentType != ContentType.FOLDER && o.contentType == ContentType.FOLDER) return 1;
        if (o.getDepth() > depth) return -1;
        if (depth > o.getDepth()) return 1;
        if (contentType != o.getContentType()) o.getContentType().compareTo(this.contentType);
        return heading.compareTo(o.getHeading());
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

    /**
     * This is the raw string from the database containing each text area.
     * Each text area is separated into textbody tags with the ID property referencing its name.
     *
     * @return Html text.
     */
    public String getText() {
        return text;
    }

    public Set<Long> getChildren() {
        return children;
    }

    public void addChild(Long childrenId) {
        this.children.add(childrenId);
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

    public List<GossContentMeta> getMetaList() {
        return metaList;
    }

    public List<Long> getRelatedArticles() {
        return relatedArticles;
    }

    public String getIntroduction() {
        return introduction;
    }

    public GossContentExtra getExtra() {
        return extra;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public void setJcrNodeName(String jcrNodeName) {
        this.jcrNodeName = jcrNodeName;
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

    public String getStatus() {
        return status;
    }

    public String getJcrParentPath() {
        return jcrParentPath;
    }

    public Date getDisplayDate() {
        return displayDate;
    }
}
