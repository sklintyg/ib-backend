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

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public final class ReportDeviationRequestDto {

    private String besokId;
    private String avvikelseId;
    private String orsakatAv;
    private String beskrivning;
    private String tidpunkt;
    private Boolean invanareUteblev;
    private String samordnare;

    private ReportDeviationRequestDto(
            final String besokId,
            final String avvikelseId,
            final String orsakatAv,
            final String beskrivning,
            final String tidpunkt,
            final Boolean invanareUteblev,
            final String samordnare) {
        this.besokId = besokId;
        this.avvikelseId = avvikelseId;
        this.orsakatAv = orsakatAv;
        this.beskrivning = beskrivning;
        this.tidpunkt = tidpunkt;
        this.invanareUteblev = invanareUteblev;
        this.samordnare = samordnare;
    }

    public String getBesokId() {
        return besokId;
    }

    public String getAvvikelseId() {
        return avvikelseId;
    }

    public String getOrsakatAv() {
        return orsakatAv;
    }

    public String getBeskrivning() {
        return beskrivning;
    }

    public String getTidpunkt() {
        return tidpunkt;
    }

    public Boolean getInvanareUteblev() {
        return invanareUteblev;
    }

    public String getSamordnare() {
        return samordnare;
    }

    public static final class ReportDeviationRequestDtoBuilder {
        private String besokId;
        private String avvikelseId;
        private String orsakatAv;
        private String beskrivning;
        private String tidpunkt;
        private Boolean invanareUteblev;
        private String samordnare;

        private ReportDeviationRequestDtoBuilder() {
        }

        public static ReportDeviationRequestDtoBuilder aReportDeviationRequestDto() {
            return new ReportDeviationRequestDtoBuilder();
        }

        public ReportDeviationRequestDtoBuilder withBesokId(String besokId) {
            this.besokId = besokId;
            return this;
        }

        public ReportDeviationRequestDtoBuilder withAvvikelseId(String avvikelseId) {
            this.avvikelseId = avvikelseId;
            return this;
        }

        public ReportDeviationRequestDtoBuilder withOrsakatAv(String orsakatAv) {
            this.orsakatAv = orsakatAv;
            return this;
        }

        public ReportDeviationRequestDtoBuilder withBeskrivning(String beskrivning) {
            this.beskrivning = beskrivning;
            return this;
        }

        public ReportDeviationRequestDtoBuilder withTidpunkt(String tidpunkt) {
            this.tidpunkt = tidpunkt;
            return this;
        }

        public ReportDeviationRequestDtoBuilder withInvanareUteblev(Boolean invanareUteblev) {
            this.invanareUteblev = invanareUteblev;
            return this;
        }

        public ReportDeviationRequestDtoBuilder withSamordnare(String samordnare) {
            this.samordnare = samordnare;
            return this;
        }

        public ReportDeviationRequestDto build() {
            return new ReportDeviationRequestDto(besokId, avvikelseId, orsakatAv, beskrivning, tidpunkt, invanareUteblev, samordnare);
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

        ReportDeviationRequestDto that = (ReportDeviationRequestDto) o;

        return new org.apache.commons.lang3.builder.EqualsBuilder()
                .append(besokId, that.besokId)
                .append(avvikelseId, that.avvikelseId)
                .append(orsakatAv, that.orsakatAv)
                .append(beskrivning, that.beskrivning)
                .append(tidpunkt, that.tidpunkt)
                .append(invanareUteblev, that.invanareUteblev)
                .append(samordnare, that.samordnare)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(besokId)
                .append(avvikelseId)
                .append(orsakatAv)
                .append(beskrivning)
                .append(tidpunkt)
                .append(invanareUteblev)
                .append(samordnare)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("besokId", besokId)
                .append("avvikelseId", avvikelseId)
                .append("orsakatAv", orsakatAv)
                .append("beskrivning", beskrivning)
                .append("tidpunkt", tidpunkt)
                .append("invanareUteblev", invanareUteblev)
                .append("samordnare", samordnare)
                .toString();
    }
}
