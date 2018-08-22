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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import se.inera.intyg.intygsbestallning.common.exception.IbErrorCodeEnum;
import se.inera.intyg.intygsbestallning.common.exception.IbExternalServiceException;
import se.inera.intyg.intygsbestallning.common.exception.IbExternalSystemEnum;
import se.inera.intyg.intygsbestallning.common.exception.IbNotFoundException;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.common.util.SchemaDateUtil;
import se.inera.intyg.intygsbestallning.integration.myndighet.dto.ReportCareContactRequestDto;
import se.inera.intyg.intygsbestallning.integration.myndighet.service.MyndighetIntegrationService;
import se.inera.intyg.intygsbestallning.persistence.model.Besok;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.type.BesokStatusTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.TolkStatusTyp;
import se.inera.intyg.intygsbestallning.persistence.repository.UtredningRepository;
import se.inera.intyg.intygsbestallning.service.pdl.LogService;
import se.inera.intyg.intygsbestallning.service.pdl.dto.PdlLogType;
import se.inera.intyg.intygsbestallning.service.user.UserService;
import se.inera.intyg.intygsbestallning.service.utredning.ServiceTestUtil;
import se.inera.intyg.intygsbestallning.testutil.TestDataGen;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.besok.RedovisaBesokRequest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static se.inera.intyg.intygsbestallning.integration.myndighet.dto.ReportCareContactRequestDto.ReportCareContactRequestDtoBuilder.aReportCareContactRequestDto;

@RunWith(MockitoJUnitRunner.class)
public class BesokReportServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private UtredningRepository utredningRepository;

    @Mock
    private LogService logService;

    @Mock
    private MyndighetIntegrationService myndighetIntegrationService;

    @InjectMocks
    private BesokReportServiceImpl besokReportService;

    @Test
    public void testRedovisaBesokInNewTransactionSuccess() {
        when(userService.getUser()).thenReturn(ServiceTestUtil.buildUser());

        Utredning utredning = TestDataGen.createUtredning();
        Besok besok1;
        utredning.setBesokList(ImmutableList.of(
                besok1 = TestDataGen.createBesok(1L),
                TestDataGen.createBesok(2L),
                TestDataGen.createBesok(3L)));

        final ReportCareContactRequestDto dto = aReportCareContactRequestDto()
                .withAssessmentId(utredning.getUtredningId())
                .withAssessmentCareContactId(besok1.getId().toString())
                .withParticipatingProfession(besok1.getDeltagareProfession().name())
                .withInterpreterStatus(TolkStatusTyp.DELTAGIT.getId())
                .withInvitationDate(SchemaDateUtil.toDateStringFromLocalDateTime(besok1.getKallelseDatum()))
                .withInvitationChannel(besok1.getKallelseForm().getCvValue())
                .withStartTime(besok1.getBesokStartTid())
                .withEndTime(besok1.getBesokSlutTid())
                .withVisitStatus(BesokStatusTyp.AVSLUTAD_VARDKONTAKT.getCvValue())
                .build();

        RedovisaBesokRequest.RedovisaBesokListItem besokRequest = new RedovisaBesokRequest.RedovisaBesokListItem(1L, TolkStatusTyp.DELTAGIT, true);

        besokReportService.redovisaBesok(utredning, besokRequest);

        verify(myndighetIntegrationService, times(1)).reportCareContactInteraction(eq(dto));

        verify(logService, times(1)).log(argThat(arg -> arg.getPatientId().equals(TestDataGen.getPersonId())),
                argThat(arg -> arg == PdlLogType.BESOK_REDOVISAT));
    }

    @Test
    public void testRedovisaBesokInNewTransactionSuccessNotGenomfort() {
        Utredning utredning = TestDataGen.createUtredning();
        utredning.setBesokList(ImmutableList.of(
                TestDataGen.createBesok(1L),
                TestDataGen.createBesok(2L),
                TestDataGen.createBesok(3L)));

        RedovisaBesokRequest.RedovisaBesokListItem besokRequest = new RedovisaBesokRequest.RedovisaBesokListItem(1L, TolkStatusTyp.DELTAGIT, false);

        besokReportService.redovisaBesok(utredning, besokRequest);

        ArgumentCaptor<Utredning> savedUtredningCaptor = ArgumentCaptor.forClass(Utredning.class);
        verify(utredningRepository, times(1)).saveUtredning(savedUtredningCaptor.capture());
        assertEquals(TolkStatusTyp.DELTAGIT, savedUtredningCaptor.getValue().getBesokList().get(0).getTolkStatus());
        assertEquals(BesokStatusTyp.TIDBOKAD_VARDKONTAKT, savedUtredningCaptor.getValue().getBesokList().get(0).getBesokStatus());

        verifyZeroInteractions(myndighetIntegrationService);
        verifyZeroInteractions(logService);
    }

    @Test
    public void testRedovisaBesokInNewTransactionFailBesokNotFound() {
        Utredning utredning = TestDataGen.createUtredning();
        utredning.setBesokList(ImmutableList.of(
                TestDataGen.createBesok(1L),
                TestDataGen.createBesok(2L),
                TestDataGen.createBesok(3L)));

        RedovisaBesokRequest.RedovisaBesokListItem besokRequest = new RedovisaBesokRequest.RedovisaBesokListItem(4L, TolkStatusTyp.DELTAGIT, false);

        assertThatThrownBy(() -> besokReportService.redovisaBesok(utredning, besokRequest))
                .isExactlyInstanceOf(IbNotFoundException.class)
                .hasFieldOrPropertyWithValue("errorEntityId", 4L);
    }

    @Test
    public void testRedovisaBesokInNewTransactionFailMyndighet() {
        when(userService.getUser()).thenReturn(ServiceTestUtil.buildUser());

        doThrow(new IbExternalServiceException(IbErrorCodeEnum.EXTERNAL_ERROR, IbExternalSystemEnum.MYNDIGHET, "", null))
                .when(myndighetIntegrationService).reportCareContactInteraction(any(ReportCareContactRequestDto.class));

        Utredning utredning = TestDataGen.createUtredning();
        utredning.setBesokList(ImmutableList.of(
                TestDataGen.createBesok(1L),
                TestDataGen.createBesok(2L),
                TestDataGen.createBesok(3L)));

        RedovisaBesokRequest.RedovisaBesokListItem besokRequest = new RedovisaBesokRequest.RedovisaBesokListItem(1L, TolkStatusTyp.DELTAGIT, true);

        assertThatThrownBy(() -> besokReportService.redovisaBesok(utredning, besokRequest))
                .isExactlyInstanceOf(IbExternalServiceException.class)
                .hasFieldOrPropertyWithValue("errorEntityId", 1L);
    }

    @Test
    public void testRedovisaBesokInNewTransactionFailTolkStatus() {
        Utredning utredning = TestDataGen.createUtredning();
        utredning.setBesokList(ImmutableList.of(
                TestDataGen.createBesok(1L),
                TestDataGen.createBesok(2L),
                TestDataGen.createBesok(3L)));

        RedovisaBesokRequest.RedovisaBesokListItem besokRequest = new RedovisaBesokRequest.RedovisaBesokListItem(1L, TolkStatusTyp.BOKAD, true);

        assertThatThrownBy(() -> besokReportService.redovisaBesok(utredning, besokRequest))
                .isExactlyInstanceOf(IbServiceException.class)
                .hasFieldOrPropertyWithValue("errorEntityId", 1L);
    }
}
