package uk.nhs.digital.gossmigrator;

import org.apache.poi.xssf.usermodel.XSSFRow;
import uk.nhs.digital.gossmigrator.Report.CSVMappingReportWriter;
import uk.nhs.digital.gossmigrator.misc.SpreadsheetHelper;
import uk.nhs.digital.gossmigrator.model.goss.GossContentList;

import java.util.HashMap;
import java.util.Map;

import static uk.nhs.digital.gossmigrator.model.mapping.enums.PublicationSeriesColumns.PUBLICATION_KEY;
import static uk.nhs.digital.gossmigrator.model.mapping.enums.PublicationSeriesColumns.SERIES_TITLE;
import static uk.nhs.digital.gossmigrator.model.mapping.enums.WorksheetEnum.PUBLICATION_SERIES_WORKSHEET;

public class SeriesImporter {

    private GossContentList seriesContentList = new GossContentList();
    private Map<String, Long> publicationKeyToSeriesIdMap = new HashMap<>();
    private Map<String, Long> processedSeries = new HashMap<>();

    /**
     * Publication Series is not a concept covered in the Goss export.
     * So Will be provided with an input csv file for the migration.
     * Need to create a Series for each unique series and then a/some publication(s) as children.
     * Will need to fill in the parent ids in the publications, so store a map.
     */
    SeriesImporter() {
        SpreadsheetHelper.parseWorksheet(this::processRow, PUBLICATION_SERIES_WORKSHEET);
    }

    private void processRow(XSSFRow row, int rowNumber) {
        String seriesTitle = row.getCell(SERIES_TITLE.getColumnIndex()).getStringCellValue();
        if (!processedSeries.containsKey(seriesTitle)) {
            Long seriesId = (long) (-1 * rowNumber);
            processedSeries.put(seriesTitle, seriesId);
            seriesContentList.add(
                    GossContentFactory.generateSeriesContent(seriesId
                            , seriesTitle, seriesTitle));
        }
        String publicationKey = row.getCell(PUBLICATION_KEY.getColumnIndex()).getStringCellValue();
        publicationKeyToSeriesIdMap.put(publicationKey,
                processedSeries.get(seriesTitle));
        CSVMappingReportWriter.addPublicationSeriesRow(seriesTitle, publicationKey);
    }

    public GossContentList getSeriesContentList() {
        return seriesContentList;
    }

    public Map<String, Long> getPublicationKeyToSeriesIdMap() {
        return publicationKeyToSeriesIdMap;
    }
}
