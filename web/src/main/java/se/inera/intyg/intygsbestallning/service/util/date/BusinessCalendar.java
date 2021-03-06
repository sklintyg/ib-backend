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

import se.inera.intyg.intygsbestallning.common.util.LocalDateUtil;

import java.time.LocalDate;

/**
 * @author Magnus Ekstrand on 2018-04-26.
 */
public interface BusinessCalendar {

    /**
     * Checks if the specified date is a business day.
     * <p>
     * A weekend is treated as a holiday.
     *
     * @param date  the date to check
     * @return true if the specified date is a business day
     * @throws IllegalArgumentException if the date is outside the supported range
     */
    boolean isBusinessDay(LocalDate date);

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
    default int daysBetween(LocalDate startInclusive, LocalDate endExclusive) {
        return Math.toIntExact(LocalDateUtil.stream(startInclusive, endExclusive)
                .filter(this::isBusinessDay)
                .count());
    }

}
