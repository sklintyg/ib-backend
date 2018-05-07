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
package se.inera.intyg.intygsbestallning.service.forfragan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.integration.hsa.services.HsaOrganizationsService;
import se.inera.intyg.intygsbestallning.persistence.repository.ExternForfraganRepository;
import se.inera.intyg.intygsbestallning.persistence.repository.RegistreradVardenhetRepository;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.ForfraganSvarRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.ForfraganSvarResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.filter.ListForfraganFilter;
import se.inera.intyg.intygsbestallning.web.controller.api.filter.ListForfraganFilterStatus;
import se.inera.intyg.intygsbestallning.web.controller.api.filter.SelectItem;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExternForfraganServiceImpl implements ExternForfraganService {

    @Autowired
    private ExternForfraganRepository externForfraganRepository;

    @Autowired
    private RegistreradVardenhetRepository registreradVardenhetRepository;

    @Autowired
    private HsaOrganizationsService hsaOrganizationsService;

    @Override
    public ForfraganSvarResponse besvaraForfragan(Long forfraganId, ForfraganSvarRequest svarRequest) {
        return null;
    }

    @Override
    public ListForfraganFilter buildListForfraganFilter(String vardenhetHsaId) {
        return new ListForfraganFilter(findLandstingBeingRelatedToVardenhet(vardenhetHsaId), buildStatusesForListForfraganFilter());
    }

    /*
     * Möjliga val för filterkriteriet är "Visa alla" samt de landsting som är kopplade till vårdenheten.
     * Förvalt värde: Visa alla.
     */
    private List<SelectItem> findLandstingBeingRelatedToVardenhet(String vardenhetHsaId) {
        List<String> vardgivareHsaIdList = registreradVardenhetRepository.findVardgivareHsaIdRegisteredForVardenhet(vardenhetHsaId);
        return vardgivareHsaIdList.stream()
                .map(veHsaId -> new SelectItem(veHsaId, hsaOrganizationsService.getVardgivareInfo(veHsaId).getNamn()))
                .collect(Collectors.toList());
    }

    private List<ListForfraganFilterStatus> buildStatusesForListForfraganFilter() {
        return Arrays.asList(ListForfraganFilterStatus.values());
    }
}
