package uk.nhs.digital.gossmigrator.model.mapping.enums;

public enum PublicationSeriesColumns {
    PUBLICATION_TITLE(1),
    PUBLICATION_KEY(2),
    SERIES_TITLE(3),
    SERIES_SUMMARY(3)
    ;

    private int columnIndex;

    PublicationSeriesColumns(int columnIndex) {

        this.columnIndex = columnIndex;
    }

    public int getColumnIndex() {
        return columnIndex;
    }
}
