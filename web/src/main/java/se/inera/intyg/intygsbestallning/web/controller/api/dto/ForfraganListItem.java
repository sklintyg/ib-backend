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

import se.inera.intyg.intygsbestallning.persistence.model.InternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.service.stateresolver.Actor;
import se.inera.intyg.intygsbestallning.service.stateresolver.InternForfraganStateResolver;
import se.inera.intyg.intygsbestallning.service.stateresolver.InternForfraganStatus;
import se.inera.intyg.intygsbestallning.service.util.holidays.Holidays;
import se.inera.intyg.intygsbestallning.web.controller.api.filter.ListForfraganFilterStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.isNull;

public class ForfraganListItem implements FreeTextSearchable {
    private static DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;

    // Temp hard-coded, replace by parameterized stuff.
    private static final int BESVARA_FORFRAGAN_ARBETSDAGAR = 2;

    private String utredningsId;
    private String utredningsTyp;
    private String vardgivareHsaId;
    private String vardgivareNamn;
    private String inkomDatum;
    private String besvarasSenastDatum;
    private boolean besvarasSenastDatumPaVagPasseras;
    private boolean besvarasSenastDatumPasserat;
    private String planeringsDatum;
    private InternForfraganStatus status;
    private List<ListForfraganFilterStatus> filterStatusar;
    private boolean kraverAtgard;

    public static ForfraganListItem from(Utredning utredning, String vardenhetId, InternForfraganStateResolver statusResolver) {
        InternForfragan internForfragan = utredning.getExternForfragan().getInternForfraganList()
                .stream()
                .filter(i -> Objects.equals(i.getVardenhetHsaId(), vardenhetId))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);

        InternForfraganStatus status = statusResolver.resolveStatus(utredning, internForfragan);

        return ForfraganListItemBuilder.aForfraganListItem()
                .withBesvarasSenastDatum(!isNull(internForfragan.getBesvarasSenastDatum())
                        ? internForfragan.getBesvarasSenastDatum().format(formatter)
                        : null)
                .withBesvarasSenastDatumPaVagPasseras(resolveBesvarasSenastPaVagPasseras(internForfragan.getBesvarasSenastDatum()))
                .withBesvarasSenastDatumPasserat(resolveBesvaraSenastPasserat(internForfragan))
                .withInkomDatum(!isNull(internForfragan.getSkapadDatum())
                        ? internForfragan.getSkapadDatum().format(formatter)
                        : null)
                .withPlaneringsDatum(
                        !isNull(internForfragan.getForfraganSvar()) && !isNull(internForfragan.getForfraganSvar().getBorjaDatum())
                                ? internForfragan.getForfraganSvar().getBorjaDatum().format(formatter)
                                : null)
                .withStatus(status)
                .withUtredningsId(utredning.getUtredningId())
                .withUtredningsTyp(utredning.getUtredningsTyp().name())
                .withVardgivareHsaId(utredning.getExternForfragan().getLandstingHsaId())
                .withVardgivareNamn(utredning.getExternForfragan().getLandstingHsaId())
                .withKraverAtgard(status.getNextActor() == Actor.VARDADMIN)
                .build();
    }

    private static boolean resolveBesvaraSenastPasserat(InternForfragan internForfragan) {
        if (internForfragan.getBesvarasSenastDatum() == null) {
            return false;
        }
        return LocalDateTime.now().compareTo(internForfragan.getBesvarasSenastDatum()) > 0;
    }

    private static boolean resolveBesvarasSenastPaVagPasseras(LocalDateTime besvarasSenastDatum) {
        if (besvarasSenastDatum == null) {
            return false;
        }

        // Om datumet redan passerats skall vi ej flagga.
        if (besvarasSenastDatum.toLocalDate().compareTo(LocalDate.now()) < 0) {
            return false;
        }
        return Holidays.SWE.daysBetween(LocalDate.now(), besvarasSenastDatum.toLocalDate()) < BESVARA_FORFRAGAN_ARBETSDAGAR;
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
        private String utredningsId;
        private String utredningsTyp;
        private String vardgivareHsaId;
        private String vardgivareNamn;
        private String inkomDatum;
        private String besvarasSenastDatum;
        private boolean besvarasSenastDatumPaVagPasseras;
        private boolean besvarasSenastDatumPasserat;
        private String planeringsDatum;
        private InternForfraganStatus status;
        private List<ListForfraganFilterStatus> filterStatusar;
        private boolean kraverAtgard;

        private ForfraganListItemBuilder() {
        }

        public static ForfraganListItemBuilder aForfraganListItem() {
            return new ForfraganListItemBuilder();
        }

        public ForfraganListItemBuilder withUtredningsId(String utredningsId) {
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

        public ForfraganListItemBuilder withFilterStatusar(List<ListForfraganFilterStatus> filterStatusar) {
            this.filterStatusar = filterStatusar;
            return this;
        }

        public ForfraganListItemBuilder withKraverAtgard(boolean kraverAtgard) {
            this.kraverAtgard = kraverAtgard;
            return this;
        }

        public ForfraganListItem build() {
            ForfraganListItem forfraganListItem = new ForfraganListItem();
            forfraganListItem.setUtredningsId(utredningsId);
            forfraganListItem.setUtredningsTyp(utredningsTyp);
            forfraganListItem.setVardgivareHsaId(vardgivareHsaId);
            forfraganListItem.setVardgivareNamn(vardgivareNamn);
            forfraganListItem.setInkomDatum(inkomDatum);
            forfraganListItem.setBesvarasSenastDatum(besvarasSenastDatum);
            forfraganListItem.setBesvarasSenastDatumPaVagPasseras(besvarasSenastDatumPaVagPasseras);
            forfraganListItem.setBesvarasSenastDatumPasserat(besvarasSenastDatumPasserat);
            forfraganListItem.setPlaneringsDatum(planeringsDatum);
            forfraganListItem.setStatus(status);
            forfraganListItem.setFilterStatusar(filterStatusar);
            forfraganListItem.setKraverAtgard(kraverAtgard);
            return forfraganListItem;
        }
    }
}
