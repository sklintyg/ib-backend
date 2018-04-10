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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.infra.security.common.model.Role;
import se.inera.intyg.intygsbestallning.auth.IbUser;
import se.inera.intyg.intygsbestallning.auth.authorities.AuthoritiesConstants;
import se.inera.intyg.intygsbestallning.auth.authorities.validation.AuthoritiesValidator;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.repository.UtredningRepository;
import se.inera.intyg.intygsbestallning.service.user.UserService;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.GetUtredningListResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.GetUtredningResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.UtredningListItem;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/utredning")
public class UtredningController {

    @Autowired
    private UserService userService;

    @Autowired
    private UtredningRepository utredningRepository;

    private AuthoritiesValidator authoritiesValidator = new AuthoritiesValidator();

    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetUtredningListResponse> getAllUtredningarForUser() {
        IbUser user = userService.getUser();



//        authoritiesValidator.given(getWebCertUserService().getUser(), intygsTyp)
//                .features(se.inera.intyg.infra.security.common.model.AuthoritiesConstants.FEATURE_HANTERA_INTYGSUTKAST)
//                .privilege(se.inera.intyg.infra.security.common.model.AuthoritiesConstants.PRIVILEGE_SKRIVA_INTYG)
//                .orThrow();


        // String currentHsaId = user.getCurrentlyLoggedInAt().getId();
        Role currentRole = user.getCurrentRole();

        // Temp code, fix this later.. split into separate endpoints or implementations...
        if (currentRole.getName().equals(AuthoritiesConstants.ROLE_FMU_VARDADMIN)) {
            authoritiesValidator.given(user).privilege(AuthoritiesConstants.PRIVILEGE_LISTA_FORFRAGNINGAR).orThrow();

            // Do a VARDADMIN search for the current VÅRDENHET HSA ID for Utredningar...
        } else if (currentRole.getName().equals(AuthoritiesConstants.ROLE_FMU_SAMORDNARE)) {
            authoritiesValidator.given(user).privilege(AuthoritiesConstants.PRIVILEGE_LISTA_UTREDNINGAR).orThrow();
            // Do a SAMORDNARE search...
        }

        List<Utredning> all = utredningRepository.findAll();
        List<UtredningListItem> listItems = all.stream().map(u -> convert(u)).collect(Collectors.toList());
        return ResponseEntity.ok(new GetUtredningListResponse(listItems));
    }

    @RequestMapping(value = "/{utredningId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetUtredningResponse> getUtredning(@PathVariable("utredningId") String utredningId) {
  //      IbUser user = userService.getUser();
        Utredning utredning = utredningRepository.findOne(utredningId);
        if (utredning != null) {
            return ResponseEntity.ok(convertUtredning(utredning));
        }
        return ResponseEntity.notFound().build();
    }

    private GetUtredningResponse convertUtredning(Utredning u) {
        GetUtredningResponse gur = new GetUtredningResponse();
        gur.setUtredningsId(u.getUtredningId());
        gur.setUtredningsTyp(u.getUtredningsTyp());
        gur.setBesvarasSenastDatum(u.getBesvarasSenastDatum().format(DateTimeFormatter.BASIC_ISO_DATE));
        gur.setInkomDatum(u.getInkomDatum().format(DateTimeFormatter.BASIC_ISO_DATE));
        gur.setInvanarePersonId(u.getInvanarePersonId());
        gur.setStatus(u.getStatus());
        gur.setVardgivareHsaId(u.getVardgivareHsaId());

        gur.setHandlaggareNamn(u.getHandlaggareNamn());
        gur.setHandlaggareTelefonnummer(u.getHandlaggareTelefonnummer());
        gur.setHandlaggareEpost(u.getHandlaggareEpost());

        gur.setBehovTolk(u.isBehovTolk());
        gur.setSprakTolk(u.getSprakTolk());
        return gur;
    }

    private UtredningListItem convert(Utredning u) {
        UtredningListItem uli = new UtredningListItem();
        uli.setUtredningsId(u.getUtredningId());
        uli.setUtredningsTyp(u.getUtredningsTyp());
        uli.setVardgivareNamn(u.getVardgivareHsaId() + "-namnet");
        uli.setStatus(u.getStatus());
        uli.setBesvarasSenastDatum(u.getBesvarasSenastDatum().format(DateTimeFormatter.BASIC_ISO_DATE));
        uli.setInkomDatum(u.getInkomDatum().format(DateTimeFormatter.BASIC_ISO_DATE));
        return uli;
    }
}
