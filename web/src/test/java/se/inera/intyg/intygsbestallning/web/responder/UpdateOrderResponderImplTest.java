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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static se.inera.intyg.intygsbestallning.persistence.model.Utredning.UtredningBuilder.anUtredning;
import static se.inera.intyg.intygsbestallning.service.utredning.dto.UpdateOrderRequest.INTERPRETER_ERROR_TEXT;
import static se.inera.intyg.intygsbestallning.testutil.TestDataGen.createUpdateOrderType;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.common.util.ResultTypeUtil;
import se.inera.intyg.intygsbestallning.service.utredning.UtredningService;
import se.inera.intyg.intygsbestallning.service.utredning.dto.UpdateOrderRequest;
import se.riv.intygsbestallning.certificate.order.updateorder.v1.UpdateOrderResponseType;
import se.riv.intygsbestallning.certificate.order.updateorder.v1.UpdateOrderType;

@RunWith(MockitoJUnitRunner.class)
public class UpdateOrderResponderImplTest {

    @Mock
    private UtredningService utredningService;

    @InjectMocks
    private UpdateOrderResponderImpl updateOrderResponder;

    @Test
    public void uppdateraOk() {

        final String utrednignsId = "utredning-id";

        doReturn(anUtredning()
                .withUtredningId(utrednignsId).build())
                .when(utredningService)
                .updateOrder(any(UpdateOrderRequest.class));

        final UpdateOrderType request = createUpdateOrderType();
        final UpdateOrderResponseType response = updateOrderResponder.updateOrder("logicalAddress", request);

        assertEquals(ResultTypeUtil.ok(), response.getResult());
    }

    @Test
    public void uppdateraMedTolkBehovUtanTolkSprakOk() {

        final String utrednignsId = "utredning-id";

        doReturn(anUtredning()
                .withUtredningId(utrednignsId).build())
                .when(utredningService)
                .updateOrder(any(UpdateOrderRequest.class));

        final UpdateOrderType request = createUpdateOrderType(true, null);
        final UpdateOrderResponseType response = updateOrderResponder.updateOrder("logicalAddress", request);

        assertEquals(ResultTypeUtil.ok(), response.getResult());
    }

    @Test
    public void uppdateraUtanTolkBehovTolkSprakOk() {

        assertThatThrownBy(() -> updateOrderResponder.updateOrder("logicalAddress", createUpdateOrderType(false, "tolkSprak")))
                .isExactlyInstanceOf(IbServiceException.class)
                .hasMessage(INTERPRETER_ERROR_TEXT);
    }

}