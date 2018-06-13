package se.inera.intyg.intygsbestallning.web.controller.api.dto.besok;

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

import se.inera.intyg.intygsbestallning.persistence.model.type.AvvikelseOrsak;

import java.time.LocalDate;
import java.time.LocalTime;

public final class ReportBesokAvvikelseVardenRequest {

    private AvvikelseOrsak orsakatAv;
    private String beskrivning;
    private LocalDate datum;
    private LocalTime tid;
    private Boolean invanareUteblev;

    public AvvikelseOrsak getOrsakatAv() {
        return orsakatAv;
    }

    public void setOrsakatAv(AvvikelseOrsak orsakatAv) {
        this.orsakatAv = orsakatAv;
    }

    public String getBeskrivning() {
        return beskrivning;
    }

    public void setBeskrivning(String beskrivning) {
        this.beskrivning = beskrivning;
    }

    public LocalDate getDatum() {
        return datum;
    }

    public void setDatum(LocalDate datum) {
        this.datum = datum;
    }

    public LocalTime getTid() {
        return tid;
    }

    public void setTid(LocalTime tid) {
        this.tid = tid;
    }

    public Boolean getInvanareUteblev() {
        return invanareUteblev;
    }

    public void setInvanareUteblev(Boolean invanareUteblev) {
        this.invanareUteblev = invanareUteblev;
    }
}

