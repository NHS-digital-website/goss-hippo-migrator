package uk.nhs.digital.gossmigrator.model.goss.enums;

public enum GossMetaType {
    GEOGRAPHICAL("Geographical Coverage"),
    TAXONOMY("Topic"),
    INFORMATION_TYPE("Information Type"),
    GRANULARITY("Geographical Granularity"),
    IMPORTFIELDCREATOR("ImportFieldCreator"),
    AREA("Area"),SUB_TOPIC("Sub-Topic");

    private String group;

    GossMetaType(String group) {

        this.group = group;
    }

    public static GossMetaType getByGroup(String metaGroup) {
        for (GossMetaType metaType : GossMetaType.values()) {
            if (metaType.group.equals(metaGroup)) {
                return metaType;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return group;
    }
}
