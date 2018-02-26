package uk.nhs.digital.gossmigrator.model.goss.enums;

public enum ContentType {
    SERVICE("Service", false),
    PUBLICATION("Publication", true),
    SERIES("Series", false),
    HUB("Hub", true),
    GENERAL("General", false),
    LIST_PAGE("List", true),
    REDIRECT("Redirect", false);

    private String description;
    private boolean expectExtraNode;

    ContentType(String description, boolean expectExtraNode) {
        this.description = description;
        this.expectExtraNode = expectExtraNode;
    }

    public String getDescription() {
        return description;
    }

    public boolean isExpectExtraNode() {
        return expectExtraNode;
    }
}
