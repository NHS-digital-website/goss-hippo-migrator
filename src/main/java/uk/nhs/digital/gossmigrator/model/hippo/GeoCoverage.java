package uk.nhs.digital.gossmigrator.model.hippo;

public class GeoCoverage {

    private boolean england = false;
    private boolean wales = false;
    private boolean scotland = false;
    private boolean northernIreland = false;
    private boolean republicOfIreland = false;

    @SuppressWarnings("unused")
    public boolean isEngland() {
        return england;
    }

    @SuppressWarnings("unused")
    public boolean isWales() {
        return wales;
    }

    @SuppressWarnings("unused")
    public boolean isScotland() {
        return scotland;
    }

    @SuppressWarnings("unused")
    public boolean isNorthernIreland() {
        return northernIreland;
    }

    @SuppressWarnings("unused")
    public boolean isRepublicOfIreland() {
        return republicOfIreland;
    }


    public void setCoverage(String csv){
        csv = csv.replace("\"","");
        String[] coverage = csv.split(",");
        for(String region: coverage){
            checkBoxes(region);
        }
    }

    private void checkBoxes(String region){
        switch(region){
            case "England":
                england = true;
                break;
            case "Wales":
                wales = true;
                break;
            case "Scotland":
                scotland = true;
                break;
            case "Northern Ireland":
                northernIreland = true;
                break;
            case "Republic of Ireland":
                republicOfIreland = true;
                break;
        }
    }

}
