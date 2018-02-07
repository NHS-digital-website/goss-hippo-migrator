package uk.nhs.digital.gossmigrator.model.hippo;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.digital.gossmigrator.GossImporter;
import uk.nhs.digital.gossmigrator.config.Config;
import uk.nhs.digital.gossmigrator.misc.GossExportHelper;
import uk.nhs.digital.gossmigrator.model.goss.GossContent;
import uk.nhs.digital.gossmigrator.model.goss.GossContentMeta;
import uk.nhs.digital.gossmigrator.model.goss.GossProcessedData;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

import static java.nio.charset.Charset.defaultCharset;
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
        private List<String> taxonomyKeys = new ArrayList<String>();
        private List<String> fullTaxonomy = new ArrayList<String>();



        private Publication(GossContent gossContent) {
            super(gossContent.getHeading(),gossContent.getJcrPath(), gossContent.getJcrNodeName());
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


        public static Publication getInstance(GossProcessedData gossData, GossContent gossContent){

            Publication publication = new Publication(gossContent);
            publication.generateHippoTaxonomy(gossData,gossContent);

            Long publicationId = publication.getId();
            Long seriesId = gossData.getPublicationSeriesMap().get(publicationId);
            if(seriesId != null) {
                Optional<GossContent> matchingSeries = gossData.getSeriesContentList().stream().
                        filter(s -> s.getId() == seriesId).findFirst();
                GossContent matchingSeriesGoss = matchingSeries.orElse(null);

                if(matchingSeriesGoss != null){
                    publication.setJcrPath(Paths.get(matchingSeriesGoss.getJcrParentPath(),publication.getJcrNodeName(),"content").toString());
                    publication.setJcrNodeName("content");
                }
            }

            return publication;
        }


        private void generateHippoTaxonomy(GossProcessedData gossData, GossContent gossContent){

            List<GossContentMeta> metadataList = gossContent.getTaxonomyData();

            for(GossContentMeta metaData: metadataList){
                if ("Topics".equals(metaData.getGroup())){
                    String gossValue = metaData.getValue();
                    String hippoValue = gossData.getTaxonomyMap().get(gossValue);

                    String[] values = hippoValue.split("-");
                    List<String> valueList = Arrays.asList(values);
                    fullTaxonomy.addAll(valueList);
                    taxonomyKeys.add(valueList.get(valueList.size() - 1));
                }
            }
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

        public Series getSeries() { return series;  }

        public void setSeries(Series series) {
            this.series = series;
        }

        public Long getId() {
            return id;
        }

        public List<String> getTaxonomyKeys() {
            return taxonomyKeys;
        }
    // TODO delete this when key facts become rich text in doc type.
    public String getKeyFactsString(){
        if(null == keyFacts){
            return "";
        }else {
            return keyFacts.getContent();
        }
    }

        public List<String> getFullTaxonomy() {
            return fullTaxonomy;
        }
}
