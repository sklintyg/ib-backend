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
package se.inera.intyg.intygsbestallning.common.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class SchemaDateUtil {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private SchemaDateUtil() {
    }

    public static LocalDateTime toLocalDateTimeFromDateTimeStamp(final String dateTimeStamp) {
        return LocalDateTime.parse(dateTimeStamp, DATE_TIME_FORMATTER);
    }

    public static LocalDateTime toLocalDateTimeFromDateType(final String dateType) {
        return LocalDate.parse(dateType, DATE_FORMATTER).atStartOfDay();
    }

    public static LocalDate toLocalDateFromDateType(final String dateType) {
        return toLocalDateTimeFromDateType(dateType).toLocalDate();
    }

    public static String toDateStringFromLocalDateTime(final LocalDateTime localDateTime) {
        return DATE_FORMATTER.format(localDateTime);
    }

    public static String toDateStringFromLocalDate(final LocalDate localDate) {
        return DATE_FORMATTER.format(localDate);
    }

    public static String toDateTimeStringFromLocalDateTime(final LocalDateTime localDateTime) {
        return DATE_TIME_FORMATTER.format(localDateTime);
    }

    public static String toDateTimeStringFromLocalDate(final LocalDate localDate) {
        return DATE_TIME_FORMATTER.format(localDate);
    }

    public static String toIsoDateStringFromLocalDate(final LocalDate localDate) {
        return DateTimeFormatter.ISO_DATE.format(localDate);
    }

    public static String toIsoDateStringFromLocalDateTime(final LocalDateTime localDateTime) {
        return toIsoDateStringFromLocalDate(localDateTime.toLocalDate());
    }
}
