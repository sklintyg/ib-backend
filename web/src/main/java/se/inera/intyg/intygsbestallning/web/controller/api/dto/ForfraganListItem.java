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

public class ForfraganListItem {
    private Long forfraganId;
    private String utredningsId;
    private String utredningsTyp;
    private String vardgivareNamn;
    private String inkomDatum;
    private String besvarasSenastDatum;
    private String planeringsDatum;
    private String status;

    public Long getForfraganId() {
        return forfraganId;
    }

    public void setForfraganId(Long forfraganId) {
        this.forfraganId = forfraganId;
    }

    public String getUtredningsId() {
        return utredningsId;
    }

    public void setUtredningsId(String utredningsId) {
        this.utredningsId = utredningsId;
    }

    public String getUtredningsTyp() {
        return utredningsTyp;
    }

    public void setUtredningsTyp(String utredningsTyp) {
        this.utredningsTyp = utredningsTyp;
    }

    public String getVardgivareNamn() {
        return vardgivareNamn;
    }

    public void setVardgivareNamn(String vardgivareNamn) {
        this.vardgivareNamn = vardgivareNamn;
    }

    public String getInkomDatum() {
        return inkomDatum;
    }

    public void setInkomDatum(String inkomDatum) {
        this.inkomDatum = inkomDatum;
    }

    public String getBesvarasSenastDatum() {
        return besvarasSenastDatum;
    }

    public void setBesvarasSenastDatum(String besvarasSenastDatum) {
        this.besvarasSenastDatum = besvarasSenastDatum;
    }

    public String getPlaneringsDatum() {
        return planeringsDatum;
    }

    public void setPlaneringsDatum(String planeringsDatum) {
        this.planeringsDatum = planeringsDatum;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}

