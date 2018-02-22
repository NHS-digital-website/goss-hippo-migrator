package uk.nhs.digital.gossmigrator.Report;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import uk.nhs.digital.gossmigrator.model.hippo.Hub;

import static uk.nhs.digital.gossmigrator.GossImporter.report;

public class HubReportWriter {

    public static void createHubTab(){

        HSSFSheet services = report.createSheet("Hub");
        HSSFRow servicesRowhead = services.createRow(0);
        servicesRowhead.createCell(0).setCellValue("Hub ID");
        servicesRowhead.createCell(1).setCellValue("Title");
        servicesRowhead.createCell(2).setCellValue("Status");

    }

    public static void addHubRow(Hub hub) {
        HSSFSheet sheet = report.getSheet("Hub");
        HSSFRow row = sheet.createRow(sheet.getPhysicalNumberOfRows());
        row.createCell(0).setCellValue(hub.getId());
        row.createCell(1).setCellValue(hub.getTitle());
        row.createCell(2).setCellValue("Success");
    }
}
