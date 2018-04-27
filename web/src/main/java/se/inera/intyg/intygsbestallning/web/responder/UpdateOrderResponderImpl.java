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

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Objects.isNull;
import static se.inera.intyg.intygsbestallning.common.util.ResultTypeUtil.ok;

import com.google.common.base.Preconditions;
import org.apache.cxf.annotations.SchemaValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.intyg.intygsbestallning.service.utredning.UtredningService;
import se.inera.intyg.intygsbestallning.service.utredning.dto.UpdateOrderRequest;
import se.riv.intygsbestallning.certificate.order.updateorder.v1.UpdateOrderResponseType;
import se.riv.intygsbestallning.certificate.order.updateorder.v1.UpdateOrderType;
import se.riv.intygsbestallning.certificate.order.updateorder.v1.rivtabp21.UpdateOrderResponderInterface;

@Service
@SchemaValidation
public class UpdateOrderResponderImpl implements UpdateOrderResponderInterface {

    @Autowired
    private UtredningService utredningService;

    @Override
    public UpdateOrderResponseType updateOrder(final String logicalAddress, final UpdateOrderType request) {

        Preconditions.checkArgument(!isNullOrEmpty(logicalAddress));
        Preconditions.checkArgument(!isNull(request));

        utredningService.updateOrder(UpdateOrderRequest.from(request));

        UpdateOrderResponseType updateOrderResponseType = new UpdateOrderResponseType();
        updateOrderResponseType.setResult(ok());
        return updateOrderResponseType;
    }
}
