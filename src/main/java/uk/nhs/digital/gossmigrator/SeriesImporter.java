package uk.nhs.digital.gossmigrator;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.digital.gossmigrator.config.Config;
import uk.nhs.digital.gossmigrator.model.goss.GossContent;
import uk.nhs.digital.gossmigrator.model.goss.GossContentList;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.Charset.defaultCharset;

public class SeriesImporter {

    private final static Logger LOGGER = LoggerFactory.getLogger(SeriesImporter.class);

    Map<Long, Long> publicationSeriesMap = new HashMap<>();
    GossContentList gossContentList = new GossContentList();

    /**
     * Publication Series is not a concept covered in the Goss export.
     * So Will be provided with an input csv file for the migration.
     * Need to create a Series for each unique series and then a/some publication(s) as children.
     * Will need to fill in the parent ids in the publications, so store a map.
     */
    public GossContentList createPublicationSeries() {
        File csvData = new File(Config.SERIES_FILE);
        CSVParser parser;
        try {
            parser = CSVParser.parse(csvData, defaultCharset(), CSVFormat.RFC4180);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        parser.forEach(this::processSeriesLine);
        return gossContentList;
    }

    private void processSeriesLine(CSVRecord strings) {
        if (strings.getRecordNumber() > Config.SERIES_FILE_HEADER_COUNT) {
            gossContentList.add(new GossContent(strings));
        }
    }


    /**
     * Store mappings between Publications and Series in a Map.
     * Use publication Goss Id as key.  Map will be used by
     * publications to get their parent.
     */
    public Map<Long, Long> readPublicationSeriesMappings() {
        File csvData = new File(Config.SERIES_PUBLICATION_MAPPING_FILE);
        CSVParser parser;
        try {
            // TODO utf-8 charset?  Probably does not matter as expecting only ASCII chars.
            parser = CSVParser.parse(csvData, defaultCharset(), CSVFormat.RFC4180);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        parser.forEach(this::processPublicationSeriesMapping);
        return publicationSeriesMap;
    }

    public void processPublicationSeriesMapping(CSVRecord record) {
        if(record.getRecordNumber() > Config.SERIES_PUBLICATION_MAPPING_FILE_HEADER_COUNT) {
            if (record.size() != 2) {
                LOGGER.error("Invalid PublicationSeries mapping. Expected 2 columns, got {}. Data:{}", record.size(), record);
            }

            publicationSeriesMap.put(Long.parseLong(StringUtils.trim(record.get(1)))
                    , -1L * Long.parseLong(StringUtils.trim(record.get(0))));
        }

    }


    public GossContentList getGossContentList() {
        return gossContentList;
    }

    public void setGossContentList(GossContentList gossContentList) {
        this.gossContentList = gossContentList;
    }
}
