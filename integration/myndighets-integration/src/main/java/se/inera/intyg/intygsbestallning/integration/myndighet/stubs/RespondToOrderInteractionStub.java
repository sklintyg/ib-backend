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

import se.inera.intyg.intygsbestallning.common.util.ResultTypeUtil;
import se.riv.intygsbestallning.certificate.order.respondtoorder.v1.rivtabp21.RespondToOrderResponderInterface;
import se.riv.intygsbestallning.certificate.order.respondtoorderresponder.v1.RespondToOrderResponseType;
import se.riv.intygsbestallning.certificate.order.respondtoorderresponder.v1.RespondToOrderType;

public class RespondToOrderInteractionStub implements RespondToOrderResponderInterface {
    @Override
    public RespondToOrderResponseType respondToOrder(String logicalAddress, RespondToOrderType request) {
        RespondToOrderResponseType response = new RespondToOrderResponseType();
        response.setResult(ResultTypeUtil.ok());
        return response;
    }
}
