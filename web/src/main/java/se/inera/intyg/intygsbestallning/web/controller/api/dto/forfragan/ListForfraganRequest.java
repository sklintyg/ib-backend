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

import se.inera.intyg.intygsbestallning.web.controller.api.dto.ListRequest;

public class ListForfraganRequest extends ListRequest {

    private String freeText;
    private String status;
    private String vardgivareHsaId;

    private String inkommetFromDate;
    private String inkommetToDate;

    private String besvarasSenastDatumFromDate;
    private String besvarasSenastDatumToDate;

    private String planeringFromDate;
    private String planeringToDate;

    public String getFreeText() {
        return freeText;
    }

    public void setFreeText(String freeText) {
        this.freeText = freeText;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVardgivareHsaId() {
        return vardgivareHsaId;
    }

    public void setVardgivareHsaId(String vardgivareHsaId) {
        this.vardgivareHsaId = vardgivareHsaId;
    }

    public String getInkommetFromDate() {
        return inkommetFromDate;
    }

    public void setInkommetFromDate(String inkommetFromDate) {
        this.inkommetFromDate = inkommetFromDate;
    }

    public String getInkommetToDate() {
        return inkommetToDate;
    }

    public void setInkommetToDate(String inkommetToDate) {
        this.inkommetToDate = inkommetToDate;
    }


    public String getBesvarasSenastDatumFromDate() {
        return besvarasSenastDatumFromDate;
    }

    public void setBesvarasSenastDatumFromDate(String besvarasSenastDatumFromDate) {
        this.besvarasSenastDatumFromDate = besvarasSenastDatumFromDate;
    }

    public String getBesvarasSenastDatumToDate() {
        return besvarasSenastDatumToDate;
    }

    public void setBesvarasSenastDatumToDate(String besvarasSenastDatumToDate) {
        this.besvarasSenastDatumToDate = besvarasSenastDatumToDate;
    }

    public String getPlaneringFromDate() {
        return planeringFromDate;
    }

    public void setPlaneringFromDate(String planeringFromDate) {
        this.planeringFromDate = planeringFromDate;
    }

    public String getPlaneringToDate() {
        return planeringToDate;
    }

    public void setPlaneringToDate(String planeringToDate) {
        this.planeringToDate = planeringToDate;
    }
}
