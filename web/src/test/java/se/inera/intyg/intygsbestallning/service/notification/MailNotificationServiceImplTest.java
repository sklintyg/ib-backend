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
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.VardenhetPreference;
import se.inera.intyg.intygsbestallning.service.mail.stub.MailServiceStub;
import se.inera.intyg.intygsbestallning.service.vardenhet.VardenhetService;
import se.inera.intyg.intygsbestallning.testutil.TestDataGen;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.vardenhet.VardenhetPreferenceResponse;

import javax.mail.MessagingException;

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
    private VardenhetService vardenhetService;

    @Mock
    private MailServiceStub mailService;

    @InjectMocks
    private MailNotificationServiceImpl testee;

    @Before
    public void init() {
        mailNotificationBodyFactory.initTemplates();
    }

    @Test
    public void testHandlingMottagenNotification() throws MessagingException {
        when(vardenhetService.getVardEnhetPreference(anyString())).thenReturn(buildVardenhetPreferenceResponse("test@ineratest.se"));
        Utredning utredning = TestDataGen.createUtredning();
        utredning.getBestallning().get().setTilldeladVardenhetHsaId("ve-1");
        testee.notifyHandlingMottagen(utredning);
        verify(mailService, times(1)).sendNotificationToUnit(anyString(), anyString(), anyString());
    }

    private VardenhetPreferenceResponse buildVardenhetPreferenceResponse(String epost) {
        VardenhetPreference pref = new VardenhetPreference();
        pref.setEpost(epost);
        VardenhetPreferenceResponse response = new VardenhetPreferenceResponse(pref);
        return response;
    }

    @Test
    public void testHandlingMottagenNotificationMailAddressFromHsa() throws MessagingException {
        when(vardenhetService.getVardEnhetPreference(anyString())).thenReturn(buildVardenhetPreferenceResponse("test@ineratest.se"));
        Utredning utredning = TestDataGen.createUtredning();
        utredning.getBestallning().get().setTilldeladVardenhetHsaId("ve-1");
        testee.notifyHandlingMottagen(utredning);
        verify(mailService, times(1)).sendNotificationToUnit(anyString(), anyString(), anyString());
    }

    @Test(expected = IbServiceException.class)
    public void testThrowsExceptionIfNoBestallning() {
        Utredning utredning = TestDataGen.createUtredning();
        utredning.setBestallning(null);
        testee.notifyHandlingMottagen(utredning);
        verifyZeroInteractions(mailService);
    }

    @Test(expected = IbServiceException.class)
    public void testThrowsExceptionIfBestallningHasNoAssignedCareUnit() {
        Utredning utredning = TestDataGen.createUtredning();
        utredning.getBestallning().get().setTilldeladVardenhetHsaId(null);
        testee.notifyHandlingMottagen(utredning);
        verifyZeroInteractions(mailService);
    }
}
