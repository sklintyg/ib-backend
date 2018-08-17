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
package se.inera.intyg.intygsbestallning.web.controller.api.dto.bestallning;

import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.status.UtredningStatus;
import se.inera.intyg.intygsbestallning.persistence.model.type.UtredningsTyp;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.BaseUtredningListItem;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.FreeTextSearchable;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.VardgivareEnrichable;

public class AvslutadBestallningListItem extends BaseUtredningListItem implements FreeTextSearchable, VardgivareEnrichable {

    private String vardgivareHsaId;
    private String vardgivareNamn;
    private UtredningStatus status;

    private String avslutsDatum;
    private boolean ersatts;
    private String fakturaVeId;
    private String betaldVeId;

    @Override
    public String getVardgivareHsaId() {
        return vardgivareHsaId;
    }

    public void setVardgivareHsaId(String vardgivareHsaId) {
        this.vardgivareHsaId = vardgivareHsaId;
    }

    public String getVardgivareNamn() {
        return vardgivareNamn;
    }

    @Override
    public void setVardgivareNamn(String vardgivareNamn) {
        this.vardgivareNamn = vardgivareNamn;
    }

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

    public String getFakturaVeId() {
        return fakturaVeId;
    }

    public void setFakturaVeId(String fakturaVeId) {
        this.fakturaVeId = fakturaVeId;
    }

    public String getBetaldVeId() {
        return betaldVeId;
    }

    public void setBetaldVeId(String betaldVeId) {
        this.betaldVeId = betaldVeId;
    }

    @Override
    public String toSearchString() {
        return utredningsId + " "
                + utredningsTyp.getLabel() + " "
                + vardgivareHsaId + " "
                + vardgivareNamn + " "
                + status.getLabel() + " "
                + avslutsDatum + " "
                + (ersatts ? "Ja" : "Nej") + " "
                + fakturaVeId + " "
                + betaldVeId;
    }

    public static final class AvslutadBestallningListItemBuilder {
        private Utredning utredning;
        private Long utredningsId;
        private UtredningsTyp utredningsTyp;
        private String vardgivareHsaId;
        private String vardgivareNamn;
        private UtredningStatus status;
        private String avslutsDatum;
        private boolean ersatts;
        private String fakturaVeId;
        private String betaldVeId;

        private AvslutadBestallningListItemBuilder() {
        }

        public static AvslutadBestallningListItemBuilder anAvslutadBestallningListItem() {
            return new AvslutadBestallningListItemBuilder();
        }

        public AvslutadBestallningListItemBuilder withUtredningsId(Long utredningsId) {
            this.utredningsId = utredningsId;
            return this;
        }

        public AvslutadBestallningListItemBuilder withUtredningsTyp(UtredningsTyp utredningsTyp) {
            this.utredningsTyp = utredningsTyp;
            return this;
        }

        public AvslutadBestallningListItemBuilder withVardgivareHsaId(String vardgivareHsaId) {
            this.vardgivareHsaId = vardgivareHsaId;
            return this;
        }

        public AvslutadBestallningListItemBuilder withVardgivareNamn(String vardgivareNamn) {
            this.vardgivareNamn = vardgivareNamn;
            return this;
        }

        public AvslutadBestallningListItemBuilder withStatus(UtredningStatus status) {
            this.status = status;
            return this;
        }

        public AvslutadBestallningListItemBuilder withAvslutsDatum(String avslutsDatum) {
            this.avslutsDatum = avslutsDatum;
            return this;
        }

        public AvslutadBestallningListItemBuilder withErsatts(boolean ersatts) {
            this.ersatts = ersatts;
            return this;
        }

        public AvslutadBestallningListItemBuilder withFakturaVeid(String fakturaVeId) {
            this.fakturaVeId = fakturaVeId;
            return this;
        }

        public AvslutadBestallningListItemBuilder withBetaldVeId(String betaldVeId) {
            this.betaldVeId = betaldVeId;
            return this;
        }

        public AvslutadBestallningListItemBuilder withUtredning(Utredning utredning) {
            this.utredning = utredning;
            return this;
        }

        public AvslutadBestallningListItem build() {
            AvslutadBestallningListItem avslutadBestallningListItem = new AvslutadBestallningListItem();
            avslutadBestallningListItem.setUtredningsId(utredningsId);
            avslutadBestallningListItem.setUtredningsTyp(utredningsTyp);
            avslutadBestallningListItem.setVardgivareHsaId(vardgivareHsaId);
            avslutadBestallningListItem.setVardgivareNamn(vardgivareNamn);
            avslutadBestallningListItem.setStatus(status);
            avslutadBestallningListItem.setAvslutsDatum(avslutsDatum);
            avslutadBestallningListItem.setErsatts(ersatts);
            avslutadBestallningListItem.setFakturaVeId(fakturaVeId);
            avslutadBestallningListItem.setBetaldVeId(betaldVeId);
            avslutadBestallningListItem.setUtredning(utredning);
            return avslutadBestallningListItem;
        }
    }
}
