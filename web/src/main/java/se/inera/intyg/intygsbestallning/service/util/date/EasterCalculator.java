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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

//CHECKSTYLE:OFF MagicNumber
public final class EasterCalculator {

    private LocalDate easter;

    private EasterCalculator() {
    }

    /**
     * Method responsible for creating an instance of this class.
     *
     * @param year
     *            A valid Gregorian year
     * @return
     *         A Date
     */
    public static EasterCalculator of(int year) {
        return init(year);
    }

    private static EasterCalculator init(int year) {
        EasterCalculator easterCalculator = new EasterCalculator();
        easterCalculator.easter = calculateEaster(year);
        return easterCalculator;
    }

    /**
     * Method returns all easter dates starting and ending
     * with Good Friday and Easter Monday respectively.
     *
     * @return
     *         A list of LocalDate
     */
    public List<LocalDate> easterDates() {
        return Arrays.asList(
          goodFriday(),
          easterEve(),
          easterSunday(),
          easterMonday()
        );
    }

    /**
     * Method returns this instance's Good Friday date.
     *
     * @return
     *         A LocalDate
     */
    public LocalDate goodFriday() {
        return easter.minusDays(2);
    }

    /**
     * Method returns this instance's Easter Eve date.
     *
     * @return
     *         A LocalDate
     */
    public LocalDate easterEve() {
        return easter.minusDays(1);
    }

    /**
     * Method returns this instance's Easter Sunday date.
     *
     * @return
     *         A LocalDate
     */
    public LocalDate easterSunday() {
        return easter;
    }

    /**
     * Method returns this instance's Easter Monday date.
     *
     * @return
     *         A LocalDate
     */
    public LocalDate easterMonday() {
        return easter.plusDays(1);
    }

    /**
     * Algorithm for calculating the date of Easter Sunday, see
     * http://en.wikipedia.org/wiki/Computus#Meeus.2FJones.2FButcher_Gregorian_algorithm.
     *
     * @param year
     *            A valid Gregorian year
     * @return
     *         A LocalDate
     */
    private static LocalDate calculateEaster(int year) {
        int a = year % 19;
        int b = year / 100;
        int c = year % 100;
        int d = b / 4;
        int e = b % 4;
        int f = (b + 8) / 25;
        int g = (b - f + 1) / 3;
        int h = (19 * a + b - d - g + 15) % 30;
        int i = c / 4;
        int k = c % 4;
        int l = (32 + 2 * e + 2 * i - h - k) % 7;
        int m = (a + 11 * h + 22 * l) / 451;
        int month = (h + l - 7 * m + 114) / 31;
        int day = ((h + l - 7 * m + 114) % 31) + 1;

        return LocalDate.of(year, month, day);
    }

}
//CHECKSTYLE:ON MagicNumber
