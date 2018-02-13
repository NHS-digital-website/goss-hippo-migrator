package uk.nhs.digital.gossmigrator.Report;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import uk.nhs.digital.gossmigrator.GossImporter;
import uk.nhs.digital.gossmigrator.model.hippo.Service;

public class ServicesReportWriter {

    public static HSSFWorkbook report = GossImporter.report;

    public static void createServicesTab(){

        HSSFSheet services = report.createSheet("Services");
        HSSFRow servicesRowhead = services.createRow(0);
        servicesRowhead.createCell(0).setCellValue("Service ID");
        servicesRowhead.createCell(1).setCellValue("Title");
        servicesRowhead.createCell(2).setCellValue("Status");

    }

    public static void addServiceRow(Service service) {
        HSSFSheet sheet = report.getSheet("Services");
        HSSFRow row = sheet.createRow(sheet.getPhysicalNumberOfRows());
        row.createCell(0).setCellValue(service.getId());
        row.createCell(1).setCellValue(service.getTitle());
        row.createCell(2).setCellValue("Success");
    }

}
