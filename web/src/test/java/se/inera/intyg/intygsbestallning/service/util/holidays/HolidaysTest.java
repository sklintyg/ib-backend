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

import org.junit.Ignore;
import org.junit.Test;

import java.time.LocalDate;
import java.time.MonthDay;
import java.util.ArrayList;
import java.util.List;

import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static org.junit.Assert.assertEquals;

/**
 * @author Magnus Ekstrand on 2018-04-27.
 */
public class HolidaysTest {

    private static final HolidayCalendar testee = Holidays.SWE;

    Object[][] dataSweHol() {
        // official data from published fixing dates
        return new Object[][] {
                {2014, mds(2014, md(1, 1), md(1, 6), md(4, 18), md(4, 21),
                        md(5, 1), md(5, 29), md(6, 6), md(6, 20), md(12, 24), md(12, 25), md(12, 26), md(12, 31))},
                {2015, mds(2015, md(1, 1), md(1, 6), md(4, 3), md(4, 6),
                        md(5, 1), md(5, 14), md(6, 19), md(12, 24), md(12, 25), md(12, 31))},
                {2016, mds(2016, md(1, 1), md(1, 6), md(3, 25), md(3, 28),
                        md(5, 5), md(6, 6), md(6, 24), md(12, 26))},
        };
    }

    @Test
    public void testSweHol() {
        Object[][] data = dataSweHol();
        for (int row = 0; row < data.length; row++) {
            int year = (Integer)data[row][0];
            List<LocalDate> holidays = (List<LocalDate>)data[row][1];
            assertSweHol(year, holidays);
        }
    }

    @Ignore
    @Test
    public void printSweHol() {
        Object[][] data = dataSweHol();

        // let's loop through array to print each row and column
        for (int row = 0; row < data.length; row++) {
            for (int col = 0; col < data[row].length; col++) {
                System.out.print(data[row][col] + "\t");
            }
            System.out.println();
        }
    }

    private void assertSweHol(int year, List<LocalDate> holidays) {
        LocalDate date = date(year, 1, 1);
        int len = date.lengthOfYear();
        for (int i = 0; i < len; i++) {
            boolean isHoliday = holidays.contains(date) || date.getDayOfWeek() == SATURDAY || date.getDayOfWeek() == SUNDAY;
            assertEquals(date.toString(), testee.isHoliday(date), isHoliday);
            date = date.plusDays(1);
        }
    }

    private static List<LocalDate> mds(int year, MonthDay... monthDays) {
        List<LocalDate> holidays = new ArrayList<>();
        for (MonthDay md : monthDays) {
            holidays.add(md.atYear(year));
        }
        return holidays;
    }

    private static MonthDay md(int month, int day) {
        return MonthDay.of(month, day);
    }

    private LocalDate date(int year, int month, int day) {
        return LocalDate.of(year, month, day);
    }

}
