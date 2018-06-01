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

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class BesokListItem {

    private static final DateTimeFormatter TIMEFORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private BesokStatus besokStatus;
    private String besokDatum;
    private String besokStartTid;
    private String besokSlutTid;
    private DeltagarProfessionTyp profession;
    private String namn;
    private TolkStatusTyp tolkStatus;
    private KallelseFormTyp kallelseForm;
    private String kallelseDatum;
    private List<HandelseListItem> handelseList;

    public static BesokListItem from(Besok besok) {
        return BesokListItemBuilder.aBesokListItem()
                .withBesokStatus(BesokStatusResolver.resolveStaticStatus(besok))
                .withBesokDatum(besok.getBesokStartTid().format(DateTimeFormatter.ISO_DATE))
                .withBesokStartTid(besok.getBesokStartTid().format(TIMEFORMATTER))
                .withBesokSlutTid(besok.getBesokSlutTid().format(TIMEFORMATTER))
                .withProfession(besok.getDeltagareProfession())
                .withNamn(besok.getDeltagareFullstandigtNamn())
                .withTolkStatus(besok.getTolkStatus())
                .withKallelseForm(besok.getKallelseForm())
                .withKallelseDatum(besok.getKallelseDatum().format(DateTimeFormatter.ISO_DATE))
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

    public String getBesokDatum() {
        return besokDatum;
    }

    public void setBesokDatum(String besokDatum) {
        this.besokDatum = besokDatum;
    }

    public String getBesokStartTid() {
        return besokStartTid;
    }

    public void setBesokStartTid(String besokStartTid) {
        this.besokStartTid = besokStartTid;
    }

    public String getBesokSlutTid() {
        return besokSlutTid;
    }

    public void setBesokSlutTid(String besokSlutTid) {
        this.besokSlutTid = besokSlutTid;
    }

    public DeltagarProfessionTyp getProfession() {
        return profession;
    }

    public void setProfession(DeltagarProfessionTyp profession) {
        this.profession = profession;
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

    public String getKallelseDatum() {
        return kallelseDatum;
    }

    public void setKallelseDatum(String kallelseDatum) {
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
        private String besokDatum;
        private String besokStartTid;
        private String besokSlutTid;
        private DeltagarProfessionTyp profession;
        private String namn;
        private TolkStatusTyp tolkStatus;
        private KallelseFormTyp kallelseForm;
        private String kallelseDatum;
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

        public BesokListItemBuilder withBesokDatum(String besokDatum) {
            this.besokDatum = besokDatum;
            return this;
        }

        public BesokListItemBuilder withBesokStartTid(String besokStartTid) {
            this.besokStartTid = besokStartTid;
            return this;
        }

        public BesokListItemBuilder withBesokSlutTid(String besokSlutTid) {
            this.besokSlutTid = besokSlutTid;
            return this;
        }

        public BesokListItemBuilder withProfession(DeltagarProfessionTyp profession) {
            this.profession = profession;
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

        public BesokListItemBuilder withKallelseDatum(String kallelseDatum) {
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
            besokListItem.setProfession(profession);
            besokListItem.setTolkStatus(tolkStatus);
            besokListItem.setHandelseList(handelseList);
            return besokListItem;
        }
    }
}
