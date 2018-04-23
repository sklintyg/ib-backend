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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "UTREDNING")
public class Utredning {

    @Id
    @Column(name = "UTREDNING_ID")
    private String utredningId;

    @Column(name = "UTREDNINGS_TYP", nullable = false)
    @Enumerated(EnumType.STRING)
    private UtredningsTyp utredningsTyp;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "BESTALLNING_ID")
    private Bestallning bestallning;

    // TODO: ENUM?
    @Column(name = "SPRAK_TOLK")
    private String sprakTolk;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "EXTERN_FORFRAGAN_ID")
    private ExternForfragan externForfragan;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "UTREDNING_ID", referencedColumnName = "UTREDNING_ID", nullable = false)
    private List<Handelse> handelseList = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "UTREDNING_ID", referencedColumnName = "UTREDNING_ID", nullable = false)
    private List<Handling> handlingList = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "HANDLAGGARE_ID")
    private Handlaggare handlaggare;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "INVANARE_ID")
    private Invanare invanare;

    public String getUtredningId() {
        return utredningId;
    }

    public void setUtredningId(String utredningId) {
        this.utredningId = utredningId;
    }

    public UtredningsTyp getUtredningsTyp() {
        return utredningsTyp;
    }

    public void setUtredningsTyp(UtredningsTyp utredningsTyp) {
        this.utredningsTyp = utredningsTyp;
    }

    public String getSprakTolk() {
        return sprakTolk;
    }

    public void setSprakTolk(String sprakTolk) {
        this.sprakTolk = sprakTolk;
    }

    public List<Handelse> getHandelseList() {
        return handelseList;
    }

    public void setHandelseList(List<Handelse> handelseList) {
        this.handelseList = handelseList;
    }

    public Bestallning getBestallning() {
        return bestallning;
    }

    public void setBestallning(Bestallning bestallning) {
        this.bestallning = bestallning;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Utredning forfragan = (Utredning) o;
        return Objects.equals(utredningId, forfragan.utredningId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(utredningId);
    }

    public List<Handling> getHandlingList() {
        return handlingList;
    }

    public void setHandlingList(List<Handling> handlingList) {
        this.handlingList = handlingList;
    }

    public ExternForfragan getExternForfragan() {
        return externForfragan;
    }

    public void setExternForfragan(ExternForfragan externForfragan) {
        this.externForfragan = externForfragan;
    }

    public Handlaggare getHandlaggare() {
        return handlaggare;
    }

    public void setHandlaggare(Handlaggare handlaggare) {
        this.handlaggare = handlaggare;
    }

    public Invanare getInvanare() {
        return invanare;
    }

    public void setInvanare(Invanare invanare) {
        this.invanare = invanare;
    }
}
