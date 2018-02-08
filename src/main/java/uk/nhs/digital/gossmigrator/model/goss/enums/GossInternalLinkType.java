package uk.nhs.digital.gossmigrator.model.goss.enums;

public enum GossInternalLinkType {
    ARTICLE_LINK("1")
    ,DOCUMENT_LINK("2")
    ,EXTERNAL_LINK("3")
    ,IMAGE("4")
    ,PAGE_SNIPPET("5")
    ,SCRIPTS("6")
    ,VIDEO_INLINE("7")
    ;

    private String id;

    GossInternalLinkType(String id) {

        this.id = id;
    }

    public static GossInternalLinkType getById(String id){
        for(GossInternalLinkType link : GossInternalLinkType.values()){
            if(id.equals(link.id)){
                return link;
            }
        }
        return null;
    }
}
