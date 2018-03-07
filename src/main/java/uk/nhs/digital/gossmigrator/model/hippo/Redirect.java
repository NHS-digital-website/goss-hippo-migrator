package uk.nhs.digital.gossmigrator.model.hippo;

import uk.nhs.digital.gossmigrator.config.Config;
import uk.nhs.digital.gossmigrator.misc.TextHelper;
import uk.nhs.digital.gossmigrator.model.goss.GossContent;
import uk.nhs.digital.gossmigrator.model.goss.GossRedirectContent;

public class Redirect extends HippoImportable{

    private String ruleFrom = "";
    private String ruleTo = "";
    private String description = "";

    private Redirect(GossRedirectContent redirectContent) {
        super(redirectContent);
        id = redirectContent.getId();
        this.setJcrPath(Config.JCR_DIRECT_ROOT.concat(TextHelper.toLowerCaseDashedValue(redirectContent.getHeading())));
        if(redirectContent.getLink() != null) {
            this.ruleTo = redirectContent.getLink().getAddress();
            this.description = redirectContent.getLink().getDescription();
        }else if(redirectContent.getRelatedArticles() != null && redirectContent.getRelatedArticles().size() > 0){
            this.ruleTo = "/article/".concat(redirectContent.getRelatedArticles().get(0).toString());
            this.description = redirectContent.getHeading();
        }
    }

    private Redirect(GossContent content) {
        super(content);
        id = content.getId();
        this.setJcrPath(Config.JCR_DIRECT_ROOT.concat(TextHelper.toLowerCaseDashedValue(content.getHeading())));
        this.ruleTo = content.getJcrPath();
        this.description = content.getHeading();
    }

    public static Redirect getInstance(GossRedirectContent redirectContent){
        Redirect redirect = new Redirect(redirectContent);
        redirect.ruleFrom = "/article/".concat(redirect.getId().toString());
        return redirect;
    }

    public static Redirect getFriendlyUrlInstance(GossContent content){
        Redirect redirect;
        if(content instanceof GossRedirectContent){
            redirect = new Redirect((GossRedirectContent) content);
        }else{
            redirect =  new Redirect(content);
        }
        redirect.ruleFrom = content.getFriendlyUrl();
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
}

