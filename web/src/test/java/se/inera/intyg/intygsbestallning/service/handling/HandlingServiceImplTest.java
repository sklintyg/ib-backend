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
package se.inera.intyg.intygsbestallning.service.handling;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import se.inera.intyg.intygsbestallning.common.exception.IbAuthorizationException;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.type.AvslutOrsak;
import se.inera.intyg.intygsbestallning.persistence.repository.UtredningRepository;
import se.inera.intyg.intygsbestallning.service.pdl.LogService;
import se.inera.intyg.intygsbestallning.service.user.UserService;
import se.inera.intyg.intygsbestallning.service.utredning.ServiceTestUtil;
import se.inera.intyg.intygsbestallning.testutil.TestDataGen;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.handling.RegisterHandlingRequest;

@RunWith(MockitoJUnitRunner.class)
public class HandlingServiceImplTest {

    @Mock
    private UtredningRepository utredningRepository;

    @Mock
    private UserService userService;

    @Mock
    private LogService logService;

    @InjectMocks
    private HandlingServiceImpl testee;

    @Test
    public void testOk() {
        Utredning utredning = TestDataGen.createUtredning();
        utredning.setHandlingList(TestDataGen.createHandling());
        when(utredningRepository.findById(anyLong())).thenReturn(Optional.of(utredning));

        when(userService.getUser()).thenReturn(ServiceTestUtil.buildUser());

        testee.registerHandlingMottagen(1L, buildRequest());
        verify(utredningRepository, times(1)).saveUtredning(any());
        verify(logService, times(1)).logHandlingMottagen(any(Utredning.class));
    }

    @Test(expected = IbServiceException.class)
    public void testUtredningAvbruten() {
        Utredning utredning = TestDataGen.createUtredning();
        utredning.setAvbrutenDatum(LocalDateTime.now());
        utredning.setAvbrutenOrsak(AvslutOrsak.JAV);
        when(userService.getUser()).thenReturn(ServiceTestUtil.buildUser());
        when(utredningRepository.findById(anyLong())).thenReturn(Optional.of(utredning));
        try {
            testee.registerHandlingMottagen(1L, buildRequest());
        } catch (Exception e) {
            throw e;
        }

    }

    @Test(expected = IbServiceException.class)
    public void testNoUtredningExists() {
        when(utredningRepository.findById(anyLong())).thenReturn(Optional.empty());
        testee.registerHandlingMottagen(1L, buildRequest());
    }

    @Test(expected = IbServiceException.class)
    public void testInvalidDate() {
        RegisterHandlingRequest req = buildRequest();
        req.setHandlingarMottogsDatum("2013-14-12");
        testee.registerHandlingMottagen(1L, req);
    }

    @Test(expected = IbAuthorizationException.class)
    public void testInvalidVardenhet() {
        when(userService.getUser()).thenReturn(ServiceTestUtil.buildUser());
        Utredning utredning = TestDataGen.createUtredning();
        utredning.getBestallning().get().setTilldeladVardenhetHsaId("AnnanVardenhet");
        when(utredningRepository.findById(anyLong())).thenReturn(Optional.of(utredning));
        RegisterHandlingRequest req = buildRequest();
        testee.registerHandlingMottagen(1L, req);
    }

    @Test(expected = IbServiceException.class)
    public void testNoWaitingHandling() {
        Utredning utredning = TestDataGen.createUtredning();
        when(utredningRepository.findById(anyLong())).thenReturn(Optional.of(utredning));
        when(userService.getUser()).thenReturn(ServiceTestUtil.buildUser());

        testee.registerHandlingMottagen(1L, buildRequest());
    }

    private RegisterHandlingRequest buildRequest() {
        return new RegisterHandlingRequest(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
    }
}
