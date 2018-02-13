package uk.nhs.digital.gossmigrator.Report;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import uk.nhs.digital.gossmigrator.GossImporter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

public class ReportWriter {

    private static HSSFWorkbook report = GossImporter.report;

    public static void generateReport() {

        WarningsReportWriter.createWarningsTab();

        AssetReportWriter.createAssetTab();

        ServicesReportWriter.createServicesTab();

        PublicationReportWriter.createPublicationTabs();

        CSVMappingReportWriter.createMappingTabs();

    }

    public static void writeFile() {

        setColumnWidth();
        try {
            FileOutputStream fileOut = new FileOutputStream("Migration report.xlsx");
            report.write(fileOut);
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void setColumnWidth() {
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
}
