package uk.nhs.digital.gossmigrator;

import org.apache.commons.cli.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.digital.gossmigrator.Report.ReportWriter;
import uk.nhs.digital.gossmigrator.config.Config;
import uk.nhs.digital.gossmigrator.model.goss.GossProcessedData;
import uk.nhs.digital.gossmigrator.model.mapping.MetadataMappingItems;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.Properties;

public class GossImporter {
    private final static Logger LOGGER = LoggerFactory.getLogger(GossImporter.class);
    public static MetadataMappingItems metadataMapping = new MetadataMappingItems();

    public static GossProcessedData gossData = new GossProcessedData();
    public static HSSFWorkbook report = new HSSFWorkbook();
    private static boolean skipAssets = false;

    public static void main(String[] args) throws Exception {

        Options options = new Options();
        Option propertiesFileOption = new Option("p", "properties", true, "Properties file path.");
        options.addOption(Option.builder().longOpt("skipAssets").hasArg(false).required(false)
                .desc("Set to suppress processing assets.  Only useful in dev.").build());
        propertiesFileOption.setRequired(true);
        options.addOption(propertiesFileOption);
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        try {

            CommandLine cmd = parser.parse(options, args);

            File propertiesFile = Paths.get(cmd.getOptionValue("properties")).toFile();
            LOGGER.info("Properties file:{}", propertiesFile);
            Properties properties = new Properties();
            properties.load(new FileReader(propertiesFile));
            Config.parsePropertiesFile(properties);

            if(cmd.hasOption("skipAssets")){
                skipAssets = true;
            }

        } catch (MissingOptionException e) {
            formatter.printHelp("GossImporter", options);
            LOGGER.error(e.getMessage(), e);
            System.exit(1);
        }

        GossImporter importer = new GossImporter();
        importer.run();

    }

    private void run() {

        ReportWriter.generateReport();

        if(!skipAssets) {
            AssetImporter assetImporter = new AssetImporter();
            assetImporter.createAssetHippoImportables();
            assetImporter.writeHippoAssetImportables();
        }

        SeriesImporter seriesImporter = new SeriesImporter();
        gossData.setSeriesContentList(seriesImporter.getSeriesContentList());
        gossData.setPublicationSeriesMap(seriesImporter.getPublicationKeyToSeriesIdMap());

        TaxonomyMapper mapper = new TaxonomyMapper();
        gossData.setTaxonomyMap(mapper.generateTaxonomyMap());

        DocumentTypeImporter typeImporter = new DocumentTypeImporter();
        gossData.setContentTypeMap(typeImporter.populateContentTypes());
        gossData.setGeneralDocumentTypeMap(typeImporter.populateGeneralContentTypes());

        ContentImporter contentImporter = new ContentImporter();
        contentImporter.populateGossData(gossData);

        HippoImportableFactory factory = new HippoImportableFactory();
        gossData.setImportableContentItems(factory.populateHippoContent(gossData));
        contentImporter.writeHippoContentImportables(gossData.getImportableContentItems());

        ReportWriter.writeFile();
    }

}
