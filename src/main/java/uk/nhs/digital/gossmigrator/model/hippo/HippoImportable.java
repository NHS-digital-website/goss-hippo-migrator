package uk.nhs.digital.gossmigrator.model.hippo;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import uk.nhs.digital.gossmigrator.model.goss.GossContent;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;
import static uk.nhs.digital.gossmigrator.misc.TextHelper.toLowerCaseDashedValue;

public abstract class HippoImportable {

    private final String localizedName;
    private String jcrNodeName;
    private String jcrPath;
    protected Long id;
    protected String title;
    protected String summary;
    List<String> warnings = new ArrayList<>();
    String seoSummary;
    String shortSummary;
    List<Section> sections;
    HippoRichText component;
    private boolean live = true;

    protected HippoImportable(final String localizedName, final String jcrPath, final String jcrNodeName) {
        this.localizedName = StringUtils.removeAll(localizedName, "\"");
        this.jcrNodeName = toLowerCaseDashedValue(jcrNodeName);
        this.jcrPath = jcrPath.toLowerCase();
    }

    protected HippoImportable(GossContent gossContent){
        this(gossContent.getHeading(), gossContent.getJcrPath(), gossContent.getJcrNodeName());
    }

    /**
     * Absolute path locating a node in JCR repository, for example
     * '{@code /content/documents/corporate-website/publication-system/my-publication}'
     */
    @SuppressWarnings("unused") // Used by template.
    public String getJcrPath() {
        return jcrPath;
    }

    public String getLocalizedName() {
        return localizedName;
    }


    @SuppressWarnings("unused") // Used by template.
    public String getJcrNodeName() {
        return jcrNodeName;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, SHORT_PREFIX_STYLE);
    }

    public void setJcrNodeName(String jcrNodeName) {
        this.jcrNodeName = jcrNodeName;
    }

    public void setJcrPath(String jcrPath) {
        this.jcrPath = jcrPath;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    @SuppressWarnings("unused") // Used by template.
    public String getSeoSummary() {
        return seoSummary;
    }

    @SuppressWarnings("unused") // Used by template.
    public String getShortSummary() {
        return shortSummary;
    }

    @SuppressWarnings("unused") // Used by template.
    public List<Section> getSections() {
        return sections;
    }

    @SuppressWarnings("unused")
    public HippoRichText getComponent() {
        return component;
    }

    public boolean isLive() {
        return live;
    }

    void setLive(GossContent gossContent) {
        if (gossContent.getStatus() != null && !"Live".equals(gossContent.getStatus())) {
            live = false;
        }
    }
}
