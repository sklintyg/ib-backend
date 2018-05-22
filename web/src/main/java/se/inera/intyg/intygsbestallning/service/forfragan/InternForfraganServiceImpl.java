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

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.inera.intyg.infra.integration.hsa.model.Vardenhet;
import se.inera.intyg.infra.integration.hsa.model.Vardgivare;
import se.inera.intyg.intygsbestallning.common.exception.IbAuthorizationException;
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
import se.inera.intyg.intygsbestallning.persistence.model.type.SvarTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.UtforareTyp;
import se.inera.intyg.intygsbestallning.service.handelse.HandelseUtil;
import se.inera.intyg.intygsbestallning.service.stateresolver.InternForfraganStatus;
import se.inera.intyg.intygsbestallning.service.stateresolver.UtredningStatus;
import se.inera.intyg.intygsbestallning.service.util.BusinessDaysBean;
import se.inera.intyg.intygsbestallning.service.utredning.BaseUtredningService;
import se.inera.intyg.intygsbestallning.service.utredning.UtredningService;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.forfragan.CreateInternForfraganRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.forfragan.TilldelaDirektRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.utredning.GetUtredningResponse;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;
import static se.inera.intyg.intygsbestallning.integration.myndighet.dto.RespondToPerformerRequestDto.RespondToPerformerRequestDtoBuilder.aRespondToPerformerRequestDto;
import static se.inera.intyg.intygsbestallning.persistence.model.ForfraganSvar.ForfraganSvarBuilder.aForfraganSvar;
import static se.inera.intyg.intygsbestallning.persistence.model.InternForfragan.InternForfraganBuilder.anInternForfragan;

@Service
@Transactional
public class InternForfraganServiceImpl extends BaseUtredningService implements InternForfraganService {

    private static final Logger LOG = LoggerFactory.getLogger(InternForfraganServiceImpl.class);

    private static final String KV_SVAR_BESTALLNING_ACCEPTERAT = "ACCEPTERAT";

    @Autowired
    private BusinessDaysBean businessDays;

    @Autowired
    private UtredningService utredningService;

    @Autowired
    private MyndighetIntegrationService myndighetIntegrationService;

    @Value("${ib.besvara.forfragan.arbetsdagar:2}")
    private int besvaraForfraganArbetsdagar;

    @Override
    @Transactional
    public GetUtredningResponse createInternForfragan(Long utredningId, String landstingHsaId, CreateInternForfraganRequest request) {

        Utredning utredning = getUtredning(utredningId, landstingHsaId, ImmutableList.of(UtredningStatus.FORFRAGAN_INKOMMEN,
                UtredningStatus.VANTAR_PA_SVAR, UtredningStatus.TILLDELA_UTREDNING));

        if (isNull(request.getVardenheter()) || request.getVardenheter().size() == 0) {
            throw new IbServiceException(IbErrorCodeEnum.BAD_REQUEST,
                    "At least one vardenhet must be selected to create an InternForfragan");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime besvarasSenastDatum = getBesvarasSenastDatum(now);

        // Remove duplicates
        List<String> newVardenheter = request.getVardenheter().stream()
                .filter(vardenhetHsaId -> utredning.getExternForfragan().getInternForfraganList().stream()
                        .map(InternForfragan::getVardenhetHsaId)
                        .noneMatch(vardenhetHsaId::equals))
                .collect(toList());

        utredning.getExternForfragan().getInternForfraganList().addAll(
                newVardenheter.stream()
                        .map(vardenhetHsaId -> anInternForfragan()
                                .withVardenhetHsaId(vardenhetHsaId)
                                .withSkapadDatum(now)
                                .withKommentar(request.getKommentar())
                                .withBesvarasSenastDatum(besvarasSenastDatum)
                                .build())
                        .collect(toList()));

        utredning.getHandelseList().addAll(
                newVardenheter.stream()
                        .map(vardenhetHsaId -> HandelseUtil.createForfraganSkickad(userService.getUser().getNamn(),
                                hsaOrganizationsService.getVardenhet(vardenhetHsaId).getNamn()))
                        .collect(toList()));

        utredningRepository.save(utredning);

        return utredningService.createGetUtredningResponse(utredning);
    }

    @Override
    @Transactional
    public GetUtredningResponse tilldelaDirekt(Long utredningId, String landstingHsaId, TilldelaDirektRequest request) {

        Utredning utredning = getUtredning(utredningId, landstingHsaId, ImmutableList.of(UtredningStatus.FORFRAGAN_INKOMMEN,
                UtredningStatus.VANTAR_PA_SVAR, UtredningStatus.TILLDELA_UTREDNING));

        if (isNull(request.getVardenhet())) {
            throw new IbServiceException(IbErrorCodeEnum.BAD_REQUEST,
                    "At least one vardenhet must be selected to create an InternForfragan");
        }

        if (utredning.getExternForfragan().getInternForfraganList().stream()
                .map(InternForfragan::getVardenhetHsaId)
                .anyMatch(request.getVardenhet()::equals)) {
            throw new IbServiceException(IbErrorCodeEnum.BAD_STATE, MessageFormat.format(
                    "InternForfragan already exists for assessment with id {0} and vardenhet {1} ", utredningId, request.getVardenhet()));
        }

        LocalDateTime now = LocalDateTime.now();
        utredning.getExternForfragan().getInternForfraganList().add(
                anInternForfragan()
                        .withVardenhetHsaId(request.getVardenhet())
                        .withSkapadDatum(now)
                        .withKommentar(request.getKommentar())
                        .withBesvarasSenastDatum(getBesvarasSenastDatum(now))
                        .withDirekttilldelad(true)
                        .withForfraganSvar(aForfraganSvar()
                                .withSvarTyp(SvarTyp.ACCEPTERA)
                                .withUtforareAdress("")
                                .withUtforareEpost("")
                                .withUtforareNamn("")
                                .withUtforarePostnr("")
                                .withUtforarePostort("")
                                .withUtforareTyp(UtforareTyp.ENHET)
                                .build())
                        .build());

        utredningRepository.save(utredning);

        return utredningService.createGetUtredningResponse(utredning);
    }

    @Override
    @Transactional
    public GetUtredningResponse acceptInternForfragan(Long utredningId, String landstingHsaId, String vardenhetHsaId) {

        Utredning utredning = getUtredning(utredningId, landstingHsaId, ImmutableList.of(UtredningStatus.VANTAR_PA_SVAR,
                UtredningStatus.TILLDELA_UTREDNING));

        InternForfragan internForfragan = utredning.getExternForfragan().getInternForfraganList().stream()
                .filter(i -> i.getVardenhetHsaId().equals(vardenhetHsaId))
                .findAny()
                .orElseThrow(() -> new IbNotFoundException(MessageFormat.format(
                        "Could not find internforfragan for {0} in utredning {1}", vardenhetHsaId, utredningId)));

        InternForfraganStatus internForfraganStatus = internForfraganStateResolver.resolveStatus(utredning, internForfragan);
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
            throw new IbExternalServiceException(IbErrorCodeEnum.EXTERNAL_ERROR, IbExternalSystemEnum.HSA, re.getMessage());
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

        utredning.getHandelseList().add(HandelseUtil.createForfraganBesvarad(forfraganSvar.getSvarTyp(), userService.getUser().getNamn(),
                vardenhet.getNamn()));

        utredningRepository.save(utredning);

        return utredningService.createGetUtredningResponse(utredning);
    }

    @NotNull
    private Utredning getUtredning(Long utredningId, String landstingHsaId, List<UtredningStatus> allowedStatuses) {
        Utredning utredning = utredningRepository.findById(utredningId).orElseThrow(
                () -> new IbNotFoundException("Could not find the assessment with id " + utredningId));

        if (!Objects.equals(utredning.getExternForfragan().getLandstingHsaId(), landstingHsaId)) {
            throw new IbAuthorizationException(
                    "Utredning with assessmentId '" + utredningId + "' does not have ExternForfragan for landsting with id '"
                            + landstingHsaId + "'");
        }

        UtredningStatus utredningStatus = utredningStatusResolver.resolveStatus(utredning);
        if  (allowedStatuses.stream().noneMatch(utredningStatus::equals)) {
            throw new IbServiceException(IbErrorCodeEnum.BAD_STATE, MessageFormat.format(
                    "Assessment with id {0} is in an incorrect state", utredning.getUtredningId()));
        }



        return utredning;
    }

    @NotNull
    private LocalDateTime getBesvarasSenastDatum(LocalDateTime now) {
        return LocalDateTime.of(
                businessDays.addBusinessDays(now.toLocalDate(), besvaraForfraganArbetsdagar), now.toLocalTime());
    }

}
