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

/**
 * @author Magnus Ekstrand on 2018-04-26.
 */
public interface HolidayCalendar extends BusinessCalendar {

    boolean isHoliday(LocalDate localDate);

    /**
     * Checks if the specified date is a business day.
     * <p>
     * This is the opposite of {@link #isHoliday(LocalDate)}.
     * A weekend is treated as a holiday.
     *
     * @param date  the date to check
     * @return true if the specified date is a business day
     * @throws IllegalArgumentException if the date is outside the supported range
     */
    @Override
    default boolean isBusinessDay(LocalDate date) {
        return !isHoliday(date);
    }

}
