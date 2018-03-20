package uk.nhs.digital.gossmigrator.Report;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import static uk.nhs.digital.gossmigrator.GossImporter.iframeReport;
import static uk.nhs.digital.gossmigrator.GossImporter.report;

public class ReportWriter {

    public static void generateReport() {

        WarningsReportWriter.createWarningsTab();

        NonRevelantReportWriter.createNonRelevantTab();

        RedirectReportWriter.createRedirectTab();

        GeneralReportWriter.createGeneralTab();

        PublicationReportWriter.createPublicationTabs();

        ServicesReportWriter.createServicesTab();

        HubReportWriter.createHubTab();

        AssetReportWriter.createAssetTab();

        ListPageReportWriter.createPageTab();

        CSVMappingReportWriter.createMappingTabs();

        IframeReport.generateIframeReport();

    }

    public static void writeFile() {

        setColumnWidth(report);
        write(report, "Migration report.xlsx");

        setColumnWidth(iframeReport);
        write(iframeReport, "iFrame report.xlsx");
    }

    private static void setColumnWidth(HSSFWorkbook report) {
        int numberOfSheets = report.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            HSSFSheet sheet = report.getSheetAt(i);
            HSSFRow row = sheet.getRow(0);
            Iterator<Cell> cellIterator = row.cellIterator();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                int columnIndex = cell.getColumnIndex();
                sheet.autoSizeColumn(columnIndex);
            }
        }
    }

    private static void write(HSSFWorkbook report, String name){
        try {
            FileOutputStream fileOut = new FileOutputStream(name);
            report.write(fileOut);
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
