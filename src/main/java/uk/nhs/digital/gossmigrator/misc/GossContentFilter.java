package uk.nhs.digital.gossmigrator.misc;

import uk.nhs.digital.gossmigrator.model.goss.GossContentList;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static uk.nhs.digital.gossmigrator.GossImporter.gossData;

public class GossContentFilter {

    public static GossContentList setRelevantGossContentFlag(GossContentList gossContentList){
        List<Long> ignoredIds = gossData.getIgnoredTemplateIdsList();
        Date today = Calendar.getInstance().getTime();

        gossContentList.stream().filter(
                item ->
                        !ignoredIds.contains(item.getTemplateId())
                        && "on".equals(item.getDisplay())
                        && (item.getDisplayEndDate() != null && item.getDisplayEndDate().after(today)))

                .forEach(item -> item.setRelevantContentFlag(true));

        return gossContentList;
    }
}