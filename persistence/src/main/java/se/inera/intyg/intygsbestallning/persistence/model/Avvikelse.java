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
import static se.inera.intyg.intygsbestallning.persistence.model.Avvikelse.AvvikelseBuilder.anAvvikelse;

import com.google.common.base.MoreObjects;
import org.hibernate.annotations.Type;
import se.inera.intyg.intygsbestallning.persistence.model.type.AvvikelseOrsak;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "AVVIKELSE")
public final class Avvikelse {

    @Id
    @Column(name = "AVVIKELSE_ID")
    private String avvikelseId;

    @Column(name = "ORSAKAT_AV")
    @Enumerated(EnumType.STRING)
    private AvvikelseOrsak orsakatAv;

    @Column(name = "BESKRIVNING")
    private String beskrivning;

    @Column(name = "TIDPUNKT")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    private LocalDateTime tidpunkt;

    @Column(name = "INVANARE_UTEBLEV", columnDefinition = "tinyint(1) default 0")
    private Boolean invanareUteblev;

    public static Avvikelse copyFrom(final Avvikelse avvikelse) {
        if (isNull(avvikelse)) {
            return null;
        }

        return anAvvikelse()
                .withAvvikelseId(avvikelse.getAvvikelseId())
                .withOrsakatAv(avvikelse.getOrsakatAv())
                .withBeskrivning(avvikelse.getBeskrivning())
                .withTidpunkt(avvikelse.getTidpunkt())
                .withInvanareUteblev(avvikelse.getInvanareUteblev())
                .build();
    }

    public String getAvvikelseId() {
        return avvikelseId;
    }

    public void setAvvikelseId(String avvikelseId) {
        this.avvikelseId = avvikelseId;
    }

    public AvvikelseOrsak getOrsakatAv() {
        return orsakatAv;
    }

    public void setOrsakatAv(AvvikelseOrsak orsakatAv) {
        this.orsakatAv = orsakatAv;
    }

    public String getBeskrivning() {
        return beskrivning;
    }

    public void setBeskrivning(String beskrivning) {
        this.beskrivning = beskrivning;
    }

    public LocalDateTime getTidpunkt() {
        return tidpunkt;
    }

    public void setTidpunkt(LocalDateTime tidpunkt) {
        this.tidpunkt = tidpunkt;
    }

    public Boolean getInvanareUteblev() {
        return invanareUteblev;
    }

    public void setInvanareUteblev(Boolean invanareUteblev) {
        this.invanareUteblev = invanareUteblev;
    }

    public static final class AvvikelseBuilder {
        private String avvikelseId;
        private AvvikelseOrsak orsakatAv;
        private String beskrivning;
        private LocalDateTime tidpunkt;
        private Boolean invanareUteblev;

        private AvvikelseBuilder() {
        }

        public static AvvikelseBuilder anAvvikelse() {
            return new AvvikelseBuilder();
        }

        public AvvikelseBuilder withAvvikelseId(String avvikelseId) {
            this.avvikelseId = avvikelseId;
            return this;
        }

        public AvvikelseBuilder withOrsakatAv(AvvikelseOrsak orsakatAv) {
            this.orsakatAv = orsakatAv;
            return this;
        }

        public AvvikelseBuilder withBeskrivning(String beskrivning) {
            this.beskrivning = beskrivning;
            return this;
        }

        public AvvikelseBuilder withTidpunkt(LocalDateTime tidpunkt) {
            this.tidpunkt = tidpunkt;
            return this;
        }

        public AvvikelseBuilder withInvanareUteblev(Boolean invanareUteblev) {
            this.invanareUteblev = invanareUteblev;
            return this;
        }

        public Avvikelse build() {
            Avvikelse avvikelse = new Avvikelse();
            avvikelse.setAvvikelseId(avvikelseId);
            avvikelse.setOrsakatAv(orsakatAv);
            avvikelse.setBeskrivning(beskrivning);
            avvikelse.setTidpunkt(tidpunkt);
            avvikelse.setInvanareUteblev(invanareUteblev);
            return avvikelse;
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Avvikelse)) {
            return false;
        }
        final Avvikelse avvikelse = (Avvikelse) o;
        return Objects.equals(avvikelseId, avvikelse.avvikelseId)
                && orsakatAv == avvikelse.orsakatAv
                && Objects.equals(beskrivning, avvikelse.beskrivning)
                && Objects.equals(tidpunkt, avvikelse.tidpunkt)
                && Objects.equals(invanareUteblev, avvikelse.invanareUteblev);
    }

    @Override
    public int hashCode() {

        return Objects.hash(avvikelseId, orsakatAv, beskrivning, tidpunkt, invanareUteblev);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("avvikelseId", avvikelseId)
                .add("orsakatAv", orsakatAv)
                .add("beskrivning", beskrivning)
                .add("tidpunkt", tidpunkt)
                .add("invanareUteblev", invanareUteblev)
                .toString();
    }
}
