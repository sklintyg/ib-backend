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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.time.DayOfWeek.FRIDAY;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.temporal.TemporalAdjusters.nextOrSame;

/**
 * @author Magnus Ekstrand on 2018-04-26.
 */
public class Holidays {

    /**
     * The holiday calendar for Sweden.
     */
    public static final HolidayCalendar SWE = generateSweden();


    //- - Generate the Swedish holiday calendar

    static ImmutableHolidayCalendar generateSweden() {
        List<LocalDate> holidays = new ArrayList<>();
        for (int year = 1950; year <= 2099; year++) {
            // nyårsdagen
            holidays.add(date(year, 1, 1));
            // trettondagen
            holidays.add(date(year, 1, 6));
            // långfredag
            holidays.add(easter(year).goodFriday());
            // annandag påsk
            holidays.add(easter(year).easterMonday());
            // första maj
            holidays.add(date(year, 5, 1));
            // kristihimmelsfärdsdagen
            holidays.add(easter(year).easterSunday().plusDays(39));
            // midsommarafton
            holidays.add(date(year, 6, 19).with(nextOrSame(FRIDAY)));
            // nationaldagen
            if (year > 2005) {
                holidays.add(date(year, 6, 6));
            }
            // julafton
            holidays.add(date(year, 12, 24));
            // juldagen
            holidays.add(date(year, 12, 25));
            // annandag jul
            holidays.add(date(year, 12, 26));
            // nyårsafton
            holidays.add(date(year, 12, 31));
        }

        removeSatSun(holidays);

        return ImmutableHolidayCalendar.of(holidays, SATURDAY, SUNDAY);
    }

    private static LocalDate date(int year, int month, int day) {
        return LocalDate.of(year, month, day);
    }

    private static EasterCalculator easter(int year) {
        return EasterCalculator.of(year);
    }

    // remove any holidays covered by Sat/Sun
    private static void removeSatSun(List<LocalDate> holidays) {
        holidays.removeIf(date -> date.getDayOfWeek() == SATURDAY || date.getDayOfWeek() == SUNDAY);
    }

}
