package uk.nhs.digital.gossmigrator.config;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

import static uk.nhs.digital.gossmigrator.config.TemplateConfig.TemplateEnum.GENERAL_TEMPLATE;
import static uk.nhs.digital.gossmigrator.config.TemplateConfig.TemplateEnum.PUBLICATION_TEMPLATE;

public class TemplateConfig {

    private final static Logger LOGGER = LoggerFactory.getLogger(Config.class);

    enum TemplateEnum {
        PUBLICATION_TEMPLATE("publication.id"),
        GENERAL_TEMPLATE("general.id");

        private String key;

        TemplateEnum(String key) {
            this.key = key;
        }

    }

    public static Long PUBLICATION_ID;
    public static Long GENERAL_ID;

    public static void parsePropertiesFile(Properties propertiesMap){
        PUBLICATION_ID = getConfig(PUBLICATION_TEMPLATE, propertiesMap);
        GENERAL_ID = getConfig(GENERAL_TEMPLATE, propertiesMap);
    }

    private static Long getConfig(TemplateEnum templateEnum, Properties propertiesMap) {
        Long id;
        String propertyValue = propertiesMap.getProperty(templateEnum.key);
        if(StringUtils.isEmpty(propertyValue)){
            throw new RuntimeException("Properties file must contain:" + templateEnum.key);
        }
        try{
            id = Long.parseLong(propertyValue);
        }catch(NumberFormatException e){
            throw new RuntimeException("Invalid format. Property must contain an integer:" + templateEnum.key);
        }
        LOGGER.info("{}: {}", templateEnum.key, id);
        return id;
    }

}
