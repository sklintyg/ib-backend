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

import org.springframework.transaction.annotation.Transactional;
import se.inera.intyg.infra.integration.hsa.model.Vardenhet;
import se.inera.intyg.infra.integration.hsa.services.HsaOrganizationsService;
import se.inera.intyg.intygsbestallning.persistence.model.VardenhetPreference;
import se.inera.intyg.intygsbestallning.persistence.repository.VardenhetPreferenceRepository;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.vardenhet.VardenhetPreferenceRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.vardenhet.VardenhetPreferenceResponse;

/**
 * Created by marced on 2018-04-23.
 */
@Service
@Transactional
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
    public VardenhetPreferenceResponse getHsaAdressInfo(String hsaId) {
        return new VardenhetPreferenceResponse(
                buildVardenhetPreferenceFromHsaVardenhet(hsaId, hsaOrganizationsService.getVardenhet(hsaId)));
    }

    @Override
    public VardenhetPreferenceResponse setVardEnhetPreference(String hsaId, VardenhetPreferenceRequest vardenhetPreferenceRequest) {
        VardenhetPreference entity = vardenhetPreferenceRepository.findByVardenhetHsaId(hsaId)
                .orElse(buildVardenhetPreferenceFromHsaVardenhet(hsaId, null));
        entity.setMottagarNamn(vardenhetPreferenceRequest.getMottagarNamn());
        entity.setAdress(vardenhetPreferenceRequest.getAdress());
        entity.setPostnummer(vardenhetPreferenceRequest.getPostnummer());
        entity.setPostort(vardenhetPreferenceRequest.getPostort());
        entity.setTelefonnummer(vardenhetPreferenceRequest.getTelefonnummer());
        entity.setEpost(vardenhetPreferenceRequest.getEpost());

        return new VardenhetPreferenceResponse(vardenhetPreferenceRepository.save(entity));
    }

    private VardenhetPreference createInitialPreference(String hsaId) {
        VardenhetPreference initialPreference = buildVardenhetPreferenceFromHsaVardenhet(hsaId,
                hsaOrganizationsService.getVardenhet(hsaId));

        return vardenhetPreferenceRepository.save(initialPreference);

    }

    @Override
    public VardenhetPreferenceResponse setVardEnhetSvarPreference(String hsaId, String svar) {
        VardenhetPreference entity = vardenhetPreferenceRepository.findByVardenhetHsaId(hsaId)
                .orElse(buildVardenhetPreferenceFromHsaVardenhet(hsaId, null));
        entity.setStandardsvar(svar);
        return new VardenhetPreferenceResponse(vardenhetPreferenceRepository.save(entity));
    }

    @NotNull
    private VardenhetPreference buildVardenhetPreferenceFromHsaVardenhet(String hsaId, Vardenhet vardenhet) {
        VardenhetPreference preference = new VardenhetPreference();
        preference.setVardenhetHsaId(hsaId);
        if (vardenhet != null) {
            preference.setMottagarNamn(vardenhet.getNamn());
            preference.setAdress(vardenhet.getPostadress());
            preference.setPostnummer(vardenhet.getPostnummer());
            preference.setPostort(vardenhet.getPostort());
            preference.setTelefonnummer(vardenhet.getTelefonnummer());
            preference.setEpost(vardenhet.getEpost());
        }
        return preference;
    }
}
