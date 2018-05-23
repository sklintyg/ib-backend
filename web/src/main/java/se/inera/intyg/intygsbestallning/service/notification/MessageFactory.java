package se.inera.intyg.intygsbestallning.service.notification;

import org.springframework.stereotype.Service;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import javax.annotation.PostConstruct;

@Service
public class MessageFactory {

    private ST utredningTemplate;
    private ST forfraganTemplate;
    private STGroup templateGroup;

    @PostConstruct
    public void initTemplates() {
        templateGroup = new STGroupFile("notification-templates/notifications.stg");
        utredningTemplate = templateGroup.getInstanceOf("utredning");
        forfraganTemplate = templateGroup.getInstanceOf("forfragan");
    }

    public String buildBodyForUtredning(String message, String url) {
        utredningTemplate.add("data", new MailContent(message, url));
        return utredningTemplate.render();
    }

    public String buildBodyForForfragan(String message, String url) {
        forfraganTemplate.add("data", new MailContent(message, url));
        return forfraganTemplate.render();
    }

    private class MailContent {
        public String message;
        public String url;

        MailContent(String message, String url) {
            this.message = message;
            this.url = url;
        }
    }
}
