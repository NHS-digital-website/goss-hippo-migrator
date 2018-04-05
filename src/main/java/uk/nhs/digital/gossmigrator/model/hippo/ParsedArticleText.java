package uk.nhs.digital.gossmigrator.model.hippo;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.digital.gossmigrator.GossImporter;
import uk.nhs.digital.gossmigrator.model.goss.enums.ArticleTextSection;
import uk.nhs.digital.gossmigrator.model.goss.enums.ContentType;
import uk.nhs.digital.gossmigrator.model.hippo.enums.SectionTypes;

import java.util.ArrayList;
import java.util.List;

import static uk.nhs.digital.gossmigrator.model.goss.enums.ArticleTextSection.*;
import static uk.nhs.digital.gossmigrator.model.goss.enums.ContentType.GENERAL;
import static uk.nhs.digital.gossmigrator.model.goss.enums.ContentType.HUB;
import static uk.nhs.digital.gossmigrator.model.goss.enums.ContentType.SERVICE;

/**
 * Parses the Goss extract ARTICLETEXT.
 * Splits the goss data into parts based upon Goss specific HTML comments.
 * Constructor runs the parsing code.
 */
public class ParsedArticleText extends ParsedArticle {

    private final static Logger LOGGER = LoggerFactory.getLogger(ParsedArticleText.class);
    private HippoRichText introduction;
    private List<Section> sections;
    private HippoRichText contactDetails;
    private List<HippoRichText> topTasks;
    private HippoRichText defaultNode;
    private ContentType contentType;
    private HippoRichText keyFacts;
    private HippoRichText component;
    private boolean addTopTasksToDefaultNode = false;
    private boolean addTopTasksToSections = false;

    /**
     * Parses ARTICLETEXT node from Goss export.
     * Results available from getters.
     *
     * @param gossId          ARTICLEID value for logging.
     * @param gossArticleText ARTICLETEXT String.
     * @param contentType,    type of content to be parsed
     */
    ParsedArticleText(long gossId, long templateId, String gossArticleText, ContentType contentType) {
        super(gossId, templateId, gossArticleText);

        this.contentType = contentType;

        checkSections(body);
        defaultNode = extractRichTextElement(INTRO_AND_SECTIONS);
        introduction = extractIntroduction();
        topTasks = extractTopTasks();
        if (null != introduction && !StringUtils.isEmpty(introduction.getContent())
                && SERVICE != contentType && ContentType.PUBLICATION != contentType
                && GossImporter.processingDigital) {
            sections = new ArrayList<>();
            sections.add(new Section("", SectionTypes.DEFAULT.getTypeName(), introduction));
        }
        // Some articles have stuff in top tasks, but our template has no top tasks.
        // If possible add then as a section.
        if (addTopTasksToSections) {
            HippoRichText tasks = extractRichTextElement(TOPTASKS);
            if(null == sections){
                sections = new ArrayList<>();
            }
            sections.add(new Section("", SectionTypes.DEFAULT.getTypeName(), tasks));
        }
        if(addTopTasksToDefaultNode){
            HippoRichText tasks = extractRichTextElement(TOPTASKS);
            if(null == defaultNode){
                defaultNode = tasks;
            }else{
                defaultNode.setContent(tasks.getContent() + defaultNode.getContent());
                defaultNode.getDocReferences().addAll(tasks.getDocReferences());
            }
        }
        extractSections();
        component = extractRichTextElement(COMPONENT);
        contactDetails = extractRichTextElement(CONTACT_INFO);
        keyFacts = extractRichTextElement(FACTS);
        LOGGER.debug(toString());
    }

    private HippoRichText extractRichTextElement(ArticleTextSection section) {
        Element gossDefaultNode = body.selectFirst("#" + section.getId());
        HippoRichText result = null;
        if (gossDefaultNode != null) {
            result = new HippoRichText(gossDefaultNode.html(), gossId, templateId, section.getRef());
        }
        return result;
    }

    private void checkSections(Element body) {
        for (Element a : body.select("textbody")) {
            String id = a.attr("id");
            if (!ArticleTextSection.idExists(id)) {
                if (id.equals("ALSOINTERESTED") && contentType != ContentType.PUBLICATION) {
                    // We have knowingly ignored these for publications.
                    LOGGER.error("Article id:{}, (not a publication). section ALSOINTERESTED unexpected", gossId, id);
                } else if (!id.equals("ALSOINTERESTED")) {
                    LOGGER.error("Article id:{}, ARTICLETEXT section {} unexpected.", gossId, id);
                }
            }
        }
    }


    /**
     * ARTICLETEXT <textbody id="__DEFAULT">body lives here<textbody>
     * Any stuff that appears before a h2 inside a textbody with id __DEFAULT. Most won't have these. (From spec).
     *
     * @return Introduction html.
     */
    private HippoRichText extractIntroduction() {
        Element gossIntroNode = body.selectFirst("#" + INTRO_AND_SECTIONS.getId());
        StringBuilder result = new StringBuilder();
        boolean haveIntro = false;

        if (gossIntroNode != null) {
            // Assume the intro node has no text of its own, only children.
            if (!StringUtils.isEmpty(gossIntroNode.ownText())) {
                LOGGER.warn("Goss article id: {}. Unexpected text in goss article text introduction.", gossId);
            }

            // Going to assume any h2 or h3 is an immediate child of this for now.
            // If they are in a table leave them together.
            Elements h2h3Elements = body.select("h2, h3");
            for (Element h2h3Element : h2h3Elements) {
                if (!h2h3Element.parent().tagName().equals("textbody")
                        && !h2h3Element.parent().tagName().equals("td")
                        && !h2h3Element.parent().tagName().equals("caption")) {
                    LOGGER.warn("Goss Article Id:{}, Found h2 or h3 in article text nested deeper than expected.", gossId);
                }
            }

            for (Element child : gossIntroNode.children()) {

                if ("h2".equals(child.tagName()) || "h3".equals(child.tagName())) {
                    // Found first h2 or h3
                    break;
                }
                if ("caption".equals(child.tagName()) && ("h2".equals(child.child(0).tagName())
                        || "h3".equals(child.child(0).tagName()))) {
                    break;
                }
                haveIntro = true;
                result.append(child.outerHtml());
                // Remove the node so does not get processed as part of sections.
                child.remove();
            }
        }

        if (haveIntro) {
            return new HippoRichText(result.toString(), gossId, templateId);
        }
        return null;
    }


    /**
     * Get List of top tasks.
     * In source data these are separated by paragraph tags.
     *
     * @return List of top tasks as HippoRichText objects.
     */
    private List<HippoRichText> extractTopTasks() {
        Element gossTopTasksNode = body.selectFirst("#" + ArticleTextSection.TOPTASKS.getId());
        List<HippoRichText> topTasks = null;

        if (null != gossTopTasksNode && gossTopTasksNode.children().size() > 0) {
            if (contentType == SERVICE) {
                topTasks = new ArrayList<>();
                for (Element topTask : gossTopTasksNode.children()) {
                    // Assume all child nodes are <p>'s
                    if (!"p".equals(topTask.tagName())) {
                        LOGGER.warn("Top Tasks in Goss Article:{} has child elements not of tag 'p' (it is {}). This is not expected.", gossId, topTask.tagName());
                    }

                    topTasks.add(new HippoRichText(topTask.outerHtml(), gossId, templateId));
                }
            } else if (contentType == GENERAL) {
                addTopTasksToSections = true;
            } else if (contentType == HUB) {
                addTopTasksToDefaultNode = true;
            } else {
                LOGGER.warn("Article:{}.  Have top tasks, but not a Service, Hub or General page ({}).  This is lost.", gossId, contentType);
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
     */
    private void extractSections() {
        Element gossSectionsNode = body.selectFirst("#" + INTRO_AND_SECTIONS.getId());

        if (gossSectionsNode != null) {
            promoteH3s(gossSectionsNode);

            Section section;

            while (true) {
                section = extractSection(gossSectionsNode);
                if (section != null) {
                    if (null == sections) {
                        sections = new ArrayList<>();
                    }
                    sections.add(section);
                } else {
                    // Finished with __DEFAULT node
                    gossSectionsNode.remove();
                    break;
                }
            }
        }
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

        if (defaultNode != null) {
            for (Element element : defaultNode.children()) {
                if (("h2".equals(element.tagName()) || "caption".equals(element.tagName())) && !haveSection) {
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

        }

        return haveSection ? new Section(title, SectionTypes.DEFAULT.getTypeName()
                , new HippoRichText(content.toString(), gossId, templateId)) : null;
    }

    /**
     * If there are no h2 nodes then change any h3s into h2s.
     *
     * @param body Element to parse.
     */
    private void promoteH3s(Element body) {
        if (body != null && body.selectFirst("h2") == null) {
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

    public HippoRichText getComponent() {
        return component;
    }

}
