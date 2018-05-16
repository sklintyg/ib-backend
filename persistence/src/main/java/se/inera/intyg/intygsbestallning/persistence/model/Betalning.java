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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "BETALNING")
public final class Betalning {

    @Id
    @GeneratedValue
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "FAKTURA_ID")
    private String fakturaId;

    @Column(name = "UTBETALNINGS_ID")
    private String utbetalningsId;

    @Column(name = "BETALNINGS_ID")
    private String betalningsId;

    @Column(name = "BETALNINGS_DATUM")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    private LocalDateTime betalningsDatum;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFakturaId() {
        return fakturaId;
    }

    public void setFakturaId(String fakturaId) {
        this.fakturaId = fakturaId;
    }

    public String getUtbetalningsId() {
        return utbetalningsId;
    }

    public void setUtbetalningsId(String utbetalningsId) {
        this.utbetalningsId = utbetalningsId;
    }

    public String getBetalningsId() {
        return betalningsId;
    }

    public void setBetalningsId(String betalningsId) {
        this.betalningsId = betalningsId;
    }

    public LocalDateTime getBetalningsDatum() {
        return betalningsDatum;
    }

    public void setBetalningsDatum(LocalDateTime betalningsDatum) {
        this.betalningsDatum = betalningsDatum;
    }


    public static final class BetalningBuilder {
        private Long id;
        private String fakturaId;
        private String utbetalningsId;
        private String betalningsId;
        private LocalDateTime betalningsDatum;

        private BetalningBuilder() {
        }

        public static BetalningBuilder aBetalning() {
            return new BetalningBuilder();
        }

        public BetalningBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public BetalningBuilder withFakturaId(String fakturaId) {
            this.fakturaId = fakturaId;
            return this;
        }

        public BetalningBuilder withUtbetalningsId(String utbetalningsId) {
            this.utbetalningsId = utbetalningsId;
            return this;
        }

        public BetalningBuilder withBetalningsId(String betalningsId) {
            this.betalningsId = betalningsId;
            return this;
        }

        public BetalningBuilder withBetalningsDatum(LocalDateTime betalningsDatum) {
            this.betalningsDatum = betalningsDatum;
            return this;
        }

        public Betalning build() {
            Betalning betalning = new Betalning();
            betalning.setId(id);
            betalning.setFakturaId(fakturaId);
            betalning.setUtbetalningsId(utbetalningsId);
            betalning.setBetalningsId(betalningsId);
            betalning.setBetalningsDatum(betalningsDatum);
            return betalning;
        }
    }
}
