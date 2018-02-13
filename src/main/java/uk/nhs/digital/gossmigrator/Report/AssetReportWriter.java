package uk.nhs.digital.gossmigrator.Report;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import uk.nhs.digital.gossmigrator.GossImporter;
import uk.nhs.digital.gossmigrator.model.hippo.Asset;

public class AssetReportWriter {

    public static HSSFWorkbook report = GossImporter.report;

    public static void createAssetTab() {

        HSSFSheet assets = report.createSheet("Assets");
        HSSFRow assetsRowhead = assets.createRow(0);
        assetsRowhead.createCell(0).setCellValue("File Path");
        assetsRowhead.createCell(1).setCellValue("MimeType");
        assetsRowhead.createCell(2).setCellValue("Status");
    }

    public static void addAssetRow(Asset asset) {
        HSSFSheet sheet = report.getSheet("Assets");
        HSSFRow row = sheet.createRow(sheet.getPhysicalNumberOfRows());
        row.createCell(0).setCellValue(asset.getFilePath());
        row.createCell(1).setCellValue(asset.getMimeType());
        row.createCell(2).setCellValue("Success");
    }
}
