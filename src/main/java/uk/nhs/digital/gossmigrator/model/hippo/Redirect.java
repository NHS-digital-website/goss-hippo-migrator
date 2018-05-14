package uk.nhs.digital.gossmigrator.model.hippo;

import org.slf4j.LoggerFactory;
import uk.nhs.digital.gossmigrator.GossImporter;
import uk.nhs.digital.gossmigrator.config.Config;
import uk.nhs.digital.gossmigrator.misc.TextHelper;
import uk.nhs.digital.gossmigrator.model.goss.GossContent;
import uk.nhs.digital.gossmigrator.model.goss.GossRedirectContent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Redirect extends HippoImportable {
    private final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Redirect.class);
    private String ruleFrom = "";
    private String ruleTo = "";
    private String description = "";
    private Long redirectToId;
    private String fromFriendlyUrl;
    private String type;
    private static Pattern r = Pattern.compile("/content/documents/corporate-website/(.*)(/content)?");

    public static List<Long> digitalidurls = new ArrayList<>();
    public static List<Long> contenidturls = new ArrayList<>();
    public static List<String> digitalfriendlyurls = new ArrayList<>();
    public static List<String> contentfriendlyurls = new ArrayList<>();

    private Redirect(GossRedirectContent redirectContent) {
        super(redirectContent);
        id = redirectContent.getId();
        // Some redirects have both article and external link.
        // Seems Goss uses the article link in preference.
        if (redirectContent.getRelatedArticles() != null && redirectContent.getRelatedArticles().size() > 0) {
            redirectToId = redirectContent.getRelatedArticles().get(0);
            this.ruleTo = "/article/".concat(redirectToId.toString());
            this.description = redirectContent.getHeading();
        } else if (redirectContent.getLink() != null) {
            this.ruleTo = redirectContent.getLink().getAddress();
            this.description = redirectContent.getLink().getDescription();
            Matcher m = r.matcher(ruleTo);
            if (m.find()) {
                this.ruleTo = removeSlashContentSuffix(m.group(1));
            }
        }
    }

    private String removeSlashContentSuffix(String in) {
        if (in.endsWith("/content")) {
            return in.substring(0, in.length() - "/content".length());
        }
        return in;
    }

    private Redirect(GossContent content) {
        super(content);
        id = content.getId();
        String path = content.getJcrPath();
        Matcher m = r.matcher(path);
        if (m.find()) {
            this.ruleTo = removeSlashContentSuffix(m.group(1));
        }
        this.description = content.getHeading();
    }

    private static String jcrPathPrefix() {
        if (GossImporter.processingDigital) {
            return Config.JCR_REDIRECT_ROOT.concat("digital/");
        } else {
            return Config.JCR_REDIRECT_ROOT.concat("content/");
        }
    }

    public static Redirect getInstance(GossRedirectContent redirectContent) {
        Redirect redirect = new Redirect(redirectContent);
        redirect.ruleFrom = "^\\/article\\/".concat(redirect.getId().toString()).concat("(\\/.*)?$");
        redirect.setJcrNodeName(TextHelper.toLowerCaseDashedValue(redirectContent.getHeading()
                .concat(redirectContent.getId().toString())));
        redirect.setJcrPath(jcrPathPrefix().concat(TextHelper.toLowerCaseDashedValue(redirectContent.getHeading()))
                .concat(redirectContent.getId().toString()));
        redirect.type = "ID";
        if(GossImporter.processingDigital){
            digitalidurls.add(redirect.getId());
        }else{
            contenidturls.add(redirect.getId());
        }
        return redirect;
    }

    public static Redirect getFriendlyUrlInstance(GossContent content) {
        Redirect redirect;
        if (content instanceof GossRedirectContent) {
            redirect = new Redirect((GossRedirectContent) content);
        } else {
            redirect = new Redirect(content);
        }
        redirect.fromFriendlyUrl = content.getFriendlyUrl();
        redirect.ruleFrom = "^\\/".concat(redirect.fromFriendlyUrl.concat("$"));
        redirect.setJcrNodeName(TextHelper.toLowerCaseDashedValue(content.getHeading())
                .concat(content.getId().toString()).concat("friendly"));

        redirect.setJcrPath(jcrPathPrefix().concat(TextHelper.toLowerCaseDashedValue(content.getHeading()))
                .concat(content.getId().toString()).concat("friendly"));
        redirect.type = "FRIENDLY";
        if (redirect.ruleTo.equals(redirect.fromFriendlyUrl)) {
            LOGGER.info("Id:{}. Not creating friendly url as jcr path matches. {}", content.getId(), content.getFriendlyUrl());
            return null;
        }else{
            if(GossImporter.processingDigital){
                digitalfriendlyurls.add(redirect.fromFriendlyUrl);
            }else{
                contentfriendlyurls.add(redirect.fromFriendlyUrl);
            }
        }

        return redirect;
    }

    public Redirect getNext() {
        Redirect redirect = null;
        if (type.equals("ID")) {
            redirect = (Redirect) GossImporter.digitalData.getImportableContentItems().stream().filter(
                    item -> item instanceof Redirect && (item.getId()).equals(redirectToId))
                    .findFirst().orElse(null);
            if (redirect == null) {
                redirect = (Redirect) GossImporter.contentData.getImportableContentItems().stream().filter(
                        item -> item instanceof Redirect && (item.getId()).equals(redirectToId))
                        .findFirst().orElse(null);
            }
        } else if (type.equals("FRIENDLY")) {
            redirect = (Redirect) GossImporter.digitalData.getImportableContentItems().stream().filter(
                    item -> item instanceof Redirect && ruleTo.equals(((Redirect) item).getFromFriendlyUrl()))
                    .findFirst().orElse(null);
            if (redirect == null) {
                redirect = (Redirect) GossImporter.contentData.getImportableContentItems().stream().filter(
                        item -> item instanceof Redirect && ruleTo.equals(((Redirect) item).getFromFriendlyUrl()))
                        .findFirst().orElse(null);
            }
        }
        return redirect;
    }


    public String getRuleFrom() {
        return ruleFrom;
    }

    @SuppressWarnings("unused")
    public String getRuleTo() {
        return ruleTo;
    }

    public String getDescription() {
        return description;
    }

    private String getFromFriendlyUrl() {
        return fromFriendlyUrl;
    }

    @Override
    public boolean equals(Object otherRedirect) {
        return (otherRedirect != null
                && otherRedirect instanceof Redirect
                && ((type.equals("ID")
                && id.equals(((Redirect) otherRedirect).getId())
                || (type.equals("FRIENDLY")
                && fromFriendlyUrl.equals(((Redirect) otherRedirect).getFromFriendlyUrl())))));
    }
}