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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.intygsbestallning.auth.IbUser;
import se.inera.intyg.intygsbestallning.auth.authorities.AuthoritiesConstants;
import se.inera.intyg.intygsbestallning.auth.authorities.validation.AuthoritiesValidator;
import se.inera.intyg.intygsbestallning.common.exception.IbAuthorizationException;
import se.inera.intyg.intygsbestallning.service.user.UserService;
import se.inera.intyg.intygsbestallning.service.utredning.UtredningService;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.GetUtredningListResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.GetUtredningResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.UtredningListItem;

import java.util.List;

@RestController
@RequestMapping("/api/utredningar")
public class UtredningController {

    @Autowired
    private UserService userService;

    @Autowired
    private UtredningService utredningService;

    private AuthoritiesValidator authoritiesValidator = new AuthoritiesValidator();

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetUtredningListResponse> getAllUtredningarForUser() {
        IbUser user = userService.getUser();
        authoritiesValidator.given(user).privilege(AuthoritiesConstants.PRIVILEGE_LISTA_UTREDNINGAR)
                .orThrow(new IbAuthorizationException("User is not allowed to view the requested resource"));

        // Do a SAMORDNARE search...
        List<UtredningListItem> utredningar = utredningService.findUtredningarByVardgivareHsaId(user.getCurrentlyLoggedInAt().getId());
        return ResponseEntity.ok(new GetUtredningListResponse(utredningar));
    }

    @GetMapping(path = "/{utredningId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetUtredningResponse> getUtredning(@PathVariable("utredningId") String utredningId) {
        IbUser user = userService.getUser();
        authoritiesValidator.given(user).privilege(AuthoritiesConstants.PRIVILEGE_VISA_UTREDNING)
                .orThrow(new IbAuthorizationException("User is not allowed to view the requested resource"));
        return ResponseEntity.ok(utredningService.getUtredning(utredningId, user.getCurrentlyLoggedInAt().getId()));
    }
}
