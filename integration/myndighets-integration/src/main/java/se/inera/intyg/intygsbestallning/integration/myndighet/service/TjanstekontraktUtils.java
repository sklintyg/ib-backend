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

import static se.inera.intyg.intygsbestallning.common.util.RivtaTypesUtil.aCv;
import static se.inera.intyg.intygsbestallning.common.util.RivtaTypesUtil.anII;

import se.inera.intyg.intygsbestallning.integration.myndighet.dto.ReportCareContactRequestDto;
import se.riv.intygsbestallning.certificate.order.reportcarecontact.v1.ReportCareContactType;
import se.riv.intygsbestallning.certificate.order.v1.TimePeriodType;

import java.time.LocalDateTime;

public final class TjanstekontraktUtils {

    private TjanstekontraktUtils() {
    }

    public static ReportCareContactType aReportCareContact(final String sourceSystemHsaId, final ReportCareContactRequestDto dto) {
        ReportCareContactType request = new ReportCareContactType();
        request.setAssessmentId(anII(sourceSystemHsaId, dto.getAssessmentId()));
        request.setAssessmentCareContactId(anII(sourceSystemHsaId, dto.getAssessmentCareContactId()));
        request.setParticipatingProfession(aCv(dto.getParticipatingProfession()));
        request.setInterpreterStatus(aCv(dto.getInterpreterStatus()));
        request.setInvitationDate(dto.getInvitationDate());
        request.setInvitationChannel(aCv(dto.getInvitationChannel()));
        request.setTime(aTimePeriod(dto.getStartTime(), dto.getEndTime()));
        request.setVisitStatus(aCv(dto.getVisitStatus()));
        return request;
    }

    public static TimePeriodType aTimePeriod(final LocalDateTime start, final LocalDateTime end) {
        TimePeriodType period = new TimePeriodType();
        period.setStart(start.toString());
        period.setEnd(end.toString());
        return period;
    }
}
