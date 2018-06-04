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
package se.inera.intyg.intygsbestallning.service.utredning;

import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.service.utredning.dto.AssessmentRequest;
import se.inera.intyg.intygsbestallning.service.utredning.dto.EndUtredningRequest;
import se.inera.intyg.intygsbestallning.service.utredning.dto.OrderRequest;
import se.inera.intyg.intygsbestallning.service.utredning.dto.UpdateOrderRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.forfragan.InternForfraganListItem;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.utredning.GetUtredningListResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.utredning.GetUtredningResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.utredning.ListUtredningRequest;

import java.util.List;

public interface UtredningService {

    /**
     * Retrieves all {@link se.inera.intyg.intygsbestallning.persistence.model.ExternForfragan} for a specific landsting
     * given the supplied filter criteria.
     *
     * @param landstingHsaId
     * @param request
     * @return
     */
    GetUtredningListResponse findExternForfraganByLandstingHsaIdWithFilter(String landstingHsaId, ListUtredningRequest request);

    /**
     * Retrieves the {@link se.inera.intyg.intygsbestallning.persistence.model.ExternForfragan} for a specific UtredningId.
     *
     * @param utredningId
     * @param landstingHsaId
     * @return
     */
    GetUtredningResponse getExternForfragan(Long utredningId, String landstingHsaId);

    /**
     * Gets all the {@link se.inera.intyg.intygsbestallning.persistence.model.InternForfragan} for the unit with HSA-id
     * vardenhetHsaId.
     *
     * @param vardenhetHsaId
     *            the HSA-id of the unit
     * @return a list of the InternForfragan represented as a {@link InternForfraganListItem}
     */
    List<InternForfraganListItem> findForfragningarForVardenhetHsaId(String vardenhetHsaId);

    /**
     * Handles the new incomming order for FMU.
     * <p>
     * Updates all the information retrieved from the
     * {@link se.inera.intyg.intygsbestallning.persistence.model.ExternForfragan} if new
     * information is available.
     *
     * @param order
     * @return
     */
    Utredning registerOrder(OrderRequest order);

    /**
     * Registers new order for Bestallningsportalen.
     * <p>
     * In this case there is no {@link se.inera.intyg.intygsbestallning.persistence.model.ExternForfragan} and all
     * information is taken
     * from the order.
     *
     * @param order
     * @return
     */
    Utredning registerNewUtredning(OrderRequest order);

    /**
     * Handles the new incoming request for FMU.
     * <p>
     * Creates an {@link se.inera.intyg.intygsbestallning.persistence.model.ExternForfragan}
     * and an {@link se.inera.intyg.intygsbestallning.persistence.model.Utredning}.
     *
     * @param request
     * @return
     */
    Utredning registerNewUtredning(AssessmentRequest request);

    /**
     * Ends the utredning with the reason if available.
     *
     * @param endUtredningRequest
     */
    void endUtredning(EndUtredningRequest endUtredningRequest);

    Utredning updateOrder(UpdateOrderRequest update);

}
