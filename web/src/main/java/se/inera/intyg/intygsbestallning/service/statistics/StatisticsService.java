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
package se.inera.intyg.intygsbestallning.service.statistics;

import se.inera.intyg.intygsbestallning.web.controller.api.dto.statistics.SamordnarStatisticsResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.statistics.VardadminStatisticsResponse;

/**
 * Created by marced on 2018-05-04.
 */
public interface StatisticsService {

    /**
     * Calculates statistics of the number of externforfragningar for the vardgivare / landsting that requires attention
     * from a samordnare.
     *
     * @param vardgivarHsaId
     * @return
     */
    SamordnarStatisticsResponse getStatsForSamordnare(String vardgivarHsaId);

    /**
     * Calculates statistics of the number of internforfragan and bestallningar for the vardenhet that requires attention
     * from a vardadministrator.
     *
     * @param enhetsHsaId
     * @return
     */
    VardadminStatisticsResponse getStatsForVardadmin(String enhetsHsaId);
}
