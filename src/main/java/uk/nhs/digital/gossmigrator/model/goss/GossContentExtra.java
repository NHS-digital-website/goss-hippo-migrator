package uk.nhs.digital.gossmigrator.model.goss;

public class GossContentExtra {
    // TODO default to what?
    private boolean includeRelatedArticles = false;
    private boolean includeChildArticles = false;

    public boolean isIncludeRelatedArticles() {
        return includeRelatedArticles;
    }

    public void setIncludeRelatedArticles(boolean includeRelatedArticles) {
        this.includeRelatedArticles = includeRelatedArticles;
    }

    public boolean isIncludeChildArticles() {
        return includeChildArticles;
    }

    public void setIncludeChildArticles(boolean includeChildArticles) {
        this.includeChildArticles = includeChildArticles;
    }
}
