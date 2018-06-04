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
import static se.inera.intyg.intygsbestallning.persistence.model.SkickadNotifiering.SkickadNotifieringBuilder.aSkickadNotifiering;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Type;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringMottagarTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringTyp;

@Entity
@Table(name = "SKICKAD_NOTIFIERING")
public final class SkickadNotifiering {

    @Id
    @GeneratedValue
    @Column(name = "ID", nullable = false)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "TYP", nullable = false)
    private NotifieringTyp typ;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "MOTTAGARE", nullable = false)
    private NotifieringMottagarTyp mottagare;

    @Column(name = "SKICKAD")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    private LocalDateTime skickad;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NotifieringTyp getTyp() {
        return typ;
    }

    public void setTyp(NotifieringTyp typ) {
        this.typ = typ;
    }

    public NotifieringMottagarTyp getMottagare() {
        return mottagare;
    }

    public void setMottagare(NotifieringMottagarTyp mottagare) {
        this.mottagare = mottagare;
    }

    public LocalDateTime getSkickad() {
        return skickad;
    }

    public void setSkickad(LocalDateTime skickad) {
        this.skickad = skickad;
    }

    public static SkickadNotifiering copyFrom(final SkickadNotifiering skickadNotifiering) {
        if (isNull(skickadNotifiering)) {
            return null;
        }

        return aSkickadNotifiering()
                .withId(skickadNotifiering.getId())
                .withTyp(skickadNotifiering.getTyp())
                .withMottagare(skickadNotifiering.getMottagare())
                .withSkickad(skickadNotifiering.getSkickad())
                .build();
    }
    
    // CHECKSTYLE:OFF MagicNumber

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SkickadNotifiering that = (SkickadNotifiering) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(typ, that.typ)
                .append(mottagare, that.mottagare)
                .append(skickad, that.skickad)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(typ)
                .append(mottagare)
                .append(skickad)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("typ", typ)
                .append("mottagare", mottagare)
                .append("skickad", skickad)
                .toString();
    }

    public static final class SkickadNotifieringBuilder {
        private Long id;
        private NotifieringTyp typ;
        private NotifieringMottagarTyp mottagare;
        private LocalDateTime skickad;

        private SkickadNotifieringBuilder() {
        }

        public static SkickadNotifieringBuilder aSkickadNotifiering() {
            return new SkickadNotifieringBuilder();
        }

        public SkickadNotifieringBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public SkickadNotifieringBuilder withTyp(NotifieringTyp typ) {
            this.typ = typ;
            return this;
        }

        public SkickadNotifieringBuilder withMottagare(NotifieringMottagarTyp mottagare) {
            this.mottagare = mottagare;
            return this;
        }

        public SkickadNotifieringBuilder withSkickad(LocalDateTime skickad) {
            this.skickad = skickad;
            return this;
        }

        public SkickadNotifiering build() {
            SkickadNotifiering skickadNotifiering = new SkickadNotifiering();
            skickadNotifiering.setId(id);
            skickadNotifiering.setTyp(typ);
            skickadNotifiering.setMottagare(mottagare);
            skickadNotifiering.skickad = this.skickad;
            return skickadNotifiering;
        }
    }

    // CHECKSTYLE:ON MagicNumber
}
