package uk.nhs.digital.gossmigrator.model.goss;


import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.digital.gossmigrator.misc.GossExportHelper;
import uk.nhs.digital.gossmigrator.misc.TextHelper;
import uk.nhs.digital.gossmigrator.model.goss.enums.ContentType;
import uk.nhs.digital.gossmigrator.model.goss.enums.GossExportFieldNames;

import java.util.Date;

import static uk.nhs.digital.gossmigrator.misc.GossExportHelper.*;
import static uk.nhs.digital.gossmigrator.model.goss.enums.ContentType.SERVICE;
import static uk.nhs.digital.gossmigrator.model.goss.enums.GossExportFieldNames.*;

public class GossContent implements Comparable<GossContent> {
    private final static Logger LOGGER = LoggerFactory.getLogger(GossContent.class);

    // Fields read from Goss export.
    private String heading;
    private long id;
    private GossContentExtra extra;
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
    // Looks like we don't need the media json array at the moment.
    private Date displayDate;
    private Date displayEndDate;


    // Non Goss sourced variables
    private Integer depth;
    private String jcrParentPath;
    private String jcrNodeName;
    private ContentType contentType;
    private long gossExportFileLine;
    private int childrenCount;

    public GossContent(JSONObject gossJson, long gossExportFileLine) {
        this.gossExportFileLine = gossExportFileLine;
        JSONObject extraJson = (JSONObject) gossJson.get(GossExportFieldNames.EXTRA.getName());
        id = getIdOrError(gossJson, ID);
        LOGGER.debug("Populating GossContentId:{}, File Line:{}", id, gossExportFileLine);
        extra = new GossContentExtra();
        heading = getString(gossJson, HEADING, id);
        templateId = getLong(gossJson, TEMPLATE_ID, id);
        summary = getString(gossJson, SUMMARY, id);
        friendlyUrl = getString(gossJson, FRIENDLY_URL, id);
        linkText = getString(gossJson, LINK_TEXT, id);
        parentId = getLong(gossJson, PARENTID, id);
        introduction = getString(gossJson, INTRO, id);
        date = GossExportHelper.getDate(gossJson, DATE, id);
        text = getString(gossJson, TEXT, id);
        display = getString(gossJson, DISPLAY, id);
        archiveDate = GossExportHelper.getDate(gossJson, ARCHIVE_DATE, id);
        displayDate = GossExportHelper.getDate(gossJson, DISPLAY_DATE, id);
        displayEndDate = GossExportHelper.getDate(gossJson, DISPLAY_END_DATE, id);
        extra.setIncludeChildArticles(getBoolean(extraJson, EXTRA_INCLUDE_CHILD, false));
        extra.setIncludeRelatedArticles(getBoolean(extraJson, EXTRA_INCLUDE_RELATED, false));

        if(StringUtils.isEmpty(friendlyUrl)) {
            jcrNodeName = TextHelper.toLowerCaseDashedValue(heading);
        }else{
            jcrNodeName = friendlyUrl;
        }
        // TODO logic for content type replaces this.
        setContentType(SERVICE);
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
    public GossContentExtra getExtra() {
        return extra;
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
        return jcrParentPath + jcrNodeName;
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
}
