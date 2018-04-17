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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.intygsbestallning.auth.IbUser;
import se.inera.intyg.intygsbestallning.auth.authorities.AuthoritiesConstants;
import se.inera.intyg.intygsbestallning.auth.authorities.validation.AuthoritiesValidator;
import se.inera.intyg.intygsbestallning.common.exception.IbAuthorizationException;
import se.inera.intyg.intygsbestallning.service.forfragan.ForfraganService;
import se.inera.intyg.intygsbestallning.service.user.UserService;
import se.inera.intyg.intygsbestallning.service.utredning.UtredningService;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.ForfraganListItem;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.ForfraganSvarRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.ForfraganSvarResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.GetForfraganListResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.GetForfraganResponse;

import java.util.List;

@RestController
@RequestMapping("/api/forfragningar")
public class ForfraganController {

    @Autowired
    private UserService userService;

    @Autowired
    private UtredningService utredningService;

    @Autowired
    private ForfraganService forfraganService;

    private AuthoritiesValidator authoritiesValidator = new AuthoritiesValidator();

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetForfraganListResponse> getAllForfragningarForUser() {
        IbUser user = userService.getUser();

        authoritiesValidator.given(user).privilege(AuthoritiesConstants.PRIVILEGE_LISTA_FORFRAGNINGAR)
                .orThrow(new IbAuthorizationException("User is not allowed to view the requested resource"));

        // Utredningar där vårdenheten har en förfrågan.
        List<ForfraganListItem> forfragningar = utredningService.findForfragningarForVardenhetHsaId(user.getCurrentlyLoggedInAt().getId());
        return ResponseEntity.ok(new GetForfraganListResponse(forfragningar));
    }

    @GetMapping(path = "/{forfraganId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetForfraganResponse> getForfragan(@PathVariable("forfraganId") Long forfraganId) {
        IbUser user = userService.getUser();
        authoritiesValidator.given(user).privilege(AuthoritiesConstants.PRIVILEGE_VISA_FORFRAGAN)
                .orThrow(new IbAuthorizationException("User is not allowed to view the requested resource"));

        GetForfraganResponse forfragan = utredningService.getForfragan(forfraganId, user.getCurrentlyLoggedInAt().getId());
        return ResponseEntity.ok(forfragan);
    }

    @PostMapping(path = "/{forfragan}/besvara", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ForfraganSvarResponse> besvaraForfragan(@PathVariable("forfraganId") Long forfraganId,
            ForfraganSvarRequest request) {
        IbUser user = userService.getUser();
        authoritiesValidator.given(user).privilege(AuthoritiesConstants.PRIVILEGE_VISA_FORFRAGAN)
                .orThrow(new IbAuthorizationException("User is not allowed to view the requested resource"));
        ForfraganSvarResponse response = forfraganService.besvaraForfragan(forfraganId, request);
        return ResponseEntity.ok(response);
    }
}
