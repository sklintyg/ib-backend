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
import se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringTyp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "NOTIFIERING")
public class Notifiering {

    @Id
    @GeneratedValue
    @Column(name = "ID", nullable = false)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "NOTIFIERING_TYP", nullable = false)
    private NotifieringTyp notifieringTyp;

    @Column(name = "NOTIFIERING_SKICKAD")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    private LocalDateTime notifieringSkickad;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NotifieringTyp getNotifieringTyp() {
        return notifieringTyp;
    }

    public void setNotifieringTyp(NotifieringTyp notifieringTyp) {
        this.notifieringTyp = notifieringTyp;
    }

    public LocalDateTime getNotifieringSkickad() {
        return notifieringSkickad;
    }

    public void setNotifieringSkickad(LocalDateTime notifieringSkickad) {
        this.notifieringSkickad = notifieringSkickad;
    }

    public static final class NotifieringBuilder {
        private Long id;
        private NotifieringTyp notifieringTyp;
        private LocalDateTime notifieringSkickad;

        private NotifieringBuilder() {
        }

        public static NotifieringBuilder aNotifiering() {
            return new NotifieringBuilder();
        }

        public NotifieringBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public NotifieringBuilder withNotifieringTyp(NotifieringTyp notifieringTyp) {
            this.notifieringTyp = notifieringTyp;
            return this;
        }

        public NotifieringBuilder withNotifieringSkickad(LocalDateTime notifieringSkickad) {
            this.notifieringSkickad = notifieringSkickad;
            return this;
        }

        public Notifiering build() {
            Notifiering notifiering = new Notifiering();
            notifiering.setId(id);
            notifiering.setNotifieringTyp(notifieringTyp);
            notifiering.setNotifieringSkickad(notifieringSkickad);
            return notifiering;
        }
    }
}
