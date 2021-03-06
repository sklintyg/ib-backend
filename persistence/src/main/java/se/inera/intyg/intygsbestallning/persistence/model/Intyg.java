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

import com.google.common.base.MoreObjects;
import org.apache.commons.lang3.BooleanUtils;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;

import static java.util.Objects.isNull;
import static se.inera.intyg.intygsbestallning.persistence.model.Intyg.IntygBuilder.anIntyg;

@Entity
@Table(name = "INTYG")
public final class Intyg {

    @Id
    @GeneratedValue
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "KOMPLETTERING", nullable = false, columnDefinition = "tinyint(1) default 0", updatable = false)
    private Boolean komplettering;

    @Column(name = "SISTA_DATUM")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    private LocalDateTime sistaDatum;

    @Column(name = "SKICKAT_DATUM")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    private LocalDateTime skickatDatum;

    @Column(name = "MOTTAGET_DATUM")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    private LocalDateTime mottagetDatum;

    @Column(name = "SISTA_DATUM_KOMPLETTERINGSBEGARAN")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    private LocalDateTime sistaDatumKompletteringsbegaran;

    @Column(name = "FRAGESTALLNING_MOTTAGEN_DATUM")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    private LocalDateTime fragestallningMottagenDatum;

    public static Intyg copyFrom(final Intyg intyg) {

        if (isNull(intyg)) {
            return null;
        }

        return anIntyg()
                .withId(intyg.getId())
                .withKomplettering(intyg.isKomplettering())
                .withSistaDatum(intyg.getSistaDatum())
                .withMottagetDatum(intyg.getMottagetDatum())
                .withSkickatDatum(intyg.getSkickatDatum())
                .withSistaDatumKompletteringsbegaran(intyg.getSistaDatumKompletteringsbegaran())
                .withFragestallningMottagenDatum(intyg.getFragestallningMottagenDatum())
                .build();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean isKomplettering() {
        return BooleanUtils.toBoolean(komplettering);
    }

    public void setKomplettering(Boolean komplettering) {
        this.komplettering = komplettering;
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

    public LocalDateTime getSkickatDatum() {
        return skickatDatum;
    }

    public void setSkickatDatum(LocalDateTime skickatDatum) {
        this.skickatDatum = skickatDatum;
    }

    public LocalDateTime getFragestallningMottagenDatum() {
        return fragestallningMottagenDatum;
    }

    public void setFragestallningMottagenDatum(LocalDateTime fragestallningMottagenDatum) {
        this.fragestallningMottagenDatum = fragestallningMottagenDatum;
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
                && Objects.equals(komplettering, intyg.komplettering)
                && Objects.equals(sistaDatum, intyg.sistaDatum)
                && Objects.equals(skickatDatum, intyg.skickatDatum)
                && Objects.equals(mottagetDatum, intyg.mottagetDatum)
                && Objects.equals(sistaDatumKompletteringsbegaran, intyg.sistaDatumKompletteringsbegaran)
                && Objects.equals(fragestallningMottagenDatum, intyg.fragestallningMottagenDatum);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, komplettering, sistaDatum, skickatDatum, mottagetDatum, sistaDatumKompletteringsbegaran,
                fragestallningMottagenDatum);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("komplettering", komplettering)
                .add("sistaDatum", sistaDatum)
                .add("skickatDatum", skickatDatum)
                .add("mottagetDatum", mottagetDatum)
                .add("sistaDatumKompletteringsbegaran", sistaDatumKompletteringsbegaran)
                .add("fragestallningMottagenDatum", fragestallningMottagenDatum)
                .toString();
    }

    public static final class IntygBuilder {
        private Long id;
        private Boolean komplettering;
        private LocalDateTime sistaDatum;
        private LocalDateTime skickatDatum;
        private LocalDateTime mottagetDatum;
        private LocalDateTime sistaDatumKompletteringsbegaran;
        private LocalDateTime fragestallningMottagenDatum;

        private IntygBuilder() {
        }

        public static IntygBuilder anIntyg() {
            return new IntygBuilder();
        }

        public IntygBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public IntygBuilder withKomplettering(Boolean komplettering) {
            this.komplettering = komplettering;
            return this;
        }

        public IntygBuilder withSistaDatum(LocalDateTime sistaDatum) {
            this.sistaDatum = sistaDatum;
            return this;
        }

        public IntygBuilder withSkickatDatum(LocalDateTime skickatDatum) {
            this.skickatDatum = skickatDatum;
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

        public IntygBuilder withFragestallningMottagenDatum(LocalDateTime fragestallningMottagenDatum) {
            this.fragestallningMottagenDatum = fragestallningMottagenDatum;
            return this;
        }

        public Intyg build() {
            Intyg intyg = new Intyg();
            intyg.setId(id);
            intyg.setKomplettering(komplettering);
            intyg.setSistaDatum(sistaDatum);
            intyg.setSkickatDatum(skickatDatum);
            intyg.setMottagetDatum(mottagetDatum);
            intyg.setSistaDatumKompletteringsbegaran(sistaDatumKompletteringsbegaran);
            intyg.setFragestallningMottagenDatum(fragestallningMottagenDatum);
            return intyg;
        }
    }
}
