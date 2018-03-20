package uk.nhs.digital.gossmigrator.Report;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static uk.nhs.digital.gossmigrator.GossImporter.iframeReport;

public class IframeReport {

    public static void generateIframeReport() {

        HSSFSheet iframeSheet = iframeReport.createSheet("iFrame");
        HSSFRow header = iframeSheet.createRow(0);
        header.createCell(0).setCellValue("Article ID");
        header.createCell(1).setCellValue("Template ID");
        header.createCell(2).setCellValue("iFrame snippet");
        header.createCell(3).setCellValue("Category");
    }

    public static void addIframeRow(Long articleId, Long templateId, String snippet, String src) {

        HSSFSheet sheet = iframeReport.getSheet("iFrame");
        HSSFRow row = sheet.createRow(sheet.getPhysicalNumberOfRows());
        row.createCell(0).setCellValue(articleId);
        row.createCell(1).setCellValue(templateId);
        row.createCell(2).setCellValue(snippet);
        row.createCell(3).setCellValue(categorize(src));
    }

    private static String categorize(String src){

        String category;

        Pattern youtube = Pattern.compile("youtube");
        Matcher youtubeMatcher = youtube.matcher(src);

        Pattern vimeo = Pattern.compile("vimeo");
        Matcher vimeoMatcher = vimeo.matcher(src);

        Pattern maps = Pattern.compile("google.com/maps");
        Matcher mapsMatcher = maps.matcher(src);

        Pattern powerbi = Pattern.compile("powerbi");
        Matcher powerbiMatcher = powerbi.matcher(src);

        if (youtubeMatcher.find()) {
            category = "YouTube";
        }else if(vimeoMatcher.find()) {
            category = "Vimeo";
        }else if(mapsMatcher.find()) {
            category = "Google Maps";
        }else if(powerbiMatcher.find()) {
            category = "Power Bi";
        }else{
            category = "Other";
        }

        return category;
    }
}