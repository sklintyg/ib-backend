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
import static se.inera.intyg.intygsbestallning.persistence.model.Handling.HandlingBuilder.aHandling;

import com.google.common.base.MoreObjects;
import org.hibernate.annotations.Type;
import se.inera.intyg.intygsbestallning.persistence.model.type.HandlingUrsprungTyp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "HANDLING")
public final class Handling {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private long id;

    @Column(name = "SKICKAT_DATUM", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    private LocalDateTime skickatDatum;

    @Column(name = "INKOM_DATUM")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    private LocalDateTime inkomDatum;

    @Column(name = "URSPRUNG", nullable = false)
    @Enumerated(EnumType.STRING)
    private HandlingUrsprungTyp ursprung;

    public Handling() {
    }

    public static Handling from(final Handling handling) {
        if (isNull(handling)) {
            return null;
        }

        return aHandling()
                .withId(handling.getId())
                .withSkickatDatum(handling.getSkickatDatum())
                .withInkomDatum(handling.getInkomDatum())
                .withUrsprung(handling.getUrsprung())
                .build();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getSkickatDatum() {
        return skickatDatum;
    }

    public void setSkickatDatum(LocalDateTime skickatDatum) {
        this.skickatDatum = skickatDatum;
    }

    public LocalDateTime getInkomDatum() {
        return inkomDatum;
    }

    public void setInkomDatum(LocalDateTime inkomDatum) {
        this.inkomDatum = inkomDatum;
    }

    public HandlingUrsprungTyp getUrsprung() {
        return ursprung;
    }

    public void setUrsprung(HandlingUrsprungTyp ursprung) {
        this.ursprung = ursprung;
    }

    public static final class HandlingBuilder {
        private long id;
        private LocalDateTime skickatDatum;
        private LocalDateTime inkomDatum;
        private HandlingUrsprungTyp ursprung;

        private HandlingBuilder() {
        }

        public static HandlingBuilder aHandling() {
            return new HandlingBuilder();
        }

        public HandlingBuilder withId(long id) {
            this.id = id;
            return this;
        }

        public HandlingBuilder withSkickatDatum(LocalDateTime skickatDatum) {
            this.skickatDatum = skickatDatum;
            return this;
        }

        public HandlingBuilder withInkomDatum(LocalDateTime inkomDatum) {
            this.inkomDatum = inkomDatum;
            return this;
        }

        public HandlingBuilder withUrsprung(HandlingUrsprungTyp ursprung) {
            this.ursprung = ursprung;
            return this;
        }

        public Handling build() {
            Handling handling = new Handling();
            handling.setId(id);
            handling.setSkickatDatum(skickatDatum);
            handling.setInkomDatum(inkomDatum);
            handling.setUrsprung(ursprung);
            return handling;
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Handling)) {
            return false;
        }
        final Handling handling = (Handling) o;
        return id == handling.id
                && Objects.equals(skickatDatum, handling.skickatDatum)
                && Objects.equals(inkomDatum, handling.inkomDatum)
                && ursprung == handling.ursprung;
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, skickatDatum, inkomDatum, ursprung);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("skickatDatum", skickatDatum)
                .add("inkomDatum", inkomDatum)
                .add("ursprung", ursprung)
                .toString();
    }
}
