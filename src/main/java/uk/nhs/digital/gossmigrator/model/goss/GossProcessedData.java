package uk.nhs.digital.gossmigrator.model.goss;

import uk.nhs.digital.gossmigrator.model.goss.enums.GossSourceFile;
import uk.nhs.digital.gossmigrator.model.hippo.HippoImportable;
import uk.nhs.digital.gossmigrator.model.mapping.ContentTypeMap;

import java.util.List;
import java.util.Map;

public class GossProcessedData {

    private GossContentList articlesContentList = new GossContentList();
    private Map<String, Long> publicationSeriesMap;
    private List<HippoImportable> importableContentItems;
    private Map<String, List<String>> taxonomyMap;
    private Map<Long, GossLink> gossLinkMap;
    private ContentTypeMap contentTypeMap;
    private Map<Long, String> generalDocumentTypeMap;
    private List<Long> ignoredTemplateIdsList;
    private GossSourceFile type;

    public GossProcessedData(GossSourceFile type) {
        this.type = type;
    }

    public Map<Long, GossFile> getGossFileMap() {
        return gossFileMap;
    }

    private Map<Long, GossFile> gossFileMap;

    public GossContentList getArticlesContentList() {
        return articlesContentList;
    }

    public void setArticlesContentList(GossContentList articlesContentList) {
        this.articlesContentList.addAll(articlesContentList);
    }

    public Map<String, Long> getPublicationSeriesMap() {
        return publicationSeriesMap;
    }

    public void setPublicationSeriesMap(Map<String, Long> publicationSeriesMap) {
        this.publicationSeriesMap = publicationSeriesMap;
    }

    public List<HippoImportable> getImportableContentItems() {
        return importableContentItems;
    }

    public void setImportableContentItems(List<HippoImportable> importableContentItems) {
        this.importableContentItems = importableContentItems;
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

    public Map<String, List<String>> getTaxonomyMap() {
        return taxonomyMap;
    }

    public void setTaxonomyMap(Map<String, List<String>> taxonomyMap) {
        this.taxonomyMap = taxonomyMap;
    }

    public ContentTypeMap getContentTypeMap() {
        return contentTypeMap;
    }

    public void setContentTypeMap(ContentTypeMap contentTypeMap) {
        this.contentTypeMap = contentTypeMap;
    }

    public Map<Long, String> getGeneralDocumentTypeMap() {
        return generalDocumentTypeMap;
    }

    public void setGeneralDocumentTypeMap(Map<Long, String> generalDocumentTypeMap) {
        this.generalDocumentTypeMap = generalDocumentTypeMap;
    }

    public void addSeriesContentList(GossContentList seriesContentList) {
        articlesContentList.addAll(seriesContentList);
    }

    public List<Long> getIgnoredTemplateIdsList() {
        return ignoredTemplateIdsList;
    }

    public void setIgnoredTemplateIdsList(List<Long> ignoredTemplateIdsList) {
        this.ignoredTemplateIdsList = ignoredTemplateIdsList;
    }

    public GossSourceFile getType() {
        return type;
    }
}

