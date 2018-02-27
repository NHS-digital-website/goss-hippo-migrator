package uk.nhs.digital.gossmigrator.model.goss.enums;

public enum ContentType {
    SERVICE("Service", false, false),
    PUBLICATION("Publication", true, false),
    SERIES("Series", false, false),
    HUB("Hub", true, false),
    GENERAL("General", false, false),
    LIST_PAGE("List", true, true),
    REDIRECT("Redirect", false, true);

    private String description;
    private boolean expectExtraNode;
    private boolean readArticlesNode;

    ContentType(String description, boolean expectExtraNode, boolean readArticlesNode) {
        this.description = description;
        this.expectExtraNode = expectExtraNode;
        this.readArticlesNode = readArticlesNode;
    }

    public String getDescription() {
        return description;
    }

    public boolean isExpectExtraNode() {
        return expectExtraNode;
    }

    public boolean isReadArticlesNode() {
        return readArticlesNode;
    }
}
