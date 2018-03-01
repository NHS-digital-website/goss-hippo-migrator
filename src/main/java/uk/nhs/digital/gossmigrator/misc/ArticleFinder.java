package uk.nhs.digital.gossmigrator.misc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.digital.gossmigrator.model.goss.GossContent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static uk.nhs.digital.gossmigrator.GossImporter.gossData;

public class ArticleFinder {
    private final static Logger LOGGER = LoggerFactory.getLogger(ArticleFinder.class);

    public static List<String> findArticlePathsByArticleId(List<Long> articleId, String context, Long parentId) {

        List<String> result = new ArrayList<>();
        for (Long id : articleId) {
            GossContent article = gossData.getArticlesContentList().getById(id);
            if (null == article) {
                LOGGER.error("Could not find article, id:{}, Context{}, Processing article:{}", id, context, parentId);
            } else {
                result.add(article.getJcrPath());
            }
        }
        return result;
    }
}
