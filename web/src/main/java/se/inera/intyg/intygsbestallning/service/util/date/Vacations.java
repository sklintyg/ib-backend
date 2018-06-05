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

import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static se.inera.intyg.intygsbestallning.common.util.SchemaDateUtil.toLocalDateFromDateType;

import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import se.inera.intyg.intygsbestallning.common.util.LocalDateUtil;

/**
 * @author Magnus Ekstrand on 2018-04-26.
 */
// CHECKSTYLE:OFF MagicNumber
public final class Vacations {

    private static final Logger LOG = LoggerFactory.getLogger(Vacations.class);

    // Hide constructor
    private Vacations() {
    }

    /**
     * @param vacationPeriods
     */
    public static ImmutableVacationCalendar of(String vacationPeriods) {
        List<LocalDate> vacations = new ArrayList<>();

        if (!isBlank(vacationPeriods)) {
            // remove all whitespaces
            String str = vacationPeriods.replaceAll("\\s", "");

            // call parser to get all dates in period
            vacationParser(str, vacations);
        }

        return of(vacations);
    }

    public static ImmutableVacationCalendar of(List<LocalDate> vacationDates) {
        List<LocalDate> vacations = new ArrayList<>();
        if (vacationDates != null) {
            vacations = vacationDates.stream().distinct().sorted().collect(Collectors.toList());
        }

        // remove Saturdays and Sundays from list
        removeSatSun(vacations);

        return ImmutableVacationCalendar.of(vacations, SATURDAY, SUNDAY);
    }


    // default scope

    static void vacationParser(final String vacationPeriod, final List<LocalDate> output) {
        if (isBlank(vacationPeriod)) {
            return;
        }

        if (vacationPeriod.contains(",")) {
            vacationParser(vacationPeriod.split(","), output);
        } else if (vacationPeriod.contains("-")) {
            rangeParser(vacationPeriod, output);
        } else {
            valueParser(vacationPeriod, output);
        }
    }

    private static void vacationParser(final String[] vacationPeriods, final List<LocalDate> output) {
        Arrays.stream(vacationPeriods).forEach(s -> vacationParser(s, output));
    }

    /*
     * Parses a range of dates or weeks. Format of range parameter
     * should be <start date>-<end date> or <start week>-<end week>
     */
    static void rangeParser(final String range, final List<LocalDate> output) {
        List<String> ranges = Splitter.on('-').splitToList(range);
        if (ranges.size() == 1) {
            valueParser(ranges.get(0), output);
        } else if (ranges.size() > 2) {
            // What to do?
            LOG.debug("Cannot parse range. Perhaps invalid date format? Correct date format is yyyyMMdd: " + range);
        }

        String lowRange = ranges.get(0);
        String highRange = ranges.get(1);

        rangeParser(lowRange, highRange, output);
    }

    static void rangeParser(final String lowRange, final String highRange, final List<LocalDate> output) {
        // Check that ranges are of the same type and lowRange < highRange

        if (isDate(lowRange) && isDate(highRange)) {
            LocalDate low = toLocalDateFromDateType(lowRange);
            LocalDate high = toLocalDateFromDateType(highRange);
            if (high.isBefore(low)) {
                LocalDate tmp = low;
                low = high;
                high = tmp;
            }

            List<LocalDate> dates = LocalDateUtil.stream(low, high.plusDays(1)).collect(Collectors.toList());
            output.addAll(dates);

        } else if (isWeek(lowRange) && isWeek(highRange)) {
            int low = Integer.parseInt(lowRange);
            int high = Integer.parseInt(highRange);
            if (high < low) {
                int tmp = low;
                low = high;
                high = tmp;
            }

            LocalDate min = getMinDate(week(low));
            LocalDate max = getMaxDate(week(high));
            List<LocalDate> dates = LocalDateUtil.stream(min, max.plusDays(1)).collect(Collectors.toList());
            output.addAll(dates);

        } else {
            LOG.debug("Cannot parse vacation range. It's neither week nor date: " + lowRange + "-" + highRange);
        }
    }

    static void valueParser(final String value, final List<LocalDate> output) {
        if (isBlank(value)) {
            return;
        }

        if (isWeek(value)) {
            output.addAll(week(Integer.parseInt(value)));
        } else if (isDate(value)) {
            output.add(toLocalDateFromDateType(value));
        } else {
            LOG.debug("Cannot parse vacation value. It's neither week nor date: " + value);
        }
    }

    static List<LocalDate> week(int weekNumber) {
        return week(LocalDate.now().getYear(), weekNumber);
    }

    static List<LocalDate> week(int year, int weekNumber) {
        int month = 1;
        int dayOfMonth = 1;

        LocalDate week = LocalDate.of(year, month, dayOfMonth).with(ChronoField.ALIGNED_WEEK_OF_YEAR, weekNumber);

        LocalDate start = week.with(MONDAY);
        LocalDate end = start.plusDays(6);

        return LocalDateUtil.stream(start, end.plusDays(1)).collect(Collectors.toList());
    }


    // private scope

    private static LocalDate getMaxDate(List<LocalDate> dates) {
        return dates.stream().max(LocalDate::compareTo).orElse(null);
    }

    private static LocalDate getMinDate(List<LocalDate> dates) {
        return dates.stream().min(LocalDate::compareTo).orElse(null);
    }

    private static boolean isBlank(String str) {
        return StringUtils.isBlank(str);
    }

    private static boolean isDate(String str) {
        try {
            toLocalDateFromDateType(str);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private static boolean isInt(String str) {
        return StringUtils.isNumeric(str);
    }

    /*
     * Control if a string is a valid week number
     */
    private static boolean isWeek(String str) {
        if (isInt(str)) {
            int value = Integer.parseInt(str);
            if (value > 0 && value < 54) {
                return true;
            }
        }

        return false;
    }

    /*
     * Remove any vacations covered by Sat/Sun
     */
    private static void removeSatSun(List<LocalDate> vacations) {
        vacations.removeIf(date -> date.getDayOfWeek() == SATURDAY || date.getDayOfWeek() == SUNDAY);
    }

}
// CHECKSTYLE:ON MagicNumber
