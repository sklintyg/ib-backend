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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static se.inera.intyg.intygsbestallning.integration.myndighet.dto.ReportCareContactRequestDto.ReportCareContactRequestDtoBuilder.aReportCareContactRequestDto;

import com.google.common.collect.MoreCollectors;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.integration.myndighet.dto.ReportCareContactRequestDto;
import se.inera.intyg.intygsbestallning.integration.myndighet.service.MyndighetIntegrationServiceImpl;
import se.inera.intyg.intygsbestallning.persistence.model.Besok;
import se.inera.intyg.intygsbestallning.persistence.model.Bestallning;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.type.DeltagarProfessionTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.KallelseFormTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.TolkStatusTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.UtredningsTyp;
import se.inera.intyg.intygsbestallning.persistence.repository.UtredningRepository;
import se.inera.intyg.intygsbestallning.testutil.TestDataGen;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.RegisterBesokRequest;

import java.time.LocalDateTime;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class BesokServiceImplTest {

    private static final String UTREDNING_ID = "utredningsId";
    private static final UtredningsTyp UTREDNING_TYP = UtredningsTyp.AFU_UTVIDGAD;
    private static final LocalDateTime DATE_TIME = LocalDateTime.of(2011, 11, 11, 11, 11, 11, 11);

    @Mock private UtredningRepository utredningRepository;
    @Mock private MyndighetIntegrationServiceImpl myndighetIntegrationService;
    @InjectMocks private BesokServiceImpl besokService;

    @Before
    public void setupMocks() {
        Utredning utredning = TestDataGen.createUtredning();
        utredning.setUtredningsTyp(UtredningsTyp.AFU);

        doReturn(Optional.ofNullable(utredning))
                .when(utredningRepository).findById(eq(UTREDNING_ID));

        doReturn(DATE_TIME.toLocalDate())
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
        final Besok besok = TestDataGen.createBesok(request).stream().collect(MoreCollectors.onlyElement());

        final ReportCareContactRequestDto dto = aReportCareContactRequestDto()
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
        final Besok besok = TestDataGen.createBesok(request).stream().collect(MoreCollectors.onlyElement());

        final ReportCareContactRequestDto dto = aReportCareContactRequestDto()
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

        doReturn(Optional.ofNullable(utredning))
                .when(utredningRepository).findById(eq(UTREDNING_ID));

        assertThatThrownBy(() -> besokService.registerNewBesok(request))
                .isExactlyInstanceOf(IbServiceException.class)
                .hasMessage("Assessment with id "+ UTREDNING_ID +" is in an incorrect state");
    }
}