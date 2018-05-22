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

import se.inera.intyg.intygsbestallning.web.controller.api.dto.utredning.GetUtredningResponse;

public class GetInternForfraganResponse {

    private InternForfraganListItem internForfragan;

    private InternForfraganSvarItem internForfraganSvar;

    private GetUtredningResponse utredning;

    private GetInternForfraganResponse() {

    }

    public GetInternForfraganResponse(InternForfraganListItem internForfragan, InternForfraganSvarItem internForfraganSvar,
            GetUtredningResponse utredning) {
        this.internForfragan = internForfragan;
        this.internForfraganSvar = internForfraganSvar;
        this.utredning = utredning;
    }

    public InternForfraganListItem getInternForfragan() {
        return internForfragan;
    }

    public void setInternForfragan(InternForfraganListItem internForfragan) {
        this.internForfragan = internForfragan;
    }

    public GetUtredningResponse getUtredning() {
        return utredning;
    }

    public void setUtredning(GetUtredningResponse utredning) {
        this.utredning = utredning;
    }

    public InternForfraganSvarItem getInternForfraganSvar() {
        return internForfraganSvar;
    }

    public void setInternForfraganSvar(InternForfraganSvarItem internForfraganSvar) {
        this.internForfraganSvar = internForfraganSvar;
    }
}
