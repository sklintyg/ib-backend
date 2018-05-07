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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "BESOK")
public class Besok {

    @Id
    @GeneratedValue
    @Column(name = "ID", nullable = false)
    private long id;

    @Column(name = "BESOK_TID")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    private LocalDateTime besokTid;

    @Column(name = "KALLELSE_DATUM")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    private LocalDateTime kallelseDatum;

    @Column(name = "BESOK_STATUS")
    @Enumerated(value = EnumType.STRING)
    private BesokStatusTyp besokStatus;

    @Column(name = "TOLK_STATUS")
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getBesokTid() {
        return besokTid;
    }

    public void setBesokTid(LocalDateTime besokTid) {
        this.besokTid = besokTid;
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

    public static final class BesokBuilder {
        private LocalDateTime besokTid;
        private LocalDateTime kallelseDatum;
        private BesokStatusTyp besokStatus;
        private TolkStatusTyp tolkStatus;
        private KallelseFormTyp kallelseForm;
        private Boolean ersatts;
        private DeltagarProfessionTyp deltagareProfession;
        private String deltagareFullstandigtNamn;
        private Avvikelse avvikelse;

        private BesokBuilder() {
        }

        public static BesokBuilder aBesok() {
            return new BesokBuilder();
        }

        public BesokBuilder withBesokTid(LocalDateTime besokTid) {
            this.besokTid = besokTid;
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

        public Besok build() {
            Besok besok = new Besok();
            besok.setBesokTid(besokTid);
            besok.setKallelseDatum(kallelseDatum);
            besok.setBesokStatus(besokStatus);
            besok.setTolkStatus(tolkStatus);
            besok.setKallelseForm(kallelseForm);
            besok.setErsatts(ersatts);
            besok.setDeltagareProfession(deltagareProfession);
            besok.setDeltagareFullstandigtNamn(deltagareFullstandigtNamn);
            besok.setAvvikelse(avvikelse);
            return besok;
        }
    }
}
