package uk.nhs.digital.gossmigrator.model.hippo;

import org.apache.commons.lang3.StringUtils;
import uk.nhs.digital.gossmigrator.model.goss.GossLink;

public class HippoLink {

    private Long id;
    private String address;
    private String displayText;

    public HippoLink(GossLink gossLink) {
        this.id = gossLink.getId();
        this.address = gossLink.getAddress();
        this.displayText = gossLink.getDisplayText();
    }

    public HippoLink(String address, String displayText){
        this.address = address;
        this.displayText = displayText;
    }

    @SuppressWarnings("unused")
    public String getAddress() {
        return address;
    }

    @SuppressWarnings("unused")
    public String getDisplayText() {
        // TODO article 5199 has a quote at start of heading
        return StringUtils.removeAll(displayText,"\"");
    }

    public Long getId() {
        return id;
    }
}
