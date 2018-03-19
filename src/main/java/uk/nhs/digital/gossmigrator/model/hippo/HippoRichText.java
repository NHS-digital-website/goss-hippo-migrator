package uk.nhs.digital.gossmigrator.model.hippo;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.digital.gossmigrator.GossImporter;
import uk.nhs.digital.gossmigrator.misc.GossExportHelper;
import uk.nhs.digital.gossmigrator.model.goss.GossContent;
import uk.nhs.digital.gossmigrator.model.goss.GossFile;
import uk.nhs.digital.gossmigrator.model.goss.GossLink;
import uk.nhs.digital.gossmigrator.model.goss.enums.GossInternalLinkType;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * POJO to populate hippo rich text elements in hippo EXIM templates.
 * Parses Goss HTML and resolves links etc.
 */
public class HippoRichText {
    private static final Logger LOGGER = LoggerFactory.getLogger(HippoRichText.class);

    private String content;
    private long gossArticleId;
    private Set<HippoLinkRef> docReferences = new HashSet<>();

    HippoRichText(String html, long gossArticleId) {
        this.gossArticleId = gossArticleId;
        this.content = parseContent(html);
    }

    public String getContent() {
        return content;
    }

    @SuppressWarnings("unused") // Used by template
    public Set<HippoLinkRef> getDocReferences() {
        return docReferences;
    }

    private String parseContent(String html) {
        // Create a temporary parent node.
        Element contentNode = new Element("temp");
        contentNode.html(html);

        contentNode = parseLinks(contentNode);
        parseButtons(contentNode);

        return escapeChars(contentNode.html());
    }

    private String escapeChars(String source) {
        String replaced = source.replaceAll("\"", "\\\\\"");

        // Looks like the chosen html/xml library adds carriage return line feeds.
        replaced = replaced.replaceAll("\n", "").replaceAll("\r", "");
        // TODO Article 8548 has invalid html.  Will be fixed in source doc.
        // For now fix with below.
        replaced = replaced.replaceAll("D:\\\\&gt", "D:&gt");
        // TODO Article 7881 similar problem
        replaced = replaced.replaceAll("C:\\\\&gt", "C:&gt");
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
        int i = 0;
        for (Element link : links) {
            // External links.
            i++;
            String linkTypeId = link.attributes().get("data-icm-inlinetypeid");
            String referenceId = link.attributes().get("data-icm-arg2");
            String linkText = link.attributes().get("data-icm-arg4");
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
                    // Links to an Asset (not images!)
                    GossFile fileLink = GossImporter.digitalData.getGossFileMap().get(referenceKey);
                    if (null == fileLink) {
                        LOGGER.error("Media Id:{}. Referenced by Article:{} does not exist."
                                , referenceKey, gossArticleId);
                    } else if (GossExportHelper.isImage(fileLink.getFileName())) {
                        LOGGER.warn("Article:{}, Link{} is an asset link to an image.", gossArticleId, referenceKey);
                    } else {
                        String docName = "Linkref" + referenceKey + "-" + i;
                        Element newLink = new Element("a").text(linkText).attr("href", docName);
                        link.replaceWith(newLink);
                        docReferences.add(new HippoLinkRef(fileLink.getJcrPath(gossArticleId), docName));
                    }
                    break;
                }
                case IMAGE: {
                    //Goss:<span data-icm-arg2=\"666\" data-icm-arg2name=\"biomedicine\" data-icm-arg4=\"standard\" data-icm-inlinetypeid=\"4\">Type=image;imageid=666;constraint=standard;<\/span>
                    //Hippo:<p><img data-type=“hippogallery:original” data-uuid=“3196cf43-76d7-4424-ba21-20ff24e53e0b” src=“binaries/content/gallery/publicationsystem/avatar.png/avatar.png/hippogallery:original” /></p>
                    GossFile fileLink = GossImporter.digitalData.getGossFileMap().get(referenceKey);
                    if (null == fileLink) {
                        LOGGER.error("Media Id:{}. Referenced by Article:{} does not exist."
                                , referenceKey, gossArticleId);
                    } else {
                        String docName = "Linkref" + referenceKey + "-" + i;
                        Element newLink = new Element("img")
                                .attr("alt", linkText)
                                .attr("data-type", "hippogallery:original")
                                .attr("src", docName + "/{_document}/hippogallery:original");
                        link.replaceWith(newLink);

                        docReferences.add(new HippoLinkRef(fileLink.getJcrPath(gossArticleId), docName));
                    }
                    break;
                }
                case SCRIPTS: {
                    // The reference Id contains the script.  Replace the node with it.
                    // Goss example:<p><span data-icm-arg2=\"&lt;iframe title=&quot;HSCIC Graduate Scheme Video&quot; width=&quot;560&quot; height=&quot;315&quot; src=&quot;https:\/\/www.youtube.com\/embed\/jD1_grFN_Fs&quot; allowfullscreen&gt;&lt;\/iframe&gt;\" data-icm-inlinetypeid=\"6\">Type=scripts;&lt;iframe title=&quot;HSCIC Graduate Scheme Video&quot; width=&quot;560&quot; height=&quot;315&quot; src=&quot;https:\/\/www.youtube.com\/embed\/jD1_grFN_Fs&quot; allowfullscreen&gt;&lt;\/iframe&gt;<\/span><\/p>

                    link.before(referenceId);

                    Elements iframes = link.previousElementSibling().select("iframe");
                    // Hippo expects iframes to have a frameborder.
                    for (Element iframe : iframes) {
                        if (iframe.attr("frameborder") != null) {
                            iframe.attr("frameborder", "0");
                        }
                    }
                    link.remove();

                    break;
                }
                case ARTICLE_LINK: {
                    // End up with something like;
                    // <p>An <a href=\"published-upcoming-publication\">internal</a> link to an article with internal as the link.</p>
                    // And a doc ref node with something like;
                    // "/content/documents/corporate-website/publication-system/published-upcoming-publication"
                    // in the HippoLinkRef collection as jcrpath and published-upcoming-publication as nodeName.
                    GossContent linkedTo = GossImporter.digitalData.getArticlesContentList().getById(new Long(referenceKey));
                    String jcrUrl = null;
                    if(linkedTo != null){
                        jcrUrl = linkedTo.getJcrPath();
                    }
                    if (null == jcrUrl) {
                        LOGGER.error("ArticleId:{}. Internal link to Article:{} could not be resolved.", gossArticleId, referenceKey);
                        break;
                    } else {
                        // Create replacement node.
                        String docName = "Linkref" + referenceKey + "-" + i;
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
                    GossLink gossLink = GossImporter.digitalData.getGossLinkMap().get(referenceKey);
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
        return source;
    }
}
