package uk.nhs.digital.gossmigrator.model.hippo;


import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * POJO to populate hippo rich text elements in hippo EXIM templates.
 * Parses Goss HTML and resolves links etc.
 */
public class HippoRichText {
    private String content;
    private List<HippoLinkRef> docReferences = new ArrayList<>();

    public HippoRichText(String html) {
        this.content = parseContent(html);
    }

    public String getContent() {
        return content;
    }

    @SuppressWarnings("unused") // Used by template
    public List<HippoLinkRef> getDocReferences() {
        return docReferences;
    }

    private String parseContent(String html){
        // Create a temporary parent node.
        Element contentNode = new Element("temp");
        contentNode.html(html);

        // TODO parse internal links.
        // TODO parse external links via goss link objects.
        // TODO parse asset links.
        // TODO parse href external links

        parseButtons(contentNode);

        return escapeChars(contentNode.html());
    }

    private String escapeChars(String source){
        String replaced = source.replaceAll("\"", "&quot;");
        return replaced;
    }

    /**
     * Remove any button style from anchors.
     * @param html Html to parse.
     */
    private void parseButtons(Element html){
        // Get anchor buttons and remove button class
        List<Element> anchors = html.select("a.button");
        for (Element anchor : anchors) {
            anchor.removeClass("button");
        }
    }

    // TODO Delete.  For now contains a few snippets of code that might be helpful when we get more detail on links.
    @SuppressWarnings("unused")
    private static Element parseLinks(Element source, Map<Long, String> gossUrlMap) {
        // Get spans with attribute data-icm-arg2
        List<Element> links = source.select("span[data-icm-arg2]");
        for (Element link : links) {
            Long gossId = Long.parseLong(link.attributes().get("data-icm-arg2"));
            String text = link.attributes().get("data-icm-arg2name");
            Element newLink = new Element("a").text(text).attr("href", gossUrlMap.get(1L));

            link.replaceWith(newLink);
        }
        return source;
    }

    // TODO delete.  Will find out shortly if needed.
    @SuppressWarnings("unused")
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
