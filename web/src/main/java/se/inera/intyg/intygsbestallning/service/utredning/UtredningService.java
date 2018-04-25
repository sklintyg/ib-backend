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
import se.inera.intyg.intygsbestallning.service.utredning.dto.OrderRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.BestallningListItem;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.ForfraganListItem;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.GetForfraganResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.GetUtredningResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.UtredningListItem;
import se.riv.intygsbestallning.certificate.order.requesthealthcareperformerforassessment.v1.RequestHealthcarePerformerForAssessmentType;

import java.util.List;

public interface UtredningService {

    Utredning registerNewUtredning(RequestHealthcarePerformerForAssessmentType req);

    List<UtredningListItem> findUtredningarByLandstingHsaId(String landstingHsaId);

    GetUtredningResponse getUtredning(String utredningId, String landstingHsaId);

    /**
     * Gets all the {@link se.inera.intyg.intygsbestallning.persistence.model.InternForfragan} for the unit with HSA-id vardenhetHsaId.
     *
     * @param vardenhetHsaId the HSA-id of the unit
     * @return a list of the InternForfragan represented as a {@link ForfraganListItem}
     */
    List<ForfraganListItem> findForfragningarForVardenhetHsaId(String vardenhetHsaId);

    /**
     * Get the {@link se.inera.intyg.intygsbestallning.persistence.model.InternForfragan} associated with the utredningid and
     * vardenhetHsaId as a {@link GetForfraganResponse}.
     *
     * @param utredningId    the id of the utredning
     * @param vardenhetHsaId the hsaId of the vardenhet of which have received an InternForfragan
     * @return The information of the InternForfragan
     */
    GetForfraganResponse getForfragan(String utredningId, String vardenhetHsaId);

    /**
     * Handles the new incomming order for FMU.
     * <p>
     * Updates all the information retrieved from the {@link se.inera.intyg.intygsbestallning.persistence.model.ExternForfragan} if new
     * information is available.
     *
     * @param order
     * @return
     */
    Utredning registerOrder(OrderRequest order);

    /**
     * Registers new order for Bestallningsportalen.
     * <p>
     * In this case there is no {@link se.inera.intyg.intygsbestallning.persistence.model.ExternForfragan} and all information is taken
     * from the order.
     *
     * @param order
     * @return
     */
    Utredning registerNewUtredning(OrderRequest order);

    /**
     * Gets all ongoing bestallningar for a given vardenhet.
     *
     * @param vardenhetHsaId
     * @return
     */
    List<BestallningListItem> findOngoingBestallningarForVardenhet(String vardenhetHsaId);
}
