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

import static com.google.common.base.Preconditions.checkArgument;

import java.text.MessageFormat;

public enum MyndighetTyp {

    AF("Arbetsförmedlingen", false),
    FKASSA("Försäkringskassan", true),
    HSVARD("Hälso- och sjukvården", false),
    INVANA("Invånaren", false),
    TRANSP("Transportstyrelsen", false),
    SOS("Socialstyrelsen", false),
    SK("Skatteverket", false);

    private final String description;
    private final boolean active;

    MyndighetTyp(final String description, final boolean active) {
        this.description = description;
        this.active = active;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return active;
    }

    public static MyndighetTyp of(final String input) {
        final MyndighetTyp myndighetTyp = MyndighetTyp.valueOf(input);
        checkArgument(myndighetTyp.isActive(), MessageFormat.format(
                "Authority with code {0} ({1}) is not active", myndighetTyp, myndighetTyp.getDescription()));
        return myndighetTyp;
    }
}
