package uk.nhs.digital.gossmigrator.model.hippo;

import uk.nhs.digital.gossmigrator.GossImporter;
import uk.nhs.digital.gossmigrator.config.Config;
import uk.nhs.digital.gossmigrator.misc.TextHelper;
import uk.nhs.digital.gossmigrator.model.goss.GossContent;
import uk.nhs.digital.gossmigrator.model.goss.GossRedirectContent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Redirect extends HippoImportable {

    private String ruleFrom = "";
    private String ruleTo = "";
    private String description = "";
    private Long redirectToId;
    private String fromFriendlyUrl;
    private String type;

    private Redirect(GossRedirectContent redirectContent) {
        super(redirectContent);
        id = redirectContent.getId();
        if (redirectContent.getLink() != null) {
            this.ruleTo = redirectContent.getLink().getAddress();
            this.description = redirectContent.getLink().getDescription();
            Pattern r = Pattern.compile("/content/documents/corporate-website/(.*)(/content)?");
            Matcher m = r.matcher(ruleTo);
            if (m.find()) {
                this.ruleTo = m.group(1);
            }
        } else if (redirectContent.getRelatedArticles() != null && redirectContent.getRelatedArticles().size() > 0) {
            redirectToId = redirectContent.getRelatedArticles().get(0);
            this.ruleTo = "/article/".concat(redirectToId.toString());
            this.description = redirectContent.getHeading();
            }
    }

    private Redirect(GossContent content) {
        super(content);
        id = content.getId();
        String path = content.getJcrPath();
        Pattern r = Pattern.compile("/content/documents/corporate-website/(.*)(/content)?");
        Matcher m = r.matcher(path);
        if (m.find()) {
            this.ruleTo = m.group(1);
        }
        this.description = content.getHeading();
    }

    public static Redirect getInstance(GossRedirectContent redirectContent) {
        Redirect redirect = new Redirect(redirectContent);
        redirect.ruleFrom = "^\\/article\\/".concat(redirect.getId().toString()).concat("(\\/.*)?$");
        redirect.setJcrPath(Config.JCR_REDIRECT_ROOT.concat(TextHelper.toLowerCaseDashedValue(redirectContent.getHeading()))
                .concat(redirectContent.getId().toString()));
        redirect.type = "ID";
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

        redirect.setJcrPath(Config.JCR_REDIRECT_ROOT.concat(TextHelper.toLowerCaseDashedValue(content.getHeading()))
                .concat(content.getId().toString()).concat("friendly"));
        redirect.type = "FRIENDLY";
        return redirect;
    }

    public Redirect getNext() {
        Redirect redirect = null;
        if (type.equals("ID")) {
            redirect = (Redirect) GossImporter.digitalData.getImportableContentItems().stream().filter(
                    item -> item instanceof Redirect && (item.getId()).equals(redirectToId))
                    .findFirst().orElse(null);
            if(redirect == null){
                redirect = (Redirect) GossImporter.contentData.getImportableContentItems().stream().filter(
                        item -> item instanceof Redirect && (item.getId()).equals(redirectToId))
                        .findFirst().orElse(null);
            }
        } else if (type.equals("FRIENDLY")) {
            redirect = (Redirect) GossImporter.digitalData.getImportableContentItems().stream().filter(
                    item -> item instanceof Redirect && ruleTo.equals(((Redirect) item).getFromFriendlyUrl()))
                    .findFirst().orElse(null);
            if(redirect == null){
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