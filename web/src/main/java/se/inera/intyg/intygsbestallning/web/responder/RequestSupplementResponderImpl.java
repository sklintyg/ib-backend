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

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.invoke.MethodHandles.lookup;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang.StringUtils.isNotEmpty;
import static se.inera.intyg.intygsbestallning.common.util.ResultTypeUtil.error;
import static se.inera.intyg.intygsbestallning.common.util.RivtaTypesUtil.anII;

import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.annotations.SchemaValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.monitoring.annotation.PrometheusTimeMethod;
import se.inera.intyg.intygsbestallning.common.util.ResultTypeUtil;
import se.riv.intygsbestallning.certificate.order.requestsupplement.v1.RequestSupplementResponseType;
import se.riv.intygsbestallning.certificate.order.requestsupplement.v1.RequestSupplementType;
import se.riv.intygsbestallning.certificate.order.requestsupplement.v1.rivtabp21.RequestSupplementResponderInterface;
import se.inera.intyg.intygsbestallning.service.utredning.KompletteringService;

@Service
@SchemaValidation
public class RequestSupplementResponderImpl implements RequestSupplementResponderInterface {

    @Value("${source.system.hsaid:}")
    private String sourceSystemHsaId;

    private final KompletteringService kompletteringService;

    private final Logger log = LoggerFactory.getLogger(lookup().lookupClass());

    public RequestSupplementResponderImpl(final KompletteringService kompletteringService) {
        this.kompletteringService = kompletteringService;
    }

    @Override
    @PrometheusTimeMethod
    public RequestSupplementResponseType requestSupplement(
            final String logicalAddress, final RequestSupplementType request) {

        log.info("RequestSupplement received request");

        checkArgument(isNotEmpty(logicalAddress), ResultTypeUtil.LOGICAL_ADDRESS);
        checkArgument(nonNull(request), ResultTypeUtil.REQUEST);

        if (isNull(request.getAssessmentId()) || isNull(request.getAssessmentId().getExtension())) {
            return createErrorResponse("Request is missing required field assessmentId");
        }

        // Not required in schema but required in användningsfall FMU-F011: Huvudflöde 1
        if (StringUtils.isBlank(request.getLastDateForSupplementReceival())) {
            return createErrorResponse("Request is missing required field lastDateForSupplementReceival");
        }

        long kompletteringsId = kompletteringService.registerNewKomplettering(request);
        RequestSupplementResponseType response = new RequestSupplementResponseType();
        response.setResult(ResultTypeUtil.ok());
        response.setSupplementRequestId(anII(sourceSystemHsaId, String.valueOf(kompletteringsId)));
        return response;
    }


    private RequestSupplementResponseType createErrorResponse(String errorMessage) {
        RequestSupplementResponseType response = new RequestSupplementResponseType();
        response.setResult(error(errorMessage));
        response.setSupplementRequestId(anII(sourceSystemHsaId, ""));
        return response;
    }
}
