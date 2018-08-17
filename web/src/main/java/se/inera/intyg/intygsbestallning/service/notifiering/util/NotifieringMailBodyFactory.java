/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.intygsbestallning.service.notifiering.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import javax.annotation.PostConstruct;

@Component
public class NotifieringMailBodyFactory {

    @Value("${mail.ib.host.url}")
    private String hostUrl;

    private STGroup templateGroup;

    @PostConstruct
    public void initTemplates() {
        templateGroup = new STGroupFile("notification-templates/notifications.stg");
    }

    public String buildBodyForUtredning(String message, String url) {
        ST utredningTemplate = templateGroup.getInstanceOf("mail");
        utredningTemplate.add("data", new MailContent(hostUrl, message, "utredningen",  url));
        return utredningTemplate.render();
    }

    public String buildBodyForForfragan(String message, String url) {
        ST forfraganTemplate = templateGroup.getInstanceOf("mail");
        forfraganTemplate.add("data", new MailContent(hostUrl, message, "förfrågan", url));
        return forfraganTemplate.render();
    }

    private static final class MailContent {
        public String hostUrl;
        public String message;
        public String linkType;
        public String url;

        MailContent(String hostUrl, String message, String linkType, String url) {
            this.hostUrl = hostUrl;
            this.message = message;
            this.linkType = linkType;
            this.url = url;
        }
    }
}
