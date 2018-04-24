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
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "HANDLING")
public class Handling {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private long id;

    @Column(name = "SKICKAT_DATUM")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    private LocalDateTime skickatDatum;

    @Column(name = "INKOM_DATUM")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    private LocalDateTime inkomDatum;

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

    public static final class HandlingBuilder {
        private LocalDateTime skickatDatum;
        private LocalDateTime inkomDatum;

        private HandlingBuilder() {
        }

        public static HandlingBuilder aHandling() {
            return new HandlingBuilder();
        }

        public HandlingBuilder withSkickatDatum(LocalDateTime skickatDatum) {
            this.skickatDatum = skickatDatum;
            return this;
        }

        public HandlingBuilder withInkomDatum(LocalDateTime inkomDatum) {
            this.inkomDatum = inkomDatum;
            return this;
        }

        public Handling build() {
            Handling handling = new Handling();
            handling.setSkickatDatum(skickatDatum);
            handling.setInkomDatum(inkomDatum);
            return handling;
        }
    }
}
