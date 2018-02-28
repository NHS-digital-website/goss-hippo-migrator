package uk.nhs.digital.gossmigrator.model.hippo;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

class ParsedArticle {

    long gossId;
    Element body;

    ParsedArticle(long gossId, String gossArticleText) {

        this.gossId = gossId;


        gossArticleText = StringUtils.replace(gossArticleText, "<!--Changing the preceding comment will unlock the text block-->", "");
        gossArticleText = StringUtils.replace(gossArticleText, "<!--iCMLockedTextBlock-->", "");

        gossArticleText = gossArticleText.replace("<!--", "<").replace("-->", ">");
        Document doc = Jsoup.parse(gossArticleText);

        // JSoup library adds html + head + body tags.  Only care about body.
        body = doc.selectFirst("body");

    }
}
