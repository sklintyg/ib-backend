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

import com.google.common.base.Preconditions;
import org.apache.cxf.annotations.SchemaValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.inera.intyg.intygsbestallning.common.util.ResultTypeUtil;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.service.utredning.UtredningService;
import se.inera.intyg.intygsbestallning.service.utredning.dto.OrderRequest;
import se.riv.intygsbestallning.certificate.order.orderassessment.v1.OrderAssessmentResponseType;
import se.riv.intygsbestallning.certificate.order.orderassessment.v1.OrderAssessmentType;
import se.riv.intygsbestallning.certificate.order.orderassessment.v1.rivtabp21.OrderAssessmentResponderInterface;

import static java.util.Objects.isNull;
import static se.inera.intyg.intygsbestallning.common.util.RivtaTypesUtil.anII;

@Service
@SchemaValidation
public class OrderAssessmentResponderImpl implements OrderAssessmentResponderInterface {

    @Autowired
    private UtredningService utredningService;

    @Value("${source.system.hsaid:}")
    private String sourceSystemHsaId;

    @Override
    public OrderAssessmentResponseType orderAssessment(
            final String logicalAddress, final OrderAssessmentType request) {

        Preconditions.checkArgument(!isNull(logicalAddress));
        Preconditions.checkArgument(!isNull(request));

        OrderRequest orderRequest = OrderRequest.from(request);

        // IF its AF we create new Utredning entity.
        Utredning utredning;
        if (isNull(orderRequest.getUtredningId())) {
            utredning = utredningService.registerNewUtredning(orderRequest);
        } else {
            utredning = utredningService.registerOrder(orderRequest);
        }

        OrderAssessmentResponseType response = new OrderAssessmentResponseType();
        response.setAssessmentId(
                anII(isNull(request.getAssessmentId()) ? sourceSystemHsaId : request.getAssessmentId().getRoot(),
                        utredning.getUtredningId().toString()));
        response.setResult(ResultTypeUtil.ok());
        return response;
    }
}