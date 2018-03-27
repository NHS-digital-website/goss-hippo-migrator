package uk.nhs.digital.gossmigrator.model.hippo;

import static uk.nhs.digital.gossmigrator.model.goss.enums.DateFormatEnum.TEMPLATE_FORMAT;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.digital.gossmigrator.GossImporter;
import uk.nhs.digital.gossmigrator.Report.PublicationReportWriter;
import uk.nhs.digital.gossmigrator.misc.GeoCoverageHelper;
import uk.nhs.digital.gossmigrator.misc.GossExportHelper;
import uk.nhs.digital.gossmigrator.model.goss.GossContentMeta;
import uk.nhs.digital.gossmigrator.model.goss.GossFile;
import uk.nhs.digital.gossmigrator.model.goss.GossProcessedData;
import uk.nhs.digital.gossmigrator.model.goss.GossPublicationContent;
import uk.nhs.digital.gossmigrator.model.goss.enums.ContentType;

import java.util.*;

public class Publication extends HippoImportable {

    private final static Logger LOGGER = LoggerFactory.getLogger(Publication.class);
    private HippoRichText publicationSummary;
    private final String informationType;
    private final HippoRichText keyFacts;
    private final String coverageStart;
    private final String coverageEnd;
    private String publicationDate;
    private Set<String> taxonomyKeys = new HashSet<>();
    private final String granuality;
    private List<HippoLink> relatedLinks;
    private List<HippoLink> resourceLinks;
    private List<S3File> files = new ArrayList<>();
    private String publicationId;
    private List<String> geoCoverageList;

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
        geoCoverageList = new GeoCoverageHelper().getGeoCoverageList(gossContent.getGeographicalData());

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

        for (GossContentMeta metaData : gossContent.getTaxonomyData()) {
            List<String> hippoValues = gossData.getTaxonomyMap().get(metaData.getValue());

            if(metaData.getValue().equals("Data quality")){
                LOGGER.warn("Data quality meta in article:{}", id);
            }
            if (null == hippoValues || hippoValues.size() == 0) {
                LOGGER.warn("No taxonomy mapped for article {}, key {}", id, metaData.getValue());
            } else {
                taxonomyKeys.addAll(hippoValues);
            }
        }
    }

    /**
     * Sets the Resource Links, Related links and Files in the Publication
     *
     * @param gossContent, the goss extract with the publication to be processed
     */
    private void setFilesAndLinks(GossPublicationContent gossContent) {
        ParsedArticleLinks parsedArticleLinks = new ParsedArticleLinks(id, gossContent.getTemplateId(), gossContent.getText());
        this.relatedLinks = parsedArticleLinks.getRelatedLinks();
        this.resourceLinks = parsedArticleLinks.getResourceLinks();

        for (HippoLink link : relatedLinks) {
            PublicationReportWriter.addPublicationLinkRow(this.getId(), "Related Link", link);
        }
        for (HippoLink link : resourceLinks) {
            PublicationReportWriter.addPublicationLinkRow(this.getId(), "Resource Link", link);
        }

        for (Long mediaId : gossContent.getFiles()) {
            GossFile mediaFile = GossImporter.digitalData.getGossFileMap().get(mediaId);
            mediaFile.addS3Reference(id);

            S3File s3File = new S3File(mediaFile);
            files.add(s3File);
            PublicationReportWriter.addPublicationFileRow(this.getId(), s3File);
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
    public Set<String> getTaxonomyKeys() {
        return taxonomyKeys;
    }

    @SuppressWarnings("unused")
    public List<HippoLink> getRelatedLinks() {
        return relatedLinks;
    }

    @SuppressWarnings("unused")
    public List<HippoLink> getResourceLinks() {
        return resourceLinks;
    }

    public List<S3File> getFiles() {
        return files;
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
    public List<String> getGeoCoverageList() {
        return geoCoverageList;
    }
}
