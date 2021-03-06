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

import se.riv.intygsbestallning.certificate.order.v1.ResultCodeType;
import se.riv.intygsbestallning.certificate.order.v1.ResultType;

public final class ResultTypeUtil {

    public static final String LOGICAL_ADDRESS = "LogicalAddress need to be defined";
    public static final String REQUEST = "Request need to be defined";

    private ResultTypeUtil() {
    }

    public static ResultType ok() {
        ResultType result = new ResultType();
        result.setResultCode(ResultCodeType.OK);
        return result;
    }

    public static ResultType error(String text) {
        ResultType result = new ResultType();
        result.setResultCode(ResultCodeType.ERROR);
        result.setResultText(text);
        return result;
    }
}
