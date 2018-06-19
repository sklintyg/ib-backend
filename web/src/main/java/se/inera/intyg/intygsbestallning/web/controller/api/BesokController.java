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
import se.inera.intyg.intygsbestallning.persistence.model.type.DeltagarProfessionTyp;
import se.inera.intyg.intygsbestallning.service.besok.BesokService;
import se.inera.intyg.intygsbestallning.service.user.UserService;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.besok.AddArbetsdagarRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.besok.RedovisaBesokRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.besok.RegisterBesokRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.besok.RegisterBesokResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.besok.ReportBesokAvvikelseVardenRequest;
import se.inera.intyg.intygsbestallning.web.responder.dto.ReportBesokAvvikelseRequest;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/vardadmin/bestallningar/{utredningId}/besok")
public class BesokController {

    private static final String EDIT_NOT_ALLOWED = "User is not allowed to edit the requested resource";

    private final UserService userService;
    private final BesokService besokService;

    private AuthoritiesValidator authoritiesValidator = new AuthoritiesValidator();

    public BesokController(final UserService userService, final BesokService besokService) {
        this.userService = userService;
        this.besokService = besokService;
    }

    @GetMapping("/professiontyper")
    public ResponseEntity<DeltagarProfessionTyp[]> getProfessionsTyper() {
        return ResponseEntity.ok(DeltagarProfessionTyp.values());
    }

    @PostMapping
    public RegisterBesokResponse registerBesok(@PathVariable Long utredningId, @RequestBody  final RegisterBesokRequest request) {

        final IbUser user = userService.getUser();
        authoritiesValidator.given(user)
                .privilege(AuthoritiesConstants.PRIVILEGE_HANTERA_BESOK)
                .orThrow(new IbAuthorizationException(EDIT_NOT_ALLOWED));

        return besokService.registerBesok(utredningId, null, RegisterBesokRequest.from(request));
    }

    @PutMapping("/{besokId}")
    public RegisterBesokResponse updateBesok(@PathVariable Long utredningId, @PathVariable Long besokId,
                                             @RequestBody final RegisterBesokRequest request) {

        final IbUser user = userService.getUser();
        authoritiesValidator.given(user)
                .privilege(AuthoritiesConstants.PRIVILEGE_HANTERA_BESOK)
                .orThrow(new IbAuthorizationException(EDIT_NOT_ALLOWED));

        return besokService.registerBesok(utredningId, besokId, RegisterBesokRequest.from(request));
    }

    @PutMapping("/{besokId}/avvikelse")
    public void createBesokAvvikelse(@PathVariable Long besokId, @RequestBody final ReportBesokAvvikelseVardenRequest request) {

        final IbUser user = userService.getUser();
        authoritiesValidator.given(user)
                .privilege(AuthoritiesConstants.PRIVILEGE_HANTERA_BESOK)
                .orThrow(new IbAuthorizationException(EDIT_NOT_ALLOWED));

        besokService.reportBesokAvvikelse(ReportBesokAvvikelseRequest.from(besokId, request, user.getNamn()));
    }

    @PutMapping("/{besokId}/avboka")
    public void avbokaBesok(@PathVariable("besokId") Long besokId) {

        final IbUser user = userService.getUser();
        authoritiesValidator.given(user)
                .privilege(AuthoritiesConstants.PRIVILEGE_HANTERA_BESOK)
                .orThrow(new IbAuthorizationException(EDIT_NOT_ALLOWED));

        besokService.avbokaBesok(besokId);
    }

    @PutMapping("/redovisa")
    public void redovisaBesok(@PathVariable Long utredningId, @RequestBody final RedovisaBesokRequest request) {

        final IbUser user = userService.getUser();
        authoritiesValidator.given(user)
                .privilege(AuthoritiesConstants.PRIVILEGE_HANTERA_BESOK)
                .orThrow(new IbAuthorizationException(EDIT_NOT_ALLOWED));

        besokService.redovisaBesok(utredningId, request);
    }

    @PostMapping("/addarbetsdagar")
    public LocalDate addArbetsdagar(@RequestBody AddArbetsdagarRequest request) {
        return besokService.addArbetsdagar(request);
    }
}
