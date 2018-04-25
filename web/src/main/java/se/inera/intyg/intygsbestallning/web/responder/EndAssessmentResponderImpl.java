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
import se.inera.intyg.intygsbestallning.service.utredning.UtredningService;
import se.inera.intyg.intygsbestallning.service.utredning.dto.EndUtredningRequest;
import se.riv.intygsbestallning.certificate.order.endassessment.v1.EndAssessmentResponseType;
import se.riv.intygsbestallning.certificate.order.endassessment.v1.EndAssessmentType;
import se.riv.intygsbestallning.certificate.order.endassessment.v1.rivtabp21.EndAssessmentResponderInterface;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Objects.isNull;

@Service
@SchemaValidation
public class EndAssessmentResponderImpl implements EndAssessmentResponderInterface {

    @Autowired
    private UtredningService utredningService;

    @Override
    public EndAssessmentResponseType endAssessment(String logicalAddress, EndAssessmentType endAssessmentType) {

        Preconditions.checkArgument(!isNullOrEmpty(logicalAddress), "LogcialAddress needs to be defined");
        Preconditions.checkArgument(!isNull(endAssessmentType), "Request need to be defined");

        utredningService.endUtredning(EndUtredningRequest.from(endAssessmentType));

        EndAssessmentResponseType response = new EndAssessmentResponseType();
        response.setResult(ResutTypeUtil.ok());
        return response;
    }
}
