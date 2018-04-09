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

import org.apache.cxf.annotations.SchemaValidation;
import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;

import se.riv.intygsbestallning.certificate.order.reportcontinuationdecision.v1.ReportContinuationDecisionResponseType;
import se.riv.intygsbestallning.certificate.order.reportcontinuationdecision.v1.ReportContinuationDecisionType;
import se.riv.intygsbestallning.certificate.order.reportcontinuationdecision.v1.rivtabp21.ReportContinuationDecisionResponderInterface;
import se.riv.intygsbestallning.certificate.order.v1.ResultCodeType;
import se.riv.intygsbestallning.certificate.order.v1.ResultType;

@Service
@SchemaValidation
public class ReportContinuationDecisionResponderImpl implements ReportContinuationDecisionResponderInterface {

    @Override
    public ReportContinuationDecisionResponseType reportContinuationDecision(
            final String logicalAddress, final ReportContinuationDecisionType request) {

        Preconditions.checkArgument(null != logicalAddress);
        Preconditions.checkArgument(null != request);

        return createDummyResponse();
    }

    private ReportContinuationDecisionResponseType createDummyResponse() {

        ResultType resultType = new ResultType();
        resultType.setResultCode(ResultCodeType.OK);
        resultType.setLogId("DUMMY_LOG_ID");
        resultType.setResultText("DUMMY_RESULT_TEXT");

        ReportContinuationDecisionResponseType response = new ReportContinuationDecisionResponseType();
        response.setResult(resultType);

        return response;
    }
}
