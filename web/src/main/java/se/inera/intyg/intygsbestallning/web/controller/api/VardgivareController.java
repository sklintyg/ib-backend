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

import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import se.inera.intyg.intygsbestallning.service.user.UserService;
import se.inera.intyg.intygsbestallning.service.vardgivare.VardgivareService;
import se.inera.intyg.intygsbestallning.service.vardgivare.dto.VardgivarVardenhetListItem;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.vardenhet.GetVardenheterForVardgivareResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.vardenhet.ListVardenheterForVardgivareRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.vardenhet.ListVardenheterForVardgivareResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.vardenhet.SearchForVardenhetResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.vardenhet.UpdateRegiFormRequest;

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
        ensureVGAuthPreconditions(user);

        return ResponseEntity.ok(vardgivareService.findVardenheterForVardgivareWithFilter(user.getCurrentlyLoggedInAt().getId(), request));
    }

    @PrometheusTimeMethod(name = "update_regiform_for_registered_vardenhet_duration_seconds", help = "Some helpful info here")
    @PutMapping(path = "/vardenheter/{vardenhetHsaId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VardgivarVardenhetListItem> updateRegiform(
            @PathVariable("vardenhetHsaId") String vardenhetHsaId, @RequestBody UpdateRegiFormRequest request) {
        IbUser user = userService.getUser();
        ensureVGAuthPreconditions(user);

        return ResponseEntity
                .ok(vardgivareService.updateRegiForm(user.getCurrentlyLoggedInAt().getId(), vardenhetHsaId, request.getRegiForm()));
    }

    @PrometheusTimeMethod(name = "add_registered_vardenhet_duration_seconds", help = "Some helpful info here")
    @PostMapping(path = "/vardenheter/{vardenhetHsaId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VardgivarVardenhetListItem> addVardenhet(
            @PathVariable("vardenhetHsaId") String vardenhetHsaId, @RequestBody UpdateRegiFormRequest request) {
        IbUser user = userService.getUser();
        ensureVGAuthPreconditions(user);

        return ResponseEntity
                .ok(vardgivareService.addVardenhet(user.getCurrentlyLoggedInAt().getId(), vardenhetHsaId, request.getRegiForm()));
    }

    @PrometheusTimeMethod(name = "delete_registered_vardenhet_duration_seconds", help = "Some helpful info here")
    @DeleteMapping(path = "/vardenheter/{vardenhetHsaId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Response deleteRegistreradVardenhet(
            @PathVariable("vardenhetHsaId") String vardenhetHsaId) {
        IbUser user = userService.getUser();
        ensureVGAuthPreconditions(user);

        vardgivareService.delete(user.getCurrentlyLoggedInAt().getId(), vardenhetHsaId);
        return Response.ok().build();
    }

    @PrometheusTimeMethod(name = "search_vardenhetbyhsaid__duration_seconds", help = "Some helpful info here")
    @GetMapping(path = "/vardenheter/{vardenhetHsaId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SearchForVardenhetResponse> searchVardenhetByHsaId(@PathVariable("vardenhetHsaId") String vardenhetHsaId) {
        IbUser user = userService.getUser();
        ensureVGAuthPreconditions(user);

        return ResponseEntity.ok(vardgivareService.searchVardenhetByHsaId(user.getCurrentlyLoggedInAt().getId(), vardenhetHsaId));
    }

    private void ensureVGAuthPreconditions(IbUser user) {
        authoritiesValidator.given(user).privilege(AuthoritiesConstants.PRIVILEGE_HANTERA_VARDENHETER_FOR_VARDGIVARE)
                .orThrow(
                        new IbAuthorizationException("User does not have required privilege PRIVILEGE_HANTERA_VARDENHETER_FOR_VARDGIVARE"));

        if (user.getCurrentlyLoggedInAt().getType() != SelectableHsaEntityType.VG) {
            throw new IbAuthorizationException("User is not logged in at a Vardgivare");
        }
    }
}
