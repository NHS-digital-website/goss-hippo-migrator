package uk.nhs.digital.gossmigrator.model.goss.enums;

public enum GossSourceFile {
    DIGITAL("Digital"),
    CONTENT("Content");

    private String description;

    GossSourceFile(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
