package uk.nhs.digital.gossmigrator.model.hippo;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import static uk.nhs.digital.gossmigrator.misc.TextHelper.toLowerCaseDashedValue;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

public abstract class HippoImportable {

    private final String localizedName;
    private String jcrNodeName;
    private String jcrPath;

    protected HippoImportable(final String localizedName, final String jcrPath) {
        this.localizedName = localizedName;
        this.jcrNodeName = toLowerCaseDashedValue(getLocalizedName());
        this.jcrPath = jcrPath;
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
}
