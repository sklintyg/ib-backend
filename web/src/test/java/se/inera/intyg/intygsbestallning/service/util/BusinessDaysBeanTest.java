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

package se.inera.intyg.intygsbestallning.service.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import se.inera.intyg.intygsbestallning.common.util.LocalDateUtil;
import se.inera.intyg.intygsbestallning.service.util.date.EasterCalculator;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.DayOfWeek.MONDAY;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author Magnus Ekstrand on 2018-05-09.
 */
@RunWith(MockitoJUnitRunner.class)
public class BusinessDaysBeanTest {

    private static final String VACATIONPERIODS = "29-31,51-52";

    private BusinessDaysBean testee;

    private EasterCalculator easter = EasterCalculator.of(2018);

    @Before
    public void setUp() {
        testee = new BusinessDaysBean(VACATIONPERIODS);
        testee.init();
    }

    @Test
    public void isBusinessDay() {
        // Verify behaviour using easter
        for (LocalDate date : easter.easterDates()) {
            assertFalse(testee.isBusinessDay(date));
        }

        assertTrue(testee.isBusinessDay(easter.goodFriday().minusDays(1)));
        assertTrue(testee.isBusinessDay(easter.easterMonday().plusDays(1)));

        // Verify behaviour using vacation
        List<LocalDate> dates = week(2018, 29);
        dates.addAll(week(2018, 30));
        dates.addAll(week(2018, 31));

        for (LocalDate date : dates) {
            assertFalse(testee.isBusinessDay(date));
        }

        assertTrue(testee.isBusinessDay(getMinDate(dates).minusDays(3)));
        assertTrue(testee.isBusinessDay(getMaxDate(dates).plusDays(1)));
    }

    @Test
    public void daysBetween() {
        LocalDate wednesdayBeforeGoodFriday = easter.goodFriday().minusDays(2);
        LocalDate fridayAfterEasterMonday = easter.easterMonday().plusDays(4);
        // Business days are: wednesday -> thursday -> tuesday -> wednesday -> thursday -> friday
        assertEquals(5, testee.daysBetween(wednesdayBeforeGoodFriday, fridayAfterEasterMonday));
    }

    @Test
    public void daysBetweenGivesZeroDistanceFromHolidayToWorkday() {
        // Given
        LocalDate sunday = week(2018, 5).get(6);
        LocalDate followingBusinessMonday = sunday.plusDays(1);
        assertTrue(testee.isBusinessDay(followingBusinessMonday));

        // When
        int daysBetween = testee.daysBetween(sunday, followingBusinessMonday);

        // Then
        assertThat(daysBetween, is(0));
    }

    @Test
    public void addBusinessDaysWithNoIrregularHolidays() {
        LocalDate sunday = week(2018, 5).get(6);
        LocalDate monday = sunday.plusDays(1);

        assertEquals(monday.plusDays(7), testee.addBusinessDays(monday, 5));   // Next Monday
        assertEquals(monday.plusDays(4), testee.addBusinessDays(monday, 4));   // Next Friday
        assertEquals(monday.minusDays(4), testee.addBusinessDays(monday, -2)); // Thursday before
        assertEquals(sunday.plusDays(1), testee.addBusinessDays(sunday, 1));   // Next Monday
        assertEquals(sunday.minusDays(2), testee.addBusinessDays(sunday, -1)); // Friday before

    }

    private static LocalDate getMaxDate(List<LocalDate> dates) {
        return dates.stream().max(LocalDate::compareTo).orElse(null);
    }

    private static LocalDate getMinDate(List<LocalDate> dates) {
        return dates.stream().min(LocalDate::compareTo).orElse(null);
    }

    private static List<LocalDate> week(int year, int weekNumber) {
        int month = 1;
        int dayOfMonth = 1;

        LocalDate week = LocalDate.of(year, month, dayOfMonth).with(ChronoField.ALIGNED_WEEK_OF_YEAR, weekNumber);

        LocalDate start = week.with(MONDAY);
        LocalDate end = start.plusDays(6);

        return LocalDateUtil.stream(start, end.plusDays(1)).collect(Collectors.toList());
    }

    @Configuration
    static class Config {
        @Bean
        public static PropertySourcesPlaceholderConfigurer propertiesResolver() {
            return new PropertySourcesPlaceholderConfigurer();
        }
    }

}