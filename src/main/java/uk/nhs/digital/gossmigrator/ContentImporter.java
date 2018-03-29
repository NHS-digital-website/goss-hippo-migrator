package uk.nhs.digital.gossmigrator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.digital.gossmigrator.config.Config;
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

import static uk.nhs.digital.gossmigrator.model.goss.enums.GossSourceFile.CONTENT;
import static uk.nhs.digital.gossmigrator.model.goss.enums.GossSourceFile.DIGITAL;

public class ContentImporter {

    private final static Logger LOGGER = LoggerFactory.getLogger(ContentImporter.class);

    public void populateGossData(GossProcessedData gossData) {
        if (DIGITAL.equals(gossData.getType())) {
            JSONObject rootJsonObject = readGossExport(Config.GOSS_CONTENT_SOURCE_FILE);
            LOGGER.info("Reading articles.");
            gossData.setArticlesContentList(populateGossContent(gossData, rootJsonObject));
            LOGGER.info("Reading links.");
            gossData.setGossLinkMap(populateGossLinks(rootJsonObject));
            LOGGER.info("Reading files.");
            gossData.setGossFileMap(populateGossFiles(rootJsonObject));
        } else if (CONTENT.equals(gossData.getType())) {
            JSONObject rootJsonObject = readGossExport(Config.REDIRECT_CONTENT_SOURCE_FILE);
            gossData.setArticlesContentList(populateGossContent(gossData, rootJsonObject));
        }
    }

    private Map<Long, GossFile> populateGossFiles(JSONObject rootJsonObject) {
        Map<Long, GossFile> gossFileMap = new HashMap<>();
        LOGGER.debug("Begin populating GossLink objects.");
        JSONArray jsonArray = (JSONArray) rootJsonObject.get("media");
        if (null != jsonArray) {
            for (Object childJsonObject : jsonArray) {
                GossFile file = new GossFile((JSONObject) childJsonObject);
                if (!file.isNotLiveLink()) {
                    gossFileMap.put(file.getId(), file);
                }
            }
        } else {
            LOGGER.error("Could not read 'media' node.");
        }
        return gossFileMap;
    }

    private Map<Long, GossLink> populateGossLinks(JSONObject rootJsonObject) {
        Map<Long, GossLink> gossLinkMap = new HashMap<>();
        LOGGER.debug("Begin populating GossLink objects.");
        JSONArray jsonArray = (JSONArray) rootJsonObject.get("links");
        if (null != jsonArray) {
            for (Object childJsonObject : jsonArray) {
                GossLink link = new GossLink((JSONObject) childJsonObject);
                gossLinkMap.put(link.getId(), link);
            }
        } else {
            LOGGER.error("Could not read 'links' node.");
        }
        return gossLinkMap;
    }

    private JSONObject readGossExport(String sourceFile) {
        LOGGER.info("Reading Goss content file:{}", sourceFile);

        File f = new File(sourceFile);
        if (!f.exists()) {
            LOGGER.error("File " + sourceFile + " does not exist.");
            throw new RuntimeException("File " + sourceFile + " does not exist.");
        }
        if (!f.isFile()) {
            LOGGER.error("Not a file :" + sourceFile);
            throw new RuntimeException("Not a file :" + sourceFile);
        }

        JSONParser jsonParser = new JSONParser();

        // Goss export comes as a JSON array with element per content.
        // To read all in One go wrap array in a single outer document.
        // Possible a bad idea and will need to do line by line later.

        StringBuilder content = new StringBuilder();
        try {
            for (String line : Files.readAllLines(Paths.get(sourceFile))) {
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

    private GossContentList populateGossContent(GossProcessedData data, JSONObject rootJsonObject) {
        LOGGER.debug("Begin populating GossContent objects.");
        JSONArray jsonArray = (JSONArray) rootJsonObject.get("articles");
        GossContentList gossContentList = new GossContentList();
        long count = 0;
        for (Object childJsonObject : jsonArray) {
            GossContent content = GossContentFactory.generateGossContent(data, (JSONObject) childJsonObject, ++count);
            if (content != null) {
                gossContentList.add(content);
            }
        }
        return GossContentFilter.setRelevantGossContentFlag(data, gossContentList);
    }


    public void writeHippoContentImportables(List<HippoImportable> importableContentItems, boolean isDigital) {
        LOGGER.debug("Begin writeHippoContentImportables");
        ImportableFileWriter writer = new ImportableFileWriter();
        writer.writeImportableFiles(importableContentItems, Config.CONTENT_TARGET_FOLDER, isDigital);
    }

}
