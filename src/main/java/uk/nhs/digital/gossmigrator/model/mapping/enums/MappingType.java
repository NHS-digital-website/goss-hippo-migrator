package uk.nhs.digital.gossmigrator.model.mapping.enums;

public enum MappingType {
    TAXONOMY_MAPPING("taxonomy", 1, 4),
    METADATA_MAPPING("metadata", 1, 3),
    DOCUMENT_TYPE("document type", 1, 2),
    GENERAL_TYPE("general document type", 1, 2),
    TEMPLATE_ID("template id",1,2),
    VALID_TAXONOMY_KEYS("taxonomy_keys", 0, 1);

    private final String description;
    private final long headerCount;
    private final int expectedColumns;

    MappingType(String description, long headerCount, int expectedColumns) {
        this.description = description;
        this.headerCount = headerCount;
        this.expectedColumns = expectedColumns;
    }

    public String getDescription() {
        return description;
    }

    public long getHeaderCount() {
        return headerCount;
    }

    public int getExpectedColumns() {
        return expectedColumns;
    }
}
