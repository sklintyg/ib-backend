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
package se.inera.intyg.intygsbestallning.web.controller.integration;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

/**
 * Created by marced on 2018-08-14.
 */

@Component
public class MaillinkRedirectUrlBuilder {
    private STGroup templateGroup;

    @Value("${mail.ib.host.url}")
    private String hostUrl;

    @PostConstruct
    public void initTemplates() {
        templateGroup = new STGroupFile("notification-templates/mail-redirect-links.stg");
    }

    public String buildVardadminInternForfraganUrl(Long utredningId, Long internforfraganId) {
        ST internforfraganTemplate = templateGroup.getInstanceOf("internforfragan_url");
        internforfraganTemplate.add("host", hostUrl);
        internforfraganTemplate.add("utredningId", utredningId);
        internforfraganTemplate.add("internforfraganId", internforfraganId);

        return internforfraganTemplate.render();
    }

    public String buildVardadminInternforfraganRedirect(String utredningId) {
        ST internforfraganTemplate = templateGroup.getInstanceOf("internforfragan_redirect");
        internforfraganTemplate.add("host", hostUrl);
        internforfraganTemplate.add("utredningId", utredningId);

        return internforfraganTemplate.render();
    }

    public String buildVardadminBestallningUrl(Long utredningId) {
        ST externForfragan = templateGroup.getInstanceOf("bestallning_url");
        externForfragan.add("host", hostUrl);
        externForfragan.add("utredningId", utredningId);

        return externForfragan.render();
    }

    public String buildVardadminBestallningRedirect(String utredningId) {
        ST bestallningTemplate = templateGroup.getInstanceOf("bestallning_redirect");
        bestallningTemplate.add("host", hostUrl);
        bestallningTemplate.add("utredningId", utredningId);

        return bestallningTemplate.render();
    }

    public String buildSamordnareUtredningUrl(Long utredningId) {
        ST externForfragan = templateGroup.getInstanceOf("samordnareutredning_url");
        externForfragan.add("host", hostUrl);
        externForfragan.add("utredningId", utredningId);

        return externForfragan.render();
    }

    public String buildSamordnareUtredningRedirect(String utredningId) {
        ST utredningTemplate = templateGroup.getInstanceOf("samordnareutredning_redirect");
        utredningTemplate.add("host", hostUrl);
        utredningTemplate.add("utredningId", utredningId);

        return utredningTemplate.render();
    }

    public String buildErrorRedirect(String reason) {
        ST errorTemplate = templateGroup.getInstanceOf("errorview_redirect");
        errorTemplate.add("host", hostUrl);
        errorTemplate.add("reason", reason);

        return errorTemplate.render();
    }
}
