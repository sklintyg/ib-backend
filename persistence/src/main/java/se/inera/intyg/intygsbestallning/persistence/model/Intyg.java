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
import static se.inera.intyg.intygsbestallning.persistence.model.Intyg.IntygBuilder.anIntyg;

import com.google.common.base.MoreObjects;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "INTYG")
public final class Intyg {

    @Id
    @GeneratedValue
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "KOMPLETTERINGS_ID")
    private String kompletteringsId;

    @Column(name = "SISTA_DATUM")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    private LocalDateTime sistaDatum;

    @Column(name = "MOTTAGET_DATUM")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    private LocalDateTime mottagetDatum;

    @Column(name = "SISTA_DATUM_KOMPLETTERINGSBEGARAN")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    private LocalDateTime sistaDatumKompletteringsbegaran;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKompletteringsId() {
        return kompletteringsId;
    }

    public void setKompletteringsId(String kompletteringsId) {
        this.kompletteringsId = kompletteringsId;
    }

    public LocalDateTime getSistaDatum() {
        return sistaDatum;
    }

    public void setSistaDatum(LocalDateTime sistaDatum) {
        this.sistaDatum = sistaDatum;
    }

    public LocalDateTime getMottagetDatum() {
        return mottagetDatum;
    }

    public void setMottagetDatum(LocalDateTime mottagetDatum) {
        this.mottagetDatum = mottagetDatum;
    }

    public LocalDateTime getSistaDatumKompletteringsbegaran() {
        return sistaDatumKompletteringsbegaran;
    }

    public void setSistaDatumKompletteringsbegaran(LocalDateTime sistaDatumKompletteringsbegaran) {
        this.sistaDatumKompletteringsbegaran = sistaDatumKompletteringsbegaran;
    }

    public static Intyg copyFrom(final Intyg intyg) {

        if (isNull(intyg)) {
            return null;
        }

        return anIntyg()
                .withId(intyg.getId())
                .withKompletteringsId(intyg.getKompletteringsId())
                .withSistaDatum(intyg.getSistaDatum())
                .withMottagetDatum(intyg.getMottagetDatum())
                .withSistaDatumKompletteringsbegaran(intyg.getSistaDatumKompletteringsbegaran())
                .build();
    }

    public static final class IntygBuilder {
        private long id;
        private String kompletteringsId;
        private LocalDateTime sistaDatum;
        private LocalDateTime mottagetDatum;
        private LocalDateTime sistaDatumKompletteringsbegaran;

        private IntygBuilder() {
        }

        public static IntygBuilder anIntyg() {
            return new IntygBuilder();
        }

        public IntygBuilder withId(long id) {
            this.id = id;
            return this;
        }

        public IntygBuilder withKompletteringsId(String kompletteringsId) {
            this.kompletteringsId = kompletteringsId;
            return this;
        }

        public IntygBuilder withSistaDatum(LocalDateTime sistaDatum) {
            this.sistaDatum = sistaDatum;
            return this;
        }

        public IntygBuilder withMottagetDatum(LocalDateTime mottagetDatum) {
            this.mottagetDatum = mottagetDatum;
            return this;
        }

        public IntygBuilder withSistaDatumKompletteringsbegaran(LocalDateTime sistaDatumKompletteringsbegaran) {
            this.sistaDatumKompletteringsbegaran = sistaDatumKompletteringsbegaran;
            return this;
        }

        public Intyg build() {
            Intyg intyg = new Intyg();
            intyg.setId(id);
            intyg.setKompletteringsId(kompletteringsId);
            intyg.setSistaDatum(sistaDatum);
            intyg.setMottagetDatum(mottagetDatum);
            intyg.setSistaDatumKompletteringsbegaran(sistaDatumKompletteringsbegaran);
            return intyg;
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Intyg)) {
            return false;
        }
        final Intyg intyg = (Intyg) o;
        return Objects.equals(id, intyg.id)
                && Objects.equals(kompletteringsId, intyg.kompletteringsId)
                && Objects.equals(sistaDatum, intyg.sistaDatum)
                && Objects.equals(mottagetDatum, intyg.mottagetDatum)
                && Objects.equals(sistaDatumKompletteringsbegaran, intyg.sistaDatumKompletteringsbegaran);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, kompletteringsId, sistaDatum, mottagetDatum, sistaDatumKompletteringsbegaran);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("kompletteringsId", kompletteringsId)
                .add("sistaDatum", sistaDatum)
                .add("mottagetDatum", mottagetDatum)
                .add("sistaDatumKompletteringsbegaran", sistaDatumKompletteringsbegaran)
                .toString();
    }
}
