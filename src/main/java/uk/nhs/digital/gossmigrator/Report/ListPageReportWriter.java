package uk.nhs.digital.gossmigrator.Report;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import uk.nhs.digital.gossmigrator.model.hippo.General;
import uk.nhs.digital.gossmigrator.model.hippo.ListPage;

import static uk.nhs.digital.gossmigrator.GossImporter.gossData;
import static uk.nhs.digital.gossmigrator.GossImporter.report;

public class ListPageReportWriter {

    public static void createPageTab(){

        HSSFSheet services = report.createSheet("ListPage");
        HSSFRow servicesRowhead = services.createRow(0);
        servicesRowhead.createCell(1).setCellValue("Doc ID");
        servicesRowhead.createCell(2).setCellValue("Title");
        servicesRowhead.createCell(3).setCellValue("Status");

    }

    public static void addRow(ListPage listPage) {
        HSSFSheet sheet = report.getSheet("ListPage");
        HSSFRow row = sheet.createRow(sheet.getPhysicalNumberOfRows());
        row.createCell(1).setCellValue(listPage.getId());
        row.createCell(2).setCellValue(listPage.getTitle());
        row.createCell(3).setCellValue("Success");
        if(gossData.getContentTypeMap().get(listPage.getTemplateId()) == null){
            WarningsReportWriter.addWarningRow("General Doc", listPage.getId(), listPage.getTitle(),
                    "Unknown Template ID: " + listPage.getTemplateId());
        }
    }

}
