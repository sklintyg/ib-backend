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
@Table(name = "ANTECKNING")
public class Anteckning {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "VARDENHET_HSA_ID", nullable = false)
    private String vardenhetHsaId;

    @Column(name = "TEXT", nullable = false)
    private String text;

    @Column(name = "ANVANDARE", nullable = false)
    private String anvandare;

    @Column(name = "SKAPAT", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    private LocalDateTime skapat;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVardenhetHsaId() {
        return vardenhetHsaId;
    }

    public void setVardenhetHsaId(String vardenhetHsaId) {
        this.vardenhetHsaId = vardenhetHsaId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAnvandare() {
        return anvandare;
    }

    public void setAnvandare(String anvandare) {
        this.anvandare = anvandare;
    }

    public LocalDateTime getSkapat() {
        return skapat;
    }

    public void setSkapat(LocalDateTime skapat) {
        this.skapat = skapat;
    }

    public static final class AnteckningBuilder {
        private String vardenhetHsaId;
        private String text;
        private String anvandare;
        private LocalDateTime skapat;

        private AnteckningBuilder() {
        }

        public static AnteckningBuilder anAnteckning() {
            return new AnteckningBuilder();
        }

        public AnteckningBuilder withVardenhetHsaId(String vardenhetHsaId) {
            this.vardenhetHsaId = vardenhetHsaId;
            return this;
        }

        public AnteckningBuilder withText(String text) {
            this.text = text;
            return this;
        }

        public AnteckningBuilder withAnvandare(String anvandare) {
            this.anvandare = anvandare;
            return this;
        }

        public AnteckningBuilder withSkapat(LocalDateTime skapat) {
            this.skapat = skapat;
            return this;
        }

        public Anteckning build() {
            Anteckning anteckning = new Anteckning();
            anteckning.setVardenhetHsaId(vardenhetHsaId);
            anteckning.setText(text);
            anteckning.setAnvandare(anvandare);
            anteckning.setSkapat(skapat);
            return anteckning;
        }
    }
}
