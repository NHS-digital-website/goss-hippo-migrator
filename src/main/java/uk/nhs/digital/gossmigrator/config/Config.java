package uk.nhs.digital.gossmigrator.config;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

import static uk.nhs.digital.gossmigrator.config.Config.PropertiesEnum.*;

/**
 * Config from properties file.
 */
public class Config {
    private final static Logger LOGGER = LoggerFactory.getLogger(Config.class);


    enum PropertiesEnum {
        JCR_ASSET_ROOT_PROP("jcr.asset.root", "Root jcr path to assets. e.g. /content/assets/", false, "/content/assets/"),
        JCR_SERVICE_DOC_ROOT_PROP("jcr.service.doc.root", "Root jcr path to services. e.g. /content/documents/corporate-website/service/", false, "/content/documents/corporate-website/service/"),
        ASSET_SOURCE_FOLDER_PROP("assets.source.folder", "File system folder holding assets to process.", true, ""),
        ASSET_TARGET_FOLDER_PROP("assets.target.folder", "File system folder to hold created asset json hippo import files.", true, ""),
        GOSS_CONTENT_SOURCE_FILE_PROP("goss.content.source.file", "Path including filename to Goss export. e.g. /home/xyz/goss1.json", true, ""),
        CONTENT_TARGET_FOLDER_PROP("content.target.folder", "File system folder to hold created content json hippo import files.", true, ""),
        JCR_PUBLICATION_ROOT_PROP("jcr.stats.pubs.doc.root", "Root jcr path for statistical publications. e.g. /content/statpubs/", true, ""),
        SERIES_PUBLICATION_MAPPING_FILE_PROP("goss.series.publication.mapping.file", "File system path to Series Publication mapping csv.", true, ""),
        SERIES_PUBLICATION_MAPPING_FILE_HEADER_COUNT_PROP("goss.series.publication.mapping.file.header.count", "Count of header lines to ignore in Series Publication mapping csv file.", false, "1"),
        SERIES_FILE_PROP("goss.series.file", "Location of Series definition file.", true, ""),
        SERIES_FILE_HEADER_COUNT_PROP("goss.series.file.header.count", "Number of header lines to skip in series file.", false, "1"),
        METADATA_MAPPING_FILE_PROP("meta.data.mapping.file", "File holding metadata mappings.", true, ""),
        SPLIT_ASSET_PATH_ON("split.asset.path.on", "For file nodes in goss export there is a path.  " +
                "Need to match identify which part of the path maps to the folder on local disk holding the assets.", false, "live-media"),
        TAXONOMY_MAPPING_FILE_PROP("goss.taxonomy.mapping.file", "File system path to Taxonomy mapping csv.", true, ""),
        JCR_GALLERY_ROOT_PROP("jcr.media.doc.root", "Where to put images.", false, "/content/gallery/publicationsystem/"),
        IGNORE_MEDIA_WITH_PATH_PART_PROP("ignore.assets.with.path.containing", "If media path contains this ignore it.", false, "pre-prod-media");

        final String key;
        final String help;
        final boolean isMandatory;
        final String defaultValue;

        PropertiesEnum(String key, String help, boolean isMandatory, String defaultValue) {
            this.key = key;
            this.help = help;
            this.isMandatory = isMandatory;
            this.defaultValue = defaultValue;
        }

        @Override
        public String toString() {
            return "Property{" +
                    "key='" + key + '\'' +
                    ", help='" + help + '\'' +
                    ", isMandatory=" + isMandatory +
                    ", defaultValue='" + defaultValue + '\'' +
                    '}';
        }
    }

    public static String JCR_ASSET_ROOT;
    public static String JCR_SERVICE_DOC_ROOT;
    public static String JCR_GALLERY_ROOT;
    public static String ASSET_SOURCE_FOLDER;
    public static String ASSET_TARGET_FOLDER;
    public static String GOSS_CONTENT_SOURCE_FILE;
    public static String CONTENT_TARGET_FOLDER;
    public static String JCR_PUBLICATION_ROOT;
    public static String SERIES_PUBLICATION_MAPPING_FILE;
    public static Long SERIES_PUBLICATION_MAPPING_FILE_HEADER_COUNT;
    public static String SERIES_FILE;
    public static Long SERIES_FILE_HEADER_COUNT;
    public static String METADATA_MAPPING_FILE;
    public static String ASSET_SOURCE_FOLDER_IN_GOSS_EXPORT;
    public static String TAXONOMY_MAPPING_FILE;
    public static String IGNORE_MEDIA_WITH_PATH_PART;

    public static void parsePropertiesFile(Properties propertiesMap) {
        LOGGER.info("Properties used:");
        JCR_ASSET_ROOT = getConfig(JCR_ASSET_ROOT_PROP, propertiesMap);
        JCR_SERVICE_DOC_ROOT = getConfig(JCR_SERVICE_DOC_ROOT_PROP, propertiesMap);
        ASSET_SOURCE_FOLDER = getConfig(ASSET_SOURCE_FOLDER_PROP, propertiesMap);
        ASSET_TARGET_FOLDER = getConfig(ASSET_TARGET_FOLDER_PROP, propertiesMap);
        GOSS_CONTENT_SOURCE_FILE = getConfig(GOSS_CONTENT_SOURCE_FILE_PROP, propertiesMap);
        CONTENT_TARGET_FOLDER = getConfig(CONTENT_TARGET_FOLDER_PROP, propertiesMap);
        JCR_PUBLICATION_ROOT = getConfig(JCR_PUBLICATION_ROOT_PROP, propertiesMap);
        SERIES_PUBLICATION_MAPPING_FILE = getConfig(SERIES_PUBLICATION_MAPPING_FILE_PROP, propertiesMap);
        SERIES_PUBLICATION_MAPPING_FILE_HEADER_COUNT = Long.valueOf(getConfig(SERIES_PUBLICATION_MAPPING_FILE_HEADER_COUNT_PROP, propertiesMap));
        SERIES_FILE = getConfig(SERIES_FILE_PROP, propertiesMap);
        SERIES_FILE_HEADER_COUNT = Long.valueOf(getConfig(SERIES_FILE_HEADER_COUNT_PROP, propertiesMap));
        METADATA_MAPPING_FILE = getConfig(METADATA_MAPPING_FILE_PROP, propertiesMap);
        ASSET_SOURCE_FOLDER_IN_GOSS_EXPORT = getConfig(SPLIT_ASSET_PATH_ON, propertiesMap);
        TAXONOMY_MAPPING_FILE = getConfig(TAXONOMY_MAPPING_FILE_PROP, propertiesMap);
        JCR_GALLERY_ROOT = getConfig(JCR_GALLERY_ROOT_PROP, propertiesMap);
        IGNORE_MEDIA_WITH_PATH_PART = getConfig(IGNORE_MEDIA_WITH_PATH_PART_PROP, propertiesMap);
    }

    private static String getConfig(PropertiesEnum propertiesEnum, Properties propertiesMap) {
        String propertyValue = propertiesMap.getProperty(propertiesEnum.key);
        if (propertiesEnum.isMandatory && StringUtils.isEmpty(propertyValue)) {
            printPropertiesHelp();
            throw new RuntimeException("Properties file must contain:" + propertiesEnum.key);
        } else if (!propertiesEnum.isMandatory && StringUtils.isEmpty(propertyValue)) {
            propertyValue = propertiesEnum.defaultValue;
        }
        LOGGER.info("{}: {}", propertiesEnum.key, propertyValue);
        return propertyValue;
    }

    private static void printPropertiesHelp() {
        for (PropertiesEnum property : PropertiesEnum.values()) {
            System.out.println(property.toString());
        }
    }
}
