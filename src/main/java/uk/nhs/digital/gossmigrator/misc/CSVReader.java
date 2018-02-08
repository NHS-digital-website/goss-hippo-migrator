package uk.nhs.digital.gossmigrator.misc;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.digital.gossmigrator.model.goss.GossContent;
import uk.nhs.digital.gossmigrator.model.goss.GossContentFactory;
import uk.nhs.digital.gossmigrator.model.goss.GossContentList;
import uk.nhs.digital.gossmigrator.model.mapping.MetadataMappingItem;
import uk.nhs.digital.gossmigrator.model.mapping.enums.MappingType;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static java.nio.charset.Charset.defaultCharset;


public class CSVReader<T> {

    private final static Logger LOGGER = LoggerFactory.getLogger(CSVReader.class);


    /**
     * Method to read a Csv file
     * @param csvData, the file that needs to be parsed
     * Returns an iterable parser that contains the csv records
     *
     */
    public CSVParser readFile(File csvData){
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
     * @param target, the entity in which will be stored the contents of the csv record
     * @param record, the csv record to be processed
     * @param mappingType, enum containing information about the csv contents and format expected
     * Returns the target object, updated with the csv information
     */
    @SuppressWarnings("unchecked")
    public T processMapping(T target, CSVRecord record, MappingType mappingType) {

        if(record.getRecordNumber() > mappingType.getHeaderCount()) {
            if (record.size() != mappingType.getExpectedColumns()) {
                LOGGER.error("Invalid {} mapping. Expected {} columns, got {}. Data:{}",
                        mappingType.getDescription(), mappingType.getExpectedColumns(), record.size(), record);
            }else{
                switch (mappingType) {
                    case TAXONOMY_MAPPING:
                        ((Map<String, String>) target).put(StringUtils.trim(record.get(0)), StringUtils.trim(record.get(1)));
                        break;
                    case PUBLICATION_SERIES_MAPPING:
                        ((Map<Long, Long>) target).put(Long.parseLong(StringUtils.trim(record.get(1)))
                                , -1L * Long.parseLong(StringUtils.trim(record.get(0))));
                        break;
                    case SERIES_ITEM:
                        ((GossContentList)target).add(GossContentFactory.generateSeriesContent(Long.parseLong(record.get(0)) * (-1L),
                                StringUtils.trim(record.get(1)), StringUtils.trim(record.get(2))));
                        break;
                    case METADATA_MAPPING:
                        ((MetadataMappingItem)target).setGossGroup(record.get(0));
                        ((MetadataMappingItem)target).setGossValue(record.get(1));
                        ((MetadataMappingItem)target).setHippoValue(record.get(2));
                        break;
                }
            }
        }
        return target;
    }

}
