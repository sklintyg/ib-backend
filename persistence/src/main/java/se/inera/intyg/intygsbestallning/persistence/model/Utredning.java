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
import org.hibernate.annotations.Type;
import se.inera.intyg.intygsbestallning.persistence.model.status.UtredningStatus;
import se.inera.intyg.intygsbestallning.persistence.model.status.UtredningStatusResolver;
import se.inera.intyg.intygsbestallning.persistence.model.type.AvslutOrsak;
import se.inera.intyg.intygsbestallning.persistence.model.type.UtredningsTyp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;
import static se.inera.intyg.intygsbestallning.persistence.model.Utredning.UtredningBuilder.anUtredning;

@Entity
@Table(name = "UTREDNING")
public final class Utredning {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "UTREDNING_ID", nullable = false)
    private Long utredningId;

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

    @Column(name = "AVBRUTEN_ORSAK")
    @Enumerated(EnumType.STRING)
    private AvslutOrsak avbrutenOrsak;

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

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "UTREDNING_ID", referencedColumnName = "UTREDNING_ID", nullable = false)
    private List<Intyg> intygList = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "UTREDNING_ID", referencedColumnName = "UTREDNING_ID", nullable = false)
    private List<Anteckning> anteckningList = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "UTREDNING_ID", referencedColumnName = "UTREDNING_ID", nullable = false)
    private List<SkickadNotifiering> skickadNotifieringList = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "HANDLAGGARE_ID")
    private Handlaggare handlaggare;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "INVANARE_ID")
    private Invanare invanare;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "BETALNING_ID")
    private Betalning betalning;

    @Column(name = "STATUS", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UtredningStatus status;

    public Utredning() {
    }

    public static Utredning copyFrom(final Utredning utredning) {

        if (isNull(utredning)) {
            return null;
        }

        return anUtredning()
                .withUtredningId(utredning.getUtredningId())
                .withUtredningsTyp(utredning.getUtredningsTyp())
                .withBestallning(Bestallning.copyFrom(utredning.getBestallning().orElse(null)))
                .withTolkBehov(utredning.getTolkBehov())
                .withTolkSprak(utredning.getTolkSprak())
                .withExternForfragan(ExternForfragan.copyFrom(utredning.getExternForfragan().orElse(null)))
                .withHandelseList(utredning.getHandelseList().stream()
                        .map(Handelse::copyFrom)
                        .collect(toList()))
                .withHandlingList(utredning.getHandlingList().stream()
                        .map(Handling::copyFrom)
                        .collect(toList()))
                .withBesokList(utredning.getBesokList().stream()
                        .map(Besok::copyFrom)
                        .collect(toList()))
                .withIntygList(utredning.getIntygList().stream()
                        .map(Intyg::copyFrom)
                        .collect(toList()))
                .withAnteckningList(utredning.getAnteckningList().stream()
                        .map(Anteckning::copyFrom)
                        .collect(toList()))
                .withHandlaggare(Handlaggare.copyFrom(utredning.getHandlaggare()))
                .withInvanare(Invanare.copyFrom(utredning.getInvanare()))
                .withAvbrutenDatum(utredning.getAvbrutenDatum())
                .withAvbrutenOrsak(utredning.getAvbrutenOrsak())
                .withBetalning(utredning.getBetalning())
                .withSkickadNotifieringList(utredning.getSkickadNotifieringList().stream()
                        .map(SkickadNotifiering::copyFrom)
                        .collect(toList()))
                .withStatus(utredning.getStatus())
                .build();
    }

    public Long getUtredningId() {
        return utredningId;
    }

    public void setUtredningId(final Long utredningId) {
        this.utredningId = utredningId;
    }

    public UtredningsTyp getUtredningsTyp() {
        return utredningsTyp;
    }

    public void setUtredningsTyp(final UtredningsTyp utredningsTyp) {
        this.utredningsTyp = utredningsTyp;
    }

    public Optional<Bestallning> getBestallning() {
        return Optional.ofNullable(bestallning);
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

    public AvslutOrsak getAvbrutenOrsak() {
        return avbrutenOrsak;
    }

    public void setAvbrutenOrsak(final AvslutOrsak avbrutenOrsak) {
        this.avbrutenOrsak = avbrutenOrsak;
    }

    public Optional<ExternForfragan> getExternForfragan() {
        return Optional.ofNullable(externForfragan);
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

    public Betalning getBetalning() {
        return betalning;
    }

    public void setBetalning(final Betalning betalning) {
        this.betalning = betalning;
    }

    public List<Intyg> getIntygList() {
        return intygList;
    }

    public void setIntygList(List<Intyg> intygList) {
        this.intygList = intygList;
    }

    public List<Anteckning> getAnteckningList() {
        return anteckningList;
    }

    public void setAnteckningList(final List<Anteckning> anteckningList) {
        this.anteckningList = anteckningList;
    }

    public List<SkickadNotifiering> getSkickadNotifieringList() {
        return skickadNotifieringList;
    }

    public void setSkickadNotifieringList(List<SkickadNotifiering> skickadNotifieringList) {
        this.skickadNotifieringList = skickadNotifieringList;
    }

    public UtredningStatus getStatus() {
        return status;
    }

    public void setStatus(UtredningStatus status) {
        this.status = status;
    }

    @PrePersist
    @PreUpdate
    public void resolveStatus() {
        this.setStatus(new UtredningStatusResolver().resolveStatus(this));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Utredning)) {
            return false;
        }

        final Utredning utredning = (Utredning) o;

        if (utredningId != null ? !utredningId.equals(utredning.utredningId) : utredning.utredningId != null) {
            return false;
        }
        if (utredningsTyp != utredning.utredningsTyp) {
            return false;
        }
        if (bestallning != null ? !bestallning.equals(utredning.bestallning) : utredning.bestallning != null) {
            return false;
        }
        if (tolkBehov != null ? !tolkBehov.equals(utredning.tolkBehov) : utredning.tolkBehov != null) {
            return false;
        }
        if (tolkSprak != null ? !tolkSprak.equals(utredning.tolkSprak) : utredning.tolkSprak != null) {
            return false;
        }
        if (avbrutenDatum != null ? !avbrutenDatum.equals(utredning.avbrutenDatum) : utredning.avbrutenDatum != null) {
            return false;
        }
        if (avbrutenOrsak != utredning.avbrutenOrsak) {
            return false;
        }
        if (arkiverad != null ? !arkiverad.equals(utredning.arkiverad) : utredning.arkiverad != null) {
            return false;
        }
        if (externForfragan != null ? !externForfragan.equals(utredning.externForfragan) : utredning.externForfragan != null) {
            return false;
        }
        if (!ListUtils.isEqualList(handelseList, utredning.handelseList)) {
            return false;
        }
        if (!ListUtils.isEqualList(handlingList, utredning.handlingList)) {
            return false;
        }
        if (!ListUtils.isEqualList(besokList, utredning.besokList)) {
            return false;
        }
        if (!ListUtils.isEqualList(intygList, utredning.intygList)) {
            return false;
        }
        if (!ListUtils.isEqualList(anteckningList, utredning.anteckningList)) {
            return false;
        }
        if (!ListUtils.isEqualList(skickadNotifieringList, utredning.skickadNotifieringList)) {
            return false;
        }
        if (handlaggare != null ? !handlaggare.equals(utredning.handlaggare) : utredning.handlaggare != null) {
            return false;
        }
        if (betalning != null ? !betalning.equals(utredning.betalning) : utredning.betalning != null) {
            return false;
        }
        if (status != null ? !status.equals(utredning.status) : utredning.status != null) {
            return false;
        }
        return invanare != null ? invanare.equals(utredning.invanare) : utredning.invanare == null;
    }

    @Override
    public int hashCode() {

        return Objects.hash(utredningId,
                utredningsTyp,
                bestallning,
                tolkBehov,
                tolkSprak,
                avbrutenDatum,
                avbrutenOrsak,
                arkiverad,
                externForfragan,
                handelseList,
                handlingList,
                besokList,
                intygList,
                anteckningList,
                skickadNotifieringList,
                handlaggare,
                invanare,
                betalning,
                status);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("utredningId", utredningId)
                .add("utredningsTyp", utredningsTyp)
                .add("bestallning", bestallning)
                .add("tolkBehov", tolkBehov)
                .add("tolkSprak", tolkSprak)
                .add("avbrutenDatum", avbrutenDatum)
                .add("avbrutenAnledning", avbrutenOrsak)
                .add("arkiverad", arkiverad)
                .add("externForfragan", externForfragan)
                .add("handelseList", handelseList)
                .add("handlingList", handlingList)
                .add("besokList", besokList)
                .add("intygList", intygList)
                .add("anteckningsList", anteckningList)
                .add("skickadNotifieringList", skickadNotifieringList)
                .add("handlaggare", handlaggare)
                .add("invanare", invanare)
                .add("betalning", betalning)
                .add("status", status)
                .toString();
    }

    public static final class UtredningBuilder {
        private Long utredningId;
        private UtredningsTyp utredningsTyp;
        private Bestallning bestallning;
        private Boolean tolkBehov;
        private String tolkSprak;
        private LocalDateTime avbrutenDatum;
        private AvslutOrsak avbrutenOrsak;
        private Boolean arkiverad = false;
        private ExternForfragan externForfragan;
        private List<Handelse> handelseList = new ArrayList<>();
        private List<Handling> handlingList = new ArrayList<>();
        private List<Besok> besokList = new ArrayList<>();
        private List<Intyg> intygList = new ArrayList<>();
        private List<Anteckning> anteckningList = new ArrayList<>();
        private List<SkickadNotifiering> skickadNotifieringList = new ArrayList<>();
        private Handlaggare handlaggare;
        private Invanare invanare;
        private Betalning betalning;
        private UtredningStatus status;

        private UtredningBuilder() {
        }

        public static UtredningBuilder anUtredning() {
            return new UtredningBuilder();
        }

        public UtredningBuilder withUtredningId(Long utredningId) {
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

        public UtredningBuilder withAvbrutenOrsak(AvslutOrsak avbrutenOrsak) {
            this.avbrutenOrsak = avbrutenOrsak;
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

        public UtredningBuilder withBesokList(List<Besok> besokList) {
            this.besokList = besokList;
            return this;
        }

        public UtredningBuilder withIntygList(List<Intyg> intygList) {
            this.intygList = intygList;
            return this;
        }

        public UtredningBuilder withAnteckningList(List<Anteckning> anteckningList) {
            this.anteckningList = anteckningList;
            return this;
        }

        public UtredningBuilder withSkickadNotifieringList(List<SkickadNotifiering> skickadNotifieringList) {
            this.skickadNotifieringList = skickadNotifieringList;
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

        public UtredningBuilder withBetalning(Betalning betalning) {
            this.betalning = betalning;
            return this;
        }

        public UtredningBuilder withStatus(UtredningStatus status) {
            this.status = status;
            return this;
        }

        public Utredning build() {
            Utredning utredning = new Utredning();
            utredning.setUtredningId(utredningId);
            utredning.setUtredningsTyp(utredningsTyp);
            utredning.setBestallning(bestallning);
            utredning.setTolkBehov(tolkBehov);
            utredning.setTolkSprak(tolkSprak);
            utredning.setAvbrutenDatum(avbrutenDatum);
            utredning.setAvbrutenOrsak(avbrutenOrsak);
            utredning.setArkiverad(arkiverad);
            utredning.setExternForfragan(externForfragan);
            utredning.setHandelseList(handelseList);
            utredning.setHandlingList(handlingList);
            utredning.setBesokList(besokList);
            utredning.setIntygList(intygList);
            utredning.setAnteckningList(anteckningList);
            utredning.setSkickadNotifieringList(skickadNotifieringList);
            utredning.setHandlaggare(handlaggare);
            utredning.setInvanare(invanare);
            utredning.setBetalning(betalning);
            utredning.setStatus(status);
            return utredning;
        }
    }
}
