package uk.nhs.digital.gossmigrator.model.goss;

public class GossContentMeta {
    private String name;
    private String value;
    private String group;

    public GossContentMeta(String name, String value, String group) {
        this.name = name;
        this.value = value;
        this.group = group;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getGroup() {
        return group;
    }
}
