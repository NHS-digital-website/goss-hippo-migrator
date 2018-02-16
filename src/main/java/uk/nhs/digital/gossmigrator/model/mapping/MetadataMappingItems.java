package uk.nhs.digital.gossmigrator.model.mapping;

import org.apache.commons.csv.CSVParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.digital.gossmigrator.config.Config;
import uk.nhs.digital.gossmigrator.misc.CSVReader;
import uk.nhs.digital.gossmigrator.model.goss.GossContentMeta;

import java.io.File;
import java.util.HashMap;

import static uk.nhs.digital.gossmigrator.model.mapping.enums.MappingType.METADATA_MAPPING;

public class MetadataMappingItems extends HashMap<String, MetadataMappingItem> {
    private final static Logger LOGGER = LoggerFactory.getLogger(MetadataMappingItems.class);
    private boolean isRead = false;

    private void readMetadataMapping() {
        File csvData = new File(Config.METADATA_MAPPING_FILE);
        CSVReader<MetadataMappingItem> reader = new CSVReader<>();
        CSVParser parser = reader.readFile(csvData);
        parser.forEach(record -> {
                    MetadataMappingItem item = new MetadataMappingItem();
                    item = reader.processMapping(item, record, METADATA_MAPPING);
                    put(getKey(item.getGossGroup(), item.getGossValue()), item);
                }
        );
        isRead = true;
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
        if (null == get(getKey(gossContentMeta.getGroup(), gossContentMeta.getValue()))) {
            LOGGER.error("No hippo static data mapping for goss values:Group:{}, Value:{}",
                    gossContentMeta.getGroup(), gossContentMeta.getValue());
            return "";
        }
        return get(getKey(gossContentMeta.getGroup(), gossContentMeta.getValue())).getHippoValue();
    }
}
