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

import se.inera.intyg.intygsbestallning.service.patient.PatientNamable;
import se.inera.intyg.intygsbestallning.service.pdl.dto.PDLLoggable;
import se.inera.intyg.intygsbestallning.persistence.model.status.UtredningFas;
import se.inera.intyg.intygsbestallning.persistence.model.status.UtredningStatus;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.FilterableListItem;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.FreeTextSearchable;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.VardgivareEnrichable;

public class BestallningListItem implements PDLLoggable, PatientNamable, FreeTextSearchable, FilterableListItem, VardgivareEnrichable {

    private Long utredningsId;
    private String utredningsTyp;
    private String vardgivareHsaId;
    private String vardgivareNamn;
    private UtredningFas fas;
    private String slutdatumFas;
    private boolean slutdatumPaVagPasseras;
    private boolean slutdatumPasserat;
    private UtredningStatus status;
    private String patientId;
    private String patientNamn;
    private String nextActor;
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

    @Override
    public UtredningStatus getStatus() {
        return status;
    }

    public void setStatus(UtredningStatus status) {
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

    @Override
    public void setPatientNamn(String patientNamn) {
        this.patientNamn = patientNamn;
    }

    public String getNextActor() {
        return nextActor;
    }

    public void setNextActor(String nextActor) {
        this.nextActor = nextActor;
    }

    public boolean getSlutdatumPaVagPasseras() {
        return slutdatumPaVagPasseras;
    }

    public void setSlutdatumPaVagPasseras(boolean slutdatumPaVagPasseras) {
        this.slutdatumPaVagPasseras = slutdatumPaVagPasseras;
    }

    public boolean getSlutdatumPasserat() {
        return slutdatumPasserat;
    }

    public void setSlutdatumPasserat(boolean slutdatumPasserat) {
        this.slutdatumPasserat = slutdatumPasserat;
    }

    public boolean getKraverAtgard() {
        return kraverAtgard;
    }

    public void setKraverAtgard(boolean kraverAtgard) {
        this.kraverAtgard = kraverAtgard;
    }

    /**
     * String that concatenates the searchable fields when listing this class.
     */
    @Override
    public String toSearchString() {
        return utredningsId + " "
                + utredningsTyp + " "
                + vardgivareNamn + " "
                + fas + " "
                + slutdatumFas + " "
                + status + " "
                + patientId + " "
                + patientNamn;
    }

    public static final class BestallningListItemBuilder {
        private Long utredningsId;
        private String utredningsTyp;
        private String vardgivareHsaId;
        private String vardgivareNamn;
        private UtredningFas fas;
        private String slutdatumFas;
        private boolean slutdatumPaVagPasseras;
        private boolean slutdatumPasserat;
        private UtredningStatus status;
        private String patientId;
        private String patientNamn;
        private String nextActor;
        private boolean kraverAtgard;

        private BestallningListItemBuilder() {
        }

        public static BestallningListItemBuilder anBestallningListItem() {
            return new BestallningListItemBuilder();
        }

        public BestallningListItemBuilder withUtredningsId(Long utredningsId) {
            this.utredningsId = utredningsId;
            return this;
        }

        public BestallningListItemBuilder withUtredningsTyp(String utredningsTyp) {
            this.utredningsTyp = utredningsTyp;
            return this;
        }

        public BestallningListItemBuilder withVardgivareHsaId(String vardgivareHsaId) {
            this.vardgivareHsaId = vardgivareHsaId;
            return this;
        }

        public BestallningListItemBuilder withVardgivareNamn(String vardgivareNamn) {
            this.vardgivareNamn = vardgivareNamn;
            return this;
        }

        public BestallningListItemBuilder withFas(UtredningFas fas) {
            this.fas = fas;
            return this;
        }

        public BestallningListItemBuilder withSlutdatumFas(String slutdatumFas) {
            this.slutdatumFas = slutdatumFas;
            return this;
        }

        public BestallningListItemBuilder withSlutdatumPaVagPasseras(boolean slutdatumPaVagPasseras) {
            this.slutdatumPaVagPasseras = slutdatumPaVagPasseras;
            return this;
        }

        public BestallningListItemBuilder withSlutdatumPasserat(boolean slutdatumPasserat) {
            this.slutdatumPasserat = slutdatumPasserat;
            return this;
        }

        public BestallningListItemBuilder withStatus(UtredningStatus status) {
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

        public BestallningListItemBuilder withKraverAtgard(boolean kraverAtgard) {
            this.kraverAtgard = kraverAtgard;
            return this;
        }

        public BestallningListItem build() {
            BestallningListItem bestallningListItem = new BestallningListItem();
            bestallningListItem.setUtredningsId(utredningsId);
            bestallningListItem.setUtredningsTyp(utredningsTyp);
            bestallningListItem.setVardgivareHsaId(vardgivareHsaId);
            bestallningListItem.setVardgivareNamn(vardgivareNamn);
            bestallningListItem.setFas(fas);
            bestallningListItem.setSlutdatumFas(slutdatumFas);
            bestallningListItem.setSlutdatumPaVagPasseras(slutdatumPaVagPasseras);
            bestallningListItem.setSlutdatumPasserat(slutdatumPasserat);
            bestallningListItem.setStatus(status);
            bestallningListItem.setPatientId(patientId);
            bestallningListItem.setPatientNamn(patientNamn);
            bestallningListItem.setNextActor(nextActor);
            bestallningListItem.setKraverAtgard(kraverAtgard);
            return bestallningListItem;
        }
    }
}
