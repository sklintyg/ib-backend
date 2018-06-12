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
import se.inera.intyg.intygsbestallning.service.forfragan.ExternForfraganService;
import se.inera.intyg.intygsbestallning.service.user.UserService;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.utredning.GetUtredningResponse;

@RestController
@RequestMapping("/api/externforfragningar")
public class ExternForfraganController {

    private static final String AVCCEPT_EXTERN_FORFRAGAN_NOT_ALLOWED = "User is not allowed to accept ExternFörfrågan";
    private static final String AVVISA_EXTERN_FORFRAGAN_NOT_ALLOWED = "User is not allowed to avvisa ExternFörfrågan";

    @Autowired
    private ExternForfraganService externForfraganService;

    @Autowired
    private UserService userService;

    private AuthoritiesValidator authoritiesValidator = new AuthoritiesValidator();

    @PrometheusTimeMethod(name = "accept_externforfragan_duration_seconds", help = "Some helpful info here")
    @PostMapping(path = "/{utredningsId}/accept",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetUtredningResponse> acceptExternForfragan(@PathVariable("utredningsId") Long utredningsId,
                                                                      @RequestBody String vardenhetHsaId) {
        IbUser user = userService.getUser();
        authoritiesValidator.given(user).privilege(AuthoritiesConstants.PRIVILEGE_ACCEPTERA_EXTERNFORFRAGAN)
                .orThrow(new IbAuthorizationException(AVCCEPT_EXTERN_FORFRAGAN_NOT_ALLOWED));
        return ResponseEntity.ok(externForfraganService.acceptExternForfragan(utredningsId, user.getCurrentlyLoggedInAt().getId(),
                vardenhetHsaId));
    }

    @PrometheusTimeMethod(name = "avvisa_externforfragan_duration_seconds", help = "Some helpful info here")
    @PostMapping(path = "/{utredningsId}/avvisa",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetUtredningResponse> avvisaExternForfragan(@PathVariable("utredningsId") Long utredningsId,
                                                                      @RequestBody String kommentar) {
        IbUser user = userService.getUser();
        authoritiesValidator.given(user)
                .privilege(AuthoritiesConstants.PRIVILEGE_AVVISA_EXTERNFORFRAGAN)
                .features(AuthoritiesConstants.FEATURE_EXTERNFORFRAGAN_FAR_AVVISAS)
                .orThrow(new IbAuthorizationException(AVVISA_EXTERN_FORFRAGAN_NOT_ALLOWED));
        return ResponseEntity.ok(externForfraganService.avvisaExternForfragan(utredningsId, user.getCurrentlyLoggedInAt().getId(),
                kommentar));
    }
}
