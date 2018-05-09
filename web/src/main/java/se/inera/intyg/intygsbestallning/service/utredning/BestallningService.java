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

import java.util.List;

import se.inera.intyg.intygsbestallning.web.controller.api.dto.AvslutadBestallningListItem;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.BestallningListItem;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.GetBestallningResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.ListAvslutadeBestallningarRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.ListBestallningRequest;
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
    List<BestallningListItem> findOngoingBestallningarForVardenhet(String vardenhetHsaId, ListBestallningRequest requestFilter);

    /**
     * Ends the utredning with the reason if available.
     *
     * @param vardenhetHsaId
     * @return
     */
    ListBestallningFilter buildListBestallningFilter(String vardenhetHsaId);

    List<AvslutadBestallningListItem> findAvslutadeBestallningarForVardenhet(String vardenhetHsaId,
            ListAvslutadeBestallningarRequest request);

    ListAvslutadeBestallningarFilter buildListAvslutadeBestallningarFilter(String vardenhetHsaId);

    /**
     * Retrieves the {@link se.inera.intyg.intygsbestallning.persistence.model.ExternForfragan} for a specific UtredningId.
     *
     * @param utredningId
     * @param vardenhetHsaId
     * @return
     */
    GetBestallningResponse getBestallning(String utredningId, String vardenhetHsaId);

}
