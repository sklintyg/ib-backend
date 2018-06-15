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
import static se.inera.intyg.intygsbestallning.persistence.model.Besok.BesokBuilder.aBesok;

import com.google.common.base.MoreObjects;
import org.apache.commons.collections4.ListUtils;
import org.hibernate.annotations.Type;
import se.inera.intyg.intygsbestallning.persistence.model.type.BesokStatusTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.DeltagarProfessionTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.KallelseFormTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.TolkStatusTyp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

    @Entity
@Table(name = "BESOK")
public final class Besok {

    @Id
    @GeneratedValue
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "BESOK_START_TID")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    private LocalDateTime besokStartTid;

    @Column(name = "BESOK_SLUT_TID")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    private LocalDateTime besokSlutTid;

    @Column(name = "KALLELSE_DATUM")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    private LocalDateTime kallelseDatum;

    @Column(name = "BESOK_STATUS")
    @Enumerated(value = EnumType.STRING)
    private BesokStatusTyp besokStatus;

    @Column(name = "TOLK_STATUS", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private TolkStatusTyp tolkStatus;

    @Column(name = "KALLELSE_FORM")
    @Enumerated(value = EnumType.STRING)
    private KallelseFormTyp kallelseForm;

    @Column(name = "ERSATTS")
    private Boolean ersatts;

    @Column(name = "DELTAGARE_PROFESSION")
    @Enumerated(value = EnumType.STRING)
    private DeltagarProfessionTyp deltagareProfession;

    @Column(name = "DELTAGARE_FULLSTANDIGT_NAMN")
    private String deltagareFullstandigtNamn;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "AVVIKELSE_ID")
    private Avvikelse avvikelse;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "BESOK_ID", referencedColumnName = "ID", nullable = true)
    private List<Handelse> handelseList = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getBesokStartTid() {
        return besokStartTid;
    }

    public void setBesokStartTid(LocalDateTime besokStartTid) {
        this.besokStartTid = besokStartTid;
    }

    public LocalDateTime getBesokSlutTid() {
        return besokSlutTid;
    }

    public void setBesokSlutTid(final LocalDateTime besokSlutTid) {
        this.besokSlutTid = besokSlutTid;
    }

    public LocalDateTime getKallelseDatum() {
        return kallelseDatum;
    }

    public void setKallelseDatum(LocalDateTime kallelseDatum) {
        this.kallelseDatum = kallelseDatum;
    }

    public BesokStatusTyp getBesokStatus() {
        return besokStatus;
    }

    public void setBesokStatus(BesokStatusTyp besokStatus) {
        this.besokStatus = besokStatus;
    }

    public TolkStatusTyp getTolkStatus() {
        return tolkStatus;
    }

    public void setTolkStatus(TolkStatusTyp tolkStatus) {
        this.tolkStatus = tolkStatus;
    }

    public KallelseFormTyp getKallelseForm() {
        return kallelseForm;
    }

    public void setKallelseForm(KallelseFormTyp kallelseForm) {
        this.kallelseForm = kallelseForm;
    }

    public Boolean getErsatts() {
        return ersatts;
    }

    public void setErsatts(Boolean ersatts) {
        this.ersatts = ersatts;
    }

    public DeltagarProfessionTyp getDeltagareProfession() {
        return deltagareProfession;
    }

    public void setDeltagareProfession(DeltagarProfessionTyp deltagareProfession) {
        this.deltagareProfession = deltagareProfession;
    }

    public String getDeltagareFullstandigtNamn() {
        return deltagareFullstandigtNamn;
    }

    public void setDeltagareFullstandigtNamn(String deltagareFullstandigtNamn) {
        this.deltagareFullstandigtNamn = deltagareFullstandigtNamn;
    }

    public Avvikelse getAvvikelse() {
        return avvikelse;
    }

    public void setAvvikelse(Avvikelse avvikelse) {
        this.avvikelse = avvikelse;
    }

    public List<Handelse> getHandelseList() {
        return handelseList;
    }

    public void setHandelseList(List<Handelse> handelseList) {
        this.handelseList = handelseList;
    }

    public static Besok copyFrom(final Besok besok) {

        if (isNull(besok)) {
            return null;
        }

        return aBesok()
                .withId(besok.getId())
                .withBesokStartTid(besok.getBesokStartTid())
                .withBesokSlutTid(besok.getBesokSlutTid())
                .withKallelseDatum(besok.getKallelseDatum())
                .withBesokStatus(besok.getBesokStatus())
                .withTolkStatus(besok.getTolkStatus())
                .withKallelseForm(besok.getKallelseForm())
                .withErsatts(besok.getErsatts())
                .withDeltagareProfession(besok.getDeltagareProfession())
                .withDeltagareFullstandigtNamn(besok.getDeltagareFullstandigtNamn())
                .withAvvikelse(Avvikelse.copyFrom(besok.getAvvikelse()))
                .withHandelseList(besok.getHandelseList().stream()
                        .map(Handelse::copyFrom)
                        .collect(Collectors.toList()))
                .build();
    }


    public static final class BesokBuilder {
        private Long id;
        private LocalDateTime besokStartTid;
        private LocalDateTime besokSlutTid;
        private LocalDateTime kallelseDatum;
        private BesokStatusTyp besokStatus;
        private TolkStatusTyp tolkStatus;
        private KallelseFormTyp kallelseForm;
        private Boolean ersatts;
        private DeltagarProfessionTyp deltagareProfession;
        private String deltagareFullstandigtNamn;
        private Avvikelse avvikelse;
        private List<Handelse> handelseList = new ArrayList<>();;

        private BesokBuilder() {
        }

        public static BesokBuilder aBesok() {
            return new BesokBuilder();
        }

        public BesokBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public BesokBuilder withBesokStartTid(LocalDateTime besokStartTid) {
            this.besokStartTid = besokStartTid;
            return this;
        }

        public BesokBuilder withBesokSlutTid(LocalDateTime besokSlutTid) {
            this.besokSlutTid = besokSlutTid;
            return this;
        }

        public BesokBuilder withKallelseDatum(LocalDateTime kallelseDatum) {
            this.kallelseDatum = kallelseDatum;
            return this;
        }

        public BesokBuilder withBesokStatus(BesokStatusTyp besokStatus) {
            this.besokStatus = besokStatus;
            return this;
        }

        public BesokBuilder withTolkStatus(TolkStatusTyp tolkStatus) {
            this.tolkStatus = tolkStatus;
            return this;
        }

        public BesokBuilder withKallelseForm(KallelseFormTyp kallelseForm) {
            this.kallelseForm = kallelseForm;
            return this;
        }

        public BesokBuilder withErsatts(Boolean ersatts) {
            this.ersatts = ersatts;
            return this;
        }

        public BesokBuilder withDeltagareProfession(DeltagarProfessionTyp deltagareProfession) {
            this.deltagareProfession = deltagareProfession;
            return this;
        }

        public BesokBuilder withDeltagareFullstandigtNamn(String deltagareFullstandigtNamn) {
            this.deltagareFullstandigtNamn = deltagareFullstandigtNamn;
            return this;
        }

        public BesokBuilder withAvvikelse(Avvikelse avvikelse) {
            this.avvikelse = avvikelse;
            return this;
        }

        public BesokBuilder withHandelseList(List<Handelse> handelseList) {
            this.handelseList = handelseList;
            return this;
        }

        public Besok build() {
            Besok besok = new Besok();
            besok.setId(id);
            besok.setBesokStartTid(besokStartTid);
            besok.setBesokSlutTid(besokSlutTid);
            besok.setKallelseDatum(kallelseDatum);
            besok.setBesokStatus(besokStatus);
            besok.setTolkStatus(tolkStatus);
            besok.setKallelseForm(kallelseForm);
            besok.setErsatts(ersatts);
            besok.setDeltagareProfession(deltagareProfession);
            besok.setDeltagareFullstandigtNamn(deltagareFullstandigtNamn);
            besok.setAvvikelse(avvikelse);
            besok.setHandelseList(handelseList);
            return besok;
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Besok)) {
            return false;
        }
        final Besok besok = (Besok) o;
        return Objects.equals(id, besok.id)
                && Objects.equals(besokStartTid, besok.besokStartTid)
                && Objects.equals(besokSlutTid, besok.besokSlutTid)
                && Objects.equals(kallelseDatum, besok.kallelseDatum)
                && besokStatus == besok.besokStatus
                && tolkStatus == besok.tolkStatus
                && kallelseForm == besok.kallelseForm
                && Objects.equals(ersatts, besok.ersatts)
                && deltagareProfession == besok.deltagareProfession
                && Objects.equals(deltagareFullstandigtNamn, besok.deltagareFullstandigtNamn)
                && Objects.equals(avvikelse, besok.avvikelse)
                && ListUtils.isEqualList(handelseList, besok.handelseList);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id,
                besokStartTid,
                besokSlutTid,
                kallelseDatum,
                besokStatus,
                tolkStatus,
                kallelseForm,
                ersatts,
                deltagareProfession,
                deltagareFullstandigtNamn,
                avvikelse,
                handelseList);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("besokStartTid", besokStartTid)
                .add("besokSlutTid", besokSlutTid)
                .add("kallelseDatum", kallelseDatum)
                .add("besokStatus", besokStatus)
                .add("tolkStatus", tolkStatus)
                .add("kallelseForm", kallelseForm)
                .add("ersatts", ersatts)
                .add("deltagareProfession", deltagareProfession)
                .add("deltagareFullstandigtNamn", deltagareFullstandigtNamn)
                .add("avvikelse", avvikelse)
                .add("handelseList", handelseList)
                .toString();
    }
}
