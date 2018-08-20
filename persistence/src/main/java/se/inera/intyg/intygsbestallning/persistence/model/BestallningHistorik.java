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
import java.util.Objects;

@Entity
@Table(name = "BESTALLNING_HISTORIK")
public final class BestallningHistorik {
    @Id
    @GeneratedValue
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "KOMMENTAR")
    private String kommentar;

    @Column(name = "DATUM")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    private LocalDateTime datum;

    private BestallningHistorik() {
    }

    public Long getId() {
        return id;
    }

    public String getKommentar() {
        return kommentar;
    }

    public LocalDateTime getDatum() {
        return datum;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setKommentar(String kommentar) {
        this.kommentar = kommentar;
    }

    public void setDatum(LocalDateTime datum) {
        this.datum = datum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BestallningHistorik that = (BestallningHistorik) o;
        return Objects.equals(id, that.id)
                && Objects.equals(kommentar, that.kommentar)
                && Objects.equals(datum, that.datum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, kommentar, datum);
    }

    public static final class BestallningHistorikBuilder {
        private Long id;
        private String kommentar;
        private LocalDateTime datum;

        private BestallningHistorikBuilder() {
        }

        public static BestallningHistorikBuilder aBestallningHistorik() {
            return new BestallningHistorikBuilder();
        }

        public BestallningHistorikBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public BestallningHistorikBuilder withKommentar(String kommetar) {
            this.kommentar = kommetar;
            return this;
        }

        public BestallningHistorikBuilder withDatum(LocalDateTime datum) {
            this.datum = datum;
            return this;
        }

        public BestallningHistorik build() {
            BestallningHistorik bestallningHistorik = new BestallningHistorik();
            bestallningHistorik.setId(id);
            bestallningHistorik.setKommentar(kommentar);
            bestallningHistorik.setDatum(datum);
            return bestallningHistorik;
        }
    }

}
