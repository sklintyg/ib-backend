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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.infra.security.authorities.AuthoritiesException;
import se.inera.intyg.infra.security.authorities.CommonAuthoritiesResolver;
import se.inera.intyg.intygsbestallning.auth.IbUser;
import se.inera.intyg.intygsbestallning.persistence.repository.AnvandarPreferenceRepository;
import se.inera.intyg.intygsbestallning.service.user.UserService;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.user.ChangeSelectedUnitRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.user.GetUserResponse;

import java.util.Arrays;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private AnvandarPreferenceRepository anvandarPreferenceRepository;

    @Autowired
    private CommonAuthoritiesResolver commonAuthoritiesResolver;

    @GetMapping
    public GetUserResponse getUser() {
        IbUser user = getIbUser();
        return new GetUserResponse(user);
    }

    /**
     * Changes the selected care unit in the security context for the logged in user.
     *
     * @param changeSelectedEnhetRequest
     * @return
     */
    @PostMapping(path = "/andraenhet", consumes = MediaType.APPLICATION_JSON_VALUE)
    public GetUserResponse changeSelectedUnitOnUser(@RequestBody ChangeSelectedUnitRequest changeSelectedEnhetRequest) {

        IbUser user = getIbUser();

        LOG.debug("Attempting to change selected unit for user '{}', currently selected unit is '{}'", user.getHsaId(),
                user.getValdVardenhet() != null ? user.getValdVardenhet().getId() : "<null>");

        boolean changeSuccess = user.changeValdVardenhet(changeSelectedEnhetRequest.getId());

        if (!changeSuccess) {
            throw new AuthoritiesException(String.format("Could not change active unit: Unit '%s' is not present in the MIUs for user '%s'",
                    changeSelectedEnhetRequest.getId(), user.getHsaId()));
        }

        user.setFeatures(
                commonAuthoritiesResolver.getFeatures(Arrays.asList(user.getCurrentlyLoggedInAt().getId())));

        LOG.debug("Selected unit is now '{}'", user.getCurrentlyLoggedInAt().getId());

        return new GetUserResponse(user);
    }

    private IbUser getIbUser() {
        IbUser user = userService.getUser();

        if (user == null) {
            throw new AuthoritiesException("No user in session");
        }

        return user;
    }

}
