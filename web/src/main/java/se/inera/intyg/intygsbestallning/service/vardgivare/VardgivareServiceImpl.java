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

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;

import se.inera.intyg.infra.integration.hsa.model.Vardenhet;
import se.inera.intyg.infra.integration.hsa.services.HsaOrganizationsService;
import se.inera.intyg.intygsbestallning.persistence.model.RegistreradVardenhet;
import se.inera.intyg.intygsbestallning.persistence.model.type.RegiFormTyp;
import se.inera.intyg.intygsbestallning.persistence.repository.RegistreradVardenhetRepository;
import se.inera.intyg.intygsbestallning.service.util.GenericComparator;
import se.inera.intyg.intygsbestallning.service.util.PagingUtil;
import se.inera.intyg.intygsbestallning.service.vardgivare.dto.VardenhetItem;
import se.inera.intyg.intygsbestallning.service.vardgivare.dto.VardenhetListItem;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.FreeTextSearchable;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.GetVardenheterForVardgivareResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.ListVardenheterForVardgivareRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.ListVardenheterForVardgivareResponse;

@Service
public class VardgivareServiceImpl implements VardgivareService {

    @Autowired
    private RegistreradVardenhetRepository registreradVardenhetRepository;

    @Autowired
    private HsaOrganizationsService hsaOrganizationsService;

    @Override
    public GetVardenheterForVardgivareResponse listVardenheterForVardgivare(String vardgivareHsaId) {
        List<RegistreradVardenhet> byVardgivareHsaId = registreradVardenhetRepository.findByVardgivareHsaId(vardgivareHsaId);

        GetVardenheterForVardgivareResponse response = new GetVardenheterForVardgivareResponse();

        for (RegistreradVardenhet rv : byVardgivareHsaId) {
            // Resolve hsaunit metadata we don't hold ie. just name in this case
            final Vardenhet vardenhet = hsaOrganizationsService.getVardenhet(rv.getVardenhetHsaId());
            final VardenhetItem vei = VardenhetItem.from(rv, vardenhet.getNamn());

            if (rv.getVardenhetRegiForm() == RegiFormTyp.PRIVAT) {
                response.getPrivat().add(vei);
            } else if (rv.getVardenhetRegiForm() == RegiFormTyp.EGET_LANDSTING) {
                response.getEgetLandsting().add(vei);
            } else {
                response.getAnnatLandsting().add(vei);
            }
        }

        return response;
    }

    @Override
    public ListVardenheterForVardgivareResponse findVardenheterForVardgivareWithFilter(String vardgivareHsaId,
            ListVardenheterForVardgivareRequest requestFilter) {
        List<VardenhetListItem> enheter = registreradVardenhetRepository.findByVardgivareHsaId(vardgivareHsaId)
                .stream()
                .map(rv -> VardenhetListItem.from(rv, hsaOrganizationsService.getVardenhet(rv.getVardenhetHsaId())))
                .filter(veli -> buildFreeTextPredicate(veli, requestFilter.getFreeText()))
                .sorted((o1, o2) -> GenericComparator.compare(VardenhetListItem.class, o1, o2, requestFilter.getOrderBy(),
                        requestFilter.isOrderByAsc()))
                .collect(Collectors.toList());

        // Paging. We need to perform some bounds-checking...
        int total = enheter.size();
        if (total == 0) {
            return new ListVardenheterForVardgivareResponse(enheter, 0);
        }

        Pair<Integer, Integer> bounds = PagingUtil.getBounds(total, requestFilter.getPageSize(), requestFilter.getCurrentPage());
        List<VardenhetListItem> paged = enheter.subList(bounds.getFirst(), bounds.getSecond() + 1);

        return new ListVardenheterForVardgivareResponse(paged, total);

    }

    private boolean buildFreeTextPredicate(FreeTextSearchable veli, String freeText) {
        return Strings.isNullOrEmpty(freeText) || veli.toSearchString().toLowerCase().contains(freeText.toLowerCase());
    }
}
