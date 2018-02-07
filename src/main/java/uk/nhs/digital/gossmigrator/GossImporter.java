package uk.nhs.digital.gossmigrator;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.digital.gossmigrator.config.Config;
import uk.nhs.digital.gossmigrator.config.TemplateConfig;
import uk.nhs.digital.gossmigrator.model.goss.GossContentList;
import uk.nhs.digital.gossmigrator.model.goss.GossProcessedData;
import uk.nhs.digital.gossmigrator.model.hippo.HippoImportable;
import uk.nhs.digital.gossmigrator.model.mapping.MetadataMappingItems;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.*;

public class GossImporter {
    private final static Logger LOGGER = LoggerFactory.getLogger(GossImporter.class);
    public static MetadataMappingItems metadataMapping = new MetadataMappingItems();

    private GossProcessedData gossData = new GossProcessedData();

    public static void main(String[] args) throws Exception {

        Options options = new Options();
        Option propertiesFileOption = new Option("p", "properties", true, "Properties file path.");
        Option templateFileOption = new Option("t", "templateProperties", true, "Template properties file path.");
        propertiesFileOption.setRequired(true);
        templateFileOption.setRequired(true);
        options.addOption(propertiesFileOption);
        options.addOption(templateFileOption);
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        try {

            CommandLine cmd = parser.parse(options, args);

            File propertiesFile = Paths.get(cmd.getOptionValue("properties")).toFile();
            LOGGER.info("Properties file:{}", propertiesFile);
            Properties properties = new Properties();
            properties.load(new FileReader(propertiesFile));
            Config.parsePropertiesFile(properties);

            File templateFile = Paths.get(cmd.getOptionValue("templateProperties")).toFile();
            LOGGER.info("Properties file:{}", templateFile);
            Properties templateProperties = new Properties();
            templateProperties.load(new FileReader(templateFile));
            TemplateConfig.parsePropertiesFile(templateProperties);

        } catch (MissingOptionException e) {
            formatter.printHelp("GossImporter", options);
            LOGGER.error(e.getMessage(), e);
            System.exit(1);
        }

        GossImporter importer = new GossImporter();
        importer.run();

    }

    public void run() {
        AssetImporter assetImporter = new AssetImporter();
        assetImporter.createAssetHippoImportables();
        assetImporter.writeHippoAssetImportables();

        SeriesImporter seriesImporter = new SeriesImporter();
        gossData.setSeriesContentList(seriesImporter.createPublicationSeries());
        gossData.setPublicationSeriesMap(seriesImporter.readPublicationSeriesMappings());

        TaxonomyMapper mapper = new TaxonomyMapper();
        gossData.setTaxonomyMap(mapper.generateTaxonomyMap());

        ContentImporter contentImporter = new ContentImporter();
        gossData.setArticlesContentList(contentImporter.createContentHippoImportables());
        gossData.setGossContentUrlMap(contentImporter.populateGossContentJcrStructure(gossData.getArticlesContentList()));

        HippoImportableFactory factory = new HippoImportableFactory();
        gossData.setImportableContentItems(factory.populateHippoContent(gossData));
        contentImporter.writeHippoContentImportables(gossData.getImportableContentItems());
    }

}
