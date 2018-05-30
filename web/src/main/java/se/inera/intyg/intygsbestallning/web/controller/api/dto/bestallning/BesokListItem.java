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
package se.inera.intyg.intygsbestallning.web.controller.api.dto.bestallning;

import se.inera.intyg.intygsbestallning.persistence.model.Besok;
import se.inera.intyg.intygsbestallning.persistence.model.type.DeltagarProfessionTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.KallelseFormTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.TolkStatusTyp;
import se.inera.intyg.intygsbestallning.service.stateresolver.BesokStatus;
import se.inera.intyg.intygsbestallning.service.stateresolver.BesokStatusResolver;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.HandelseListItem;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

public class BesokListItem {

    private BesokStatus besokStatus;
    private LocalDate besokDatum;
    private LocalTime besokStartTid;
    private LocalTime besokSlutTid;
    private DeltagarProfessionTyp proffesion;
    private String namn;
    private TolkStatusTyp tolkStatus;
    private KallelseFormTyp kallelseForm;
    private LocalDateTime kallelseDatum;
    private List<HandelseListItem> handelseList;

    public static BesokListItem from(Besok besok) {
        return BesokListItemBuilder.aBesokListItem()
                .withBesokStatus(BesokStatusResolver.resolveStaticStatus(besok))
                .withBesokDatum(besok.getBesokStartTid().toLocalDate())
                .withBesokStartTid(besok.getBesokStartTid().toLocalTime())
                .withBesokSlutTid(besok.getBesokSlutTid().toLocalTime())
                .withProffesion(besok.getDeltagareProfession())
                .withNamn(besok.getDeltagareFullstandigtNamn())
                .withTolkStatus(besok.getTolkStatus())
                .withKallelseForm(besok.getKallelseForm())
                .withKallelseDatum(besok.getKallelseDatum())
                .withHandelseList(besok.getHandelseList().stream()
                        .map(handelse ->  HandelseListItem.from(handelse, true))
                        .collect(Collectors.toList()))
                .build();
    }

    public BesokStatus getBesokStatus() {
        return besokStatus;
    }

    public void setBesokStatus(BesokStatus besokStatus) {
        this.besokStatus = besokStatus;
    }

    public LocalDate getBesokDatum() {
        return besokDatum;
    }

    public void setBesokDatum(LocalDate besokDatum) {
        this.besokDatum = besokDatum;
    }

    public LocalTime getBesokStartTid() {
        return besokStartTid;
    }

    public void setBesokStartTid(LocalTime besokStartTid) {
        this.besokStartTid = besokStartTid;
    }

    public LocalTime getBesokSlutTid() {
        return besokSlutTid;
    }

    public void setBesokSlutTid(LocalTime besokSlutTid) {
        this.besokSlutTid = besokSlutTid;
    }

    public DeltagarProfessionTyp getProffesion() {
        return proffesion;
    }

    public void setProffesion(DeltagarProfessionTyp proffesion) {
        this.proffesion = proffesion;
    }

    public String getNamn() {
        return namn;
    }

    public void setNamn(String namn) {
        this.namn = namn;
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

    public LocalDateTime getKallelseDatum() {
        return kallelseDatum;
    }

    public void setKallelseDatum(LocalDateTime kallelseDatum) {
        this.kallelseDatum = kallelseDatum;
    }

    public List<HandelseListItem> getHandelseList() {
        return handelseList;
    }

    public void setHandelseList(List<HandelseListItem> handelseList) {
        this.handelseList = handelseList;
    }

    public static final class  BesokListItemBuilder {
        private BesokStatus besokStatus;
        private LocalDate besokDatum;
        private LocalTime besokStartTid;
        private LocalTime besokSlutTid;
        private DeltagarProfessionTyp proffesion;
        private String namn;
        private TolkStatusTyp tolkStatus;
        private KallelseFormTyp kallelseForm;
        private LocalDateTime kallelseDatum;
        private List<HandelseListItem> handelseList;

        private BesokListItemBuilder() {
        }

        public static BesokListItem.BesokListItemBuilder aBesokListItem() {
            return new BesokListItem.BesokListItemBuilder();
        }

        public BesokListItemBuilder withBesokStatus(BesokStatus besokStatus) {
            this.besokStatus = besokStatus;
            return this;
        }

        public BesokListItemBuilder withBesokDatum(LocalDate besokDatum) {
            this.besokDatum = besokDatum;
            return this;
        }

        public BesokListItemBuilder withBesokStartTid(LocalTime besokStartTid) {
            this.besokStartTid = besokStartTid;
            return this;
        }

        public BesokListItemBuilder withBesokSlutTid(LocalTime besokSlutTid) {
            this.besokSlutTid = besokSlutTid;
            return this;
        }

        public BesokListItemBuilder withProffesion(DeltagarProfessionTyp proffesion) {
            this.proffesion = proffesion;
            return this;
        }

        public BesokListItemBuilder withNamn(String namn) {
            this.namn = namn;
            return this;
        }

        public BesokListItemBuilder withTolkStatus(TolkStatusTyp tolkStatus) {
            this.tolkStatus = tolkStatus;
            return this;
        }

        public BesokListItemBuilder withKallelseForm(KallelseFormTyp kallelseForm) {
            this.kallelseForm = kallelseForm;
            return this;
        }

        public BesokListItemBuilder withKallelseDatum(LocalDateTime kallelseDatum) {
            this.kallelseDatum = kallelseDatum;
            return this;
        }

        public BesokListItemBuilder withHandelseList(List<HandelseListItem> handelseList) {
            this.handelseList = handelseList;
            return this;
        }

        public BesokListItem build() {
            BesokListItem besokListItem = new BesokListItem();
            besokListItem.setBesokDatum(besokDatum);
            besokListItem.setBesokSlutTid(besokSlutTid);
            besokListItem.setBesokStartTid(besokStartTid);
            besokListItem.setBesokStatus(besokStatus);
            besokListItem.setKallelseDatum(kallelseDatum);
            besokListItem.setKallelseForm(kallelseForm);
            besokListItem.setNamn(namn);
            besokListItem.setProffesion(proffesion);
            besokListItem.setTolkStatus(tolkStatus);
            besokListItem.setHandelseList(handelseList);
            return besokListItem;
        }
    }
}
