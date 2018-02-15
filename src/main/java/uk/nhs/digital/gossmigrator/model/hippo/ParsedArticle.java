package uk.nhs.digital.gossmigrator.model.hippo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class ParsedArticle {

    protected long gossId;
    protected Element body;

    ParsedArticle(long gossId, String gossArticleText) {

        this.gossId = gossId;

        // Turn the comments into elements (so can parse)
        gossArticleText = gossArticleText.replace("<!--", "<").replace("-->", ">");
        Document doc = Jsoup.parse(gossArticleText);

        // JSoup library adds html + head + body tags.  Only care about body.
        body = doc.selectFirst("body");

    }

}
