package uk.nhs.digital.gossmigrator.model.goss;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.digital.gossmigrator.config.Config;
import uk.nhs.digital.gossmigrator.misc.GossExportHelper;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import static uk.nhs.digital.gossmigrator.model.goss.enums.GossExportFieldNames.FILE_ID;

public class GossFile {
    private static final Logger LOGGER = LoggerFactory.getLogger(GossFile.class);
    Long id;
    String title;
    String pathInGossExport;
    String pathOnDisk;
    String jcrPath;
    Set<Long> references = new HashSet<>();
    boolean existsOnDisk = false;

    public GossFile(JSONObject fileJson) {
        id = GossExportHelper.getIdOrError(fileJson, FILE_ID);
        JSONObject fileObject = (JSONObject) fileJson.get("Files");
        if (fileObject.size() > 1) {
            LOGGER.error("Goss File MediaId:{} has more than one file node.  Don't know what to do with this.", id);
        } else if (!fileObject.containsKey("Any")) {
            LOGGER.warn("Don't know how to deal with file yet (Only coded the 'Any' type) MediaId:{}.", id);
        } else {
            for (Object v : fileObject.values()) {
                pathInGossExport = v.toString();
            }
            setPathOnDiskAndJcrPath();
        }
    }

    private void setPathOnDiskAndJcrPath() {
        String fileSourceFolder = Config.ASSET_SOURCE_FOLDER;
        String rootFolder = Config.ASSET_SOURCE_FOLDER_IN_GOSS_EXPORT;

        // Remove windows drive.
        String[] windowsPathParts = pathInGossExport.split(":");
        if (windowsPathParts.length == 2) {
            pathOnDisk = windowsPathParts[1];
        } else {
            pathOnDisk = windowsPathParts[0];
        }

        // Turn windows slashes into unix.
        pathOnDisk = pathOnDisk.replaceAll("\\\\", "/");

        // Find where path begins to match files on disk
        int rootPartId = 0;
        Path p = Paths.get(pathOnDisk);
        boolean foundRootPath = false;
        for (Path part : p) {
            if (part.toString().equals(rootFolder)) {
                foundRootPath = true;
                break;
            }
            rootPartId++;
        }

        if (!foundRootPath) {
            LOGGER.warn("Expected Media (id:{}) node in export to have a path part '{}' to map to disk."
                    , id, rootFolder);
        }

        p = p.subpath(rootPartId, p.getNameCount());
        jcrPath = Paths.get(Config.JCR_ASSET_ROOT, p.toString()).toString();

        p = Paths.get(fileSourceFolder, p.toString());
        if (!p.toFile().exists()) {
            LOGGER.error("Could not find file:{} when processing goss MediaId:{}", p, id);
        } else {
            existsOnDisk = true;
        }
        pathOnDisk = p.toString();

    }

    public Long getId() {
        return id;
    }

    public String getJcrPath(Long articleId) {
        references.add(articleId);
        return jcrPath;
    }
}
