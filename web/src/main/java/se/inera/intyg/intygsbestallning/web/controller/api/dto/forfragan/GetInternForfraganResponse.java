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

import se.inera.intyg.intygsbestallning.persistence.model.InternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.service.stateresolver.InternForfraganStateResolver;
import se.inera.intyg.intygsbestallning.service.stateresolver.UtredningStatus;
import se.inera.intyg.intygsbestallning.service.util.BusinessDaysBean;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.GetUtredningResponse;

public class GetInternForfraganResponse {

    private InternForfraganListItem internForfragan;

    private InternForfraganSvarItem internForfraganSvar;

    private GetUtredningResponse utredning;

    public static GetInternForfraganResponse from(Utredning utredning, UtredningStatus status, InternForfragan internForfragan,
            InternForfraganStateResolver internForfraganStateResolver, BusinessDaysBean businessDays) {


        final GetUtredningResponse utredningsResponse = GetUtredningResponse.from(utredning, status);
        // Vardadmins should not see händelser or InternforfraganList
        utredningsResponse.getHandelseList().clear();
        utredningsResponse.getInternForfraganList().clear();

        final InternForfraganListItem internForfraganListItem = InternForfraganListItem.from(utredning, internForfragan.getVardenhetHsaId(),
                internForfraganStateResolver, businessDays);
        final InternForfraganSvarItem internForfraganSvarItem = InternForfraganSvarItem.from(internForfragan.getForfraganSvar());

        return GetForfraganResponseBuilder.aGetForfraganResponse()
                .withInternForfragan(internForfraganListItem)
                .withInternForfraganSvar(internForfraganSvarItem)
                .withUtredning(utredningsResponse)
                .build();
    }

    public InternForfraganListItem getInternForfragan() {
        return internForfragan;
    }

    public void setInternForfragan(InternForfraganListItem internforfragan) {
        this.internForfragan = internforfragan;
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

    public static final class GetForfraganResponseBuilder {
        private InternForfraganListItem internForfraganListItem;
        private InternForfraganSvarItem internForfraganSvarItem;
        private GetUtredningResponse utredning;

        private GetForfraganResponseBuilder() {
        }

        public static GetForfraganResponseBuilder aGetForfraganResponse() {
            return new GetForfraganResponseBuilder();
        }

        public GetForfraganResponseBuilder withUtredning(GetUtredningResponse utredningResponse) {
            this.utredning = utredningResponse;
            return this;
        }

        public GetForfraganResponseBuilder withInternForfragan(InternForfraganListItem internForfraganListItem) {
            this.internForfraganListItem = internForfraganListItem;
            return this;
        }
        public GetForfraganResponseBuilder withInternForfraganSvar(InternForfraganSvarItem internForfraganSvarItem) {
            this.internForfraganSvarItem = internForfraganSvarItem;
            return this;
        }


        public GetInternForfraganResponse build() {
            GetInternForfraganResponse getForfraganResponse = new GetInternForfraganResponse();
            getForfraganResponse.setInternForfragan(internForfraganListItem);
            getForfraganResponse.setInternForfraganSvar(internForfraganSvarItem);
            getForfraganResponse.setUtredning(utredning);
            return getForfraganResponse;
        }
    }
}
