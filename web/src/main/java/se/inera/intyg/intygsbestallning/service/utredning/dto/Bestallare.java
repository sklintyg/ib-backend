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
package se.inera.intyg.intygsbestallning.service.utredning.dto;

public final class Bestallare {
    private String fullstandigtNamn;
    private String telefonnummer;
    private String email;
    private String myndighet;
    private String kontor;
    private String kostnadsstalle;
    private String adress;
    private String postkod;
    private String stad;

    private Bestallare() {
    }

    public String getFullstandigtNamn() {
        return fullstandigtNamn;
    }

    public String getTelefonnummer() {
        return telefonnummer;
    }

    public String getEmail() {
        return email;
    }

    public String getMyndighet() {
        return myndighet;
    }

    public String getKontor() {
        return kontor;
    }

    public String getKostnadsstalle() {
        return kostnadsstalle;
    }

    public String getAdress() {
        return adress;
    }

    public String getPostkod() {
        return postkod;
    }

    public String getStad() {
        return stad;
    }

    protected static final class BestallareBuilder {
        private String fullstandigtNamn;
        private String telefonnummer;
        private String email;
        private String myndighet;
        private String kontor;
        private String kostnadsstalle;
        private String adress;
        private String postkod;
        private String stad;

        private BestallareBuilder() {
        }

        public static BestallareBuilder aBestallare() {
            return new BestallareBuilder();
        }

        public BestallareBuilder withFullstandigtNamn(String fullName) {
            this.fullstandigtNamn = fullName;
            return this;
        }

        public BestallareBuilder withTelefonnummer(String phoneNumber) {
            this.telefonnummer = phoneNumber;
            return this;
        }

        public BestallareBuilder withEmail(String email) {
            this.email = email;
            return this;
        }

        public BestallareBuilder withMyndighet(String myndighet) {
            this.myndighet = myndighet;
            return this;
        }

        public BestallareBuilder withKontor(String officeName) {
            this.kontor = officeName;
            return this;
        }

        public BestallareBuilder withKostnadsstalle(String kostnadsstalle) {
            this.kostnadsstalle = kostnadsstalle;
            return this;
        }

        public BestallareBuilder withAdress(String postalAddress) {
            this.adress = postalAddress;
            return this;
        }

        public BestallareBuilder withPostkod(String postalCode) {
            this.postkod = postalCode;
            return this;
        }

        public BestallareBuilder withStad(String postalCity) {
            this.stad = postalCity;
            return this;
        }

        public Bestallare build() {
            Bestallare bestallare = new Bestallare();
            bestallare.telefonnummer = this.telefonnummer;
            bestallare.email = this.email;
            bestallare.stad = this.stad;
            bestallare.postkod = this.postkod;
            bestallare.myndighet = this.myndighet;
            bestallare.kontor = this.kontor;
            bestallare.adress = this.adress;
            bestallare.fullstandigtNamn = this.fullstandigtNamn;
            bestallare.kostnadsstalle = this.kostnadsstalle;
            return bestallare;
        }
    }
}
