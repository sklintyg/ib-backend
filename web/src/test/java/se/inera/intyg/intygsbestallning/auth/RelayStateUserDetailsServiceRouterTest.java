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
package se.inera.intyg.intygsbestallning.auth;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.saml.SAMLCredential;
import se.inera.intyg.intygsbestallning.auth.bp.IbBpUserDetailsService;
import se.inera.intyg.intygsbestallning.auth.model.IbRelayStateType;
import se.inera.intyg.intygsbestallning.common.exception.IbAuthorizationException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RelayStateUserDetailsServiceRouterTest {

    @Mock
    private IbUserDetailsService ibUserDetailsService;

    @Mock
    private IbBpUserDetailsService ibBpUserDetailsService;

    @InjectMocks
    private RelayStateUserDetailsServiceRouter testee;

    @Test
    public void testRoutesToIbUDS() {
        testee.loadUserBySAML(buildCredential(IbRelayStateType.FMU.name()));
        verify(ibUserDetailsService).loadUserBySAML(any(SAMLCredential.class));
        verifyZeroInteractions(ibBpUserDetailsService);
    }

    @Test
    public void testRoutesToIbBpUDS() {
        testee.loadUserBySAML(buildCredential(IbRelayStateType.BP.name()));
        verify(ibBpUserDetailsService).loadUserBySAML(any(SAMLCredential.class));
        verifyZeroInteractions(ibUserDetailsService);
    }

    @Test
    public void testRoutesToIbUDSWhenNullRelayState() {
        testee.loadUserBySAML(buildCredential(null));
        verify(ibUserDetailsService).loadUserBySAML(any(SAMLCredential.class));
        verifyZeroInteractions(ibBpUserDetailsService);
    }

    @Test
    public void testRoutesToIbUDSWhenBlankRelayState() {
        testee.loadUserBySAML(buildCredential(""));
        verify(ibUserDetailsService).loadUserBySAML(any(SAMLCredential.class));
        verifyZeroInteractions(ibBpUserDetailsService);
    }

    @Test(expected = IbAuthorizationException.class)
    public void testThrowsExceptionOnUnknownRelayState() {
        testee.loadUserBySAML(buildCredential("helt-okand"));
        verifyZeroInteractions(ibUserDetailsService);
        verifyZeroInteractions(ibBpUserDetailsService);
    }

    private SAMLCredential buildCredential(String relayState) {
        SAMLCredential mock = mock(SAMLCredential.class);
        when(mock.getRelayState()).thenReturn(relayState);
        return mock;
    }
}
