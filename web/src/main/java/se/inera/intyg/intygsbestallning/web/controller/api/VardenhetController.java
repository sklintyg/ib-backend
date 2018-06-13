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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import se.inera.intyg.intygsbestallning.auth.IbUser;
import se.inera.intyg.intygsbestallning.auth.authorities.AuthoritiesConstants;
import se.inera.intyg.intygsbestallning.auth.authorities.validation.AuthoritiesValidator;
import se.inera.intyg.intygsbestallning.auth.model.IbSelectableHsaEntity;
import se.inera.intyg.intygsbestallning.auth.model.SelectableHsaEntityType;
import se.inera.intyg.intygsbestallning.common.exception.IbAuthorizationException;
import se.inera.intyg.intygsbestallning.common.exception.IbErrorCodeEnum;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.monitoring.PrometheusTimeMethod;
import se.inera.intyg.intygsbestallning.service.user.UserService;
import se.inera.intyg.intygsbestallning.service.vardenhet.VardenhetService;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.vardenhet.VardenhetPreferenceRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.vardenhet.VardenhetPreferenceResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.vardenhet.VardenhetSvarPreferenceRequest;

/**
 * Created by marced on 2018-04-23.
 */
@RestController
@RequestMapping("/api/vardadmin/vardenhet")
public class VardenhetController {

    @Autowired
    private UserService userService;

    @Autowired
    private VardenhetService vardenhetService;

    private AuthoritiesValidator authoritiesValidator = new AuthoritiesValidator();

    @PrometheusTimeMethod(name = "get_preference_for_vardenhet_duration_seconds", help = "Some helpful info here")
    @GetMapping(path = "/preference", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<VardenhetPreferenceResponse> getPreferenceForVardenhet() {
        IbUser user = userService.getUser();
        ensureUpdateAllowed(user);
        return ResponseEntity.ok(vardenhetService.getVardEnhetPreference(user.getCurrentlyLoggedInAt().getId()));
    }
    @PrometheusTimeMethod(name = "get_hsaAdressInfo_for_vardenhet_duration_seconds", help = "Some helpful info here")
    @GetMapping(path = "/fromhsa", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<VardenhetPreferenceResponse> getHsaAdressInfo() {
        IbUser user = userService.getUser();
        ensureUpdateAllowed(user);
        return ResponseEntity.ok(vardenhetService.getHsaAdressInfo(user.getCurrentlyLoggedInAt().getId()));
    }

    @PrometheusTimeMethod(name = "set_preference_for_vardenhet_duration_seconds", help = "Some helpful info here")
    @PutMapping(path = "/preference", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<VardenhetPreferenceResponse> setPreferenceForVardenhet(
            @RequestBody final VardenhetPreferenceRequest vardenhetPreferenceRequest) {
        IbUser user = userService.getUser();
        ensureUpdateAllowed(user);

        return ResponseEntity
                .ok(vardenhetService.setVardEnhetPreference(user.getCurrentlyLoggedInAt().getId(), vardenhetPreferenceRequest));
    }

    @PrometheusTimeMethod(name = "get_svarpreference_for_vardenhet_duration_seconds", help = "Some helpful info here")
    @GetMapping(path = "/preference/svar", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<VardenhetPreferenceResponse> getSvarPreferenceForVardenhet() {
        IbUser user = userService.getUser();
        ensureUpdateAllowed(user);

        return ResponseEntity.ok(vardenhetService.getVardEnhetPreference(user.getCurrentlyLoggedInAt().getId()));
    }

    @PrometheusTimeMethod(name = "set_svarpreference_for_vardenhet_duration_seconds", help = "Some helpful info here")
    @PutMapping(path = "/preference/svar", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<VardenhetPreferenceResponse> setSvarPreferenceForVardenhet(
            @RequestBody final VardenhetSvarPreferenceRequest vardenhetSvarPreferenceRequest) {
        IbUser user = userService.getUser();
        ensureUpdateAllowed(user);

        return ResponseEntity
                .ok(vardenhetService.setVardEnhetSvarPreference(user.getCurrentlyLoggedInAt().getId(),
                        vardenhetSvarPreferenceRequest.getStandardsvar()));
    }

    private void ensureUpdateAllowed(IbUser user) {
        final IbSelectableHsaEntity currentUnit = user.getCurrentlyLoggedInAt();

        if (currentUnit == null || !currentUnit.getType().equals(SelectableHsaEntityType.VE)) {
            throw new IbServiceException(IbErrorCodeEnum.BAD_REQUEST, "This operation is only valid on VE unit type");
        }

        authoritiesValidator.given(user).privilege(AuthoritiesConstants.PRIVILEGE_SPARA_VARDENHETPREFERENS)
                .orThrow(new IbAuthorizationException("User is not allowed to update the requested resource"));
    }
}
