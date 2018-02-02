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
        //  private final String GeographicCoverage;
        //  private final String Taxonomy;

        public Publication(GossContent gossContent) {
            super(gossContent.getHeading(), gossContent.getJcrPath(), gossContent.getJcrNodeName());
            this.title = gossContent.getHeading();
            this.informationType = gossContent.getContentType().name();
            ParsedArticleText parsedArticleText = new ParsedArticleText(gossContent.getId(), gossContent.getText());
            this.keyFacts = parsedArticleText.getKeyFacts();
            Date endDate = gossContent.getExtra().getCoverageEnd();
            this.coverageEnd = GossExportHelper.getDateString(endDate, TEMPLATE_FORMAT);
            Date startDate = gossContent.getExtra().getCoverageStart();
            this.coverageStart = GossExportHelper.getDateString(startDate, TEMPLATE_FORMAT);
            Date publicationDate = gossContent.getExtra().getPublicationDate();
            this.publicationDate = GossExportHelper.getDateString(publicationDate, TEMPLATE_FORMAT);
        }

        @SuppressWarnings("unused") // used in template
        public String getTitle() {
            return title;
        }

        public String getSummary() {
            return title + " Summary";
        }

        public String getInformationType() {
            return informationType;
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
}
