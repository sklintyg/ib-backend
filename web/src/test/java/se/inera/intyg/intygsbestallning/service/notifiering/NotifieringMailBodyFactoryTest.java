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
package se.inera.intyg.intygsbestallning.service.notifiering;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

import se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailBodyFactory;

public class NotifieringMailBodyFactoryTest {

    private static final String TEST_MSG = "all your base are belong to us";
    private static final String TEST_URL = "http://ineratest.nu/utredning";
    private NotifieringMailBodyFactory testee = new NotifieringMailBodyFactory();

    @Before
    public void init() {
        testee.initTemplates();
    }

    @Test
    public void testGenerateUtredningNotification() {
        String body = testee.buildBodyForUtredning(TEST_MSG, TEST_URL);
        assertTrue(body.contains(TEST_MSG));
        assertTrue(body.contains(TEST_URL));
    }

    @Test
    public void testGenerateForfraganNotification() {
        String body = testee.buildBodyForForfragan(TEST_MSG, TEST_URL);
        assertTrue(body.contains(TEST_MSG));
        assertTrue(body.contains(TEST_URL));
    }
}
