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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TIDIGARE_UTFORARE")
public class TidigareUtforare {

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private long id;

    @Column(name = "TIDIGARE_ENHET_ID")
    private String tidigareEnhetId;

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
        private String tidigareEnhetId;

        private TidigareUtforareBuilder() {
        }

        public static TidigareUtforareBuilder aTidigareUtforare() {
            return new TidigareUtforareBuilder();
        }

        public TidigareUtforareBuilder withTidigareEnhetId(String tidigareEnhetId) {
            this.tidigareEnhetId = tidigareEnhetId;
            return this;
        }

        public TidigareUtforare build() {
            TidigareUtforare tidigareUtforare = new TidigareUtforare();
            tidigareUtforare.setTidigareEnhetId(tidigareEnhetId);
            return tidigareUtforare;
        }
    }
}
