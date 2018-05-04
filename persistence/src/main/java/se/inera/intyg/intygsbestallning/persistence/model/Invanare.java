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
import static se.inera.intyg.intygsbestallning.persistence.model.Invanare.InvanareBuilder.anInvanare;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "INVANARE")
public class Invanare {

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private long id;

    @Column(name = "PERSON_ID")
    private String personId;

    @Column(name = "SARSKILDA_BEHOV")
    private String sarskildaBehov;

    @Column(name = "BAKGRUND_NULAGE")
    private String bakgrundNulage;

    @Column(name = "POSTKOD")
    private String postkod;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "INVANARE_ID", referencedColumnName = "ID", nullable = false)
    private List<TidigareUtforare> tidigareUtforare = new ArrayList<>();

    public Invanare() {
    }

    public static Invanare from(final Invanare invanare) {
        if (isNull(invanare)) {
            return null;
        }

        return anInvanare()
                .withId(invanare.getId())
                .withPersonId(invanare.getPersonId())
                .withSarskildaBehov(invanare.getSarskildaBehov())
                .withBakgrundNulage(invanare.getBakgrundNulage())
                .withPostkod(invanare.getPostkod())
                .withTidigareUtforare(invanare.getTidigareUtforare().stream()
                        .map(TidigareUtforare::from)
                        .collect(Collectors.toList()))
                .build();

    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getSarskildaBehov() {
        return sarskildaBehov;
    }

    public void setSarskildaBehov(String sarskildaBehov) {
        this.sarskildaBehov = sarskildaBehov;
    }

    public String getBakgrundNulage() {
        return bakgrundNulage;
    }

    public void setBakgrundNulage(String bakgrundNulage) {
        this.bakgrundNulage = bakgrundNulage;
    }

    public List<TidigareUtforare> getTidigareUtforare() {
        return tidigareUtforare;
    }

    public void setTidigareUtforare(List<TidigareUtforare> tidigareUtforare) {
        this.tidigareUtforare = tidigareUtforare;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPostkod() {
        return postkod;
    }

    public void setPostkod(String postkod) {
        this.postkod = postkod;
    }

    public static final class InvanareBuilder {
        private long id;
        private String personId;
        private String sarskildaBehov;
        private String bakgrundNulage;
        private String postkod;
        private List<TidigareUtforare> tidigareUtforare = new ArrayList<>();

        private InvanareBuilder() {
        }

        public static InvanareBuilder anInvanare() {
            return new InvanareBuilder();
        }

        public InvanareBuilder withId(long id) {
            this.id = id;
            return this;
        }

        public InvanareBuilder withPersonId(String personId) {
            this.personId = personId;
            return this;
        }

        public InvanareBuilder withSarskildaBehov(String sarskildaBehov) {
            this.sarskildaBehov = sarskildaBehov;
            return this;
        }

        public InvanareBuilder withBakgrundNulage(String bakgrundNulage) {
            this.bakgrundNulage = bakgrundNulage;
            return this;
        }

        public InvanareBuilder withPostkod(String postkod) {
            this.postkod = postkod;
            return this;
        }

        public InvanareBuilder withTidigareUtforare(List<TidigareUtforare> tidigareUtforare) {
            this.tidigareUtforare = tidigareUtforare;
            return this;
        }

        public Invanare build() {
            Invanare invanare = new Invanare();
            invanare.setId(id);
            invanare.setPersonId(personId);
            invanare.setSarskildaBehov(sarskildaBehov);
            invanare.setBakgrundNulage(bakgrundNulage);
            invanare.setPostkod(postkod);
            invanare.setTidigareUtforare(tidigareUtforare);
            return invanare;
        }
    }
}
