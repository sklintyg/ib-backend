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

    public static final class BestallareBuilder {
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

        public BestallareBuilder withFullstandigtNamn(String fullstandigtNamn) {
            this.fullstandigtNamn = fullstandigtNamn;
            return this;
        }

        public BestallareBuilder withTelefonnummer(String telefonnummer) {
            this.telefonnummer = telefonnummer;
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

        public BestallareBuilder withKontor(String kontor) {
            this.kontor = kontor;
            return this;
        }

        public BestallareBuilder withKostnadsstalle(String kostnadsstalle) {
            this.kostnadsstalle = kostnadsstalle;
            return this;
        }

        public BestallareBuilder withAdress(String adress) {
            this.adress = adress;
            return this;
        }

        public BestallareBuilder withPostkod(String postkod) {
            this.postkod = postkod;
            return this;
        }

        public BestallareBuilder withStad(String stad) {
            this.stad = stad;
            return this;
        }

        public Bestallare build() {
            Bestallare bestallare = new Bestallare();
            bestallare.telefonnummer = this.telefonnummer;
            bestallare.kontor = this.kontor;
            bestallare.kostnadsstalle = this.kostnadsstalle;
            bestallare.postkod = this.postkod;
            bestallare.stad = this.stad;
            bestallare.fullstandigtNamn = this.fullstandigtNamn;
            bestallare.email = this.email;
            bestallare.myndighet = this.myndighet;
            bestallare.adress = this.adress;
            return bestallare;
        }
    }

    //CHECKSTYLE:OFF OperatorWrap

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Bestallare that = (Bestallare) o;
        return java.util.Objects.equals(fullstandigtNamn, that.fullstandigtNamn) &&
                java.util.Objects.equals(telefonnummer, that.telefonnummer) &&
                java.util.Objects.equals(email, that.email) &&
                java.util.Objects.equals(myndighet, that.myndighet) &&
                java.util.Objects.equals(kontor, that.kontor) &&
                java.util.Objects.equals(kostnadsstalle, that.kostnadsstalle) &&
                java.util.Objects.equals(adress, that.adress) &&
                java.util.Objects.equals(postkod, that.postkod) &&
                java.util.Objects.equals(stad, that.stad);
    }

    @Override
    public int hashCode() {

        return java.util.Objects.hash(fullstandigtNamn, telefonnummer, email, myndighet, kontor, kostnadsstalle, adress, postkod, stad);
    }

    //CHECKSTYLE:ON OperatorWrap
}
