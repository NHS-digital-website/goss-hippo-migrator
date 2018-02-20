package uk.nhs.digital.gossmigrator.model.hippo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import uk.nhs.digital.gossmigrator.model.goss.enums.ArticleTextSection;

import java.util.ArrayList;
import java.util.List;

import static uk.nhs.digital.gossmigrator.model.goss.enums.ArticleTextSection.RELATED_LINKS;
import static uk.nhs.digital.gossmigrator.model.goss.enums.ArticleTextSection.RESOURCE_LINKS;

public class ParsedArticleLinks extends ParsedArticle{

    private List<HippoLink> relatedLinks;
    private List<HippoLink> resourceLinks;

    ParsedArticleLinks(long gossId, String gossArticleText) {
        super(gossId, gossArticleText);

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
        Element gossLinks = body.selectFirst("#" + section.getId());
        if(gossLinks != null){
            List<Element> elements = gossLinks.getElementsByTag("a");
            for (Element element : elements) {
                String text = element.ownText();
                String address = element.attributes().get("href");
                links.add(new HippoLink(address, text));
            }
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