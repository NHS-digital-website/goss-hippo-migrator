package uk.nhs.digital.gossmigrator.model.hippo;

public class Redirect extends HippoImportable{

    private String ruleFrom;
    private String ruletTo;

    //TODO extract redirects from Content to create Redirect hippo objects
    public Redirect(String localizedName, String jcrPath, String jcrNodeName, String ruleFrom, String ruletTo) {
        super(localizedName, jcrPath, jcrNodeName);
        this.ruleFrom = ruleFrom;
        this.ruletTo = ruletTo;
    }

    public String getRuleFrom() {
        return ruleFrom;
    }

    public String getRuletTo() {
        return ruletTo;
    }
}
