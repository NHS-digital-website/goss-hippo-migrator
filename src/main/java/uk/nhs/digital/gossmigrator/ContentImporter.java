package uk.nhs.digital.gossmigrator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.digital.gossmigrator.config.Config;
import uk.nhs.digital.gossmigrator.misc.FolderHelper;
import uk.nhs.digital.gossmigrator.misc.GossContentFilter;
import uk.nhs.digital.gossmigrator.model.goss.*;
import uk.nhs.digital.gossmigrator.model.hippo.HippoImportable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static uk.nhs.digital.gossmigrator.config.Config.CONTENT_TARGET_FOLDER;
import static uk.nhs.digital.gossmigrator.config.Config.LIVE_CONTENT_TARGET_FOLDER;
import static uk.nhs.digital.gossmigrator.config.Config.NON_LIVE_CONTENT_TARGET_FOLDER;
import static uk.nhs.digital.gossmigrator.config.Constants.OUTPUT_FILE_TYPE_SUFFIX;

public class ContentImporter {

    private final static Logger LOGGER = LoggerFactory.getLogger(ContentImporter.class);

    public void populateGossData(GossProcessedData gossData){
        FolderHelper.cleanFolder(Paths.get(CONTENT_TARGET_FOLDER), OUTPUT_FILE_TYPE_SUFFIX);
        FolderHelper.cleanFolder(Paths.get(LIVE_CONTENT_TARGET_FOLDER), OUTPUT_FILE_TYPE_SUFFIX);
        FolderHelper.cleanFolder(Paths.get(NON_LIVE_CONTENT_TARGET_FOLDER), OUTPUT_FILE_TYPE_SUFFIX);
        JSONObject rootJsonObject = readGossExport();
        gossData.setArticlesContentList(populateGossContent(rootJsonObject));
        gossData.setGossLinkMap(populateGossLinks(rootJsonObject));
        gossData.setGossFileMap(populateGossFiles(rootJsonObject));
    }

    private Map<Long, GossFile> populateGossFiles(JSONObject rootJsonObject) {
        Map<Long, GossFile> gossFileMap = new HashMap<>();
        LOGGER.debug("Begin populating GossLink objects.");
        JSONArray jsonArray = (JSONArray) rootJsonObject.get("media");
        if(null != jsonArray) {
            for (Object childJsonObject : jsonArray) {
                GossFile file = new GossFile((JSONObject) childJsonObject);
                if(!file.isNotLiveLink()) {
                    gossFileMap.put(file.getId(), file);
                }
            }
        }else{
            LOGGER.error("Could not read 'media' node.");
        }
        return gossFileMap;
    }

    private Map<Long, GossLink> populateGossLinks(JSONObject rootJsonObject) {
        Map<Long, GossLink> gossLinkMap = new HashMap<>();
        LOGGER.debug("Begin populating GossLink objects.");
        JSONArray jsonArray = (JSONArray) rootJsonObject.get("links");
        if(null != jsonArray) {
            for (Object childJsonObject : jsonArray) {
                GossLink link = new GossLink((JSONObject) childJsonObject);
                gossLinkMap.put(link.getId(), link);
            }
        }else{
            LOGGER.error("Could not read 'links' node.");
        }
        return gossLinkMap;
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

        StringBuilder content = new StringBuilder();
        try {
            for (String line : Files.readAllLines(Paths.get(Config.GOSS_CONTENT_SOURCE_FILE))) {
                content.append(line);
            }
        } catch (IOException e) {
            LOGGER.error("Failed reading Goss Content JSON File.", e);
            throw new RuntimeException(e.getMessage(), e);
        }


        try {

            return (JSONObject) jsonParser.parse(content.toString());

        } catch (ParseException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException("Failed Goss JSON parsing", e);
        }
    }

    private GossContentList populateGossContent(JSONObject rootJsonObject) {
        LOGGER.debug("Begin populating GossContent objects.");
        JSONArray jsonArray = (JSONArray) rootJsonObject.get("articles");
        GossContentList gossContentList = new GossContentList();
        long count = 0;
        for (Object childJsonObject : jsonArray) {
            gossContentList.add(GossContentFactory.generateGossContent((JSONObject) childJsonObject, ++count));
        }
        return GossContentFilter.setRelevantGossContentFlag(gossContentList);
    }

    public void writeHippoContentImportables(List<HippoImportable> importableContentItems) {
        LOGGER.debug("Begin writeHippoContentImportables");
        ImportableFileWriter writer = new ImportableFileWriter();
        writer.writeImportableFiles(importableContentItems, Config.CONTENT_TARGET_FOLDER);
    }

}
