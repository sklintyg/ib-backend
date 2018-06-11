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
import static se.inera.intyg.intygsbestallning.persistence.model.InternForfragan.InternForfraganBuilder.anInternForfragan;

import com.google.common.base.MoreObjects;
import org.hibernate.annotations.Type;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;
import se.inera.intyg.intygsbestallning.persistence.model.status.InternForfraganStatus;

@Entity
@Table(name = "INTERN_FORFRAGAN")
public final class InternForfragan {

    @Id
    @GeneratedValue
    @Column(name = "ID", nullable = false)
    private Long id;

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

    @Column(name = "DIREKTTILLDELAD")
    private Boolean direkttilldelad;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "FORFRAGAN_SVAR_ID")
    private ForfraganSvar forfraganSvar;

    @Column(name = "STATUS", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private InternForfraganStatus status;

    public InternForfragan() {
    }

    public static InternForfragan from(final InternForfragan internForfragan) {

        if (isNull(internForfragan)) {
            return null;
        }

        return anInternForfragan()
                .withId(internForfragan.getId())
                .withVardenhetHsaId(internForfragan.getVardenhetHsaId())
                .withTilldeladDatum(internForfragan.getTilldeladDatum())
                .withBesvarasSenastDatum(internForfragan.getBesvarasSenastDatum())
                .withSkapadDatum(internForfragan.getSkapadDatum())
                .withKommentar(internForfragan.getKommentar())
                .withForfraganSvar(ForfraganSvar.copyFrom(internForfragan.getForfraganSvar()))
                .withDirekttilldelad(internForfragan.getDirekttilldelad())
                .build();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Boolean getDirekttilldelad() {
        return direkttilldelad;
    }

    public void setDirekttilldelad(Boolean direkttilldelad) {
        this.direkttilldelad = direkttilldelad;
    }

    public InternForfraganStatus getStatus() {
        return status;
    }

    public void setStatus(InternForfraganStatus status) {
        this.status = status;
    }

    public static final class InternForfraganBuilder {
        private Long id;
        private String vardenhetHsaId;
        private LocalDateTime tilldeladDatum;
        private LocalDateTime besvarasSenastDatum;
        private LocalDateTime skapadDatum;
        private String kommentar;
        private ForfraganSvar forfraganSvar;
        private Boolean direkttilldelad;
        private InternForfraganStatus status;

        private InternForfraganBuilder() {
        }

        public static InternForfraganBuilder anInternForfragan() {
            return new InternForfraganBuilder();
        }

        public InternForfraganBuilder withId(Long id) {
            this.id = id;
            return this;
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

        public InternForfraganBuilder withDirekttilldelad(Boolean direkttilldelad) {
            this.direkttilldelad = direkttilldelad;
            return this;
        }

        public InternForfraganBuilder withStatus(InternForfraganStatus status) {
            this.status = status;
            return this;
        }

        public InternForfragan build() {
            InternForfragan internForfragan = new InternForfragan();
            internForfragan.setId(id);
            internForfragan.setVardenhetHsaId(vardenhetHsaId);
            internForfragan.setTilldeladDatum(tilldeladDatum);
            internForfragan.setBesvarasSenastDatum(besvarasSenastDatum);
            internForfragan.setSkapadDatum(skapadDatum);
            internForfragan.setKommentar(kommentar);
            internForfragan.setForfraganSvar(forfraganSvar);
            internForfragan.setDirekttilldelad(direkttilldelad);
            internForfragan.setStatus(status);
            return internForfragan;
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InternForfragan)) {
            return false;
        }
        final InternForfragan that = (InternForfragan) o;
        return Objects.equals(id, that.id)
                && Objects.equals(vardenhetHsaId, that.vardenhetHsaId)
                && Objects.equals(tilldeladDatum, that.tilldeladDatum)
                && Objects.equals(besvarasSenastDatum, that.besvarasSenastDatum)
                && Objects.equals(skapadDatum, that.skapadDatum)
                && Objects.equals(kommentar, that.kommentar)
                && Objects.equals(forfraganSvar, that.forfraganSvar)
                && Objects.equals(direkttilldelad, that.direkttilldelad)
                && Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, vardenhetHsaId, tilldeladDatum, besvarasSenastDatum, skapadDatum, kommentar, forfraganSvar,
                direkttilldelad, status);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("vardenhetHsaId", vardenhetHsaId)
                .add("tilldeladDatum", tilldeladDatum)
                .add("besvarasSenastDatum", besvarasSenastDatum)
                .add("skapadDatum", skapadDatum)
                .add("kommentar", kommentar)
                .add("forfraganSvar", forfraganSvar)
                .add("direktTilldelad", direkttilldelad)
                .add("status", status)
                .toString();
    }
}
