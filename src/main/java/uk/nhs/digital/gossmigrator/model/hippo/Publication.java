package uk.nhs.digital.gossmigrator.model.hippo;

import uk.nhs.digital.gossmigrator.misc.GossExportHelper;
import uk.nhs.digital.gossmigrator.model.goss.GossContent;

import java.util.Date;

import static uk.nhs.digital.gossmigrator.model.goss.enums.DateFormatEnum.TEMPLATE_FORMAT;

public class Publication extends HippoImportable {

    private final String title;
    private final String informationType;
    private final HippoRichText keyFacts;
    private final String coverageStart;
    private final String coverageEnd;
    private final String publicationDate;
    private final Long id;
    private final String geographicCoverage;
    private final String granuality;
    //  private final String Taxonomy;

    public Publication(GossContent gossContent) {
        super(gossContent.getHeading(), gossContent.getJcrPath(), gossContent.getJcrNodeName());
        this.title = gossContent.getHeading();
        ParsedArticleText parsedArticleText = new ParsedArticleText(gossContent.getId(), gossContent.getText());
        this.keyFacts = parsedArticleText.getKeyFacts();
        Date endDate = gossContent.getExtra().getCoverageEnd();
        this.coverageEnd = GossExportHelper.getDateString(endDate, TEMPLATE_FORMAT);
        Date startDate = gossContent.getExtra().getCoverageStart();
        this.coverageStart = GossExportHelper.getDateString(startDate, TEMPLATE_FORMAT);
        Date publicationDate = gossContent.getExtra().getPublicationDate();
        this.publicationDate = GossExportHelper.getDateString(publicationDate, TEMPLATE_FORMAT);
        this.id = gossContent.getId();
        this.geographicCoverage = gossContent.getGeographicalData();
        this.granuality = gossContent.getGranularity();
        this.informationType = gossContent.getInformationTypes();
    }

    @SuppressWarnings("unused") // used in template
    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return title + " Summary";
    }

    @SuppressWarnings("unused") // Used in template
    public String getInformationType() {
        return informationType;
    }

    @SuppressWarnings("unused") // Used in template
    public String getGeographicCoverage() {
        return geographicCoverage;
    }

    @SuppressWarnings("unused") // Used in template
    public String getGranuality() {
        return granuality;
    }

    public HippoRichText getKeyFacts() {
        return keyFacts;
    }

    public String getCoverageStart() {
        return coverageStart;
    }

    public String getCoverageEnd() {
        return coverageEnd;
    }

    public String getPublicationDate() {
        return publicationDate;
    }

    public Long getId() {
        return id;
    }

    // TODO delete this when key facts become rich text in doc type.
    public String getKeyFactsString(){
        if(null == keyFacts){
            return "";
        }else {
            return keyFacts.getContent();
        }
    }

}
