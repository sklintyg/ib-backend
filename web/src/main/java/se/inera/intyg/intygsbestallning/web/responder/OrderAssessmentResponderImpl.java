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
import static se.inera.intyg.intygsbestallning.common.util.RivtaTypesUtil.anII;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.annotations.SchemaValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.monitoring.annotation.PrometheusTimeMethod;
import se.riv.intygsbestallning.certificate.order.orderassessment.v1.OrderAssessmentResponseType;
import se.riv.intygsbestallning.certificate.order.orderassessment.v1.OrderAssessmentType;
import se.riv.intygsbestallning.certificate.order.orderassessment.v1.rivtabp21.OrderAssessmentResponderInterface;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.service.utredning.UtredningService;
import se.inera.intyg.intygsbestallning.service.utredning.dto.OrderRequest;
import se.inera.intyg.intygsbestallning.web.responder.resulthandler.ResultFactory;

@Service
@SchemaValidation
public class OrderAssessmentResponderImpl implements OrderAssessmentResponderInterface, ResultFactory {

    @Value("${source.system.hsaid:}")
    private String sourceSystemHsaId;

    private final UtredningService utredningService;

    private final Logger log = LoggerFactory.getLogger(lookup().lookupClass());

    public OrderAssessmentResponderImpl(final UtredningService utredningService) {
        this.utredningService = utredningService;
    }

    @Override
    @PrometheusTimeMethod
    public OrderAssessmentResponseType orderAssessment(
            final String logicalAddress, final OrderAssessmentType request) {

        log.info("Received OrderAssessment request");

        try {
            checkArgument(StringUtils.isNotEmpty(logicalAddress), LOGICAL_ADDRESS);
            checkArgument(nonNull(request), REQUEST);

            final OrderRequest orderRequest = OrderRequest.from(request);

            // IF its AF we create new Utredning entity.
            Utredning utredning;
            if (isNull(orderRequest.getUtredningId())) {
                utredning = utredningService.registerNewUtredning(orderRequest);
            } else {
                utredning = utredningService.registerOrder(orderRequest);
            }

            OrderAssessmentResponseType response = new OrderAssessmentResponseType();
            response.setAssessmentId(
                    anII(isNull(request.getAssessmentId())
                                    ? sourceSystemHsaId
                                    : request.getAssessmentId().getRoot(),
                            utredning.getUtredningId().toString()));
            response.setResult(toResultTypeOK());
            return response;
        } catch (final Exception e) {
            log.error("Error in orderAssessment", e);
            OrderAssessmentResponseType response = new OrderAssessmentResponseType();
            response.setAssessmentId(anII(sourceSystemHsaId, ""));
            response.setResult(toResultTypeError(e));
            return response;
        }
    }
}
