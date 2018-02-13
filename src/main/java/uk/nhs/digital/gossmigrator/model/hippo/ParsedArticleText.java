package uk.nhs.digital.gossmigrator.model.hippo;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.digital.gossmigrator.model.goss.enums.ArticleTextSection;
import uk.nhs.digital.gossmigrator.model.hippo.enums.SectionTypes;

import java.util.ArrayList;
import java.util.List;

import static uk.nhs.digital.gossmigrator.model.goss.enums.ArticleTextSection.*;

/**
 * Parses the Goss extract ARTICLETEXT.
 * Splits the goss data into parts based upon Goss specific HTML comments.
 * Constructor runs the parsing code.
 */
public class ParsedArticleText {
    private final static Logger LOGGER = LoggerFactory.getLogger(ParsedArticleText.class);
    private HippoRichText introduction;
    private List<Section> sections;
    private HippoRichText contactDetails;
    private List<HippoRichText> topTasks;
    private HippoRichText defaultNode;
    private long gossId;
    private HippoRichText keyFacts;

    /**
     * Parses ARTICLETEXT node from Goss export.
     * Results available from getters.
     *
     * @param gossId          ARTICLEID value for logging.
     * @param gossArticleText ARTICLETEXT String.
     */
    public ParsedArticleText(long gossId, String gossArticleText) {
        this.gossId = gossId;

        // Turn the comments into elements (so can parse)
        gossArticleText = gossArticleText.replace("<!--", "<").replace("-->", ">");
        Document doc = Jsoup.parse(gossArticleText);

        // JSoup library adds html + head + body tags.  Only care about body.
        Element body = doc.selectFirst("body");

        defaultNode = extractDefaultNode(body);
        introduction = extractIntroduction(body);
        sections = extractSections(body);
        topTasks = extractTopTasks(body);
        extractComponent(body);
        contactDetails = extractContactDetails(body);
        keyFacts = extractKeyFacts(body);
        LOGGER.debug(toString());
    }

    /**
     * ARTICLETEXT <textbody id="__DEFAULT">body lives here<textbody>
     *
     * @param body The Goss ARTICLETEXT.
     * @return Introduction html.
     */
    private HippoRichText extractDefaultNode(Element body) {
        Element gossDefaultNode = body.selectFirst("#" + INTRO_AND_SECTIONS.getId());
        HippoRichText result = null;
        if (gossDefaultNode != null) {
            result = new HippoRichText(gossDefaultNode.html(), gossId);
        }
        return result;
    }

    /**
     * Get the contact details part of the Goss articletext node.
     *
     * @param body Articletext as child of a body element.
     * @return HippoRichText object for the contact details.
     */
    private HippoRichText extractContactDetails(Element body) {
        Element gossContactDetails = body.selectFirst("#" + CONTACT_INFO.getId());
        HippoRichText result = null;
        if (gossContactDetails != null) {
            result = new HippoRichText(gossContactDetails.html(), gossId);
        }
        return result;
    }

    private HippoRichText extractKeyFacts(Element body) {
        Element gossContactDetails = body.selectFirst("#" + FACTS.getId());
        HippoRichText result = null;
        if (gossContactDetails != null) {
            result = new HippoRichText(gossContactDetails.html(), gossId);
        }
        return result;
    }

    /**
     * ARTICLETEXT <textbody id="__DEFAULT">body lives here<textbody>
     * Any stuff that appears before a h2 inside a textbody with id __DEFAULT. Most won't have these. (From spec).
     *
     * @param body The Goss ARTICLETEXT.
     * @return Introduction html.
     */
    private HippoRichText extractIntroduction(Element body) {
        Element gossIntroNode = body.selectFirst("#" + INTRO_AND_SECTIONS.getId());

        // Assume the intro node has no text of its own, only children.
        if (!StringUtils.isEmpty(gossIntroNode.ownText())) {
            LOGGER.warn("Goss article id: {}. Unexpected text in goss article text introduction.", gossId);
        }
        boolean haveIntro = false;

        // Going to assume any h2 or h3 is an immediate child of this for now.
        Elements h2h3Elements = body.select("h2, h3");
        for (Element h2h3Element : h2h3Elements) {
            if (!h2h3Element.parent().tagName().equals("textbody")) {
                LOGGER.warn("Goss Article Id:{}, Found h2 or h3 in article text nested deeper than expected.", gossId);
            }
        }
        StringBuilder result = new StringBuilder();
        for (Element child : gossIntroNode.children()) {

            if ("h2".equals(child.tagName()) || "h3".equals(child.tagName())) {
                // Found first h2 or h3
                break;
            }
            haveIntro = true;
            result.append(child.outerHtml());
            // Remove the node so does not get processed as part of sections.
            child.remove();
        }

        if (haveIntro) {
            return new HippoRichText(result.toString(), gossId);
        }
        return null;
    }

    /**
     * There is a component part of the Goss ARTICLETEXT.
     * Not used at the moment.  In to log a warning if we find it containing data in full export.
     *
     * @param body The ARTICLETEXT as child of a body node.
     */
    private void extractComponent(Element body) {
        Element componentGossNode = body.selectFirst("#" + COMPONENT.getId());
        if (null != componentGossNode) {
            if (componentGossNode.children().size() > 0 || !StringUtils.isEmpty(componentGossNode.ownText())) {
                LOGGER.warn("Goss Id:{}.  Has data in ARTICLETEXT 'Component' Section.  Currently ignored in import.", gossId);
            }
        }
    }

    /**
     * Get List of top tasks.
     * In source data these are separated by paragraph tags.
     *
     * @param body Parent element of Goss ARTICLETEXT
     * @return List of top tasks as HippoRichText objects.
     */
    private List<HippoRichText> extractTopTasks(Element body) {
        Element gossTopTasksNode = body.selectFirst("#" + ArticleTextSection.TOPTASKS.getId());
        List<HippoRichText> topTasks = null;

        if (null != gossTopTasksNode && gossTopTasksNode.children().size() > 0) {
            topTasks = new ArrayList<>();
            for (Element topTask : gossTopTasksNode.children()) {
                // Assume all child nodes are <p>'s
                if (!"p".equals(topTask.tagName())) {
                    LOGGER.warn("Top Tasks in Goss Article:{} has child elements not of tag 'p'. This is not expected.", gossId);
                }
                topTasks.add(new HippoRichText(topTask.outerHtml(), gossId));
            }
        }

        return topTasks;
    }

    /**
     * Get immediate children of __DEFAULT node.
     * If h2 then is a section.
     * If no h2's then promote any h3's to h2.
     * Anything before first h2 is the introduction and should have already been dealt with
     * and removed from tree.
     * TODO Verify h2/h3 are always immediate children.  Expect them to be.
     *
     * @param body The parent of the __DEFAULT span.
     * @return A list of Sections.
     */
    private List<Section> extractSections(Element body) {
        Element gossSectionsNode = body.selectFirst("#" + INTRO_AND_SECTIONS.getId());
        promoteH3s(gossSectionsNode);

        boolean haveSections = false;
        List<Section> sections = null;
        Section section;

        while (true) {
            section = extractSection(gossSectionsNode);
            if (section != null) {
                if (!haveSections) {
                    sections = new ArrayList<>();
                }
                sections.add(section);
                haveSections = true;
            } else {
                // Finished with __DEFAULT node
                gossSectionsNode.remove();
                break;
            }
        }

        return sections;
    }

    /**
     * Returns a populated Section object from the span '__DEFAULT' node and removes the section from it.
     * Call multiple times to get all Sections.
     *
     * @param defaultNode The <textbody id="__DEFAULT"> node from Goss ARTICLETEXT
     * @return The populated Section object.
     */
    private Section extractSection(Element defaultNode) {
        boolean haveSection = false;
        String title = null;
        StringBuilder content = new StringBuilder();

        for (Element element : defaultNode.children()) {
            if ("h2".equals(element.tagName()) && !haveSection) {
                title = element.ownText();
                haveSection = true;
                element.remove();
            } else if ("h2".equals(element.tagName())) {
                // Start of new section
                break;
            } else {
                // Part of section to be processed.
                content.append(element.toString());
                element.remove();
            }
        }

        return haveSection ? new Section(title, SectionTypes.DEFAULT.getTypeName()
                , new HippoRichText(content.toString(), gossId)) : null;
    }

    /**
     * If there are no h2 nodes then change any h3s into h2s.
     *
     * @param body Element to parse.
     */
    private void promoteH3s(Element body) {
        if (body.selectFirst("h2") == null) {
            // No h2's so promote any h3s
            for (Element h3 : body.select("h3")) {
                h3.tagName("h2");
            }
        }
    }

    public HippoRichText getIntroduction() {
        return introduction;
    }

    public List<Section> getSections() {
        return sections;
    }

    public HippoRichText getContactDetails() {
        return contactDetails;
    }


    public List<HippoRichText> getTopTasks() {
        return topTasks;
    }

    public HippoRichText getKeyFacts() {
        return keyFacts;
    }

    public HippoRichText getDefaultNode() {
        return defaultNode;
    }

    @Override
    public String toString() {
        return "ParsedArticleText{\nGoss Id:" + gossId +
                "\nIntroduction:" + Boolean.toString(null == introduction) +
                "\nSections:" + Boolean.toString(null == sections) +
                "\nTop Tasks:" + Boolean.toString(null == topTasks) +
                "}";
    }
}
