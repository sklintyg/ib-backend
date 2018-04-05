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
package se.inera.intyg.intygsbestallning.web.controller.api;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import se.inera.intyg.infra.integration.hsa.model.Vardenhet;
import se.inera.intyg.infra.security.authorities.AuthoritiesException;
import se.inera.intyg.infra.security.authorities.CommonAuthoritiesResolver;
import se.inera.intyg.intygsbestallning.auth.IbUser;
import se.inera.intyg.intygsbestallning.service.user.UserService;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.ChangeSelectedUnitRequest;

import java.util.Collections;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by marced on 01/02/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    IbUser ibUserMock;

    @Mock
    UserService userService;

    @Mock
    private CommonAuthoritiesResolver commonAuthoritiesResolver;

    @InjectMocks
    private UserController userController = new UserController();

    @Before
    public void before() {
        when(commonAuthoritiesResolver.getFeatures(any())).thenReturn(Collections.emptyMap());
        when(userService.getUser()).thenReturn(ibUserMock);
        when(ibUserMock.getValdVardenhet()).thenReturn(new Vardenhet("123", "enhet"));
        when(ibUserMock.getValdVardgivare()).thenReturn(new Vardenhet("456", "vardgivare"));
        when(ibUserMock.changeValdVardenhet(anyString())).thenReturn(true);
    }

    @Test
    public void testCreateGet() {
        userController.getUser();

        verify(userService).getUser();
    }

    @Test
    public void testChangeEnhetSuccess() {
        ChangeSelectedUnitRequest req = new ChangeSelectedUnitRequest("123");

        userController.changeSelectedUnitOnUser(req);

        verify(userService).getUser();
    }

    @Test
    public void testChangeEnhetFails() {
        when(ibUserMock.changeValdVardenhet(anyString())).thenReturn(false);
        ChangeSelectedUnitRequest req = new ChangeSelectedUnitRequest("123");
         thrown.expect(AuthoritiesException.class);

        userController.changeSelectedUnitOnUser(req);

        verify(userService).getUser();

    }
}
