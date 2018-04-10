package uk.nhs.digital.gossmigrator.Report;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import uk.nhs.digital.gossmigrator.model.hippo.S3File;
import uk.nhs.digital.gossmigrator.model.hippo.HippoLink;
import uk.nhs.digital.gossmigrator.model.hippo.Publication;

import static uk.nhs.digital.gossmigrator.GossImporter.report;

public class PublicationReportWriter {


    public static void createPublicationTabs(){

        HSSFSheet publications = report.createSheet("Publications");
        HSSFRow publicationsRowhead = publications.createRow(0);
        publicationsRowhead.createCell(0).setCellValue("Publication ID");
        publicationsRowhead.createCell(1).setCellValue("Title");
        publicationsRowhead.createCell(2).setCellValue("Status");

        HSSFSheet pubLinks = report.createSheet("Publication Links");
        HSSFRow pubLinksRowhead = pubLinks.createRow(0);
        pubLinksRowhead.createCell(0).setCellValue("Publication ID");
        pubLinksRowhead.createCell(1).setCellValue("Link Type");
        pubLinksRowhead.createCell(2).setCellValue("Text");
        pubLinksRowhead.createCell(3).setCellValue("Path");
        pubLinksRowhead.createCell(4).setCellValue("Status");

        HSSFSheet pubFiles = report.createSheet("Publication Files");
        HSSFRow pubFilesRowhead = pubFiles.createRow(0);
        pubFilesRowhead.createCell(0).setCellValue("Publication ID");
        pubFilesRowhead.createCell(1).setCellValue("File ID");
        pubFilesRowhead.createCell(2).setCellValue("Name");
        pubFilesRowhead.createCell(3).setCellValue("MimeType");
        pubFilesRowhead.createCell(4).setCellValue("Status");

    }


    public static void addPublicationRow(Publication publication) {

        HSSFSheet sheet = report.getSheet("Publications");
        HSSFRow row = sheet.createRow(sheet.getPhysicalNumberOfRows());
        row.createCell(0).setCellValue(publication.getId());
        row.createCell(1).setCellValue(publication.getTitle());
        if(publication.getWarnings().isEmpty()){
            row.createCell(2).setCellValue("Success");
        }else{
            row.createCell(2).setCellValue("Warnings");
            for(String warning: publication.getWarnings()){
                WarningsReportWriter.addWarningRow("Publication", publication.getId(), publication.getTitle(), warning);
            }
        }
    }

    public static void addPublicationLinkRow(Long publicationId, String linkType, HippoLink link){

        HSSFSheet sheet = report.getSheet("Publication Links");
        HSSFRow row = sheet.createRow(sheet.getPhysicalNumberOfRows());
        row.createCell(0).setCellValue(publicationId);
        row.createCell(1).setCellValue(linkType);
        row.createCell(2).setCellValue(link.getDisplayText());
        row.createCell(3).setCellValue(link.getAddress());
        row.createCell(4).setCellValue("Success");
    }

    public static void addPublicationFileRow(Long publicationId, S3File file){
        HSSFSheet sheet = report.getSheet("Publication Files");
        HSSFRow row = sheet.createRow(sheet.getPhysicalNumberOfRows());
        row.createCell(0).setCellValue(publicationId);
        row.createCell(1).setCellValue(file.getId());
        row.createCell(2).setCellValue(file.getLocalizedName());
        try {
            row.createCell(3).setCellValue(file.getMimeType());
        }catch(Exception e){
            row.createCell(3).setCellValue("File-not-found");
        }
        if(file.getWarnings().isEmpty()){
            row.createCell(4).setCellValue("Success");
        }else{
            row.createCell(4).setCellValue("Warnings");
            for(String warning: file.getWarnings()){
                WarningsReportWriter.addWarningRow("File", file.getId(), file.getLocalizedName(), warning);
            }
        }
    }
}
