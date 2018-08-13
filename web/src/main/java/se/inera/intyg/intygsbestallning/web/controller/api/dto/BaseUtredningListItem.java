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

import com.fasterxml.jackson.annotation.JsonIgnore;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.type.UtredningsTyp;

public class BaseUtredningListItem implements VardenhetEnrichable {
    protected Utredning utredning;
    protected Long utredningsId;
    protected UtredningsTyp utredningsTyp;
    protected String vardenhetHsaId;
    protected String vardenhetNamn;

    @JsonIgnore
    public Utredning getUtredning() {
        return utredning;
    }

    public void setUtredning(Utredning utredning) {
        this.utredning = utredning;
    }

    public Long getUtredningsId() {
        return utredningsId;
    }

    public void setUtredningsId(Long utredningsId) {
        this.utredningsId = utredningsId;
    }

    public UtredningsTyp getUtredningsTyp() {
        return utredningsTyp;
    }

    public void setUtredningsTyp(UtredningsTyp utredningsTyp) {
        this.utredningsTyp = utredningsTyp;
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

    @Override
    public void setVardenhetFelmeddelande(String vardenhetFelmeddelande) {
    }
}
