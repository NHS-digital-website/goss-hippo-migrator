package uk.nhs.digital.gossmigrator;

import static uk.nhs.digital.gossmigrator.config.Config.ASSET_TARGET_FOLDER;
import static uk.nhs.digital.gossmigrator.config.Config.LIVE_CONTENT_TARGET_FOLDER;
import static uk.nhs.digital.gossmigrator.config.Config.NON_LIVE_CONTENT_TARGET_FOLDER;

import org.apache.commons.cli.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.digital.gossmigrator.Report.ReportWriter;
import uk.nhs.digital.gossmigrator.config.Config;
import uk.nhs.digital.gossmigrator.misc.FolderHelper;
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

        SeriesImporter seriesImporter = new SeriesImporter();
        gossData.addSeriesContentList(seriesImporter.getSeriesContentList());
        gossData.setPublicationSeriesMap(seriesImporter.getPublicationKeyToSeriesIdMap());

        TaxonomyMapper mapper = new TaxonomyMapper();
        gossData.setTaxonomyMap(mapper.generateTaxonomyMap());

        DocumentTypeImporter typeImporter = new DocumentTypeImporter();
        gossData.setContentTypeMap(typeImporter.populateContentTypes());
        gossData.setGeneralDocumentTypeMap(typeImporter.populateGeneralContentTypes());
        gossData.setIgnoredTemplateIdsList(typeImporter.populateIgnoredTemplateIds());

        ContentImporter contentImporter = new ContentImporter();
        contentImporter.populateGossData(gossData);
        gossData.getArticlesContentList().generateJcrStructure();

        HippoImportableFactory factory = new HippoImportableFactory();
        gossData.setImportableContentItems(factory.populateHippoContent(gossData));
        contentImporter.writeHippoContentImportables(gossData.getImportableContentItems());

        // Assets need to be done after content as only import those referenced
        // in rich text in content.
        AssetImporter assetImporter = new AssetImporter();
        if(!skipAssets) {
            assetImporter.createAssetHippoImportables();
            int folders = assetImporter.writeHippoAssetImportables();
            for(int i = 0; i <= folders; i++){
                FolderHelper.zipFolder(Paths.get(ASSET_TARGET_FOLDER, Integer.toString(i)).toString());
            }
        }

        gossData.getContentTypeMap().logNeverReferenced();
        FolderHelper.zipFolder(LIVE_CONTENT_TARGET_FOLDER);
        FolderHelper.zipFolder(NON_LIVE_CONTENT_TARGET_FOLDER);

        ReportWriter.writeFile();
    }

}
