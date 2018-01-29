package uk.nhs.digital.gossmigrator.model.goss.enums;

public enum ArticleTextSection {
    TOPTASKS("UPPERBODY"),
    CONTACT_INFO("CTA"),
    INTRO_AND_SECTIONS("__DEFAULT"),
    COMPONENT("COMPONENT");

    private String id;

    ArticleTextSection(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return id;
    }
}
