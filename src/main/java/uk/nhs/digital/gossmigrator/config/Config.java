package uk.nhs.digital.gossmigrator.config;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.Properties;

import static uk.nhs.digital.gossmigrator.config.Config.PropertiesEnum.*;
import static uk.nhs.digital.gossmigrator.config.Constants.*;

/**
 * Config from properties file.
 */
public class Config {
    private final static Logger LOGGER = LoggerFactory.getLogger(Config.class);


    enum PropertiesEnum {
        JCR_ASSET_ROOT_PROP("jcr.asset.root", "Root jcr path to assets. e.g. /content/assets/", false, "/content/assets/goss-legacy/"),
        JCR_SERVICE_DOC_ROOT_PROP("jcr.service.doc.root", "Root jcr path to services. e.g. /content/documents/corporate-website/service/", false, "/content/documents/corporate-website/systems-and-services/"),
        ASSET_SOURCE_FOLDER_PROP("assets.source.folder", "File system folder holding assets to process.", true, ""),
        GOSS_CONTENT_SOURCE_FILE_PROP("goss.content.source.file", "Path including filename to Goss export. e.g. /home/xyz/goss1.json", true, ""),
        TARGET_FOLDER_PROP("target.folder", "File system folder to hold created content json hippo import files.", true, ""),
        REDIRECT_CONTENT_SOURCE_FILE_PROP("redirect.content.source.file", "Path including filename to Goss export. e.g. /home/xyz/goss1.json", true, ""),
        JCR_PUBLICATION_ROOT_PROP("jcr.stats.pubs.doc.root", "Root jcr path for statistical publications. e.g. /content/documents/corporate-website/", false, "/content/documents/corporate-website/publication-system/statistical/"),
        SPLIT_ASSET_PATH_ON("split.asset.path.on", "For file nodes in goss export there is a path.  " +
                "Need to match identify which part of the path maps to the folder on local disk holding the assets.", false, "live-media"),
        JCR_GALLERY_ROOT_PROP("jcr.media.doc.root", "Where to put images.", false, "/content/gallery/goss-legacy/"),
        IGNORE_MEDIA_WITH_PATH_PART_PROP("ignore.assets.with.path.containing", "If media path contains this ignore it.", false, "pre-prod-media"),
        CONFIG_FOLDER_PROP("config.folder", "Folder containing mamppings and properties files", true,""),
        JCR_GENERAL_ROOT_PROP("jcr.general.root", "JCR path to general root.", false, "/content/documents/corporate-website/"),
        MAX_ASSETS_SIZE_PER_ZIP_MB_PROP("max.assets.size.per.zip", "Max size in Mb.", false, "1024"),
        JCR_DIRECT_ROOT_PROP("jcr.redirect.root", "JCR path to redirect root.", false, "/content/urlrewriter/rules/"),
        SKIP_DIGITAL_PROP("skip.digital","If digital export should be skipped.",false,"false"),
        SKIP_CONTENT_PROP("skip.content","If content export should be skipped.",false,"false")
        ;

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

        static boolean hasKey(String key){
            for(PropertiesEnum property : PropertiesEnum.values()){
                if(property.key.equals(key)){
                    return true;
                }
            }
            return false;
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

    private static String TARGET_FOLDER_ROOT;
    public static String JCR_ASSET_ROOT;
    public static String JCR_SERVICE_DOC_ROOT;
    public static String JCR_GALLERY_ROOT;
    public static String JCR_GENERAL_ROOT;
    public static String ASSET_SOURCE_FOLDER;
    public static String ASSET_TARGET_FOLDER;
    public static String GOSS_CONTENT_SOURCE_FILE;
    public static String REDIRECT_CONTENT_SOURCE_FILE;
    public static String CONTENT_TARGET_FOLDER;
    public static String LIVE_CONTENT_TARGET_FOLDER;
    public static String NON_LIVE_CONTENT_TARGET_FOLDER;
    public static String JCR_PUBLICATION_ROOT;
    public static String JCR_REDIRECT_ROOT;
    public static String METADATA_MAPPING_FILE;
    public static String ASSET_SOURCE_FOLDER_IN_GOSS_EXPORT;
    public static String TAXONOMY_MAPPING_FILE;
    public static String IGNORE_MEDIA_WITH_PATH_PART;
    public static String DOCUMENT_TYPE_MAPPING_FILE;
    public static String GOSS_HIPPO_MAPPING_FILE;
    public static String GENERAL_TYPE_MAPPING_FILE;
    public static String NON_RELEVANT_TEMPLATE_IDS_FILE;
    public static long MAX_ASSET_SIZE_MB_IN_ZIP;
    public static Boolean SKIP_DIGITAL;
    public static Boolean SKIP_CONTENT;

    public static void parsePropertiesFile(Properties propertiesMap) {
        LOGGER.info("Properties used:");
        String CONFIG_FOLDER = getConfig(CONFIG_FOLDER_PROP, propertiesMap);
        JCR_ASSET_ROOT = getConfig(JCR_ASSET_ROOT_PROP, propertiesMap);
        JCR_SERVICE_DOC_ROOT = getConfig(JCR_SERVICE_DOC_ROOT_PROP, propertiesMap);
        ASSET_SOURCE_FOLDER = getConfig(ASSET_SOURCE_FOLDER_PROP, propertiesMap);
        GOSS_CONTENT_SOURCE_FILE = getConfig(GOSS_CONTENT_SOURCE_FILE_PROP, propertiesMap);
        TARGET_FOLDER_ROOT = getConfig(TARGET_FOLDER_PROP, propertiesMap);
        ASSET_TARGET_FOLDER = Paths.get(TARGET_FOLDER_ROOT, "assets").toString();
        CONTENT_TARGET_FOLDER = Paths.get(TARGET_FOLDER_ROOT, "content").toString();
        LIVE_CONTENT_TARGET_FOLDER = Paths.get(CONTENT_TARGET_FOLDER,"live").toString();
        NON_LIVE_CONTENT_TARGET_FOLDER = Paths.get(CONTENT_TARGET_FOLDER, "notLive").toString();
        REDIRECT_CONTENT_SOURCE_FILE = getConfig(REDIRECT_CONTENT_SOURCE_FILE_PROP, propertiesMap);
        JCR_PUBLICATION_ROOT = getConfig(JCR_PUBLICATION_ROOT_PROP, propertiesMap);
        ASSET_SOURCE_FOLDER_IN_GOSS_EXPORT = getConfig(SPLIT_ASSET_PATH_ON, propertiesMap);
        JCR_GALLERY_ROOT = getConfig(JCR_GALLERY_ROOT_PROP, propertiesMap);
        IGNORE_MEDIA_WITH_PATH_PART = getConfig(IGNORE_MEDIA_WITH_PATH_PART_PROP, propertiesMap);
        GOSS_HIPPO_MAPPING_FILE = CONFIG_FOLDER.concat(PUB_SERIES_FILE);
        METADATA_MAPPING_FILE =  CONFIG_FOLDER.concat(METADATA_FILE);
        DOCUMENT_TYPE_MAPPING_FILE =  CONFIG_FOLDER.concat(DOC_TYPE_FILE);
        TAXONOMY_MAPPING_FILE =  CONFIG_FOLDER.concat(TAXONOMY_FILE);
        GENERAL_TYPE_MAPPING_FILE = CONFIG_FOLDER.concat(GENERAL_TYPE_FILE);
        NON_RELEVANT_TEMPLATE_IDS_FILE = CONFIG_FOLDER.concat(NON_RELEVANT_IDS_FILE);
        JCR_GENERAL_ROOT = getConfig(JCR_GENERAL_ROOT_PROP, propertiesMap);
        String maxSize = getConfig(MAX_ASSETS_SIZE_PER_ZIP_MB_PROP, propertiesMap);
        MAX_ASSET_SIZE_MB_IN_ZIP = Long.valueOf(maxSize);
        JCR_REDIRECT_ROOT = getConfig(JCR_DIRECT_ROOT_PROP, propertiesMap);
        SKIP_DIGITAL = Boolean.parseBoolean(getConfig(SKIP_DIGITAL_PROP, propertiesMap));
        SKIP_CONTENT = Boolean.parseBoolean(getConfig(SKIP_CONTENT_PROP, propertiesMap));

        // Check all properties in file are expected
        for(String property : propertiesMap.stringPropertyNames()){
            if(!PropertiesEnum.hasKey(property)){
                LOGGER.warn("Unexpected property:{}.  This is unused and can be safely deleted from properties file.", property);
            }
        }
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
