package uk.nhs.digital.gossmigrator.model.hippo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import uk.nhs.digital.gossmigrator.model.goss.enums.ArticleTextSection;

import java.util.ArrayList;
import java.util.List;

import static uk.nhs.digital.gossmigrator.model.goss.enums.ArticleTextSection.RELATED_LINKS;
import static uk.nhs.digital.gossmigrator.model.goss.enums.ArticleTextSection.RESOURCE_LINKS;

public class ParsedArticleLinks {

    private List<HippoLink> relatedLinks;
    private List<HippoLink> resourceLinks;

    ParsedArticleLinks(String gossArticleText) {

        // Turn the comments into elements (so can parse)
        gossArticleText = gossArticleText.replace("<!--", "<").replace("-->", ">");
        Document doc = Jsoup.parse(gossArticleText);

        // JSoup library adds html + head + body tags.  Only care about body.
        Element body = doc.selectFirst("body");

        resourceLinks = extractLinks(RESOURCE_LINKS, body);
        relatedLinks = extractLinks(RELATED_LINKS, body);
    }


    /**
     * Extracts the link from the html element and creates a list of HippoLink
     * Sets the href attribute as address and the text of the <a> element as displayText
     *
     * @param section, type of link to be processed
     * @param body, the element containing the links
     */
    private List<HippoLink> extractLinks(ArticleTextSection section, Element body) {
        List<HippoLink> links = new ArrayList<>();
        Element gossContactDetails = body.selectFirst("#" + section.getId());
        List<Element> elements = gossContactDetails.getElementsByTag("a");
        for (Element element : elements) {
            String text = element.ownText();
            String address = element.attributes().get("href");
            links.add(new HippoLink(text, address));
        }
        return links;
    }

    public List<HippoLink> getRelatedLinks() {
        return relatedLinks;
    }

    public List<HippoLink> getResourceLinks() {
        return resourceLinks;
    }
}