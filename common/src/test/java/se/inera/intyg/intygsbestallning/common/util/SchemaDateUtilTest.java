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

import static org.assertj.core.api.Assertions.assertThat;
import static se.inera.intyg.intygsbestallning.common.util.SchemaDateUtil.toDateTimeStringFromLocalDateTime;
import static se.inera.intyg.intygsbestallning.common.util.SchemaDateUtil.toLocalDateTimeFromDateType;

import org.junit.Test;
import java.time.LocalDateTime;

public class SchemaDateUtilTest {

    @Test
    public void testToLocalDateTimeFromDateType() {
        final String dateType = "20181212";
        final LocalDateTime expected = LocalDateTime.of(2018, 12, 12, 0, 0, 0);

        assertThat(toLocalDateTimeFromDateType(dateType)).isEqualTo(expected);
    }

    @Test
    public void testToDateTimeStringFromLocalDateTime() {
        final String expected = "20181212121212";
        final LocalDateTime dateTime = LocalDateTime.of(2018, 12, 12, 12, 12, 12);

        assertThat(toDateTimeStringFromLocalDateTime(dateTime)).isEqualTo(expected);
    }
}