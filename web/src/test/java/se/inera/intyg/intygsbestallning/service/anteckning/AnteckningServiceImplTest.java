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
package se.inera.intyg.intygsbestallning.service.anteckning;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import se.inera.intyg.intygsbestallning.common.exception.IbAuthorizationException;
import se.inera.intyg.intygsbestallning.common.exception.IbNotFoundException;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.type.HandelseTyp;
import se.inera.intyg.intygsbestallning.persistence.repository.UtredningRepository;
import se.inera.intyg.intygsbestallning.service.pdl.LogService;
import se.inera.intyg.intygsbestallning.service.pdl.dto.PDLLoggable;
import se.inera.intyg.intygsbestallning.service.pdl.dto.PdlLogType;
import se.inera.intyg.intygsbestallning.service.user.UserService;
import se.inera.intyg.intygsbestallning.service.utredning.ServiceTestUtil;
import se.inera.intyg.intygsbestallning.testutil.TestDataGen;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.anteckning.CreateAnteckningRequest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AnteckningServiceImplTest {

    private static final Long UTREDNING_ID = 1L;

    @Mock
    private UtredningRepository utredningRepository;

    @Mock
    private LogService logService;

    @Mock
    private UserService userService;

    @InjectMocks
    private AnteckningServiceImpl anteckningService;

    @Test
    public void testCreateAnteckningSuccess() {
        when(userService.getUser()).thenReturn(ServiceTestUtil.buildUser());

        Utredning utredning = TestDataGen.createUtredning();
        doReturn(Optional.of(utredning))
                .when(utredningRepository).findById(eq(UTREDNING_ID));

        CreateAnteckningRequest request = new CreateAnteckningRequest();
        request.setText("Test anteckning med text");

        anteckningService.createAnteckning(UTREDNING_ID, request);

        verify(logService, times(1)).log(any(PDLLoggable.class), eq(PdlLogType.ANTECKNING_SKAPAD));
        verify(utredningRepository, times(1)).saveUtredning(eq(utredning));
        assertEquals(HandelseTyp.NY_ANTECKNING, utredning.getHandelseList().get(0).getHandelseTyp());
    }

    @Test
    public void testCreateAnteckningFailUtredningNotFound() {
        doReturn(Optional.empty())
                .when(utredningRepository).findById(eq(UTREDNING_ID));

        CreateAnteckningRequest request = new CreateAnteckningRequest();
        request.setText("Test anteckning med text");

        assertThatThrownBy(() -> anteckningService.createAnteckning(UTREDNING_ID, request))
                .isExactlyInstanceOf(IbNotFoundException.class);

        verify(logService, times(0)).log(any(PDLLoggable.class), any(PdlLogType.class));
        verify(utredningRepository, times(0)).saveUtredning(any(Utredning.class));
    }

    @Test
    public void testCreateAnteckningFelaktigVardenhet() {
        when(userService.getUser()).thenReturn(ServiceTestUtil.buildUser());

        Utredning utredning = TestDataGen.createUtredning();
        utredning.getBestallning().get().setTilldeladVardenhetHsaId("AnnanVardenhet");
        doReturn(Optional.of(utredning))
                .when(utredningRepository).findById(eq(UTREDNING_ID));

        CreateAnteckningRequest request = new CreateAnteckningRequest();
        request.setText("Test anteckning med text");

        assertThatThrownBy(() -> anteckningService.createAnteckning(UTREDNING_ID, request))
                .isExactlyInstanceOf(IbAuthorizationException.class);

        verify(logService, times(0)).log(any(PDLLoggable.class), any(PdlLogType.class));
        verify(utredningRepository, times(0)).saveUtredning(any(Utredning.class));
    }

    @Test
    public void testCreateAnteckningFailEmptyText() {
        CreateAnteckningRequest request = new CreateAnteckningRequest();
        request.setText("");

        assertThatThrownBy(() -> anteckningService.createAnteckning(UTREDNING_ID, request))
                .isExactlyInstanceOf(IllegalArgumentException.class);

        verify(logService, times(0)).log(any(PDLLoggable.class), any(PdlLogType.class));
        verify(utredningRepository, times(0)).saveUtredning(any(Utredning.class));
    }
}
