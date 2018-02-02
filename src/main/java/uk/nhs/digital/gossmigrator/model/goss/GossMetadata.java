package uk.nhs.digital.gossmigrator.model.goss;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import uk.nhs.digital.gossmigrator.model.goss.enums.GossExportFieldNames;

import java.util.List;

import static uk.nhs.digital.gossmigrator.model.goss.enums.GossExportFieldNames.METADATA_GROUP;

public class GossMetadata {

    private final List<String> GeographicalCoverage;

    public GossMetadata(JSONObject gossJson, GossExportFieldNames fieldName, long gossId) {

        JSONArray arr = (JSONArray)gossJson.get(fieldName.getName());
        if(arr != null){
            for (int i = 0; i < arr.size(); i++) {
                JSONObject obj = (JSONObject)arr.get(i);
                String group = (String) obj.get(METADATA_GROUP);
            }

        }
        GeographicalCoverage = null;
    }

    public List<String> getGeographicalCoverage() {
        return GeographicalCoverage;
    }
}
