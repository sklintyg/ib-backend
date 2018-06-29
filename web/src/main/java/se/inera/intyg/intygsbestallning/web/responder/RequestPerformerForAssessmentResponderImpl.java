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
import static java.util.Objects.nonNull;
import static org.apache.commons.lang.StringUtils.isNotEmpty;
import static se.inera.intyg.intygsbestallning.common.util.RivtaTypesUtil.anII;

import org.apache.cxf.annotations.SchemaValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.riv.intygsbestallning.certificate.order.requestperformerforassessment.v1.RequestPerformerForAssessmentResponseType;
import se.riv.intygsbestallning.certificate.order.requestperformerforassessment.v1.RequestPerformerForAssessmentType;
import se.riv.intygsbestallning.certificate.order.requestperformerforassessment.v1.rivtabp21.RequestPerformerForAssessmentResponderInterface;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.service.utredning.UtredningService;
import se.inera.intyg.intygsbestallning.service.utredning.dto.AssessmentRequest;
import se.inera.intyg.intygsbestallning.web.responder.resulthandler.ResultFactory;

@Service
@SchemaValidation
public class RequestPerformerForAssessmentResponderImpl implements RequestPerformerForAssessmentResponderInterface, ResultFactory {

    @Value("${source.system.hsaid:}")
    private String sourceSystemHsaId;

    private final UtredningService utredningService;

    private final Logger log = LoggerFactory.getLogger(lookup().lookupClass());

    public RequestPerformerForAssessmentResponderImpl(final UtredningService utredningService) {
        this.utredningService = utredningService;
    }

    @Override
    public RequestPerformerForAssessmentResponseType requestPerformerForAssessment(
            final String logicalAddress, final RequestPerformerForAssessmentType request) {

        log.info("RequestPerformerForAssessment received request");

        try {
            checkArgument(isNotEmpty(logicalAddress), LOGICAL_ADDRESS);
            checkArgument(nonNull(request), REQUEST);

            final Utredning sparadUtredning = utredningService.registerNewUtredning(AssessmentRequest.from(request));

            RequestPerformerForAssessmentResponseType response = new RequestPerformerForAssessmentResponseType();
            response.setResult(toResultTypeOK());
            response.setAssessmentId(anII(sourceSystemHsaId, sparadUtredning.getUtredningId().toString()));
            return response;
        } catch (final Exception e) {
            RequestPerformerForAssessmentResponseType response = new RequestPerformerForAssessmentResponseType();
            response.setAssessmentId(anII(sourceSystemHsaId, ""));
            response.setResult(toResultTypeError(e));
            return response;
        }
    }
}
