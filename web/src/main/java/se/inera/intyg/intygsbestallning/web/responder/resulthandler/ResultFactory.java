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
package se.inera.intyg.intygsbestallning.web.responder.resulthandler;

import se.riv.intygsbestallning.certificate.order.v1.ResultType;
import java.util.Objects;
import se.inera.intyg.intygsbestallning.common.exception.IbNotFoundException;
import se.inera.intyg.intygsbestallning.common.exception.NotFoundType;
import se.inera.intyg.intygsbestallning.common.util.ResultTypeUtil;

public interface ResultFactory {

    String LOGICAL_ADDRESS = "LogicalAddress need to be defined";
    String REQUEST = "Request need to be defined";

    default ResultType toResultTypeOK() {
        return ResultTypeUtil.ok();
    }

    default ResultType toResultTypeError(final Exception exception) {

        String resultText;

        if (exception instanceof IbNotFoundException) {
            final NotFoundType notFoundType = ((IbNotFoundException) exception).getNotFoundType();
            final Long entityId = ((IbNotFoundException) exception).getErrorEntityId();
            resultText = notFoundType != null
                    ? String.format(notFoundType.getErrorText(), Objects.toString(entityId))
                    : exception.getMessage();
        } else {
            resultText = exception.getMessage();
        }

        return ResultTypeUtil.error(resultText);
    }
}
