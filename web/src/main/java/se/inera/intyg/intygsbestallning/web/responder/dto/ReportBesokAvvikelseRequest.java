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

import com.google.common.base.MoreObjects;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import se.inera.intyg.intygsbestallning.common.util.SchemaDateUtil;
import se.inera.intyg.intygsbestallning.persistence.model.type.AvvikelseOrsak;
import se.riv.intygsbestallning.certificate.order.reportdeviation.v1.ReportDeviationType;

import java.time.LocalDateTime;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.nonNull;

public final class ReportBesokAvvikelseRequest {

    private final Long besokId;
    private final Long avvikelseId;
    private final AvvikelseOrsak orsakatAv;
    private final String beskrivning;
    private final LocalDateTime tidpunkt;
    private final Boolean invanareUteblev;


    private ReportBesokAvvikelseRequest(
            final Long besokId,
            final Long avvikelseId,
            final AvvikelseOrsak orsakatAv,
            final String beskrivning,
            final LocalDateTime tidpunkt,
            final Boolean invanareUteblev) {
        this.besokId = besokId;
        this.avvikelseId = avvikelseId;
        this.orsakatAv = orsakatAv;
        this.beskrivning = beskrivning;
        this.tidpunkt = tidpunkt;
        this.invanareUteblev = invanareUteblev;
    }

    public Long getBesokId() {
        return besokId;
    }

    public Long getAvvikelseId() {
        return avvikelseId;
    }

    public AvvikelseOrsak getOrsakatAv() {
        return orsakatAv;
    }

    public String getBeskrivning() {
        return beskrivning;
    }

    public LocalDateTime getTidpunkt() {
        return tidpunkt;
    }

    public Boolean getInvanareUteblev() {
        return invanareUteblev;
    }

    public static ReportBesokAvvikelseRequest from(final ReportDeviationType type) {

        checkArgument(nonNull(type));
        checkArgument(nonNull(type.getAssessmentCareContactId()));
        checkArgument(nonNull(type.getAssessmentCareContactId().getExtension()));
        checkArgument(nonNull(type.getDeviationId()));
        checkArgument(nonNull(type.getDeviationId().getExtension()));
        checkArgument(nonNull(type.getCausedBy()));
        checkArgument(nonNull(type.getCausedBy().getCode()));
        checkArgument(nonNull(type.getDescription()));
        checkArgument(nonNull(type.getDeviationTime()));
        checkArgument(nonNull(type.isCitizenFailedToArrive()));

        final Long besokId = Long.valueOf(type.getAssessmentCareContactId().getExtension());
        final Long avvikelseId = Long.valueOf(type.getDeviationId().getExtension());
        final AvvikelseOrsak orsakatAv = AvvikelseOrsak.valueOf(type.getCausedBy().getCode());
        final String beskrivning = type.getDescription();
        final LocalDateTime tidpunkt = SchemaDateUtil.toLocalDateTimeFromDateTimeStamp(type.getDeviationTime());
        final Boolean invanareUteblev = BooleanUtils.toBoolean(type.isCitizenFailedToArrive());

        return new ReportBesokAvvikelseRequest(besokId, avvikelseId, orsakatAv, beskrivning, tidpunkt, invanareUteblev);
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
                .append(avvikelseId, that.avvikelseId)
                .append(orsakatAv, that.orsakatAv)
                .append(beskrivning, that.beskrivning)
                .append(tidpunkt, that.tidpunkt)
                .append(invanareUteblev, that.invanareUteblev)
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
                .toHashCode();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("besokId", besokId)
                .add("avvikelseId", avvikelseId)
                .add("orsakatAv", orsakatAv)
                .add("beskrivning", beskrivning)
                .add("tidpunkt", tidpunkt)
                .add("invanareUteblev", invanareUteblev)
                .toString();
    }

    // CHECKSTYLE:ON MagicNumber
}
