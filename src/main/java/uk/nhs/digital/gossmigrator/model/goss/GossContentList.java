package uk.nhs.digital.gossmigrator.model.goss;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.digital.gossmigrator.config.Config;

import java.util.*;

public class GossContentList extends ArrayList<GossContent> {
    private final static Logger LOGGER = LoggerFactory.getLogger(GossContentList.class);

    private Map<Long, GossContent> contentMetaMap;
    private Stack<Long> stack;
    private boolean sorted = false;

    public void generateJcrStructure() {
        if (sorted) {
            LOGGER.warn("Collection already sorted once.  Don't sort again!");
        } else {

            contentMetaMap = new HashMap<>();
            for (GossContent p : this) {
                contentMetaMap.put(p.getId(), p);
            }

            this.stream().filter(p -> p.getDepth() == null).forEach(p -> {
                stack = new Stack<>();
                calculateDepth(p);
            });

            for (GossContent p : this) {
                p.setDepth(contentMetaMap.get(p.getId()).getDepth());
            }
            Collections.sort(this);
        }
        sorted = true;

        for (GossContent i : this) {
            LOGGER.info("Goss Id:{}, Parent:{}, Type:{}, Children Count:{}", i.getId(), i.getParentId()
                    , i.getContentType(), i.getChildren().size());
        }
    }

    private void calculateDepth(GossContent p) {
        stack.push(p.getId());
        // Check for possible circular dependency and output something useful rather than stack overflow.
        // Pick 30 levels of children.  Should be more than enough.
        if (stack.size() > 30) {
            LOGGER.error("Circular dependency");
            StringBuilder errorText = new StringBuilder();
            while (!stack.empty()) {
                errorText.append(stack.pop()).append(" : ");
            }
            LOGGER.error(errorText.toString());
            throw new RuntimeException("Circular parent/child dependency:" + errorText.toString());
        }

        // Already calculated as is dependency of another node
        if (p.getDepth() != null) {
            return;
        }

        // No parent.  Must be a root node.
        if (p.getParentId() == null || p.getParentId().intValue() == p.getId()) {
            p.setDepth(1);
            p.setJcrParentPath(Config.JCR_SERVICE_DOC_ROOT);
            return;
        }

        // Not yet calculated and has parent.
        GossContent p1 = contentMetaMap.get(p.getParentId());

        if (null == p1) {
            // p1 should never be null with real data.
            if (p.getParentId() != 0) {
                // Home document has parent id 0 in extract.
                LOGGER.error("Invalid article parent id:{} for article:{}", p.getParentId(), p.getId());
            }
            p.setDepth(1);
            p.setJcrParentPath(Config.JCR_SERVICE_DOC_ROOT);
            return;
        } else if (null == p1.getDepth()) {
            calculateDepth(p1);
        }

        p.setDepth(p1.getDepth() + 1);
        p1.addChild(p.getId());
        p.setJcrParentPath(p1.getJcrPath() + "/");
    }

    /**
     * Get all articles that match at least One of the meta data terms supplied.
     *
     * @param matchMetaList List to match to.
     * @param excludeId     Don't include this article (probably the list page calling this)
     * @return
     */
    public Set<Long> getArticlesMatchingMeta(List<GossContentMeta> matchMetaList, Long excludeId) {
        Set<Long> includeList = new HashSet<>();

        for (GossContent article : this) {
            if (article.getId() == excludeId) {
                continue;
            }
            for (GossContentMeta matchTo : matchMetaList) {
                if (article.getMetaList().contains(matchTo)) {
                    includeList.add(article.id);
                    break;
                }
            }
        }
        return includeList;
    }

}
