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
package se.inera.intyg.intygsbestallning.service.utlatande;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import se.inera.intyg.intygsbestallning.auth.IbUser;
import se.inera.intyg.intygsbestallning.common.exception.IbAuthorizationException;
import se.inera.intyg.intygsbestallning.common.exception.IbNotFoundException;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.type.HandelseTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.HandlingUrsprungTyp;
import se.inera.intyg.intygsbestallning.persistence.repository.UtredningRepository;
import se.inera.intyg.intygsbestallning.service.pdl.LogService;
import se.inera.intyg.intygsbestallning.service.pdl.dto.PdlLogType;
import se.inera.intyg.intygsbestallning.persistence.model.status.UtredningStatus;
import se.inera.intyg.intygsbestallning.service.user.UserService;
import se.inera.intyg.intygsbestallning.service.utredning.ServiceTestUtil;
import se.inera.intyg.intygsbestallning.testutil.TestDataGen;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.utlatande.SendUtlatandeRequest;
import se.inera.intyg.intygsbestallning.web.responder.dto.ReportUtlatandeMottagetRequest;
import se.riv.intygsbestallning.certificate.order.reportcertificatereceival.v1.ReportCertificateReceivalType;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static se.inera.intyg.intygsbestallning.common.util.RivtaTypesUtil.anII;
import static se.inera.intyg.intygsbestallning.persistence.model.Besok.BesokBuilder.aBesok;
import static se.inera.intyg.intygsbestallning.persistence.model.Handling.HandlingBuilder.aHandling;
import static se.inera.intyg.intygsbestallning.persistence.model.Intyg.IntygBuilder.anIntyg;

@RunWith(MockitoJUnitRunner.class)
public class UtlatandeServiceImplTest {

    private static final String RECEIVAL_DATE = "20180909";
    private static final String LAST_DATE_FOR_SUPPLEMENT_REQUEST = "20181009";

    @Mock
    private UtredningRepository utredningRepository;

    @Mock
    private LogService logService;

    @Mock
    private UserService userService;

    @InjectMocks
    private UtlatandeServiceImpl utlatandeService;

    @Test
    public void sendUtlatandeSuccess() {
        String testDate = "2018-05-05";
        String userName = "testUser";

        Utredning utredning = TestDataGen.createUtredning();
        utredning.getIntygList().get(0).setSkickatDatum(null);
        utredning.getIntygList().get(0).setMottagetDatum(null);
        utredning.setInvanare(TestDataGen.createInvanare());
        utredning.setHandlingList(TestDataGen.createHandling());
        utredning.setBesokList(TestDataGen.createBesok());
        doReturn(Optional.of(utredning))
                .when(utredningRepository)
                .findById(TestDataGen.getUtredningId());

        when(userService.getUser()).thenReturn(ServiceTestUtil.buildUser());

        SendUtlatandeRequest request = new SendUtlatandeRequest();
        request.setUtlatandeSentDate(testDate);
        UtredningStatus status = utlatandeService.sendUtlatande(TestDataGen.getUtredningId(), request);
        assertEquals(UtredningStatus.UTLATANDE_SKICKAT, status);

        verify(logService).log(argThat(arg -> arg.getPatientId().equals(TestDataGen.getPersonId())),
                argThat(arg -> arg == PdlLogType.UTREDNING_UPPDATERAD));

        ArgumentCaptor<Utredning> utredningArgument = ArgumentCaptor.forClass(Utredning.class);
        verify(utredningRepository).save(utredningArgument.capture());
        utredning = utredningArgument.getValue();
        assertEquals(HandelseTyp.UTLATANDE_SKICKAT, utredning.getHandelseList().get(0).getHandelseTyp());
    }

    @Test(expected = IbNotFoundException.class)
    public void sendUtlatandeFailUtredningNotExisting() {
        String testDate = "2018-05-05";

        when(utredningRepository.findById(TestDataGen.getUtredningId())).thenReturn(Optional.empty());

        SendUtlatandeRequest request = new SendUtlatandeRequest();
        request.setUtlatandeSentDate(testDate);
        utlatandeService.sendUtlatande(TestDataGen.getUtredningId(), request);
    }

    @Test(expected = IbServiceException.class)
    public void sendUtlatandeFailMissingRequiredArgument() {
        SendUtlatandeRequest request = new SendUtlatandeRequest();
        utlatandeService.sendUtlatande(TestDataGen.getUtredningId(), request);
    }

    @Test(expected = IbServiceException.class)
    public void sendUtlatandeFailBadState() {
        String testDate = "2018-05-05";

        Utredning utredning = TestDataGen.createUtredning();
        utredning.setInvanare(TestDataGen.createInvanare());
        utredning.setHandlingList(TestDataGen.createHandling());
        utredning.setBesokList(TestDataGen.createBesok());
        doReturn(Optional.of(utredning))
                .when(utredningRepository)
                .findById(TestDataGen.getUtredningId());

        SendUtlatandeRequest request = new SendUtlatandeRequest();
        request.setUtlatandeSentDate(testDate);
        utlatandeService.sendUtlatande(TestDataGen.getUtredningId(), request);
    }

    @Test(expected = IbAuthorizationException.class)
    public void sendUtlatandeFailAnotherVardenhet() {
        String testDate = "2018-05-05";

        when(userService.getUser()).thenReturn(ServiceTestUtil.buildUser());

        Utredning utredning = TestDataGen.createUtredning();
        utredning.getIntygList().get(0).setSkickatDatum(null);
        utredning.getIntygList().get(0).setMottagetDatum(null);
        utredning.setInvanare(TestDataGen.createInvanare());
        utredning.setHandlingList(TestDataGen.createHandling());
        utredning.setBesokList(TestDataGen.createBesok());
        utredning.getBestallning().get().setTilldeladVardenhetHsaId("AnotherVardenhet");
        doReturn(Optional.of(utredning))
                .when(utredningRepository)
                .findById(TestDataGen.getUtredningId());

        SendUtlatandeRequest request = new SendUtlatandeRequest();
        request.setUtlatandeSentDate(testDate);
        utlatandeService.sendUtlatande(TestDataGen.getUtredningId(), request);
    }

    @Test
    public void registreraUtlatandeMottagetOk() {

        final Utredning utredning = TestDataGen.createUtredning();
        utredning.getHandlingList().add(aHandling()
                .withSkickatDatum(LocalDateTime.now())
                .withUrsprung(HandlingUrsprungTyp.BESTALLNING)
                .withInkomDatum(LocalDateTime.now())
                .build());
        utredning.getBesokList().add(aBesok()
                .build());
        utredning.setIntygList(ImmutableList.of(anIntyg()
                .withSkickatDatum(LocalDateTime.now())
                .build()));

        ReportCertificateReceivalType type = new ReportCertificateReceivalType();
        type.setAssessmentId(anII("", utredning.getUtredningId().toString()));
        type.setReceivedDate(RECEIVAL_DATE);
        type.setLastDateForSupplementRequest(LAST_DATE_FOR_SUPPLEMENT_REQUEST);

        final ReportUtlatandeMottagetRequest request = ReportUtlatandeMottagetRequest.from(type);

        doReturn(Optional.of(utredning))
                .when(utredningRepository)
                .findById(request.getUtredningId());

        utlatandeService.reportUtlatandeMottaget(request);
    }

    @Test
    public void registreraUtlatandeMottagetIncorrectStateNok() {

        final Utredning utredning = TestDataGen.createUtredning();

        ReportCertificateReceivalType type = new ReportCertificateReceivalType();
        type.setAssessmentId(anII("", utredning.getUtredningId().toString()));
        type.setReceivedDate(RECEIVAL_DATE);
        type.setLastDateForSupplementRequest(LAST_DATE_FOR_SUPPLEMENT_REQUEST);


        final ReportUtlatandeMottagetRequest request = ReportUtlatandeMottagetRequest.from(type);

        doReturn(Optional.of(utredning))
                .when(utredningRepository)
                .findById(request.getUtredningId());

        assertThatThrownBy(() -> utlatandeService.reportUtlatandeMottaget(request))
                .isExactlyInstanceOf(IbServiceException.class);
    }
}
