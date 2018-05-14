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
import se.inera.intyg.intygsbestallning.common.util.LocalDateUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static org.junit.Assert.assertEquals;

/**
 * @author Magnus Ekstrand on 2018-05-07.
 */
public class VacationsTest {

    @Test
    public void testWeekNumber() {
        List<LocalDate> expected;
        List<LocalDate> actual;

        // Week 2018-19
        expected = LocalDateUtil.stream(date(2018, 5, 7), date(2018, 5, 14)).collect(Collectors.toList());
        actual = week(2018, 19);
        assertEquals(expected, actual);

        // Week 2020-01
        expected = LocalDateUtil.stream(date(2019, 12, 30), date(2020, 1, 6)).collect(Collectors.toList());
        actual = week(2020, 1);
        assertEquals(expected, actual);

        // Week 2020-53
        expected = LocalDateUtil.stream(date(2020, 12, 28), date(2021, 1, 4)).collect(Collectors.toList());
        actual = week(2020, 53);
        assertEquals(expected, actual);
    }

    @Test
    public void testRangeParserUsingWeeks() {
        List<LocalDate> expected = new ArrayList<>();
        List<LocalDate> actual = new ArrayList<>();

        // Week 2018-29 - 2018-31
        expected.addAll(week(29));
        expected.addAll(week(30));
        expected.addAll(week(31));

        // Check happy days
        Vacations.rangeParser("29-31", actual);
        assertEquals(expected, actual);

        // Check happy days reversed
        actual.clear();
        Vacations.rangeParser("31-29", actual);
        assertEquals(expected, actual);

        // Check when low and high are equal
        expected.clear();
        actual.clear();
        expected.addAll(week(29));
        Vacations.rangeParser("29-29", actual);
        assertEquals(expected, actual);
    }

    @Test
    public void testRangeParserUsingLowAndHighWeeks() {
        List<LocalDate> expected = new ArrayList<>();
        List<LocalDate> actual = new ArrayList<>();

        // Week 2018-29 - 2018-31
        expected.addAll(week(29));
        expected.addAll(week(30));
        expected.addAll(week(31));

        // Check happy days
        Vacations.rangeParser("29", "31", actual);
        assertEquals(expected, actual);

        // Check happy days reversed
        actual.clear();
        Vacations.rangeParser("31", "29", actual);
        assertEquals(expected, actual);

        // Check when low and high are equal
        expected.clear();
        actual.clear();
        expected.addAll(week(29));
        Vacations.rangeParser("29", "29", actual);
        assertEquals(expected, actual);
    }

    @Test
    public void testRangeParserUsingDates() {
        List<LocalDate> expected = new ArrayList<>();
        List<LocalDate> actual = new ArrayList<>();

        // 20180716-20180805
        LocalDate low = date(2018, 7, 16);
        LocalDate high = date(2018, 8, 5);

        expected.addAll(LocalDateUtil.stream(low, high.plusDays(1)).collect(Collectors.toList()));

        // Check happy days
        Vacations.rangeParser("20180716-20180805", actual);
        assertEquals(expected, actual);

        // Check happy days reversed
        actual.clear();
        Vacations.rangeParser("20180805-20180716", actual);
        assertEquals(expected, actual);

        // Check when low and high are equal
        expected.clear();
        actual.clear();
        expected.add(low);
        Vacations.rangeParser("20180716-20180716", actual);
        assertEquals(expected, actual);
    }

    @Test
    public void testRangeParserUsingLowAndHighDates() {
        List<LocalDate> expected = new ArrayList<>();
        List<LocalDate> actual = new ArrayList<>();

        // 20180716-20180805
        LocalDate low = date(2018, 7, 16);
        LocalDate high = date(2018, 8, 5);

        expected.addAll(LocalDateUtil.stream(low, high.plusDays(1)).collect(Collectors.toList()));

        // Check happy days
        Vacations.rangeParser("20180716", "20180805", actual);
        assertEquals(expected, actual);

        // Check happy days reversed
        actual.clear();
        Vacations.rangeParser("20180805", "20180716", actual);
        assertEquals(expected, actual);

        // Check when low and high are equal
        expected.clear();
        actual.clear();
        expected.add(low);
        Vacations.rangeParser("20180716", "20180716", actual);
        assertEquals(expected, actual);
    }


    @Test
    public void testVacationParserOnlyWeeks() {
        List<LocalDate> expected = new ArrayList<>();
        List<LocalDate> actual = new ArrayList<>();

        // Week 2018-29 - 2018-31 and 2018-51 - 2018-52
        expected.addAll(week(2018, 29));
        expected.addAll(week(2018, 30));
        expected.addAll(week(2018, 31));
        expected.addAll(week(2018, 40));
        expected.addAll(week(2018, 51));
        expected.addAll(week(2018, 52));

        Vacations.vacationParser("29-31,40,51-52", actual);
        assertEquals(expected, actual);
    }

    @Test
    public void testVacationParserOnlyDates() {
        List<LocalDate> expected = new ArrayList<>();
        List<LocalDate> actual = new ArrayList<>();

        // 20180716-20180805
        LocalDate low = date(2018, 7, 16);
        LocalDate high = date(2018, 8, 5);
        expected.addAll(LocalDateUtil.stream(low, high.plusDays(1)).collect(Collectors.toList()));

        // 20181001
        low = date(2018, 10, 1);
        expected.add(low);

        // 20181217-20181230
        low = date(2018, 12, 17);
        high = date(2018, 12, 30);
        expected.addAll(LocalDateUtil.stream(low, high.plusDays(1)).collect(Collectors.toList()));

        // Week 2018-29 - 2018-31 and 2018-51 - 2018-52
        Vacations.vacationParser("20180716-20180805,20181001,20181217-20181230", actual);
        assertEquals(expected, actual);
    }

    @Test
    public void testVacationParserMixingWeekAndDates() {
        List<LocalDate> expected = new ArrayList<>();
        List<LocalDate> actual = new ArrayList<>();

        // Week 2018-29 - 2018-31
        expected.addAll(week(2018, 29));
        expected.addAll(week(2018, 30));
        expected.addAll(week(2018, 31));

        // 20181001
        LocalDate low = date(2018, 10, 1);
        expected.add(low);

        // Week 2018-51
        expected.addAll(week(2018, 51));

        // 20181224-20181230
        low = date(2018, 12, 24);
        LocalDate high = date(2018, 12, 30);
        expected.addAll(LocalDateUtil.stream(low, high.plusDays(1)).collect(Collectors.toList()));

        // Do call and assert
        Vacations.vacationParser("29-31,20181001,51,20181224-20181230", actual);
        assertEquals(expected, actual);
    }

    @Test
    public void testSortOrder() {
        List<LocalDate> expected = new ArrayList<>();
        List<LocalDate> actual = new ArrayList<>();

        // Week 2018-29 - 2018-31
        expected.addAll(week(2018, 29));
        expected.addAll(week(2018, 30));
        expected.addAll(week(2018, 31));

        // 20181001
        LocalDate low = date(2018, 10, 1);
        expected.add(low);

        // Week 2018-51
        expected.addAll(week(2018, 51));

        // 20181224-20181230
        low = date(2018, 12, 24);
        LocalDate high = date(2018, 12, 30);
        expected.addAll(LocalDateUtil.stream(low, high.plusDays(1)).collect(Collectors.toList()));

        // Need to remove Saturday and Sundays
        expected.removeIf(date -> date.getDayOfWeek() == SATURDAY || date.getDayOfWeek() == SUNDAY);

        // Do call and assert
        actual = new ArrayList<>(Vacations.of("51,20181001,31,20181224-20181230,29-30").getVacations());
        assertEquals(expected, actual);

    }

    private static LocalDate date(int year, int month, int day) {
        return LocalDate.of(year, month, day);
    }

    private static List<LocalDate> week(int weekNumber) {
        return Vacations.week(LocalDate.now().getYear(), weekNumber);
    }

    private static List<LocalDate> week(int year, int weekNumber) {
        return Vacations.week(year, weekNumber);
    }
}