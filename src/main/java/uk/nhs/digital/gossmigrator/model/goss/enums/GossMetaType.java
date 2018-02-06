package uk.nhs.digital.gossmigrator.model.goss.enums;

public enum GossMetaType {
    GEOGRAPHICAL("Geographical Coverage")
    ,TAXONOMY("Topics")
    ,INFORMATION_TYPE("Information Types")
    ,GRANULARITY("Geographical Granularity")
    ;

    private String group;

    GossMetaType(String group) {

        this.group = group;
    }

    public static GossMetaType getByGroup(String metaGroup){
        for(GossMetaType metaType : GossMetaType.values()){
            if(metaType.group.equals(metaGroup)){
                return metaType;
            }
        }
        return null;
    }
}
