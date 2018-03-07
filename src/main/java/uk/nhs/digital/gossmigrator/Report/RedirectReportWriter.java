package uk.nhs.digital.gossmigrator.Report;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import uk.nhs.digital.gossmigrator.model.hippo.Redirect;

import static uk.nhs.digital.gossmigrator.GossImporter.report;

public class RedirectReportWriter {

    public static void createRedirectTab(){

        HSSFSheet services = report.createSheet("Redirect");
        HSSFRow servicesRowhead = services.createRow(0);
        servicesRowhead.createCell(0).setCellValue("Redirect ID");
        servicesRowhead.createCell(1).setCellValue("From");
        servicesRowhead.createCell(2).setCellValue("To");

    }

    public static void addRedirectRow(Redirect redirect) {
        HSSFSheet sheet = report.getSheet("Redirect");
        HSSFRow row = sheet.createRow(sheet.getPhysicalNumberOfRows());
        row.createCell(0).setCellValue(redirect.getId());
        row.createCell(1).setCellValue(redirect.getRuleFrom());
        row.createCell(2).setCellValue(redirect.getRuleFrom());
    }
}
