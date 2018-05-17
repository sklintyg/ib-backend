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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;

import se.inera.intyg.infra.integration.hsa.client.OrganizationUnitService;
import se.inera.intyg.infra.integration.hsa.exception.HsaServiceCallException;
import se.inera.intyg.infra.integration.hsa.model.Vardenhet;
import se.inera.intyg.infra.integration.hsa.services.HsaOrganizationsService;
import se.inera.intyg.intygsbestallning.common.exception.IbErrorCodeEnum;
import se.inera.intyg.intygsbestallning.common.exception.IbNotFoundException;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.persistence.model.RegistreradVardenhet;
import se.inera.intyg.intygsbestallning.persistence.model.type.RegiFormTyp;
import se.inera.intyg.intygsbestallning.persistence.repository.RegistreradVardenhetRepository;
import se.inera.intyg.intygsbestallning.service.util.GenericComparator;
import se.inera.intyg.intygsbestallning.service.util.PagingUtil;
import se.inera.intyg.intygsbestallning.service.vardgivare.dto.VardenhetItem;
import se.inera.intyg.intygsbestallning.service.vardgivare.dto.VardgivarVardenhetListItem;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.FreeTextSearchable;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.GetVardenheterForVardgivareResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.ListVardenheterForVardgivareRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.ListVardenheterForVardgivareResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.SearchForVardenhetResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.SearchFormVardenhetResultCodesEnum;
import se.riv.infrastructure.directory.organization.gethealthcareunitresponder.v1.HealthCareUnitType;

@Service
public class VardgivareServiceImpl implements VardgivareService {

    private static final Logger LOG = LoggerFactory.getLogger(VardgivareService.class);

    @Autowired
    private RegistreradVardenhetRepository registreradVardenhetRepository;

    @Autowired
    private HsaOrganizationsService hsaOrganizationsService;

    @Autowired
    private OrganizationUnitService organizationUnitService;

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
        List<VardgivarVardenhetListItem> enheter = registreradVardenhetRepository.findByVardgivareHsaId(vardgivareHsaId)
                .stream()
                .map(rv -> VardgivarVardenhetListItem.from(rv, hsaOrganizationsService.getVardenhet(rv.getVardenhetHsaId())))
                .filter(veli -> buildFreeTextPredicate(veli, requestFilter.getFreeText()))
                .sorted((o1, o2) -> GenericComparator.compare(VardgivarVardenhetListItem.class, o1, o2, requestFilter.getOrderBy(),
                        requestFilter.isOrderByAsc()))
                .collect(Collectors.toList());

        // Paging. We need to perform some bounds-checking...
        int total = enheter.size();
        if (total == 0) {
            return new ListVardenheterForVardgivareResponse(enheter, 0);
        }

        Pair<Integer, Integer> bounds = PagingUtil.getBounds(total, requestFilter.getPageSize(), requestFilter.getCurrentPage());
        List<VardgivarVardenhetListItem> paged = enheter.subList(bounds.getFirst(), bounds.getSecond() + 1);

        return new ListVardenheterForVardgivareResponse(paged, total);

    }

    @Override
    public VardgivarVardenhetListItem updateRegiForm(String vardgivareHsaId, String vardenhetHsaId, String regiForm) {
        RegistreradVardenhet rv = registreradVardenhetRepository
                .findByVardgivareHsaIdAndVardenhetHsaId(vardgivareHsaId, vardenhetHsaId)
                .orElseThrow(() -> new IbNotFoundException(
                        "Could not update Regiform - a registered vardenhet with vardenhetHsaId '" + vardenhetHsaId
                                + "' does not exist for vardgivare with vardgivareHsaId '" + vardgivareHsaId + "'"));
        rv.setVardenhetRegiFormTyp(RegiFormTyp.valueOf(regiForm));
        RegistreradVardenhet updated = registreradVardenhetRepository.save(rv);

        return VardgivarVardenhetListItem.from(updated, hsaOrganizationsService.getVardenhet(updated.getVardenhetHsaId()));

    }

    @Override
    public void delete(String vardgivareHsaId, String vardenhetHsaId) {
        RegistreradVardenhet rv = registreradVardenhetRepository
                .findByVardgivareHsaIdAndVardenhetHsaId(vardgivareHsaId, vardenhetHsaId)
                .orElseThrow(() -> new IbNotFoundException(String.format(
                        "Could not delete - registrered vardenhetHsaId '%s' does not exist for vardgivareHsaId '%s'",
                        vardenhetHsaId, vardgivareHsaId)));

        registreradVardenhetRepository.delete(rv);
    }

    @Override
    public SearchForVardenhetResponse searchVardenhetByHsaId(String vardgivarHsaId, String vardenhetHsaId) {
        try {
            // We use getHealthCareUnit first to make sure this is a VE type unit
            final HealthCareUnitType healthCareUnit = organizationUnitService.getHealthCareUnit(vardenhetHsaId);
            if (!Boolean.TRUE.equals(healthCareUnit.isUnitIsHealthCareUnit())) {
                return new SearchForVardenhetResponse(null, SearchFormVardenhetResultCodesEnum.INVALID_UNIT_TYPE);
            }

            final Vardenhet vardenhet = hsaOrganizationsService.getVardenhet(vardenhetHsaId);
            // Vardenhet retrieved using GetUnit does not have vardgivarHsaId set - use vardgivarHsaId from HealthCareUnitType
            vardenhet.setVardgivareHsaId(healthCareUnit.getHealthCareProviderHsaId());

            // Check if this enhet already exists for this vardgivare
            if (registreradVardenhetRepository.findByVardgivareHsaIdAndVardenhetHsaId(vardgivarHsaId, vardenhetHsaId).isPresent()) {
                return new SearchForVardenhetResponse(VardgivarVardenhetListItem.from(vardenhet),
                        SearchFormVardenhetResultCodesEnum.ALREADY_EXISTS);
            }

            return new SearchForVardenhetResponse(VardgivarVardenhetListItem.from(vardenhet), SearchFormVardenhetResultCodesEnum.OK_TO_ADD);

        } catch (HsaServiceCallException e) {
            LOG.error("HsaServiceCallException (HSA returned no result) while querying HSA for hsaId " + vardenhetHsaId, e);
            return new SearchForVardenhetResponse(null, SearchFormVardenhetResultCodesEnum.NO_MATCH, e.getMessage());
        } catch (RuntimeException e) {
            LOG.error("RuntimeException while while querying HSA for hsaId " + vardenhetHsaId, e);
            return new SearchForVardenhetResponse(null, SearchFormVardenhetResultCodesEnum.SEARCH_ERROR, e.getMessage());
        }

    }

    @Override
    public VardgivarVardenhetListItem addVardenhet(String vardgivarHsaId, String vardenhetHsaId, String regiForm) {

        // retrieve the candidate we should add
        final SearchForVardenhetResponse candidate = searchVardenhetByHsaId(vardgivarHsaId, vardenhetHsaId);
        if (!SearchFormVardenhetResultCodesEnum.OK_TO_ADD.equals(candidate.getResultCode())) {
            throw new IbServiceException(IbErrorCodeEnum.BAD_STATE,
                    String.format(
                            "Could not add vardenhet '%s' to vardgivare '%s', precondition that failed was '%s' and errorMessage '%s'",
                            vardenhetHsaId, vardgivarHsaId, candidate.getResultCode(), candidate.getErrorMessage()));
        }

        RegistreradVardenhet rv = RegistreradVardenhet.RegistreradVardenhetBuilder
                .aRegistreradVardenhet()
                .withVardgivareHsaId(vardgivarHsaId)
                .withVardenhetHsaId(vardenhetHsaId)
                .withVardenhetVardgivareHsaId(candidate.getVardenhet().getVardenhetVardgivarHsaId())
                .withVardenhetRegiForm(RegiFormTyp.valueOf(regiForm))
                .build();
        registreradVardenhetRepository.save(rv);
        return candidate.getVardenhet();
    }

    private boolean buildFreeTextPredicate(FreeTextSearchable veli, String freeText) {
        return Strings.isNullOrEmpty(freeText) || veli.toSearchString().toLowerCase().contains(freeText.toLowerCase());
    }
}
