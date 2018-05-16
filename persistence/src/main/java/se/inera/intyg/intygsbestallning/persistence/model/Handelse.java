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
import static se.inera.intyg.intygsbestallning.persistence.model.Handelse.HandelseBuilder.aHandelse;

import com.google.common.base.MoreObjects;
import org.hibernate.annotations.Type;
import se.inera.intyg.intygsbestallning.persistence.model.type.HandelseTyp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "HANDELSE")
public final class Handelse {

    @Id
    @GeneratedValue
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "HANDELSE_TYP", nullable = false)
    @Enumerated(EnumType.STRING)
    private HandelseTyp handelseTyp;

    @Column(name = "SKAPAD", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    private LocalDateTime skapad;

    @Column(name = "ANVANDARE", nullable = true)
    private String anvandare;

    @Column(name = "HANDELSE_TEXT", nullable = false)
    private String handelseText;

    @Column(name = "KOMMENTAR", nullable = true)
    private String kommentar;

    public Handelse() {
    }

    public static Handelse copyFrom(final Handelse handelse) {
        if (isNull(handelse)) {
            return null;
        }

        return aHandelse()
                .withId(handelse.getId())
                .withHandelseTyp(handelse.getHandelseTyp())
                .withSkapad(handelse.getSkapad())
                .withAnvandare(handelse.getAnvandare())
                .withHandelseText(handelse.getHandelseText())
                .withKommentar(handelse.getKommentar())
                .build();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public HandelseTyp getHandelseTyp() {
        return handelseTyp;
    }

    public void setHandelseTyp(HandelseTyp handelseTyp) {
        this.handelseTyp = handelseTyp;
    }

    public LocalDateTime getSkapad() {
        return skapad;
    }

    public void setSkapad(LocalDateTime skapad) {
        this.skapad = skapad;
    }

    public String getAnvandare() {
        return anvandare;
    }

    public void setAnvandare(String anvandare) {
        this.anvandare = anvandare;
    }

    public String getHandelseText() {
        return handelseText;
    }

    public void setHandelseText(String handelseText) {
        this.handelseText = handelseText;
    }

    public String getKommentar() {
        return kommentar;
    }

    public void setKommentar(String kommentar) {
        this.kommentar = kommentar;
    }

    public static final class HandelseBuilder {
        private Long id;
        private HandelseTyp handelseTyp;
        private LocalDateTime skapad;
        private String anvandare;
        private String handelseText;
        private String kommentar;

        private HandelseBuilder() {
        }

        public static HandelseBuilder aHandelse() {
            return new HandelseBuilder();
        }

        public HandelseBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public HandelseBuilder withHandelseTyp(HandelseTyp handelseTyp) {
            this.handelseTyp = handelseTyp;
            return this;
        }

        public HandelseBuilder withSkapad(LocalDateTime skapad) {
            this.skapad = skapad;
            return this;
        }

        public HandelseBuilder withAnvandare(String anvandare) {
            this.anvandare = anvandare;
            return this;
        }

        public HandelseBuilder withHandelseText(String handelseText) {
            this.handelseText = handelseText;
            return this;
        }

        public HandelseBuilder withKommentar(String kommentar) {
            this.kommentar = kommentar;
            return this;
        }

        public Handelse build() {
            Handelse handelse = new Handelse();
            handelse.setId(id);
            handelse.setHandelseTyp(handelseTyp);
            handelse.setSkapad(skapad);
            handelse.setAnvandare(anvandare);
            handelse.setHandelseText(handelseText);
            handelse.setKommentar(kommentar);
            return handelse;
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Handelse)) {
            return false;
        }
        final Handelse handelse = (Handelse) o;
        return Objects.equals(id, handelse.id)
                && handelseTyp == handelse.handelseTyp
                && Objects.equals(skapad, handelse.skapad)
                && Objects.equals(anvandare, handelse.anvandare)
                && Objects.equals(handelseText, handelse.handelseText)
                && Objects.equals(kommentar, handelse.kommentar);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, handelseTyp, skapad, anvandare, handelseText, kommentar);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("handelseTyp", handelseTyp)
                .add("skapad", skapad)
                .add("anvandare", anvandare)
                .add("handelseText", handelseText)
                .add("kommentar", kommentar)
                .toString();
    }
}
