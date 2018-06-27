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
package se.inera.intyg.intygsbestallning.web.responder.dto;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.nonNull;
import static se.inera.intyg.intygsbestallning.web.responder.dto.ReportBesokAvvikelseRequest.ReportBesokAvvikelseRequestBuilder.aReportBesokAvvikelseRequest;

import com.google.common.base.MoreObjects;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import se.riv.intygsbestallning.certificate.order.reportdeviation.v1.ReportDeviationType;
import java.time.LocalDateTime;
import java.util.Optional;
import se.inera.intyg.intygsbestallning.common.util.SchemaDateUtil;
import se.inera.intyg.intygsbestallning.persistence.model.status.Actor;
import se.inera.intyg.intygsbestallning.persistence.model.type.AvvikelseOrsak;
import se.inera.intyg.intygsbestallning.persistence.model.type.HandelseTyp;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.besok.ReportBesokAvvikelseVardenRequest;

public final class ReportBesokAvvikelseRequest {

    private Long besokId;
    private AvvikelseOrsak orsakatAv;
    private String beskrivning;
    private LocalDateTime tidpunkt;
    private Boolean invanareUteblev;
    private String samordnare;
    private HandelseTyp handelseTyp;

    public Long getBesokId() {
        return besokId;
    }

    public AvvikelseOrsak getOrsakatAv() {
        return orsakatAv;
    }

    public Optional<String> getBeskrivning() {
        return Optional.ofNullable(beskrivning);
    }

    public LocalDateTime getTidpunkt() {
        return tidpunkt;
    }

    public Boolean getInvanareUteblev() {
        return invanareUteblev;
    }

    public String getSamordnare() {
        return samordnare;
    }

    public HandelseTyp getHandelseTyp() {
        return handelseTyp;
    }

    public static ReportBesokAvvikelseRequest from(final long besokId, final ReportBesokAvvikelseVardenRequest dto,
                                                   final String samordnare) {

        checkArgument(nonNull(besokId));
        checkArgument(nonNull(dto.getOrsakatAv()));
        checkArgument(nonNull(dto.getDatum()));
        checkArgument(nonNull(dto.getTid()));
        checkArgument(nonNull(dto.getInvanareUteblev()));

        final AvvikelseOrsak orsakatAv = dto.getOrsakatAv();
        final String beskrivning = dto.getBeskrivning();
        final LocalDateTime tidpunkt = LocalDateTime.of(dto.getDatum(), dto.getTid());
        final Boolean invanareUteblev = BooleanUtils.toBoolean(dto.getInvanareUteblev());
        final HandelseTyp handelseTyp = HandelseTyp.AVVIKELSE_RAPPORTERAD;

        return aReportBesokAvvikelseRequest()
                .withBesokId(besokId)
                .withOrsakatAv(orsakatAv)
                .withBeskrivning(beskrivning)
                .withTidpunkt(tidpunkt)
                .withInvanareUteblev(invanareUteblev)
                .withSamordnare(samordnare)
                .withHandelseTyp(handelseTyp)
                .build();
    }

    public static ReportBesokAvvikelseRequest from(final ReportDeviationType type) {

        checkArgument(nonNull(type));
        checkArgument(nonNull(type.getAssessmentCareContactId()), "AssessmentCareContactId may not be null");
        checkArgument(nonNull(type.getAssessmentCareContactId().getExtension()), "AssessmentCareContactId Extension may not be null");
        checkArgument(nonNull(type.getCausedBy()), "CausedBy may not be null");
        checkArgument(nonNull(type.getCausedBy().getCode()), "CausedBy Code may not be null");
        checkArgument(nonNull(type.getDeviationTime()), "DeviationTime may not be null");
        checkArgument(nonNull(type.isCitizenFailedToArrive()), "CitizenFailedToArrive may not be null");

        final Long besokId = Long.valueOf(type.getAssessmentCareContactId().getExtension());
        final AvvikelseOrsak orsakatAv = AvvikelseOrsak.valueOf(type.getCausedBy().getCode());
        final String beskrivning = type.getDescription();

        final LocalDateTime tidpunkt;
        try {
            tidpunkt = SchemaDateUtil.toLocalDateTimeFromDateTimeStamp(type.getDeviationTime());
        } catch (Exception e) {
            throw new IllegalStateException(e.getCause());
        }

        final Boolean invanareUteblev = BooleanUtils.toBoolean(type.isCitizenFailedToArrive());
        final HandelseTyp handelseTyp = HandelseTyp.AVVIKELSE_MOTTAGEN;

        return aReportBesokAvvikelseRequest()
                .withBesokId(besokId)
                .withOrsakatAv(orsakatAv)
                .withBeskrivning(beskrivning)
                .withTidpunkt(tidpunkt)
                .withInvanareUteblev(invanareUteblev)
                .withSamordnare(Actor.FK.getLabel())
                .withHandelseTyp(handelseTyp)
                .build();
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

        ReportBesokAvvikelseRequest that = (ReportBesokAvvikelseRequest) o;

        return new EqualsBuilder()
                .append(besokId, that.besokId)
                .append(orsakatAv, that.orsakatAv)
                .append(beskrivning, that.beskrivning)
                .append(tidpunkt, that.tidpunkt)
                .append(invanareUteblev, that.invanareUteblev)
                .append(samordnare, that.samordnare)
                .append(handelseTyp, that.handelseTyp)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(besokId)
                .append(orsakatAv)
                .append(beskrivning)
                .append(tidpunkt)
                .append(invanareUteblev)
                .append(samordnare)
                .append(handelseTyp)
                .toHashCode();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("besokId", besokId)
                .add("orsakatAv", orsakatAv)
                .add("beskrivning", beskrivning)
                .add("tidpunkt", tidpunkt)
                .add("invanareUteblev", invanareUteblev)
                .add("samordnare", samordnare)
                .add("handelseTyp", handelseTyp)
                .toString();
    }

    public static final class ReportBesokAvvikelseRequestBuilder {
        private Long besokId;
        private AvvikelseOrsak orsakatAv;
        private String beskrivning;
        private LocalDateTime tidpunkt;
        private Boolean invanareUteblev;
        private String samordnare;
        private HandelseTyp handelseTyp;

        private ReportBesokAvvikelseRequestBuilder() {
        }

        public static ReportBesokAvvikelseRequestBuilder aReportBesokAvvikelseRequest() {
            return new ReportBesokAvvikelseRequestBuilder();
        }

        public ReportBesokAvvikelseRequestBuilder withBesokId(Long besokId) {
            this.besokId = besokId;
            return this;
        }

        public ReportBesokAvvikelseRequestBuilder withOrsakatAv(AvvikelseOrsak orsakatAv) {
            this.orsakatAv = orsakatAv;
            return this;
        }

        public ReportBesokAvvikelseRequestBuilder withBeskrivning(String beskrivning) {
            this.beskrivning = beskrivning;
            return this;
        }

        public ReportBesokAvvikelseRequestBuilder withTidpunkt(LocalDateTime tidpunkt) {
            this.tidpunkt = tidpunkt;
            return this;
        }

        public ReportBesokAvvikelseRequestBuilder withInvanareUteblev(Boolean invanareUteblev) {
            this.invanareUteblev = invanareUteblev;
            return this;
        }

        public ReportBesokAvvikelseRequestBuilder withSamordnare(String samordnare) {
            this.samordnare = samordnare;
            return this;
        }

        public ReportBesokAvvikelseRequestBuilder withHandelseTyp(HandelseTyp handelseTyp) {
            this.handelseTyp = handelseTyp;
            return this;
        }

        public ReportBesokAvvikelseRequest build() {
            ReportBesokAvvikelseRequest reportBesokAvvikelseRequest = new ReportBesokAvvikelseRequest();
            reportBesokAvvikelseRequest.besokId = this.besokId;
            reportBesokAvvikelseRequest.orsakatAv = this.orsakatAv;
            reportBesokAvvikelseRequest.invanareUteblev = this.invanareUteblev;
            reportBesokAvvikelseRequest.handelseTyp = this.handelseTyp;
            reportBesokAvvikelseRequest.beskrivning = this.beskrivning;
            reportBesokAvvikelseRequest.samordnare = this.samordnare;
            reportBesokAvvikelseRequest.tidpunkt = this.tidpunkt;
            return reportBesokAvvikelseRequest;
        }
    }

    // CHECKSTYLE:ON MagicNumber

}
