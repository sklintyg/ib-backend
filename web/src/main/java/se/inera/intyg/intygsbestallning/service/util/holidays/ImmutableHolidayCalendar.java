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

/**
 * @author Magnus Ekstrand on 2018-04-26.
 */

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Sets;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;
import java.util.SortedSet;

/**
 * A holiday calendar implementation based on an immutable set of holiday dates and weekends.
 */
public final class ImmutableHolidayCalendar implements HolidayCalendar, Serializable {

    private static final long serialVersionUID = 1L;

    // optimized implementation of HolidayCalendar
    // uses an int array where each int represents a month
    // each bit within the int represents a date, where 0 is a holiday and 1 is a business day
    // (most logic involves finding business days, finding 1 is easier than finding 0
    // when using Integer.numberOfTrailingZeros and Integer.numberOfLeadingZeros)
    // benchmarking showed nextOrSame() and previousOrSame() do not need to be overridden
    // out-of-range and weekend-only (used in testing) are handled using exceptions to fast-path the common case

    /**
     * The set of holiday dates.
     */
    private final ImmutableSortedSet<LocalDate> holidays;

    /**
     * The set of weekend days.
     * Each date that has a day-of-week matching one of these days is not a business day.
     */
    private final ImmutableSet<DayOfWeek> weekendDays;

    /**
     * The start year.
     * Used as the base year for the lookup table.
     */
    private final transient int startYear;  // not a property

    /**
     * The lookup table, where each item represents a month from January of startYear onwards.
     * Bits 0 to 31 are used for each day-of-month, where 0 is a holiday and 1 is a business day.
     * Trailing bits are set to 0 so they act as holidays, avoiding month length logic.
     */
    private final transient int[] lookup;  // not a property

    /**
     * Obtains an instance from a set of holiday dates and weekend days.
     * <p>
     * The holiday dates will be extracted into a set with duplicates ignored.
     * The minimum supported date for query is the start of the year of the earliest holiday.
     * The maximum supported date for query is the end of the year of the latest holiday.
     * <p>
     * The weekend days may both be the same.
     *
     * @param holidays  the set of holiday dates
     * @param firstWeekendDay  the first weekend day
     * @param secondWeekendDay  the second weekend day, may be same as first
     * @return the holiday calendar
     */
    public static ImmutableHolidayCalendar of(
            Iterable<LocalDate> holidays, DayOfWeek firstWeekendDay, DayOfWeek secondWeekendDay) {
        ImmutableSet<DayOfWeek> weekendDays = Sets.immutableEnumSet(firstWeekendDay, secondWeekendDay);
        return new ImmutableHolidayCalendar(ImmutableSortedSet.copyOf(holidays), weekendDays);
    }

    /**
     * Obtains an instance from a set of holiday dates and weekend days.
     * <p>
     * The holiday dates will be extracted into a set with duplicates ignored.
     * The minimum supported date for query is the start of the year of the earliest holiday.
     * The maximum supported date for query is the end of the year of the latest holiday.
     * <p>
     * The weekend days may be empty, in which case the holiday dates should contain any weekends.
     *
     * @param holidays  the set of holiday dates
     * @param weekendDays  the days that define the weekend, if empty then weekends are treated as business days
     * @return the holiday calendar
     */
    public static ImmutableHolidayCalendar of(
            Iterable<LocalDate> holidays, Iterable<DayOfWeek> weekendDays) {
        return new ImmutableHolidayCalendar(ImmutableSortedSet.copyOf(holidays), Sets.immutableEnumSet(weekendDays));
    }

    /**
     * Creates an instance calculating the supported range.
     *
     * @param holidays  the set of holidays, validated non-null
     * @param weekendDays  the set of weekend days, validated non-null
     */
    private ImmutableHolidayCalendar(SortedSet<LocalDate> holidays, Set<DayOfWeek> weekendDays) {
        assert holidays != null;
        assert weekendDays != null;

        this.holidays = ImmutableSortedSet.copyOfSorted(holidays);
        this.weekendDays = Sets.immutableEnumSet(weekendDays);

        if (holidays.isEmpty()) {
            // special case where no holiday dates are specified
            this.startYear = 0;
            this.lookup = new int[0];
        } else {
            // normal case where holidays are specified
            this.startYear = holidays.first().getYear();
            int endYearExclusive = holidays.last().getYear() + 1;
            this.lookup = buildLookupArray(holidays, weekendDays, startYear, endYearExclusive);
        }
    }

    // of and populate the int[] lookup
    // use 1 for business days and 0 for holidays
    private static int[] buildLookupArray(
            SortedSet<LocalDate> holidays,
            Set<DayOfWeek> weekendDays,
            int startYear,
            int endYearExclusive) {

        // array that has one entry for each month
        int[] array = new int[(endYearExclusive - startYear) * 12];

        // loop through all months to handle end-of-month and weekends
        LocalDate firstOfMonth = LocalDate.of(startYear, 1, 1);
        for (int i = 0; i < array.length; i++) {
            int monthLen = firstOfMonth.lengthOfMonth();
            // set each valid day-of-month to be a business day
            // the bits for days beyond the end-of-month will be unset and thus treated as non-business days
            // the minus one part converts a single set bit into each lower bit being set
            array[i] = (1 << monthLen) - 1;
            // unset the bits associated with a weekend
            // can unset across whole month using repeating pattern of 7 bits
            // just need to find the offset between the weekend and the day-of-week of the 1st of the month
            for (DayOfWeek weekendDow : weekendDays) {
                int daysDiff = weekendDow.getValue() - firstOfMonth.getDayOfWeek().getValue();
                int offset = (daysDiff < 0 ? daysDiff + 7 : daysDiff);
                array[i] &= ~(0b10000001000000100000010000001 << offset);
            }
            firstOfMonth = firstOfMonth.plusMonths(1);
        }
        // unset the bit associated with each holiday date
        for (LocalDate date : holidays) {
            int index = (date.getYear() - startYear) * 12 + date.getMonthValue() - 1;
            array[index] &= ~(1 << (date.getDayOfMonth() - 1));
        }
        return array;
    }

    @Override
    public boolean isHoliday(LocalDate date) {
        try {
            // find data for month
            int index = (date.getYear() - startYear) * 12 + date.getMonthValue() - 1;
            // check if bit is 1 at zero-based day-of-month
            return (lookup[index] & (1 << (date.getDayOfMonth() - 1))) == 0;

        } catch (ArrayIndexOutOfBoundsException ex) {
            return isHolidayOutOfRange(date);
        }
    }

    private boolean isHolidayOutOfRange(LocalDate date) {
        if (date.getYear() >= 0 && date.getYear() < 10000) {
            return weekendDays.contains(date.getDayOfWeek());
        }
        throw new IllegalArgumentException("Date is outside the accepted range (year 0000 to 10,000): " + date);
    }

    /**
     * Gets the set of holiday dates.
     * <p>
     * Each date in this set is not a business day.
     * @return the value of the property, not null
     */
    public ImmutableSortedSet<LocalDate> getHolidays() {
        return holidays;
    }

    /**
     * Gets the set of weekend days.
     * <p>
     * Each date that has a day-of-week matching one of these days is not a business day.
     * @return the value of the property, not null
     */
    public ImmutableSet<DayOfWeek> getWeekendDays() {
        return weekendDays;
    }

}