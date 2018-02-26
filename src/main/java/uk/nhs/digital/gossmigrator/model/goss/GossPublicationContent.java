package uk.nhs.digital.gossmigrator.model.goss;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.digital.gossmigrator.misc.GossExportHelper;
import uk.nhs.digital.gossmigrator.model.goss.enums.ContentType;
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
import static uk.nhs.digital.gossmigrator.model.goss.enums.GossMetaType.*;

public class GossPublicationContent extends GossContent {

    private List<GossFile> files = new ArrayList<>();

    private GossPublicationContent(JSONObject gossJson, long gossExportFileLine) {
        super(gossJson, gossExportFileLine, ContentType.PUBLICATION);

        // Process Media node
        JSONArray filesJson = (JSONArray) gossJson.get(MEDIA.getName());
        if (null != filesJson) {
            for (Object fileObject : filesJson) {
                GossFile file = new GossFile((JSONObject) fileObject);
                files.add(file);
            }
        }
    }

    /*
     * Factory method to generate a GossPublicationContent
     */
    public static GossPublicationContent getInstance(JSONObject gossJson, long gossExportFileLine) {
        return new GossPublicationContent(gossJson, gossExportFileLine);
    }

    public List<GossContentMeta> getTaxonomyData() {
        List<GossContentMeta> list = getMetaByGroup(TOPIC);
        list.addAll(getMetaByGroup(SUB_TOPIC));
        return list;
    }

    @SuppressWarnings("unused")
    public GossContentExtra getExtra() {
        return extra;
    }

    public String getGeographicalData() {
        return getValuesAsCsvList(getMetaByGroup(GEOGRAPHICAL), 1, GEOGRAPHICAL.toString());
    }

    public String getInformationTypes() {
        return getValuesAsCsvList(getMetaByGroup(INFORMATION_TYPE));
    }

    public String getGranularity() {
        return getValuesAsCsvList(getMetaByGroup(GRANULARITY));
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
