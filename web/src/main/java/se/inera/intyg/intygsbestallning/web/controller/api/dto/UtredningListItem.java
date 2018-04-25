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

import static java.util.Objects.isNull;

public class UtredningListItem {

    private String utredningsId;
    private String utredningsTyp;
    private String vardgivareNamn;
    private String fas;
    private String slutdatumFas;
    private String status;
    private String patientId;

    public static UtredningListItem from(Utredning utredning) {
        return UtredningListItemBuilder.anUtredningListItem()
                .withFas("TODO")
                .withPatientId(utredning.getInvanare().getPersonId())
                .withSlutdatumFas("TODO")
                .withStatus("TODO")
                .withUtredningsId(utredning.getUtredningId())
                .withUtredningsTyp(utredning.getUtredningsTyp().name())
                .withVardgivareNamn(!isNull(utredning.getExternForfragan()) ? utredning.getExternForfragan().getLandstingHsaId() : null)
                .build();
    }

    public String getUtredningsId() {
        return utredningsId;
    }

    public void setUtredningsId(String utredningsId) {
        this.utredningsId = utredningsId;
    }

    public String getUtredningsTyp() {
        return utredningsTyp;
    }

    public void setUtredningsTyp(String utredningsTyp) {
        this.utredningsTyp = utredningsTyp;
    }

    public String getVardgivareNamn() {
        return vardgivareNamn;
    }

    public void setVardgivareNamn(String vardgivareNamn) {
        this.vardgivareNamn = vardgivareNamn;
    }

    public String getFas() {
        return fas;
    }

    public void setFas(String fas) {
        this.fas = fas;
    }

    public String getSlutdatumFas() {
        return slutdatumFas;
    }

    public void setSlutdatumFas(String slutdatumFas) {
        this.slutdatumFas = slutdatumFas;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @JsonIgnore
    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public static final class UtredningListItemBuilder {
        private String utredningsId;
        private String utredningsTyp;
        private String vardgivareNamn;
        private String fas;
        private String slutdatumFas;
        private String status;
        private String patientId;

        private UtredningListItemBuilder() {
        }

        public static UtredningListItemBuilder anUtredningListItem() {
            return new UtredningListItemBuilder();
        }

        public UtredningListItemBuilder withUtredningsId(String utredningsId) {
            this.utredningsId = utredningsId;
            return this;
        }

        public UtredningListItemBuilder withUtredningsTyp(String utredningsTyp) {
            this.utredningsTyp = utredningsTyp;
            return this;
        }

        public UtredningListItemBuilder withVardgivareNamn(String vardgivareNamn) {
            this.vardgivareNamn = vardgivareNamn;
            return this;
        }

        public UtredningListItemBuilder withFas(String fas) {
            this.fas = fas;
            return this;
        }

        public UtredningListItemBuilder withSlutdatumFas(String slutdatumFas) {
            this.slutdatumFas = slutdatumFas;
            return this;
        }

        public UtredningListItemBuilder withStatus(String status) {
            this.status = status;
            return this;
        }

        public UtredningListItemBuilder withPatientId(String patientId) {
            this.patientId = patientId;
            return this;
        }

        public UtredningListItem build() {
            UtredningListItem utredningListItem = new UtredningListItem();
            utredningListItem.setUtredningsId(utredningsId);
            utredningListItem.setUtredningsTyp(utredningsTyp);
            utredningListItem.setVardgivareNamn(vardgivareNamn);
            utredningListItem.setFas(fas);
            utredningListItem.setSlutdatumFas(slutdatumFas);
            utredningListItem.setStatus(status);
            utredningListItem.setPatientId(patientId);
            return utredningListItem;
        }
    }
}
