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

import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.service.pdl.dto.PDLLoggable;
import se.inera.intyg.intygsbestallning.service.stateresolver.UtredningStatus;

import static java.util.Objects.isNull;

public class BestallningListItem implements PDLLoggable {

    private String utredningsId;
    private String utredningsTyp;
    private String vardgivareNamn;
    private String fas;
    private String slutdatumFas;
    private String status;
    private String patientId;
    private String patientNamn;
    private String nextActor;

    public static BestallningListItem from(Utredning utredning, UtredningStatus utredningStatus, String patientNamn) {
        return BestallningListItemBuilder.anUtredningListItem()
                .withFas(utredningStatus.getUtredningFas().name())
                .withPatientId(utredning.getInvanare().getPersonId())
                .withPatientNamn(patientNamn)
                .withSlutdatumFas("TODO")
                .withStatus(utredningStatus.name())
                .withNextActor(utredningStatus.getNextActor().name())
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

    @Override
    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getPatientNamn() {
        return patientNamn;
    }

    public void setPatientNamn(String patientNamn) {
        this.patientNamn = patientNamn;
    }

    public String getNextActor() {
        return nextActor;
    }

    public void setNextActor(String nextActor) {
        this.nextActor = nextActor;
    }

    public static final class BestallningListItemBuilder {
        private String utredningsId;
        private String utredningsTyp;
        private String vardgivareNamn;
        private String fas;
        private String slutdatumFas;
        private String status;
        private String patientId;
        private String patientNamn;
        private String nextActor;

        private BestallningListItemBuilder() {
        }

        public static BestallningListItemBuilder anUtredningListItem() {
            return new BestallningListItemBuilder();
        }

        public BestallningListItemBuilder withUtredningsId(String utredningsId) {
            this.utredningsId = utredningsId;
            return this;
        }

        public BestallningListItemBuilder withUtredningsTyp(String utredningsTyp) {
            this.utredningsTyp = utredningsTyp;
            return this;
        }

        public BestallningListItemBuilder withVardgivareNamn(String vardgivareNamn) {
            this.vardgivareNamn = vardgivareNamn;
            return this;
        }

        public BestallningListItemBuilder withFas(String fas) {
            this.fas = fas;
            return this;
        }

        public BestallningListItemBuilder withSlutdatumFas(String slutdatumFas) {
            this.slutdatumFas = slutdatumFas;
            return this;
        }

        public BestallningListItemBuilder withStatus(String status) {
            this.status = status;
            return this;
        }

        public BestallningListItemBuilder withPatientId(String patientId) {
            this.patientId = patientId;
            return this;
        }

        public BestallningListItemBuilder withPatientNamn(String patientNamn) {
            this.patientNamn = patientNamn;
            return this;
        }

        public BestallningListItemBuilder withNextActor(String nextActor) {
            this.nextActor = nextActor;
            return this;
        }

        public BestallningListItem build() {
            BestallningListItem bestallningListItem = new BestallningListItem();
            bestallningListItem.setUtredningsId(utredningsId);
            bestallningListItem.setUtredningsTyp(utredningsTyp);
            bestallningListItem.setVardgivareNamn(vardgivareNamn);
            bestallningListItem.setFas(fas);
            bestallningListItem.setSlutdatumFas(slutdatumFas);
            bestallningListItem.setStatus(status);
            bestallningListItem.setPatientId(patientId);
            bestallningListItem.setPatientNamn(patientNamn);
            bestallningListItem.setNextActor(nextActor);
            return bestallningListItem;
        }
    }
}
