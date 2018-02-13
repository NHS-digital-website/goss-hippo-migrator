package uk.nhs.digital.gossmigrator.model.hippo;

import org.apache.tika.Tika;
import uk.nhs.digital.gossmigrator.misc.GossExportHelper;
import uk.nhs.digital.gossmigrator.model.goss.GossFile;
import uk.nhs.digital.gossmigrator.model.goss.GossPublicationContent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static uk.nhs.digital.gossmigrator.model.goss.enums.DateFormatEnum.TEMPLATE_FORMAT;

public class HippoFile {

    private Long id;
    private String displayName;
    private String jrcEncoding;
    private String lastModified;
    private String data;
    private String fileName;
    private List<String> warnings = new ArrayList<>();

    HippoFile(GossPublicationContent publication, GossFile gossFile) {
        warnings.addAll(gossFile.getWarnings());
        this.id = gossFile.getId();
        this.displayName = gossFile.getDisplayText();
        this.jrcEncoding = "UTF-8";
        Date today = Calendar.getInstance().getTime();
        this.lastModified = GossExportHelper.getDateString(today, TEMPLATE_FORMAT);
        this.data = gossFile.getJcrPath(publication.getId());
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
    public String getMimeType() {
        Tika tika = new Tika();
        return tika.detect(data);
    }

    @SuppressWarnings("unused")
    public String getFileName() {
        return fileName;
    }

    public Long getId() {
        return id;
    }

    public List<String> getWarnings() {
        return warnings;
    }
}
