package uk.nhs.digital.gossmigrator.misc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GeoCoverageHelper {

    private Set<String> regions = new HashSet<>();

    public List<String> getGeoCoverageList(String csv) {
        csv = csv.replace("\"", "");
        String[] coverage = csv.split(", ");
        for (String region : coverage) {
            checkBoxes(region);
        }
        return new ArrayList<>(regions);
    }

    private void checkBoxes(String region) {
        if (region != null && !region.isEmpty()) {
            switch (region) {
                case "England":
                    regions.add("England");
                    break;
                case "England and Scotland":
                    regions.add("England");
                    regions.add("Scotland");
                    break;
                case "England and Wales":
                    regions.add("England");
                    regions.add("Wales");
                    break;
                case "England Wales and Northern Ireland":
                    regions.add("England");
                    regions.add("Wales");
                    regions.add("Northern Ireland");
                    break;
                case "Great Britain":
                    regions.add("England");
                    regions.add("Wales");
                    regions.add("Scotland");
                    break;
                case "International":
                    regions.add("Republic of Ireland");
                    break;
                case "Northern Ireland":
                    regions.add("Northern Ireland");
                    break;
                case "Scotland":
                    regions.add("Scotland");
                    break;
                case "UK":
                    regions.add("England");
                    regions.add("Wales");
                    regions.add("Scotland");
                    regions.add("Northern Ireland");
                    break;
                case "Wales":
                    regions.add("Wales");
                    break;
            }
        }
    }
}
