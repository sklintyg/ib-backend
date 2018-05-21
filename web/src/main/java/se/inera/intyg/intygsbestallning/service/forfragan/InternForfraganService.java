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
package se.inera.intyg.intygsbestallning.service.forfragan;

import se.inera.intyg.intygsbestallning.web.controller.api.dto.forfragan.CreateInternForfraganRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.GetUtredningResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.forfragan.TilldelaDirektRequest;

public interface InternForfraganService {

    /**
     * Creates InternForfragan for selected vardenheter.
     *
     * @param utredningsId
     * @param landstingHsaId
     * @param request
     * @return
     */
    GetUtredningResponse createInternForfragan(Long utredningsId, String landstingHsaId, CreateInternForfraganRequest request);

    /**
     * Tilldela utredning direkt to selected vardenhet.
     *
     * @param utredningsId
     * @param landstingHsaId
     * @param request
     * @return
     */
    GetUtredningResponse tilldelaDirekt(Long utredningsId, String landstingHsaId, TilldelaDirektRequest request);
}
