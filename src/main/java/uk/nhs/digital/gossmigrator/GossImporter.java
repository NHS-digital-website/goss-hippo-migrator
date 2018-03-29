package uk.nhs.digital.gossmigrator;

import org.apache.commons.cli.*;
import org.apache.commons.csv.CSVParser;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.digital.gossmigrator.Report.ReportWriter;
import uk.nhs.digital.gossmigrator.config.Config;
import uk.nhs.digital.gossmigrator.misc.CSVReader;
import uk.nhs.digital.gossmigrator.misc.FolderHelper;
import uk.nhs.digital.gossmigrator.misc.LoopFinder;
import uk.nhs.digital.gossmigrator.model.goss.GossFile;
import uk.nhs.digital.gossmigrator.model.goss.GossProcessedData;
import uk.nhs.digital.gossmigrator.model.goss.enums.GossSourceFile;
import uk.nhs.digital.gossmigrator.model.mapping.ContentTypeMap;
import uk.nhs.digital.gossmigrator.model.mapping.MetadataMappingItems;
import uk.nhs.digital.gossmigrator.model.mapping.enums.MappingType;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import static uk.nhs.digital.gossmigrator.config.Config.*;
import static uk.nhs.digital.gossmigrator.model.goss.enums.GossSourceFile.CONTENT;
import static uk.nhs.digital.gossmigrator.model.goss.enums.GossSourceFile.DIGITAL;


public class GossImporter {
    private final static Logger LOGGER = LoggerFactory.getLogger(GossImporter.class);
    public static MetadataMappingItems metadataMapping = new MetadataMappingItems();

    public static GossProcessedData digitalData = new GossProcessedData(GossSourceFile.DIGITAL);
    public static GossProcessedData contentData = new GossProcessedData(CONTENT);
    public static HSSFWorkbook report = new HSSFWorkbook();
    public static HSSFWorkbook iframeReport = new HSSFWorkbook();
    private static boolean skipAssets = true;
    private ContentImporter contentImporter = new ContentImporter();
    public static boolean processingDigital;

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

            if(cmd.hasOption("skipAssets") || SKIP_DIGITAL){
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

        cleanOutputFolders();

        // Need an empty file in each content folder for publication external assets to work.
        FolderHelper.makeDummyDataBinFile(Config.LIVE_CONTENT_TARGET_FOLDER);
        FolderHelper.makeDummyDataBinFile(Config.NON_LIVE_CONTENT_TARGET_FOLDER);

        ReportWriter.generateReport();
        processingDigital = true;

        LOGGER.info("Mapping publications to series.");
        SeriesImporter seriesImporter = new SeriesImporter();
        digitalData.addSeriesContentList(seriesImporter.getSeriesContentList());
        digitalData.setPublicationSeriesMap(seriesImporter.getPublicationKeyToSeriesIdMap());

        LOGGER.info("Reading taxonomy.");
        TaxonomyMapper mapper = new TaxonomyMapper();
        digitalData.setTaxonomyMap(mapper.generateTaxonomyMap());

        LOGGER.info("Reading document types");
        DocumentTypeImporter typeImporter = new DocumentTypeImporter();

        digitalData.setContentTypeMap(typeImporter.populateContentTypes());
        digitalData.setGeneralDocumentTypeMap(typeImporter.populateGeneralContentTypes());
        digitalData.setIgnoredTemplateIdsList(typeImporter.populateIgnoredTemplateIds());

        LOGGER.info("Begin processing Digital.");
        processGoss(digitalData);
        processingDigital = false;
        LOGGER.info("Begin processing Content.");
        processGoss(contentData);
        processingDigital = true;
        LOGGER.info("Writing Digital.");
        writeImportables(digitalData);
        processingDigital = false;
        LOGGER.info("Writing Content.");
        writeImportables(contentData);

        processingDigital = true;

        // Assets need to be done after digital content as only import those referenced
        // in rich text in content.
        AssetImporter assetImporter = new AssetImporter();
        if(!skipAssets) {
            LOGGER.info("Copying S3 required files.");
            copyS3RequiredFiles();
            LOGGER.info("Writing Asset importables.");
            assetImporter.createAssetHippoImportables();
            int folders = assetImporter.writeHippoAssetImportables();
            for(int i = 0; i <= folders; i++){
                LOGGER.info("Zipping asset folder {}.", i);
                FolderHelper.zipFolder(Paths.get(ASSET_TARGET_FOLDER, Integer.toString(i)).toString());
            }
        }

        digitalData.getContentTypeMap().logNeverReferenced();
        LOGGER.info("Zipping live json.");
        FolderHelper.zipFolder(LIVE_CONTENT_TARGET_FOLDER);
        LOGGER.info("Zipping not live json.");
        FolderHelper.zipFolder(NON_LIVE_CONTENT_TARGET_FOLDER);
        LOGGER.info("Zipping folders json.");
        FolderHelper.zipFolder(FOLDERS_TARGET_FOLDER);
        LOGGER.info("Zipping content rewrites.");
        FolderHelper.zipFolder(URLREWRITE_CONTENT_TARGET_FOLDER);
        LOGGER.info("Zipping digital rewrites.");
        FolderHelper.zipFolder(URLREWRITE_DIGITAL_TARGET_FOLDER);
        LOGGER.info("Writing import report.");
        ReportWriter.writeFile();

    }

    private void copyS3RequiredFiles() {
        for(GossFile file : digitalData.getGossFileMap().values()){
            if(file.getS3references().size() > 0){
                try {
                    Files.createDirectories(Paths.get(Config.S3_TARGET_FOLDER, file.getRelativeFilePathWithoutFileName()));
                    Files.copy(Paths.get(file.getFilePathOnDisk()), Paths.get(Config.S3_TARGET_FOLDER, file.getRelativeFilePath()));
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
    }

    private void processGoss(GossProcessedData data){
        LOGGER.info("Reading goss export.");
        contentImporter.populateGossData(data);
        LOGGER.info("Generating Jcr structure.");
        data.getArticlesContentList().generateJcrStructure();

        HippoImportableFactory factory = new HippoImportableFactory();
        LOGGER.info("Creating Hippo importable objects.");
        data.setImportableContentItems(factory.populateHippoContent(data));

    }

    private void writeImportables(GossProcessedData data){
        data = LoopFinder.removeRedirectLoops(data);

        if((DIGITAL.equals(data.getType()) && !SKIP_DIGITAL)
                || (CONTENT.equals(data.getType()) && !SKIP_CONTENT)){
            boolean isDigital = false;
            if (DIGITAL.equals(data.getType())) {
                isDigital = true;
            }
            contentImporter.writeHippoContentImportables(data.getImportableContentItems(), isDigital);
        }
    }

    private void cleanOutputFolders() {
        LOGGER.info("Preparing output folders.");
        FolderHelper.cleanFolder(Paths.get(ASSET_TARGET_FOLDER));
        FolderHelper.cleanFolder(Paths.get(LIVE_CONTENT_TARGET_FOLDER));
        FolderHelper.cleanFolder(Paths.get(NON_LIVE_CONTENT_TARGET_FOLDER));
        FolderHelper.cleanFolder(Paths.get(FOLDERS_TARGET_FOLDER));
        FolderHelper.cleanFolder(Paths.get(URLREWRITE_CONTENT_TARGET_FOLDER));
        FolderHelper.cleanFolder(Paths.get(URLREWRITE_DIGITAL_TARGET_FOLDER));
        FolderHelper.cleanFolder(Paths.get(S3_TARGET_FOLDER));
    }
}