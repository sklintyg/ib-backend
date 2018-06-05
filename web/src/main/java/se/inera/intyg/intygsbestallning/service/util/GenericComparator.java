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

import com.google.common.base.Strings;
import org.springframework.data.util.Pair;
import se.inera.intyg.intygsbestallning.common.exception.IbErrorCodeEnum;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.persistence.model.status.SortableLabel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class GenericComparator {

    private GenericComparator() {

    }

    public static int compare(Class clazz, Object o1, Object o2, String orderBy, boolean orderByAsc) {
        if (Strings.isNullOrEmpty(orderBy)) {
            return 0;
        }

        try {
            // Reflection...
            Method m = clazz.getDeclaredMethod("get" + camelCase(orderBy));

            Object o1Value = m.invoke(o1);
            Object o2Value = m.invoke(o2);

            if (o1Value == null && o2Value == null) {
                return 0;
            }

            // If either value is null, don't sort?
            if (o1Value == null && o2Value != null) {
                return orderByAsc ? 1 : -1;
            }
            if (o1Value != null && o2Value == null) {
                return orderByAsc ? -1 : 1;
            }

            if (SortableLabel.class.isAssignableFrom(m.getReturnType())) {
                o1Value = ((SortableLabel) o1Value).getLabel();
                o2Value = ((SortableLabel) o2Value).getLabel();
            }

            int compareResult = 0;

            if (o1Value instanceof Number) {
                Pair<Integer, Integer> intPair = castToIntegerPair(o1Value, o2Value);
                compareResult = intPair.getFirst().compareTo(intPair.getSecond());
            }
            if (o1Value instanceof String) {
                compareResult = ((String) o1Value).compareToIgnoreCase((String) o2Value);
            }
            if (o1Value instanceof Boolean) {
                compareResult = Boolean.compare((Boolean) o1Value, (Boolean) o2Value);
            }

            return orderByAsc ? compareResult : Math.negateExact(compareResult);

        } catch (NoSuchMethodException e) {
            throw new IbServiceException(IbErrorCodeEnum.UNKNOWN_INTERNAL_PROBLEM, "Unknown column to order by: '" + orderBy + "'");
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IbServiceException(IbErrorCodeEnum.UNKNOWN_INTERNAL_PROBLEM,
                    "Unable to sort by column : '" + orderBy + "'. Message: " + e.getMessage());
        }
    }

    private static Pair<Integer, Integer> castToIntegerPair(Object o1Value, Object o2Value) {
        Number n1 = (Number) o1Value;
        Number n2 = (Number) o2Value;
        return Pair.of(n1.intValue(), n2.intValue());
    }

    private static String camelCase(String orderBy) {
        return orderBy.substring(0, 1).toUpperCase() + orderBy.substring(1);
    }

}
