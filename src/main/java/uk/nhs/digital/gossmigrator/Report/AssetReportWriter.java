package uk.nhs.digital.gossmigrator.Report;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import uk.nhs.digital.gossmigrator.model.hippo.AssetReportable;

import static uk.nhs.digital.gossmigrator.GossImporter.report;

public class AssetReportWriter {


    public static void createAssetTab() {

        HSSFSheet assets = report.createSheet("Assets");
        HSSFRow assetsRowhead = assets.createRow(0);
        assetsRowhead.createCell(0).setCellValue("File Path");
        assetsRowhead.createCell(1).setCellValue("MimeType");
        assetsRowhead.createCell(2).setCellValue("Status");
    }

    public static void addAssetRow(AssetReportable asset) {
        HSSFSheet sheet = report.getSheet("Assets");
        HSSFRow row = sheet.createRow(sheet.getPhysicalNumberOfRows());
        row.createCell(0).setCellValue(asset.getFilePath());
        row.createCell(1).setCellValue(asset.getMimeType());
        row.createCell(2).setCellValue("Success");
    }
}
