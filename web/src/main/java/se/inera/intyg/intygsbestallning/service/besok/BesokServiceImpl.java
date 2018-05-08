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
package se.inera.intyg.intygsbestallning.service.besok;

import static se.inera.intyg.intygsbestallning.common.dto.ReportCareContactRequestDto.ReportCareContactRequestDtoBuilder.aReportCareContactRequestDto;
import static se.inera.intyg.intygsbestallning.common.exception.IbErrorCodeEnum.BAD_STATE;
import static se.inera.intyg.intygsbestallning.persistence.model.Besok.BesokBuilder.aBesok;
import static se.inera.intyg.intygsbestallning.web.controller.api.dto.RegisterBesokRequest.validate;

import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.inera.intyg.intygsbestallning.common.exception.IbNotFoundException;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.integration.myndighet.service.MyndighetIntegrationService;
import se.inera.intyg.intygsbestallning.persistence.model.Besok;
import se.inera.intyg.intygsbestallning.persistence.model.Bestallning;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.type.DeltagarProfessionTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.UtredningsTyp;
import se.inera.intyg.intygsbestallning.service.stateresolver.UtredningStateResolver;
import se.inera.intyg.intygsbestallning.service.stateresolver.UtredningStatus;
import se.inera.intyg.intygsbestallning.service.utredning.BaseUtredningService;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.RegisterBesokRequest;

import java.lang.invoke.MethodHandles;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BesokServiceImpl extends BaseUtredningService implements BesokService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final List<UtredningStatus> GODKANDA_STATE = ImmutableList.of(
            UtredningStatus.TILLDELAD_VANTAR_PA_BESTALLNING,
            UtredningStatus.UTREDNING_PAGAR);

    private final MyndighetIntegrationService myndighetIntegrationService;

    public BesokServiceImpl(final MyndighetIntegrationService myndighetIntegrationService) {
        this.myndighetIntegrationService = myndighetIntegrationService;
    }

    @Override
    @Transactional
    public void registerNewBesok(final RegisterBesokRequest request) {
        validate(request);

        LOG.debug(MessageFormat.format("Received a request to register new besok for utredning with id {0}", request.getUtredningId()));

        final Utredning utredning = utredningRepository.findById(request.getUtredningId())
                .orElseThrow(() -> new IbNotFoundException("Utredning with id '" + request.getUtredningId() + "' does not exist."));

        if (GODKANDA_STATE.contains(UtredningStateResolver.resolveStaticStatus(utredning))) {
            throw new IbServiceException(
                    BAD_STATE, MessageFormat.format("Assessment with id '{0}' is in an incorrect state", utredning.getUtredningId()));
        }

        final Besok besok = aBesok()
                .withKallelseDatum(request.getKallelseDatum())
                .withKallelseForm(request.getKallelseForm())
                .withBesokStartTid(LocalDateTime.of(request.getBesokDatum(), request.getBesokStartTid()))
                .withBesokSlutTid(LocalDateTime.of(request.getBesokDatum(), request.getBesokSlutTid()))
                .withDeltagareProfession(request.getProffesion())
                .withTolkStatus(request.getTolkStatus())
                .withDeltagareFullstandigtNamn(request.getUtredandeVardPersonalNamn().orElse(null))
                .build();

        utredning.getBesokList().add(besok);

        if (besok.getDeltagareProfession() != DeltagarProfessionTyp.LK && utredning.getUtredningsTyp() == UtredningsTyp.AFU) {
            uppdateraUtredningUtokadAFU(utredning.getUtredningId());
        }

        myndighetIntegrationService.reportCareContactInteraction(aReportCareContactRequestDto()
                .withAssessmentId(utredning.getUtredningId())
                .withAssessmentCareContactId(utredning.getBestallning()
                        .map(Bestallning::getId)
                        .map(Object::toString)
                        .orElse(null))
                .withParticipatingProfession(besok.getDeltagareProfession().name())
                .withInterpreterStatus(besok.getTolkStatus().getLabel())
                .withInvitationDate(besok.getKallelseDatum().toString())
                .withInvitationChannel(besok.getKallelseForm().getCvValue())
                .withStartTime(besok.getBesokStartTid())
                .withEndTime(besok.getBesokSlutTid())
                .withVisitStatus(besok.getBesokStatus().getCvValue())
                .build());

        utredningRepository.save(utredning);
    }

    private void uppdateraUtredningUtokadAFU(final String utredningId) {
        myndighetIntegrationService.updateAssessment(utredningId, UtredningsTyp.AFU_UTVIDGAD.name());
    }


}
