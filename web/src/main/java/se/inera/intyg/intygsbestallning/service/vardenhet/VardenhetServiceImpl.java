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
package se.inera.intyg.intygsbestallning.service.vardenhet;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import se.inera.intyg.infra.integration.hsa.model.Vardenhet;
import se.inera.intyg.infra.integration.hsa.services.HsaOrganizationsService;
import se.inera.intyg.intygsbestallning.persistence.model.VardenhetPreference;
import se.inera.intyg.intygsbestallning.persistence.repository.VardenhetPreferenceRepository;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.VardenhetPreferenceRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.VardenhetPreferenceResponse;

/**
 * Created by marced on 2018-04-23.
 */
@Service
public class VardenhetServiceImpl implements VardenhetService {

    @Autowired
    private HsaOrganizationsService hsaOrganizationsService;

    @Autowired
    private VardenhetPreferenceRepository vardenhetPreferenceRepository;

    @Override
    public VardenhetPreferenceResponse getVardEnhetPreference(String hsaId) {
        return new VardenhetPreferenceResponse(
                vardenhetPreferenceRepository.findByVardenhetHsaId(hsaId).orElseGet(() -> createInitialPreference(hsaId)));
    }

    @Override
    public VardenhetPreferenceResponse setVardEnhetPreference(String hsaId, VardenhetPreferenceRequest vardenhetPreferenceRequest) {
        VardenhetPreference entity = vardenhetPreferenceRepository.findByVardenhetHsaId(hsaId)
                .orElse(buildVardenhetPreference(hsaId, null));
        entity.setMottagarNamn(vardenhetPreferenceRequest.getMottagarNamn());
        entity.setAdress(vardenhetPreferenceRequest.getAdress());
        entity.setPostnummer(vardenhetPreferenceRequest.getPostnummer());
        entity.setPostort(vardenhetPreferenceRequest.getPostort());
        entity.setTelefonnummer(vardenhetPreferenceRequest.getTelefonnummer());
        entity.setEpost(vardenhetPreferenceRequest.getEpost());
        entity.setStandardsvar(vardenhetPreferenceRequest.getStandardsvar());

        return new VardenhetPreferenceResponse(vardenhetPreferenceRepository.save(entity));
    }

    private VardenhetPreference createInitialPreference(String hsaId) {
        VardenhetPreference initialPreference = buildVardenhetPreference(hsaId, hsaOrganizationsService.getVardenhet(hsaId));

        return vardenhetPreferenceRepository.save(initialPreference);

    }

    @NotNull
    private VardenhetPreference buildVardenhetPreference(String hsaId, Vardenhet vardenhet) {
        VardenhetPreference initialPreference = new VardenhetPreference();
        initialPreference.setVardenhetHsaId(hsaId);
        if (vardenhet != null) {
            initialPreference.setMottagarNamn(vardenhet.getNamn());
            initialPreference.setAdress(vardenhet.getPostadress());
            initialPreference.setPostnummer(vardenhet.getPostnummer());
            initialPreference.setPostort(vardenhet.getPostort());
            initialPreference.setTelefonnummer(vardenhet.getTelefonnummer());
            initialPreference.setEpost(vardenhet.getEpost());
        }
        return initialPreference;
    }
}
