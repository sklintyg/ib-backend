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

import org.junit.Test;
import org.springframework.data.util.Pair;

import static org.junit.Assert.assertEquals;

public class PagingUtilTest {

    @Test
    public void firstTenOf13() {
        Pair<Integer, Integer> bounds = PagingUtil.getBounds(13, 10, 0);
        assertEquals(0, bounds.getFirst().intValue());
        assertEquals(9, bounds.getSecond().intValue());
    }

    @Test
    public void firstTenOf7() {
        Pair<Integer, Integer> bounds = PagingUtil.getBounds(7, 10, 0);
        assertEquals(0, bounds.getFirst().intValue());
        assertEquals(6, bounds.getSecond().intValue());
    }

    @Test
    public void tenToTwentyOf27() {
        Pair<Integer, Integer> bounds = PagingUtil.getBounds(27, 10, 1);
        assertEquals(10, bounds.getFirst().intValue());
        assertEquals(19, bounds.getSecond().intValue());
    }

    @Test
    public void twentyToThiryOf27() {
        Pair<Integer, Integer> bounds = PagingUtil.getBounds(27, 10, 2);
        assertEquals(20, bounds.getFirst().intValue());
        assertEquals(26, bounds.getSecond().intValue());
    }

    @Test
    public void thirtyToFortyOf27() {
        Pair<Integer, Integer> bounds = PagingUtil.getBounds(27, 10, 3);
        assertEquals(20, bounds.getFirst().intValue());
        assertEquals(26, bounds.getSecond().intValue());
    }
}
