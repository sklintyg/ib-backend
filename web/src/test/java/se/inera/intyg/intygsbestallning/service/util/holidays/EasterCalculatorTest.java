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
package se.inera.intyg.intygsbestallning.service.util.holidays;

import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class EasterCalculatorTest {

    private EasterCalculator testee = EasterCalculator.of(2018);

    @Test
    public void testGoodFriday2018() {
        assertGoodFriday2018(testee.goodFriday());
    }

    @Test
    public void testEasterEve2018() {
        assertEasterEve2018(testee.easterEve());
    }

    @Test
    public void testEasterSunday2018() {
        assertEasterSunday2018(testee.easterSunday());
    }

    @Test
    public void testEasterMonday2018() {
        assertEasterMonday2018(testee.easterMonday());
    }

    @Test
    public void testEasterDates2018() {
        List<LocalDate> localDates = testee.easterDates();
        assertEquals(4, localDates.size());
        assertGoodFriday2018(localDates.get(0));
        assertEasterEve2018(localDates.get(1));
        assertEasterSunday2018(localDates.get(2));
        assertEasterMonday2018(localDates.get(3));
    }

    public void assertGoodFriday2018(LocalDate localDate) {
        assertEquals(30, localDate.getDayOfMonth());
        assertEquals(3, localDate.getMonthValue());
    }

    public void assertEasterEve2018(LocalDate localDate) {
        assertEquals(31, localDate.getDayOfMonth());
        assertEquals(3, localDate.getMonthValue());
    }

    public void assertEasterSunday2018(LocalDate localDate) {
        assertEquals(1, localDate.getDayOfMonth());
        assertEquals(4, localDate.getMonthValue());
    }

    public void assertEasterMonday2018(LocalDate localDate) {
        assertEquals(2, localDate.getDayOfMonth());
        assertEquals(4, localDate.getMonthValue());
    }

}
