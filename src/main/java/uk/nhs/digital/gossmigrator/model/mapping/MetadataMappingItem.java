package uk.nhs.digital.gossmigrator.model.mapping;

public class MetadataMappingItem {
    private String gossGroup;
    private String gossValue;
    private String hippoValue;

    public MetadataMappingItem(String gossGroup, String gossValue, String hippoValue) {
        this.gossGroup = gossGroup;
        this.gossValue = gossValue;
        this.hippoValue = hippoValue;
    }

    public String getKey(){
        return gossGroup + gossValue;
    }

    public String getGossGroup() {
        return gossGroup;
    }

    public String getGossValue() {
        return gossValue;
    }

    public String getHippoValue() {
        return hippoValue;
    }
}
