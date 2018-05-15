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

import se.inera.intyg.intygsbestallning.service.util.date.Holidays;

import java.time.LocalDate;

import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;

/**
 * @author Magnus Ekstrand on 2018-05-14.
 */
public class BusinessDaysStub extends BusinessDaysBean {

    public BusinessDaysStub() {
        // Do not set any vacation period for current testing.
        // Might change in a future near you.
        super("");
    }

    @Override
    public boolean isBusinessDay(LocalDate date) {
        return isBusinessDay(date, false);
    }

    @Override
    public boolean isBusinessDay(LocalDate date, boolean accountForVacationPeriods) {
        // In this stub we only care about Swedish holidays, not the vacation period
        if (date.getDayOfWeek() == SATURDAY || date.getDayOfWeek() == SUNDAY) {
            return false;
        }
        return Holidays.SWE.isBusinessDay(date);
    }


}
