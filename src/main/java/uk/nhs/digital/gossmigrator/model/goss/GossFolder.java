package uk.nhs.digital.gossmigrator.model.goss;

import uk.nhs.digital.gossmigrator.model.goss.enums.ContentType;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class GossFolder extends GossContent {

    public GossFolder(GossContent content) {
        Path parentPath = Paths.get(content.getJcrParentPath());
        depth = parentPath.getNameCount();
        jcrParentPath = parentPath.subpath(0, depth - 2).toString();
        jcrNodeName = parentPath.getName(depth - 1).toString();
        contentType = ContentType.FOLDER;
        id = 0L;
        heading = content.getHeading();
        relevantContentFlag = true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(jcrNodeName, jcrParentPath);
    }

    @Override
    public boolean equals(Object o) {
        return Objects.equals(jcrNodeName, jcrParentPath);
    }
}
