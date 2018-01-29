package uk.nhs.digital.gossmigrator.model.hippo.enums;

public enum SectionTypes {
    DEFAULT("default")
    ;

    private String typeName;

    SectionTypes(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }
}
