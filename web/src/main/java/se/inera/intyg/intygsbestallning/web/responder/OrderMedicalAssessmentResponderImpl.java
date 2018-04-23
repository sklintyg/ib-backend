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
import se.inera.intyg.intygsbestallning.service.utredning.UtredningService;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.GetUtredningResponse;
import se.riv.intygsbestallning.certificate.order.ordermedicalassessment.v1.OrderMedicalAssessmentResponseType;
import se.riv.intygsbestallning.certificate.order.ordermedicalassessment.v1.OrderMedicalAssessmentType;
import se.riv.intygsbestallning.certificate.order.ordermedicalassessment.v1.rivtabp21.OrderMedicalAssessmentResponderInterface;

import java.util.Objects;

import static java.util.Objects.isNull;

@Service
@SchemaValidation
public class OrderMedicalAssessmentResponderImpl implements OrderMedicalAssessmentResponderInterface {

//    @Autowired UtredningService utredningService;

    @Override
    public OrderMedicalAssessmentResponseType orderMedicalAssessment(
            final String logicalAddress, final OrderMedicalAssessmentType request) {

        Preconditions.checkArgument(null != logicalAddress);
        Preconditions.checkArgument(null != request);
        validateRequest(request);


        // Its either AF or FK who request the assessment.

        String assessmentID = request.getAssessmentId().getExtension();

        // IF its AF we create whole new assessment.
        if (isNull(assessmentID)) {

        } else { // In this block we handle FK
            // IF its FK we update forfragan
//            utredningService.registerOrder(request);
        }

        return null;
    }

    /**
     * Validates the request based on rules in tkb.
     *
     * @param request the request in to validate
     */
    private void validateRequest(OrderMedicalAssessmentType request) {
        // TODO!
        if (request.getCertificateType() == null) {
            throw new IllegalArgumentException("Not ok!");
        }
    }
}
