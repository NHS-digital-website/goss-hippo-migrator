package uk.nhs.digital.gossmigrator.misc;

import uk.nhs.digital.gossmigrator.model.goss.GossContent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static uk.nhs.digital.gossmigrator.GossImporter.gossData;

public class ArticleFinder {

    public static List<String> findArticlePathsByArticleId(List<Long> articleId){

        List<String> result = new ArrayList<>();
        for(Long id : articleId){
            List<GossContent> articleList = gossData.getArticlesContentList().stream()
                    .filter(gossContent -> gossContent.getId().equals(id)).collect(Collectors.toList());
            if(articleList != null && !articleList.isEmpty()) {
                result.add(articleList.get(0).getModifiedPath());
            }
        }
        return result;
    }
}
