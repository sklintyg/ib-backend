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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "HANDELSE")
public class Handelse {

    @Id
    @GeneratedValue
    @Column(name = "INTERNREFERENS", nullable = false)
    private Long internreferens;

    @Column(name = "UTREDNING_ID", nullable = true)
    private String utredningId;

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

    public Long getInternreferens() {
        return internreferens;
    }

    public void setInternreferens(Long internreferens) {
        this.internreferens = internreferens;
    }

    public String getUtredningId() {
        return utredningId;
    }

    public void setUtredningId(String utredningId) {
        this.utredningId = utredningId;
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
}
