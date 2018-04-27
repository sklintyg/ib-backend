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

import java.time.LocalDate;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author Magnus Ekstrand on 2018-04-26.
 */
public class LocalDateUtils {

    /**
     * Streams the set of dates included in the range.
     * <p>
     * This returns a stream consisting of each date in the range.
     * The stream is ordered.
     *
     * @param startInclusive  the start date
     * @param endExclusive  the end date
     * @return the stream of dates from the start to the end
     */
    public static Stream<LocalDate> stream(LocalDate startInclusive, LocalDate endExclusive) {
        Iterator<LocalDate> it = new Iterator<LocalDate>() {
            private LocalDate current = startInclusive;

            @Override
            public LocalDate next() {
                LocalDate result = current;
                current = current.plusDays(1);
                return result;
            }

            @Override
            public boolean hasNext() {
                return current.isBefore(endExclusive);
            }
        };

        long count = endExclusive.toEpochDay() - startInclusive.toEpochDay() + 1;
        Spliterator<LocalDate> spliterator = Spliterators.spliterator(it, count,
                Spliterator.IMMUTABLE | Spliterator.NONNULL |
                        Spliterator.DISTINCT | Spliterator.ORDERED | Spliterator.SORTED |
                        Spliterator.SIZED | Spliterator.SUBSIZED);

        return StreamSupport.stream(spliterator, false);
    }

}
