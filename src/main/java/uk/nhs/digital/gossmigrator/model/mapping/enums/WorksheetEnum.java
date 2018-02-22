package uk.nhs.digital.gossmigrator.model.mapping.enums;

public enum WorksheetEnum {
    PUBLICATION_SERIES_WORKSHEET("PublicationSeries", 1)
    ;

    private String name;
    private int headerCount;

    WorksheetEnum(String name, int headerCount) {

        this.name = name;
        this.headerCount = headerCount;
    }

    public String getName() {
        return name;
    }

    public int getHeaderCount() {
        return headerCount;
    }
}
