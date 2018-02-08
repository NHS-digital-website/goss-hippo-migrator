package uk.nhs.digital.gossmigrator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.nhs.digital.gossmigrator.config.Config;
import uk.nhs.digital.gossmigrator.misc.FolderHelper;
import uk.nhs.digital.gossmigrator.model.goss.GossContent;
import uk.nhs.digital.gossmigrator.model.goss.GossContentFactory;
import uk.nhs.digital.gossmigrator.model.goss.GossContentList;
import uk.nhs.digital.gossmigrator.model.hippo.HippoImportable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static uk.nhs.digital.gossmigrator.config.Config.CONTENT_TARGET_FOLDER;
import static uk.nhs.digital.gossmigrator.config.Constants.OUTPUT_FILE_TYPE_SUFFIX;

public class ContentImporter {

    private final static Logger LOGGER = LoggerFactory.getLogger(ContentImporter.class);

    public GossContentList createContentHippoImportables() {
        FolderHelper.cleanFolder(Paths.get(CONTENT_TARGET_FOLDER), OUTPUT_FILE_TYPE_SUFFIX);
        JSONObject rootJsonObject = readGossExport();
        GossContentList gossContentList = populateGossContent(rootJsonObject, null);

        return gossContentList;
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

        String content = "";
        try {
            for (String line : Files.readAllLines(Paths.get(Config.GOSS_CONTENT_SOURCE_FILE))) {
                content = content + line;
            }
        } catch (IOException e) {
            LOGGER.error("Failed reading Goss Content JSON File.", e);
            throw new RuntimeException(e.getMessage(), e);
        }


        try {

            return (JSONObject)jsonParser.parse(content);

        } catch (ParseException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException("Failed Goss JSON parsing", e);
        }
    }

    private GossContentList populateGossContent(JSONObject rootJsonObject, Long limit) {
        LOGGER.debug("Begin populating GossContent objects.");
        JSONArray jsonArray = (JSONArray) rootJsonObject.get("articles");
        GossContentList gossContentList = new GossContentList();
        long count = 0;
        for (Object childJsonObject : jsonArray) {
            if (null != limit && limit <= count) {
                break;
            }
            gossContentList.add(GossContentFactory.generateGossContent((JSONObject) childJsonObject, ++count));
        }
        return gossContentList;
    }

    public Map<Long, String> populateGossContentJcrStructure(GossContentList gossContentList) {
        gossContentList.generateJcrStructure();
        Map<Long, String> gossContentUrlMap = new HashMap<Long, String>();
        for (GossContent content : gossContentList) {
            gossContentUrlMap.put(content.getId(), content.getJcrPath() + content.getJcrNodeName());
        }
        return gossContentUrlMap;
    }

    public void writeHippoContentImportables(List<HippoImportable> importableContentItems) {
        LOGGER.debug("Begin writeHippoContentImportables");
        ImportableFileWriter writer = new ImportableFileWriter();
        writer.writeImportableFiles(importableContentItems, Paths.get(CONTENT_TARGET_FOLDER));
    }

}
