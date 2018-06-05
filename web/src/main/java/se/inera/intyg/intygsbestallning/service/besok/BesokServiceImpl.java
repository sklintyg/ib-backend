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

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.MoreCollectors.onlyElement;
import static java.util.Objects.nonNull;
import static se.inera.intyg.intygsbestallning.integration.myndighet.dto.ReportCareContactRequestDto.ReportCareContactRequestDtoBuilder.aReportCareContactRequestDto;
import static se.inera.intyg.intygsbestallning.integration.myndighet.dto.ReportDeviationRequestDto.ReportDeviationRequestDtoBuilder.aReportDeviationRequestDto;
import static se.inera.intyg.intygsbestallning.persistence.model.Avvikelse.AvvikelseBuilder.anAvvikelse;
import static se.inera.intyg.intygsbestallning.persistence.model.Besok.BesokBuilder.aBesok;
import static se.inera.intyg.intygsbestallning.service.stateresolver.UtredningStatus.AVVIKELSE_MOTTAGEN;
import static se.inera.intyg.intygsbestallning.service.stateresolver.UtredningStatus.BESTALLNING_MOTTAGEN_VANTAR_PA_HANDLINGAR;
import static se.inera.intyg.intygsbestallning.service.stateresolver.UtredningStatus.HANDLINGAR_MOTTAGNA_BOKA_BESOK;
import static se.inera.intyg.intygsbestallning.service.stateresolver.UtredningStatus.UPPDATERAD_BESTALLNING_VANTAR_PA_HANDLINGAR;
import static se.inera.intyg.intygsbestallning.service.stateresolver.UtredningStatus.UTREDNING_PAGAR;
import static se.inera.intyg.intygsbestallning.web.controller.api.dto.RegisterBesokRequest.validate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.lang.invoke.MethodHandles;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import se.inera.intyg.intygsbestallning.common.exception.IbErrorCodeEnum;
import se.inera.intyg.intygsbestallning.common.exception.IbNotFoundException;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.common.util.SchemaDateUtil;
import se.inera.intyg.intygsbestallning.integration.myndighet.dto.ReportCareContactRequestDto;
import se.inera.intyg.intygsbestallning.integration.myndighet.dto.ReportDeviationRequestDto;
import se.inera.intyg.intygsbestallning.integration.myndighet.service.MyndighetIntegrationService;
import se.inera.intyg.intygsbestallning.persistence.model.Avvikelse;
import se.inera.intyg.intygsbestallning.persistence.model.Besok;
import se.inera.intyg.intygsbestallning.persistence.model.Handelse;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.type.BesokStatusTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.DeltagarProfessionTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.HandelseTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.UtredningsTyp;
import se.inera.intyg.intygsbestallning.service.handelse.HandelseUtil;
import se.inera.intyg.intygsbestallning.service.notifiering.send.NotifieringSendService;
import se.inera.intyg.intygsbestallning.service.pdl.LogService;
import se.inera.intyg.intygsbestallning.service.pdl.dto.PatientPdlLoggable;
import se.inera.intyg.intygsbestallning.service.pdl.dto.PdlLogType;
import se.inera.intyg.intygsbestallning.service.stateresolver.BesokStatus;
import se.inera.intyg.intygsbestallning.service.stateresolver.BesokStatusResolver;
import se.inera.intyg.intygsbestallning.service.stateresolver.UtredningStatus;
import se.inera.intyg.intygsbestallning.service.stateresolver.UtredningStatusResolver;
import se.inera.intyg.intygsbestallning.service.utredning.BaseUtredningService;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.RegisterBesokRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.RegisterBesokResponse;
import se.inera.intyg.intygsbestallning.web.responder.dto.ReportBesokAvvikelseRequest;

@Service
public class BesokServiceImpl extends BaseUtredningService implements BesokService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private LogService logService;

    private static final List<UtredningStatus> BESOK_HANTERING_GODKANDA_STATUSAR = Arrays.asList(
            BESTALLNING_MOTTAGEN_VANTAR_PA_HANDLINGAR,
            UPPDATERAD_BESTALLNING_VANTAR_PA_HANDLINGAR,
            HANDLINGAR_MOTTAGNA_BOKA_BESOK,
            UTREDNING_PAGAR,
            AVVIKELSE_MOTTAGEN);

    private static Predicate<Utredning> isKorrektStatusForBesokAvvikelseMottagen() {
        return utr -> BESOK_HANTERING_GODKANDA_STATUSAR.contains(UtredningStatusResolver.resolveStaticStatus(utr));
    }

    @Autowired
    private MyndighetIntegrationService myndighetIntegrationService;

    @Autowired
    private NotifieringSendService notifieringSendService;

    @Override
    @Transactional
    public RegisterBesokResponse registerBesok(final RegisterBesokRequest request) {
        validate(request);

        LOG.debug(MessageFormat.format("Received a request to register new besok for utredning with id {0}", request.getUtredningId()));

        final Utredning utredning = utredningRepository.findById(request.getUtredningId())
                .orElseThrow(() -> new IbNotFoundException("Utredning with id '" + request.getUtredningId() + "' does not exist."));

        if (!BESOK_HANTERING_GODKANDA_STATUSAR.contains(UtredningStatusResolver.resolveStaticStatus(utredning))) {
            throw new IbServiceException(IbErrorCodeEnum.BAD_STATE,
                    MessageFormat.format("Utredning with id {0} is in an incorrect state", utredning.getUtredningId()));
        }

        Besok besok;
        Handelse besokHandelse;
        if (request.getBesokId() != null) {
            besok = utredning.getBesokList().stream()
                    .filter(b -> b.getId().equals(request.getBesokId()))
                    .findAny()
                    .orElseThrow(() -> new IbNotFoundException(MessageFormat.format(
                            "Could not find besok \'{0}\' in utredning \'{0}\'", request.getBesokId(), request.getUtredningId())));

            if (isBesokOmbokat(besok, request)) {
                besokHandelse = HandelseUtil.createOmbokatBesok(besok, LocalDateTime.of(request.getBesokDatum(),
                        request.getBesokStartTid()), LocalDateTime.of(request.getBesokDatum(), request.getBesokSlutTid()),
                        request.getProfession(), request.getUtredandeVardPersonalNamn().orElse(""), userService.getUser().getNamn());
            } else {
                besokHandelse = HandelseUtil.createUppdateraBesok(besok, request.getProfession(), request.getUtredandeVardPersonalNamn()
                        .orElse(""), userService.getUser().getNamn());
            }

            updateBesok(besok, request);

            logService.log(new PatientPdlLoggable(utredning.getInvanare().getPersonId()), PdlLogType.BESOK_ANDRAT);
        }  else {
            besok = createBesok(request);
            utredning.getBesokList().add(besok);
            besokHandelse = HandelseUtil.createNyttBesok(besok, userService.getUser().getNamn());

            // Save besok here to get its id for reportBesok
            utredningRepository.persist(utredning);

            logService.log(new PatientPdlLoggable(utredning.getInvanare().getPersonId()), PdlLogType.BESOK_SKAPAT);
        }

        utredning.getHandelseList().add(besokHandelse);
        besok.getHandelseList().add(besokHandelse);

        if (isOtherProfessionThanLakare(utredning, besok)) {
            utredning.setUtredningsTyp(UtredningsTyp.AFU_UTVIDGAD);
            reportBesok(utredning, besok);
            final LocalDateTime nyttSistaDatum = updateUtredningWithUtredningsTypAfuUtvidgad(utredning);
            utredningRepository.save(utredning);
            return RegisterBesokResponse.withUpdatedUtredningsTyp(nyttSistaDatum.toString());
        } else {
            utredningRepository.save(utredning);
            reportBesok(utredning, besok);
            return RegisterBesokResponse.withNotUpdatedUtredgningsTyp();
        }
    }

    @Override
    @Transactional
    public void reportBesokAvvikelse(final ReportBesokAvvikelseRequest request) {

        Optional<Utredning> optionalUtredning = utredningRepository.findByBesokList_Id(request.getBesokId());
        optionalUtredning.orElseThrow(() -> new IbNotFoundException(
                MessageFormat.format("Besok with id {0} was not found.", request.getBesokId())));

        optionalUtredning.filter(isKorrektStatusForBesokAvvikelseMottagen())
                .orElseThrow(() -> new IbServiceException(
                        IbErrorCodeEnum.BAD_STATE,
                        MessageFormat.format("Utredning with id {0} is in an incorrect state.", optionalUtredning.get().getUtredningId())));

        Besok besokToUpdate = optionalUtredning.get().getBesokList().stream()
                .filter(b -> b.getId().equals(request.getBesokId()))
                .collect(onlyElement());
        besokToUpdate.setAvvikelse(createAvvikelse(request));
        Handelse besokHandelse = HandelseUtil.createBesokAvvikelse(request);
        optionalUtredning.get().getHandelseList().add(besokHandelse);
        besokToUpdate.getHandelseList().add(besokHandelse);

        final Utredning uppdateradUtredning = utredningRepository.save(optionalUtredning.get());
        final Besok uppdateratBesok = uppdateradUtredning.getBesokList().stream()
                .filter(b -> b.getId().equals(request.getBesokId()))
                .collect(onlyElement());

        if (request.getHandelseTyp().equals(HandelseTyp.AVVIKELSE_RAPPORTERAD)) {
            request.setAvvikelseId(uppdateratBesok.getAvvikelse().getAvvikelseId());
            checkState(Objects.equals(BesokStatus.AVVIKELSE_RAPPORTERAD, BesokStatusResolver.resolveStaticStatus(uppdateratBesok)));
            myndighetIntegrationService.reportDeviation(createReportDeviationRequestDto(request));
            notifieringSendService.notifieraLandstingAvvikelseRapporteradAvVarden(uppdateradUtredning, besokToUpdate);
        } else {
            checkState(Objects.equals(BesokStatus.AVVIKELSE_MOTTAGEN, BesokStatusResolver.resolveStaticStatus(uppdateratBesok)));
            notifieringSendService.notifieraLandstingAvvikelseMottagenFranFK(uppdateradUtredning, besokToUpdate);
            notifieringSendService.notifieraVardenhetAvvikelseMottagenFranFK(uppdateradUtredning, besokToUpdate);
        }
    }

    private Avvikelse createAvvikelse(final ReportBesokAvvikelseRequest request) {
        return anAvvikelse()
                .withOrsakatAv(request.getOrsakatAv())
                .withBeskrivning(request.getBeskrivning().orElse(null))
                .withTidpunkt(request.getTidpunkt())
                .withInvanareUteblev(request.getInvanareUteblev())
                .build();
    }

    private LocalDateTime updateUtredningWithUtredningsTypAfuUtvidgad(Utredning utredning) {

        final LocalDateTime nyttSistaDatum = myndighetIntegrationService
                .updateAssessment(utredning.getUtredningId(), UtredningsTyp.AFU_UTVIDGAD.name());

        if (utredning.getIntygList().size() == 1 && !utredning.getIntygList().get(0).isKomplettering()) {
            utredning.getIntygList().get(0).setSistaDatum(nyttSistaDatum);
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
                .withDeltagareProfession(request.getProfession())
                .withTolkStatus(request.getTolkStatus())
                .withDeltagareFullstandigtNamn(request.getUtredandeVardPersonalNamn().orElse(null))
                .withBesokStatus(BesokStatusTyp.TIDBOKAD_VARDKONTAKT)
                .build();
    }

    private void updateBesok(final Besok besok, final RegisterBesokRequest request) {
        besok.setKallelseDatum(request.getKallelseDatum());
        besok.setKallelseForm(request.getKallelseForm());
        besok.setBesokStartTid(LocalDateTime.of(request.getBesokDatum(), request.getBesokStartTid()));
        besok.setBesokSlutTid(LocalDateTime.of(request.getBesokDatum(), request.getBesokSlutTid()));
        besok.setDeltagareProfession(request.getProfession());
        besok.setTolkStatus(request.getTolkStatus());
        besok.setDeltagareFullstandigtNamn(request.getUtredandeVardPersonalNamn().orElse(null));
    }

    private boolean isBesokOmbokat(Besok besok, RegisterBesokRequest request) {
        return !besok.getBesokStartTid().toLocalDate().equals(request.getBesokDatum())
                || !besok.getBesokStartTid().toLocalTime().equals(request.getBesokStartTid())
                || !besok.getBesokSlutTid().toLocalTime().equals(request.getBesokSlutTid());
    }

    private ReportCareContactRequestDto createReportCareContactRequestDto(final Utredning utredning, final Besok besok) {
        return aReportCareContactRequestDto()
                .withAssessmentId(utredning.getUtredningId())
                .withAssessmentCareContactId(besok.getId().toString())
                .withParticipatingProfession(besok.getDeltagareProfession().name())
                .withInterpreterStatus(nonNull(besok.getTolkStatus()) ? besok.getTolkStatus().getLabel() : null)
                .withInvitationDate(SchemaDateUtil.toStringFromLocalDateTime(besok.getKallelseDatum()))
                .withInvitationChannel(besok.getKallelseForm().getCvValue())
                .withStartTime(besok.getBesokStartTid())
                .withEndTime(besok.getBesokSlutTid())
                .withVisitStatus(besok.getBesokStatus().getCvValue())
                .build();
    }

    private ReportDeviationRequestDto createReportDeviationRequestDto(final ReportBesokAvvikelseRequest avvikelseRequest) {
        return aReportDeviationRequestDto()
                .withBesokId(avvikelseRequest.getBesokId().toString())
                .withAvvikelseId(avvikelseRequest.getAvvikelseId().toString())
                .withOrsakatAv(avvikelseRequest.getOrsakatAv().name())
                .withBeskrivning(avvikelseRequest.getBeskrivning().orElse(null))
                .withTidpunkt(SchemaDateUtil.toStringFromLocalDateTime(avvikelseRequest.getTidpunkt()))
                .withInvanareUteblev(avvikelseRequest.getInvanareUteblev())
                .withSamordnare(avvikelseRequest.getSamordnare())
                .build();
    }

    private boolean isOtherProfessionThanLakare(final Utredning utredning, final Besok besok) {
        return besok.getDeltagareProfession() != DeltagarProfessionTyp.LK && utredning.getUtredningsTyp() == UtredningsTyp.AFU;
    }
}
