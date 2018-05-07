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
import se.inera.intyg.intygsbestallning.persistence.model.type.AvvikelseOrsak;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "AVVIKELSE")
public class Avvikelse {

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
}
