package uk.nhs.digital.gossmigrator.model.hippo;

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
}
