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

import org.apache.cxf.annotations.SchemaValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.riv.intygsbestallning.certificate.order.requestsupplement.v1.RequestSupplementResponseType;
import se.riv.intygsbestallning.certificate.order.requestsupplement.v1.RequestSupplementType;
import se.riv.intygsbestallning.certificate.order.requestsupplement.v1.rivtabp21.RequestSupplementResponderInterface;
import se.inera.intyg.intygsbestallning.service.utredning.KompletteringService;
import se.inera.intyg.intygsbestallning.web.responder.resulthandler.ResultFactory;

@Service
@SchemaValidation
public class RequestSupplementResponderImpl implements RequestSupplementResponderInterface, ResultFactory {

    @Value("${source.system.hsaid:}")
    private String sourceSystemHsaId;

    private final KompletteringService kompletteringService;

    private final Logger log = LoggerFactory.getLogger(lookup().lookupClass());

    public RequestSupplementResponderImpl(final KompletteringService kompletteringService) {
        this.kompletteringService = kompletteringService;
    }

    @Override
    public RequestSupplementResponseType requestSupplement(
            final String logicalAddress, final RequestSupplementType request) {
        RequestSupplementResponseType response = new RequestSupplementResponseType();

        log.info("RequestSupplement received request");

        try {

            checkArgument(isNotEmpty(logicalAddress), LOGICAL_ADDRESS);
            checkArgument(nonNull(request), REQUEST);

            if (isNull(request.getAssessmentId()) || isNull(request.getAssessmentId().getExtension())) {
                response.setResult(error("Request is missing required field assessmentId"));
                return response;
            }

            long kompletteringsId = kompletteringService.registerNewKomplettering(request);
            response.setResult(toResultTypeOK());
            response.setSupplementRequestId(anII(sourceSystemHsaId, String.valueOf(kompletteringsId)));
            return response;
        } catch (Exception e) {
            response.setResult(toResultTypeError(e));
            return response;
        }
    }
}
