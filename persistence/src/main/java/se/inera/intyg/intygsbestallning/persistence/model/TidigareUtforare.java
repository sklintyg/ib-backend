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
import static se.inera.intyg.intygsbestallning.persistence.model.TidigareUtforare.TidigareUtforareBuilder.aTidigareUtforare;

import com.google.common.base.MoreObjects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "TIDIGARE_UTFORARE")
public final class TidigareUtforare {

    @Id
    @GeneratedValue
    @Column(name = "ID", nullable = false)
    private long id;

    @Column(name = "TIDIGARE_ENHET_ID")
    private String tidigareEnhetId;

    public TidigareUtforare() {
    }

    public static TidigareUtforare copyFrom(final TidigareUtforare tidigareUtforare) {
        if (isNull(tidigareUtforare)) {
            return null;
        }

        return aTidigareUtforare()
                .withId(tidigareUtforare.getId())
                .withTidigareEnhetId(tidigareUtforare.getTidigareEnhetId())
                .build();

    }

    public String getTidigareEnhetId() {
        return tidigareEnhetId;
    }

    public void setTidigareEnhetId(String tidigareEnhetId) {
        this.tidigareEnhetId = tidigareEnhetId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public static final class TidigareUtforareBuilder {
        private long id;
        private String tidigareEnhetId;

        private TidigareUtforareBuilder() {
        }

        public static TidigareUtforareBuilder aTidigareUtforare() {
            return new TidigareUtforareBuilder();
        }

        public TidigareUtforareBuilder withId(long id) {
            this.id = id;
            return this;
        }

        public TidigareUtforareBuilder withTidigareEnhetId(String tidigareEnhetId) {
            this.tidigareEnhetId = tidigareEnhetId;
            return this;
        }

        public TidigareUtforare build() {
            TidigareUtforare tidigareUtforare = new TidigareUtforare();
            tidigareUtforare.setId(id);
            tidigareUtforare.setTidigareEnhetId(tidigareEnhetId);
            return tidigareUtforare;
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TidigareUtforare)) {
            return false;
        }
        final TidigareUtforare that = (TidigareUtforare) o;
        return id == that.id
                && Objects.equals(tidigareEnhetId, that.tidigareEnhetId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, tidigareEnhetId);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("tidigareEnhetId", tidigareEnhetId)
                .toString();
    }
}
