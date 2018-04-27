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

import org.springframework.data.util.Pair;

/**
 * Returns a Pair of lower and upper index bounds given some paging parameters.
 *
 * @author eriklupander
 */
public final class PagingUtil {

    private PagingUtil() {

    }

    public static Pair<Integer, Integer> getBounds(int total, int pageSize, int currentPage) {
        if (total == 0) {
            return Pair.of(0, 0);
        }
        int maxPage = total / pageSize;
        int fromIndex, toIndex;

        // Check so the currentPage isn't out of bounds.
        int requestedPage = currentPage;
        if (requestedPage > maxPage) {
            requestedPage = maxPage;
        }

        fromIndex = requestedPage * pageSize;
        toIndex = fromIndex + pageSize - 1; // If we're on page 1 and have pagesize 10, we want index 0-9
        if (toIndex > total) {
            toIndex = toIndex - ((toIndex + 1) - total);
        }

        return Pair.of(fromIndex, toIndex);
    }

}
