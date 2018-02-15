package uk.nhs.digital.gossmigrator.model.hippo;

import uk.nhs.digital.gossmigrator.model.goss.GossHubContent;
import uk.nhs.digital.gossmigrator.model.goss.GossServiceContent;

import java.util.List;

public class Hub extends Service {

    List<Long> componentIds;

    public Hub(GossServiceContent gossContent){
        super(gossContent);
        componentIds = ((GossHubContent)gossContent).getComponentIds();
    }

    @SuppressWarnings("unused")
    public List<Long> getComponentIds() {
        return componentIds;
    }
}
