package uk.nhs.digital.gossmigrator.model.hippo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.digital.gossmigrator.misc.GossExportHelper;
import uk.nhs.digital.gossmigrator.model.goss.*;
import uk.nhs.digital.gossmigrator.model.goss.enums.GossInternalLinkType;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static uk.nhs.digital.gossmigrator.model.goss.enums.DateFormatEnum.TEMPLATE_FORMAT;

public class Publication extends HippoImportable {

    private final static Logger LOGGER = LoggerFactory.getLogger(Publication.class);

    private String title;
    private final String informationType;
    private final HippoRichText keyFacts;
    private final String coverageStart;
    private final String coverageEnd;
    private String publicationDate;
    private final Long id;
    private List<String> taxonomyKeys = new ArrayList<>();
    private List<String> fullTaxonomy = new ArrayList<>();
    private final String geographicCoverage;
    private final String granuality;
    private List<HippoLink> relatedLinks = new ArrayList<>();
    private List<HippoLink> resourceLinks = new ArrayList<>();
    private List<HippoFile> files = new ArrayList<>();

    public Publication(GossPublicationContent gossContent) {
        super(gossContent.getHeading(), gossContent.getJcrPath(), gossContent.getJcrNodeName());
        this.id = gossContent.getId();

        if(gossContent.getHeading() != null){
            this.title = gossContent.getHeading();
        }else{
            LOGGER.warn("Title field is empty. ArticleId:{}.", id);
        }

        Date publicationDate = gossContent.getExtra().getPublicationDate();
        if(publicationDate != null){
            this.publicationDate = GossExportHelper.getDateString(publicationDate, TEMPLATE_FORMAT);
        }else{
            LOGGER.warn("Publication Date field is empty. ArticleId:{}.", id);
        }
        Date endDate = gossContent.getExtra().getCoverageEnd();
        this.coverageEnd = GossExportHelper.getDateString(endDate, TEMPLATE_FORMAT);
        Date startDate = gossContent.getExtra().getCoverageStart();
        this.coverageStart = GossExportHelper.getDateString(startDate, TEMPLATE_FORMAT);

        ParsedArticleText parsedArticleText = new ParsedArticleText(gossContent.getId(), gossContent.getText());
        this.keyFacts = parsedArticleText.getKeyFacts();

        this.geographicCoverage = gossContent.getGeographicalData();
        this.granuality = gossContent.getGranularity();
        this.informationType = gossContent.getInformationTypes();
        setResourcesAndLinks(gossContent);
    }

    /**
     * Factory method. Creates a Publication instance, searches for the publication series
     * and assigns the series to the publication
     * @param gossData, full goss extract containing the series
     * @param gossContent, the goss extract with the publication to be processed
     * Returns a Publication instance with the provided goss information
     */
    public static Publication getInstance(GossProcessedData gossData, GossPublicationContent gossContent) {

        Publication publication = new Publication(gossContent);
        publication.generateHippoTaxonomy(gossData, gossContent);

        Long publicationId = publication.getId();
        Long seriesId = gossData.getPublicationSeriesMap().get(publicationId);
        if (seriesId != null) {
            Optional<GossContent> matchingSeries = gossData.getSeriesContentList().stream().
                    filter(s -> s.getId() == seriesId).findFirst();
            GossContent matchingSeriesGoss = matchingSeries.orElse(null);

            if (matchingSeriesGoss != null) {
                publication.setJcrPath(Paths.get(matchingSeriesGoss.getJcrParentPath(),
                        publication.getJcrNodeName(), "content").toString());
                publication.setJcrNodeName("content");
            }else{
                LOGGER.warn("No matching series found.  ArticleId:{}. SeriesId:{}."
                        , publication.getId(), seriesId);
            }
        }

        return publication;
    }

    /**
     * Sets the Publication taxonomy keys and full taxonomy in the Publication object
     * @param gossData, full goss extract containing the taxonomy map
     * @param gossContent, the goss extract with the publication to be processed
     */
    private void generateHippoTaxonomy(GossProcessedData gossData, GossPublicationContent gossContent) {

        List<GossContentMeta> metadataList = gossContent.getTaxonomyData();

        for (GossContentMeta metaData : metadataList) {
            if ("Topics".equals(metaData.getGroup())) {
                String gossValue = metaData.getValue();
                String hippoValue = gossData.getTaxonomyMap().get(gossValue);

                if(hippoValue != null){
                    List<String> valueList = new ArrayList<>();
                    String[] values = hippoValue.split("-");
                    for(String s : values){
                        valueList.add(s.toLowerCase().replace(' ','-'));
                    }

                    fullTaxonomy.addAll(valueList);
                    taxonomyKeys.add(valueList.get(valueList.size() - 1));
                }else{
                    LOGGER.warn("No matching taxonomy found.  ArticleId:{}. GossCategory:{}."
                            , id, gossValue);
                }

            }
        }
    }

    /**
     * Sets the Resource Links, Related links and Files in the Publication
     * @param gossContent, the goss extract with the publication to be processed
     */
    private void setResourcesAndLinks(GossPublicationContent gossContent){
        List<GossLink> gossLinks = gossContent.getLinks();
        List<GossFile> gossFiles = gossContent.getFiles();


        for(GossLink gossLink: gossLinks){
            HippoLink hippoLink = new HippoLink(gossLink);
            //TODO verify link type matching
            if (gossLink.getId() != null){
                String id = gossLink.getId().toString();
                GossInternalLinkType linkType = GossInternalLinkType.getById(id);
                if(linkType != null) {
                    switch (linkType) {
                        case ARTICLE_LINK:
                            resourceLinks.add(hippoLink);
                            break;
                        case EXTERNAL_LINK:
                            relatedLinks.add(hippoLink);
                            break;
                    }
                }
            }
        }
        for(GossFile gossFile: gossFiles){
            HippoFile hippoFile = new HippoFile(gossContent,gossFile);
            files.add(hippoFile);
        }
    }


    @SuppressWarnings("unused") // used in template
    public String getTitle() {
        return title;
    }
    @SuppressWarnings("unused") // Used in template
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

    // TODO delete this when key facts become rich text in doc type.
    @SuppressWarnings("unused") // Used in template
    public String getKeyFactsString() {
        if (null == keyFacts) {
            return "";
        } else {
            return keyFacts.getContent();
        }
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
        return files;
    }
}
