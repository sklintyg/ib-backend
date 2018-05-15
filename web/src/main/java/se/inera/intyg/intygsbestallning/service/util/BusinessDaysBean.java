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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import se.inera.intyg.intygsbestallning.common.util.LocalDateUtil;
import se.inera.intyg.intygsbestallning.service.util.date.BusinessCalendar;
import se.inera.intyg.intygsbestallning.service.util.date.Holidays;
import se.inera.intyg.intygsbestallning.service.util.date.Vacations;

import javax.annotation.PostConstruct;
import java.time.LocalDate;

import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;

/**
 * A bean implementing logic to calculate whether a specified date
 * is a working day or not by considering both holidays and vacation periods.
 *
 * @author Magnus Ekstrand on 2018-05-04.
 */
@Component
public class BusinessDaysBean {

    private String vacationPeriods;

    private BusinessCalendar holidays;
    private BusinessCalendar vacations;

    /**
     * Constructor that take an argument which sets the
     * current year's vacation periods. If argument is set to
     * to null or empty string, there will be no vacation periods.
     * <p>
     * The vacationPeriods parameter can handle weeks, week ranges,
     * dates and date ranges. It's possible to mix weeks, dates and ranges.
     * <p>
     * A week is a number between 1 and 53.<br>
     * A date shall be on the format <b>yyyyMMdd</b>.<br>
     * <p>
     * Examples:
     * <ul>
     *     <li>29-30,51-52</li>
     *     <li>29,30,31,51,52</li>
     *     <li>20180716-20180805,20181217-20181230</li>
     *     <li>29-31,20181001,51,20181224-20181230</li>
     * </ul>
     *
     * When using ranges, the start and end values must be of the same type.
     * You cannot use a week as start value and a date as an end value.
     *
     * @param vacationPeriods  the current year's vacation period, can be null or empty string
     */
    public BusinessDaysBean(@Value("${vacation.periods}") String vacationPeriods) {
        this.vacationPeriods = vacationPeriods;
    }

    @PostConstruct
    public void init() {
        holidays = Holidays.SWE;
        vacations = Vacations.of(this.vacationPeriods);
    }

    /**
     * Method checks whether the specified date is a working day by
     * considering both holidays and vacation periods.
     * <p>
     * A weekend is treated as a holiday.
     *
     * @param date  the date to check
     * @return true if the specified date is a business day
     * @throws IllegalArgumentException if the date is outside the supported range
     */
    public boolean isBusinessDay(LocalDate date) {
        return isBusinessDay(date, true);
    }

    /**
     * Method checks whether the specified date is a working day by
     * considering holidays and, optionally, vacation periods.
     * Saturdays and Sundays are considered as holidays.
     * <p>
     * A weekend is treated as a holiday.
     *
     * @param date  the date to check
     * @param accountForVacationPeriods  takes into account the vacation periods
     * @return true if the specified date is a business day
     * @throws IllegalArgumentException if the date is outside the supported range
     */
    public boolean isBusinessDay(LocalDate date, boolean accountForVacationPeriods) {
        if (date.getDayOfWeek() == SATURDAY || date.getDayOfWeek() == SUNDAY) {
            return false;
        }
        if (accountForVacationPeriods) {
            return holidays.isBusinessDay(date) && vacations.isBusinessDay(date);
        }
        return holidays.isBusinessDay(date);
    }

    /**
     * Method calculates the number of business days between two dates.
     * <p>
     * It calculates the number of business days within the range.
     * If the dates are equal, zero is returned.
     * If the end is before the start, an exception is thrown.
     *
     * @param startInclusive  the start date
     * @param endExclusive  the end date
     * @return the total number of business days between the start and end date
     * @throws IllegalArgumentException if the calculation is outside the supported range
     */
    public int daysBetween(LocalDate startInclusive, LocalDate endExclusive) {
        return daysBetween(startInclusive, endExclusive, true);
    }

    /**
     * Calculates the number of business days between two dates with an
     * option of taking into account the vacation periods.
     * <p>
     * This calculates the number of business days within the range.
     * If the dates are equal, zero is returned.
     * If the end is before the start, an exception is thrown.
     *
     * @param startInclusive  the start date
     * @param endExclusive  the end date
     * @param accountForVacationPeriods  takes into account the vacation periods
     * @return the total number of business days between the start and end date
     * @throws IllegalArgumentException if the calculation is outside the supported range
     */
    public int daysBetween(LocalDate startInclusive, LocalDate endExclusive, boolean accountForVacationPeriods) {
        return Math.toIntExact(LocalDateUtil.stream(startInclusive, endExclusive)
                .filter(o -> isBusinessDay(o, accountForVacationPeriods))
                .count());
    }

}
