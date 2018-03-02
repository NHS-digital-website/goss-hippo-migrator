package uk.nhs.digital.gossmigrator.misc;

import uk.nhs.digital.gossmigrator.model.goss.GossContentList;
import uk.nhs.digital.gossmigrator.model.goss.enums.ContentType;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static uk.nhs.digital.gossmigrator.GossImporter.gossData;
import static uk.nhs.digital.gossmigrator.model.goss.enums.ContentType.SERIES;

public class GossContentFilter {

    public static GossContentList setRelevantGossContentFlag(GossContentList gossContentList){
        List<Long> ignoredIds = gossData.getIgnoredTemplateIdsList();
        Date today = Calendar.getInstance().getTime();

        gossContentList.stream().filter(
                item ->
                        (!ignoredIds.contains(item.getTemplateId())
                        && "on".equals(item.getDisplay())
                        && (item.getDisplayEndDate() != null && item.getDisplayEndDate().after(today)))
                || item.getContentType() == SERIES)

                .forEach(item -> item.setRelevantContentFlag(true));

        return gossContentList;
    }
}