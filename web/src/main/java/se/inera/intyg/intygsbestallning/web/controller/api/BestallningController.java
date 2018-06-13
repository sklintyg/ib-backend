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
import se.inera.intyg.intygsbestallning.auth.model.SelectableHsaEntityType;
import se.inera.intyg.intygsbestallning.common.exception.IbAuthorizationException;
import se.inera.intyg.intygsbestallning.monitoring.PrometheusTimeMethod;
import se.inera.intyg.intygsbestallning.persistence.model.status.UtredningStatus;
import se.inera.intyg.intygsbestallning.service.user.UserService;
import se.inera.intyg.intygsbestallning.service.utlatande.UtlatandeService;
import se.inera.intyg.intygsbestallning.service.utredning.BestallningService;
import se.inera.intyg.intygsbestallning.service.utredning.UtredningService;
import se.inera.intyg.intygsbestallning.service.utredning.dto.EndUtredningRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.bestallning.AvslutadBestallningListItem;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.bestallning.BestallningListItem;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.bestallning.GetAvslutadeBestallningarListResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.bestallning.GetBestallningListResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.bestallning.GetBestallningResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.bestallning.ListAvslutadeBestallningarRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.bestallning.ListBestallningRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.SaveFakturaForUtredningRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.utlatande.SendUtlatandeRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.filter.ListAvslutadeBestallningarFilter;
import se.inera.intyg.intygsbestallning.web.controller.api.filter.ListBestallningFilter;

import java.util.List;

@RestController
@RequestMapping("/api/vardadmin/bestallningar")
public class BestallningController {

    @Autowired
    private UserService userService;

    @Autowired
    private BestallningService bestallningService;

    @Autowired
    private UtlatandeService utlatandeService;

    @Autowired
    private UtredningService utredningService;

    private AuthoritiesValidator authoritiesValidator = new AuthoritiesValidator();

    @PrometheusTimeMethod(name = "list_bestallningar_for_vardenhet_duration_seconds", help = "Some helpful info here")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetBestallningListResponse> getBestallningarForVardenhet(@RequestBody ListBestallningRequest requestFilter) {
        IbUser user = userService.getUser();
        authoritiesValidator.given(user).privilege(AuthoritiesConstants.PRIVILEGE_LISTA_BESTALLNINGAR)
                .orThrow(new IbAuthorizationException("User does not have required privilege LISTA_BESTALLNINGAR"));

        if (user.getCurrentlyLoggedInAt().getType() != SelectableHsaEntityType.VE) {
            throw new IbAuthorizationException("User is not logged in at a Vårdenhet");
        }

        List<BestallningListItem> bestallningar = bestallningService
                .findOngoingBestallningarForVardenhet(user.getCurrentlyLoggedInAt().getId(), requestFilter);

        return ResponseEntity.ok(new GetBestallningListResponse(bestallningar, bestallningar.size()));
    }

    /**
     * Returns an object containing all possible filter values for the getBestallningarForVardenhet query.
     */
    @PrometheusTimeMethod(name = "get_bestallning_list_filter_duration_seconds", help = "Some helpful info here")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/list/filter")
    public ResponseEntity<ListBestallningFilter> getListBestallningFilter() {
        IbUser user = userService.getUser();
        authoritiesValidator.given(user).privilege(AuthoritiesConstants.PRIVILEGE_LISTA_BESTALLNINGAR)
                .orThrow(new IbAuthorizationException("User does not have required privilege LISTA_BESTALLNINGAR"));

        ListBestallningFilter listBestallningFilter = bestallningService.buildListBestallningFilter(user.getCurrentlyLoggedInAt().getId());
        return ResponseEntity.ok(listBestallningFilter);
    }

    @PrometheusTimeMethod(name = "list_avslutade_bestallningar_for_vardenhet_duration_seconds", help = "Some helpful info here")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/avslutade")
    public ResponseEntity<GetAvslutadeBestallningarListResponse> getAvslutadeBestallningarForVardenhet(
            @RequestBody ListAvslutadeBestallningarRequest request) {
        IbUser user = userService.getUser();
        authoritiesValidator.given(user).privilege(AuthoritiesConstants.PRIVILEGE_LISTA_BESTALLNINGAR)
                .orThrow(new IbAuthorizationException("User does not have required privilege LISTA_BESTALLNINGAR"));

        if (user.getCurrentlyLoggedInAt().getType() != SelectableHsaEntityType.VE) {
            throw new IbAuthorizationException("User is not logged in at a Vårdenhet");
        }

        List<AvslutadBestallningListItem> bestallningar = bestallningService
                .findAvslutadeBestallningarForVardenhet(user.getCurrentlyLoggedInAt().getId(), request);

        return ResponseEntity.ok(new GetAvslutadeBestallningarListResponse(bestallningar, bestallningar.size()));
    }

    /**
     * Returns an object containing all possible filter values for the getBestallningarForVardenhet query.
     */
    @PrometheusTimeMethod(name = "get_bestallning_list_filter_duration_seconds", help = "Some helpful info here")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/avslutade/list/filter")
    public ResponseEntity<ListAvslutadeBestallningarFilter> getListAvslutadeBestallningarFilter() {
        IbUser user = userService.getUser();
        authoritiesValidator.given(user).privilege(AuthoritiesConstants.PRIVILEGE_LISTA_BESTALLNINGAR)
                .orThrow(new IbAuthorizationException("User does not have required privilege LISTA_BESTALLNINGAR"));

        ListAvslutadeBestallningarFilter listAvslutadeBestallningarFilter = bestallningService
                .buildListAvslutadeBestallningarFilter(user.getCurrentlyLoggedInAt().getId());
        return ResponseEntity.ok(listAvslutadeBestallningarFilter);
    }

    @PrometheusTimeMethod(name = "get_utredning_duration_seconds", help = "Some helpful info here")
    @GetMapping(path = "/{utredningsId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetBestallningResponse> getUtredning(@PathVariable("utredningsId") Long utredningsId) {
        IbUser user = userService.getUser();
        authoritiesValidator.given(user).privilege(AuthoritiesConstants.PRIVILEGE_VISA_BESTALLNING)
                .orThrow(new IbAuthorizationException("User does not have required privilege VISA_BESTALLNING"));
        return ResponseEntity.ok(bestallningService.getBestallning(utredningsId, user.getCurrentlyLoggedInAt().getId()));
    }

    @PrometheusTimeMethod(name = "post_save_faktura_for_utredning_duration_seconds", help = "Some helpful info here")
    @PostMapping(path = "/{utredningsId}/faktura", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> saveFakturaIdForUtredning(@PathVariable("utredningsId") Long utredningsId,
            @RequestBody SaveFakturaForUtredningRequest request) {
        IbUser user = userService.getUser();
        authoritiesValidator.given(user).privilege(AuthoritiesConstants.PRIVILEGE_LISTA_BESTALLNINGAR)
                .orThrow(new IbAuthorizationException("User does not have required privilege PRIVILEGE_LISTA_BESTALLNINGAR"));

        bestallningService.saveFakturaIdForUtredning(utredningsId, request, user.getCurrentlyLoggedInAt().getId());
        return ResponseEntity.ok().build();
    }

    @PrometheusTimeMethod(name = "send_utlatande_duration_seconds", help = "Some helpful info here")
    @PostMapping(path = "/{utredningsId}/sendutlatande", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UtredningStatus> sendUtlatande(@PathVariable("utredningsId") Long utredningId,
                                                         @RequestBody SendUtlatandeRequest request) {
        IbUser user = userService.getUser();
        authoritiesValidator.given(user).privilege(AuthoritiesConstants.PRIVILEGE_LISTA_BESTALLNINGAR)
                .orThrow(new IbAuthorizationException("User does not have required privilege PRIVILEGE_LISTA_BESTALLNINGAR"));

        utlatandeService.sendUtlatande(utredningId, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{utredningId}/avsluta")
    public ResponseEntity avslutaUtredning(
            @PathVariable("utredningId") final String utredningId) {

        final IbUser user = userService.getUser();

        authoritiesValidator.given(user).privilege(AuthoritiesConstants.PRIVILEGE_AVSLUTA_UTREDNING)
                .orThrow(new IbAuthorizationException("User does not have required privilege PRIVILEGE_AVSLUTA_UTREDNING"));

        utredningService.avslutaUtredning(EndUtredningRequest.from(utredningId, user));

        return ResponseEntity.ok().build();
    }
}
