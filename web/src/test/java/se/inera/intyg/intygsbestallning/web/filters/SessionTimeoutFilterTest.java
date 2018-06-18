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
package se.inera.intyg.intygsbestallning.web.filters;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static se.inera.intyg.intygsbestallning.web.filters.SessionTimeoutFilter.SESSION_LAST_ACCESS_TIME;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@RunWith(MockitoJUnitRunner.class)
public class SessionTimeoutFilterTest {
    private static final String IGNORED_URL = "/test";
    private static final int ONE_SECOND = 1000;
    private static final int HALF_AN_HOUR = ONE_SECOND * 60 * 30;
    private static final int SESSION_MAX_TTL_SECONDS = 30;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private HttpSession session;

    @Test
    public void testDoNothingWhenNoSession() throws Exception {
        // Arrange
        when(request.getSession(false)).thenReturn(null);
        SessionTimeoutFilter filter = new SessionTimeoutFilter();
        filter.setIgnoredUrl(IGNORED_URL);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verify(session, never()).setAttribute(any(), any());

    }

    @Test
    public void testWillInvalidateSessionWhenExpired() throws Exception {
        // Arrange
        setupMocks(System.currentTimeMillis() - HALF_AN_HOUR, IGNORED_URL);
        SessionTimeoutFilter filter = new SessionTimeoutFilter();
        filter.setIgnoredUrl(IGNORED_URL);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verify(session).getMaxInactiveInterval();
        verify(session).invalidate();
        verify(session, never()).setAttribute(any(), any());

    }

    @Test
    public void testWillNotInvalidateValidSessionWhenNotExpired() throws Exception {
        // Arrange
        setupMocks(System.currentTimeMillis(), "anotherurl");
        SessionTimeoutFilter filter = new SessionTimeoutFilter();
        filter.setIgnoredUrl(IGNORED_URL);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verify(session, never()).invalidate();
        verify(session).setAttribute(eq(SESSION_LAST_ACCESS_TIME), any());

    }

    private void setupMocks(Long lastAccess, String reportedRequestURI) {

        when(request.getSession(false)).thenReturn(session);
        when(request.getRequestURI()).thenReturn(reportedRequestURI);
        when(session.getAttribute(eq(SESSION_LAST_ACCESS_TIME)))
                .thenReturn(lastAccess);
        when(session.getMaxInactiveInterval()).thenReturn(SESSION_MAX_TTL_SECONDS);

    }
}
