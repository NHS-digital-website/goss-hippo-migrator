package uk.nhs.digital.gossmigrator.model.hippo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.digital.gossmigrator.Report.PublicationReportWriter;
import uk.nhs.digital.gossmigrator.misc.GossExportHelper;
import uk.nhs.digital.gossmigrator.model.goss.GossContentMeta;
import uk.nhs.digital.gossmigrator.model.goss.GossFile;
import uk.nhs.digital.gossmigrator.model.goss.GossProcessedData;
import uk.nhs.digital.gossmigrator.model.goss.GossPublicationContent;
import uk.nhs.digital.gossmigrator.model.goss.enums.ContentType;

import java.util.*;

import static uk.nhs.digital.gossmigrator.model.goss.enums.DateFormatEnum.TEMPLATE_FORMAT;

public class Publication extends HippoImportable {

    private final static Logger LOGGER = LoggerFactory.getLogger(Publication.class);
    private HippoRichText publicationSummary;
    private final String informationType;
    private final HippoRichText keyFacts;
    private final String coverageStart;
    private final String coverageEnd;
    private String publicationDate;
    private List<String> taxonomyKeys = new ArrayList<>();
    private List<String> fullTaxonomy = new ArrayList<>();
    private final String geographicCoverage;
    private final String granuality;
    private List<HippoLink> relatedLinks;
    private List<HippoLink> resourceLinks;
    private List<HippoFile> files = new ArrayList<>();
    private String publicationId;
    private GeoCoverage geoCoverage = new GeoCoverage();

    public Publication(GossPublicationContent gossContent) {
        super(gossContent.getHeading(), gossContent.getJcrPath(), gossContent.getJcrNodeName());
        setLive(gossContent);
        warnings.addAll(gossContent.getWarnings());
        id = gossContent.getId();

        if (gossContent.getHeading() != null) {
            title = gossContent.getHeading();
        } else {
            LOGGER.warn("Title field is empty. ArticleId:{}.", id);
            warnings.add("Title field is empty.");
        }

        Date publicationDate = gossContent.getExtra().getPublicationDate();
        if (publicationDate != null) {
            this.publicationDate = GossExportHelper.getDateString(publicationDate, TEMPLATE_FORMAT);
        } else {
            LOGGER.warn("Publication Date field is empty. ArticleId:{}.", id);
            warnings.add("Publication Date field is empty.");
        }
        Date endDate = gossContent.getExtra().getCoverageEnd();
        coverageEnd = GossExportHelper.getDateString(endDate, TEMPLATE_FORMAT);
        Date startDate = gossContent.getExtra().getCoverageStart();
        coverageStart = GossExportHelper.getDateString(startDate, TEMPLATE_FORMAT);

        ParsedArticleText parsedArticleText = new ParsedArticleText(gossContent.getId(), gossContent.getTemplateId(), gossContent.getText(), ContentType.PUBLICATION);
        keyFacts = parsedArticleText.getKeyFacts();
        publicationSummary = parsedArticleText.getDefaultNode();
        geographicCoverage = gossContent.getGeographicalData();

        //TODO complete geoCoverage mapping
        geoCoverage.setCoverage(gossContent.getGeographicalData());

        granuality = gossContent.getGranularity();
        informationType = gossContent.getInformationTypes();

        publicationId = gossContent.getExtra().getPublicationId();
        setFilesAndLinks(gossContent);
    }

    /**
     * Factory method. Creates a Publication instance, searches for the publication series
     * and assigns the series to the publication
     *
     * @param gossData,    full goss extract containing the series
     * @param gossContent, the goss extract with the publication to be processed
     *                     Returns a Publication instance with the provided goss information
     */
    public static Publication getInstance(GossProcessedData gossData, GossPublicationContent gossContent) {

        Publication publication = new Publication(gossContent);
        publication.generateHippoTaxonomy(gossData, gossContent);
        return publication;
    }

    /**
     * Sets the Publication taxonomy keys and full taxonomy in the Publication object
     *
     * @param gossData,    full goss extract containing the taxonomy map
     * @param gossContent, the goss extract with the publication to be processed
     */
    private void generateHippoTaxonomy(GossProcessedData gossData, GossPublicationContent gossContent) {

        List<GossContentMeta> metadataList = gossContent.getTaxonomyData();
        Set<String> hippoUniqueKeys = new HashSet<>();
        for (GossContentMeta metaData : metadataList) {
            String gossValue = metaData.getValue();
            List<String> hippoValues = gossData.getTaxonomyMap().get(gossValue);
                for (String hippoValue : hippoValues) {
                    if (hippoValue != null && !hippoValue.isEmpty()) {
                        List<String> valueList = new ArrayList<>();
                        String[] values = hippoValue.split("-");
                        for (String s : values) {
                            valueList.add(s.toLowerCase().replace(' ', '-'));
                        }

                        hippoUniqueKeys.add(valueList.get(valueList.size() - 1));
                        fullTaxonomy.addAll(valueList);
                    } else {
                        LOGGER.warn("No matching taxonomy found.  ArticleId:{}. GossCategory:{}."
                                , id, gossValue);
                        warnings.add("No matching taxonomy found. Goss Category: " + gossValue);
                    }
                }
        }
        taxonomyKeys.addAll(hippoUniqueKeys);
    }

    /**
     * Sets the Resource Links, Related links and Files in the Publication
     *
     * @param gossContent, the goss extract with the publication to be processed
     */
    private void setFilesAndLinks(GossPublicationContent gossContent){
        ParsedArticleLinks parsedArticleLinks = new ParsedArticleLinks(id, gossContent.getTemplateId(), gossContent.getText());
        this.relatedLinks = parsedArticleLinks.getRelatedLinks();
        this.resourceLinks = parsedArticleLinks.getResourceLinks();

        for(HippoLink link : relatedLinks){
            PublicationReportWriter.addPublicationLinkRow(this.getId(), "Related Link", link);
        }
        for(HippoLink link : resourceLinks){
            PublicationReportWriter.addPublicationLinkRow(this.getId(), "Resource Link", link);
        }

        List<GossFile> gossFiles = gossContent.getFiles();
        for(GossFile gossFile: gossFiles){
            HippoFile hippoFile = new HippoFile(gossContent,gossFile);
            files.add(hippoFile);
            PublicationReportWriter.addPublicationFileRow(this.getId(), hippoFile);
        }
    }


    @SuppressWarnings("unused") // used in template
    public String getTitle() {
        return title;
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

    @SuppressWarnings("unused") // Used in template
    public HippoRichText getKeyFacts() {
        return keyFacts;
    }

    @SuppressWarnings("unused") // Used in template
    public String getCoverageStart() {
        return coverageStart;
    }

    @SuppressWarnings("unused") // Used in template
    public String getCoverageEnd() {
        return coverageEnd;
    }

    @SuppressWarnings("unused") // Used in template
    public String getPublicationDate() {
        return publicationDate;
    }

    @SuppressWarnings("unused") // Used in template
    public Long getId() {
        return id;
    }

    @SuppressWarnings("unused") // Used in template
    public List<String> getTaxonomyKeys() {
        return taxonomyKeys;
    }

    @SuppressWarnings("unused") // Used in template
    public List<String> getFullTaxonomy() {
        return fullTaxonomy;
    }

    @SuppressWarnings("unused")
    public List<HippoLink> getRelatedLinks() {
        return relatedLinks;
    }

    @SuppressWarnings("unused")
    public List<HippoLink> getResourceLinks() {
        return resourceLinks;
    }

    public List<HippoFile> getFiles() {
        // TODO put files back when know RPS solution!
        //return files;
        return null;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    @SuppressWarnings("unused")
    public String getPublicationId() {
        return publicationId;
    }

    @SuppressWarnings("unused") // Used in template
    public HippoRichText getPublicationSummary() {
        return publicationSummary;
    }

    @SuppressWarnings("unused") // Used in template
    public GeoCoverage getGeoCoverage() {
        return geoCoverage;
    }
}
