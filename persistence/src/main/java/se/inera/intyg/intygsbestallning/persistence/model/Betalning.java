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

    @Column(name = "FAKTURA_VE_ID")
    private String fakturaVeId;

    @Column(name = "FAKTURA_FK_ID")
    private String fakturaFkId;

    @Column(name = "BETALD_FK_ID")
    private String betaldFkId;

    @Column(name = "BETALD_VE_ID")
    private String betaldVeId;

    @Column(name = "BETALNINGS_DATUM")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    private LocalDateTime betalningsDatum;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFakturaFkId() {
        return fakturaFkId;
    }

    public void setFakturaFkId(String fakturaFkId) {
        this.fakturaFkId = fakturaFkId;
    }

    public String getFakturaVeId() {
        return fakturaVeId;
    }

    public void setFakturaVeId(String fakturaVeId) {
        this.fakturaVeId = fakturaVeId;
    }

    public String getBetaldFkId() {
        return betaldFkId;
    }

    public void setBetaldFkId(String betaldFkId) {
        this.betaldFkId = betaldFkId;
    }

    public String getBetaldVeId() {
        return betaldVeId;
    }

    public void setBetaldVeId(String betaldVeId) {
        this.betaldVeId = betaldVeId;
    }

    public LocalDateTime getBetalningsDatum() {
        return betalningsDatum;
    }

    public void setBetalningsDatum(LocalDateTime betalningsDatum) {
        this.betalningsDatum = betalningsDatum;
    }


    public static final class BetalningBuilder {
        private Long id;
        private String fakturaFkId;
        private String fakturaVeId;
        private String betaldFkId;
        private String betaldVeId;
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

        public BetalningBuilder withFakturaVeId(String fakturaVeId) {
            this.fakturaVeId = fakturaVeId;
            return this;
        }

        public BetalningBuilder withFakturaFkId(String fakturaFkId) {
            this.fakturaFkId = fakturaFkId;
            return this;
        }

        public BetalningBuilder withBetaldFkId(String betaldFkId) {
            this.betaldFkId = betaldFkId;
            return this;
        }

        public BetalningBuilder withBetaldVeId(String betaldVeId) {
            this.betaldVeId = betaldVeId;
            return this;
        }

        public BetalningBuilder withBetalningsDatum(LocalDateTime betalningsDatum) {
            this.betalningsDatum = betalningsDatum;
            return this;
        }

        public Betalning build() {
            Betalning betalning = new Betalning();
            betalning.setId(id);
            betalning.setFakturaFkId(fakturaFkId);
            betalning.setFakturaVeId(fakturaVeId);
            betalning.setBetaldFkId(betaldFkId);
            betalning.setBetaldVeId(betaldVeId);
            betalning.setBetalningsDatum(betalningsDatum);
            return betalning;
        }
    }
}
