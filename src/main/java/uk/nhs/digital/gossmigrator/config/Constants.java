package uk.nhs.digital.gossmigrator.config;

/**
 * Internal non environment specific constants.
 */
public interface Constants {
    String OUTPUT_FILE_TYPE_SUFFIX = ".json";
    String DOC_TYPE_FILE = "DocumentTypeMapping.csv";
    String GENERAL_TYPE_FILE = "GeneralDocumentTypes.csv";
    String PUB_SERIES_FILE = "PublicationSeriesMapping.xlsx";
    String METADATA_FILE = "MetadataMapping.csv";
    String TAXONOMY_FILE = "TaxonomyMapping.csv";
    String NON_RELEVANT_IDS_FILE = "NonRelevantTemplateIDs.csv";

    interface Output{
        String ZIP_FILE_NAME = "import-package.zip";
        String JSON_DIR = "exim";
        String ASSET_DIR = "attachments";
    }
}
