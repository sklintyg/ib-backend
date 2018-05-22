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
package se.inera.intyg.intygsbestallning.web.controller.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.infra.dynamiclink.model.DynamicLink;
import se.inera.intyg.infra.dynamiclink.service.DynamicLinkService;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.user.GetConfigResponse;

import java.util.Map;

/**
 * Created by marced on 2016-02-09.
 */
@RestController
@RequestMapping("/api/config")
public class ConfigController {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigController.class);

    private static final String PROJECT_VERSION_PROPERTY = "project.version";
    private static final String UTREDNING_PAMINNELSE_DAGAR_PROPERTY = "ib.utredning.paminnelse.dagar";
    private static final String FORFRAGAN_PAMINNELSE_DAGAR_PROPERTY = "ib.forfragan.paminnelse.dagar";
    private static final int UTREDNING_PAMINNELSE_DAGAR_DEFAULT_VALUE = 5;
    private static final int FORFRAGAN_PAMINNELSE_DAGAR_DEFAULT_VALUE = 2;

    @Autowired
    private DynamicLinkService dynamicLinkService;

    /**
     * Note - using Environment injection instead of @Value since the latter has some issues when injected into the
     * context of this @RestController.
     */
    @Autowired
    private Environment env;

    @GetMapping
    public GetConfigResponse getConfig() {
        Integer utredningPaminnelseDagar;
        try {
            utredningPaminnelseDagar = Integer.parseInt(env.getProperty(UTREDNING_PAMINNELSE_DAGAR_PROPERTY, ""));
        } catch (NumberFormatException e) {
            LOG.error("Configuration property '{}' has not been set to integer!", UTREDNING_PAMINNELSE_DAGAR_PROPERTY);
            utredningPaminnelseDagar = UTREDNING_PAMINNELSE_DAGAR_DEFAULT_VALUE;
        }

        Integer forfraganPaminnelseDagar;
        try {
            forfraganPaminnelseDagar = Integer.parseInt(env.getProperty(FORFRAGAN_PAMINNELSE_DAGAR_PROPERTY, ""));
        } catch (NumberFormatException e) {
            LOG.error("Configuration property '{}' has not been set to integer!", FORFRAGAN_PAMINNELSE_DAGAR_PROPERTY);
            forfraganPaminnelseDagar = FORFRAGAN_PAMINNELSE_DAGAR_DEFAULT_VALUE;
        }

        return GetConfigResponse.GetConfigResponseBuilder.aGetConfigResponse()
                .withVersion(env.getProperty(PROJECT_VERSION_PROPERTY))
                .withUtredningPaminnelseDagar(utredningPaminnelseDagar)
                .withForfraganPaminnelseDagar(forfraganPaminnelseDagar)
                .build();
    }

    @GetMapping(path = "/links", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, DynamicLink> getDynamicLinks() {
        return dynamicLinkService.getAllAsMap();
    }
}
