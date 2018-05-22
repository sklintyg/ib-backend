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
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import se.inera.intyg.intygsbestallning.common.util.SchemaDateUtil;
import se.riv.intygsbestallning.certificate.order.reportsupplementreceival.v1.ReportSupplementReceivalType;

import java.time.LocalDateTime;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.nonNull;

public final class ReportKompletteringMottagenRequest {

    private final Long utredningId;
    private final LocalDateTime mottagetDatum;
    private final Long kompletteringId;
    private final LocalDateTime sistaKompletteringDatum;

    private ReportKompletteringMottagenRequest(
            final Long utredningId,
            final LocalDateTime mottagetDatum,
            final Long kompletteringId,
            final LocalDateTime sistaKompletteringDatum) {
        this.utredningId = utredningId;
        this.mottagetDatum = mottagetDatum;
        this.kompletteringId = kompletteringId;
        this.sistaKompletteringDatum = sistaKompletteringDatum;
    }

    public Long getUtredningId() {
        return utredningId;
    }

    public LocalDateTime getMottagetDatum() {
        return mottagetDatum;
    }

    public Long getKompletteringId() {
        return kompletteringId;
    }

    public LocalDateTime getSistaKompletteringDatum() {
        return sistaKompletteringDatum;
    }

    public static ReportKompletteringMottagenRequest from(final ReportSupplementReceivalType type) {

        checkArgument(nonNull(type));
        checkArgument(nonNull(type.getAssessmentId()));
        checkArgument(nonNull(type.getAssessmentId().getExtension()));
        checkArgument(nonNull(type.getReceivedDate()));
        checkArgument(nonNull(type.getSupplementRequestId()));
        checkArgument(nonNull(type.getLastDateForSupplementRequest()));

        final Long utredningId = Long.valueOf(type.getAssessmentId().getExtension());
        final LocalDateTime mottagetDatum = SchemaDateUtil.toLocalDateTimeFromDateType(type.getReceivedDate());
        final Long kompletteringId = Long.valueOf(type.getSupplementRequestId().getExtension());
        final LocalDateTime sistaKompletteringDatum = SchemaDateUtil.toLocalDateTimeFromDateType(type.getLastDateForSupplementRequest());

        return new ReportKompletteringMottagenRequest(utredningId, mottagetDatum, kompletteringId, sistaKompletteringDatum);
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

        ReportKompletteringMottagenRequest that = (ReportKompletteringMottagenRequest) o;

        return new EqualsBuilder()
                .append(utredningId, that.utredningId)
                .append(mottagetDatum, that.mottagetDatum)
                .append(kompletteringId, that.kompletteringId)
                .append(sistaKompletteringDatum, that.sistaKompletteringDatum)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(utredningId)
                .append(mottagetDatum)
                .append(kompletteringId)
                .append(sistaKompletteringDatum)
                .toHashCode();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("utredningId", utredningId)
                .add("mottagetDatum", mottagetDatum)
                .add("kompletteringId", kompletteringId)
                .add("sistaKompletteringDatum", sistaKompletteringDatum)
                .toString();
    }

    // CHECKSTYLE:ON MagicNumber
}
