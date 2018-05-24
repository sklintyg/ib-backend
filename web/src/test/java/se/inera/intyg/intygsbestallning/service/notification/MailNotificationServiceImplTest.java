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
package se.inera.intyg.intygsbestallning.service.notification;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import se.inera.intyg.infra.integration.hsa.model.Vardenhet;
import se.inera.intyg.infra.integration.hsa.services.HsaOrganizationsService;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.VardenhetPreference;
import se.inera.intyg.intygsbestallning.persistence.repository.VardenhetPreferenceRepository;
import se.inera.intyg.intygsbestallning.service.mail.stub.MailServiceStub;
import se.inera.intyg.intygsbestallning.testutil.TestDataGen;

import javax.mail.MessagingException;
import javax.xml.ws.WebServiceException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MailNotificationServiceImplTest {

    @Spy
    private MailNotificationBodyFactory mailNotificationBodyFactory;

    @Mock
    private VardenhetPreferenceRepository vardenhetPreferenceRepository;

    @Mock
    private HsaOrganizationsService hsaOrganizationsService;

    @Mock
    private MailServiceStub mailService;

    @InjectMocks
    private MailNotificationServiceImpl testee;

    @Before
    public void init() {
        mailNotificationBodyFactory.initTemplates();
        // ReflectionTestUtils.setField(testee, "fromAddress", "test@ineratestarnotifications.se");
    }

    @Test
    public void testHandlingMottagenNotification() throws MessagingException {
        when(vardenhetPreferenceRepository.findByVardenhetHsaId(anyString())).thenReturn(buildVardenhetPreference());
        Utredning utredning = TestDataGen.createUtredning();
        utredning.getBestallning().get().setTilldeladVardenhetHsaId("ve-1");
        testee.notifyHandlingMottagen(utredning);
        verify(mailService, times(1)).sendNotificationToUnit(anyString(), anyString(), anyString());
        verifyZeroInteractions(hsaOrganizationsService);
    }

    @Test
    public void testHandlingMottagenNotificationMailAddressFromHsa() throws MessagingException {
        when(vardenhetPreferenceRepository.findByVardenhetHsaId(anyString())).thenReturn(Optional.empty());
        when(hsaOrganizationsService.getVardenhet(anyString())).thenReturn(buildVardenhet());
        Utredning utredning = TestDataGen.createUtredning();
        utredning.getBestallning().get().setTilldeladVardenhetHsaId("ve-1");
        testee.notifyHandlingMottagen(utredning);
        verify(mailService, times(1)).sendNotificationToUnit(anyString(), anyString(), anyString());
        verify(hsaOrganizationsService, times(1)).getVardenhet(anyString());
    }

    @Test(expected = IbServiceException.class)
    public void testThrowsExceptionIfNoBestallning() {
        Utredning utredning = TestDataGen.createUtredning();
        utredning.setBestallning(null);
        testee.notifyHandlingMottagen(utredning);
        verifyZeroInteractions(mailService);
        verifyZeroInteractions(hsaOrganizationsService);
    }

    @Test(expected = IbServiceException.class)
    public void testThrowsExceptionIfBestallningHasNoAssignedCareUnit() {
        Utredning utredning = TestDataGen.createUtredning();
        utredning.getBestallning().get().setTilldeladVardenhetHsaId(null);
        testee.notifyHandlingMottagen(utredning);
        verifyZeroInteractions(mailService);
        verifyZeroInteractions(hsaOrganizationsService);
    }

    @Test(expected = IbServiceException.class)
    public void testThrowsExceptionIfNoEmailAddressCouldBeFound() {
        Utredning utredning = TestDataGen.createUtredning();
        utredning.getBestallning().get().setTilldeladVardenhetHsaId("ve-1");

        when(vardenhetPreferenceRepository.findByVardenhetHsaId(anyString())).thenReturn(Optional.empty());
        when(hsaOrganizationsService.getVardenhet(anyString())).thenThrow(new WebServiceException(""));


        testee.notifyHandlingMottagen(utredning);
        verifyZeroInteractions(mailService);
        verifyZeroInteractions(hsaOrganizationsService);
    }

    private Vardenhet buildVardenhet() {
        Vardenhet ve = new Vardenhet("id", "namn");
        ve.setEpost("test@ineratest.se");
        return ve;
    }

    private Optional<VardenhetPreference> buildVardenhetPreference() {
        VardenhetPreference vp = new VardenhetPreference();
        vp.setVardenhetHsaId("ve-1");
        vp.setEpost("test@ineratest.se");
        return Optional.of(vp);
    }
}
