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
package se.inera.intyg.intygsbestallning.web.responder;

import application.riv.intygsbestallning.certificate.order._1.ResultCodeType;
import application.riv.intygsbestallning.certificate.order._1.ResultType;
import com.google.common.base.Preconditions;
import org.apache.cxf.annotations.SchemaValidation;
import se.riv.intygsbestallning.certificate.order.reportsupplementreceival.rivtabp21.v1.ReportSupplementReceivalResponderInterface;
import se.riv.intygsbestallning.certificate.order.reportsupplementreceival.v1.ReportSupplementReceivalResponseType;
import se.riv.intygsbestallning.certificate.order.reportsupplementreceival.v1.ReportSupplementReceivalType;

@SchemaValidation
public class ReportSupplementReceivalResponderImpl implements ReportSupplementReceivalResponderInterface {

    @Override
    public ReportSupplementReceivalResponseType reportSupplementReceival(
            final String logicalAddress, final ReportSupplementReceivalType request) {

        Preconditions.checkArgument(null != logicalAddress);
        Preconditions.checkArgument(null != request);

        return createDummyResponse();
    }

    private ReportSupplementReceivalResponseType createDummyResponse() {

        ResultType resultType = new ResultType();
        resultType.setResultCode(ResultCodeType.OK);
        resultType.setLogId("DUMMY_LOG_ID");
        resultType.setResultText("DUMMY_RESULT_TEXT");

        ReportSupplementReceivalResponseType response = new ReportSupplementReceivalResponseType();
        response.setResult(resultType);

        return response;
    }
}
