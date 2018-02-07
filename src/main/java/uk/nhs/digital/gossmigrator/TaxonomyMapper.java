package uk.nhs.digital.gossmigrator;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.digital.gossmigrator.config.Config;
import uk.nhs.digital.gossmigrator.model.hippo.Publication;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.Charset.defaultCharset;

public class TaxonomyMapper {

    private final static Logger LOGGER = LoggerFactory.getLogger(Publication.class);

    private Map<String, String> taxonomyMap = new HashMap<String, String>();

    public Map<String, String> generateTaxonomyMap() {
        File csvData = new File(Config.TAXONOMY_MAPPING_FILE);
        CSVParser parser;
        try {
            // TODO utf-8 charset?  Probably does not matter as expecting only ASCII chars.
            parser = CSVParser.parse(csvData, defaultCharset(), CSVFormat.RFC4180);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        parser.forEach(this::processTaxonomyMapping);
        return taxonomyMap;
    }

    public void processTaxonomyMapping(CSVRecord record) {

        if (record.size() != 2) {
                LOGGER.error("Invalid Taxonomy mapping. Expected 2 columns, got {}. Data:{}", record.size(), record);
            }

            taxonomyMap.put(StringUtils.trim(record.get(0))
                    , StringUtils.trim(record.get(1)));
        }
}
