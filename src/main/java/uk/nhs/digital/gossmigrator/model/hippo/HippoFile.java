package uk.nhs.digital.gossmigrator.model.hippo;

import uk.nhs.digital.gossmigrator.misc.GossExportHelper;
import uk.nhs.digital.gossmigrator.model.goss.GossFile;
import uk.nhs.digital.gossmigrator.model.goss.GossPublicationContent;

import java.util.Calendar;
import java.util.Date;

import static uk.nhs.digital.gossmigrator.model.goss.enums.DateFormatEnum.TEMPLATE_FORMAT;

public class HippoFile {

    private String displayName;
    private String jrcEncoding;
    private String lastModified;
    private String data;
    private String mymeType;
    private String fileName;

    HippoFile(GossPublicationContent publication, GossFile gossFile) {
        this.displayName = gossFile.getDisplayText();
        this.jrcEncoding = "UTF-8";
        Date today = Calendar.getInstance().getTime();
        this.lastModified = GossExportHelper.getDateString(today, TEMPLATE_FORMAT);
        this.data = gossFile.getJcrPath(publication.getId());
        //TODO Confirm mimeType mapping
        this.mymeType = gossFile.getMimeType();
        this.fileName = gossFile.getFileName();
    }

    @SuppressWarnings("unused")
    public String getDisplayName() {
        return displayName;
    }

    @SuppressWarnings("unused")
    public String getJrcEncoding() {
        return jrcEncoding;
    }

    @SuppressWarnings("unused")
    public String getLastModified() {
        return lastModified;
    }

    public String getData() {
        return data;
    }

    @SuppressWarnings("unused")
    public String getMymeType() {
        return mymeType;
    }

    @SuppressWarnings("unused")
    public String getFileName() {
        return fileName;
    }
}
