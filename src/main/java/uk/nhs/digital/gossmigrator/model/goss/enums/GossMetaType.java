package uk.nhs.digital.gossmigrator.model.goss.enums;

public enum GossMetaType {
    GEOGRAPHICAL("Geographical Coverage"),
    TOPIC("Topic"),
    INFORMATION_TYPE("Information Type"),
    GRANULARITY("Geographical Granularity"),
    IMPORTFIELDCREATOR("ImportFieldCreator"),
    AREA("Area"),
    SUB_TOPIC("Sub-Topic"),
    A_TO_Z("AtoZ"),
    GOSS_TEST("GOSS Test");

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

    public String getGroup() {
        return group;
    }

    @Override
    public String toString() {
        return group;
    }
}
