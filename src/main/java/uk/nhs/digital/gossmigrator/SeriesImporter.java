package uk.nhs.digital.gossmigrator;

import org.apache.commons.csv.CSVParser;
import uk.nhs.digital.gossmigrator.config.Config;
import uk.nhs.digital.gossmigrator.misc.CSVReader;
import uk.nhs.digital.gossmigrator.model.goss.GossContentList;

import java.io.File;

import static uk.nhs.digital.gossmigrator.model.mapping.enums.MappingType.SERIES_ITEM;

public class SeriesImporter {

    private GossContentList gossContentList = new GossContentList();

    /**
     * Publication Series is not a concept covered in the Goss export.
     * So Will be provided with an input csv file for the migration.
     * Need to create a Series for each unique series and then a/some publication(s) as children.
     * Will need to fill in the parent ids in the publications, so store a map.
     */
    public GossContentList createPublicationSeries() {
        File csvData = new File(Config.SERIES_FILE);
        CSVReader<GossContentList> reader = new CSVReader<>();
        CSVParser parser = reader.readFile(csvData);
        parser.forEach(record -> gossContentList = reader.processMapping(gossContentList,record, SERIES_ITEM));
        return gossContentList;
    }

}
