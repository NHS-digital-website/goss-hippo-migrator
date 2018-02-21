package uk.nhs.digital.gossmigrator.model.hippo;

import java.util.Objects;

public class HippoLinkRef {
    private String nodeName;
    private String jcrPath;

    public HippoLinkRef(String jcrPath, String nodeName) {
        this.nodeName = nodeName;
        this.jcrPath = jcrPath.toLowerCase();
    }

    @SuppressWarnings("unused") // Used by template
    public String getNodeName() {
        return nodeName;
    }

    @SuppressWarnings("unused") // Used by template
    public String getJcrPath() {
        return jcrPath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HippoLinkRef that = (HippoLinkRef) o;
        return this.jcrPath.equals(that.jcrPath) && this.nodeName.equals(that.nodeName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNodeName(), getJcrPath());
    }
}
