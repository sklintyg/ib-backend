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
package se.inera.intyg.intygsbestallning.persistence.model;

import static java.util.Objects.isNull;
import static se.inera.intyg.intygsbestallning.persistence.model.ForfraganSvar.ForfraganSvarBuilder.aForfraganSvar;

import org.hibernate.annotations.Type;
import se.inera.intyg.intygsbestallning.persistence.model.type.SvarTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.UtforareTyp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "FORFRAGAN_SVAR")
public class ForfraganSvar {

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private long id;

    @Column(name = "SVAR_TYP", nullable = false)
    @Enumerated(EnumType.STRING)
    private SvarTyp svarTyp;

    @Enumerated(EnumType.STRING)
    @Column(name = "UTFORARE_TYP", nullable = false)
    private UtforareTyp utforareTyp;

    @Column(name = "UTFORARE_NAMN", nullable = false)
    private String utforareNamn;

    @Column(name = "UTFORARE_ADRESS", nullable = false)
    private String utforareAdress;

    @Column(name = "UTFORARE_POSTNUMMER", nullable = false)
    private String utforarePostnr;

    @Column(name = "UTFORARE_POSTORT", nullable = false)
    private String utforarePostort;

    @Column(name = "UTFORARE_TELEFONNUMMER")
    private String utforareTelefon;

    @Column(name = "UTFORARE_EPOST")
    private String utforareEpost;

    @Column(name = "KOMMENTAR")
    private String kommentar;

    @Column(name = "BORJA_DATUM")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    private LocalDate borjaDatum;

    public ForfraganSvar() {

    }

    public static ForfraganSvar from(final ForfraganSvar forfraganSvar) {

        if (isNull(forfraganSvar)) {
            return null;
        }

        return aForfraganSvar()
                .withId(forfraganSvar.getId())
                .withSvarTyp(forfraganSvar.getSvarTyp())
                .withUtforareTyp(forfraganSvar.getUtforareTyp())
                .withUtforareNamn(forfraganSvar.getUtforareNamn())
                .withUtforareAdress(forfraganSvar.getUtforareAdress())
                .withUtforarePostnr(forfraganSvar.getUtforarePostnr())
                .withUtforarePostort(forfraganSvar.getUtforarePostort())
                .withUtforareTelefon(forfraganSvar.getUtforareTelefon())
                .withUtforareEpost(forfraganSvar.getUtforareEpost())
                .withKommentar(forfraganSvar.getKommentar())
                .withBorjaDatum(forfraganSvar.getBorjaDatum())
                .build();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public SvarTyp getSvarTyp() {
        return svarTyp;
    }

    public void setSvarTyp(SvarTyp svarTyp) {
        this.svarTyp = svarTyp;
    }

    public String getKommentar() {
        return kommentar;
    }

    public void setKommentar(String kommentar) {
        this.kommentar = kommentar;
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

    public LocalDate getBorjaDatum() {
        return borjaDatum;
    }

    public void setBorjaDatum(LocalDate borjaDatum) {
        this.borjaDatum = borjaDatum;
    }

    public static final class ForfraganSvarBuilder {
        private long id;
        private SvarTyp svarTyp;
        private UtforareTyp utforareTyp;
        private String utforareNamn;
        private String utforareAdress;
        private String utforarePostnr;
        private String utforarePostort;
        private String utforareTelefon;
        private String utforareEpost;
        private String kommentar;
        private LocalDate borjaDatum;

        private ForfraganSvarBuilder() {
        }

        public static ForfraganSvarBuilder aForfraganSvar() {
            return new ForfraganSvarBuilder();
        }

        public ForfraganSvarBuilder withId(long id) {
            this.id = id;
            return this;
        }

        public ForfraganSvarBuilder withSvarTyp(SvarTyp svarTyp) {
            this.svarTyp = svarTyp;
            return this;
        }

        public ForfraganSvarBuilder withUtforareTyp(UtforareTyp utforareTyp) {
            this.utforareTyp = utforareTyp;
            return this;
        }

        public ForfraganSvarBuilder withUtforareNamn(String utforareNamn) {
            this.utforareNamn = utforareNamn;
            return this;
        }

        public ForfraganSvarBuilder withUtforareAdress(String utforareAdress) {
            this.utforareAdress = utforareAdress;
            return this;
        }

        public ForfraganSvarBuilder withUtforarePostnr(String utforarePostnr) {
            this.utforarePostnr = utforarePostnr;
            return this;
        }

        public ForfraganSvarBuilder withUtforarePostort(String utforarePostort) {
            this.utforarePostort = utforarePostort;
            return this;
        }

        public ForfraganSvarBuilder withUtforareTelefon(String utforareTelefon) {
            this.utforareTelefon = utforareTelefon;
            return this;
        }

        public ForfraganSvarBuilder withUtforareEpost(String utforareEpost) {
            this.utforareEpost = utforareEpost;
            return this;
        }

        public ForfraganSvarBuilder withKommentar(String kommentar) {
            this.kommentar = kommentar;
            return this;
        }

        public ForfraganSvarBuilder withBorjaDatum(LocalDate borjaDatum) {
            this.borjaDatum = borjaDatum;
            return this;
        }

        public ForfraganSvar build() {
            ForfraganSvar forfraganSvar = new ForfraganSvar();
            forfraganSvar.setId(id);
            forfraganSvar.setSvarTyp(svarTyp);
            forfraganSvar.setUtforareTyp(utforareTyp);
            forfraganSvar.setUtforareNamn(utforareNamn);
            forfraganSvar.setUtforareAdress(utforareAdress);
            forfraganSvar.setUtforarePostnr(utforarePostnr);
            forfraganSvar.setUtforarePostort(utforarePostort);
            forfraganSvar.setUtforareTelefon(utforareTelefon);
            forfraganSvar.setUtforareEpost(utforareEpost);
            forfraganSvar.setKommentar(kommentar);
            forfraganSvar.setBorjaDatum(borjaDatum);
            return forfraganSvar;
        }
    }
}
