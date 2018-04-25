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

@Entity
@Table(name = "EXTERN_FORFRAGAN")
public class ExternForfragan {

    @Id
    @GeneratedValue
    @Column(name = "ID", nullable = false)
    private long id;

    @Column(name = "LANDSTING_HSA_ID", nullable = false)
    private String landstingHsaId;

    @Column(name = "BESVARAS_SENAST_DATUM", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    private LocalDateTime besvarasSenastDatum;

    @Column(name = "KOMMENTAR")
    private String kommentar;

    @Column(name = "AVVISAT_KOMMENTAR")
    private String avvisatKommentar;

    @Column(name = "AVVISAT_DATUM")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    private LocalDateTime avvisatDatum;

    @Column(name = "INKOM_DATUM", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    private LocalDateTime inkomDatum;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "EXTERN_FORFRAGAN_ID", referencedColumnName = "ID", nullable = false)
    private List<InternForfragan> internForfraganList = new ArrayList<>();

    public String getLandstingHsaId() {
        return landstingHsaId;
    }

    public void setLandstingHsaId(String landstingHsaId) {
        this.landstingHsaId = landstingHsaId;
    }

    public String getAvvisatKommentar() {
        return avvisatKommentar;
    }

    public void setAvvisatKommentar(String avvisatKommentar) {
        this.avvisatKommentar = avvisatKommentar;
    }

    public LocalDateTime getAvvisatDatum() {
        return avvisatDatum;
    }

    public void setAvvisatDatum(LocalDateTime avvisatDatum) {
        this.avvisatDatum = avvisatDatum;
    }

    public LocalDateTime getBesvarasSenastDatum() {
        return besvarasSenastDatum;
    }

    public void setBesvarasSenastDatum(LocalDateTime besvarasSenastDatum) {
        this.besvarasSenastDatum = besvarasSenastDatum;
    }

    public String getKommentar() {
        return kommentar;
    }

    public void setKommentar(String kommentar) {
        this.kommentar = kommentar;
    }

    public List<InternForfragan> getInternForfraganList() {
        return internForfraganList;
    }

    public void setInternForfraganList(List<InternForfragan> internForfraganList) {
        this.internForfraganList = internForfraganList;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getInkomDatum() {
        return inkomDatum;
    }

    public void setInkomDatum(LocalDateTime inkomDatum) {
        this.inkomDatum = inkomDatum;
    }

    public static final class ExternForfraganBuilder {
        private String landstingHsaId;
        private LocalDateTime besvarasSenastDatum;
        private String kommentar;
        private String avvisatKommentar;
        private LocalDateTime avvisatDatum;
        private LocalDateTime inkomDatum;
        private List<InternForfragan> internForfraganList = new ArrayList<>();

        private ExternForfraganBuilder() {
        }

        public static ExternForfraganBuilder anExternForfragan() {
            return new ExternForfraganBuilder();
        }

        public ExternForfraganBuilder withLandstingHsaId(String landstingHsaId) {
            this.landstingHsaId = landstingHsaId;
            return this;
        }

        public ExternForfraganBuilder withBesvarasSenastDatum(LocalDateTime besvarasSenastDatum) {
            this.besvarasSenastDatum = besvarasSenastDatum;
            return this;
        }

        public ExternForfraganBuilder withKommentar(String kommentar) {
            this.kommentar = kommentar;
            return this;
        }

        public ExternForfraganBuilder withAvvisatKommentar(String avvisatKommentar) {
            this.avvisatKommentar = avvisatKommentar;
            return this;
        }

        public ExternForfraganBuilder withAvvisatDatum(LocalDateTime avvisatDatum) {
            this.avvisatDatum = avvisatDatum;
            return this;
        }

        public ExternForfraganBuilder withInkomDatum(LocalDateTime inkomDatum) {
            this.inkomDatum = inkomDatum;
            return this;
        }

        public ExternForfraganBuilder withInternForfraganList(List<InternForfragan> internForfraganList) {
            this.internForfraganList = internForfraganList;
            return this;
        }

        public ExternForfragan build() {
            ExternForfragan externForfragan = new ExternForfragan();
            externForfragan.setLandstingHsaId(landstingHsaId);
            externForfragan.setBesvarasSenastDatum(besvarasSenastDatum);
            externForfragan.setKommentar(kommentar);
            externForfragan.setAvvisatKommentar(avvisatKommentar);
            externForfragan.setAvvisatDatum(avvisatDatum);
            externForfragan.setInternForfraganList(internForfraganList);
            externForfragan.setInkomDatum(inkomDatum);
            return externForfragan;
        }
    }
}