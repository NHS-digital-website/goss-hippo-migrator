package uk.nhs.digital.gossmigrator.model.mapping;

import org.apache.commons.csv.CSVParser;
import uk.nhs.digital.gossmigrator.config.Config;
import uk.nhs.digital.gossmigrator.misc.CSVReader;
import uk.nhs.digital.gossmigrator.model.goss.GossContentMeta;

import java.io.File;
import java.util.HashMap;

import static uk.nhs.digital.gossmigrator.model.mapping.enums.MappingType.METADATA_MAPPING;

public class MetadataMappingItems extends HashMap<String, MetadataMappingItem> {

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
        return get(getKey(gossContentMeta.getGroup(), gossContentMeta.getValue())).getHippoValue();
    }
}
