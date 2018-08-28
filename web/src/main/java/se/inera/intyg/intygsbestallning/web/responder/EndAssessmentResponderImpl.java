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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.monitoring.annotation.PrometheusTimeMethod;
import se.inera.intyg.intygsbestallning.common.util.ResultTypeUtil;
import se.inera.intyg.intygsbestallning.service.utredning.UtredningService;
import se.inera.intyg.intygsbestallning.service.utredning.dto.AvslutaUtredningRequest;
import se.riv.intygsbestallning.certificate.order.endassessment.v1.EndAssessmentResponseType;
import se.riv.intygsbestallning.certificate.order.endassessment.v1.EndAssessmentType;
import se.riv.intygsbestallning.certificate.order.endassessment.v1.rivtabp21.EndAssessmentResponderInterface;


import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.invoke.MethodHandles.lookup;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

@Service
@SchemaValidation
public class EndAssessmentResponderImpl implements EndAssessmentResponderInterface {

    private final UtredningService utredningService;

    private final Logger log = LoggerFactory.getLogger(lookup().lookupClass());

    public EndAssessmentResponderImpl(final UtredningService utredningService) {
        this.utredningService = utredningService;
    }

    @Override
    @PrometheusTimeMethod
    public EndAssessmentResponseType endAssessment(
            final String logicalAddress, final EndAssessmentType request) {

        log.info("Received EndAssessment request");

        checkArgument(isNotEmpty(logicalAddress), ResultTypeUtil.LOGICAL_ADDRESS);
        checkArgument(nonNull(request), ResultTypeUtil.REQUEST);

        utredningService.avslutaUtredning(AvslutaUtredningRequest.from(request));

        EndAssessmentResponseType response = new EndAssessmentResponseType();
        response.setResult(ResultTypeUtil.ok());
        return response;
    }
}
