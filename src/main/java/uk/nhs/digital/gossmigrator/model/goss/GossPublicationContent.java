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
    // Looks like we don't need the media json array at the moment.
    private Date displayDate;
    private Date displayEndDate;
    private List<GossContentMeta> geographicalData = new ArrayList<>();
    private List<GossContentMeta> taxonomyData = new ArrayList<>();
    private List<GossContentMeta> informationTypes = new ArrayList<>();
    private List<GossContentMeta> granularity = new ArrayList<>();

    private GossPublicationContent(JSONObject gossJson, long gossExportFileLine){
        super(gossJson, gossExportFileLine);
        this.gossExportFileLine = gossExportFileLine;
        contentType = PUBLICATION;
        displayDate = GossExportHelper.getDate(gossJson, DISPLAY_DATE, id, GOSS_LONG_FORMAT);
        displayEndDate = GossExportHelper.getDate(gossJson, DISPLAY_END_DATE, id, GOSS_LONG_FORMAT);
        JSONObject extraJson = (JSONObject) gossJson.get(GossExportFieldNames.EXTRA.getName());
        extra = new GossContentExtra(gossJson, EXTRA, id);
        extra.setIncludeChildArticles(getBoolean(extraJson, EXTRA_INCLUDE_CHILD, false));
        extra.setIncludeRelatedArticles(getBoolean(extraJson, EXTRA_INCLUDE_RELATED, false));

    }

    /*
     * Factory method to generate a GossPublicationContent and assign the Metadata information
     */
    public static GossPublicationContent getInstance(JSONObject gossJson, long gossExportFileLine){
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
                        informationTypes.add(new GossContentMeta(gossMetaName, gossMetaValue, metaGroup));
                        break;
                    case GRANULARITY:
                        granularity.add(new GossContentMeta(gossMetaName, gossMetaValue, metaGroup));
                        break;
                    default:
                        LOGGER.warn("Meta group ignored:{}, article id:{}", metaGroup, id);

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
     * Date representing when this article will stop displaying
     *
     * @return Date
     */
    @SuppressWarnings("unused")
    public Date getDisplayEndDate() {
        return displayEndDate;
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


}
