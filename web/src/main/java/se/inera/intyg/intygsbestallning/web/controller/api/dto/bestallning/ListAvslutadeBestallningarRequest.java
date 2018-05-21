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
package se.inera.intyg.intygsbestallning.web.controller.api.dto.bestallning;

import se.inera.intyg.intygsbestallning.web.controller.api.dto.ListRequest;

public class ListAvslutadeBestallningarRequest extends ListRequest {

    private String freeText;
    private String vardgivareHsaId;
    private String avslutsDatumFromDate;
    private String avslutsDatumToDate;
    private String ersatts;
    private String fakturerad;
    private String utbetald;

    public String getFreeText() {
        return freeText;
    }

    public void setFreeText(String freeText) {
        this.freeText = freeText;
    }

    public String getVardgivareHsaId() {
        return vardgivareHsaId;
    }

    public void setVardgivareHsaId(String vardgivareHsaId) {
        this.vardgivareHsaId = vardgivareHsaId;
    }

    public String getAvslutsDatumFromDate() {
        return avslutsDatumFromDate;
    }

    public void setAvslutsDatumFromDate(String avslutsDatumFromDate) {
        this.avslutsDatumFromDate = avslutsDatumFromDate;
    }

    public String getAvslutsDatumToDate() {
        return avslutsDatumToDate;
    }

    public void setAvslutsDatumToDate(String avslutsDatumToDate) {
        this.avslutsDatumToDate = avslutsDatumToDate;
    }

    public String getErsatts() {
        return ersatts;
    }

    public void setErsatts(String ersatts) {
        this.ersatts = ersatts;
    }

    public String getFakturerad() {
        return fakturerad;
    }

    public void setFakturerad(String fakturerad) {
        this.fakturerad = fakturerad;
    }

    public String getUtbetald() {
        return utbetald;
    }

    public void setUtbetald(String utbetald) {
        this.utbetald = utbetald;
    }
}
