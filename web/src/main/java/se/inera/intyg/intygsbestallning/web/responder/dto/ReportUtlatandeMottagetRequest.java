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
import se.riv.intygsbestallning.certificate.order.reportcertificatereceival.v1.ReportCertificateReceivalType;

import java.time.LocalDateTime;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.nonNull;
import static se.inera.intyg.intygsbestallning.common.util.SchemaDateUtil.toLocalDateTimeFromDateType;

public final class ReportUtlatandeMottagetRequest {

    private final Long utredningId;
    private final LocalDateTime mottagetDatum;
    private final LocalDateTime sistaKompletteringsDatum;

    private ReportUtlatandeMottagetRequest(
            final Long utredningId,
            final LocalDateTime mottagetDatum,
            final LocalDateTime sistaKompletteringsDatum) {
        this.utredningId = utredningId;
        this.mottagetDatum = mottagetDatum;
        this.sistaKompletteringsDatum = sistaKompletteringsDatum;
    }

    public Long getUtredningId() {
        return utredningId;
    }

    public LocalDateTime getMottagetDatum() {
        return mottagetDatum;
    }

    public LocalDateTime getSistaKompletteringsDatum() {
        return sistaKompletteringsDatum;
    }

    public static ReportUtlatandeMottagetRequest from(final ReportCertificateReceivalType external) {

        checkArgument(nonNull(external), "ReportCertificateReceivalType must be defined");
        checkArgument(nonNull(external.getAssessmentId()), "AssessmentId must be defined");
        checkArgument(nonNull(external.getReceivedDate()), "ReceivalDate must be defined");
        checkArgument(nonNull(external.getLastDateForSupplementRequest()), "LastDateForSupplementRequest must be defined");

        final Long assessmentId = Long.valueOf(external.getAssessmentId().getExtension());
        final LocalDateTime mottagetDatum = toLocalDateTimeFromDateType(external.getReceivedDate());
        final LocalDateTime sistaKompletteringsDatum = toLocalDateTimeFromDateType(external.getLastDateForSupplementRequest());

        return new ReportUtlatandeMottagetRequest(assessmentId, mottagetDatum, sistaKompletteringsDatum);
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

        ReportUtlatandeMottagetRequest request = (ReportUtlatandeMottagetRequest) o;

        return new EqualsBuilder()
                .append(utredningId, request.utredningId)
                .append(mottagetDatum, request.mottagetDatum)
                .append(sistaKompletteringsDatum, request.sistaKompletteringsDatum)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(utredningId)
                .append(mottagetDatum)
                .append(sistaKompletteringsDatum)
                .toHashCode();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("utredningId", utredningId)
                .add("mottagetDatum", mottagetDatum)
                .add("sistaKompletteringsDatum", sistaKompletteringsDatum)
                .toString();
    }

    // CHECKSTYLE:ON MagicNumber
}
