package uk.nhs.digital.gossmigrator.model.hippo;


import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.digital.gossmigrator.GossImporter;
import uk.nhs.digital.gossmigrator.model.goss.GossFile;
import uk.nhs.digital.gossmigrator.model.goss.GossLink;
import uk.nhs.digital.gossmigrator.model.goss.enums.GossInternalLinkType;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static uk.nhs.digital.gossmigrator.model.goss.enums.GossInternalLinkType.VIDEO_INLINE;

/**
 * POJO to populate hippo rich text elements in hippo EXIM templates.
 * Parses Goss HTML and resolves links etc.
 */
public class HippoRichText {
    private static final Logger LOGGER = LoggerFactory.getLogger(HippoRichText.class);

    private String content;
    private long gossArticleId;
    private List<HippoLinkRef> docReferences = new ArrayList<>();

    public HippoRichText(String html, long gossArticleId) {
        this.gossArticleId = gossArticleId;
        this.content = parseContent(html);
    }

    public String getContent() {
        return content;
    }

    @SuppressWarnings("unused") // Used by template
    public List<HippoLinkRef> getDocReferences() {
        return docReferences;
    }

    private String parseContent(String html) {
        // Create a temporary parent node.
        Element contentNode = new Element("temp");
        contentNode.html(html);

        parseLinks(contentNode);
        parseButtons(contentNode);

        return escapeChars(contentNode.html());
    }

    private String escapeChars(String source) {
        String replaced = source.replaceAll("\"", "\\\\\"");
       // String replaced = source;

        // Looks like the chosen html/xml library adds carriage return line feeds.
        replaced = replaced.replaceAll("\n", "").replaceAll("\r", "");
        return replaced;
    }

    /**
     * Remove any button style from anchors.
     *
     * @param html Html to parse.
     */
    private void parseButtons(Element html) {
        // Get anchor buttons and remove button class
        List<Element> anchors = html.select("a.button");
        for (Element anchor : anchors) {
            anchor.removeClass("button");
        }
    }

    private Element parseLinks(Element source) {
        // Get spans with attribute data-icm-arg2
        List<Element> links = source.select("span[data-icm-arg2]");
        for (Element link : links) {
            // External links.
            String linkTypeId = link.attributes().get("data-icm-inlinetypeid");
            String referenceId = link.attributes().get("data-icm-arg2");
            String linkText = link.attributes().get("data-icm-arg2name");
            Long referenceKey = null;
            if (null != referenceId) {
                if (StringUtils.isNumeric(referenceId)) {
                    referenceKey = new Long(referenceId);
                }
            } else {
                referenceId = "";
                LOGGER.warn("ArticleId:{}. Parsing a goss link with no Referenced ID (data-icm-arg2)? Data:{}", gossArticleId, link.toString());
            }

            switch (Objects.requireNonNull(GossInternalLinkType.getById(linkTypeId))) {
                case PAGE_SNIPPET: {
                    LOGGER.error("ArticleId:{}. Goss internal link parser for {} not coded yet."
                            , gossArticleId, GossInternalLinkType.getById(linkTypeId));
                    break;
                }
                case DOCUMENT_LINK: {
                    // <span data-icm-arg2=\"298\" data-icm-arg2name=\"Clinical Audits and Registries calendar\" data-icm-arg4=\"CARMS calendar\" data-icm-arg6=\"_self\" data-icm-inlinetypeid=\"2\">Type=media;MediaID=298;Title=CARMS calendar;Target=_self;<\/span>
                    // Links to an Asset
                    // For now do same as media below.
                }
                case IMAGE: {
                    //<span data-icm-arg2=\"160\" data-icm-arg2name=\"biomedicine\" data-icm-arg4=\"standard\" data-icm-inlinetypeid=\"4\">Type=image;imageid=160;constraint=standard;<\/span>
                    GossFile fileLink = GossImporter.gossData.getGossFileMap().get(referenceKey);
                    if (null == fileLink) {
                        LOGGER.error("Media Id:{}. Referenced by Article:{} does not exist."
                                , referenceKey, gossArticleId);
                    } else {
                        String docName = Paths.get(fileLink.getJcrPath(gossArticleId)).getFileName().toString();
                        // TODO probably want images in img element.  Need to check what hippo does.
                        Element newLink = new Element("a").text(linkText).attr("href", docName);
                        link.replaceWith(newLink);
                        docReferences.add(new HippoLinkRef(fileLink.getJcrPath(gossArticleId), docName));
                    }
                    break;
                }
                case SCRIPTS: {
                    // The reference Id contains the script.  Replace the node with it.
                    Element newLink = new Element("span").text(referenceId);
                    link.replaceWith(newLink);
                    break;
                }
                case ARTICLE_LINK: {
                    // End up with something like;
                    // <p>An <a href=\"published-upcoming-publication\">internal</a> link to an article with internal as the link.</p>
                    // And a doc ref node with something like;
                    // "/content/documents/corporate-website/publication-system/published-upcoming-publication"
                    // in the HippoLinkRef collection as jcrpath and published-upcoming-publication as nodeName.
                    String jcrUrl = GossImporter.gossData.getGossContentUrlMap().get(referenceKey);
                    if (null == jcrUrl) {
                        LOGGER.error("ArticleId:{}. Internal link to Article:{} could not be resolved.", gossArticleId, referenceKey);
                        break;
                    } else {
                        // Create replacement node.
                        String docName = Paths.get(jcrUrl).getFileName().toString();
                        Element newLink = new Element("a").text(linkText).attr("href", docName);
                        link.replaceWith(newLink);
                        docReferences.add(new HippoLinkRef(jcrUrl, docName));
                    }
                    break;
                }
                case EXTERNAL_LINK: {
                    // End up with something like
                    // <p>An <a href=\"http://www.google.com\">external</a> link with external as the link (url type).</p>

                    // Get link.
                    GossLink gossLink = GossImporter.gossData.getGossLinkMap().get(referenceKey);
                    if (null == gossLink) {
                        LOGGER.error("ArticleId:{}. No link could be found for link id:{}.", gossArticleId, referenceKey);
                        break;  // Leave node unchanged.
                    }

                    // Create replacement node.
                    Element newLink = new Element("a").text(linkText).attr("href", gossLink.getAddress());
                    link.replaceWith(newLink);
                    break;
                }
            }
        }
        // VIDEO_INLINE lives in a div.
        // div data-icm-arg1=\"92587\" data-icm-arg1name=\"video\" data-icm-inlinetypeid=\"7\">92587<\/div>
        // Not sure there will be any of these in the export.
        links = source.select("div[data-icm-arg1]");
        for (
                Element link : links)

        {
            String linkTypeId = link.attributes().get("data-icm-inlinetypeid");
            String referenceId = link.attributes().get("data-icm-arg1");
            String linkText = link.attributes().get("data-icm-arg1name");
            Long referenceKey = null;
            if (null != referenceId) {
                if (StringUtils.isNumeric(referenceId)) {
                    referenceKey = new Long(referenceId);
                }
            } else {
                referenceId = "";
                LOGGER.warn("ArticleId:{}. Parsing a goss link with no Referenced ID (data-icm-arg2)? Data:{}", gossArticleId, link.toString());
            }
            if (Objects.requireNonNull(GossInternalLinkType.getById(linkTypeId)) == VIDEO_INLINE) {
                GossFile fileLink = GossImporter.gossData.getGossFileMap().get(referenceKey);
                String docName = Paths.get(fileLink.getJcrPath(gossArticleId)).getFileName().toString();
                Element newLink = new Element("a").text(linkText).attr("href", docName);
                link.replaceWith(newLink);
                docReferences.add(new HippoLinkRef(fileLink.getJcrPath(gossArticleId), docName));
            } else {
                LOGGER.error("ArticleId:{}. Unexpected inline link in div.  Link refers to:{}.", gossArticleId, referenceId);
            }
        }


        return source;
    }

    // TODO delete.  Will find out shortly if needed.
    private static void removeComments(Node node) {
        for (int i = 0; i < node.childNodeSize(); ) {
            Node child = node.childNode(i);
            if (child.nodeName().equals("#comment"))
                child.remove();
            else {
                removeComments(child);
                i++;
            }
        }
    }


}
