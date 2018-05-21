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
package se.inera.intyg.intygsbestallning.web.controller.api.dto.vardenhet;

import se.inera.intyg.intygsbestallning.web.controller.api.dto.VardenhetEnrichable;

public class VardenhetListItem implements VardenhetEnrichable {
    private String vardenhetHsaId;
    private String vardenhetNamn;
    private String vardenhetFelmeddelande;

    public VardenhetListItem(String vardenhetHsaId) {
        this.vardenhetHsaId = vardenhetHsaId;
    }

    @Override
    public String getVardenhetHsaId() {
        return vardenhetHsaId;
    }

    public void setVardenhetHsaId(String vardenhetHsaId) {
        this.vardenhetHsaId = vardenhetHsaId;
    }

    public String getVardenhetNamn() {
        return vardenhetNamn;
    }

    @Override
    public void setVardenhetNamn(String vardenhetNamn) {
        this.vardenhetNamn = vardenhetNamn;
    }

    public String getVardenhetFelmeddelande() {
        return vardenhetFelmeddelande;
    }

    @Override
    public void setVardenhetFelmeddelande(String vardenhetFelmeddelande) {
        this.vardenhetFelmeddelande = vardenhetFelmeddelande;
    }
}
