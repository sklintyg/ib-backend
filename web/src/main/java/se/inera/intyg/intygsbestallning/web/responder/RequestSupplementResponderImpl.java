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

import static se.inera.intyg.intygsbestallning.common.util.ResultTypeUtil.error;
import static se.inera.intyg.intygsbestallning.common.util.ResultTypeUtil.ok;
import static se.inera.intyg.intygsbestallning.common.util.RivtaTypesUtil.anII;

import com.google.common.base.Preconditions;
import org.apache.cxf.annotations.SchemaValidation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.riv.intygsbestallning.certificate.order.requestsupplement.v1.RequestSupplementResponseType;
import se.riv.intygsbestallning.certificate.order.requestsupplement.v1.RequestSupplementType;
import se.riv.intygsbestallning.certificate.order.requestsupplement.v1.rivtabp21.RequestSupplementResponderInterface;
import java.util.Objects;
import se.inera.intyg.intygsbestallning.service.utredning.KompletteringService;

@Service
@SchemaValidation
public class RequestSupplementResponderImpl implements RequestSupplementResponderInterface {

    @Value("${source.system.hsaid:}")
    private String sourceSystemHsaId;

    private final KompletteringService kompletteringService;

    public RequestSupplementResponderImpl(final KompletteringService kompletteringService) {
        this.kompletteringService = kompletteringService;
    }

    @Override
    public RequestSupplementResponseType requestSupplement(
            final String logicalAddress, final RequestSupplementType request) {
        RequestSupplementResponseType response = new RequestSupplementResponseType();

        Preconditions.checkArgument(null != logicalAddress);
        Preconditions.checkArgument(null != request);

        if (Objects.isNull(request.getAssessmentId()) || Objects.isNull(request.getAssessmentId().getExtension())) {
            response.setResult(error("Request is missing required field assessmentId"));
            return response;
        }

        try {
            long kompletteringsId = kompletteringService.registerNewKomplettering(request);
            response.setResult(ok());
            response.setSupplementRequestId(anII(sourceSystemHsaId, String.valueOf(kompletteringsId)));
            return response;
        } catch (Exception e) {
            response.setResult(error(e.getMessage()));
            return response;
        }
    }

}
