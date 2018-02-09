package uk.nhs.digital.gossmigrator.model.goss;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.digital.gossmigrator.config.Config;
import uk.nhs.digital.gossmigrator.misc.FolderHelper;
import uk.nhs.digital.gossmigrator.misc.GossExportHelper;
import uk.nhs.digital.gossmigrator.misc.TextHelper;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import static uk.nhs.digital.gossmigrator.model.goss.enums.GossExportFieldNames.FILE_ID;

public class GossFile {
    private static final Logger LOGGER = LoggerFactory.getLogger(GossFile.class);
    private Long id;
    private String pathInGossExport;
    private String jcrPath;
    // Expecting to use next in reporting.
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private Set<Long> references = new HashSet<>();
    // Expecting to use next in reporting.
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private boolean existsOnDisk = false;

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
        String pathOnDisk = FolderHelper.dosToUnixPath(pathInGossExport);

        // Split goss path on node that starts to match what is stored on local distk.
        // e.g. goss might be /inetpub/export/content/media/folder1/folder2/a.pdf
        // but we store the pdf in /home/bob/gossfiles/media/folder1/folder2/a.pdf
        // Need to remove the goss prefix part of path and replace with the local prefix.
        // Similarly need the part with neither prefix to build the jcr path.
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
        // Format node name.
        String fileName = p.getFileName().toString();
        p = p.subpath(0, p.getNameCount() - 1);

        jcrPath = Paths.get(Config.JCR_ASSET_ROOT, p.toString().toLowerCase()
                , fileName.toLowerCase()).toString();

        // Check source file exists
        p = Paths.get(fileSourceFolder, p.toString(), fileName);
        if (!p.toFile().exists()) {
            LOGGER.error("Could not find file:{} when processing goss MediaId:{}", p, id);
        } else {
            existsOnDisk = true;
        }
    }

    public Long getId() {
        return id;
    }

    public String getJcrPath(Long articleId) {
        references.add(articleId);
        return jcrPath;
    }
}
