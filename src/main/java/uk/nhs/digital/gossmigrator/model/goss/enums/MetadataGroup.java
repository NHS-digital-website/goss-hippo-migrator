package uk.nhs.digital.gossmigrator.model.goss.enums;

public enum MetadataGroup {
    GEO_COVERAGE("Geographical Coverage"),
    TOPICS("Topics"),
    SUB_TOPICS("Sub-Topics");

    private String description;

    MetadataGroup(String format) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
