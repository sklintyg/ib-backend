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
 * A vacation calendar implementation based on an immutable set of vacation dates and weekends.
 */
// CHECKSTYLE:OFF MagicNumber
public final class ImmutableVacationCalendar implements VacationCalendar, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The set of vacation dates.
     */
    private final ImmutableSortedSet<LocalDate> vacations;

    /**
     * The set of weekend days.
     * Each date that has a day-of-week matching one of these days is not a business day.
     */
    private final ImmutableSet<DayOfWeek> weekendDays;

    /**
     * Obtains an instance from a set of vacation dates and weekend days.
     * <p>
     * The vacation dates will be extracted into a set with duplicates ignored.
     * The minimum supported date for query is the start of the year of the earliest vacation.
     * The maximum supported date for query is the end of the year of the latest vacation.
     * <p>
     * The weekend days may both be the same.
     *
     * @param vacations  the set of vacation dates
     * @param firstWeekendDay  the first weekend day
     * @param secondWeekendDay  the second weekend day, may be same as first
     * @return the vacation calendar
     */
    public static ImmutableVacationCalendar of(
            Iterable<LocalDate> vacations, DayOfWeek firstWeekendDay, DayOfWeek secondWeekendDay) {
        ImmutableSet<DayOfWeek> weekendDays = Sets.immutableEnumSet(firstWeekendDay, secondWeekendDay);
        return new ImmutableVacationCalendar(ImmutableSortedSet.copyOf(vacations), weekendDays);
    }

    /**
     * Obtains an instance from a set of vacation dates and weekend days.
     * <p>
     * The vacation dates will be extracted into a set with duplicates ignored.
     * The minimum supported date for query is the start of the year of the earliest vacation.
     * The maximum supported date for query is the end of the year of the latest vacation.
     * <p>
     * The weekend days may be empty, in which case the vacation dates should contain any weekends.
     *
     * @param vacations  the set of vacation dates
     * @param weekendDays  the days that define the weekend, if empty then weekends are treated as business days
     * @return the vacation calendar
     */
    public static ImmutableVacationCalendar of(
            Iterable<LocalDate> vacations, Iterable<DayOfWeek> weekendDays) {
        return new ImmutableVacationCalendar(ImmutableSortedSet.copyOf(vacations), Sets.immutableEnumSet(weekendDays));
    }

    /**
     * Creates an instance calculating the supported range.
     *
     * @param vacations  the set of vacations, validated non-null
     * @param weekendDays  the set of weekend days, validated non-null
     */
    private ImmutableVacationCalendar(SortedSet<LocalDate> vacations, Set<DayOfWeek> weekendDays) {
        assert vacations != null;
        assert weekendDays != null;

        this.vacations = ImmutableSortedSet.copyOfSorted(vacations);
        this.weekendDays = Sets.immutableEnumSet(weekendDays);
    }

    @Override
    public boolean isVacation(LocalDate date) {
        return vacations.contains(date);
    }

    /**
     * Gets the set of vacation dates.
     * <p>
     * Each date in this set is not a business day.
     * @return the value of the property, not null
     */
    public ImmutableSortedSet<LocalDate> getVacations() {
        return vacations;
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
// CHECKSTYLE:ON MagicNumber
