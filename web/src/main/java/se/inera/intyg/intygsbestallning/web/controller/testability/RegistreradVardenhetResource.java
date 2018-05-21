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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.intygsbestallning.persistence.model.RegistreradVardenhet;
import se.inera.intyg.intygsbestallning.persistence.repository.RegistreradVardenhetRepository;

import javax.ws.rs.core.Response;

@RestController
@RequestMapping("/api/test/registreradvardenhet")
@Profile({ "dev", "testability-api" })
public class RegistreradVardenhetResource {

    @Autowired
    protected RegistreradVardenhetRepository registreradVardenhetRepository;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Response createRegistreradVardenhet(@RequestBody RegistreradVardenhet registreradVardenhet) {
        registreradVardenhetRepository.save(registreradVardenhet);
        return Response.ok().build();
    }

    @DeleteMapping(path = "/vardgivare/{vardgivareHsaId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response deleteRegistreradeVardenheterForVardgivare(@PathVariable("vardgivareHsaId") String vardgivareHsaId) {
        registreradVardenhetRepository.findByVardgivareHsaId(vardgivareHsaId)
                .stream()
                .forEach(vardenhet -> registreradVardenhetRepository.delete(vardenhet));
        return Response.ok().build();
    }

}
