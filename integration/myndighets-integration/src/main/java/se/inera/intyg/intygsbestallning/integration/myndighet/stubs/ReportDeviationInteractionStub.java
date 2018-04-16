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
package se.inera.intyg.intygsbestallning.integration.myndighet.stubs;

import static java.lang.invoke.MethodHandles.lookup;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.riv.intygsbestallning.certificate.order.reportdeviation.v1.ReportDeviationResponseType;
import se.riv.intygsbestallning.certificate.order.reportdeviation.v1.ReportDeviationType;
import se.riv.intygsbestallning.certificate.order.reportdeviation.v1.rivtabp21.ReportDeviationResponderInterface;
import se.riv.intygsbestallning.certificate.order.v1.IIType;
import se.riv.intygsbestallning.certificate.order.v1.ResultCodeType;
import se.riv.intygsbestallning.certificate.order.v1.ResultType;

import java.util.Objects;

public class ReportDeviationInteractionStub implements ReportDeviationResponderInterface {

    private static final Logger LOG = LoggerFactory.getLogger(lookup().lookupClass());

    @Override
    public ReportDeviationResponseType reportDeviation(
            final String logicalAddress, final ReportDeviationType request) {

        LOG.info("ReportDeviationResponseType received request");

        Preconditions.checkArgument(!Strings.isNullOrEmpty(logicalAddress), "logicalAddress may not be null or empty");
        Preconditions.checkArgument(!Objects.isNull(request), "request may not be null");

        return createDummyResponse();
    }

    private ReportDeviationResponseType createDummyResponse() {

        IIType iiType = new IIType();
        iiType.setExtension("DUMMY_EXTENSION");
        iiType.setRoot("DUMY_ROOT");

        ResultType resultType = new ResultType();
        resultType.setLogId("DUMMY_LOG_ID");
        resultType.setResultCode(ResultCodeType.OK);
        resultType.setResultText("DUMMY_RESULT_TEXT");

        ReportDeviationResponseType response = new ReportDeviationResponseType();
        response.setDeviationId(iiType);
        response.setResult(resultType);

        return response;
    }
}
