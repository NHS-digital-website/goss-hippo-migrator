package uk.nhs.digital.gossmigrator.model.goss;

import org.json.simple.JSONObject;
import uk.nhs.digital.gossmigrator.GossImporter;
import uk.nhs.digital.gossmigrator.misc.GossExportHelper;

import java.nio.file.Paths;
import java.util.Date;

import static uk.nhs.digital.gossmigrator.misc.GossExportHelper.getString;
import static uk.nhs.digital.gossmigrator.model.goss.enums.ContentType.SERVICE;
import static uk.nhs.digital.gossmigrator.model.goss.enums.DateFormatEnum.GOSS_LONG_FORMAT;
import static uk.nhs.digital.gossmigrator.model.goss.enums.GossExportFieldNames.*;

public class GossServiceContent extends GossContent{

    private String linkText;
    private String introduction;
    private Date date;
    private String display;
    private Date archiveDate;

    private GossServiceContent(JSONObject gossJson, long gossExportFileLine){
        super(gossJson, gossExportFileLine);
        contentType = SERVICE;
        linkText = getString(gossJson, LINK_TEXT, id);
        introduction = getString(gossJson, INTRO, id);
        date = GossExportHelper.getDate(gossJson, DATE, id, GOSS_LONG_FORMAT);
        display = getString(gossJson, DISPLAY, id);
        archiveDate = GossExportHelper.getDate(gossJson, ARCHIVE_DATE, id, GOSS_LONG_FORMAT);
    }

    /*
     * Factory method to generate a GossServiceContent
     */
    public static GossServiceContent getInstance(JSONObject gossJson, long gossExportFileLine){
        GossServiceContent serviceContent = new GossServiceContent(gossJson, gossExportFileLine);
        return serviceContent;
    }

    /*
     * Adds one additional level for Services folder structure
     */
    public String getServicePath() {
        return Paths.get(jcrParentPath, jcrNodeName, jcrNodeName).toString();
    }

    /**
     * Either set as on or off if displayed or hidden.
     *
     * @return on, off or hidden.
     */
    @SuppressWarnings("unused")
    public String getDisplay() {
        return display;
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

    public String getIntroduction() {
        return introduction;
    }

}
