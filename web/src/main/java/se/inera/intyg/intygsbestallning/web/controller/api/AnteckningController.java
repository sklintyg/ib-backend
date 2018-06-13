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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.intygsbestallning.auth.IbUser;
import se.inera.intyg.intygsbestallning.auth.authorities.AuthoritiesConstants;
import se.inera.intyg.intygsbestallning.auth.authorities.validation.AuthoritiesValidator;
import se.inera.intyg.intygsbestallning.common.exception.IbAuthorizationException;
import se.inera.intyg.intygsbestallning.monitoring.PrometheusTimeMethod;
import se.inera.intyg.intygsbestallning.service.anteckning.AnteckningService;
import se.inera.intyg.intygsbestallning.service.user.UserService;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.anteckning.CreateAnteckningRequest;

@RestController
@RequestMapping("/api/vardadmin/bestallningar/{utredningId}/anteckning")
public class AnteckningController {

    @Autowired
    private AnteckningService anteckningService;

    @Autowired
    private UserService userService;

    private AuthoritiesValidator authoritiesValidator = new AuthoritiesValidator();

    @PrometheusTimeMethod(name = "create_anteckning_duration_seconds", help = "Some helpful info here")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public void createAnteckning(@PathVariable Long utredningId, @RequestBody CreateAnteckningRequest request) {

        final IbUser user = userService.getUser();
        authoritiesValidator.given(user)
                .privilege(AuthoritiesConstants.PRIVILEGE_SPARA_ANTECKNING)
                .orThrow(new IbAuthorizationException("User is missing privilege SPARA_ANTECKNING"));

        anteckningService.createAnteckning(utredningId, request);
    }
}
