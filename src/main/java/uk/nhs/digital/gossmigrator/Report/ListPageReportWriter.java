package uk.nhs.digital.gossmigrator.Report;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import uk.nhs.digital.gossmigrator.model.hippo.ListPage;

import static uk.nhs.digital.gossmigrator.GossImporter.report;

public class ListPageReportWriter {

    public static void createPageTab(){

        HSSFSheet sheet = report.createSheet("ListPage");
        HSSFRow rowhead = sheet.createRow(0);
        rowhead.createCell(1).setCellValue("Doc ID");
        rowhead.createCell(2).setCellValue("Title");
        rowhead.createCell(3).setCellValue("Status");

    }

    public static void addRow(ListPage listPage) {
        HSSFSheet sheet = report.getSheet("ListPage");
        HSSFRow row = sheet.createRow(sheet.getPhysicalNumberOfRows());
        row.createCell(1).setCellValue(listPage.getId());
        row.createCell(2).setCellValue(listPage.getTitle());
        row.createCell(3).setCellValue("Success");
    }

}
