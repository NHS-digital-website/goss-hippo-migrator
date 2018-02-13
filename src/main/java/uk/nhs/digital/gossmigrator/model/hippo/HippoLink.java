package uk.nhs.digital.gossmigrator.model.hippo;

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

    @SuppressWarnings("unused")
    public String getAddress() {
        return address;
    }

    @SuppressWarnings("unused")
    public String getDisplayText() {
        return displayText;
    }

    public Long getId() {
        return id;
    }
}
