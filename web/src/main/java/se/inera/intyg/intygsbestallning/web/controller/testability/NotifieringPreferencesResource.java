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
package se.inera.intyg.intygsbestallning.web.controller.testability;

import java.util.Optional;

import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import se.inera.intyg.intygsbestallning.persistence.model.NotifieringPreference;
import se.inera.intyg.intygsbestallning.persistence.repository.NotifieringPreferenceRepository;

@RestController
@RequestMapping("/api/test/notificationpreferences")
@Profile({ "dev", "testability-api" })
public class NotifieringPreferencesResource {

    @Autowired
    protected NotifieringPreferenceRepository notifieringPreferenceRepository;

    @DeleteMapping(path = "/{hsaId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response deleteUtredning(@PathVariable("hsaId") String hsaId) {
        final Optional<NotifieringPreference> notifieringPreference = notifieringPreferenceRepository.findByHsaId(hsaId);
        if (notifieringPreference.isPresent()) {
            notifieringPreferenceRepository.delete(notifieringPreference.get());
        }
        return Response.ok().build();
    }

}
