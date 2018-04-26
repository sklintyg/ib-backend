package se.inera.intyg.intygsbestallning.service.util;

import java.time.LocalDate;

public class EasterCalculator {

    /**
     * Algorithm for calculating the date of Easter Sunday, see
     * http://en.wikipedia.org/wiki/Computus#Meeus.2FJones.2FButcher_Gregorian_algorithm
     *
     *
     * @param year
     *            A valid Gregorian year
     * @return
     *         A Date
     */
    public static LocalDate easterDate(int year) {
        int Y = year;
        int a = Y % 19;
        int b = Y / 100;
        int c = Y % 100;
        int d = b / 4;
        int e = b % 4;
        int f = (b + 8) / 25;
        int g = (b - f + 1) / 3;
        int h = (19 * a + b - d - g + 15) % 30;
        int i = c / 4;
        int k = c % 4;
        int L = (32 + 2 * e + 2 * i - h - k) % 7;
        int m = (a + 11 * h + 22 * L) / 451;
        int month = (h + L - 7 * m + 114) / 31;
        int day = ((h + L - 7 * m + 114) % 31) + 1;
        return LocalDate.of(year, month, day);
    }
}
