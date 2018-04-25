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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "INTERN_FORFRAGAN")
public class InternForfragan {

    @Id
    @GeneratedValue
    @Column(name = "ID", nullable = false)
    private long id;

    @Column(name = "VARDENHET_HSA_ID", nullable = false)
    private String vardenhetHsaId;

    @Column(name = "TILLDELAD_DATUM")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    private LocalDateTime tilldeladDatum;

    @Column(name = "BESVARAS_SENAST_DATUM", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    private LocalDateTime besvarasSenastDatum;

    @Column(name = "SKAPAD_DATUM")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    private LocalDateTime skapadDatum;

    @Column(name = "KOMMENTAR")
    private String kommentar;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "FORFRAGAN_SVAR_ID")
    private ForfraganSvar forfraganSvar;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getVardenhetHsaId() {
        return vardenhetHsaId;
    }

    public void setVardenhetHsaId(String vardenhetHsaId) {
        this.vardenhetHsaId = vardenhetHsaId;
    }

    public LocalDateTime getTilldeladDatum() {
        return tilldeladDatum;
    }

    public void setTilldeladDatum(LocalDateTime tilldeladDatum) {
        this.tilldeladDatum = tilldeladDatum;
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

    public ForfraganSvar getForfraganSvar() {
        return forfraganSvar;
    }

    public void setForfraganSvar(ForfraganSvar forfraganSvar) {
        this.forfraganSvar = forfraganSvar;
    }

    public LocalDateTime getSkapadDatum() {
        return skapadDatum;
    }

    public void setSkapadDatum(LocalDateTime skapadDatum) {
        this.skapadDatum = skapadDatum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        InternForfragan internForfragan = (InternForfragan) o;
        return Objects.equals(id, internForfragan.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public static final class InternForfraganBuilder {
        private String vardenhetHsaId;
        private LocalDateTime tilldeladDatum;
        private LocalDateTime besvarasSenastDatum;
        private LocalDateTime skapadDatum;
        private String kommentar;
        private ForfraganSvar forfraganSvar;

        private InternForfraganBuilder() {
        }

        public static InternForfraganBuilder anInternForfragan() {
            return new InternForfraganBuilder();
        }

        public InternForfraganBuilder withVardenhetHsaId(String vardenhetHsaId) {
            this.vardenhetHsaId = vardenhetHsaId;
            return this;
        }

        public InternForfraganBuilder withTilldeladDatum(LocalDateTime tilldeladDatum) {
            this.tilldeladDatum = tilldeladDatum;
            return this;
        }

        public InternForfraganBuilder withBesvarasSenastDatum(LocalDateTime besvarasSenastDatum) {
            this.besvarasSenastDatum = besvarasSenastDatum;
            return this;
        }

        public InternForfraganBuilder withSkapadDatum(LocalDateTime skapadDatum) {
            this.skapadDatum = skapadDatum;
            return this;
        }

        public InternForfraganBuilder withKommentar(String kommentar) {
            this.kommentar = kommentar;
            return this;
        }

        public InternForfraganBuilder withForfraganSvar(ForfraganSvar forfraganSvar) {
            this.forfraganSvar = forfraganSvar;
            return this;
        }

        public InternForfragan build() {
            InternForfragan internForfragan = new InternForfragan();
            internForfragan.setVardenhetHsaId(vardenhetHsaId);
            internForfragan.setTilldeladDatum(tilldeladDatum);
            internForfragan.setBesvarasSenastDatum(besvarasSenastDatum);
            internForfragan.setKommentar(kommentar);
            internForfragan.setForfraganSvar(forfraganSvar);
            internForfragan.setSkapadDatum(skapadDatum);
            return internForfragan;
        }
    }
}
