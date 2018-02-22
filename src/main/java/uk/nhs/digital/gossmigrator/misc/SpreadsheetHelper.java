package uk.nhs.digital.gossmigrator.misc;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.digital.gossmigrator.config.Config;
import uk.nhs.digital.gossmigrator.model.mapping.enums.WorksheetEnum;

import java.io.File;
import java.io.IOException;

public class SpreadsheetHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpreadsheetHelper.class);

    public interface SpreadsheetRowProcessor {
        void process(XSSFRow row, int rowNumber);
    }

    public static void parseWorksheet(SpreadsheetRowProcessor runner, WorksheetEnum sheet) {
        try (OPCPackage opcPackage = OPCPackage.open(new File(Config.GOSS_HIPPO_MAPPING_FILE))) {
            XSSFWorkbook workbook = new XSSFWorkbook(opcPackage);
            XSSFSheet worksheet = workbook.getSheet(sheet.getName());
            int firstRow = worksheet.getFirstRowNum() + sheet.getHeaderCount();
            int lastRow = worksheet.getLastRowNum();
            for (int i = firstRow; i <= lastRow; i++) {
                XSSFRow row = worksheet.getRow(i);
                runner.process(row, i);
            }

        } catch (IOException | InvalidFormatException e) {
            LOGGER.error("Failed reading mapping file:" + Config.GOSS_HIPPO_MAPPING_FILE, e);
        }
    }
}
