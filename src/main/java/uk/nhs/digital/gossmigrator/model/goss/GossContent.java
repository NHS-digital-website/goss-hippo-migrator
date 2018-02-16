package uk.nhs.digital.gossmigrator.model.goss;


import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.digital.gossmigrator.GossImporter;
import uk.nhs.digital.gossmigrator.misc.TextHelper;
import uk.nhs.digital.gossmigrator.model.goss.enums.ContentType;
import uk.nhs.digital.gossmigrator.model.goss.enums.GossExportFieldNames;

import java.nio.file.Paths;
import java.util.List;

import static uk.nhs.digital.gossmigrator.misc.GossExportHelper.*;
import static uk.nhs.digital.gossmigrator.model.goss.enums.GossExportFieldNames.*;


public class GossContent implements Comparable<GossContent> {
    private final static Logger LOGGER = LoggerFactory.getLogger(GossContent.class);

    // Fields read from Goss export.
    String heading;
    protected long id;
    protected String summary;
    Long parentId;
    protected String text;
    ContentType contentType;
    private String friendlyUrl;


    // Non Goss sourced variables
    Integer depth;
    String jcrParentPath;
    String jcrNodeName;
    private int childrenCount;

    // TODO lose the constructors and put in factory methods for each content type.
    // TODO e.g. Series, General and Publications (which need special node name/parent).

    /*
     * Constructor that populates from an article node in Goss export.
     *
     * @param gossJson           Goss article
     * @param gossExportFileLine Source line in export for logging.
     */
    public GossContent(JSONObject gossJson, long gossExportFileLine) {
        id = getIdOrError(gossJson, ID);
        LOGGER.debug("Populating GossContentId:{}, File Line:{}", id, gossExportFileLine);
        heading = getString(gossJson, HEADING, id);
        summary = getString(gossJson, SUMMARY, id);
        parentId = getLong(gossJson, PARENTID, id);
        text = getString(gossJson, TEXT, id);
        friendlyUrl = getString(gossJson, FRIENDLY_URL,id);

        if (StringUtils.isEmpty(friendlyUrl)) {
            jcrNodeName = TextHelper.toLowerCaseDashedValue(heading);
        } else {
            jcrNodeName = friendlyUrl;
        }
    }

    /*
     * No-Arg Constructor to populate Series.
     */
    public GossContent(){}

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
        for (GossContentMeta item : sourceList) {
            if (maxExpectedValues < count && maxExpectedValues > 0) {
                break;
            }

            String hippoValue = GossImporter.metadataMapping.getHippoValue(item);
            if(!StringUtils.isEmpty(hippoValue)) {
                if (!isFirstRow) {
                    csvList.append(", ");
                }
                csvList.append("\"").append(hippoValue).append("\"");
                isFirstRow = false;
            }
            count++;
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

    public long getId() {
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

}
