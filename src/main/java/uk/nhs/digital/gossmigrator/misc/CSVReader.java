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
import java.util.ArrayList;
import java.util.List;
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
                        target = (T) processTaxonomyMapping((Map<String, List<String>>)target, record);
                        break;
                    case METADATA_MAPPING:
                        target = (T) processMetadataMapping((MetadataMappingItem)target, record);
                        break;
                    case DOCUMENT_TYPE:
                        target = (T) processDocumentTypeMapping((Map<Long, ContentType>)target, record);
                        break;
                    case GENERAL_TYPE:
                        target = (T) processGeneralTypeMapping((Map<Long, String>)target, record);
                        break;
                    case TEMPLATE_ID:
                        target = (T) processTemplateIdMapping((List<Long>)target, record);
                        break;
                }
            }
        }
        return target;
    }

    private Map<String, List<String>> processTaxonomyMapping(Map<String, List<String>> target, CSVRecord record){
        String gossTaxonomy = StringUtils.trim(record.get(0));
        String hippoTaxonomy = StringUtils.trim(record.get(1));
        String additionalTaxononomy1 = StringUtils.trim(record.get(2));
        String additionalTaxononomy2 = StringUtils.trim(record.get(3));

        List<String> taxonomies = new ArrayList<>();
        taxonomies.add(hippoTaxonomy);
        if(additionalTaxononomy1 != null && !additionalTaxononomy1.isEmpty()){
            taxonomies.add(additionalTaxononomy1);
        }
        if(additionalTaxononomy2 != null && !additionalTaxononomy2.isEmpty()){
            taxonomies.add(additionalTaxononomy2);
        }
        for (String taxonomy: taxonomies){

            CSVMappingReportWriter.addTaxonomyRow(gossTaxonomy, taxonomy);
        }
        target.put(gossTaxonomy, taxonomies);
        return target;
    }

    private MetadataMappingItem processMetadataMapping(MetadataMappingItem target, CSVRecord record){
        String gossGroup = record.get(0);
        String gossValue = record.get(1);
        String hippoValue = record.get(2);
        target.setGossGroup(gossGroup);
        target.setGossValue(gossValue);
        target.setHippoValue(hippoValue);
        CSVMappingReportWriter.addMetadataRow(gossGroup, gossValue, hippoValue);
        return target;
    }

    private Map<Long, ContentType> processDocumentTypeMapping(Map<Long, ContentType> target, CSVRecord record){

        Pattern r = Pattern.compile("ID=([0-9]+)");
        Matcher m = r.matcher(record.get(0));

        if (m.find()) {
            Long templateID = Long.parseLong(m.group(1));
            for(ContentType contentType :ContentType.values()){
                if(contentType.getDescription().equals(record.get(1))){
                    target.put(templateID, contentType);
                    break;
                }
            }
        }
        return target;
    }

    private Map<Long, String> processGeneralTypeMapping(Map<Long, String> target, CSVRecord record){
        Long templateID = Long.parseLong(record.get(0));
        String documentType = record.get(1);
        target.put(templateID, documentType);
        return target;
    }

    private List<Long> processTemplateIdMapping(List<Long> target, CSVRecord record){
        Long templateID = Long.parseLong(record.get(1));
        target.add(templateID);
        return target;
    }

}
