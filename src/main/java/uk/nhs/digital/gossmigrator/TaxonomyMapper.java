package uk.nhs.digital.gossmigrator;

import org.apache.commons.csv.CSVParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.digital.gossmigrator.config.Config;
import uk.nhs.digital.gossmigrator.misc.CSVReader;
import uk.nhs.digital.gossmigrator.model.mapping.enums.MappingType;

import java.io.File;
import java.util.*;

import static uk.nhs.digital.gossmigrator.model.mapping.enums.MappingType.TAXONOMY_MAPPING;

public class TaxonomyMapper {
    private final static Logger LOGGER = LoggerFactory.getLogger(TaxonomyMapper.class);
    private Map<String, List<String>> taxonomyMap = new HashMap<>();

    /*
     * Method to generate the taxonomy map from a Csv file
     */
    public Map<String, List<String>> generateTaxonomyMap() {
        File csvData = new File(Config.TAXONOMY_MAPPING_FILE);
        CSVReader<Map<String, List<String>>> reader = new CSVReader<>();
        CSVParser parser = reader.readFile(csvData);
        parser.forEach(record -> taxonomyMap = reader.processMapping(taxonomyMap, record, TAXONOMY_MAPPING));
        return readValidKeysAndValidate(taxonomyMap);
    }

    private Map<String, List<String>> readValidKeysAndValidate(Map<String, List<String>> mappings){
        CSVReader<Set<String>> reader = new CSVReader<>();
        CSVParser parser = reader.readFile(new File(Config.TAXONOMY_KEYS_FILE));
        Set<String> keys = new HashSet<>();
        parser.forEach(r -> reader.processMapping(keys, r, MappingType.VALID_TAXONOMY_KEYS));

        for(List<String> keyset : mappings.values()){
            for(String aKey : keyset) {
                if (!keys.contains(aKey)){
                    LOGGER.warn("Invalid Taxonomy mapping. {} is not a valid hippo key.", aKey);
                }
            }
        }
        return mappings;
    }

}
