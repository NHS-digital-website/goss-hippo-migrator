package uk.nhs.digital.gossmigrator;

import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.digital.gossmigrator.config.Config;
import uk.nhs.digital.gossmigrator.config.Constants;
import uk.nhs.digital.gossmigrator.model.goss.GossContent;
import uk.nhs.digital.gossmigrator.model.goss.GossContentList;
import uk.nhs.digital.gossmigrator.model.hippo.Asset;
import uk.nhs.digital.gossmigrator.model.hippo.HippoImportable;
import uk.nhs.digital.gossmigrator.model.hippo.Service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static uk.nhs.digital.gossmigrator.config.Config.*;
import static uk.nhs.digital.gossmigrator.config.Constants.OUTPUT_FILE_TYPE_SUFFIX;

public class GossImporter {
    private final static Logger LOGGER = LoggerFactory.getLogger(GossImporter.class);

    // TODO add a target path to HippoImportable and combine next 2
    List<HippoImportable> importableContentItems = new ArrayList<>();
    List<HippoImportable> importableAssetItems = new ArrayList<>();
    GossContentList gossContentList = new GossContentList();
    Map<Long, String> gossContentUrlMap = new HashMap<>();

    public static void main(String[] args) throws Exception {

        Options options = new Options();
        Option propertiesFileOption = new Option("p", "properties", true, "Properties file path.");
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
        } catch (MissingOptionException e) {
            formatter.printHelp("GossImporter", options);
            LOGGER.error(e.getMessage(), e);
            System.exit(1);
        }

        GossImporter importer = new GossImporter();
        importer.run();

    }

    public void run() {
        createAssetHippoImportables();
        writeHippoAssetImportables();
        createContentHippoImportables();
        writeHippoContentImportables();
    }

    private void createAssetHippoImportables() {
        // TODO Assets is WIP at the moment.  Leave it not plugged in...
        cleanFolder(Paths.get(ASSET_TARGET_FOLDER), OUTPUT_FILE_TYPE_SUFFIX);
    }

    private void createContentHippoImportables() {
        cleanFolder(Paths.get(CONTENT_TARGET_FOLDER), OUTPUT_FILE_TYPE_SUFFIX);
        JSONObject rootJsonObject = readGossExport();
        populateGossContent(rootJsonObject, null);
        populateGossContentJcrStructure();
        populateHippoContent();
    }

    private void populateGossContentJcrStructure() {
        gossContentList.generateJcrStructure();
        for (GossContent content : gossContentList) {
            gossContentUrlMap.put(content.getId(), content.getJcrPath() + content.getJcrNodeName());
        }
    }

    private void populateGossContent(JSONObject rootJsonObject, Long limit) {
        LOGGER.debug("Begin populating GossContent objects.");
        JSONArray jsonArray = (JSONArray) rootJsonObject.get("docs");

        long count = 0;
        for (Object childJsonObject : jsonArray) {
            if (null != limit && limit <= count) {
                break;
            }
            gossContentList.add(new GossContent((JSONObject) childJsonObject, ++count));
        }
    }

    private void createAssets() {
        try {
            Files.walk(Paths.get(ASSET_SOURCE_FOLDER)).forEach(this::createAsset);
        } catch (IOException e) {
            LOGGER.error("Failed reading Asset files.");
            throw new RuntimeException(e);
        }
    }

    private void createAsset(Path file) {
        Asset a = new Asset(file.getFileName().toString(), JCR_ASSET_ROOT + file.toString(), file);
        importableAssetItems.add(a);
    }

    private void writeHippoAssetImportables() {
        ImportableFileWriter writer = new ImportableFileWriter();
        writer.writeImportableFiles(importableAssetItems, Paths.get(ASSET_TARGET_FOLDER));
    }


    private void populateHippoContent() {
        LOGGER.debug("Begin populating hippo content from Goss content.");
        for (GossContent gossContent : gossContentList) {
            HippoImportable hippoContent = null;
            switch (gossContent.getContentType()) {
                case SERVICE:
                    hippoContent = new Service(gossContent);
                    break;
                default:
                    LOGGER.warn("Goss ID:{}, Unknown content type:{}", gossContent.getId(), gossContent.getContentType());
            }
            importableContentItems.add(hippoContent);
        }
    }

    private void writeHippoContentImportables() {
        LOGGER.debug("Begin writeHippoContentImportables");
        ImportableFileWriter writer = new ImportableFileWriter();
        writer.writeImportableFiles(importableContentItems, Paths.get(CONTENT_TARGET_FOLDER));
    }

    private JSONObject readGossExport() {
        LOGGER.info("Reading Goss content file:{}", Config.GOSS_CONTENT_SOURCE_FILE);

        File f = new File(Config.GOSS_CONTENT_SOURCE_FILE);
        if (!f.exists()) {
            LOGGER.error("File " + Config.GOSS_CONTENT_SOURCE_FILE + " does not exist.");
            throw new RuntimeException("File " + Config.GOSS_CONTENT_SOURCE_FILE + " does not exist.");
        }
        if (!f.isFile()) {
            LOGGER.error("Not a file :" + Config.GOSS_CONTENT_SOURCE_FILE);
            throw new RuntimeException("Not a file :" + Config.GOSS_CONTENT_SOURCE_FILE);
        }

        JSONParser jsonParser = new JSONParser();

        // Goss export comes as a JSON array with element per content.
        // To read all in One go wrap array in a single outer document.
        // Possible a bad idea and will need to do line by line later.
        String content = Constants.GOSS_EXTRACT_PREFIX;

        try {
            for (String line : Files.readAllLines(Paths.get(Config.GOSS_CONTENT_SOURCE_FILE))) {
                content = content + line;
            }
        } catch (IOException e) {
            LOGGER.error("Failed reading Goss Content JSON File.", e);
            throw new RuntimeException(e.getMessage(), e);
        }

        content = content + Constants.GOSS_EXTRACT_SUFFIX;
        try {
            return (JSONObject) jsonParser.parse(content);
        } catch (ParseException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException("Failed Goss JSON parsing", e);
        }
    }

    /**
     * Remove .json files from folder or create folder if not exists.
     * If non json files in folder log warning.
     * Does not delete recursively.
     * Create Directory if not exists.
     */
    private void cleanFolder(Path folder, String fileExtension) {
        File f = folder.toFile();
        // Check exists.
        if (f.exists()) {
            // Check is folder.
            if (f.isFile()) {
                LOGGER.error("Expected {} to be a directory not a file.", f);
            } else {
                for(File toDelete : f.listFiles()){
                    if(toDelete.getName().endsWith(fileExtension)){
                        toDelete.delete();
                    }
                }
            }
        }
    }
}
