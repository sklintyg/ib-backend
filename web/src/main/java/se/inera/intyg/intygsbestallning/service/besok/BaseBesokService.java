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
package se.inera.intyg.intygsbestallning.service.besok;

import org.springframework.beans.factory.annotation.Autowired;
import se.inera.intyg.intygsbestallning.common.util.SchemaDateUtil;
import se.inera.intyg.intygsbestallning.integration.myndighet.dto.ReportCareContactRequestDto;
import se.inera.intyg.intygsbestallning.integration.myndighet.service.MyndighetIntegrationService;
import se.inera.intyg.intygsbestallning.persistence.model.Besok;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.service.utredning.BaseUtredningService;

import static java.util.Objects.nonNull;
import static se.inera.intyg.intygsbestallning.integration.myndighet.dto.ReportCareContactRequestDto.ReportCareContactRequestDtoBuilder.aReportCareContactRequestDto;

public class BaseBesokService extends BaseUtredningService {

    @Autowired
    private MyndighetIntegrationService myndighetIntegrationService;

    public void reportBesok(final Utredning utredning, final Besok besok) {
        myndighetIntegrationService.reportCareContactInteraction(createReportCareContactRequestDto(utredning, besok));
    }

    private ReportCareContactRequestDto createReportCareContactRequestDto(final Utredning utredning, final Besok besok) {
        return aReportCareContactRequestDto()
                .withAssessmentId(utredning.getUtredningId())
                .withAssessmentCareContactId(besok.getId().toString())
                .withParticipatingProfession(besok.getDeltagareProfession().name())
                .withInterpreterStatus(nonNull(besok.getTolkStatus()) ? besok.getTolkStatus().getId() : null)
                .withInvitationDate(SchemaDateUtil.toStringFromLocalDateTime(besok.getKallelseDatum()))
                .withInvitationChannel(besok.getKallelseForm().getCvValue())
                .withStartTime(besok.getBesokStartTid())
                .withEndTime(besok.getBesokSlutTid())
                .withVisitStatus(besok.getBesokStatus().getCvValue())
                .build();
    }

}
