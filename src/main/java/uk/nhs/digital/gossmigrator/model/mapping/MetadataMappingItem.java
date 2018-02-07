package uk.nhs.digital.gossmigrator.model.mapping;

public class MetadataMappingItem {
    private String gossGroup;
    private String gossValue;
    private String hippoValue;

    public String getGossGroup() {
        return gossGroup;
    }

    public String getGossValue() {
        return gossValue;
    }

    public String getHippoValue() {
        return hippoValue;
    }

    public void setGossGroup(String gossGroup) {
        this.gossGroup = gossGroup;
    }

    public void setGossValue(String gossValue) {
        this.gossValue = gossValue;
    }

    public void setHippoValue(String hippoValue) {
        this.hippoValue = hippoValue;
    }
}
