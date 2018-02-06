package uk.nhs.digital.gossmigrator.model.goss;


import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.digital.gossmigrator.config.Config;
import uk.nhs.digital.gossmigrator.misc.GossExportHelper;
import uk.nhs.digital.gossmigrator.misc.TextHelper;
import uk.nhs.digital.gossmigrator.model.goss.enums.ContentType;
import uk.nhs.digital.gossmigrator.model.goss.enums.GossExportFieldNames;
import uk.nhs.digital.gossmigrator.model.goss.enums.GossMetaType;
import uk.nhs.digital.gossmigrator.model.mapping.MetadataMappingItems;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static uk.nhs.digital.gossmigrator.config.TemplateConfig.PUBLICATION_ID;

import static uk.nhs.digital.gossmigrator.misc.GossExportHelper.*;
import static uk.nhs.digital.gossmigrator.model.goss.enums.ContentType.PUBLICATION;
import static uk.nhs.digital.gossmigrator.model.goss.enums.ContentType.SERVICE;
import static uk.nhs.digital.gossmigrator.model.goss.enums.DateFormatEnum.GOSS_LONG_FORMAT;
import static uk.nhs.digital.gossmigrator.model.goss.enums.GossExportFieldNames.*;


public class GossContent implements Comparable<GossContent> {
    private final static Logger LOGGER = LoggerFactory.getLogger(GossContent.class);

    // Fields read from Goss export.
    private String heading;
    protected long id;

    private Long templateId;
    private String summary;
    private String friendlyUrl;
    private String linkText;
    private Long parentId;
    private String introduction;
    private Date date;
    private String text;
    private String display;
    private Date archiveDate;

    //Publications
    private GossContentExtra extra;
    private GossMetadata metadata;

    // Looks like we don't need the media json array at the moment.
    private Date displayDate;
    private Date displayEndDate;
    private List<GossContentMeta> geographicalData = new ArrayList<>();
    private List<GossContentMeta> taxonomyData = new ArrayList<>();
    private List<GossContentMeta> informationTypes = new ArrayList<>();
    private List<GossContentMeta> granularity = new ArrayList<>();

    // Non Goss sourced variables
    private Integer depth;
    private String jcrParentPath;
    private String jcrNodeName;
    private ContentType contentType;
    private long gossExportFileLine;
    private int childrenCount;

    // TODO lose the constructors and put in factory methods for each content type.
    // TODO e.g. Series, General and Publications (which need special node name/parent).

    /**
     * Constructor that populates based upon series csv line.
     *
     * @param seriesCsv CSV
     */
    public GossContent(CSVRecord seriesCsv) {
        if (seriesCsv.size() != 3) {
            LOGGER.error("Line in series csv had unexpected number of columns. Expected 3, got {}. Data:{}", seriesCsv.size(), seriesCsv);
        }
        this.id = Long.parseLong(seriesCsv.get(0)) * (-1L);
        this.heading = seriesCsv.get(1);
        this.summary = seriesCsv.get(2);

        // Currently the jcr node containing the series content has to be called content.
        // This is the convention used by RPS project.
        jcrNodeName = "content";
        jcrParentPath =
                Paths.get(Config.JCR_PUBLICATION_ROOT
                        , TextHelper.toLowerCaseDashedValue(this.heading)).toString();
        depth = 1;
        parentId = this.id;  // No parent doc for series.
        setContentType(ContentType.SERIES);
    }

    /**
     * Constructor that populates from an article node in Goss export.
     *
     * @param gossJson           Goss article
     * @param gossExportFileLine Source line in export for logging.
     */
    public GossContent(JSONObject gossJson, long gossExportFileLine) {
        this.gossExportFileLine = gossExportFileLine;
        id = getIdOrError(gossJson, ID);
        LOGGER.debug("Populating GossContentId:{}, File Line:{}", id, gossExportFileLine);
        JSONArray metaJson = (JSONArray) gossJson.get(GossExportFieldNames.META_DATA.getName());
        if(null != metaJson) {
            processMetaNode(metaJson);
        }

        heading = getString(gossJson, HEADING, id);
        templateId = getLong(gossJson, TEMPLATE_ID, id);
        summary = getString(gossJson, SUMMARY, id);
        friendlyUrl = getString(gossJson, FRIENDLY_URL, id);
        linkText = getString(gossJson, LINK_TEXT, id);
        parentId = getLong(gossJson, PARENTID, id);
        introduction = getString(gossJson, INTRO, id);
        date = GossExportHelper.getDate(gossJson, DATE, id, GOSS_LONG_FORMAT);
        text = getString(gossJson, TEXT, id);
        display = getString(gossJson, DISPLAY, id);
        archiveDate = GossExportHelper.getDate(gossJson, ARCHIVE_DATE, id, GOSS_LONG_FORMAT);
        displayDate = GossExportHelper.getDate(gossJson, DISPLAY_DATE, id, GOSS_LONG_FORMAT);
        displayEndDate = GossExportHelper.getDate(gossJson, DISPLAY_END_DATE, id, GOSS_LONG_FORMAT);
        JSONObject extraJson = (JSONObject) gossJson.get(GossExportFieldNames.EXTRA.getName());
        extra = new GossContentExtra(gossJson, EXTRA, id);
        metadata = new GossMetadata(gossJson, METADATA, id);
        extra.setIncludeChildArticles(getBoolean(extraJson, EXTRA_INCLUDE_CHILD, false));
        extra.setIncludeRelatedArticles(getBoolean(extraJson, EXTRA_INCLUDE_RELATED, false));


        if (StringUtils.isEmpty(friendlyUrl)) {
            jcrNodeName = TextHelper.toLowerCaseDashedValue(heading);
        } else {
            jcrNodeName = friendlyUrl;
        }
        // TODO logic for content type replaces this.
        if(templateId == PUBLICATION_ID){
            setContentType(PUBLICATION);
        }else {
            setContentType(SERVICE);
        }
    }

    private void processMetaNode(JSONArray metaJson){
        for (Object metaObject : metaJson) {
            JSONObject meta = (JSONObject) metaObject;
            String metaGroup = GossExportHelper.getString(meta, GossExportFieldNames.META_DATA_GROUP, id);
            GossMetaType gossMetaType = GossMetaType.getByGroup(metaGroup);
            if (null == gossMetaType) {
                LOGGER.warn("Unexpected goss meta group:{}, in export line {}. Article id:{}."
                        , metaGroup, gossExportFileLine, id);
            } else {
                String gossMetaValue = getString(meta, GossExportFieldNames.META_DATA_VALUE, gossExportFileLine);
                String gossMetaName = getString(meta, GossExportFieldNames.META_DATA_VALUE, gossExportFileLine);

                switch (gossMetaType) {
                    case TAXONOMY:
                        taxonomyData.add(new GossContentMeta(gossMetaName, gossMetaValue, metaGroup));
                        break;
                    case GEOGRAPHICAL:
                        geographicalData.add(new GossContentMeta(gossMetaName, gossMetaValue, metaGroup));
                        break;
                    case INFORMATION_TYPE:
                        informationTypes.add(new GossContentMeta(gossMetaName,gossMetaValue,metaGroup));
                        break;
                    case GRANULARITY:
                        granularity.add(new GossContentMeta(gossMetaName,gossMetaValue,metaGroup));
                        break;
                    default:
                        LOGGER.warn("Meta group ignored:{}, article id:{}", metaGroup, id);

                }
            }
        }
    }

    public Integer getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    @Override
    public int compareTo(GossContent o) {
        if (o.getDepth() > depth) return -1;
        if (depth > o.getDepth()) return 1;
        return 0;
    }

    public long getId() {
        return id;
    }

    public Long getParentId() {
        return parentId;
    }

    public String getHeading() {
        return heading;
    }



    @SuppressWarnings("unused")
    public Long getTemplateId() {
        return templateId;
    }

    public String getSummary() {
        return summary;
    }

    @SuppressWarnings("unused")
    public String getFriendlyUrl() {
        return friendlyUrl;
    }

    /**
     * Text seen on links to the article.
     *
     * @return Link text.
     */
    @SuppressWarnings("unused")
    public String getLinkText() {
        return linkText;
    }

    public String getIntroduction() {
        return introduction;
    }

    /**
     * Article creation date.
     *
     * @return Creation date.
     */
    @SuppressWarnings("unused")
    public Date getDate() {
        return date;
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

    @SuppressWarnings("unused")
    public Date getArchiveDate() {
        return archiveDate;
    }

    /**
     * Display start date. i.e. When published.
     *
     * @return date.
     */
    @SuppressWarnings("unused")
    public Date getDisplayDate() {
        return displayDate;
    }

    public void setJcrParentPath(String jcrParentPath) {
        this.jcrParentPath = jcrParentPath;
    }

    public String getJcrNodeName() {
        return jcrNodeName;
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

    /**
     * Date representing when this article will stop displaying
     *
     * @return Date
     */
    @SuppressWarnings("unused")
    public Date getDisplayEndDate() {
        return displayEndDate;
    }

    public String getJcrPath() {
        return Paths.get(jcrParentPath, jcrNodeName).toString();
    }

    public String getJcrParentPath() {
        return jcrParentPath;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    public ContentType getContentType() {
        return contentType;
    }

    @Override
    public String toString() {
        return "GossContent{" +
                "id=" + id +
                ", heading='" + heading + '\'' +
                '}';
    }

    public int getChildrenCount() {
        return childrenCount;
    }

    public void setChildrenCount(int childrenCount) {
        this.childrenCount = childrenCount;
    }

    @SuppressWarnings("unused")
    public GossContentExtra getExtra() {
        return extra;
    }

    public GossMetadata getMetadata() {
        return metadata;
    }

    public List<GossContentMeta> getTaxonomyData() {
        return taxonomyData;
    }
}
