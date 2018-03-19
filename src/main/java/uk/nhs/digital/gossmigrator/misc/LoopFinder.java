package uk.nhs.digital.gossmigrator.misc;

import uk.nhs.digital.gossmigrator.Report.WarningsReportWriter;
import uk.nhs.digital.gossmigrator.model.goss.GossProcessedData;
import uk.nhs.digital.gossmigrator.model.hippo.HippoImportable;
import uk.nhs.digital.gossmigrator.model.hippo.Redirect;

import java.util.ArrayList;
import java.util.List;

public class LoopFinder {


    public static GossProcessedData removeRedirectLoops(GossProcessedData data){

        List<HippoImportable> importables = data.getImportableContentItems();
        List<HippoImportable> filteredImportables =  new ArrayList<>();
        importables.forEach(item -> {
            if (item instanceof Redirect && hasLoop((Redirect)item)){
                WarningsReportWriter.addWarningRow("Redirect", item.getId(),((Redirect) item).getRuleTo(), "Loop Found");
            }else{
                filteredImportables.add(item);
            }
        });

        data.setImportableContentItems(filteredImportables);
        return data;
    }

    private static boolean hasLoop(Redirect first) {

        if(first == null) // list does not exist..so no loop either
            return false;

        Redirect slow, fast; // create two references.

        slow = fast = first; // make both refer to the start of the list

        while(true) {

            slow = slow.getNext();          // 1 hop

            if(fast.getNext() != null)
                fast = fast.getNext().getNext(); // 2 hops
            else
                return false;          // next node null => no loop

            if(slow == null || fast == null) // if either hits null..no loop
                return false;

            if(slow.equals(fast)) // if the two ever meet...we must have a loop
                return true;
        }
    }

}
