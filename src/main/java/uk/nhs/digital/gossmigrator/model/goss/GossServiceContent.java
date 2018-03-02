package uk.nhs.digital.gossmigrator.model.goss;

import org.json.simple.JSONObject;
import uk.nhs.digital.gossmigrator.misc.GossExportHelper;
import uk.nhs.digital.gossmigrator.model.goss.enums.ContentType;

import java.util.Date;

import static uk.nhs.digital.gossmigrator.misc.GossExportHelper.getString;
import static uk.nhs.digital.gossmigrator.model.goss.enums.ContentType.SERVICE;
import static uk.nhs.digital.gossmigrator.model.goss.enums.DateFormatEnum.GOSS_LONG_FORMAT;
import static uk.nhs.digital.gossmigrator.model.goss.enums.GossExportFieldNames.*;

public class GossServiceContent extends GossContent{

    private String linkText;
    private Date date;
    private Date archiveDate;


    GossServiceContent(JSONObject gossJson, long gossExportFileLine, ContentType contentType){
        super(gossJson, gossExportFileLine, contentType);
        linkText = getString(gossJson, LINK_TEXT, id);
        date = GossExportHelper.getDate(gossJson, DATE, id, GOSS_LONG_FORMAT);

        archiveDate = GossExportHelper.getDate(gossJson, ARCHIVE_DATE, id, GOSS_LONG_FORMAT);
    }

    GossServiceContent(JSONObject gossJson, long gossExportFileLine) {
        this(gossJson, gossExportFileLine, SERVICE);
    }

    /*
     * Factory method to generate a GossServiceContent
     */
    public static GossServiceContent getInstance(JSONObject gossJson, long gossExportFileLine){
        return new GossServiceContent(gossJson, gossExportFileLine);
    }

    @SuppressWarnings("unused")
    public Date getArchiveDate() {
        return archiveDate;
    }


    /**
     * Article creation date.
     *
     * @return Creation date.
     */
    @SuppressWarnings("unused")
    public Date getDate() {
        return date;
    }



    /**
     * Text seen on links to the article.
     *
     * @return Link text.
     */
    @SuppressWarnings("unused")
    public String getLinkText() {
        return linkText;
    }
}
