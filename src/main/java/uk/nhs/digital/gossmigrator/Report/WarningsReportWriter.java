package uk.nhs.digital.gossmigrator.Report;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import uk.nhs.digital.gossmigrator.GossImporter;

public class WarningsReportWriter {

    public static HSSFWorkbook report = GossImporter.report;

    public static void createWarningsTab() {


        HSSFSheet warnings = report.createSheet("Warnings");
        HSSFRow warningsRowhead = warnings.createRow(0);
        warningsRowhead.createCell(0).setCellValue("Item Type");
        warningsRowhead.createCell(1).setCellValue("Item Id");
        warningsRowhead.createCell(2).setCellValue("Item Details");
        warningsRowhead.createCell(3).setCellValue("Warning");
    }

    public static void addWarningRow(String itemType, Long itemId, String itemDetails, String warning){
        HSSFSheet sheet = report.getSheet("Warnings");
        HSSFRow row = sheet.createRow(sheet.getPhysicalNumberOfRows());
        row.createCell(0).setCellValue(itemType);
        row.createCell(1).setCellValue(itemId);
        row.createCell(2).setCellValue(itemDetails);
        row.createCell(3).setCellValue(warning);
    }
}
