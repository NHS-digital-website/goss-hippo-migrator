package uk.nhs.digital.gossmigrator;

import org.apache.commons.csv.CSVParser;
import uk.nhs.digital.gossmigrator.config.Config;
import uk.nhs.digital.gossmigrator.misc.CSVReader;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static uk.nhs.digital.gossmigrator.model.mapping.enums.MappingType.PUBLICATION_SERIES_MAPPING;

public class PublicationSeriesMapper {

    private Map<Long, Long> publicationSeriesMap = new HashMap<>();

    /**
     * Store mappings between Publications and Series in a Map.
     * Use publication Goss Id as key.  Map will be used by
     * publications to get their parent.
     */
    public Map<Long, Long> readPublicationSeriesMappings() {
        File csvData = new File(Config.SERIES_PUBLICATION_MAPPING_FILE);
        CSVReader<Map<Long, Long>> reader = new CSVReader<>();
        CSVParser parser = reader.readFile(csvData);
        parser.forEach(record -> publicationSeriesMap =
                reader.processMapping(publicationSeriesMap, record, PUBLICATION_SERIES_MAPPING));
        return publicationSeriesMap;
    }
}