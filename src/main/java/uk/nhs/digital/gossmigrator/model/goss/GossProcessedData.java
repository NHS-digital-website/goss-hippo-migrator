package uk.nhs.digital.gossmigrator.model.goss;

import uk.nhs.digital.gossmigrator.model.hippo.HippoImportable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GossProcessedData {

    private GossContentList seriesContentList;
    private GossContentList articlesContentList;
    private Map<Long, Long> publicationSeriesMap;
    private List<HippoImportable> importableContentItems;
    private Map<Long, String> gossContentUrlMap;
    private Map<String, String> taxonomyMap;

    public GossContentList getSeriesContentList() {
        return seriesContentList;
    }

    public void setSeriesContentList(GossContentList seriesContentList) {
        this.seriesContentList = seriesContentList;
    }

    public GossContentList getArticlesContentList() {
        return articlesContentList;
    }

    public void setArticlesContentList(GossContentList articlesContentList) {
        this.articlesContentList = articlesContentList;
    }

    public Map<Long, Long> getPublicationSeriesMap() {
        return publicationSeriesMap;
    }

    public void setPublicationSeriesMap(Map<Long, Long> publicationSeriesMap) {
        this.publicationSeriesMap = publicationSeriesMap;
    }

    public List<HippoImportable> getImportableContentItems() {
        return importableContentItems;
    }

    public void setImportableContentItems(List<HippoImportable> importableContentItems) {
        this.importableContentItems = importableContentItems;
    }

    public Map<Long, String> getGossContentUrlMap() {
        return gossContentUrlMap;
    }

    public void setGossContentUrlMap(Map<Long, String> gossContentUrlMap) {
        this.gossContentUrlMap = gossContentUrlMap;
    }

    public Map<String, String> getTaxonomyMap() {
        return taxonomyMap;
    }

    public void setTaxonomyMap(Map<String, String> taxonomyMap) {
        this.taxonomyMap = taxonomyMap;
    }
}
