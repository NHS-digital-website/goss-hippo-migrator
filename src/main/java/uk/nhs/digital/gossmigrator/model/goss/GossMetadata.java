package uk.nhs.digital.gossmigrator.model.goss;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import uk.nhs.digital.gossmigrator.model.goss.enums.GossExportFieldNames;
import uk.nhs.digital.gossmigrator.model.goss.enums.MetadataGroup;

import java.util.*;

import static uk.nhs.digital.gossmigrator.model.goss.enums.GossExportFieldNames.METADATA_GROUP;
import static uk.nhs.digital.gossmigrator.model.goss.enums.GossExportFieldNames.METADATA_NAME;
import static uk.nhs.digital.gossmigrator.model.goss.enums.GossExportFieldNames.METADATA_VALUE;
import static uk.nhs.digital.gossmigrator.model.goss.enums.MetadataGroup.GEO_COVERAGE;
import static uk.nhs.digital.gossmigrator.model.goss.enums.MetadataGroup.SUB_TOPICS;
import static uk.nhs.digital.gossmigrator.model.goss.enums.MetadataGroup.TOPICS;

public class GossMetadata {

    private final List<String> GeographicalCoverage;
    private final Map<String, String> TopicsMap;

    public GossMetadata(JSONObject gossJson, GossExportFieldNames fieldName, long gossId) {

        GeographicalCoverage = new ArrayList<>();
        TopicsMap = new HashMap<>();
        JSONArray arr = (JSONArray)gossJson.get(fieldName.getName());
        if(arr != null){
            for (int i = 0; i < arr.size(); i++) {
                JSONObject obj = (JSONObject)arr.get(i);
                String group = (String) obj.get(METADATA_GROUP);
                String name = (String) obj.get(METADATA_NAME);
                String value = (String) obj.get(METADATA_VALUE);

                if(GEO_COVERAGE.getDescription().equals(group)){
                    GeographicalCoverage.add(value);
                }else if(TOPICS.getDescription().equals(group) || SUB_TOPICS.getDescription().equals(group)){
                    TopicsMap.put(name, value);
                }
            }
        }
    }

    public List<String> getGeographicalCoverage() {
        return GeographicalCoverage;
    }

    public Map<String, String> getTopicsMap() {
        return TopicsMap;
    }
}
