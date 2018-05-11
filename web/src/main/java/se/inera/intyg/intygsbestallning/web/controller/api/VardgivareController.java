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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import se.inera.intyg.intygsbestallning.auth.IbUser;
import se.inera.intyg.intygsbestallning.auth.authorities.AuthoritiesConstants;
import se.inera.intyg.intygsbestallning.auth.authorities.validation.AuthoritiesValidator;
import se.inera.intyg.intygsbestallning.auth.model.SelectableHsaEntityType;
import se.inera.intyg.intygsbestallning.common.exception.IbAuthorizationException;
import se.inera.intyg.intygsbestallning.monitoring.PrometheusTimeMethod;
import se.inera.intyg.intygsbestallning.service.user.UserService;
import se.inera.intyg.intygsbestallning.service.vardgivare.VardgivareService;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.GetVardenheterForVardgivareResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.ListVardenheterForVardgivareRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.ListVardenheterForVardgivareResponse;

@RestController
@RequestMapping("/api/vardgivare")
public class VardgivareController {

    @Autowired
    private UserService userService;

    @Autowired
    private VardgivareService vardgivareService;

    private AuthoritiesValidator authoritiesValidator = new AuthoritiesValidator();

    @PrometheusTimeMethod(name = "get_registrerade_vardenheter_duration_seconds", help = "Some helpful info here")
    @GetMapping(path = "/vardenheter", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<GetVardenheterForVardgivareResponse> getRegistreradeVardenheter() {
        IbUser user = userService.getUser();

        authoritiesValidator.given(user).privilege(AuthoritiesConstants.PRIVILEGE_HANTERA_VARDENHETER_FOR_VARDGIVARE)
                .orThrow(new IbAuthorizationException("User is not allowed to list or manage vardenheter for vardgivare"));

        return ResponseEntity.ok(vardgivareService.listVardenheterForVardgivare(user.getCurrentlyLoggedInAt().getId()));
    }

    @PrometheusTimeMethod(name = "list_vardenheter_for_vardgivare_duration_seconds", help = "Some helpful info here")
    @PostMapping(path = "/vardenheter", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ListVardenheterForVardgivareResponse> findVardenheterForVardgivareWithFilter(
            @RequestBody ListVardenheterForVardgivareRequest request) {
        IbUser user = userService.getUser();
        authoritiesValidator.given(user).privilege(AuthoritiesConstants.PRIVILEGE_HANTERA_VARDENHETER_FOR_VARDGIVARE)
                .orThrow(
                        new IbAuthorizationException("User does not have required privilege PRIVILEGE_HANTERA_VARDENHETER_FOR_VARDGIVARE"));

        if (user.getCurrentlyLoggedInAt().getType() != SelectableHsaEntityType.VG) {
            throw new IbAuthorizationException("User is not logged in at a Vardgivare");
        }

        return ResponseEntity.ok(vardgivareService.findVardenheterForVardgivareWithFilter(user.getCurrentlyLoggedInAt().getId(), request));
    }
}
