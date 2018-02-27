package uk.nhs.digital.gossmigrator.model.goss;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GossContentMeta that = (GossContentMeta) o;
        return Objects.equals(getName(), that.getName()) &&
                Objects.equals(getValue(), that.getValue()) &&
                Objects.equals(getGroup(), that.getGroup());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getValue(), getGroup());
    }
}
