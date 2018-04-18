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

import static java.util.Objects.isNull;
import static se.inera.intyg.intygsbestallning.common.util.ResultTypeUtil.ok;
import static se.inera.intyg.intygsbestallning.common.util.RivtaTypesUtil.anII;

import com.google.common.base.Preconditions;
import org.apache.cxf.annotations.SchemaValidation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.service.utredning.UtredningService;
import se.inera.intyg.intygsbestallning.service.utredning.dto.AssessmentRequest;
import se.riv.intygsbestallning.certificate.order.requesthealthcareperformerforassessment.v1.RequestHealthcarePerformerForAssessmentResponseType;
import se.riv.intygsbestallning.certificate.order.requesthealthcareperformerforassessment.v1.RequestHealthcarePerformerForAssessmentType;
import se.riv.intygsbestallning.certificate.order.requesthealthcareperformerforassessment.v1.rivtabp21.RequestHealthcarePerformerForAssessmentResponderInterface;

@Service
@SchemaValidation
public class RequestHealthcarePerformerForAssessmentResponderImpl
        implements RequestHealthcarePerformerForAssessmentResponderInterface {

    @Value("${source.system.hsaid:}")
    private String sourceSystemHsaId;

    private final UtredningService utredningService;

    public RequestHealthcarePerformerForAssessmentResponderImpl(final UtredningService utredningService) {
        this.utredningService = utredningService;
    }

    @Override
    public RequestHealthcarePerformerForAssessmentResponseType requestHealthcarePerformerForAssessment(
            final String logicalAddress, final RequestHealthcarePerformerForAssessmentType request) {

        Preconditions.checkArgument(!isNull(logicalAddress));
        Preconditions.checkArgument(!isNull(request));

        final Utredning sparadUtredning = utredningService.registerNewUtredning(AssessmentRequest.from(request));

        RequestHealthcarePerformerForAssessmentResponseType response = new RequestHealthcarePerformerForAssessmentResponseType();
        response.setResult(ok());
        response.setAssessmentId(anII(sourceSystemHsaId, sparadUtredning.getUtredningId()));
        return response;
    }
}
