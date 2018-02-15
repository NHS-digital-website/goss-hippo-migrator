package uk.nhs.digital.gossmigrator.model.goss;

import org.json.simple.JSONObject;
import uk.nhs.digital.gossmigrator.misc.GossExportHelper;
import uk.nhs.digital.gossmigrator.model.goss.enums.GossExportFieldNames;

import java.util.List;

public class GossHubContent extends GossServiceContent{

    List<Long> componentIds;

    public GossHubContent(JSONObject gossJson, long gossExportFileLine) {
        super(gossJson, gossExportFileLine);
        JSONObject extra = (JSONObject)gossJson.get(GossExportFieldNames.EXTRA.getName());
        componentIds = GossExportHelper.getLongList(extra, GossExportFieldNames.COMPONENTS, id);
    }

    public List<Long> getComponentIds() {
        return componentIds;
    }

}