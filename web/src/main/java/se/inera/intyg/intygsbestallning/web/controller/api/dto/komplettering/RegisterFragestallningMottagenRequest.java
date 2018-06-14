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
package se.inera.intyg.intygsbestallning.web.controller.api.dto.komplettering;

import java.time.LocalDate;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.nonNull;

public class RegisterFragestallningMottagenRequest {
    private LocalDate fragestallningMottagenDatum;

    public LocalDate getFragestallningMottagenDatum() {
        return fragestallningMottagenDatum;
    }

    public void setFragestallningMottagenDatum(LocalDate fragestallningMottagenDatum) {
        this.fragestallningMottagenDatum = fragestallningMottagenDatum;
    }

    public void validate() {
        checkArgument(nonNull(fragestallningMottagenDatum), "fragestallningMottagenDatum may not be null");
    }
}
