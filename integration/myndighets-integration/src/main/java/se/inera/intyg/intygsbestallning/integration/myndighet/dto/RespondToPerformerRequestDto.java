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

import com.google.common.base.Objects;

public class RespondToPerformerRequestDto {

    private Long assessmentId;
    private String responseCode;
    private String comment;
    private String careUnitId;
    private String careUnitName;
    private String careGiverId;
    private String careGiverName;
    private String postalAddress;
    private String postalCode;
    private String postalCity;
    private String phoneNumber;
    private String email;
    private String subcontractorName;

    public Long getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(Long assessmentId) {
        this.assessmentId = assessmentId;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCareUnitId() {
        return careUnitId;
    }

    public void setCareUnitId(String careUnitId) {
        this.careUnitId = careUnitId;
    }

    public String getCareUnitName() {
        return careUnitName;
    }

    public void setCareUnitName(String careUnitName) {
        this.careUnitName = careUnitName;
    }

    public String getCareGiverId() {
        return careGiverId;
    }

    public void setCareGiverId(String careGiverId) {
        this.careGiverId = careGiverId;
    }

    public String getCareGiverName() {
        return careGiverName;
    }

    public void setCareGiverName(String careGiverName) {
        this.careGiverName = careGiverName;
    }

    public String getPostalAddress() {
        return postalAddress;
    }

    public void setPostalAddress(String postalAddress) {
        this.postalAddress = postalAddress;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPostalCity() {
        return postalCity;
    }

    public void setPostalCity(String postalCity) {
        this.postalCity = postalCity;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSubcontractorName() {
        return subcontractorName;
    }

    public void setSubcontractorName(String subcontractorName) {
        this.subcontractorName = subcontractorName;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RespondToPerformerRequestDto)) {
            return false;
        }
        final RespondToPerformerRequestDto dto = (RespondToPerformerRequestDto) o;
        return Objects.equal(assessmentId, dto.assessmentId)
                && Objects.equal(responseCode, dto.responseCode)
                && Objects.equal(comment, dto.comment)
                && Objects.equal(careUnitId, dto.careUnitId)
                && Objects.equal(careUnitName, dto.careUnitName)
                && Objects.equal(careGiverId, dto.careGiverId)
                && Objects.equal(careGiverName, dto.careGiverName)
                && Objects.equal(postalAddress, dto.postalAddress)
                && Objects.equal(postalCode, dto.postalCode)
                && Objects.equal(postalCity, dto.postalCity)
                && Objects.equal(phoneNumber, dto.phoneNumber)
                && Objects.equal(email, dto.email)
                && Objects.equal(subcontractorName, dto.subcontractorName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(assessmentId,
                responseCode,
                comment,
                careUnitId,
                careUnitName,
                careGiverId,
                careGiverName,
                postalAddress,
                postalCode,
                postalCity,
                phoneNumber,
                email,
                subcontractorName);
    }

    public static final class RespondToPerformerRequestDtoBuilder {
        private Long assessmentId;
        private String responseCode;
        private String comment;
        private String careUnitId;
        private String careUnitName;
        private String careGiverId;
        private String careGiverName;
        private String postalAddress;
        private String postalCode;
        private String postalCity;
        private String phoneNumber;
        private String email;
        private String subcontractorName;

        private RespondToPerformerRequestDtoBuilder() {
        }

        public static RespondToPerformerRequestDtoBuilder aRespondToPerformerRequestDto() {
            return new RespondToPerformerRequestDtoBuilder();
        }

        public RespondToPerformerRequestDtoBuilder withAssessmentId(Long assessmentId) {
            this.assessmentId = assessmentId;
            return this;
        }

        public RespondToPerformerRequestDtoBuilder withResponseCode(String responseCode) {
            this.responseCode = responseCode;
            return this;
        }

        public RespondToPerformerRequestDtoBuilder withComment(String comment) {
            this.comment = comment;
            return this;
        }

        public RespondToPerformerRequestDtoBuilder withCareUnitId(String careUnitId) {
            this.careUnitId = careUnitId;
            return this;
        }

        public RespondToPerformerRequestDtoBuilder withCareUnitName(String careUnitName) {
            this.careUnitName = careUnitName;
            return this;
        }

        public RespondToPerformerRequestDtoBuilder withCareGiverId(String careGiverId) {
            this.careGiverId = careGiverId;
            return this;
        }

        public RespondToPerformerRequestDtoBuilder withCareGiverName(String careGiverName) {
            this.careGiverName = careGiverName;
            return this;
        }

        public RespondToPerformerRequestDtoBuilder withPostalAddress(String postalAddress) {
            this.postalAddress = postalAddress;
            return this;
        }

        public RespondToPerformerRequestDtoBuilder withPostalCode(String postalCode) {
            this.postalCode = postalCode;
            return this;
        }

        public RespondToPerformerRequestDtoBuilder withPostalCity(String postalCity) {
            this.postalCity = postalCity;
            return this;
        }

        public RespondToPerformerRequestDtoBuilder withPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public RespondToPerformerRequestDtoBuilder withEmail(String email) {
            this.email = email;
            return this;
        }

        public RespondToPerformerRequestDtoBuilder withSubcontractorName(String subcontractorName) {
            this.subcontractorName = subcontractorName;
            return this;
        }

        public RespondToPerformerRequestDto build() {
            RespondToPerformerRequestDto respondToPerformerRequestDto = new RespondToPerformerRequestDto();
            respondToPerformerRequestDto.setAssessmentId(assessmentId);
            respondToPerformerRequestDto.setResponseCode(responseCode);
            respondToPerformerRequestDto.setComment(comment);
            respondToPerformerRequestDto.setCareUnitId(careUnitId);
            respondToPerformerRequestDto.setCareUnitName(careUnitName);
            respondToPerformerRequestDto.setCareGiverId(careGiverId);
            respondToPerformerRequestDto.setCareGiverName(careGiverName);
            respondToPerformerRequestDto.setPostalAddress(postalAddress);
            respondToPerformerRequestDto.setPostalCode(postalCode);
            respondToPerformerRequestDto.setPostalCity(postalCity);
            respondToPerformerRequestDto.setPhoneNumber(phoneNumber);
            respondToPerformerRequestDto.setEmail(email);
            respondToPerformerRequestDto.setSubcontractorName(subcontractorName);
            return respondToPerformerRequestDto;
        }
    }
}
