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

/**
 * avslutad vårdkontakt = 53671000052101  inställd vårdkontakt = 53641000052109 tidbokad vårdkontakt = 53631000052103.
 */
public enum BesokStatusTyp {
    AVSLUTAD_VARDKONTAKT("53671000052101"), INSTALLD_VARDKONTAKT("53641000052109"), TIDBOKAD_VARDKONTAKT("53631000052103");

    private final String cvValue;

    BesokStatusTyp(String cvValue) {
        this.cvValue = cvValue;
    }

    public String getCvValue() {
        return cvValue;
    }
}
