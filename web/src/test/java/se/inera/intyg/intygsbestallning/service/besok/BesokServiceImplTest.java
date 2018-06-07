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

import static com.google.common.collect.MoreCollectors.onlyElement;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static se.inera.intyg.intygsbestallning.integration.myndighet.dto.ReportCareContactRequestDto.ReportCareContactRequestDtoBuilder.aReportCareContactRequestDto;
import static se.inera.intyg.intygsbestallning.persistence.model.Avvikelse.AvvikelseBuilder.anAvvikelse;
import static se.inera.intyg.intygsbestallning.persistence.model.Besok.BesokBuilder.aBesok;
import static se.inera.intyg.intygsbestallning.persistence.model.Handelse.HandelseBuilder.aHandelse;
import static se.inera.intyg.intygsbestallning.persistence.model.Handling.HandlingBuilder.aHandling;
import static se.inera.intyg.intygsbestallning.persistence.model.Intyg.IntygBuilder.anIntyg;
import static se.inera.intyg.intygsbestallning.web.responder.dto.ReportBesokAvvikelseRequest.ReportBesokAvvikelseRequestBuilder.aReportBesokAvvikelseRequest;

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Optional;

import se.inera.intyg.intygsbestallning.common.exception.IbAuthorizationException;
import se.inera.intyg.intygsbestallning.common.exception.IbNotFoundException;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.common.util.SchemaDateUtil;
import se.inera.intyg.intygsbestallning.integration.myndighet.dto.ReportCareContactRequestDto;
import se.inera.intyg.intygsbestallning.integration.myndighet.dto.ReportDeviationRequestDto;
import se.inera.intyg.intygsbestallning.integration.myndighet.service.MyndighetIntegrationServiceImpl;
import se.inera.intyg.intygsbestallning.persistence.model.Besok;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.type.AvvikelseOrsak;
import se.inera.intyg.intygsbestallning.persistence.model.type.BesokStatusTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.DeltagarProfessionTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.HandelseTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.KallelseFormTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.TolkStatusTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.UtredningsTyp;
import se.inera.intyg.intygsbestallning.persistence.repository.UtredningRepository;
import se.inera.intyg.intygsbestallning.service.notifiering.send.NotifieringSendService;
import se.inera.intyg.intygsbestallning.service.pdl.LogService;
import se.inera.intyg.intygsbestallning.service.pdl.dto.PdlLogType;
import se.inera.intyg.intygsbestallning.service.user.UserService;
import se.inera.intyg.intygsbestallning.service.utredning.ServiceTestUtil;
import se.inera.intyg.intygsbestallning.testutil.TestDataGen;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.RegisterBesokRequest;
import se.inera.intyg.intygsbestallning.web.responder.dto.ReportBesokAvvikelseRequest;

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

    @Mock
    private NotifieringSendService notifieringSendService;

    @Mock
    private UserService userService;

    @Mock
    private LogService logService;

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
    public void testRegisterBesokMedLakareSomProfession() {

        when(userService.getUser()).thenReturn(ServiceTestUtil.buildUser());

        RegisterBesokRequest request = new RegisterBesokRequest();
        request.setUtredningId(UTREDNING_ID);
        request.setUtredandeVardPersonalNamn("utredandeVardPersonalNamn");
        request.setProfession(DeltagarProfessionTyp.LK);
        request.setTolkStatus(TolkStatusTyp.BOKAT);
        request.setKallelseForm(KallelseFormTyp.TELEFONKONTAKT);
        request.setKallelseDatum(DATE_TIME);
        request.setBesokDatum(DATE_TIME.plusMonths(1).toLocalDate());
        request.setBesokStartTid(DATE_TIME.plusMonths(1).plusHours(4).toLocalTime());
        request.setBesokSlutTid(DATE_TIME.plusMonths(1).plusHours(6).toLocalTime());

        Utredning utredning = TestDataGen.createUtredning();
        utredning.setUtredningsTyp(UtredningsTyp.AFU);
        final Besok besok = TestDataGen.createBesok(request).stream().collect(onlyElement());

        // the database assigns besok_id when saved
        doAnswer(i -> {
            ((Utredning)i.getArgument(0)).getBesokList().get(0).setId(BESOK_ID);
            return null;
        }).when(utredningRepository).persist(any(Utredning.class));

        final ReportCareContactRequestDto dto = aReportCareContactRequestDto()
                .withAssessmentId(utredning.getUtredningId())
                .withAssessmentCareContactId(BESOK_ID.toString())
                .withParticipatingProfession(besok.getDeltagareProfession().name())
                .withInterpreterStatus(besok.getTolkStatus().getLabel())
                .withInvitationDate(SchemaDateUtil.toStringFromLocalDateTime(besok.getKallelseDatum()))
                .withInvitationChannel(besok.getKallelseForm().getCvValue())
                .withStartTime(besok.getBesokStartTid())
                .withEndTime(besok.getBesokSlutTid())
                .withVisitStatus(besok.getBesokStatus().getCvValue())
                .build();

        besokService.registerBesok(request);

        verify(myndighetIntegrationService, times(0)).updateAssessment(eq(UTREDNING_ID), eq(UTREDNING_TYP.name()));
        verify(myndighetIntegrationService, times(1)).reportCareContactInteraction(eq(dto));

        verify(logService, times(1)).log(argThat(arg -> arg.getPatientId().equals(TestDataGen.getPersonId())),
                argThat(arg -> arg == PdlLogType.BESOK_SKAPAT));
    }

    @Test
    public void testRegisterBesokMedAnnanProfession() {

        when(userService.getUser()).thenReturn(ServiceTestUtil.buildUser());

        RegisterBesokRequest request = new RegisterBesokRequest();
        request.setUtredningId(UTREDNING_ID);
        request.setUtredandeVardPersonalNamn("utredandeVardPersonalNamn");
        request.setProfession(DeltagarProfessionTyp.PS);
        request.setTolkStatus(TolkStatusTyp.BOKAT);
        request.setKallelseForm(KallelseFormTyp.TELEFONKONTAKT);
        request.setKallelseDatum(DATE_TIME);
        request.setBesokDatum(DATE_TIME.plusMonths(1).toLocalDate());
        request.setBesokStartTid(DATE_TIME.plusMonths(1).plusHours(4).toLocalTime());
        request.setBesokSlutTid(DATE_TIME.plusMonths(1).plusHours(6).toLocalTime());

        Utredning utredning = TestDataGen.createUtredning();
        utredning.setUtredningsTyp(UtredningsTyp.AFU);
        final Besok besok = TestDataGen.createBesok(request).stream().collect(onlyElement());

        // the database assigns besok_id when saved
        doAnswer(i -> {
            ((Utredning)i.getArgument(0)).getBesokList().get(0).setId(BESOK_ID);
            return null;
        }).when(utredningRepository).persist(any(Utredning.class));

        final ReportCareContactRequestDto dto = aReportCareContactRequestDto()
                .withAssessmentId(utredning.getUtredningId())
                .withAssessmentCareContactId(BESOK_ID.toString())
                .withParticipatingProfession(besok.getDeltagareProfession().name())
                .withInterpreterStatus(besok.getTolkStatus().getLabel())
                .withInvitationDate(SchemaDateUtil.toStringFromLocalDateTime(besok.getKallelseDatum()))
                .withInvitationChannel(besok.getKallelseForm().getCvValue())
                .withStartTime(besok.getBesokStartTid())
                .withEndTime(besok.getBesokSlutTid())
                .withVisitStatus(besok.getBesokStatus().getCvValue())
                .build();

        besokService.registerBesok(request);

        verify(myndighetIntegrationService, times(1)).updateAssessment(eq(UTREDNING_ID), eq(UTREDNING_TYP.name()));
        verify(myndighetIntegrationService, times(1)).reportCareContactInteraction(eq(dto));

        verify(logService, times(1)).log(argThat(arg -> arg.getPatientId().equals(TestDataGen.getPersonId())),
                argThat(arg -> arg == PdlLogType.BESOK_SKAPAT));
    }

    @Test
    public void testRegisterBesokIllegalState() {
        RegisterBesokRequest request = new RegisterBesokRequest();
        request.setUtredningId(UTREDNING_ID);
        request.setUtredandeVardPersonalNamn("utredandeVardPersonalNamn");
        request.setProfession(DeltagarProfessionTyp.PS);
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

        assertThatThrownBy(() -> besokService.registerBesok(request))
                .isExactlyInstanceOf(IbServiceException.class)
                .hasMessage("Utredning with id " + UTREDNING_ID + " is in an incorrect state");
    }

    @Test(expected = IbAuthorizationException.class)
    public void testRegisterBesokFelaktigVardenhet() {

        when(userService.getUser()).thenReturn(ServiceTestUtil.buildUser());

        RegisterBesokRequest request = new RegisterBesokRequest();
        request.setUtredningId(UTREDNING_ID);
        request.setUtredandeVardPersonalNamn("utredandeVardPersonalNamn");
        request.setProfession(DeltagarProfessionTyp.LK);
        request.setTolkStatus(TolkStatusTyp.BOKAT);
        request.setKallelseForm(KallelseFormTyp.TELEFONKONTAKT);
        request.setKallelseDatum(DATE_TIME);
        request.setBesokDatum(DATE_TIME.plusMonths(1).toLocalDate());
        request.setBesokStartTid(DATE_TIME.plusMonths(1).plusHours(4).toLocalTime());
        request.setBesokSlutTid(DATE_TIME.plusMonths(1).plusHours(6).toLocalTime());

        Utredning utredning = TestDataGen.createUtredning();
        utredning.getBestallning().get().setTilldeladVardenhetHsaId("AnnanVardenhet");
        doReturn(Optional.of(utredning))
                .when(utredningRepository).findById(eq(UTREDNING_ID));

        besokService.registerBesok(request);
    }

    @Test
    public void testRegisterBesokOmbokat() {
        when(userService.getUser()).thenReturn(ServiceTestUtil.buildUser());

        Utredning utredning = TestDataGen.createUtredning();
        utredning.setBesokList(ImmutableList.of(aBesok()
                .withId(BESOK_ID)
                .withBesokStatus(BesokStatusTyp.TIDBOKAD_VARDKONTAKT)
                .withDeltagareProfession(DeltagarProfessionTyp.LK)
                .withKallelseDatum(DATE_TIME)
                .withKallelseForm(KallelseFormTyp.TELEFONKONTAKT)
                .withBesokStartTid(DATE_TIME.plusMonths(1))
                .withBesokSlutTid(DATE_TIME.plusMonths(1).plusHours(1))
                .build()));

        doReturn(Optional.of(utredning))
                .when(utredningRepository).findById(eq(UTREDNING_ID));

        RegisterBesokRequest request = new RegisterBesokRequest();
        request.setUtredningId(UTREDNING_ID);
        request.setBesokId(BESOK_ID);
        request.setUtredandeVardPersonalNamn("changed");
        request.setProfession(DeltagarProfessionTyp.LK);
        request.setTolkStatus(TolkStatusTyp.BOKAT);
        request.setKallelseForm(KallelseFormTyp.TELEFONKONTAKT);
        request.setKallelseDatum(DATE_TIME);
        request.setBesokDatum(DATE_TIME.plusMonths(1).toLocalDate());
        request.setBesokStartTid(DATE_TIME.plusMonths(1).plusHours(4).toLocalTime());
        request.setBesokSlutTid(DATE_TIME.plusMonths(1).plusHours(6).toLocalTime());

        besokService.registerBesok(request);

        final ReportCareContactRequestDto dto = aReportCareContactRequestDto()
                .withAssessmentId(utredning.getUtredningId())
                .withAssessmentCareContactId(BESOK_ID.toString())
                .withParticipatingProfession(request.getProfession().name())
                .withInterpreterStatus(request.getTolkStatus().getLabel())
                .withInvitationDate(SchemaDateUtil.toStringFromLocalDateTime(request.getKallelseDatum()))
                .withInvitationChannel(request.getKallelseForm().getCvValue())
                .withStartTime(LocalDateTime.of(request.getBesokDatum(), request.getBesokStartTid()))
                .withEndTime(LocalDateTime.of(request.getBesokDatum(), request.getBesokSlutTid()))
                .withVisitStatus(BesokStatusTyp.TIDBOKAD_VARDKONTAKT.getCvValue())
                .build();

        verify(myndighetIntegrationService, times(0)).updateAssessment(eq(UTREDNING_ID), eq(UTREDNING_TYP.name()));
        verify(myndighetIntegrationService, times(1)).reportCareContactInteraction(eq(dto));

        verify(logService, times(1)).log(argThat(arg -> arg.getPatientId().equals(TestDataGen.getPersonId())),
                argThat(arg -> arg == PdlLogType.BESOK_ANDRAT));

        ArgumentCaptor<Utredning> utredningCaptor = ArgumentCaptor.forClass(Utredning.class);
        verify(utredningRepository).save(utredningCaptor.capture());
        assertEquals(HandelseTyp.OMBOKAT_BESOK, utredningCaptor.getValue().getHandelseList().get(0).getHandelseTyp());
    }

    @Test
    public void testRegisterBesokUppdaterat() {
        when(userService.getUser()).thenReturn(ServiceTestUtil.buildUser());

        Utredning utredning = TestDataGen.createUtredning();
        utredning.setUtredningsTyp(UtredningsTyp.AFU);
        utredning.setBesokList(ImmutableList.of(aBesok()
                .withId(BESOK_ID)
                .withBesokStatus(BesokStatusTyp.TIDBOKAD_VARDKONTAKT)
                .withDeltagareProfession(DeltagarProfessionTyp.LK)
                .withKallelseDatum(DATE_TIME)
                .withKallelseForm(KallelseFormTyp.TELEFONKONTAKT)
                .withBesokStartTid(DATE_TIME.plusMonths(1))
                .withBesokSlutTid(DATE_TIME.plusMonths(1).plusHours(1))
                .build()));

        doReturn(Optional.of(utredning))
                .when(utredningRepository).findById(eq(UTREDNING_ID));

        RegisterBesokRequest request = new RegisterBesokRequest();
        request.setUtredningId(UTREDNING_ID);
        request.setBesokId(BESOK_ID);
        request.setUtredandeVardPersonalNamn("changed");
        request.setProfession(DeltagarProfessionTyp.AT);
        request.setTolkStatus(TolkStatusTyp.BOKAT);
        request.setKallelseForm(KallelseFormTyp.TELEFONKONTAKT);
        request.setKallelseDatum(DATE_TIME);
        request.setBesokDatum(DATE_TIME.plusMonths(1).toLocalDate());
        request.setBesokStartTid(DATE_TIME.plusMonths(1).toLocalTime());
        request.setBesokSlutTid(DATE_TIME.plusMonths(1).plusHours(1).toLocalTime());

        besokService.registerBesok(request);

        final ReportCareContactRequestDto dto = aReportCareContactRequestDto()
                .withAssessmentId(utredning.getUtredningId())
                .withAssessmentCareContactId(BESOK_ID.toString())
                .withParticipatingProfession(request.getProfession().name())
                .withInterpreterStatus(request.getTolkStatus().getLabel())
                .withInvitationDate(SchemaDateUtil.toStringFromLocalDateTime(request.getKallelseDatum()))
                .withInvitationChannel(request.getKallelseForm().getCvValue())
                .withStartTime(LocalDateTime.of(request.getBesokDatum(), request.getBesokStartTid()))
                .withEndTime(LocalDateTime.of(request.getBesokDatum(), request.getBesokSlutTid()))
                .withVisitStatus(BesokStatusTyp.TIDBOKAD_VARDKONTAKT.getCvValue())
                .build();

        verify(myndighetIntegrationService, times(1)).updateAssessment(eq(UTREDNING_ID), eq(UTREDNING_TYP.name()));
        verify(myndighetIntegrationService, times(1)).reportCareContactInteraction(eq(dto));

        verify(logService, times(1)).log(argThat(arg -> arg.getPatientId().equals(TestDataGen.getPersonId())),
                argThat(arg -> arg == PdlLogType.BESOK_ANDRAT));

        ArgumentCaptor<Utredning> utredningCaptor = ArgumentCaptor.forClass(Utredning.class);
        verify(utredningRepository).save(utredningCaptor.capture());
        assertEquals(HandelseTyp.UPPDATERA_BESOK, utredningCaptor.getValue().getHandelseList().get(0).getHandelseTyp());
    }

    @Test(expected = IbNotFoundException.class)
    public void testRegisterBesokUpdateNotExisting() {
        when(userService.getUser()).thenReturn(ServiceTestUtil.buildUser());

        Utredning utredning = TestDataGen.createUtredning();
        utredning.setUtredningsTyp(UtredningsTyp.AFU);
        utredning.setBesokList(ImmutableList.of(aBesok()
                .withId(BESOK_ID)
                .withBesokStatus(BesokStatusTyp.TIDBOKAD_VARDKONTAKT)
                .withDeltagareProfession(DeltagarProfessionTyp.LK)
                .withKallelseDatum(DATE_TIME)
                .withKallelseForm(KallelseFormTyp.TELEFONKONTAKT)
                .withBesokStartTid(DATE_TIME.plusMonths(1))
                .withBesokSlutTid(DATE_TIME.plusMonths(1).plusHours(1))
                .build()));

        doReturn(Optional.of(utredning))
                .when(utredningRepository).findById(eq(UTREDNING_ID));

        RegisterBesokRequest request = new RegisterBesokRequest();
        request.setUtredningId(UTREDNING_ID);
        request.setBesokId(11111L);
        request.setUtredandeVardPersonalNamn("changed");
        request.setProfession(DeltagarProfessionTyp.AT);
        request.setTolkStatus(TolkStatusTyp.BOKAT);
        request.setKallelseForm(KallelseFormTyp.TELEFONKONTAKT);
        request.setKallelseDatum(DATE_TIME);
        request.setBesokDatum(DATE_TIME.plusMonths(1).toLocalDate());
        request.setBesokStartTid(DATE_TIME.plusMonths(1).toLocalTime());
        request.setBesokSlutTid(DATE_TIME.plusMonths(1).plusHours(1).toLocalTime());

        besokService.registerBesok(request);
    }

    @Test
    public void testReportBesokAvvikelseMottagen() {

        Utredning utredning = createUtredningForBesokTest();
        Utredning utredningAfterSavedAvvikelse = createUtredningForBesokTest();

        utredningAfterSavedAvvikelse.getBesokList().get(0).setAvvikelse(anAvvikelse()
                .withAvvikelseId(1L)
                .withOrsakatAv(AvvikelseOrsak.PATIENT)
                .withBeskrivning("beskrivning")
                .withTidpunkt(DATE_TIME)
                .build());

        utredningAfterSavedAvvikelse.getBesokList().get(0).getHandelseList().add(aHandelse()
                .withId(1L)
                .withHandelseTyp(HandelseTyp.AVVIKELSE_MOTTAGEN)
                .withSkapad(DATE_TIME.plusDays(1))
                .withAnvandare("Ann Vändare")
                .withHandelseText("Avvikelse Rapporterad")
                .withKommentar("Kom en Tar")
                .build());

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

        doReturn(utredningAfterSavedAvvikelse)
                .when(utredningRepository)
                .save(any());

        besokService.reportBesokAvvikelse(request);

        verifyZeroInteractions(myndighetIntegrationService);
    }

    @Test
    public void testReportBesokAvvikelseRapporterad() {

        when(userService.getUser()).thenReturn(ServiceTestUtil.buildUser());

        Utredning utredning = createUtredningForBesokTest();
        Utredning utredningAfterSavedAvvikelse = createUtredningForBesokTest();
        utredningAfterSavedAvvikelse.getBesokList().get(0).setAvvikelse(anAvvikelse()
                .withAvvikelseId(1L)
                .withOrsakatAv(AvvikelseOrsak.PATIENT)
                .withBeskrivning("beskrivning")
                .withTidpunkt(DATE_TIME)
                .build());

        utredningAfterSavedAvvikelse.getBesokList().get(0).getHandelseList().add(aHandelse()
                .withId(1L)
                .withHandelseTyp(HandelseTyp.AVVIKELSE_RAPPORTERAD)
                .withSkapad(DATE_TIME.plusDays(1))
                .withAnvandare("Ann Vändare")
                .withHandelseText("Avvikelse Rapporterad")
                .withKommentar("Kom en Tar")
                .build());

        final ReportBesokAvvikelseRequest request = aReportBesokAvvikelseRequest()
                .withBesokId(1L)
                .withOrsakatAv(AvvikelseOrsak.PATIENT)
                .withBeskrivning("beskrivning")
                .withTidpunkt(DATE_TIME)
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
        utredning.getIntygList().get(0).setSkickatDatum(DATE_TIME.minusDays(1));

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

