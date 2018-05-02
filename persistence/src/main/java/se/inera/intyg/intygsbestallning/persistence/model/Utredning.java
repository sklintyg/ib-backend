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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;
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

    @Column(name = "TOLK_BEHOV", columnDefinition = "tinyint(1) default 0")
    private Boolean tolkBehov;

    @Column(name = "TOLK_SPRAK")
    private String tolkSprak;

    @Column(name = "AVBRUTEN_DATUM")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    private LocalDateTime avbrutenDatum;

    @Column(name = "AVBRUTEN_ANLEDNING")
    @Enumerated(EnumType.STRING)
    private EndReason avbrutenAnledning;

    @Column(name = "ARKIVERAD", nullable = false)
    private Boolean arkiverad = false;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "EXTERN_FORFRAGAN_ID")
    private ExternForfragan externForfragan;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "UTREDNING_ID", referencedColumnName = "UTREDNING_ID", nullable = false)
    private List<Handelse> handelseList = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "UTREDNING_ID", referencedColumnName = "UTREDNING_ID", nullable = false)
    private List<Handling> handlingList = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "UTREDNING_ID", referencedColumnName = "UTREDNING_ID", nullable = false)
    private List<Besok> besokList = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "HANDLAGGARE_ID")
    private Handlaggare handlaggare;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "INVANARE_ID")
    private Invanare invanare;

    public String getUtredningId() {
        return utredningId;
    }

    public void setUtredningId(final String utredningId) {
        this.utredningId = utredningId;
    }

    public UtredningsTyp getUtredningsTyp() {
        return utredningsTyp;
    }

    public void setUtredningsTyp(final UtredningsTyp utredningsTyp) {
        this.utredningsTyp = utredningsTyp;
    }

    public Bestallning getBestallning() {
        return bestallning;
    }

    public void setBestallning(final Bestallning bestallning) {
        this.bestallning = bestallning;
    }

    public Boolean getTolkBehov() {
        return tolkBehov;
    }

    public void setTolkBehov(final Boolean tolkBehov) {
        this.tolkBehov = tolkBehov;
    }

    public String getTolkSprak() {
        return tolkSprak;
    }

    public void setTolkSprak(final String tolkSprak) {
        this.tolkSprak = tolkSprak;
    }

    public Boolean getArkiverad() {
        return arkiverad;
    }

    public void setArkiverad(Boolean arkiverad) {
        this.arkiverad = arkiverad;
    }

    public LocalDateTime getAvbrutenDatum() {
        return avbrutenDatum;
    }

    public void setAvbrutenDatum(final LocalDateTime avbrutenDatum) {
        this.avbrutenDatum = avbrutenDatum;
    }

    public List<Besok> getBesokList() {
        return besokList;
    }

    public void setBesokList(List<Besok> besokList) {
        this.besokList = besokList;
    }

    public EndReason getAvbrutenAnledning() {
        return avbrutenAnledning;
    }

    public void setAvbrutenAnledning(final EndReason avbrutenAnledning) {
        this.avbrutenAnledning = avbrutenAnledning;
    }

    public ExternForfragan getExternForfragan() {
        return externForfragan;
    }

    public void setExternForfragan(final ExternForfragan externForfragan) {
        this.externForfragan = externForfragan;
    }

    public List<Handelse> getHandelseList() {
        return handelseList;
    }

    public void setHandelseList(final List<Handelse> handelseList) {
        this.handelseList = handelseList;
    }

    public List<Handling> getHandlingList() {
        return handlingList;
    }

    public void setHandlingList(final List<Handling> handlingList) {
        this.handlingList = handlingList;
    }

    public Handlaggare getHandlaggare() {
        return handlaggare;
    }

    public void setHandlaggare(final Handlaggare handlaggare) {
        this.handlaggare = handlaggare;
    }

    public Invanare getInvanare() {
        return invanare;
    }

    public void setInvanare(final Invanare invanare) {
        this.invanare = invanare;
    }

    @Override
    public int hashCode() {
        return Objects.hash(utredningId);
    }

    public static final class UtredningBuilder {
        private String utredningId;
        private UtredningsTyp utredningsTyp;
        private Bestallning bestallning;
        private Boolean tolkBehov;
        private String tolkSprak;
        private LocalDateTime avbrutenDatum;
        private EndReason avbrutenAnledning;
        private ExternForfragan externForfragan;
        private Boolean arkiverad;
        private List<Handelse> handelseList = new ArrayList<>();
        private List<Handling> handlingList = new ArrayList<>();
        private Handlaggare handlaggare;
        private Invanare invanare;

        private UtredningBuilder() {
        }

        public static UtredningBuilder anUtredning() {
            return new UtredningBuilder();
        }

        public UtredningBuilder withUtredningId(String utredningId) {
            this.utredningId = utredningId;
            return this;
        }

        public UtredningBuilder withUtredningsTyp(UtredningsTyp utredningsTyp) {
            this.utredningsTyp = utredningsTyp;
            return this;
        }

        public UtredningBuilder withBestallning(Bestallning bestallning) {
            this.bestallning = bestallning;
            return this;
        }

        public UtredningBuilder withTolkBehov(Boolean tolkBehov) {
            this.tolkBehov = tolkBehov;
            return this;
        }

        public UtredningBuilder withTolkSprak(String tolkSprak) {
            this.tolkSprak = tolkSprak;
            return this;
        }

        public UtredningBuilder withAvbrutenDatum(LocalDateTime avbrutenDatum) {
            this.avbrutenDatum = avbrutenDatum;
            return this;
        }

        public UtredningBuilder withAvbrutenAnledning(EndReason avbrutenAnledning) {
            this.avbrutenAnledning = avbrutenAnledning;
            return this;
        }

        public UtredningBuilder withArkiverad(Boolean arkiverad) {
            this.arkiverad = arkiverad;
            return this;
        }

        public UtredningBuilder withExternForfragan(ExternForfragan externForfragan) {
            this.externForfragan = externForfragan;
            return this;
        }

        public UtredningBuilder withHandelseList(List<Handelse> handelseList) {
            this.handelseList = handelseList;
            return this;
        }

        public UtredningBuilder withHandlingList(List<Handling> handlingList) {
            this.handlingList = handlingList;
            return this;
        }

        public UtredningBuilder withHandlaggare(Handlaggare handlaggare) {
            this.handlaggare = handlaggare;
            return this;
        }

        public UtredningBuilder withInvanare(Invanare invanare) {
            this.invanare = invanare;
            return this;
        }

        public Utredning build() {
            Utredning utredning = new Utredning();
            utredning.setUtredningId(utredningId);
            utredning.setUtredningsTyp(utredningsTyp);
            utredning.setBestallning(bestallning);
            utredning.setTolkBehov(tolkBehov);
            utredning.setAvbrutenDatum(avbrutenDatum);
            utredning.setAvbrutenAnledning(avbrutenAnledning);
            utredning.setArkiverad(arkiverad);
            utredning.setExternForfragan(externForfragan);
            utredning.setHandelseList(handelseList);
            utredning.setHandlingList(handlingList);
            utredning.setHandlaggare(handlaggare);
            utredning.setInvanare(invanare);
            utredning.tolkSprak = this.tolkSprak;
            return utredning;
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Utredning utredning = (Utredning) o;
        return Objects.equals(utredningId, utredning.utredningId)
                && utredningsTyp == utredning.utredningsTyp
                && Objects.equals(bestallning, utredning.bestallning)
                && Objects.equals(tolkBehov, utredning.tolkBehov)
                && Objects.equals(tolkSprak, utredning.tolkSprak)
                && Objects.equals(avbrutenDatum, utredning.avbrutenDatum)
                && avbrutenAnledning == utredning.avbrutenAnledning
                && arkiverad == utredning.arkiverad
                && Objects.equals(externForfragan, utredning.externForfragan)
                && Objects.equals(handelseList, utredning.handelseList)
                && Objects.equals(handlingList, utredning.handlingList)
                && Objects.equals(handlaggare, utredning.handlaggare)
                && Objects.equals(invanare, utredning.invanare);
    }
}
