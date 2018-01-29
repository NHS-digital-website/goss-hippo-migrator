package uk.nhs.digital.gossmigrator.model.hippo;

public class Section {
    private String title;
    private String type;
    private HippoRichText content;

    public Section(String title, String type, HippoRichText content) {
        this.title = title;
        this.type = type;
        this.content = content;
    }

    @SuppressWarnings("unused") // Used in template
    public String getTitle() {
        return title;
    }

    @SuppressWarnings("unused") // Used in template
    public String getType() {
        return type;
    }

    public HippoRichText getContent() {
        return content;
    }
}
