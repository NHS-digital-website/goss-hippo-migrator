package uk.nhs.digital.gossmigrator.config;

/**
 * Internal non environment specific constants.
 */
public interface Constants {
    String GOSS_EXTRACT_PREFIX = "{ \"docs\":";
    String GOSS_EXTRACT_SUFFIX = "}";
    String OUTPUT_FILE_TYPE_SUFFIX = ".json";
    int SERIES_PUBLICATION_MAPPING_COLUMN_COUNT = 4;
}
