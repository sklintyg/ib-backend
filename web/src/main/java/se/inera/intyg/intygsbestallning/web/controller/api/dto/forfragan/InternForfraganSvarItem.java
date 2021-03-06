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
package se.inera.intyg.intygsbestallning.web.controller.api.dto.forfragan;

import static java.util.Objects.nonNull;

import java.time.format.DateTimeFormatter;

import se.inera.intyg.intygsbestallning.persistence.model.InternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.type.SvarTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.UtforareTyp;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.vardenhet.VardenhetPreferenceResponse;

/**
 * Created by marced on 2018-05-21.
 */
public class InternForfraganSvarItem {

    private static DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
    private Long forfraganId;
    private SvarTyp svarTyp;
    private UtforareTyp utforareTyp;
    private String utforareNamn;
    private String utforareAdress;
    private String utforarePostnr;
    private String utforarePostort;
    private String utforareTelefon;
    private String utforareEpost;
    private String kommentar;
    private String borjaDatum;

    public static InternForfraganSvarItem from(InternForfragan internForfragan) {
        if (internForfragan == null || internForfragan.getForfraganSvar() == null) {
            return null;
        }

        return InternForfraganSvarItem.InternForfraganSvarItemBuilder.aInternForfraganSvarItem()
                .withForfraganId(internForfragan.getId())
                .withSvarTyp(internForfragan.getForfraganSvar().getSvarTyp())
                .withUtforareTyp(internForfragan.getForfraganSvar().getUtforareTyp())
                .withUtforareNamn(internForfragan.getForfraganSvar().getUtforareNamn())
                .withUtforareAdress(internForfragan.getForfraganSvar().getUtforareAdress())
                .withUtforarePostnr(internForfragan.getForfraganSvar().getUtforarePostnr())
                .withUtforarePostort(internForfragan.getForfraganSvar().getUtforarePostort())
                .withUtforareTelefon(internForfragan.getForfraganSvar().getUtforareTelefon())
                .withUtforareEpost(internForfragan.getForfraganSvar().getUtforareEpost())
                .withKommentar(internForfragan.getForfraganSvar().getKommentar())
                .withBorjaDatum(nonNull(internForfragan.getForfraganSvar().getBorjaDatum())
                        ? internForfragan.getForfraganSvar().getBorjaDatum().format(formatter)
                        : null)
                .build();
    }

    /**
     * Creates a InternForfraganSvarItem based on a vardenhetPreference. This is used as a fallback to simplify editing of
     * InternForfraganSvar.
     *
     * @param internForfragan
     * @param vardenhetPreference
     * @return InternForfraganSvarItem based on the vardpreference rather that a persitent entity.
     */
    public static InternForfraganSvarItem from(InternForfragan internForfragan, VardenhetPreferenceResponse vardenhetPreference) {
        if (internForfragan == null) {
            return null;
        }

        return InternForfraganSvarItem.InternForfraganSvarItemBuilder.aInternForfraganSvarItem()
                .withForfraganId(internForfragan.getId())
                .withSvarTyp(null)
                .withUtforareTyp(UtforareTyp.ENHET) // default
                .withUtforareNamn(vardenhetPreference.getMottagarNamn())
                .withUtforareAdress(vardenhetPreference.getAdress())
                .withUtforarePostnr(vardenhetPreference.getPostnummer())
                .withUtforarePostort(vardenhetPreference.getPostort())
                .withUtforareTelefon(vardenhetPreference.getTelefonnummer())
                .withUtforareEpost(vardenhetPreference.getEpost())
                .withKommentar(vardenhetPreference.getStandardsvar())
                .withBorjaDatum(null)
                .build();
    }

    public static DateTimeFormatter getFormatter() {
        return formatter;
    }

    public static void setFormatter(DateTimeFormatter formatter) {
        InternForfraganSvarItem.formatter = formatter;
    }

    public SvarTyp getSvarTyp() {
        return svarTyp;
    }

    public void setSvarTyp(SvarTyp svarTyp) {
        this.svarTyp = svarTyp;
    }

    public UtforareTyp getUtforareTyp() {
        return utforareTyp;
    }

    public void setUtforareTyp(UtforareTyp utforareTyp) {
        this.utforareTyp = utforareTyp;
    }

    public String getUtforareNamn() {
        return utforareNamn;
    }

    public void setUtforareNamn(String utforareNamn) {
        this.utforareNamn = utforareNamn;
    }

    public String getUtforareAdress() {
        return utforareAdress;
    }

    public void setUtforareAdress(String utforareAdress) {
        this.utforareAdress = utforareAdress;
    }

    public String getUtforarePostnr() {
        return utforarePostnr;
    }

    public void setUtforarePostnr(String utforarePostnr) {
        this.utforarePostnr = utforarePostnr;
    }

    public String getUtforarePostort() {
        return utforarePostort;
    }

    public void setUtforarePostort(String utforarePostort) {
        this.utforarePostort = utforarePostort;
    }

    public String getUtforareTelefon() {
        return utforareTelefon;
    }

    public void setUtforareTelefon(String utforareTelefon) {
        this.utforareTelefon = utforareTelefon;
    }

    public String getUtforareEpost() {
        return utforareEpost;
    }

    public void setUtforareEpost(String utforareEpost) {
        this.utforareEpost = utforareEpost;
    }

    public String getKommentar() {
        return kommentar;
    }

    public void setKommentar(String kommentar) {
        this.kommentar = kommentar;
    }

    public String getBorjaDatum() {
        return borjaDatum;
    }

    public void setBorjaDatum(String borjaDatum) {
        this.borjaDatum = borjaDatum;
    }

    public Long getForfraganId() {
        return forfraganId;
    }

    public void setForfraganId(Long forfraganId) {
        this.forfraganId = forfraganId;
    }

    public static final class InternForfraganSvarItemBuilder {

        private Long forfraganId;
        private SvarTyp svarTyp;
        private UtforareTyp utforareTyp;
        private String utforareNamn;
        private String utforareAdress;
        private String utforarePostnr;
        private String utforarePostort;
        private String utforareTelefon;
        private String utforareEpost;
        private String kommentar;
        private String borjaDatum;

        private InternForfraganSvarItemBuilder() {
        }

        public static InternForfraganSvarItemBuilder aInternForfraganSvarItem() {
            return new InternForfraganSvarItemBuilder();
        }

        public InternForfraganSvarItemBuilder withForfraganId(Long forfraganId) {
            this.forfraganId = forfraganId;
            return this;
        }

        public InternForfraganSvarItemBuilder withSvarTyp(SvarTyp svarTyp) {
            this.svarTyp = svarTyp;
            return this;
        }

        public InternForfraganSvarItemBuilder withUtforareTyp(UtforareTyp utforareTyp) {
            this.utforareTyp = utforareTyp;
            return this;
        }

        public InternForfraganSvarItemBuilder withUtforareNamn(String utforareNamn) {
            this.utforareNamn = utforareNamn;
            return this;
        }

        public InternForfraganSvarItemBuilder withUtforareAdress(String utforareAdress) {
            this.utforareAdress = utforareAdress;
            return this;
        }

        public InternForfraganSvarItemBuilder withUtforarePostnr(String utforarePostnr) {
            this.utforarePostnr = utforarePostnr;
            return this;
        }

        public InternForfraganSvarItemBuilder withUtforarePostort(String utforarePostort) {
            this.utforarePostort = utforarePostort;
            return this;
        }

        public InternForfraganSvarItemBuilder withUtforareTelefon(String utforareTelefon) {
            this.utforareTelefon = utforareTelefon;
            return this;
        }

        public InternForfraganSvarItemBuilder withUtforareEpost(String utforareEpost) {
            this.utforareEpost = utforareEpost;
            return this;
        }

        public InternForfraganSvarItemBuilder withKommentar(String kommentar) {
            this.kommentar = kommentar;
            return this;
        }

        public InternForfraganSvarItemBuilder withBorjaDatum(String borjaDatum) {
            this.borjaDatum = borjaDatum;
            return this;
        }

        public InternForfraganSvarItem build() {
            InternForfraganSvarItem internForfraganSvarItem = new InternForfraganSvarItem();
            internForfraganSvarItem.setForfraganId(forfraganId);
            internForfraganSvarItem.setSvarTyp(svarTyp);
            internForfraganSvarItem.setUtforareTyp(utforareTyp);
            internForfraganSvarItem.setUtforareNamn(utforareNamn);
            internForfraganSvarItem.setUtforareAdress(utforareAdress);
            internForfraganSvarItem.setUtforarePostnr(utforarePostnr);
            internForfraganSvarItem.setUtforarePostort(utforarePostort);
            internForfraganSvarItem.setUtforareTelefon(utforareTelefon);
            internForfraganSvarItem.setUtforareEpost(utforareEpost);
            internForfraganSvarItem.setKommentar(kommentar);
            internForfraganSvarItem.setBorjaDatum(borjaDatum);

            return internForfraganSvarItem;
        }

    }
}
