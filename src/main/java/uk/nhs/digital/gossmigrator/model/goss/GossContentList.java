package uk.nhs.digital.gossmigrator.model.goss;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.digital.gossmigrator.GossImporter;
import uk.nhs.digital.gossmigrator.config.Config;
import uk.nhs.digital.gossmigrator.misc.TextHelper;
import uk.nhs.digital.gossmigrator.model.goss.enums.ContentType;

import java.nio.file.Paths;
import java.util.*;

import static uk.nhs.digital.gossmigrator.GossImporter.digitalData;
import static uk.nhs.digital.gossmigrator.config.Config.JCR_GENERAL_ROOT;
import static uk.nhs.digital.gossmigrator.config.Config.JCR_SERVICE_DOC_ROOT;
import static uk.nhs.digital.gossmigrator.model.goss.enums.ContentType.*;

public class GossContentList extends ArrayList<GossContent> {
    private final static Logger LOGGER = LoggerFactory.getLogger(GossContentList.class);

    private Map<Long, GossContent> contentMetaMap = new HashMap<>();
    private Stack<Long> stack;
    private boolean sorted = false;

    public GossContent getById(Long id) {
        return contentMetaMap.get(id);
    }

    @Override
    public boolean add(GossContent gossContent) {
        contentMetaMap.put(gossContent.getId(), gossContent);
        return super.add(gossContent);
    }

    @Override
    public boolean addAll(Collection<? extends GossContent> collection) {
        for (GossContent a : collection) {
            contentMetaMap.put(a.getId(), a);
        }
        return super.addAll(collection);
    }

    public void generateJcrStructure() {
        if (sorted) {
            LOGGER.warn("Collection already sorted once.  Don't sort again!");
        } else {

            // 1 Any publications get linked to their mapped series.
            linkPublications();
            // 2 Any services without another service in their ancestors get mapped to root services node
            moveServices();
            // 3 Anything else links to its parent, need to know if we have children though..
            populateChildren();
            // 4 Populate the anything else items from 3.
            this.stream().filter(p -> p.getDepth() == null).forEach(p -> {
                stack = new Stack<>();
                generateJcrStructure(p);
            });

            createFolders();
            Collections.sort(this);
        }
        sorted = true;
        if (Config.PRINT_JCR_STRUCTURE && GossImporter.processingDigital) {
            printJcrStructure();
        }
    }

    private void createFolders() {
        if(!GossImporter.processingDigital){
            return;
        }

        Set<GossFolder> folders = new HashSet<>();
        for (GossContent content : this) {
            if (content.getDepth() > 0
                    && (content.getChildren().size() > 0 || content.getContentType() == PUBLICATION)) {
                GossFolder folder = new GossFolder(content);
                folders.add(folder);
            }
        }
        // Use super method, don't want these adding to map.
        super.addAll(folders);
    }

    /**
     * Link all publications to their series.
     */
    private void linkPublications() {
        for (GossContent p : this) {
            if (p.getContentType() == PUBLICATION) {
                // Parent should be a series
                String pubId = p.getExtra().getPublicationId();
                Long seriesId = digitalData.getPublicationSeriesMap().get(pubId);
                if (seriesId != null) {
                    p.setParentId(seriesId);

                } else {
                    if(p.isRelevantContentFlag()) {
                        LOGGER.warn("Publication:{}, {} has no series mapped.", p.getId(), pubId);
                        p.getWarnings().add("Publication:" + p.getId() + ", " + pubId + " has no series mapped.");
                    }
                    p.setJcrParentPath(Config.JCR_PUBLICATION_ROOT);
                    p.setDepth(1);
                }
            }
        }
    }

    private void moveServices() {
        for (GossContent p : this) {
            // Put all Services under service node.
            // If descendant of another service then leave it be relative to the other service
            if (p.getContentType() == ContentType.SERVICE) {
                if (!hasParentService(p)) {
                    p.setJcrParentPath(Paths.get(JCR_SERVICE_DOC_ROOT, p.jcrNodeName).toString());
                    p.setJcrNodeName("content");
                    p.setDepth(1);
                    p.setParentId(p.id);
                } else {
                    LOGGER.warn("Unexpected nested services, Article {} has a service as an ancestor.", p.id);
                }
            }
        }
    }

    private boolean hasParentService(GossContent c) {
        if (c.id.equals(c.parentId)) {
            return false;
        }
        GossContent parent = getById(c.getParentId());
        return null != parent && (parent.contentType == SERVICE || hasParentService(parent));
    }

    private void populateChildren() {
        for (GossContent c : this) {
            if (c.id.equals(c.parentId)) {
                continue;
            }
            GossContent parent = getById(c.parentId);
            if (null != parent) {
                parent.addChild(c.getId());
            }
        }
    }

    private void setJcrValuesBasedUponChildren(String parentPath, GossContent item) {
        if (item.getChildren().size() > 0 || item.getContentType() == PUBLICATION) {
            item.setJcrParentPath(Paths.get(parentPath, item.jcrNodeName).toString());
            item.setJcrNodeName("content");
        } else {
            item.setJcrParentPath(parentPath);
        }
    }

    private void generateJcrStructure(GossContent p) {
        stack.push(p.getId());
        // Check for possible circular dependency and output something useful rather than stack overflow.
        // Pick 30 levels of children.  Should be more than enough.
        if (stack.size() > 30) {
            throw new RuntimeException("Circular parent/child dependency");
        }

        // Already calculated as must have other children.
        if (p.getDepth() != null) {
            return;
        }

        if (p.id.equals(p.getParentId())) {
            // It's a root node for it's type.
            if (p.getContentType() != SERVICE && p.getContentType() != SERIES) {
                setJcrValuesBasedUponChildren(JCR_GENERAL_ROOT, p);
                p.setDepth(1);
                return;
            } else {
                LOGGER.error("Article:{}, has no parent.", p.id);
            }
        }

        // The old home page
        if (p.getParentId().equals(0L)) {
            p.setJcrParentPath(JCR_GENERAL_ROOT);
            p.setDepth(0);
            return;
        }

        GossContent parent = getById(p.getParentId());

        if (null == parent) {
            // p1 should never be null with real data.
            if(GossImporter.processingDigital) {
                LOGGER.error("Invalid article parent id:{} for article:{}", p.getParentId(), p.getId());
            }
            p.setDepth(0);
            p.setJcrParentPath(JCR_GENERAL_ROOT);
            return;
        }

        if (null == parent.getDepth()) {
            // Work out the parent path first.
            generateJcrStructure(parent);
        }

        if (parent.getDepth().equals(0)) {
            // Parent is home page.  Lose the home page path and treat this as a root node.
            p.setDepth(1);
            setJcrValuesBasedUponChildren(JCR_GENERAL_ROOT, p);
        } else {
            // It's a child
            p.setDepth(parent.getDepth() + 1);
            setJcrValuesBasedUponChildren(parent.jcrParentPath, p);
        }
    }

    private void printJcrStructure() {
        StringBuilder output = new StringBuilder();
        String lastNodeCategory = "";
        for (GossContent p : this) {
            if (p.getDepth().equals(0)) {
                output.append("content|documents|corporate-website").append("\n");
            } else if (p.getDepth().equals(1)) {
                if (p.getContentType() == ContentType.SERIES) {
                    if (!lastNodeCategory.equalsIgnoreCase(ContentType.SERIES.name())) {
                        output.append("\t|->STATISTICAL PUBLICATIONS ROOT").append("\n");
                        lastNodeCategory = ContentType.SERIES.name();
                    }
                } else if (p.getContentType() == ContentType.SERVICE) {
                    if (!lastNodeCategory.equalsIgnoreCase(ContentType.SERVICE.name())) {
                        output.append("\t|->SERVICES ROOT").append("\n");
                        lastNodeCategory = ContentType.SERVICE.name();
                    }
                } else {
                    if (!lastNodeCategory.equalsIgnoreCase(ContentType.GENERAL.name())) {
                        output.append("\t|->GENERAL ROOT").append("\n");
                        lastNodeCategory = ContentType.GENERAL.name();
                    }
                }
                if ("content".equalsIgnoreCase(p.jcrNodeName)) {
                    output.append("\t\t").append(TextHelper.toLowerCaseDashedValue(p.heading))
                            .append("[").append(p.contentType.name()).append("][").append(p.id).append("]")
                            .append("[exported=").append(p.isRelevantContentFlag()).append("]\n");
                    output.append("\t\t\t|->").append(p.jcrNodeName)
                            .append("[").append(p.contentType.name()).append("][").append(p.id).append("]")
                            .append("[exported=").append(p.isRelevantContentFlag()).append("]\n");
                    printChildren(p, output, 1);
                } else {
                    output.append("\t\t").append(p.jcrNodeName)
                            .append("[").append(p.contentType.name()).append("][").append(p.id).append("]")
                            .append("[exported=").append(p.isRelevantContentFlag()).append("]\n");
                }

            }
        }
        System.out.print(output.toString());
    }

    private void printChildren(GossContent parent, StringBuilder output, int extraTabs) {
        for (Long childId : parent.getChildren()) {
            GossContent child = contentMetaMap.get(childId);
            int extra = extraTabs + child.getDepth();
            int nextExtraTabs = extraTabs;

            if ("content".equalsIgnoreCase(child.jcrNodeName)) {
                nextExtraTabs++;
                output.append(StringUtils.repeat("\t", extra)).append("|->")
                        .append(TextHelper.toLowerCaseDashedValue(child.heading))
                        .append("[").append(child.contentType.name()).append("][").append(child.id).append("]")
                        .append("[exported=").append(child.isRelevantContentFlag()).append("]\n");
                output.append(StringUtils.repeat("\t", ++extra)).append("|->").append(child.jcrNodeName)
                        .append("[").append(child.contentType.name()).append("][").append(child.id).append("]")
                        .append("[exported=").append(child.isRelevantContentFlag()).append("]\n");
                printChildren(child, output, nextExtraTabs);
            } else {
                output.append(StringUtils.repeat("\t", extra)).append(child.jcrNodeName)
                        .append("[").append(child.contentType.name()).append("][").append(child.id).append("]")
                        .append("[exported=").append(child.isRelevantContentFlag()).append("]\n");
            }
        }
    }

    /**
     * Get all articles that match at least One of the meta data terms supplied.
     *
     * @param matchMetaList List to match to.
     * @param excludeId     Don't include this article (probably the list page calling this)
     * @return Set of matches.
     */
    public Set<Long> getArticlesMatchingMeta(List<GossContentMeta> matchMetaList, Long excludeId) {
        Set<Long> includeList = new HashSet<>();

        for (GossContent article : this) {
            if (article.getId().equals(excludeId)) {
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
