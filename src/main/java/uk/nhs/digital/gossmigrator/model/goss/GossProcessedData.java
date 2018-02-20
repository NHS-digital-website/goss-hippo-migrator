package uk.nhs.digital.gossmigrator.model.goss;

import uk.nhs.digital.gossmigrator.model.goss.enums.ContentType;
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
    private Map<Long,GossLink> gossLinkMap;
    private Map<Long, ContentType> contentTypeMap;

    public Map<Long, GossFile> getGossFileMap() {
        return gossFileMap;
    }

    private Map<Long, GossFile> gossFileMap;

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

    public Map<Long, GossLink> getGossLinkMap() {
        return gossLinkMap;
    }

    public void setGossLinkMap(Map<Long, GossLink> gossLinkMap) {
        this.gossLinkMap = gossLinkMap;
    }

    public void setGossFileMap(Map<Long, GossFile> gossFileMap) {
        this.gossFileMap = gossFileMap;
    }

    public Map<String, String> getTaxonomyMap() {
        return taxonomyMap;
    }

    public void setTaxonomyMap(Map<String, String> taxonomyMap) {
        this.taxonomyMap = taxonomyMap;
    }

    public Map<Long, ContentType> getContentTypeMap() {
        return contentTypeMap;
    }

    public void setContentTypeMap(Map<Long, ContentType> contentTypeMap) {
        this.contentTypeMap = contentTypeMap;
    }
}
