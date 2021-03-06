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

import com.google.common.base.MoreObjects;
import org.apache.commons.collections4.ListUtils;

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
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static se.inera.intyg.intygsbestallning.persistence.model.Invanare.InvanareBuilder.anInvanare;

@Entity
@Table(name = "INVANARE")
public final class Invanare {
    //CHECKSTYLE:OFF MagicNumber

    @Id
    @GeneratedValue
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "PERSON_ID", length = 64)
    private String personId;

    @Column(name = "FORNAMN", length = 64)
    private String fornamn;

    @Column(name = "MELLANNAMN", length = 64)
    private String mellannamn;

    @Column(name = "EFTERNAMN", length = 64)
    private String efternamn;

    @Column(name = "SARSKILDA_BEHOV")
    private String sarskildaBehov;

    @Column(name = "BAKGRUND_NULAGE")
    private String bakgrundNulage;

    @Column(name = "POSTORT", length = 255)
    private String postort;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "INVANARE_ID", referencedColumnName = "ID", nullable = false)
    private List<TidigareUtforare> tidigareUtforare = new ArrayList<>();

    //CHECKSTYLE:ON MagicNumber
    public Invanare() {
    }

    public static Invanare copyFrom(final Invanare invanare) {
        if (isNull(invanare)) {
            return null;
        }

        return anInvanare()
                .withId(invanare.getId())
                .withPersonId(invanare.getPersonId())
                .withFornamn(invanare.getFornamn())
                .withMellannamn(invanare.getMellannamn())
                .withEfternamn(invanare.getEfternamn())
                .withSarskildaBehov(invanare.getSarskildaBehov())
                .withBakgrundNulage(invanare.getBakgrundNulage())
                .withPostort(invanare.getPostort())
                .withTidigareUtforare(invanare.getTidigareUtforare().stream()
                        .map(TidigareUtforare::copyFrom)
                        .collect(Collectors.toList()))
                .build();

    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getFornamn() {
        return fornamn;
    }

    public void setFornamn(String fornamn) {
        this.fornamn = fornamn;
    }

    public String getMellannamn() {
        return mellannamn;
    }

    public void setMellannamn(String mellannamn) {
        this.mellannamn = mellannamn;
    }

    public String getEfternamn() {
        return efternamn;
    }

    public void setEfternamn(String efternamn) {
        this.efternamn = efternamn;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPostort() {
        return postort;
    }

    public void setPostort(String postort) {
        this.postort = postort;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Invanare)) {
            return false;
        }

        final Invanare invanare = (Invanare) o;

        if (!Objects.equals(id, invanare.id)) {
            return false;
        }
        if (personId != null ? !personId.equals(invanare.personId) : invanare.personId != null) {
            return false;
        }
        if (fornamn != null ? !fornamn.equals(invanare.fornamn) : invanare.fornamn != null) {
            return false;
        }
        if (mellannamn != null ? !mellannamn.equals(invanare.mellannamn) : invanare.mellannamn != null) {
            return false;
        }
        if (efternamn != null ? !efternamn.equals(invanare.efternamn) : invanare.efternamn != null) {
            return false;
        }
        if (sarskildaBehov != null ? !sarskildaBehov.equals(invanare.sarskildaBehov) : invanare.sarskildaBehov != null) {
            return false;
        }
        if (bakgrundNulage != null ? !bakgrundNulage.equals(invanare.bakgrundNulage) : invanare.bakgrundNulage != null) {
            return false;
        }
        if (postort != null ? !postort.equals(invanare.postort) : invanare.postort != null) {
            return false;
        }
        return ListUtils.isEqualList(tidigareUtforare, invanare.tidigareUtforare);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, personId, fornamn, mellannamn, efternamn, sarskildaBehov, bakgrundNulage, postort, tidigareUtforare);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("personId", personId)
                .add("fornamn", fornamn)
                .add("mellannamn", mellannamn)
                .add("efternamn", efternamn)
                .add("sarskildaBehov", sarskildaBehov)
                .add("bakgrundNulage", bakgrundNulage)
                .add("postort", postort)
                .add("tidigareUtforare", tidigareUtforare)
                .toString();
    }

    public static final class InvanareBuilder {
        private Long id;
        private String personId;
        private String fornamn;
        private String mellannamn;
        private String efternamn;
        private String sarskildaBehov;
        private String bakgrundNulage;
        private String postort;
        private List<TidigareUtforare> tidigareUtforare = new ArrayList<>();

        private InvanareBuilder() {
        }

        public static InvanareBuilder anInvanare() {
            return new InvanareBuilder();
        }

        public InvanareBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public InvanareBuilder withPersonId(String personId) {
            this.personId = personId;
            return this;
        }

        public InvanareBuilder withFornamn(String fornamn) {
            this.fornamn = fornamn;
            return this;
        }

        public InvanareBuilder withMellannamn(String mellannamn) {
            this.mellannamn = mellannamn;
            return this;
        }

        public InvanareBuilder withEfternamn(String efternamn) {
            this.efternamn = efternamn;
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

        public InvanareBuilder withPostort(String postort) {
            this.postort = postort;
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
            invanare.setFornamn(fornamn);
            invanare.setMellannamn(mellannamn);
            invanare.setEfternamn(efternamn);
            invanare.setSarskildaBehov(sarskildaBehov);
            invanare.setBakgrundNulage(bakgrundNulage);
            invanare.setPostort(postort);
            invanare.setTidigareUtforare(tidigareUtforare);
            return invanare;
        }
    }
}
