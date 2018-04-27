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

import static java.util.Objects.isNull;

import se.inera.intyg.intygsbestallning.persistence.model.Utredning;

import java.time.format.DateTimeFormatter;

public class GetUtredningResponse {
    private static DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;

    private String utredningsId;

    private String utredningsTyp;

    private String vardgivareHsaId;

    private String inkomDatum;

    private String besvarasSenastDatum;

    private String invanarePersonId;

    private String handlaggareNamn;

    private String handlaggareTelefonnummer;

    private String handlaggareEpost;

    private boolean behovTolk;

    private String tolkSprak;

    private String status;

    public static GetUtredningResponse from(Utredning utredning) {
        return GetUtredningResponseBuilder.aGetUtredningResponse()
                .withUtredningsId(utredning.getUtredningId())
                .withUtredningsTyp(utredning.getUtredningsTyp().name())
                .withVardgivareHsaId(!isNull(utredning.getExternForfragan())
                        ? utredning.getExternForfragan().getLandstingHsaId() : null)
                .withInkomDatum(!isNull(utredning.getExternForfragan())
                        ? utredning.getExternForfragan().getInkomDatum().format(formatter) : null)
                .withBesvarasSenastDatum(!isNull(utredning.getExternForfragan())
                        ? utredning.getExternForfragan().getBesvarasSenastDatum().format(formatter) : null)
                .withInvanarePersonId(utredning.getInvanare().getPersonId())
                .withHandlaggareNamn(utredning.getHandlaggare().getFullstandigtNamn())
                .withHandlaggareTelefonnummer(utredning.getHandlaggare().getTelefonnummer())
                .withHandlaggareEpost(utredning.getHandlaggare().getEmail())
                .withBehovTolk(utredning.getTolkBehov() != null)
                .withTolkSprak(utredning.getTolkSprak())
                .withStatus("TODO")
                .build();
    }

    public String getUtredningsId() {
        return utredningsId;
    }

    public void setUtredningsId(final String utredningsId) {
        this.utredningsId = utredningsId;
    }

    public String getUtredningsTyp() {
        return utredningsTyp;
    }

    public void setUtredningsTyp(final String utredningsTyp) {
        this.utredningsTyp = utredningsTyp;
    }

    public String getVardgivareHsaId() {
        return vardgivareHsaId;
    }

    public void setVardgivareHsaId(final String vardgivareHsaId) {
        this.vardgivareHsaId = vardgivareHsaId;
    }

    public String getInkomDatum() {
        return inkomDatum;
    }

    public void setInkomDatum(final String inkomDatum) {
        this.inkomDatum = inkomDatum;
    }

    public String getBesvarasSenastDatum() {
        return besvarasSenastDatum;
    }

    public void setBesvarasSenastDatum(final String besvarasSenastDatum) {
        this.besvarasSenastDatum = besvarasSenastDatum;
    }

    public String getInvanarePersonId() {
        return invanarePersonId;
    }

    public void setInvanarePersonId(final String invanarePersonId) {
        this.invanarePersonId = invanarePersonId;
    }

    public String getHandlaggareNamn() {
        return handlaggareNamn;
    }

    public void setHandlaggareNamn(final String handlaggareNamn) {
        this.handlaggareNamn = handlaggareNamn;
    }

    public String getHandlaggareTelefonnummer() {
        return handlaggareTelefonnummer;
    }

    public void setHandlaggareTelefonnummer(final String handlaggareTelefonnummer) {
        this.handlaggareTelefonnummer = handlaggareTelefonnummer;
    }

    public String getHandlaggareEpost() {
        return handlaggareEpost;
    }

    public void setHandlaggareEpost(final String handlaggareEpost) {
        this.handlaggareEpost = handlaggareEpost;
    }

    public boolean isBehovTolk() {
        return behovTolk;
    }

    public void setBehovTolk(final boolean behovTolk) {
        this.behovTolk = behovTolk;
    }

    public String getTolkSprak() {
        return tolkSprak;
    }

    public void setTolkSprak(final String tolkSprak) {
        this.tolkSprak = tolkSprak;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public static final class GetUtredningResponseBuilder {
        private static DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
        private String utredningsId;
        private String utredningsTyp;
        private String vardgivareHsaId;
        private String inkomDatum;
        private String besvarasSenastDatum;
        private String invanarePersonId;
        private String handlaggareNamn;
        private String handlaggareTelefonnummer;
        private String handlaggareEpost;
        private boolean behovTolk;
        private String tolkSprak;
        private String status;

        private GetUtredningResponseBuilder() {
        }

        public static GetUtredningResponseBuilder aGetUtredningResponse() {
            return new GetUtredningResponseBuilder();
        }

        public GetUtredningResponseBuilder withUtredningsId(String utredningsId) {
            this.utredningsId = utredningsId;
            return this;
        }

        public GetUtredningResponseBuilder withUtredningsTyp(String utredningsTyp) {
            this.utredningsTyp = utredningsTyp;
            return this;
        }

        public GetUtredningResponseBuilder withVardgivareHsaId(String vardgivareHsaId) {
            this.vardgivareHsaId = vardgivareHsaId;
            return this;
        }

        public GetUtredningResponseBuilder withInkomDatum(String inkomDatum) {
            this.inkomDatum = inkomDatum;
            return this;
        }

        public GetUtredningResponseBuilder withBesvarasSenastDatum(String besvarasSenastDatum) {
            this.besvarasSenastDatum = besvarasSenastDatum;
            return this;
        }

        public GetUtredningResponseBuilder withInvanarePersonId(String invanarePersonId) {
            this.invanarePersonId = invanarePersonId;
            return this;
        }

        public GetUtredningResponseBuilder withHandlaggareNamn(String handlaggareNamn) {
            this.handlaggareNamn = handlaggareNamn;
            return this;
        }

        public GetUtredningResponseBuilder withHandlaggareTelefonnummer(String handlaggareTelefonnummer) {
            this.handlaggareTelefonnummer = handlaggareTelefonnummer;
            return this;
        }

        public GetUtredningResponseBuilder withHandlaggareEpost(String handlaggareEpost) {
            this.handlaggareEpost = handlaggareEpost;
            return this;
        }

        public GetUtredningResponseBuilder withBehovTolk(boolean behovTolk) {
            this.behovTolk = behovTolk;
            return this;
        }

        public GetUtredningResponseBuilder withTolkSprak(String tolkSprak) {
            this.tolkSprak = tolkSprak;
            return this;
        }

        public GetUtredningResponseBuilder withStatus(String status) {
            this.status = status;
            return this;
        }

        public GetUtredningResponse build() {
            GetUtredningResponse getUtredningResponse = new GetUtredningResponse();
            getUtredningResponse.setUtredningsId(utredningsId);
            getUtredningResponse.setUtredningsTyp(utredningsTyp);
            getUtredningResponse.setVardgivareHsaId(vardgivareHsaId);
            getUtredningResponse.setInkomDatum(inkomDatum);
            getUtredningResponse.setBesvarasSenastDatum(besvarasSenastDatum);
            getUtredningResponse.setInvanarePersonId(invanarePersonId);
            getUtredningResponse.setHandlaggareNamn(handlaggareNamn);
            getUtredningResponse.setHandlaggareTelefonnummer(handlaggareTelefonnummer);
            getUtredningResponse.setHandlaggareEpost(handlaggareEpost);
            getUtredningResponse.setBehovTolk(behovTolk);
            getUtredningResponse.setTolkSprak(tolkSprak);
            getUtredningResponse.setStatus(status);
            return getUtredningResponse;
        }
    }
}
