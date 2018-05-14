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
package se.inera.intyg.intygsbestallning.service.util.date;

import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

/**
 * @author Magnus Ekstrand on 2018-04-27.
 */
public class HolidayCalendarTest {

    // Base dates
    private static final LocalDate WED_2018_03_28 = LocalDate.of(2018, 3, 28);
    private static final LocalDate FRI_2018_04_13 = LocalDate.of(2018, 4, 13);

    private static final LocalDate MON_2018_08_06 = LocalDate.of(2018, 8, 6);
    private static final LocalDate WED_2018_08_08 = LocalDate.of(2018, 8, 8);
    private static final LocalDate THU_2018_08_09 = LocalDate.of(2018, 8, 9);
    private static final LocalDate FRI_2018_08_10 = LocalDate.of(2018, 8, 10);
    private static final LocalDate SAT_2018_08_11 = LocalDate.of(2018, 8, 11);
    private static final LocalDate SUN_2018_08_12 = LocalDate.of(2018, 8, 12);
    private static final LocalDate MON_2018_08_13 = LocalDate.of(2018, 8, 13);
    private static final LocalDate TUE_2018_08_14 = LocalDate.of(2018, 8, 14);
    private static final LocalDate WED_2018_08_15 = LocalDate.of(2018, 8, 15);
    private static final LocalDate THU_2018_08_16 = LocalDate.of(2018, 8, 16);
    private static final LocalDate FRI_2018_08_17 = LocalDate.of(2018, 8, 17);
    private static final LocalDate SAT_2018_08_18 = LocalDate.of(2018, 8, 18);
    private static final LocalDate SUN_2018_08_19 = LocalDate.of(2018, 8, 19);
    private static final LocalDate MON_2018_08_20 = LocalDate.of(2018, 8, 20);
    private static final LocalDate TUE_2018_08_21 = LocalDate.of(2018, 8, 21);
    private static final LocalDate WED_2018_08_22 = LocalDate.of(2018, 8, 22);

    private static final LocalDate THU_2018_08_30 = LocalDate.of(2018, 8, 30);
    private static final LocalDate FRI_2018_08_31 = LocalDate.of(2018, 8, 31);

    // Past dates
    private static final LocalDate FRI_2014_07_11 = LocalDate.of(2014, 7, 11);
    private static final LocalDate SAT_2014_07_12 = LocalDate.of(2014, 7, 12);
    private static final LocalDate SUN_2014_07_13 = LocalDate.of(2014, 7, 13);
    private static final LocalDate MON_2014_07_14 = LocalDate.of(2014, 7, 14);

    // Future dates
    private static final LocalDate FRI_2020_09_11 = LocalDate.of(2020, 9, 11);
    private static final LocalDate SAT_2020_09_12 = LocalDate.of(2020, 9, 12);
    private static final LocalDate SUN_2020_09_13 = LocalDate.of(2020, 9, 13);
    private static final LocalDate MON_2020_09_14 = LocalDate.of(2020, 9, 14);



    private static final HolidayCalendar testee = Holidays.SWE;

    @Test
    public void testIsBusinessday() {
        assertEquals(testee.isBusinessDay(FRI_2014_07_11), true);
        assertEquals(testee.isBusinessDay(MON_2014_07_14), true);

        assertEquals(testee.isBusinessDay(FRI_2018_08_10), true);
        assertEquals(testee.isBusinessDay(MON_2018_08_13), true);

        assertEquals(testee.isBusinessDay(FRI_2020_09_11), true);
        assertEquals(testee.isBusinessDay(MON_2020_09_14), true);
    }

    @Test
    public void testDaysBetweenSaturdaySunday() {
        assertEquals(testee.daysBetween(FRI_2018_08_10, MON_2018_08_13), 1);
    }

    @Test
    public void testDaysBeetweenPreEasterAndPostNextWeekend() {
        assertEquals(testee.daysBetween(WED_2018_03_28, FRI_2018_04_13), 10);
    }

    @Test
    public void testSaturdaySundayIsHoliday() {
        assertEquals(testee.isHoliday(SAT_2014_07_12), true);
        assertEquals(testee.isHoliday(SUN_2014_07_13), true);

        assertEquals(testee.isHoliday(SAT_2018_08_11), true);
        assertEquals(testee.isHoliday(SUN_2018_08_12), true);
        assertEquals(testee.isHoliday(SAT_2018_08_18), true);
        assertEquals(testee.isHoliday(SUN_2018_08_19), true);

        assertEquals(testee.isHoliday(SAT_2020_09_12), true);
        assertEquals(testee.isHoliday(SUN_2020_09_13), true);
    }

}
