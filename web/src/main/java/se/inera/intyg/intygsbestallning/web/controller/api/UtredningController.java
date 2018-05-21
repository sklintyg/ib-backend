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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.intygsbestallning.auth.IbUser;
import se.inera.intyg.intygsbestallning.auth.authorities.AuthoritiesConstants;
import se.inera.intyg.intygsbestallning.auth.authorities.validation.AuthoritiesValidator;
import se.inera.intyg.intygsbestallning.common.exception.IbAuthorizationException;
import se.inera.intyg.intygsbestallning.monitoring.PrometheusTimeMethod;
import se.inera.intyg.intygsbestallning.service.besok.BesokService;
import se.inera.intyg.intygsbestallning.service.forfragan.InternForfraganService;
import se.inera.intyg.intygsbestallning.service.user.UserService;
import se.inera.intyg.intygsbestallning.service.utredning.UtredningService;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.forfragan.CreateInternForfraganRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.GetUtredningListResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.GetUtredningResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.ListUtredningRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.RegisterBesokRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.RegisterBesokResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.forfragan.TilldelaDirektRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.UtredningListItem;

import java.util.List;

@RestController
@RequestMapping("/api/utredningar")
public class UtredningController {

    private static final String VIEW_NOT_ALLOWED = "User is not allowed to view the requested resource";
    private static final String EDIT_NOT_ALLOWED = "User is not allowed to edit the requested resource";

    @Autowired
    private UserService userService;

    @Autowired
    private UtredningService utredningService;

    @Autowired
    private BesokService besokService;

    @Autowired
    private InternForfraganService internForfraganService;

    private AuthoritiesValidator authoritiesValidator = new AuthoritiesValidator();

    @PrometheusTimeMethod(name = "list_utredningar_for_user_GET_duration_seconds", help = "Some helpful info here")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetUtredningListResponse> getAllUtredningarForUser() {
        IbUser user = userService.getUser();
        authoritiesValidator.given(user).privilege(AuthoritiesConstants.PRIVILEGE_LISTA_UTREDNINGAR)
                .orThrow(new IbAuthorizationException(VIEW_NOT_ALLOWED));

        // Do a SAMORDNARE search...
        List<UtredningListItem> utredningar = utredningService.findExternForfraganByLandstingHsaId(user.getCurrentlyLoggedInAt().getId());
        return ResponseEntity.ok(new GetUtredningListResponse(utredningar, utredningar.size()));
    }

    @PrometheusTimeMethod(name = "list_utredningar_for_user_duration_seconds", help = "Some helpful info here")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetUtredningListResponse> getUtredningarForUser(@RequestBody ListUtredningRequest req) {
        IbUser user = userService.getUser();
        authoritiesValidator.given(user).privilege(AuthoritiesConstants.PRIVILEGE_LISTA_UTREDNINGAR)
                .orThrow(new IbAuthorizationException(VIEW_NOT_ALLOWED));

        // Do a SAMORDNARE search...
        GetUtredningListResponse response = utredningService
                .findExternForfraganByLandstingHsaIdWithFilter(user.getCurrentlyLoggedInAt().getId(), req);

        return ResponseEntity.ok(response);
    }

    @PrometheusTimeMethod(name = "get_utredning_duration_seconds", help = "Some helpful info here")
    @GetMapping(path = "/{utredningsId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetUtredningResponse> getUtredning(@PathVariable("utredningsId") Long utredningsId) {
        IbUser user = userService.getUser();
        authoritiesValidator.given(user).privilege(AuthoritiesConstants.PRIVILEGE_VISA_UTREDNING)
                .orThrow(new IbAuthorizationException(VIEW_NOT_ALLOWED));
        return ResponseEntity.ok(utredningService.getExternForfragan(utredningsId, user.getCurrentlyLoggedInAt().getId()));
    }

    @PrometheusTimeMethod(name = "create_internforfragan_duration_seconds", help = "Some helpful info here")
    @PostMapping(path = "/{utredningsId}/createinternforfragan",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetUtredningResponse> createInternForfragan(@PathVariable("utredningsId") Long utredningsId,
                                                                      @RequestBody CreateInternForfraganRequest req) {
        IbUser user = userService.getUser();
        authoritiesValidator.given(user).privilege(AuthoritiesConstants.PRIVILEGE_VISA_UTREDNING)
                .orThrow(new IbAuthorizationException(EDIT_NOT_ALLOWED));
        return ResponseEntity.ok(internForfraganService.createInternForfragan(utredningsId, user.getCurrentlyLoggedInAt().getId(), req));
    }

    @PrometheusTimeMethod(name = "tilldela_direkt_duration_seconds", help = "Some helpful info here")
    @PostMapping(path = "/{utredningsId}/tilldeladirekt",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetUtredningResponse> createInternForfragan(@PathVariable("utredningsId") Long utredningsId,
                                                                      @RequestBody TilldelaDirektRequest req) {
        IbUser user = userService.getUser();
        authoritiesValidator.given(user).privilege(AuthoritiesConstants.PRIVILEGE_VISA_UTREDNING)
                .orThrow(new IbAuthorizationException(EDIT_NOT_ALLOWED));
        return ResponseEntity.ok(internForfraganService.tilldelaDirekt(utredningsId, user.getCurrentlyLoggedInAt().getId(), req));
    }

    @PrometheusTimeMethod(name = "accept_internforfragan_duration_seconds", help = "Some helpful info here")
    @PostMapping(path = "/{utredningsId}/acceptinternforfragan",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetUtredningResponse> acceptInternForfragan(@PathVariable("utredningsId") Long utredningsId,
                                                                      @RequestBody String vardenhetHsaId) {
        IbUser user = userService.getUser();
        authoritiesValidator.given(user).privilege(AuthoritiesConstants.PRIVILEGE_VISA_UTREDNING)
                .orThrow(new IbAuthorizationException(EDIT_NOT_ALLOWED));
        return ResponseEntity.ok(internForfraganService.acceptInternForfragan(utredningsId, user.getCurrentlyLoggedInAt().getId(),
                vardenhetHsaId));
    }

    @PutMapping("/besok")
    public RegisterBesokResponse createBesok(final RegisterBesokRequest request) {

        final IbUser user = userService.getUser();
        authoritiesValidator.given(user)
                .privilege(AuthoritiesConstants.ROLE_FMU_SAMORDNARE)
                .privilege(AuthoritiesConstants.ROLE_FMU_VARDADMIN)
                .orThrow(new IbAuthorizationException(EDIT_NOT_ALLOWED));

        return besokService.registerNewBesok(request);
    }
}
