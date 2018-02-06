package uk.nhs.digital.gossmigrator.model.mapping;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.digital.gossmigrator.config.Config;
import uk.nhs.digital.gossmigrator.model.goss.GossContentMeta;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static java.nio.charset.Charset.defaultCharset;

public class MetadataMappingItems extends HashMap<String, MetadataMappingItem> {
    private final static Logger LOGGER = LoggerFactory.getLogger(MetadataMappingItems.class);
    private boolean isRead = false;

    private void readMetadataMapping() {
        File csvData = new File(Config.METADATA_MAPPING_FILE);
        CSVParser parser;
        try {
            // TODO utf-8 charset?  Probably does not matter as expecting only ASCII chars.
            parser = CSVParser.parse(csvData, defaultCharset(), CSVFormat.RFC4180);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        parser.forEach(this::processMetadataMapping);
    }

    private void processMetadataMapping(CSVRecord strings) {
        if (strings.size() != 3) {
            LOGGER.error("Invalid csv mapping length {}, expected {} at line no: {}. Line data:{}."
                    , strings.size(), 3, strings.getRecordNumber(), strings.toString());
        }
        String group = strings.get(0);
        String gossValue = strings.get(1);
        String hippoValue = strings.get(2);
        put(getKey(group, gossValue), new MetadataMappingItem(group, gossValue, hippoValue));
    }

    private String getKey(String group, String value) {
        return group + value;
    }

    @Override
    public MetadataMappingItem get(Object o) {
        if (!isRead) {
            readMetadataMapping();
        }
        return super.get(o);
    }

    public String getHippoValue(GossContentMeta gossContentMeta) {
        return get(getKey(gossContentMeta.getGroup(), gossContentMeta.getValue())).getHippoValue();
    }
}
