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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.intygsbestallning.auth.IbUser;
import se.inera.intyg.intygsbestallning.auth.authorities.AuthoritiesConstants;
import se.inera.intyg.intygsbestallning.auth.authorities.validation.AuthoritiesValidator;
import se.inera.intyg.intygsbestallning.auth.model.SelectableHsaEntityType;
import se.inera.intyg.intygsbestallning.common.exception.IbAuthorizationException;
import se.inera.intyg.intygsbestallning.service.user.UserService;
import se.inera.intyg.intygsbestallning.service.utredning.UtredningService;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.BestallningListItem;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.GetBestallningListResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.filter.ListBestallningFilter;

import java.util.List;

@RestController
@RequestMapping("/api/vardadmin/bestallningar")
public class BestallningController {

    @Autowired
    private UserService userService;

    @Autowired
    private UtredningService utredningService;

    private AuthoritiesValidator authoritiesValidator = new AuthoritiesValidator();

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetBestallningListResponse> getBestallningarForVardenhet() {
        IbUser user = userService.getUser();
        authoritiesValidator.given(user).privilege(AuthoritiesConstants.PRIVILEGE_LISTA_BESTALLNINGAR)
                .orThrow(new IbAuthorizationException("User does not have required privilege LISTA_BESTALLNINGAR"));

        if (user.getCurrentlyLoggedInAt().getType() != SelectableHsaEntityType.VE) {
            throw new IbAuthorizationException("User is not logged in at a Vårdenhet");
        }

        List<BestallningListItem> bestallningar = utredningService
                .findOngoingBestallningarForVardenhet(user.getCurrentlyLoggedInAt().getId());

        return ResponseEntity.ok(new GetBestallningListResponse(bestallningar, bestallningar.size()));
    }

    /**
     * Returns an object containing all possible filter values for the getBestallningarForVardenhet query.
     * @return
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/list/filter")
    public ResponseEntity<ListBestallningFilter> getListBestallningFilter() {
        IbUser user = userService.getUser();
        authoritiesValidator.given(user).privilege(AuthoritiesConstants.PRIVILEGE_LISTA_BESTALLNINGAR)
                .orThrow(new IbAuthorizationException("User does not have required privilege LISTA_BESTALLNINGAR"));

        ListBestallningFilter listBestallningFilter = utredningService.buildListBestallningFilter(user.getCurrentlyLoggedInAt().getId());
        return ResponseEntity.ok(listBestallningFilter);
    }
}
