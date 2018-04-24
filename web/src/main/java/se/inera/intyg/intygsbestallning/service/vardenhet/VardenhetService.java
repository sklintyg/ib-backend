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
package se.inera.intyg.intygsbestallning.service.vardenhet;

import se.inera.intyg.intygsbestallning.web.controller.api.dto.VardenhetPreferenceRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.VardenhetPreferenceResponse;

/**
 * Created by marced on 2018-04-23.
 */
public interface VardenhetService {

    /**
     * Returns the latest vardenhetspreference.
     * If a preference for the hsaId is not already present in repository, it will fetch and return initial data
     * from HSA, so a result is guaranteed.
     *
     * @param hsaId
     *            id for the vardenhet to get preference for
     * @return latest or initial preference
     */
    VardenhetPreferenceResponse getVardEnhetPreference(String hsaId);

    VardenhetPreferenceResponse setVardEnhetPreference(String hsaId, VardenhetPreferenceRequest vardenhetPreferenceRequest);
}
