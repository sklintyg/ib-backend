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
package se.inera.intyg.intygsbestallning.persistence.model;

import static java.util.Objects.isNull;
import static se.inera.intyg.intygsbestallning.persistence.model.Bestallning.BestallningBuilder.aBestallning;

import com.google.common.base.MoreObjects;
import org.apache.commons.collections4.ListUtils;
import org.hibernate.annotations.Type;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "BESTALLNING")
public final class Bestallning {

    @Id
    @GeneratedValue
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "TILLDELAD_VARDENHET_HSA_ID", nullable = false)
    private String tilldeladVardenhetHsaId;

    @Column(name = "TILLDELAD_VARDENHET_ORG_NR", nullable = false)
    private String tilldeladVardenhetOrgNr;

    @Column(name = "ORDER_DATUM")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    private LocalDateTime orderDatum;

    @Column(name = "UPPDATERAD_DATUM")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    private LocalDateTime uppdateradDatum;

    @Column(name = "SYFTE")
    private String syfte;

    @Column(name = "PLANERADE_AKTIVITETER")
    private String planeradeAktiviteter;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "BESTALLNING_ID", referencedColumnName = "ID", nullable = false)
    private List<BestallningHistorik> bestallningHistorikList = new ArrayList<>();

    public Bestallning() {
    }

    public static Bestallning copyFrom(final Bestallning bestallning) {

        if (isNull(bestallning)) {
            return null;
        }

        return aBestallning()
                .withId(bestallning.getId())
                .withTilldeladVardenhetHsaId(bestallning.getTilldeladVardenhetHsaId())
                .withTilldeladVardenhetOrgNr(bestallning.getTilldeladVardenhetOrgNr())
                .withOrderDatum(bestallning.getOrderDatum())
                .withUppdateradDatum(bestallning.getUppdateradDatum())
                .withSyfte(bestallning.getSyfte())
                .withPlaneradeAktiviteter(bestallning.getPlaneradeAktiviteter())
                .withBestallningHistorik(bestallning.getBestallningHistorikList())
                .build();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTilldeladVardenhetHsaId() {
        return tilldeladVardenhetHsaId;
    }

    public void setTilldeladVardenhetHsaId(String tilldeladVardenhetHsaId) {
        this.tilldeladVardenhetHsaId = tilldeladVardenhetHsaId;
    }

    public String getTilldeladVardenhetOrgNr() {
        return tilldeladVardenhetOrgNr;
    }

    public void setTilldeladVardenhetOrgNr(String tilldeladVardenhetOrgNr) {
        this.tilldeladVardenhetOrgNr = tilldeladVardenhetOrgNr;
    }

    public LocalDateTime getOrderDatum() {
        return orderDatum;
    }

    public void setOrderDatum(LocalDateTime orderDatum) {
        this.orderDatum = orderDatum;
    }

    public LocalDateTime getUppdateradDatum() {
        return uppdateradDatum;
    }

    public void setUppdateradDatum(LocalDateTime uppdateradDatum) {
        this.uppdateradDatum = uppdateradDatum;
    }

    public String getSyfte() {
        return syfte;
    }

    public void setSyfte(String syfte) {
        this.syfte = syfte;
    }

    public String getPlaneradeAktiviteter() {
        return planeradeAktiviteter;
    }

    public void setPlaneradeAktiviteter(String planeradeAktiviteter) {
        this.planeradeAktiviteter = planeradeAktiviteter;
    }

    public List<BestallningHistorik> getBestallningHistorikList() {
        return bestallningHistorikList;
    }

    public void setBestallningHistorikList(List<BestallningHistorik> bestallningHistorikList) {
        this.bestallningHistorikList = bestallningHistorikList;
    }

    public static final class BestallningBuilder {
        private Long id;
        private String tilldeladVardenhetHsaId;
        private String tilldeladVardenhetOrgNr;
        private LocalDateTime orderDatum;
        private LocalDateTime uppdateradDatum;
        private String syfte;
        private String planeradeAktiviteter;
        private List<BestallningHistorik> bestallningHistorikList = new ArrayList<>();

        private BestallningBuilder() {
        }

        public static BestallningBuilder aBestallning() {
            return new BestallningBuilder();
        }

        public BestallningBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public BestallningBuilder withTilldeladVardenhetHsaId(String tilldeladVardenhetHsaId) {
            this.tilldeladVardenhetHsaId = tilldeladVardenhetHsaId;
            return this;
        }

        public BestallningBuilder withTilldeladVardenhetOrgNr(String tilldeladVardenhetOrgNr) {
            this.tilldeladVardenhetOrgNr = tilldeladVardenhetOrgNr;
            return this;
        }

        public BestallningBuilder withOrderDatum(LocalDateTime orderDatum) {
            this.orderDatum = orderDatum;
            return this;
        }

        public BestallningBuilder withUppdateradDatum(LocalDateTime uppdateradDatum) {
            this.uppdateradDatum = uppdateradDatum;
            return this;
        }

        public BestallningBuilder withSyfte(String syfte) {
            this.syfte = syfte;
            return this;
        }

        public BestallningBuilder withPlaneradeAktiviteter(String planeradeAktiviteter) {
            this.planeradeAktiviteter = planeradeAktiviteter;
            return this;
        }

        public BestallningBuilder withBestallningHistorik(List<BestallningHistorik> bestallningHistorikList) {
            this.bestallningHistorikList = bestallningHistorikList;
            return this;
        }

        public Bestallning build() {
            Bestallning bestallning = new Bestallning();
            bestallning.setId(id);
            bestallning.setTilldeladVardenhetHsaId(tilldeladVardenhetHsaId);
            bestallning.setTilldeladVardenhetOrgNr(tilldeladVardenhetOrgNr);
            bestallning.setOrderDatum(orderDatum);
            bestallning.setUppdateradDatum(uppdateradDatum);
            bestallning.setSyfte(syfte);
            bestallning.setPlaneradeAktiviteter(planeradeAktiviteter);
            bestallning.setBestallningHistorikList(bestallningHistorikList);
            return bestallning;
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Bestallning)) {
            return false;
        }
        final Bestallning that = (Bestallning) o;
        return Objects.equals(id, that.id)
                && Objects.equals(tilldeladVardenhetHsaId, that.tilldeladVardenhetHsaId)
                && Objects.equals(tilldeladVardenhetOrgNr, that.tilldeladVardenhetOrgNr)
                && Objects.equals(orderDatum, that.orderDatum)
                && Objects.equals(uppdateradDatum, that.uppdateradDatum)
                && Objects.equals(syfte, that.syfte)
                && Objects.equals(planeradeAktiviteter, that.planeradeAktiviteter)
                && ListUtils.isEqualList(bestallningHistorikList, that.bestallningHistorikList);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, tilldeladVardenhetHsaId, tilldeladVardenhetOrgNr, orderDatum, uppdateradDatum, syfte, planeradeAktiviteter,
                bestallningHistorikList);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("tilldeladVardenhetHsaId", tilldeladVardenhetHsaId)
                .add("tilldeladVardenhetOrgNr", tilldeladVardenhetOrgNr)
                .add("orderDatum", orderDatum)
                .add("uppdateradDatum", uppdateradDatum)
                .add("syfte", syfte)
                .add("planeradeAktiviteter", planeradeAktiviteter)
                .toString();
    }
}
