package uk.nhs.digital.gossmigrator.Report;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import uk.nhs.digital.gossmigrator.misc.GossExportHelper;
import uk.nhs.digital.gossmigrator.model.goss.GossContent;

import static uk.nhs.digital.gossmigrator.GossImporter.report;
import static uk.nhs.digital.gossmigrator.model.goss.enums.DateFormatEnum.TEMPLATE_FORMAT;

public class NonRevelantReportWriter {

    public static void createNonRelevantTab(){

        HSSFSheet services = report.createSheet("NonRelevant");
        HSSFRow servicesRowhead = services.createRow(0);
        servicesRowhead.createCell(0).setCellValue("Article ID");
        servicesRowhead.createCell(1).setCellValue("Title");
        servicesRowhead.createCell(2).setCellValue("Template ID");
        servicesRowhead.createCell(3).setCellValue("Display");
        servicesRowhead.createCell(4).setCellValue("Display End Date");

    }

    public static void addNonRelevantRow(GossContent content) {
        HSSFSheet sheet = report.getSheet("NonRelevant");
        HSSFRow row = sheet.createRow(sheet.getPhysicalNumberOfRows());
        try {
            row.createCell(0).setCellValue(content.getId());
            row.createCell(1).setCellValue(content.getHeading());
            row.createCell(2).setCellValue(content.getTemplateId());
            row.createCell(3).setCellValue(content.getDisplay());
            String displayEndDate = GossExportHelper.getDateString(content.getDisplayEndDate(), TEMPLATE_FORMAT);
            row.createCell(4).setCellValue(displayEndDate);
        }catch(Exception e){
            // no-op
        }

    }
}
