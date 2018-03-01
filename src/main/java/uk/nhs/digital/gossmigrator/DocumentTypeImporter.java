package uk.nhs.digital.gossmigrator;

import org.apache.commons.csv.CSVParser;
import uk.nhs.digital.gossmigrator.config.Config;
import uk.nhs.digital.gossmigrator.misc.CSVReader;
import uk.nhs.digital.gossmigrator.model.goss.enums.ContentType;
import uk.nhs.digital.gossmigrator.model.mapping.enums.MappingType;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DocumentTypeImporter {

    private Map<Long,ContentType> documentTypeMap = new HashMap<>();
    private Map<Long,String> generalDocumentTypeMap = new HashMap<>();
    private List<Long> nonRelevantIdList = new ArrayList<>();

    public Map<Long,ContentType> populateContentTypes(){
        File csvData = new File(Config.DOCUMENT_TYPE_MAPPING_FILE);
        CSVReader<Map<Long,ContentType>> reader = new CSVReader<>();
        CSVParser parser = reader.readFile(csvData);
        parser.forEach(record -> documentTypeMap = reader.processMapping(documentTypeMap,record, MappingType.DOCUMENT_TYPE));
        return documentTypeMap;
    }

    public Map<Long,String> populateGeneralContentTypes(){
        File csvData = new File(Config.GENERAL_TYPE_MAPPING_FILE);
        CSVReader<Map<Long,String>> reader = new CSVReader<>();
        CSVParser parser = reader.readFile(csvData);
        parser.forEach(record -> generalDocumentTypeMap = reader.processMapping(generalDocumentTypeMap,record, MappingType.GENERAL_TYPE));
        return generalDocumentTypeMap;
    }

    public List<Long> populateIgnoredTemplateIds(){
        File csvData = new File(Config.NON_RELEVANT_TEMPLATE_IDS_FILE);
        CSVReader<List<Long>> reader = new CSVReader<>();
        CSVParser parser = reader.readFile(csvData);
        parser.forEach(record -> nonRelevantIdList = reader.processMapping(nonRelevantIdList,record, MappingType.TEMPLATE_ID));
        return nonRelevantIdList;
    }
}
