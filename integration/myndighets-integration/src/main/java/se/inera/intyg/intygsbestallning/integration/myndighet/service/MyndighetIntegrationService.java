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
package se.inera.intyg.intygsbestallning.integration.myndighet.service;

import se.inera.intyg.intygsbestallning.integration.myndighet.dto.ReportCareContactRequestDto;
import se.inera.intyg.intygsbestallning.integration.myndighet.dto.ReportDeviationRequestDto;
import se.inera.intyg.intygsbestallning.integration.myndighet.dto.RespondToPerformerRequestDto;

import java.time.LocalDateTime;

public interface MyndighetIntegrationService {

    void respondToPerformerRequest(RespondToPerformerRequestDto request);

    void reportCareContactInteraction(ReportCareContactRequestDto request);

    LocalDateTime updateAssessment(Long id, String certificateType);

    void reportDeviation(ReportDeviationRequestDto reportDeviationRequestDto);
}
