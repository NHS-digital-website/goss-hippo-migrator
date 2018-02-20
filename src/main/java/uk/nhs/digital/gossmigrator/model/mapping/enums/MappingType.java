package uk.nhs.digital.gossmigrator.model.mapping.enums;

import uk.nhs.digital.gossmigrator.config.Config;

public enum MappingType {
    SERIES_ITEM("series item", Config.SERIES_FILE_HEADER_COUNT, 3),
    PUBLICATION_SERIES_MAPPING("publication-series",Config.SERIES_PUBLICATION_MAPPING_FILE_HEADER_COUNT, 2),
    TAXONOMY_MAPPING("taxonomy", 1, 2),
    METADATA_MAPPING("metadata", 1, 3),
    DOCUMENT_TYPE("document type", 1, 2);

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
