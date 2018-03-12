package uk.nhs.digital.gossmigrator.Report;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import uk.nhs.digital.gossmigrator.model.hippo.Redirect;

import static uk.nhs.digital.gossmigrator.GossImporter.report;

public class RedirectReportWriter {

    public static void createRedirectTab(){

        HSSFSheet services = report.createSheet("Redirect");
        HSSFRow servicesRowhead = services.createRow(0);
        servicesRowhead.createCell(0).setCellValue("Source");
        servicesRowhead.createCell(1).setCellValue("Redirect ID");
        servicesRowhead.createCell(2).setCellValue("From");
        servicesRowhead.createCell(3).setCellValue("To");

    }

    public static void addRedirectRow(String source, Redirect redirect) {
        HSSFSheet sheet = report.getSheet("Redirect");
        HSSFRow row = sheet.createRow(sheet.getPhysicalNumberOfRows());
        row.createCell(0).setCellValue(source);
        row.createCell(1).setCellValue(redirect.getId());
        row.createCell(2).setCellValue(redirect.getRuleFrom());
        row.createCell(3).setCellValue(redirect.getRuleTo());
        if(redirect.getRuleTo().isEmpty()){
            WarningsReportWriter.addWarningRow("Redirect", redirect.getId(), redirect.getRuleFrom(), "Redirect rule To is empty");
        }
    }
}
