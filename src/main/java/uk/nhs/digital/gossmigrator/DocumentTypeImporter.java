package uk.nhs.digital.gossmigrator;

import org.apache.commons.csv.CSVParser;
import uk.nhs.digital.gossmigrator.config.Config;
import uk.nhs.digital.gossmigrator.misc.CSVReader;
import uk.nhs.digital.gossmigrator.model.goss.enums.ContentType;
import uk.nhs.digital.gossmigrator.model.mapping.enums.MappingType;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class DocumentTypeImporter {

    private Map<Long,ContentType> documentTypeMap = new HashMap<>();

    public Map<Long,ContentType> populateContentTypes(){
        File csvData = new File(Config.DOCUMENT_TYPE_FILE);
        CSVReader<Map<Long,ContentType>> reader = new CSVReader<>();
        CSVParser parser = reader.readFile(csvData);
        parser.forEach(record -> documentTypeMap = reader.processMapping(documentTypeMap,record, MappingType.DOCUMENT_TYPE));
        return documentTypeMap;
    }
}
