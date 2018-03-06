package uk.nhs.digital.gossmigrator.model.goss;

import static uk.nhs.digital.gossmigrator.model.goss.enums.GossExportFieldNames.*;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.digital.gossmigrator.config.Config;
import uk.nhs.digital.gossmigrator.misc.FolderHelper;
import uk.nhs.digital.gossmigrator.misc.GossExportHelper;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GossFile {
    private static final Logger LOGGER = LoggerFactory.getLogger(GossFile.class);
    private Long id;
    private String pathInGossExport;
    private String jcrPath;
    private String filePathOnDisk;
    // Expecting to use next in reporting.

    private Set<Long> references = new HashSet<>();
    // Expecting to use next in reporting.
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private boolean existsOnDisk = false;
    private String displayText;
    private String fileName;
    private String mediaDirectory;
    private boolean notLiveLink = false;
    private List<String> warnings = new ArrayList<>();
    private long size;

    public GossFile(JSONObject fileJson) {
        id = GossExportHelper.getIdOrError(fileJson, FILE_ID);
        JSONObject fileObject = (JSONObject) fileJson.get("Files");
        if (fileObject.size() > 1) {
            LOGGER.warn("Goss File MediaId:{} has more than one file node.  Using first file path.", id);
            warnings.add("Goss File has more than one file node.");
        }

        // Take first path
        for (Object v : fileObject.values()) {
            pathInGossExport = v.toString();
            break;
        }

        // Skip any media not with live path.  The export has 2 links for most files.
        if (pathInGossExport.contains(Config.IGNORE_MEDIA_WITH_PATH_PART)) {
            LOGGER.info("Ignoring Goss Media Link:, {}", id, pathInGossExport);
            notLiveLink = true;
            return;
        }

        mediaDirectory = GossExportHelper.getString(fileJson, MEDIA_DIRECTORY, id);
        setPathOnDiskAndJcrPath();

        displayText = GossExportHelper.getString(fileJson, FILE_TITLE, id);
    }

    private void setPathOnDiskAndJcrPath() {
        String fileSourceFolder = Config.ASSET_SOURCE_FOLDER;
        String rootFolder = Config.ASSET_SOURCE_FOLDER_IN_GOSS_EXPORT;

        // Split goss path on node that starts to match what is stored on local disk.
        // e.g. goss might be /inetpub/export/content/media/folder1/folder2/a.pdf
        // but we store the pdf in /home/bob/gossfiles/media/folder1/folder2/a.pdf
        // Need to remove the goss prefix part of path and replace with the local prefix.
        // Similarly need the part with neither prefix to build the jcr path.
        int rootPartId = 0;
        Path p = Paths.get(FolderHelper.dosToUnixPath(pathInGossExport));
        fileName = p.getFileName().toString();
        boolean foundRootPath = false;
        for (Path part : p) {
            if (part.toString().equals(rootFolder)) {
                foundRootPath = true;
                break;
            }
            rootPartId++;
        }
        // Strip off prefix.
        if (foundRootPath) {
            p = p.subpath(rootPartId + 1, p.getNameCount());
         //   p = p.subpath(0, p.getNameCount() - 1);
        } else {
            p = Paths.get(mediaDirectory, p.toString());
        }

        if (GossExportHelper.isImage(fileName)) {
            jcrPath = Paths.get(Config.JCR_GALLERY_ROOT, p.toString().toLowerCase()).toString();
        } else {
            jcrPath = Paths.get(Config.JCR_ASSET_ROOT, p.toString().toLowerCase()).toString();
        }

        // Check source file exists
        filePathOnDisk = Paths.get(fileSourceFolder, p.toString()).toString();

        if (!Paths.get(filePathOnDisk).toFile().exists()) {
            LOGGER.error("Could not find file:{} when processing goss MediaId:{}", filePathOnDisk, id);
            warnings.add("Could not find file " + filePathOnDisk);
        } else {
            size = Paths.get(filePathOnDisk).toFile().length();
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

    public String getJcrPath() {
        return jcrPath;
    }

    public String getDisplayText() {
        return displayText;
    }

    public String getFileName() {
        return fileName;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public boolean isNotLiveLink() {
        return notLiveLink;
    }

    public Set<Long> getReferences() {
        return references;
    }

    public String getFilePathOnDisk() {
        return filePathOnDisk;
    }

    public long getSize() {
        return size;
    }
}
