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
package se.inera.intyg.intygsbestallning.integration.myndighet.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.LocalDateTime;

public final class ReportCareContactRequestDto {

    private Long assessmentId;
    private String assessmentCareContactId;
    private String participatingProfession;
    private String interpreterStatus;
    private String invitationDate;
    private String invitationChannel;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String visitStatus;

    public Long getAssessmentId() {
        return assessmentId;
    }

    public String getAssessmentCareContactId() {
        return assessmentCareContactId;
    }

    public String getParticipatingProfession() {
        return participatingProfession;
    }

    public String getInterpreterStatus() {
        return interpreterStatus;
    }

    public String getInvitationDate() {
        return invitationDate;
    }

    public String getInvitationChannel() {
        return invitationChannel;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public String getVisitStatus() {
        return visitStatus;
    }

    public static final class ReportCareContactRequestDtoBuilder {
        private Long assessmentId;
        private String assessmentCareContactId;
        private String participatingProfession;
        private String interpreterStatus;
        private String invitationDate;
        private String invitationChannel;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String visitStatus;

        private ReportCareContactRequestDtoBuilder() {
        }

        public static ReportCareContactRequestDtoBuilder aReportCareContactRequestDto() {
            return new ReportCareContactRequestDtoBuilder();
        }

        public ReportCareContactRequestDtoBuilder withAssessmentId(Long assessmentId) {
            this.assessmentId = assessmentId;
            return this;
        }

        public ReportCareContactRequestDtoBuilder withAssessmentCareContactId(String assessmentCareContactId) {
            this.assessmentCareContactId = assessmentCareContactId;
            return this;
        }

        public ReportCareContactRequestDtoBuilder withParticipatingProfession(String participatingProfession) {
            this.participatingProfession = participatingProfession;
            return this;
        }

        public ReportCareContactRequestDtoBuilder withInterpreterStatus(String interpreterStatus) {
            this.interpreterStatus = interpreterStatus;
            return this;
        }

        public ReportCareContactRequestDtoBuilder withInvitationDate(String invitationDate) {
            this.invitationDate = invitationDate;
            return this;
        }

        public ReportCareContactRequestDtoBuilder withInvitationChannel(String invitationChannel) {
            this.invitationChannel = invitationChannel;
            return this;
        }

        public ReportCareContactRequestDtoBuilder withStartTime(LocalDateTime startTime) {
            this.startTime = startTime;
            return this;
        }

        public ReportCareContactRequestDtoBuilder withEndTime(LocalDateTime endTime) {
            this.endTime = endTime;
            return this;
        }

        public ReportCareContactRequestDtoBuilder withVisitStatus(String visitStatus) {
            this.visitStatus = visitStatus;
            return this;
        }

        public ReportCareContactRequestDto build() {
            ReportCareContactRequestDto reportCareContactRequestDto = new ReportCareContactRequestDto();
            reportCareContactRequestDto.participatingProfession = this.participatingProfession;
            reportCareContactRequestDto.assessmentId = this.assessmentId;
            reportCareContactRequestDto.invitationChannel = this.invitationChannel;
            reportCareContactRequestDto.endTime = this.endTime;
            reportCareContactRequestDto.startTime = this.startTime;
            reportCareContactRequestDto.invitationDate = this.invitationDate;
            reportCareContactRequestDto.visitStatus = this.visitStatus;
            reportCareContactRequestDto.interpreterStatus = this.interpreterStatus;
            reportCareContactRequestDto.assessmentCareContactId = this.assessmentCareContactId;
            return reportCareContactRequestDto;
        }
    }

    // CHECKSTYLE:OFF MagicNumber

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ReportCareContactRequestDto that = (ReportCareContactRequestDto) o;

        return new EqualsBuilder()
                .append(assessmentId, that.assessmentId)
                .append(assessmentCareContactId, that.assessmentCareContactId)
                .append(participatingProfession, that.participatingProfession)
                .append(interpreterStatus, that.interpreterStatus)
                .append(invitationDate, that.invitationDate)
                .append(invitationChannel, that.invitationChannel)
                .append(startTime, that.startTime)
                .append(endTime, that.endTime)
                .append(visitStatus, that.visitStatus)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(assessmentId)
                .append(assessmentCareContactId)
                .append(participatingProfession)
                .append(interpreterStatus)
                .append(invitationDate)
                .append(invitationChannel)
                .append(startTime)
                .append(endTime)
                .append(visitStatus)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("assessmentId", assessmentId)
                .append("assessmentCareContactId", assessmentCareContactId)
                .append("participatingProfession", participatingProfession)
                .append("interpreterStatus", interpreterStatus)
                .append("invitationDate", invitationDate)
                .append("invitationChannel", invitationChannel)
                .append("startTime", startTime)
                .append("endTime", endTime)
                .append("visitStatus", visitStatus)
                .toString();
    }

    // CHECKSTYLE:ON MagicNumber
}
