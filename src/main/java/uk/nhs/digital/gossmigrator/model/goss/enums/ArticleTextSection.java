package uk.nhs.digital.gossmigrator.model.goss.enums;

public enum ArticleTextSection {
    TOPTASKS("UPPERBODY", "Upper"),
    CONTACT_INFO("CTA", "CTA"),
    INTRO_AND_SECTIONS("__DEFAULT", "Default"),
    COMPONENT("COMPONENT", "Component"),
    FACTS("FACTS", "Facts"),
    RESOURCE_LINKS("RESOURCELINKS", "ResourceLinks"),
    RELATED_LINKS("LINKS", "RelatedLinks"),
    NEWSFLASH("NEWSFLASH", "Newsflash")
    ;

    private String id;
    private String ref;

    ArticleTextSection(String id, String ref) {
        this.id = id;
        this.ref = ref;
    }

    public String getId() {
        return id;
    }

    public String getRef() {
        return ref;
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
