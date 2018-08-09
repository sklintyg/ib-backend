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

import static java.util.stream.Collectors.toList;
import static se.inera.intyg.intygsbestallning.integration.myndighet.dto.RespondToPerformerRequestDto.RespondToPerformerRequestDtoBuilder.aRespondToPerformerRequestDto;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import se.inera.intyg.infra.integration.hsa.model.Vardenhet;
import se.inera.intyg.infra.integration.hsa.model.Vardgivare;
import se.inera.intyg.intygsbestallning.common.exception.IbErrorCodeEnum;
import se.inera.intyg.intygsbestallning.common.exception.IbExternalServiceException;
import se.inera.intyg.intygsbestallning.common.exception.IbExternalSystemEnum;
import se.inera.intyg.intygsbestallning.common.exception.IbNotFoundException;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.integration.myndighet.dto.RespondToPerformerRequestDto;
import se.inera.intyg.intygsbestallning.integration.myndighet.service.MyndighetIntegrationService;
import se.inera.intyg.intygsbestallning.persistence.model.ForfraganSvar;
import se.inera.intyg.intygsbestallning.persistence.model.InternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.status.InternForfraganStatus;
import se.inera.intyg.intygsbestallning.persistence.model.status.UtredningStatus;
import se.inera.intyg.intygsbestallning.persistence.model.type.UtforareTyp;
import se.inera.intyg.intygsbestallning.persistence.repository.ExternForfraganRepository;
import se.inera.intyg.intygsbestallning.persistence.repository.RegistreradVardenhetRepository;
import se.inera.intyg.intygsbestallning.service.handelse.HandelseUtil;
import se.inera.intyg.intygsbestallning.service.notifiering.send.NotifieringSendService;
import se.inera.intyg.intygsbestallning.service.util.GenericComparator;
import se.inera.intyg.intygsbestallning.service.util.PagingUtil;
import se.inera.intyg.intygsbestallning.service.utredning.BaseUtredningService;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.forfragan.ForfraganSvarRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.forfragan.ForfraganSvarResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.forfragan.GetForfraganListResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.forfragan.InternForfraganListItem;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.forfragan.InternForfraganListItemFactory;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.forfragan.ListForfraganRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.utredning.GetUtredningResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.filter.ListForfraganFilter;
import se.inera.intyg.intygsbestallning.web.controller.api.filter.ListForfraganFilterStatus;
import se.inera.intyg.intygsbestallning.web.controller.api.filter.SelectItem;

@Service
public class ExternForfraganServiceImpl extends BaseUtredningService implements ExternForfraganService {

    private static final Logger LOG = LoggerFactory.getLogger(ExternForfraganServiceImpl.class);

    private static final String KV_SVAR_BESTALLNING_ACCEPTERAT = "ACCEPTERAT";
    private static final String KV_SVAR_BESTALLNING_AVVISAT = "AVVISAT";

    @Autowired
    private ExternForfraganRepository externForfraganRepository;

    @Autowired
    private RegistreradVardenhetRepository registreradVardenhetRepository;

    @Autowired
    private InternForfraganListItemFactory internForfraganListItemFactory;

    @Autowired
    private MyndighetIntegrationService myndighetIntegrationService;

    @Autowired
    private NotifieringSendService notifieringSendService;

    @Override
    public ForfraganSvarResponse besvaraForfragan(Long forfraganId, ForfraganSvarRequest svarRequest) {
        return null;
    }

    @Override
    public GetForfraganListResponse findForfragningarForVardenhetHsaIdWithFilter(String vardenhetHsaId, ListForfraganRequest request) {
        long start = System.currentTimeMillis();
        List<Utredning> jpaList = externForfraganRepository
                .findByExternForfraganAndVardenhetHsaIdAndArkiveradFalse(vardenhetHsaId);
        LOG.info("Loading findByExternForfraganAndVardenhetHsaIdAndArkiveradFalse took {} ms", (System.currentTimeMillis() - start));
        start = System.currentTimeMillis();

        List<InternForfraganListItem> forfraganList = jpaList
                .stream()
                .map(utr -> internForfraganListItemFactory.from(utr, vardenhetHsaId))
                .collect(toList());
        LOG.info("internForfraganListItemFactory from:: took {} ms", (System.currentTimeMillis() - start));

        Map<InternForfraganStatus, List<ListForfraganFilterStatus>> statusMap = buildFilterStatusesForForfragan();

        // Set the "filterStatusar" on each item. This is context dependent.
        for (InternForfraganListItem fli : forfraganList) {
            fli.setFilterStatusar(statusMap.get(fli.getStatus()));
        }

        // If filtering by freeText or ordering by landsting we need all vardgivareNames
        boolean enrichedWithVardgivareNames = false;
        if (request.getFreeText() != null || request.getOrderBy().equals("vardgivareNamn")) {
            // Enrich with vårdgivare namn from HSA
            for (InternForfraganListItem fli : forfraganList) {
                fli.setVardgivareNamn(getVardgivareNamn(fli.getVardgivareHsaId()));
            }
            enrichedWithVardgivareNames = true;
        }

        // Apply the filter from the request.
        start = System.currentTimeMillis();
        List<InternForfraganListItem> filtered = forfraganList.stream()
                .filter(fli -> buildForfroganStatusPredicate(fli, request.getStatus()))
                .filter(fli -> buildToFromPredicate(fli.getInkomDatum(), request.getInkommetFromDate(), request.getInkommetToDate()))
                .filter(fli -> buildToFromPredicate(fli.getBesvarasSenastDatum(), request.getBesvarasSenastDatumFromDate(),
                        request.getBesvarasSenastDatumToDate()))
                .filter(fli -> buildToFromPredicate(fli.getPlaneringsDatum(), request.getPlaneringFromDate(), request.getPlaneringToDate()))
                .filter(fli -> buildFreeTextPredicate(fli, request.getFreeText()))
                .filter(fli -> buildVardgivareHsaIdPredicate(fli.getVardgivareHsaId(), request.getVardgivareHsaId()))
                .sorted((o1, o2) -> GenericComparator.compare(InternForfraganListItem.class, o1, o2, request.getOrderBy(),
                        request.isOrderByAsc()))
                .collect(toList());
        LOG.info("filtering of InternForfraganListItem took {} ms", (System.currentTimeMillis() - start));
        int total = filtered.size();
        if (total == 0) {
            return new GetForfraganListResponse(filtered, total);
        }

        // Paging...
        Pair<Integer, Integer> bounds = PagingUtil.getBounds(total, request.getPageSize(), request.getCurrentPage());
        List<InternForfraganListItem> paged = filtered.subList(bounds.getFirst(), bounds.getSecond() + 1);

        if (!enrichedWithVardgivareNames) {
            // Enrich with vardenhet namn from HSA
            start = System.currentTimeMillis();
            for (InternForfraganListItem fli : paged) {
                fli.setVardgivareNamn(getVardgivareNamn(fli.getVardgivareHsaId()));
            }
            LOG.info("enrich with vårdgivare name (second) took {} ms", (System.currentTimeMillis() - start));
        }

        return new GetForfraganListResponse(paged, total);
    }

    @Override
    public ListForfraganFilter buildListForfraganFilter(String vardenhetHsaId) {
        return new ListForfraganFilter(findLandstingBeingRelatedToVardenhet(vardenhetHsaId), buildStatusesForListForfraganFilter());
    }

    @Override
    @Transactional
    public GetUtredningResponse acceptExternForfragan(Long utredningId, String landstingHsaId, String vardenhetHsaId) {

        Utredning utredning = getUtredningForLandsting(utredningId, landstingHsaId, ImmutableList.of(UtredningStatus.VANTAR_PA_SVAR,
                UtredningStatus.TILLDELA_UTREDNING));

        InternForfragan internForfragan = utredning.getExternForfragan().get().getInternForfraganList().stream()
                .filter(i -> i.getVardenhetHsaId().equals(vardenhetHsaId))
                .findAny()
                .orElseThrow(() -> new IbNotFoundException(MessageFormat.format(
                        "Could not find internforfragan for {0} in utredning {1}", vardenhetHsaId, utredningId)));

        InternForfraganStatus internForfraganStatus = internForfragan.getStatus();
        if (internForfraganStatus != InternForfraganStatus.ACCEPTERAD_VANTAR_PA_TILLDELNINGSBESLUT
                && internForfraganStatus != InternForfraganStatus.DIREKTTILLDELAD) {
            throw new IbServiceException(IbErrorCodeEnum.BAD_STATE, MessageFormat.format(
                    "Internforfragan for {0} in utredning {1} is in an incorrect state", internForfragan.getVardenhetHsaId(),
                    utredning.getUtredningId()));
        }

        Vardenhet vardenhet;
        Vardgivare vardgivare;
        try {
            vardenhet = hsaOrganizationsService.getVardenhet(internForfragan.getVardenhetHsaId());
            String vardgivareHsaId = hsaOrganizationsService.getVardgivareOfVardenhet(internForfragan.getVardenhetHsaId());
            vardgivare = hsaOrganizationsService.getVardgivareInfo(vardgivareHsaId);
        } catch (RuntimeException re) {
            LOG.error("RuntimeException while while querying HSA for hsaId " + internForfragan.getVardenhetHsaId(), re);
            throw new IbExternalServiceException(IbErrorCodeEnum.EXTERNAL_ERROR, IbExternalSystemEnum.HSA, re.getMessage(), null);
        }

        ForfraganSvar forfraganSvar = internForfragan.getForfraganSvar();
        RespondToPerformerRequestDto request = aRespondToPerformerRequestDto()
                .withAssessmentId(utredning.getUtredningId())
                .withCareGiverId(vardgivare.getId())
                .withCareGiverName(vardgivare.getNamn())
                .withCareUnitId(internForfragan.getVardenhetHsaId())
                .withCareUnitName(vardenhet.getNamn())
                .withComment(forfraganSvar.getKommentar())
                .withEmail(forfraganSvar.getUtforareEpost())
                .withPhoneNumber(forfraganSvar.getUtforareTelefon())
                .withPostalAddress(forfraganSvar.getUtforareAdress())
                .withPostalCity(forfraganSvar.getUtforarePostort())
                .withPostalCode(forfraganSvar.getUtforarePostnr())
                .withResponseCode(KV_SVAR_BESTALLNING_ACCEPTERAT)
                .withSubcontractorName(forfraganSvar.getUtforareTyp() == UtforareTyp.UNDERLEVERANTOR ? forfraganSvar.getUtforareNamn()
                        : null)
                .build();

        myndighetIntegrationService.respondToPerformerRequest(request);

        internForfragan.setTilldeladDatum(LocalDateTime.now());

        utredning.getHandelseList().add(HandelseUtil.createExternForfraganBesvarad(true, userService.getUser().getNamn(),
                vardenhet.getNamn()));

        notifieringSendService.notifieraVardenhetTilldeladUtredning(utredning, internForfragan, vardgivare.getNamn());

        utredningRepository.saveUtredning(utredning);

        return createGetUtredningResponse(utredning);
    }

    @Override
    @Transactional
    public GetUtredningResponse avvisaExternForfragan(Long utredningId, String landstingHsaId, String kommentar) {

        if (Strings.isNullOrEmpty(kommentar)) {
            throw new IbServiceException(IbErrorCodeEnum.BAD_REQUEST, "Required argument \"kommentar\" is empty.");
        }

        Utredning utredning = getUtredningForLandsting(utredningId, landstingHsaId, ImmutableList.of(UtredningStatus.FORFRAGAN_INKOMMEN,
                UtredningStatus.VANTAR_PA_SVAR, UtredningStatus.TILLDELA_UTREDNING));

        myndighetIntegrationService.respondToPerformerRequest(aRespondToPerformerRequestDto()
                .withAssessmentId(utredningId)
                .withResponseCode(KV_SVAR_BESTALLNING_AVVISAT)
                .withComment(kommentar)
                .build());

        utredning.getExternForfragan().get().setAvvisatDatum(LocalDateTime.now());
        utredning.getExternForfragan().get().setAvvisatKommentar(kommentar);

        utredning.getHandelseList().add(HandelseUtil.createExternForfraganBesvarad(false, userService.getUser().getNamn(), null));

        utredningRepository.saveUtredning(utredning);

        return createGetUtredningResponse(utredning);
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

    private boolean buildForfroganStatusPredicate(InternForfraganListItem fli, String forfraganListStatus) {
        if (Strings.isNullOrEmpty(forfraganListStatus)) {
            return true;
        }
        try {
            ListForfraganFilterStatus status = ListForfraganFilterStatus.valueOf(forfraganListStatus);
            return fli.getFilterStatusar().contains(status);
        } catch (IllegalArgumentException e) {
            throw new IbServiceException(IbErrorCodeEnum.UNKNOWN_INTERNAL_PROBLEM, "Unknown filter status: '" + forfraganListStatus + "'");
        }

    }

    private String getVardgivareNamn(String vardgivareHsaId) {
        try {
            Vardgivare vardgivareInfo = hsaOrganizationsService.getVardgivareInfo(vardgivareHsaId);
            if (vardgivareInfo != null) {
                return vardgivareInfo.getNamn();
            }
            return vardgivareHsaId;
        } catch (Exception e) {
            return vardgivareHsaId;
        }
    }

    private Map<InternForfraganStatus, List<ListForfraganFilterStatus>> buildFilterStatusesForForfragan() {

        Map<InternForfraganStatus, List<ListForfraganFilterStatus>> statusMap = new HashMap<>();

        for (InternForfraganStatus ifs : InternForfraganStatus.values()) {
            List<ListForfraganFilterStatus> statuses = new ArrayList<>();
            statuses.add(ListForfraganFilterStatus.ALL);
            switch (ifs) {
                case INKOMMEN:
                    statuses.addAll(Arrays.asList(ListForfraganFilterStatus.PAGAENDE, ListForfraganFilterStatus.KRAVER_ATGARD));
                    break;
                case ACCEPTERAD_VANTAR_PA_TILLDELNINGSBESLUT:
                case TILLDELAD_VANTAR_PA_BESTALLNING:
                    statuses.addAll(Arrays.asList(ListForfraganFilterStatus.PAGAENDE, ListForfraganFilterStatus.VANTAR_ANNAN_AKTOR));
                    break;
                case AVVISAD:
                case EJ_TILLDELAD:
                case INGEN_BESTALLNING:
                case BESTALLD:
                    statuses.add(ListForfraganFilterStatus.AVSLUTADE);
                    break;
                default:
                    break;
            }
            statusMap.put(ifs, statuses);
        }
        return statusMap;
    }
}
