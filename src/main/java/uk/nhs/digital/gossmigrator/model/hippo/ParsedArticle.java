package uk.nhs.digital.gossmigrator.model.hippo;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ParsedArticle {

    private final static Logger LOGGER = LoggerFactory.getLogger(ParsedArticle.class);
    long gossId;
    long templateId;
    Element body;

    ParsedArticle(long gossId, long templateId, String gossArticleText) {

        this.gossId = gossId;
        this.templateId = templateId;


        gossArticleText = StringUtils.replace(gossArticleText, "<!--Changing the preceding comment will unlock the text block-->", "");
        gossArticleText = StringUtils.replace(gossArticleText, "<!--iCMLockedTextBlock-->", "");

        gossArticleText = gossArticleText.replace("<!--", "<").replace("-->", ">");
        Document doc = Jsoup.parse(gossArticleText);

        // JSoup library adds html + head + body tags.  Only care about body.
        body = doc.selectFirst("body");
        Elements nohref = body.select("a:not([href])");
        if(null != nohref && nohref.size() > 0){
            LOGGER.info("Article:{}. Anchors without href. See DW-226", gossId);
        }
    }
}
