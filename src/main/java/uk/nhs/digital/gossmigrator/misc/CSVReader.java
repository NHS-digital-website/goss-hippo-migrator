package uk.nhs.digital.gossmigrator.misc;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.digital.gossmigrator.Report.CSVMappingReportWriter;
import uk.nhs.digital.gossmigrator.Report.WarningsReportWriter;
import uk.nhs.digital.gossmigrator.model.goss.enums.ContentType;
import uk.nhs.digital.gossmigrator.model.mapping.MetadataMappingItem;
import uk.nhs.digital.gossmigrator.model.mapping.enums.MappingType;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.charset.Charset.defaultCharset;


public class CSVReader<T> {

    private final static Logger LOGGER = LoggerFactory.getLogger(CSVReader.class);


    /**
     * Method to read a Csv file
     *
     * @param csvData, the file that needs to be parsed
     *                 Returns an iterable parser that contains the csv records
     */
    public CSVParser readFile(File csvData) {
        CSVParser parser;
        try {
            // TODO utf-8 charset?  Probably does not matter as expecting only ASCII chars.
            parser = CSVParser.parse(csvData, defaultCharset(), CSVFormat.RFC4180);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        return parser;
    }


    /**
     * Method to read a Csv record
     *
     * @param target,      the entity in which will be stored the contents of the csv record
     * @param record,      the csv record to be processed
     * @param mappingType, enum containing information about the csv contents and format expected
     *                     Returns the target object, updated with the csv information
     */
    @SuppressWarnings("unchecked")
    public T processMapping(T target, CSVRecord record, MappingType mappingType) {

        if (record.getRecordNumber() > mappingType.getHeaderCount()) {
            if (record.size() != mappingType.getExpectedColumns()) {
                LOGGER.error("Invalid {} mapping. Expected {} columns, got {}. Data:{}",
                        mappingType.getDescription(), mappingType.getExpectedColumns(), record.size(), record);
                WarningsReportWriter.addWarningRow(mappingType.getDescription(), 0L, record.toString(), "Invalid number of columns");
            } else {
                switch (mappingType) {
                    case TAXONOMY_MAPPING:
                        String gossTaxonomy = StringUtils.trim(record.get(0));
                        String hippoTaxonomy = StringUtils.trim(record.get(1));
                        ((Map<String, String>) target).put(gossTaxonomy, hippoTaxonomy);
                        CSVMappingReportWriter.addTaxonomyRow(gossTaxonomy, hippoTaxonomy);
                        break;
                    case METADATA_MAPPING:
                        String gossGroup = record.get(0);
                        String gossValue = record.get(1);
                        String hippoValue = record.get(2);
                        ((MetadataMappingItem) target).setGossGroup(gossGroup);
                        ((MetadataMappingItem) target).setGossValue(gossValue);
                        ((MetadataMappingItem) target).setHippoValue(hippoValue);
                        CSVMappingReportWriter.addMetadataRow(gossGroup, gossValue, hippoValue);
                        break;
                    case DOCUMENT_TYPE:

                        Pattern r = Pattern.compile("ID=([0-9]+)");
                        Matcher m = r.matcher(record.get(0));

                        if (m.find()) {
                            Long templateID = Long.parseLong(m.group(1));
                            for(ContentType contentType :ContentType.values()){
                                if(contentType.getDescription().equals(record.get(1))){
                                    ((Map<Long, ContentType>) target).put(templateID, contentType);
                                    break;
                                }
                            }
                        }
                        break;
                    case GENERAL_TYPE:
                        Long templateID = Long.parseLong(record.get(0));
                        String documentType = record.get(1);
                        ((Map<Long, String>) target).put(templateID, documentType);
                        break;
                }
            }
        }
        return target;
    }

}
