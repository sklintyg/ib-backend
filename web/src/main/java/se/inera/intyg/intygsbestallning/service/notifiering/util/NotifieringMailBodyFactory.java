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

import org.springframework.stereotype.Component;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import javax.annotation.PostConstruct;

@Component
public class NotifieringMailBodyFactory {

    private STGroup templateGroup;

    @PostConstruct
    public void initTemplates() {
        templateGroup = new STGroupFile("notification-templates/notifications.stg");
    }

    public String buildBodyForUtredning(String message, String url) {
        ST utredningTemplate = templateGroup.getInstanceOf("utredning");
        utredningTemplate.add("data", new MailContent(message, url));
        return utredningTemplate.render();
    }

    public String buildBodyForForfragan(String message, String url) {
        ST forfraganTemplate = templateGroup.getInstanceOf("forfragan");
        forfraganTemplate.add("data", new MailContent(message, url));
        return forfraganTemplate.render();
    }

    private static final class MailContent {
        public String message;
        public String url;

        MailContent(String message, String url) {
            this.message = message;
            this.url = url;
        }
    }
}
