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

import se.inera.intyg.intygsbestallning.persistence.model.status.UtredningFas;
import se.inera.intyg.intygsbestallning.persistence.model.status.UtredningStatus;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.FilterableListItem;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.FreeTextSearchable;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.VardenhetEnrichable;

public class UtredningListItem implements FreeTextSearchable, FilterableListItem, VardenhetEnrichable {

    private Long utredningsId;
    private String utredningsTyp;
    private String vardenhetHsaId;
    private String vardenhetNamn;
    private UtredningFas fas;
    private String slutdatumFas;
    private boolean slutdatumFasPaVagPasseras;
    private boolean slutdatumFasPasserat;
    private UtredningStatus status;
    private boolean kraverAtgard;

    public Long getUtredningsId() {
        return utredningsId;
    }

    public void setUtredningsId(Long utredningsId) {
        this.utredningsId = utredningsId;
    }

    public String getUtredningsTyp() {
        return utredningsTyp;
    }

    public void setUtredningsTyp(String utredningsTyp) {
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

    public UtredningFas getFas() {
        return fas;
    }

    public void setFas(UtredningFas fas) {
        this.fas = fas;
    }

    @Override
    public String getSlutdatumFas() {
        return slutdatumFas;
    }

    public void setSlutdatumFas(String slutdatumFas) {
        this.slutdatumFas = slutdatumFas;
    }

    public boolean isSlutdatumFasPaVagPasseras() {
        return slutdatumFasPaVagPasseras;
    }

    public void setSlutdatumFasPaVagPasseras(boolean slutdatumFasPaVagPasseras) {
        this.slutdatumFasPaVagPasseras = slutdatumFasPaVagPasseras;
    }

    public boolean isSlutdatumFasPasserat() {
        return slutdatumFasPasserat;
    }

    public void setSlutdatumFasPasserat(boolean slutdatumFasPasserat) {
        this.slutdatumFasPasserat = slutdatumFasPasserat;
    }

    @Override
    public UtredningStatus getStatus() {
        return status;
    }

    public void setStatus(UtredningStatus status) {
        this.status = status;
    }

    public boolean isKraverAtgard() {
        return kraverAtgard;
    }

    public void setKraverAtgard(boolean kraverAtgard) {
        this.kraverAtgard = kraverAtgard;
    }

    @Override
    public String toSearchString() {
        return utredningsId + " "
                + utredningsTyp + " "
                + vardenhetNamn + " "
                + fas.getLabel() + " "
                + slutdatumFas + " "
                + status.getLabel() + " ";
    }

    public static final class UtredningListItemBuilder {
        private Long utredningsId;
        private String utredningsTyp;
        private String vardenhetHsaId;
        private String vardenhetNamn;
        private UtredningFas fas;
        private String slutdatumFas;
        private boolean slutdatumFasPaVagPasseras;
        private boolean slutdatumFasPasserat;
        private UtredningStatus status;
        private boolean kraverAtgard;

        private UtredningListItemBuilder() {
        }

        public static UtredningListItemBuilder anUtredningListItem() {
            return new UtredningListItemBuilder();
        }

        public UtredningListItemBuilder withUtredningsId(Long utredningsId) {
            this.utredningsId = utredningsId;
            return this;
        }

        public UtredningListItemBuilder withUtredningsTyp(String utredningsTyp) {
            this.utredningsTyp = utredningsTyp;
            return this;
        }

        public UtredningListItemBuilder withVardenhetHsaId(String vardenhetHsaId) {
            this.vardenhetHsaId = vardenhetHsaId;
            return this;
        }

        public UtredningListItemBuilder withVardenhetNamn(String vardenhetNamn) {
            this.vardenhetNamn = vardenhetNamn;
            return this;
        }

        public UtredningListItemBuilder withFas(UtredningFas fas) {
            this.fas = fas;
            return this;
        }

        public UtredningListItemBuilder withSlutdatumFas(String slutdatumFas) {
            this.slutdatumFas = slutdatumFas;
            return this;
        }

        public UtredningListItemBuilder withSlutdatumFasPaVagPasseras(boolean slutdatumFasPaVagPasseras) {
            this.slutdatumFasPaVagPasseras = slutdatumFasPaVagPasseras;
            return this;
        }

        public UtredningListItemBuilder withSlutdatumFasPasserat(boolean slutdatumFasPasserat) {
            this.slutdatumFasPasserat = slutdatumFasPasserat;
            return this;
        }

        public UtredningListItemBuilder withStatus(UtredningStatus status) {
            this.status = status;
            return this;
        }

        public UtredningListItemBuilder withKraverAtgard(boolean kraverAtgard) {
            this.kraverAtgard = kraverAtgard;
            return this;
        }

        public UtredningListItem build() {
            UtredningListItem utredningListItem = new UtredningListItem();
            utredningListItem.setUtredningsId(utredningsId);
            utredningListItem.setUtredningsTyp(utredningsTyp);
            utredningListItem.setVardenhetHsaId(vardenhetHsaId);
            utredningListItem.setVardenhetNamn(vardenhetNamn);
            utredningListItem.setFas(fas);
            utredningListItem.setSlutdatumFas(slutdatumFas);
            utredningListItem.setSlutdatumFasPasserat(slutdatumFasPasserat);
            utredningListItem.setSlutdatumFasPaVagPasseras(slutdatumFasPaVagPasseras);
            utredningListItem.setStatus(status);
            utredningListItem.setKraverAtgard(kraverAtgard);
            return utredningListItem;
        }
    }
}
