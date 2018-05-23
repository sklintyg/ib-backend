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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.inera.intyg.intygsbestallning.common.exception.IbErrorCodeEnum;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.persistence.model.InternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.type.SvarTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.UtforareTyp;
import se.inera.intyg.intygsbestallning.service.handelse.HandelseUtil;
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

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;
import static se.inera.intyg.intygsbestallning.persistence.model.ForfraganSvar.ForfraganSvarBuilder.aForfraganSvar;
import static se.inera.intyg.intygsbestallning.persistence.model.InternForfragan.InternForfraganBuilder.anInternForfragan;

@Service
@Transactional
public class InternForfraganServiceImpl extends BaseUtredningService implements InternForfraganService {

    @Autowired
    private BusinessDaysBean businessDays;

    @Autowired
    private UtredningService utredningService;

    @Value("${ib.besvara.forfragan.arbetsdagar:2}")
    private int besvaraForfraganArbetsdagar;

    @Override
    @Transactional
    public GetUtredningResponse createInternForfragan(Long utredningId, String landstingHsaId, CreateInternForfraganRequest request) {

        Utredning utredning = getUtredningForLandsting(utredningId, landstingHsaId, ImmutableList.of(UtredningStatus.FORFRAGAN_INKOMMEN,
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

        Utredning utredning = getUtredningForLandsting(utredningId, landstingHsaId, ImmutableList.of(UtredningStatus.FORFRAGAN_INKOMMEN,
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

    @NotNull
    private LocalDateTime getBesvarasSenastDatum(LocalDateTime now) {
        return LocalDateTime.of(
                businessDays.addBusinessDays(now.toLocalDate(), besvaraForfraganArbetsdagar), now.toLocalTime());
    }

}
