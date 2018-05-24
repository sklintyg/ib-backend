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

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.common.util.SchemaDateUtil;
import se.inera.intyg.intygsbestallning.integration.myndighet.dto.ReportCareContactRequestDto;
import se.inera.intyg.intygsbestallning.integration.myndighet.dto.ReportDeviationRequestDto;
import se.inera.intyg.intygsbestallning.integration.myndighet.service.MyndighetIntegrationServiceImpl;
import se.inera.intyg.intygsbestallning.persistence.model.Besok;
import se.inera.intyg.intygsbestallning.persistence.model.Bestallning;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.type.*;
import se.inera.intyg.intygsbestallning.persistence.repository.UtredningRepository;
import se.inera.intyg.intygsbestallning.testutil.TestDataGen;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.RegisterBesokRequest;
import se.inera.intyg.intygsbestallning.web.responder.dto.ReportBesokAvvikelseRequest;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static com.google.common.collect.MoreCollectors.onlyElement;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static se.inera.intyg.intygsbestallning.integration.myndighet.dto.ReportCareContactRequestDto.ReportCareContactRequestDtoBuilder.aReportCareContactRequestDto;
import static se.inera.intyg.intygsbestallning.persistence.model.Avvikelse.AvvikelseBuilder.anAvvikelse;
import static se.inera.intyg.intygsbestallning.persistence.model.Besok.BesokBuilder.aBesok;
import static se.inera.intyg.intygsbestallning.persistence.model.Handling.HandlingBuilder.aHandling;
import static se.inera.intyg.intygsbestallning.persistence.model.Intyg.IntygBuilder.anIntyg;
import static se.inera.intyg.intygsbestallning.web.responder.dto.ReportBesokAvvikelseRequest.ReportBesokAvvikelseRequestBuilder.aReportBesokAvvikelseRequest;

@RunWith(MockitoJUnitRunner.class)
public class BesokServiceImplTest {

    private static final Long UTREDNING_ID = 1L;
    private static final Long BESOK_ID = 1L;
    private static final UtredningsTyp UTREDNING_TYP = UtredningsTyp.AFU_UTVIDGAD;
    private static final LocalDateTime DATE_TIME = LocalDateTime.of(2011, 11, 11, 11, 11, 11, 11);

    @Mock
    private UtredningRepository utredningRepository;

    @Mock
    private MyndighetIntegrationServiceImpl myndighetIntegrationService;

    @InjectMocks
    private BesokServiceImpl besokService;

    @Before
    public void setupMocks() {
        Utredning utredning = TestDataGen.createUtredning();
        utredning.setUtredningsTyp(UtredningsTyp.AFU);

        doReturn(Optional.of(utredning))
                .when(utredningRepository).findById(eq(UTREDNING_ID));

        doReturn(DATE_TIME)
                .when(myndighetIntegrationService)
                .updateAssessment(eq(UTREDNING_ID), eq(UTREDNING_TYP.name()));
    }

    @Test
    public void testRegisterNewBesokMedLakareSomProfession() {

        RegisterBesokRequest request = new RegisterBesokRequest();
        request.setUtredningId(UTREDNING_ID);
        request.setUtredandeVardPersonalNamn("utredandeVardPersonalNamn");
        request.setProffesion(DeltagarProfessionTyp.LK);
        request.setTolkStatus(TolkStatusTyp.BOKAT);
        request.setKallelseForm(KallelseFormTyp.TELEFONKONTAKT);
        request.setKallelseDatum(DATE_TIME);
        request.setBesokDatum(DATE_TIME.plusMonths(1).toLocalDate());
        request.setBesokStartTid(DATE_TIME.plusMonths(1).plusHours(4).toLocalTime());
        request.setBesokSlutTid(DATE_TIME.plusMonths(1).plusHours(6).toLocalTime());

        Utredning utredning = TestDataGen.createUtredning();
        utredning.setUtredningsTyp(UtredningsTyp.AFU);
        final Besok besok = TestDataGen.createBesok(request).stream().collect(onlyElement());

        final ReportCareContactRequestDto dto = aReportCareContactRequestDto()
                .withAssessmentId(utredning.getUtredningId())
                .withAssessmentCareContactId(utredning.getBestallning()
                        .map(Bestallning::getId)
                        .map(Object::toString)
                        .orElse(null))
                .withParticipatingProfession(besok.getDeltagareProfession().name())
                .withInterpreterStatus(besok.getTolkStatus().getLabel())
                .withInvitationDate(SchemaDateUtil.toStringFromLocalDateTime(besok.getKallelseDatum()))
                .withInvitationChannel(besok.getKallelseForm().getCvValue())
                .withStartTime(besok.getBesokStartTid())
                .withEndTime(besok.getBesokSlutTid())
                .withVisitStatus(besok.getBesokStatus().getCvValue())
                .build();

        besokService.registerNewBesok(request);

        verify(myndighetIntegrationService, times(0)).updateAssessment(eq(UTREDNING_ID), eq(UTREDNING_TYP.name()));
        verify(myndighetIntegrationService, times(1)).reportCareContactInteraction(eq(dto));
    }

    @Test
    public void testRegisterNewBesokMedAnnanProfession() {

        RegisterBesokRequest request = new RegisterBesokRequest();
        request.setUtredningId(UTREDNING_ID);
        request.setUtredandeVardPersonalNamn("utredandeVardPersonalNamn");
        request.setProffesion(DeltagarProfessionTyp.PS);
        request.setTolkStatus(TolkStatusTyp.BOKAT);
        request.setKallelseForm(KallelseFormTyp.TELEFONKONTAKT);
        request.setKallelseDatum(DATE_TIME);
        request.setBesokDatum(DATE_TIME.plusMonths(1).toLocalDate());
        request.setBesokStartTid(DATE_TIME.plusMonths(1).plusHours(4).toLocalTime());
        request.setBesokSlutTid(DATE_TIME.plusMonths(1).plusHours(6).toLocalTime());

        Utredning utredning = TestDataGen.createUtredning();
        utredning.setUtredningsTyp(UtredningsTyp.AFU);
        final Besok besok = TestDataGen.createBesok(request).stream().collect(onlyElement());

        final ReportCareContactRequestDto dto = aReportCareContactRequestDto()
                .withAssessmentId(utredning.getUtredningId())
                .withAssessmentCareContactId(utredning.getBestallning()
                        .map(Bestallning::getId)
                        .map(Object::toString)
                        .orElse(null))
                .withParticipatingProfession(besok.getDeltagareProfession().name())
                .withInterpreterStatus(besok.getTolkStatus().getLabel())
                .withInvitationDate(SchemaDateUtil.toStringFromLocalDateTime(besok.getKallelseDatum()))
                .withInvitationChannel(besok.getKallelseForm().getCvValue())
                .withStartTime(besok.getBesokStartTid())
                .withEndTime(besok.getBesokSlutTid())
                .withVisitStatus(besok.getBesokStatus().getCvValue())
                .build();

        besokService.registerNewBesok(request);

        verify(myndighetIntegrationService, times(1)).updateAssessment(eq(UTREDNING_ID), eq(UTREDNING_TYP.name()));
        verify(myndighetIntegrationService, times(1)).reportCareContactInteraction(eq(dto));
    }

    @Test
    public void testRegisterIllegalState() {
        RegisterBesokRequest request = new RegisterBesokRequest();
        request.setUtredningId(UTREDNING_ID);
        request.setUtredandeVardPersonalNamn("utredandeVardPersonalNamn");
        request.setProffesion(DeltagarProfessionTyp.PS);
        request.setTolkStatus(TolkStatusTyp.BOKAT);
        request.setKallelseForm(KallelseFormTyp.TELEFONKONTAKT);
        request.setKallelseDatum(DATE_TIME);
        request.setBesokDatum(DATE_TIME.plusMonths(1).toLocalDate());
        request.setBesokStartTid(DATE_TIME.plusMonths(1).plusHours(4).toLocalTime());
        request.setBesokSlutTid(DATE_TIME.plusMonths(1).plusHours(6).toLocalTime());

        Utredning utredning = TestDataGen.createUtredning();
        utredning.setUtredningsTyp(UtredningsTyp.AFU);
        utredning.setBestallning(null); //this sets the Utredning-entity in an incorrect state

        doReturn(Optional.of(utredning))
                .when(utredningRepository).findById(eq(UTREDNING_ID));

        assertThatThrownBy(() -> besokService.registerNewBesok(request))
                .isExactlyInstanceOf(IbServiceException.class)
                .hasMessage("Utredning with id " + UTREDNING_ID + " is in an incorrect state");
    }

    @Test
    public void testReportBesokAvvikelseMottagen() {

        Utredning utredning = createUtredningForBesokTest();


        final ReportBesokAvvikelseRequest request = aReportBesokAvvikelseRequest()
                .withBesokId(1L)
                .withOrsakatAv(AvvikelseOrsak.PATIENT)
                .withBeskrivning("beskrivning")
                .withTidpunkt(DATE_TIME)
                .withInvanareUteblev(true)
                .withSamordnare("Sam Ordnare")
                .withHandelseTyp(HandelseTyp.AVVIKELSE_MOTTAGEN)
                .build();

        doReturn(Optional.of(utredning))
                .when(utredningRepository)
                .findByBesokList_Id(eq(BESOK_ID));

        besokService.reportBesokAvvikelse(request);

        verifyZeroInteractions(myndighetIntegrationService);
    }

    @Test
    public void testReportBesokAvvikelseRapporterad() {

        Utredning utredning = createUtredningForBesokTest();
        Utredning utredningAfterSavedAvvikelse = createUtredningForBesokTest();
        utredningAfterSavedAvvikelse.getBesokList().get(0).setAvvikelse(anAvvikelse()
                .withAvvikelseId(1L)
                .withOrsakatAv(AvvikelseOrsak.PATIENT)
                .withBeskrivning("beskrivning")
                .withTidpunkt(DATE_TIME)
                .withInvanareUteblev(true)
                .build());
        final ReportBesokAvvikelseRequest request = aReportBesokAvvikelseRequest()
                .withBesokId(1L)
                .withOrsakatAv(AvvikelseOrsak.PATIENT)
                .withBeskrivning("beskrivning")
                .withTidpunkt(DATE_TIME)
                .withInvanareUteblev(true)
                .withSamordnare("Sam Ordnare")
                .withHandelseTyp(HandelseTyp.AVVIKELSE_RAPPORTERAD)
                .build();

        doReturn(Optional.of(utredning))
                .when(utredningRepository)
                .findByBesokList_Id(eq(BESOK_ID));

        doReturn(utredningAfterSavedAvvikelse)
                .when(utredningRepository)
                .save(any());

        besokService.reportBesokAvvikelse(request);

        verify(myndighetIntegrationService, times(1))
                .reportDeviation(any(ReportDeviationRequestDto.class));
    }

    @Test
    public void testReportBesokAvvikelseRapporteradIncorrectState() {

        Utredning utredning = createUtredningForBesokTest();
        //This puts utredning in an incorrect state to be able to report besok avvikelse
        utredning.setHandlingList(Collections.emptyList());

        Utredning utredningAfterSavedAvvikelse = createUtredningForBesokTest();
        final ReportBesokAvvikelseRequest request = aReportBesokAvvikelseRequest()
                .withBesokId(1L)
                .withOrsakatAv(AvvikelseOrsak.PATIENT)
                .withBeskrivning("beskrivning")
                .withTidpunkt(DATE_TIME)
                .withInvanareUteblev(true)
                .withSamordnare("Sam Ordnare")
                .withHandelseTyp(HandelseTyp.AVVIKELSE_RAPPORTERAD)
                .build();

        doReturn(Optional.of(utredning))
                .when(utredningRepository)
                .findByBesokList_Id(eq(BESOK_ID));

        assertThatThrownBy(() -> besokService.reportBesokAvvikelse(request))
                .isExactlyInstanceOf(IbServiceException.class)
                .hasMessage(MessageFormat.format("Utredning with id {0} is in an incorrect state.", utredning.getUtredningId()));
    }

    private Utredning createUtredningForBesokTest() {
        Utredning utredning = TestDataGen.createUtredning();
        utredning.setUtredningsTyp(UtredningsTyp.AFU);
        utredning.setHandlingList(ImmutableList.of(aHandling()
                .withId(1L)
                .build()));
        utredning.setIntygList(ImmutableList.of(anIntyg()
                .withId(1L)
                .withSistaDatum(DATE_TIME)
                .withSistaDatumKompletteringsbegaran(DATE_TIME.plusMonths(6))
                .build()));
        utredning.setBesokList(ImmutableList.of(aBesok()
                .withId(1L)
                .withKallelseDatum(DATE_TIME)
                .withKallelseForm(KallelseFormTyp.BREVKONTAKT)
                .withBesokStartTid(LocalDateTime.of(DATE_TIME.toLocalDate(), DATE_TIME.toLocalTime().plusHours(1)))
                .withBesokSlutTid(LocalDateTime.of(DATE_TIME.toLocalDate(), DATE_TIME.toLocalTime().plusHours(2)))
                .withDeltagareProfession(DeltagarProfessionTyp.LK)
                .withTolkStatus(TolkStatusTyp.BOKAT)
                .withDeltagareFullstandigtNamn("Delta Gare")
                .withBesokStatus(BesokStatusTyp.TIDBOKAD_VARDKONTAKT)
                .build()));
        return utredning;
    }
}

