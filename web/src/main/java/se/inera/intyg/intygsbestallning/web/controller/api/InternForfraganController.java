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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import se.inera.intyg.infra.monitoring.annotation.PrometheusTimeMethod;
import se.inera.intyg.intygsbestallning.auth.IbUser;
import se.inera.intyg.intygsbestallning.auth.authorities.AuthoritiesConstants;
import se.inera.intyg.intygsbestallning.auth.authorities.validation.AuthoritiesValidator;
import se.inera.intyg.intygsbestallning.auth.model.IbSelectableHsaEntity;
import se.inera.intyg.intygsbestallning.auth.model.SelectableHsaEntityType;
import se.inera.intyg.intygsbestallning.common.exception.IbAuthorizationException;
import se.inera.intyg.intygsbestallning.common.exception.IbErrorCodeEnum;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.service.forfragan.ExternForfraganService;
import se.inera.intyg.intygsbestallning.service.forfragan.InternForfraganService;
import se.inera.intyg.intygsbestallning.service.user.UserService;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.forfragan.ForfraganSvarRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.forfragan.GetForfraganListResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.forfragan.GetInternForfraganResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.forfragan.InternForfraganSvarItem;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.forfragan.ListForfraganRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.filter.ListForfraganFilter;

@RestController
@RequestMapping("/api/vardadmin/internforfragningar")
public class InternForfraganController {

    @Autowired
    private UserService userService;

    @Autowired
    private ExternForfraganService externForfraganService;

    @Autowired
    private InternForfraganService internForfraganService;

    private AuthoritiesValidator authoritiesValidator = new AuthoritiesValidator();

    @PrometheusTimeMethod
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetForfraganListResponse> getFilteredForfragningarForUser(@RequestBody ListForfraganRequest request) {
        IbUser user = userService.getUser();

        authoritiesValidator.given(user).privilege(AuthoritiesConstants.PRIVILEGE_LISTA_FORFRAGNINGAR)
                .orThrow(new IbAuthorizationException("User is not allowed to view the requested resource"));

        // Utredningar där vårdenheten har en förfrågan.
        GetForfraganListResponse response = externForfraganService
                .findForfragningarForVardenhetHsaIdWithFilter(user.getCurrentlyLoggedInAt().getId(), request);
        return ResponseEntity.ok(response);
    }

    /**
     * Returns an object containing all possible filter values for the getAllForfragningarForUser query.
     */
    @PrometheusTimeMethod
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/list/filter")
    public ResponseEntity<ListForfraganFilter> getListForfragningarFilter() {
        IbUser user = userService.getUser();
        authoritiesValidator.given(user).privilege(AuthoritiesConstants.PRIVILEGE_LISTA_FORFRAGNINGAR)
                .orThrow(new IbAuthorizationException("User does not have required privilege LISTA_FORFRAGNINGAR"));

        ListForfraganFilter listBestallningFilter = externForfraganService.buildListForfraganFilter(user.getCurrentlyLoggedInAt().getId());
        return ResponseEntity.ok(listBestallningFilter);
    }

    @PrometheusTimeMethod
    @GetMapping(path = "/{utredningsId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetInternForfraganResponse> getForfragan(@PathVariable("utredningsId") Long utredningsId) {
        IbUser user = userService.getUser();
        ensureBesvaraAuthPreconditions(user);

        GetInternForfraganResponse forfragan = internForfraganService.getInternForfragan(utredningsId,
                user.getCurrentlyLoggedInAt().getId());
        return ResponseEntity.ok(forfragan);
    }

    @PrometheusTimeMethod
    @PostMapping(path = "/{utredningsId}/besvara", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InternForfraganSvarItem> besvaraForfragan(@PathVariable("utredningsId") Long utredningsId,
            @RequestBody ForfraganSvarRequest request) {

        IbUser user = userService.getUser();
        ensureBesvaraAuthPreconditions(user);

        return ResponseEntity.ok(internForfraganService.besvaraInternForfragan(utredningsId, request));
    }

    private void ensureBesvaraAuthPreconditions(IbUser user) {
        final IbSelectableHsaEntity currentUnit = user.getCurrentlyLoggedInAt();

        if (currentUnit == null || !currentUnit.getType().equals(SelectableHsaEntityType.VE)) {
            throw new IbServiceException(IbErrorCodeEnum.BAD_REQUEST, "This operation is only valid on VE unit type");
        }
        authoritiesValidator.given(user).privilege(AuthoritiesConstants.PRIVILEGE_HANTERA_INTERNFORFRAGAN)
                .orThrow(
                        new IbAuthorizationException("User does not have required privilege PRIVILEGE_HANTERA_INTERNFORFRAGAN"));

    }
}
