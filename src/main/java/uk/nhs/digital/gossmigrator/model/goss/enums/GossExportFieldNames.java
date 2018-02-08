package uk.nhs.digital.gossmigrator.model.goss.enums;

public enum GossExportFieldNames {
    HEADING("ARTICLEHEADING", true)
    ,ID("ARTICLEID", true)
    ,EXTRA("Extra", true)
    ,EXTRA_INCLUDE_RELATED("INCLUDERELATEDARTICLES", false)
    ,EXTRA_INCLUDE_CHILD("INCLUDECHILDARTICLES", false)
    ,TEMPLATE_ID("ARTICLETEMPLATEID", true)
    ,SUMMARY("ARTICLESUMMARY", false)
    ,FRIENDLY_URL("FRIENDLYURL", false)
    ,LINK_TEXT("ARTICLELINKTEXT", false)
    ,PARENTID("ARTICLEPARENTID", false)
    ,INTRO("ARTICLEINTROTEXT", false)
    ,DATE("ARTICLEDATE", true)
    ,TEXT("ARTICLETEXT", false)
    ,DISPLAY_DATE("ARTICLEDISPLAYDATE", false)
    ,DISPLAY_END_DATE("ARTICLEDISPLAYEDATE", false)
    ,DISPLAY("ARTICLEDISPLAY", false)
    ,ARCHIVE_DATE("ARCHIVEDATE", false)
    ,META_DATA("Metadata", false)
    ,META_DATA_GROUP("GROUP", false)
    ,META_DATA_VALUE("VALUE", false)
    ,META_DATA_NAME("NAME", false)
    ,COVSTARTDATE("COVSTARTDATE", false)
    ,PUBDATE("PUBDATE",false)
    ,COVENDDATE("COVENDDATE",false)
    ,LINK_ID("ID", false)
    ,LINK_ADDRESS("ADDRESS", true)
    ,FILE_ID("MEDIAID", true)
    ,FILE_TITLE("TITLE", false)
    ,EXTRA_OBJECT_ID("ETCOBJECTID", false)
    ;

    private String name;
    private boolean isMandatory;

    GossExportFieldNames(String name, boolean isMandatory) {
        this.name = name;
        this.isMandatory = isMandatory;
    }

    public String getName() {
        return name;
    }

    public boolean isMandatory() {
        return isMandatory;
    }

    @Override
    public String toString() {
        return name;
    }
}
