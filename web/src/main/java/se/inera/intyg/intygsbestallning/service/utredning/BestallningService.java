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

import se.inera.intyg.intygsbestallning.auth.model.IbSelectableHsaEntity;
import se.inera.intyg.intygsbestallning.auth.model.IbVardenhet;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.bestallning.GetAvslutadeBestallningarListResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.bestallning.GetBestallningListResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.bestallning.GetBestallningResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.bestallning.ListAvslutadeBestallningarRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.bestallning.ListBestallningRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.bestallning.SaveFakturaVeIdForUtredningRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.filter.ListAvslutadeBestallningarFilter;
import se.inera.intyg.intygsbestallning.web.controller.api.filter.ListBestallningFilter;

public interface BestallningService {
    /**
     * Gets all ongoing bestallningar for a given vardenhet.
     *
     * @param vardenhetHsaId
     * @param requestFilter
     * @return
     */
    GetBestallningListResponse findOngoingBestallningarForVardenhet(IbVardenhet vardenhet, ListBestallningRequest requestFilter);

    /**
     * Ends the utredning with the reason if available.
     *
     * @param vardenhetHsaId
     * @return
     */
    ListBestallningFilter buildListBestallningFilter(IbVardenhet vardenhet);

    GetAvslutadeBestallningarListResponse findAvslutadeBestallningarForVardenhet(IbVardenhet vardenhet,
                                                                                 ListAvslutadeBestallningarRequest request);

    ListAvslutadeBestallningarFilter buildListAvslutadeBestallningarFilter(IbVardenhet vardenhet);

    /**
     * Retrieves the {@link se.inera.intyg.intygsbestallning.persistence.model.ExternForfragan} for a specific UtredningId.
     *
     * @param utredningId
     * @param vardenhet
     * @return
     */
    GetBestallningResponse getBestallning(Long utredningId, IbSelectableHsaEntity vardenhet);

    void saveFakturaVeIdForUtredning(Long utredningsId, SaveFakturaVeIdForUtredningRequest request, IbSelectableHsaEntity vardenhet);
}
