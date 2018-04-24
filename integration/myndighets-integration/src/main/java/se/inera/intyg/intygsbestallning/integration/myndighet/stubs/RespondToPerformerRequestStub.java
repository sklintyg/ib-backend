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
package se.inera.intyg.intygsbestallning.integration.myndighet.stubs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.riv.intygsbestallning.certificate.order.respondtoperformerrequest.v1.RespondToPerformerRequestResponseType;
import se.riv.intygsbestallning.certificate.order.respondtoperformerrequest.v1.RespondToPerformerRequestType;
import se.riv.intygsbestallning.certificate.order.respondtoperformerrequest.v1.rivtabp21.RespondToPerformerRequestResponderInterface;
import se.riv.intygsbestallning.certificate.order.v1.ResultCodeType;
import se.riv.intygsbestallning.certificate.order.v1.ResultType;

public class RespondToPerformerRequestStub implements RespondToPerformerRequestResponderInterface {

    private static final Logger LOG = LoggerFactory.getLogger(RespondToPerformerRequestStub.class);

    @Override
    public RespondToPerformerRequestResponseType respondToPerformerRequest(String logicalAddress, RespondToPerformerRequestType request) {
        LOG.info("RespondToPerformerRequestStub received request {}", request.getAssessmentId().getExtension());
        RespondToPerformerRequestResponseType response = new RespondToPerformerRequestResponseType();
        ResultType rt = new ResultType();
        rt.setResultCode(ResultCodeType.OK);
        rt.setResultText("Result");
        response.setResult(rt);
        return response;
    }
}
