package uk.nhs.digital.gossmigrator.Report;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import uk.nhs.digital.gossmigrator.model.hippo.General;

import static uk.nhs.digital.gossmigrator.GossImporter.digitalData;
import static uk.nhs.digital.gossmigrator.GossImporter.report;

public class GeneralReportWriter {

    public static void createGeneralTab(){

        HSSFSheet services = report.createSheet("General");
        HSSFRow servicesRowhead = services.createRow(0);
        servicesRowhead.createCell(0).setCellValue("Template ID");
        servicesRowhead.createCell(1).setCellValue("Doc ID");
        servicesRowhead.createCell(2).setCellValue("Title");
        servicesRowhead.createCell(3).setCellValue("Status");

    }

    public static void addGeneralRow(General general) {
        HSSFSheet sheet = report.getSheet("General");
        HSSFRow row = sheet.createRow(sheet.getPhysicalNumberOfRows());
        row.createCell(0).setCellValue(general.getTemplateId());
        row.createCell(1).setCellValue(general.getId());
        row.createCell(2).setCellValue(general.getTitle());
        row.createCell(3).setCellValue("Success");
        if(digitalData.getContentTypeMap().get(general.getTemplateId()) == null){
            WarningsReportWriter.addWarningRow("General Doc", general.getId(), general.getTitle(),
                    "Unknown Template ID: " + general.getTemplateId());
        }
    }

}
