package uk.nhs.digital.gossmigrator.Report;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import uk.nhs.digital.gossmigrator.GossImporter;
import uk.nhs.digital.gossmigrator.model.goss.GossSeriesContent;

public class CSVMappingReportWriter {

    public static HSSFWorkbook report = GossImporter.report;

    public static void createMappingTabs(){

        HSSFSheet series = report.createSheet("Series");
        HSSFRow seriesRowhead = series.createRow(0);
        seriesRowhead.createCell(0).setCellValue("Series ID");
        seriesRowhead.createCell(1).setCellValue("Title");
        seriesRowhead.createCell(2).setCellValue("Children count");
        seriesRowhead.createCell(3).setCellValue("Status");

        HSSFSheet pubSeries = report.createSheet("Publication-Series Mapping");
        HSSFRow pubSeriesRowhead = pubSeries.createRow(0);
        pubSeriesRowhead.createCell(0).setCellValue("Series ID");
        pubSeriesRowhead.createCell(1).setCellValue("Publication ID");
        pubSeriesRowhead.createCell(2).setCellValue("Status");

        HSSFSheet taxonomy = report.createSheet("Taxonomy Mapping");
        HSSFRow taxonomyRowhead = taxonomy.createRow(0);
        taxonomyRowhead.createCell(0).setCellValue("Goss Topic");
        taxonomyRowhead.createCell(1).setCellValue("Hippo Mapping");
        taxonomyRowhead.createCell(2).setCellValue("Status");

        HSSFSheet metadata = report.createSheet("Metadata Mapping");
        HSSFRow metadataRowhead = metadata.createRow(0);
        metadataRowhead.createCell(0).setCellValue("Goss group");
        metadataRowhead.createCell(1).setCellValue("Goss value");
        metadataRowhead.createCell(2).setCellValue("Hippo value");
        metadataRowhead.createCell(3).setCellValue("Status");

    }

    public static void addPublicationSeriesRow(Long seriesID, Long PublicationID) {
        HSSFSheet sheet = report.getSheet("Publication-Series Mapping");
        HSSFRow row = sheet.createRow(sheet.getPhysicalNumberOfRows());
        row.createCell(0).setCellValue(seriesID);
        row.createCell(1).setCellValue(PublicationID);
        row.createCell(2).setCellValue("Success");

    }

    public static void addSeriesRow(GossSeriesContent series) {
        HSSFSheet sheet = report.getSheet("Series");
        HSSFRow row = sheet.createRow(sheet.getPhysicalNumberOfRows());
        row.createCell(0).setCellValue(series.getId());
        row.createCell(1).setCellValue(series.getHeading());
        row.createCell(2).setCellValue(series.getChildrenCount());
        row.createCell(3).setCellValue("Success");
    }

    public static void addTaxonomyRow(String gossTaxonomy, String hippoTaxonomy) {
        HSSFSheet sheet = report.getSheet("Taxonomy Mapping");
        HSSFRow row = sheet.createRow(sheet.getPhysicalNumberOfRows());
        row.createCell(0).setCellValue(gossTaxonomy);
        row.createCell(1).setCellValue(hippoTaxonomy);
        row.createCell(2).setCellValue("Success");
    }

    public static void addMetadataRow(String gossGroup, String gossValue, String hippoValue) {
        HSSFSheet sheet = report.getSheet("Metadata Mapping");
        HSSFRow row = sheet.createRow(sheet.getPhysicalNumberOfRows());
        row.createCell(0).setCellValue(gossGroup);
        row.createCell(1).setCellValue(gossValue);
        row.createCell(2).setCellValue(hippoValue);
        row.createCell(3).setCellValue("Success");
    }
}
