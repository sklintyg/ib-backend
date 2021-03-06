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
package se.inera.intyg.intygsbestallning.persistence.model.type;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum KallelseFormTyp {
    TELEFONKONTAKT("185317003", "Telefonkontakt"),
    BREVKONTAKT("308720009", "Brevkontakt");

    private final String id;
    private final String cvValue;
    private final String label;

    KallelseFormTyp(String cvValue, String label) {
        this.id = name();
        this.cvValue = cvValue;
        this.label = label;
    }

    public String getId() {
        return id;
    }

    public String getCvValue() {
        return cvValue;
    }

    public String getLabel() {
        return label;
    }
}
