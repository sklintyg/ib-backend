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
package se.inera.intyg.intygsbestallning.service.vardgivare;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.intyg.intygsbestallning.persistence.model.RegiFormTyp;
import se.inera.intyg.intygsbestallning.persistence.model.RegistreradVardenhet;
import se.inera.intyg.intygsbestallning.persistence.repository.RegistreradVardenhetRepository;
import se.inera.intyg.intygsbestallning.service.vardgivare.dto.VardenhetItem;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.GetVardenheterForVardgivareResponse;

import java.util.List;

@Service
public class VardgivareServiceImpl implements VardgivareService {

    @Autowired
    private RegistreradVardenhetRepository registreradVardenhetRepository;

    @Override
    public GetVardenheterForVardgivareResponse listVardenheterForVardgivare(String vardgivareHsaId) {
        List<RegistreradVardenhet> byVardgivareHsaId = registreradVardenhetRepository.findByVardgivareHsaId(vardgivareHsaId);

        GetVardenheterForVardgivareResponse response = new GetVardenheterForVardgivareResponse();

        for (RegistreradVardenhet rv : byVardgivareHsaId) {
            if (rv.getVardenhetRegiForm() == RegiFormTyp.PRIVAT) {
                response.getPrivat().add(VardenhetItem.from(rv));
            } else if (rv.getVardenhetVardgivareHsaId().equals(vardgivareHsaId)) {
                response.getEgetLandsting().add(VardenhetItem.from(rv));
            } else {
                response.getAnnatLandsting().add(VardenhetItem.from(rv));
            }
        }

        return response;
    }
}
