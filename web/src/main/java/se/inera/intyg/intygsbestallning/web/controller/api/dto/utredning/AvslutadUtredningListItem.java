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
package se.inera.intyg.intygsbestallning.web.controller.api.dto.utredning;

import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.status.UtredningStatus;
import se.inera.intyg.intygsbestallning.persistence.model.type.UtredningsTyp;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.BaseUtredningListItem;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.FreeTextSearchable;

public class AvslutadUtredningListItem extends BaseUtredningListItem implements FreeTextSearchable {

    private UtredningStatus status;

    private String avslutsDatum;
    private boolean ersatts;
    private String fakturerad;
    private String betald;
    private String utbetaldFk;

    public UtredningStatus getStatus() {
        return status;
    }

    public void setStatus(UtredningStatus status) {
        this.status = status;
    }

    public String getAvslutsDatum() {
        return avslutsDatum;
    }

    public void setAvslutsDatum(String avslutsDatum) {
        this.avslutsDatum = avslutsDatum;
    }

    public boolean getErsatts() {
        return ersatts;
    }

    public void setErsatts(boolean ersatts) {
        this.ersatts = ersatts;
    }

    public String getFakturerad() {
        return fakturerad;
    }

    public void setFakturerad(String fakturerad) {
        this.fakturerad = fakturerad;
    }

    public String getBetald() {
        return betald;
    }

    public void setBetald(String betald) {
        this.betald = betald;
    }

    public String getUtbetaldFk() {
        return utbetaldFk;
    }

    public void setUtbetaldFk(String utbetaldFk) {
        this.utbetaldFk = utbetaldFk;
    }

    @Override
    public String toSearchString() {
        return utredningsId + " "
                + utredningsTyp.getLabel() + " "
                + vardenhetHsaId + " "
                + vardenhetNamn + " "
                + status.getLabel() + " "
                + avslutsDatum + " "
                + (ersatts ? "Ja" : "Nej") + " "
                + fakturerad + " "
                + betald + " "
                + utbetaldFk;
    }

    public static final class AvslutadUtredningListItemBuilder {
        private Long utredningsId;
        private UtredningsTyp utredningsTyp;
        private String vardenhetHsaId;
        private String vardenhetNamn;
        private UtredningStatus status;
        private String avslutsDatum;
        private boolean ersatts;
        private String fakturerad;
        private String betald;
        private String utbetaldFk;
        private Utredning utredning;

        private AvslutadUtredningListItemBuilder() {
        }

        public static AvslutadUtredningListItemBuilder anAvslutadUtredningListItem() {
            return new AvslutadUtredningListItemBuilder();
        }

        public AvslutadUtredningListItemBuilder withUtredningsId(Long utredningsId) {
            this.utredningsId = utredningsId;
            return this;
        }

        public AvslutadUtredningListItemBuilder withUtredningsTyp(UtredningsTyp utredningsTyp) {
            this.utredningsTyp = utredningsTyp;
            return this;
        }

        public AvslutadUtredningListItemBuilder withVardenhetHsaId(String vardenhetHsaId) {
            this.vardenhetHsaId = vardenhetHsaId;
            return this;
        }

        public AvslutadUtredningListItemBuilder withVardenhetNamn(String vardenhetNamn) {
            this.vardenhetNamn = vardenhetNamn;
            return this;
        }

        public AvslutadUtredningListItemBuilder withStatus(UtredningStatus status) {
            this.status = status;
            return this;
        }

        public AvslutadUtredningListItemBuilder withAvslutsDatum(String avslutsDatum) {
            this.avslutsDatum = avslutsDatum;
            return this;
        }

        public AvslutadUtredningListItemBuilder withErsatts(boolean ersatts) {
            this.ersatts = ersatts;
            return this;
        }

        public AvslutadUtredningListItemBuilder withFakturerad(String fakturerad) {
            this.fakturerad = fakturerad;
            return this;
        }

        public AvslutadUtredningListItemBuilder withBetald(String betald) {
            this.betald = betald;
            return this;
        }

        public AvslutadUtredningListItemBuilder withUtbetaldFk(String utbetaldFk) {
            this.utbetaldFk = utbetaldFk;
            return this;
        }

        public AvslutadUtredningListItemBuilder withUtredning(Utredning utredning) {
            this.utredning = utredning;
            return this;
        }

        public AvslutadUtredningListItem build() {
            AvslutadUtredningListItem avslutadUtredningListItem = new AvslutadUtredningListItem();
            avslutadUtredningListItem.setUtredningsId(utredningsId);
            avslutadUtredningListItem.setUtredningsTyp(utredningsTyp);
            avslutadUtredningListItem.setVardenhetHsaId(vardenhetHsaId);
            avslutadUtredningListItem.setVardenhetNamn(vardenhetNamn);
            avslutadUtredningListItem.setStatus(status);
            avslutadUtredningListItem.setAvslutsDatum(avslutsDatum);
            avslutadUtredningListItem.setErsatts(ersatts);
            avslutadUtredningListItem.setFakturerad(fakturerad);
            avslutadUtredningListItem.setBetald(betald);
            avslutadUtredningListItem.setUtbetaldFk(utbetaldFk);
            avslutadUtredningListItem.setUtredning(utredning);
            return avslutadUtredningListItem;
        }
    }
}
