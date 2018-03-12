package uk.nhs.digital.gossmigrator.misc;

import uk.nhs.digital.gossmigrator.model.goss.GossContentList;
import uk.nhs.digital.gossmigrator.model.goss.GossProcessedData;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static uk.nhs.digital.gossmigrator.GossImporter.digitalData;
import static uk.nhs.digital.gossmigrator.model.goss.enums.ContentType.SERIES;
import static uk.nhs.digital.gossmigrator.model.goss.enums.GossSourceFile.CONTENT;
import static uk.nhs.digital.gossmigrator.model.goss.enums.GossSourceFile.DIGITAL;

public class GossContentFilter {

    public static GossContentList setRelevantGossContentFlag(GossProcessedData data, GossContentList gossContentList){

        Date today = Calendar.getInstance().getTime();

        if(DIGITAL.equals(data.getType())){
            List<Long> ignoredIds = digitalData.getIgnoredTemplateIdsList();
            gossContentList.stream().filter(
                    item ->
                            (!ignoredIds.contains(item.getTemplateId())
                                    && "on".equals(item.getDisplay())
                                    && (item.getDisplayEndDate() != null && item.getDisplayEndDate().after(today)))
                                    || item.getContentType() == SERIES)
                    .forEach(item -> item.setRelevantContentFlag(true));

        }else if(CONTENT.equals(data.getType())){
            gossContentList.stream().filter(
                    item ->
                            ("on".equals(item.getDisplay())
                                    && (item.getDisplayEndDate() != null && item.getDisplayEndDate().after(today))))
                    .forEach(item -> item.setRelevantContentFlag(true));
        }

        return gossContentList;
    }
}