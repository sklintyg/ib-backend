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
import org.springframework.stereotype.Service;
import se.inera.intyg.intygsbestallning.common.util.ResutTypeUtil;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.service.utredning.UtredningService;
import se.inera.intyg.intygsbestallning.service.utredning.dto.OrderRequest;
import se.riv.intygsbestallning.certificate.order.ordermedicalassessment.v1.OrderMedicalAssessmentResponseType;
import se.riv.intygsbestallning.certificate.order.ordermedicalassessment.v1.OrderMedicalAssessmentType;
import se.riv.intygsbestallning.certificate.order.ordermedicalassessment.v1.rivtabp21.OrderMedicalAssessmentResponderInterface;

import static java.util.Objects.isNull;
import static se.inera.intyg.intygsbestallning.common.util.RivtaTypesUtil.anII;

@Service
@SchemaValidation
public class OrderMedicalAssessmentResponderImpl implements OrderMedicalAssessmentResponderInterface {

    @Autowired
    private UtredningService utredningService;

    @Override
    public OrderMedicalAssessmentResponseType orderMedicalAssessment(
            final String logicalAddress, final OrderMedicalAssessmentType request) {

        Preconditions.checkArgument(!isNull(logicalAddress));
        Preconditions.checkArgument(!isNull(request));

        OrderRequest orderRequest = OrderRequest.fromRequest(request);

        // IF its AF we create new assessment
        Utredning utredning;
        if (isNull(orderRequest.getUtredningId())) {
            utredning = utredningService.registerNewUtredning(orderRequest);
        } else {
            utredning = utredningService.registerOrder(orderRequest);
        }

        OrderMedicalAssessmentResponseType response = new OrderMedicalAssessmentResponseType();
        response.setAssessmentId(anII("ROOT?!", utredning.getUtredningId()));
        response.setResult(ResutTypeUtil.ok());
        return response;
    }
}
