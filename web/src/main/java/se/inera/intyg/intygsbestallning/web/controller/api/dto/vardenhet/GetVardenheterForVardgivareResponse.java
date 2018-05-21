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

import se.inera.intyg.intygsbestallning.service.vardgivare.dto.VardenhetItem;

import java.util.ArrayList;
import java.util.List;

public class GetVardenheterForVardgivareResponse {
    private List<VardenhetItem> egetLandsting = new ArrayList<>();
    private List<VardenhetItem> annatLandsting = new ArrayList<>();
    private List<VardenhetItem> privat = new ArrayList<>();

    public GetVardenheterForVardgivareResponse() {

    }

    public List<VardenhetItem> getEgetLandsting() {
        return egetLandsting;
    }

    public void setEgetLandsting(List<VardenhetItem> egetLandsting) {
        this.egetLandsting = egetLandsting;
    }

    public List<VardenhetItem> getAnnatLandsting() {
        return annatLandsting;
    }

    public void setAnnatLandsting(List<VardenhetItem> annatLandsting) {
        this.annatLandsting = annatLandsting;
    }

    public List<VardenhetItem> getPrivat() {
        return privat;
    }

    public void setPrivat(List<VardenhetItem> privat) {
        this.privat = privat;
    }
}
