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

public class ForfraganSvarRequest {

    private Long forfraganId;
    private String svarTyp;
    private String utforareTyp;

    private String utforareNamn;

    private String utforareAdress;

    private String utforarePostnr;

    private String utforarePostort;

    private String utforareTelefon;

    private String utforareEpost;

    private String kommentar;

    public Long getForfraganId() {
        return forfraganId;
    }

    public void setForfraganId(Long forfraganId) {
        this.forfraganId = forfraganId;
    }

    public String getSvarTyp() {
        return svarTyp;
    }

    public void setSvarTyp(String svarTyp) {
        this.svarTyp = svarTyp;
    }

    public String getUtforareTyp() {
        return utforareTyp;
    }

    public void setUtforareTyp(String utforareTyp) {
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
}
