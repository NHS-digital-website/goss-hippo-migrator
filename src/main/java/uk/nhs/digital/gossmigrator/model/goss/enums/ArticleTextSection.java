package uk.nhs.digital.gossmigrator.model.goss.enums;

public enum ArticleTextSection {
    TOPTASKS("UPPERBODY"),
    CONTACT_INFO("CTA"),
    INTRO_AND_SECTIONS("__DEFAULT"),
    COMPONENT("COMPONENT"),
    FACTS("FACTS"),
    RESOURCE_LINKS("RESOURCELINKS"),
    RELATED_LINKS("LINKS")
    ;

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

    public static boolean idExists(String id){
        for(ArticleTextSection a : ArticleTextSection.values()){
            if(a.id.equals(id)){
                return true;
            }
        }
        return false;
    }
}
