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
package se.inera.intyg.intygsbestallning.web.controller.api.dto;

import se.inera.intyg.intygsbestallning.persistence.model.InternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;

import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static java.util.Objects.isNull;

public class GetForfraganResponse {
    private static DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;

    private String vardenhetHsaId;
    private String tilldeladDatum;
    private String besvarasSenastDatum;
    private String status;
    private String kommentar;

    public static GetForfraganResponse from(Utredning utredning, String vardenhetHsaId) {

        InternForfragan internForfragan = utredning.getExternForfragan().getInternForfraganList()
                .stream()
                .filter(i -> Objects.equals(i.getVardenhetHsaId(), vardenhetHsaId))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);

        return GetForfraganResponseBuilder.aGetForfraganResponse()
                .withBesvarasSenastDatum(!isNull(internForfragan.getBesvarasSenastDatum())
                        ? internForfragan.getBesvarasSenastDatum().format(formatter) : null)
                .withKommentar(internForfragan.getKommentar())
                .withStatus("TODO")
                .withTilldeladDatum(!isNull(internForfragan.getTilldeladDatum())
                        ? internForfragan.getTilldeladDatum().format(formatter) : null)
                .withVardenhetHsaId(vardenhetHsaId)
                .build();
    }

    public String getVardenhetHsaId() {
        return vardenhetHsaId;
    }

    public void setVardenhetHsaId(String vardenhetHsaId) {
        this.vardenhetHsaId = vardenhetHsaId;
    }

    public String getTilldeladDatum() {
        return tilldeladDatum;
    }

    public void setTilldeladDatum(String tilldeladDatum) {
        this.tilldeladDatum = tilldeladDatum;
    }

    public String getBesvarasSenastDatum() {
        return besvarasSenastDatum;
    }

    public void setBesvarasSenastDatum(String besvarasSenastDatum) {
        this.besvarasSenastDatum = besvarasSenastDatum;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getKommentar() {
        return kommentar;
    }

    public void setKommentar(String kommentar) {
        this.kommentar = kommentar;
    }

    public static final class GetForfraganResponseBuilder {
        private String vardenhetHsaId;
        private String tilldeladDatum;
        private String besvarasSenastDatum;
        private String status;
        private String kommentar;

        private GetForfraganResponseBuilder() {
        }

        public static GetForfraganResponseBuilder aGetForfraganResponse() {
            return new GetForfraganResponseBuilder();
        }

        public GetForfraganResponseBuilder withVardenhetHsaId(String vardenhetHsaId) {
            this.vardenhetHsaId = vardenhetHsaId;
            return this;
        }

        public GetForfraganResponseBuilder withTilldeladDatum(String tilldeladDatum) {
            this.tilldeladDatum = tilldeladDatum;
            return this;
        }

        public GetForfraganResponseBuilder withBesvarasSenastDatum(String besvarasSenastDatum) {
            this.besvarasSenastDatum = besvarasSenastDatum;
            return this;
        }

        public GetForfraganResponseBuilder withStatus(String status) {
            this.status = status;
            return this;
        }

        public GetForfraganResponseBuilder withKommentar(String kommentar) {
            this.kommentar = kommentar;
            return this;
        }

        public GetForfraganResponse build() {
            GetForfraganResponse getForfraganResponse = new GetForfraganResponse();
            getForfraganResponse.setVardenhetHsaId(vardenhetHsaId);
            getForfraganResponse.setTilldeladDatum(tilldeladDatum);
            getForfraganResponse.setBesvarasSenastDatum(besvarasSenastDatum);
            getForfraganResponse.setStatus(status);
            getForfraganResponse.setKommentar(kommentar);
            return getForfraganResponse;
        }
    }
}
