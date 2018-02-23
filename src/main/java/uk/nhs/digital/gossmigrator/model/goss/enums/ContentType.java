package uk.nhs.digital.gossmigrator.model.goss.enums;

public enum ContentType {
    SERVICE("Service"),
    PUBLICATION("Publication"),
    SERIES("Series"),
    HUB("Hub"),
    GENERAL("General");

    ContentType(String description) {
        this.description = description;
    }

    private String description;

    public String getDescription() {
        return description;
    }
}
