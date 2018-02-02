package uk.nhs.digital.gossmigrator.model.goss.enums;

public enum DateFormatEnum {
    GOSS_LONG_FORMAT("MMM, dd yyyy HH:mm:ss Z"),
    GOSS_SHOR_FORMAT("dd/MM/yyyy"),
    TEMPLATE_FORMAT("yyyy'-'MM'-'dd'T'HH':'mm':'sss'Z'");

    private String format;

    DateFormatEnum(String format) {
        this.format = format;
    }

    public String getFormat() {
        return format;
    }
}
