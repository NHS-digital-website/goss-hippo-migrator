package uk.nhs.digital.gossmigrator.model.goss;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.digital.gossmigrator.misc.GossExportHelper;
import uk.nhs.digital.gossmigrator.model.goss.enums.GossExportFieldNames;
import uk.nhs.digital.gossmigrator.model.goss.enums.GossMetaType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static uk.nhs.digital.gossmigrator.misc.GossExportHelper.getBoolean;
import static uk.nhs.digital.gossmigrator.misc.GossExportHelper.getString;
import static uk.nhs.digital.gossmigrator.model.goss.enums.ContentType.PUBLICATION;
import static uk.nhs.digital.gossmigrator.model.goss.enums.DateFormatEnum.GOSS_LONG_FORMAT;
import static uk.nhs.digital.gossmigrator.model.goss.enums.GossExportFieldNames.*;
import static uk.nhs.digital.gossmigrator.model.goss.enums.GossMetaType.GEOGRAPHICAL;

public class GossPublicationContent extends GossContent {

    private final static Logger LOGGER = LoggerFactory.getLogger(GossPublicationContent.class);

    private GossContentExtra extra;
    private long gossExportFileLine;
    private Date displayDate;
    private List<GossContentMeta> geographicalData = new ArrayList<>();
    private List<GossContentMeta> taxonomyData = new ArrayList<>();
    private List<GossContentMeta> informationTypes = new ArrayList<>();
    private List<GossContentMeta> granularity = new ArrayList<>();
    private List<GossFile> files = new ArrayList<>();
    private List<String> warnings = new ArrayList<>();

    private GossPublicationContent(JSONObject gossJson, long gossExportFileLine) {
        super(gossJson, gossExportFileLine);
        this.gossExportFileLine = gossExportFileLine;
        contentType = PUBLICATION;
        displayDate = GossExportHelper.getDate(gossJson, DISPLAY_DATE, id, GOSS_LONG_FORMAT);
        Object etcid = gossJson.get(EXTRA_OBJECT_ID.getName());

        // Not all documents have an extra section.
        if (etcid instanceof Long) {
            JSONObject extraJson = (JSONObject) gossJson.get(GossExportFieldNames.EXTRA.getName());
            extra = new GossContentExtra(gossJson, EXTRA, id);
            extra.setIncludeChildArticles(getBoolean(extraJson, EXTRA_INCLUDE_CHILD, false));
            extra.setIncludeRelatedArticles(getBoolean(extraJson, EXTRA_INCLUDE_RELATED, false));
            extra.setPublicationId(GossExportHelper.getString(extraJson,PUBID, id));
        } else {
            LOGGER.error("Publication with no extra node. Id:{}", id);
            extra = new GossContentExtra();
        }

        // Process Media node
        JSONArray filesJson = (JSONArray) gossJson.get(GossExportFieldNames.MEDIA.getName());
        if (null != filesJson) {
            for (Object fileObject : filesJson) {
                GossFile file = new GossFile((JSONObject) fileObject);
                files.add(file);
            }
        }

    }

    /*
     * Factory method to generate a GossPublicationContent and assign the Metadata information
     */
    public static GossPublicationContent getInstance(JSONObject gossJson, long gossExportFileLine) {
        GossPublicationContent content = new GossPublicationContent(gossJson, gossExportFileLine);
        JSONArray metaJson = (JSONArray) gossJson.get(GossExportFieldNames.META_DATA.getName());
        if (null != metaJson) {
            content.processMetaNode(metaJson);
        }
        return content;
    }

    private void processMetaNode(JSONArray metaJson) {
        for (Object metaObject : metaJson) {
            JSONObject meta = (JSONObject) metaObject;
            String metaGroup = GossExportHelper.getString(meta, GossExportFieldNames.META_DATA_GROUP, id);
            GossMetaType gossMetaType = GossMetaType.getByGroup(metaGroup);
            if (null == gossMetaType) {
                LOGGER.warn("Unexpected goss meta group:{}, in export line {}. Article id:{}."
                        , metaGroup, gossExportFileLine, id);
                warnings.add("Unexpected goss meta group: " + metaGroup);
            } else {
                String gossMetaValue = getString(meta, GossExportFieldNames.META_DATA_VALUE, gossExportFileLine);
                String gossMetaName = getString(meta, GossExportFieldNames.META_DATA_VALUE, gossExportFileLine);

                switch (gossMetaType) {
                    case TOPIC: //Same as SubTopic
                    case SUB_TOPIC:
                        taxonomyData.add(new GossContentMeta(gossMetaName, gossMetaValue, metaGroup));
                        break;
                    case GEOGRAPHICAL:
                        geographicalData.add(new GossContentMeta(gossMetaName, gossMetaValue, metaGroup));
                        break;
                    case INFORMATION_TYPE:
                        informationTypes.add(new GossContentMeta(gossMetaName, gossMetaValue, metaGroup));
                        break;
                    case GRANULARITY:
                        granularity.add(new GossContentMeta(gossMetaName, gossMetaValue, metaGroup));
                        break;
                    case IMPORTFIELDCREATOR:
                        // Don't need
                    case AREA:
                    default:
                        LOGGER.warn("Meta group ignored:{}, article id:{}", metaGroup, id);
                        warnings.add("Meta group ignored: " + metaGroup);
                }
            }
        }
    }

    public List<GossContentMeta> getTaxonomyData() {
        return taxonomyData;
    }

    @SuppressWarnings("unused")
    public GossContentExtra getExtra() {
        return extra;
    }

    public String getGeographicalData() {
        return getValuesAsCsvList(geographicalData, 1, GEOGRAPHICAL.toString());
    }

    public String getInformationTypes() {
        return getValuesAsCsvList(informationTypes);
    }

    public String getGranularity() {
        return getValuesAsCsvList(granularity);
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

    public List<GossFile> getFiles() {
        return files;
    }

    public List<String> getWarnings() {
        return warnings;
    }
}
