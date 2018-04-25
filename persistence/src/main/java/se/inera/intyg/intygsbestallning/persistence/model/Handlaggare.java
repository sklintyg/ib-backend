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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "HANDLAGGARE")
public class Handlaggare {
    @Id
    @GeneratedValue
    @Column(name = "ID")
    private long id;

    @Column(name = "FULLSTANDIGT_NAMN")
    private String fullstandigtNamn;

    @Column(name = "TELEFONNUMMER")
    private String telefonnummer;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "MYNDIGHET")
    private String myndighet;

    @Column(name = "KONTOR")
    private String kontor;

    @Column(name = "KOSTNADSSTALLE")
    private String kostnadsstalle;

    @Column(name = "ADRESS")
    private String adress;

    @Column(name = "POSTKOD")
    private String postkod;

    @Column(name = "STAD")
    private String stad;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFullstandigtNamn() {
        return fullstandigtNamn;
    }

    public void setFullstandigtNamn(String fullstandigtNamn) {
        this.fullstandigtNamn = fullstandigtNamn;
    }

    public String getTelefonnummer() {
        return telefonnummer;
    }

    public void setTelefonnummer(String telefonnummer) {
        this.telefonnummer = telefonnummer;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMyndighet() {
        return myndighet;
    }

    public void setMyndighet(String myndighet) {
        this.myndighet = myndighet;
    }

    public String getKontor() {
        return kontor;
    }

    public void setKontor(String kontor) {
        this.kontor = kontor;
    }

    public String getKostnadsstalle() {
        return kostnadsstalle;
    }

    public void setKostnadsstalle(String kostnadsstalle) {
        this.kostnadsstalle = kostnadsstalle;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getPostkod() {
        return postkod;
    }

    public void setPostkod(String postkod) {
        this.postkod = postkod;
    }

    public String getStad() {
        return stad;
    }

    public void setStad(String stad) {
        this.stad = stad;
    }

    public static final class HandlaggareBuilder {
        private String fullstandigtNamn;
        private String telefonnummer;
        private String email;
        private String myndighet;
        private String kontor;
        private String kostnadsstalle;
        private String adress;
        private String postkod;
        private String stad;

        private HandlaggareBuilder() {
        }

        public static HandlaggareBuilder aHandlaggare() {
            return new HandlaggareBuilder();
        }

        public HandlaggareBuilder withFullstandigtNamn(String fullstandigtNamn) {
            this.fullstandigtNamn = fullstandigtNamn;
            return this;
        }

        public HandlaggareBuilder withTelefonnummer(String telefonnummer) {
            this.telefonnummer = telefonnummer;
            return this;
        }

        public HandlaggareBuilder withEmail(String email) {
            this.email = email;
            return this;
        }

        public HandlaggareBuilder withMyndighet(String authority) {
            this.myndighet = authority;
            return this;
        }

        public HandlaggareBuilder withKontor(String kontor) {
            this.kontor = kontor;
            return this;
        }

        public HandlaggareBuilder withKostnadsstalle(String kostnadsstalle) {
            this.kostnadsstalle = kostnadsstalle;
            return this;
        }

        public HandlaggareBuilder withAdress(String adress) {
            this.adress = adress;
            return this;
        }

        public HandlaggareBuilder withPostkod(String postkod) {
            this.postkod = postkod;
            return this;
        }

        public HandlaggareBuilder withStad(String stad) {
            this.stad = stad;
            return this;
        }

        public Handlaggare build() {
            Handlaggare handlaggare = new Handlaggare();
            handlaggare.setFullstandigtNamn(fullstandigtNamn);
            handlaggare.setTelefonnummer(telefonnummer);
            handlaggare.setEmail(email);
            handlaggare.setMyndighet(myndighet);
            handlaggare.setKontor(kontor);
            handlaggare.setKostnadsstalle(kostnadsstalle);
            handlaggare.setAdress(adress);
            handlaggare.setPostkod(postkod);
            handlaggare.setStad(stad);
            return handlaggare;
        }
    }
}