package uk.nhs.digital.gossmigrator;

import org.apache.commons.csv.CSVParser;
import uk.nhs.digital.gossmigrator.config.Config;
import uk.nhs.digital.gossmigrator.misc.CSVReader;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static uk.nhs.digital.gossmigrator.model.mapping.enums.MappingType.TAXONOMY_MAPPING;

public class TaxonomyMapper {

    private Map<String, String> taxonomyMap = new HashMap<>();

    /*
     * Method to generate the taxonomy map from a Csv file     *
     */
    public Map<String, String> generateTaxonomyMap() {
        File csvData = new File(Config.TAXONOMY_MAPPING_FILE);
        CSVReader<Map<String, String>> reader = new CSVReader<>();
        CSVParser parser = reader.readFile(csvData);
        parser.forEach(record -> taxonomyMap = reader.processMapping(taxonomyMap, record, TAXONOMY_MAPPING));
        return taxonomyMap;
    }

}
