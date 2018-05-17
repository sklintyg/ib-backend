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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.intyg.intygsbestallning.common.exception.IbErrorCodeEnum;
import se.inera.intyg.intygsbestallning.common.exception.IbNotFoundException;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.integration.myndighet.dto.ReportCareContactRequestDto;
import se.inera.intyg.intygsbestallning.integration.myndighet.service.MyndighetIntegrationService;
import se.inera.intyg.intygsbestallning.persistence.model.Besok;
import se.inera.intyg.intygsbestallning.persistence.model.Bestallning;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.type.BesokStatusTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.DeltagarProfessionTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.UtredningsTyp;
import se.inera.intyg.intygsbestallning.service.stateresolver.UtredningStatus;
import se.inera.intyg.intygsbestallning.service.stateresolver.UtredningStatusResolver;
import se.inera.intyg.intygsbestallning.service.utredning.BaseUtredningService;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.RegisterBesokRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.RegisterBesokResponse;

import java.lang.invoke.MethodHandles;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static se.inera.intyg.intygsbestallning.integration.myndighet.dto.ReportCareContactRequestDto.ReportCareContactRequestDtoBuilder.aReportCareContactRequestDto;
import static se.inera.intyg.intygsbestallning.persistence.model.Besok.BesokBuilder.aBesok;
import static se.inera.intyg.intygsbestallning.service.stateresolver.UtredningStatus.BESTALLNING_MOTTAGEN_VANTAR_PA_HANDLINGAR;
import static se.inera.intyg.intygsbestallning.service.stateresolver.UtredningStatus.HANDLINGAR_MOTTAGNA_BOKA_BESOK;
import static se.inera.intyg.intygsbestallning.service.stateresolver.UtredningStatus.UPPDATERAD_BESTALLNING_VANTAR_PA_HANDLINGAR;
import static se.inera.intyg.intygsbestallning.service.stateresolver.UtredningStatus.UTREDNING_PAGAR;
import static se.inera.intyg.intygsbestallning.web.controller.api.dto.RegisterBesokRequest.validate;

@Service
public class BesokServiceImpl extends BaseUtredningService implements BesokService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final List<UtredningStatus> REGISTRERA_BESOK_GODKANDA_STATES = Arrays.asList(
            BESTALLNING_MOTTAGEN_VANTAR_PA_HANDLINGAR,
            UPPDATERAD_BESTALLNING_VANTAR_PA_HANDLINGAR,
            HANDLINGAR_MOTTAGNA_BOKA_BESOK,
            UTREDNING_PAGAR);

    @Autowired
    private MyndighetIntegrationService myndighetIntegrationService;

    @Override
    public RegisterBesokResponse registerNewBesok(final RegisterBesokRequest request) {
        validate(request);

        LOG.debug(MessageFormat.format("Received a request to register new besok for utredning with id {0}", request.getUtredningId()));

        final Utredning utredning = utredningRepository.findById(request.getUtredningId())
                .orElseThrow(() -> new IbNotFoundException("Utredning with id '" + request.getUtredningId() + "' does not exist."));

        if (!REGISTRERA_BESOK_GODKANDA_STATES.contains(UtredningStatusResolver.resolveStaticStatus(utredning))) {
            throw new IbServiceException(IbErrorCodeEnum.BAD_STATE,
                    MessageFormat.format("Assessment with id {0} is in an incorrect state", utredning.getUtredningId()));
        }

        final Besok besok = createBesok(request);

        utredning.getBesokList().add(besok);

        if (isOtherProfessionThanLakare(utredning, besok)) {
            utredning.setUtredningsTyp(UtredningsTyp.AFU_UTVIDGAD);
            utredningRepository.save(utredning);
            reportBesok(utredning, besok);
            final LocalDateTime nyttSistaDatum = updateUtredningWithUtredningsTypAfuUtvidgad(utredning);
            return RegisterBesokResponse.withUpdatedUtredningsTyp(nyttSistaDatum.toString());
        } else {
            utredningRepository.save(utredning);
            reportBesok(utredning, besok);
            return RegisterBesokResponse.withNotUpdatedUtredgningsTyp();
        }
    }

    private LocalDateTime updateUtredningWithUtredningsTypAfuUtvidgad(Utredning utredning) {

        final LocalDateTime nyttSistaDatum = myndighetIntegrationService
                .updateAssessment(utredning.getUtredningId(), UtredningsTyp.AFU_UTVIDGAD.name()).atStartOfDay();

        if (utredning.getIntygList().size() == 1 && !utredning.getIntygList().get(0).isKomplettering()) {
            utredning.getIntygList().get(0).setSistaDatum(nyttSistaDatum);
            utredningRepository.save(utredning);
            return nyttSistaDatum;
        }

        throw new IbServiceException(IbErrorCodeEnum.BAD_STATE, MessageFormat.format(
                "assessment with id {0} is in an incorrect state", utredning.getUtredningId()));
    }

    private void reportBesok(final Utredning utredning, final Besok besok) {
        myndighetIntegrationService.reportCareContactInteraction(createReportCareContactRequestDto(utredning, besok));
    }

    private Besok createBesok(final RegisterBesokRequest request) {
        return aBesok()
                .withKallelseDatum(request.getKallelseDatum())
                .withKallelseForm(request.getKallelseForm())
                .withBesokStartTid(LocalDateTime.of(request.getBesokDatum(), request.getBesokStartTid()))
                .withBesokSlutTid(LocalDateTime.of(request.getBesokDatum(), request.getBesokSlutTid()))
                .withDeltagareProfession(request.getProffesion())
                .withTolkStatus(request.getTolkStatus())
                .withDeltagareFullstandigtNamn(request.getUtredandeVardPersonalNamn().orElse(null))
                .withBesokStatus(BesokStatusTyp.TIDBOKAD_VARDKONTAKT)
                .build();
    }

    private ReportCareContactRequestDto createReportCareContactRequestDto(final Utredning utredning, final Besok besok) {
        return aReportCareContactRequestDto()
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
                .build();
    }

    private boolean isOtherProfessionThanLakare(final Utredning utredning, final Besok besok) {
        return besok.getDeltagareProfession() != DeltagarProfessionTyp.LK && utredning.getUtredningsTyp() == UtredningsTyp.AFU;
    }
}
