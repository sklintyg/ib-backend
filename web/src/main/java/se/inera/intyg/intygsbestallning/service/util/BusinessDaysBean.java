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
 * @author Magnus Ekstrand on 2018-05-04.
 */
@Component
public class BusinessDaysBean {

    @Value("${vacation.periods}")
    private String vacationPeriods;

    private BusinessCalendar holidays;
    private BusinessCalendar vacations;

    @PostConstruct
    public void init() {
        // init code goes here
        holidays = Holidays.SWE;
        vacations = Vacations.of(this.vacationPeriods);
    }

    public void init(String vacationPeriods) {
        this.vacationPeriods = vacationPeriods;
        this.init();
    }

    /**
     * Checks whether the specified date is a working day by
     * considering both holidays and vacation periods.
     * <p>
     * A weekend is treated as a holiday.
     *
     * @param date  the date to check
     * @return true if the specified date is a business day
     * @throws IllegalArgumentException if the date is outside the supported range
     */
    public boolean isBusinessDay(LocalDate date) {
        if (date.getDayOfWeek() == SATURDAY || date.getDayOfWeek() == SUNDAY) {
            return false;
        }
        return isBusinessDay(date, true);
    }

    /**
     * Checks whether the specified date is a working day by
     * considering holidays and, optionally, vacation periods.
     * <p>
     * A weekend is treated as a holiday.
     *
     * @param date  the date to check
     * @param accountForVacationPeriods  take into account the vacation periods
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
     * Calculates the number of business days between two dates.
     * <p>
     * This calculates the number of business days within the range.
     * If the dates are equal, zero is returned.
     * If the end is before the start, an exception is thrown.
     *
     * @param startInclusive  the start date
     * @param endExclusive  the end date
     * @return the total number of business days between the start and end date
     * @throws IllegalArgumentException if the calculation is outside the supported range
     */
    public int daysBetween(LocalDate startInclusive, LocalDate endExclusive) {
        return Math.toIntExact(LocalDateUtil.stream(startInclusive, endExclusive)
                .filter(this::isBusinessDay)
                .count());
    }

}
