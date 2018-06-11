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

import se.inera.intyg.intygsbestallning.persistence.model.status.InternForfraganStatus;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.FreeTextSearchable;
import se.inera.intyg.intygsbestallning.web.controller.api.filter.ListForfraganFilterStatus;

import java.util.List;

public class InternForfraganListItem implements FreeTextSearchable {

    private Long utredningsId;
    private String utredningsTyp;
    private String vardgivareHsaId;
    private String vardgivareNamn;
    private String inkomDatum;
    private String besvarasSenastDatum;
    private boolean besvarasSenastDatumPaVagPasseras;
    private boolean besvarasSenastDatumPasserat;
    private String planeringsDatum;
    private Long forfraganId;
    private InternForfraganStatus status;
    private List<ListForfraganFilterStatus> filterStatusar;
    private String kommentar;
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

    public String getVardgivareHsaId() {
        return vardgivareHsaId;
    }

    public void setVardgivareHsaId(String vardgivareHsaId) {
        this.vardgivareHsaId = vardgivareHsaId;
    }

    public String getVardgivareNamn() {
        return vardgivareNamn;
    }

    public void setVardgivareNamn(String vardgivareNamn) {
        this.vardgivareNamn = vardgivareNamn;
    }

    public String getInkomDatum() {
        return inkomDatum;
    }

    public void setInkomDatum(String inkomDatum) {
        this.inkomDatum = inkomDatum;
    }

    public String getBesvarasSenastDatum() {
        return besvarasSenastDatum;
    }

    public void setBesvarasSenastDatum(String besvarasSenastDatum) {
        this.besvarasSenastDatum = besvarasSenastDatum;
    }

    public String getPlaneringsDatum() {
        return planeringsDatum;
    }

    public void setPlaneringsDatum(String planeringsDatum) {
        this.planeringsDatum = planeringsDatum;
    }

    public InternForfraganStatus getStatus() {
        return status;
    }

    public void setStatus(InternForfraganStatus status) {
        this.status = status;
    }

    public List<ListForfraganFilterStatus> getFilterStatusar() {
        return filterStatusar;
    }

    public void setFilterStatusar(List<ListForfraganFilterStatus> filterStatusar) {
        this.filterStatusar = filterStatusar;
    }

    public boolean isBesvarasSenastDatumPaVagPasseras() {
        return besvarasSenastDatumPaVagPasseras;
    }

    public void setBesvarasSenastDatumPaVagPasseras(boolean besvarasSenastDatumPaVagPasseras) {
        this.besvarasSenastDatumPaVagPasseras = besvarasSenastDatumPaVagPasseras;
    }

    public boolean isBesvarasSenastDatumPasserat() {
        return besvarasSenastDatumPasserat;
    }

    public void setBesvarasSenastDatumPasserat(boolean besvarasSenastDatumPasserat) {
        this.besvarasSenastDatumPasserat = besvarasSenastDatumPasserat;
    }

    public boolean isKraverAtgard() {
        return kraverAtgard;
    }

    public void setKraverAtgard(boolean kraverAtgard) {
        this.kraverAtgard = kraverAtgard;
    }

    public Long getForfraganId() {
        return forfraganId;
    }

    public void setForfraganId(Long forfraganId) {
        this.forfraganId = forfraganId;
    }

    public String getKommentar() {
        return kommentar;
    }

    public void setKommentar(String kommentar) {
        this.kommentar = kommentar;
    }

    @Override
    public String toSearchString() {
        return utredningsId
                + utredningsTyp + " "
                + vardgivareHsaId + " "
                + vardgivareNamn + " "
                + inkomDatum + " "
                + besvarasSenastDatum + " "
                + planeringsDatum + " "
                + status;
    }

    public static final class ForfraganListItemBuilder {
        private Long utredningsId;
        private String utredningsTyp;
        private String vardgivareHsaId;
        private String vardgivareNamn;
        private String inkomDatum;
        private String besvarasSenastDatum;
        private boolean besvarasSenastDatumPaVagPasseras;
        private boolean besvarasSenastDatumPasserat;
        private String planeringsDatum;
        private Long forfraganId;
        private InternForfraganStatus status;
        private List<ListForfraganFilterStatus> filterStatusar;
        private boolean kraverAtgard;
        private String kommentar;

        private ForfraganListItemBuilder() {
        }

        public static ForfraganListItemBuilder aForfraganListItem() {
            return new ForfraganListItemBuilder();
        }

        public ForfraganListItemBuilder withUtredningsId(Long utredningsId) {
            this.utredningsId = utredningsId;
            return this;
        }

        public ForfraganListItemBuilder withUtredningsTyp(String utredningsTyp) {
            this.utredningsTyp = utredningsTyp;
            return this;
        }

        public ForfraganListItemBuilder withVardgivareHsaId(String vardgivareHsaId) {
            this.vardgivareHsaId = vardgivareHsaId;
            return this;
        }

        public ForfraganListItemBuilder withVardgivareNamn(String vardgivareNamn) {
            this.vardgivareNamn = vardgivareNamn;
            return this;
        }

        public ForfraganListItemBuilder withInkomDatum(String inkomDatum) {
            this.inkomDatum = inkomDatum;
            return this;
        }

        public ForfraganListItemBuilder withBesvarasSenastDatum(String besvarasSenastDatum) {
            this.besvarasSenastDatum = besvarasSenastDatum;
            return this;
        }

        public ForfraganListItemBuilder withBesvarasSenastDatumPaVagPasseras(boolean besvarasSenastDatumPaVagPasseras) {
            this.besvarasSenastDatumPaVagPasseras = besvarasSenastDatumPaVagPasseras;
            return this;
        }

        public ForfraganListItemBuilder withBesvarasSenastDatumPasserat(boolean besvarasSenastDatumPasserat) {
            this.besvarasSenastDatumPasserat = besvarasSenastDatumPasserat;
            return this;
        }

        public ForfraganListItemBuilder withPlaneringsDatum(String planeringsDatum) {
            this.planeringsDatum = planeringsDatum;
            return this;
        }

        public ForfraganListItemBuilder withStatus(InternForfraganStatus status) {
            this.status = status;
            return this;
        }
        public ForfraganListItemBuilder withForfraganId(Long forfraganId) {
            this.forfraganId = forfraganId;
            return this;
        }

        public ForfraganListItemBuilder withFilterStatusar(List<ListForfraganFilterStatus> filterStatusar) {
            this.filterStatusar = filterStatusar;
            return this;
        }

        public ForfraganListItemBuilder withKraverAtgard(boolean kraverAtgard) {
            this.kraverAtgard = kraverAtgard;
            return this;
        }
        public ForfraganListItemBuilder withKommentar(String kommentar) {
            this.kommentar = kommentar;
            return this;
        }

        public InternForfraganListItem build() {
            InternForfraganListItem internForfraganListItem = new InternForfraganListItem();
            internForfraganListItem.setUtredningsId(utredningsId);
            internForfraganListItem.setUtredningsTyp(utredningsTyp);
            internForfraganListItem.setVardgivareHsaId(vardgivareHsaId);
            internForfraganListItem.setVardgivareNamn(vardgivareNamn);
            internForfraganListItem.setInkomDatum(inkomDatum);
            internForfraganListItem.setBesvarasSenastDatum(besvarasSenastDatum);
            internForfraganListItem.setBesvarasSenastDatumPaVagPasseras(besvarasSenastDatumPaVagPasseras);
            internForfraganListItem.setBesvarasSenastDatumPasserat(besvarasSenastDatumPasserat);
            internForfraganListItem.setPlaneringsDatum(planeringsDatum);
            internForfraganListItem.setForfraganId(forfraganId);
            internForfraganListItem.setStatus(status);
            internForfraganListItem.setFilterStatusar(filterStatusar);
            internForfraganListItem.setKraverAtgard(kraverAtgard);
            internForfraganListItem.setKommentar(kommentar);
            return internForfraganListItem;
        }
    }
}
