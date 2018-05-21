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

import se.inera.intyg.intygsbestallning.persistence.model.Intyg;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.service.stateresolver.ErsattsResolver;
import se.inera.intyg.intygsbestallning.service.stateresolver.UtredningStatusResolver;
import se.inera.intyg.intygsbestallning.service.stateresolver.UtredningStatus;
import se.inera.intyg.intygsbestallning.service.util.BusinessDaysBean;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.FreeTextSearchable;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.VardgivareEnrichable;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;

import static java.util.Objects.nonNull;

public class AvslutadBestallningListItem implements FreeTextSearchable, VardgivareEnrichable {

    private Long utredningsId;
    private String utredningsTyp;
    private String vardgivareHsaId;
    private String vardgivareNamn;

    private UtredningStatus status;

    private String avslutsDatum;
    private boolean ersatts;
    private String fakturerad;
    private String utbetald;

    public static AvslutadBestallningListItem from(Utredning utredning, UtredningStatusResolver utredningStatusResolver,
                                                   BusinessDaysBean businessDays) {

        return AvslutadBestallningListItemBuilder.anAvslutadBestallningListItem()
                .withUtredningsId(utredning.getUtredningId())
                .withUtredningsTyp(utredning.getUtredningsTyp().name())
                .withStatus(utredningStatusResolver.resolveStatus(utredning))
                .withVardgivareHsaId(utredning.getExternForfragan().getLandstingHsaId())
                .withVardgivareNamn("Enriched later")
                .withAvslutsDatum(resolveAvslutsDatum(utredning))
                .withErsatts(ErsattsResolver.resolveUtredningErsatts(utredning, businessDays))
                .withFakturerad(nonNull(utredning.getBetalning()) ? utredning.getBetalning().getFakturaId() : null)
                .withUtbetald(nonNull(utredning.getBetalning()) ? utredning.getBetalning().getBetalningsId() : null)
                .build();
    }



    private static String resolveAvslutsDatum(Utredning utredning) {
        if (utredning.getAvbrutenDatum() != null) {
            return utredning.getAvbrutenDatum().format(DateTimeFormatter.ISO_DATE);
        }

        // Find the highest mottaget datum.
        return utredning.getIntygList().stream()
                .filter(intyg -> intyg.getMottagetDatum() != null)
                .max(Comparator.comparing(Intyg::getMottagetDatum))
                .map(intyg -> intyg.getMottagetDatum().format(DateTimeFormatter.ISO_DATE))
                .orElse("Fixme");
    }

    public Long getUtredningsId() {
        return utredningsId;
    }

    public void setUtredningsId(Long utredningsId) {
        this.utredningsId = utredningsId;
    }

    public String getUtredningsTyp() {
        return utredningsTyp;
    }

    public void setUtredningsTyp(String utredningsTyp) {
        this.utredningsTyp = utredningsTyp;
    }

    @Override
    public String getVardgivareHsaId() {
        return vardgivareHsaId;
    }

    public void setVardgivareHsaId(String vardgivareHsaId) {
        this.vardgivareHsaId = vardgivareHsaId;
    }

    public String getVardgivareNamn() {
        return vardgivareNamn;
    }

    @Override
    public void setVardgivareNamn(String vardgivareNamn) {
        this.vardgivareNamn = vardgivareNamn;
    }

    public UtredningStatus getStatus() {
        return status;
    }

    public void setStatus(UtredningStatus status) {
        this.status = status;
    }

    public String getAvslutsDatum() {
        return avslutsDatum;
    }

    public void setAvslutsDatum(String avslutsDatum) {
        this.avslutsDatum = avslutsDatum;
    }

    public boolean getErsatts() {
        return ersatts;
    }

    public void setErsatts(boolean ersatts) {
        this.ersatts = ersatts;
    }

    public String getFakturerad() {
        return fakturerad;
    }

    public void setFakturerad(String fakturerad) {
        this.fakturerad = fakturerad;
    }

    public String getUtbetald() {
        return utbetald;
    }

    public void setUtbetald(String utbetald) {
        this.utbetald = utbetald;
    }

    @Override
    public String toSearchString() {
        return utredningsId + " "
                + utredningsTyp + " "
                + vardgivareHsaId + " "
                + vardgivareNamn + " "
                + status.getLabel() + " "
                + avslutsDatum + " "
                + (ersatts ? "Ja" : "Nej") + " "
                + fakturerad + " "
                + utbetald;
    }

    public static final class AvslutadBestallningListItemBuilder {
        private Long utredningsId;
        private String utredningsTyp;
        private String vardgivareHsaId;
        private String vardgivareNamn;
        private UtredningStatus status;
        private String avslutsDatum;
        private boolean ersatts;
        private String fakturerad;
        private String utbetald;

        private AvslutadBestallningListItemBuilder() {
        }

        public static AvslutadBestallningListItemBuilder anAvslutadBestallningListItem() {
            return new AvslutadBestallningListItemBuilder();
        }

        public AvslutadBestallningListItemBuilder withUtredningsId(Long utredningsId) {
            this.utredningsId = utredningsId;
            return this;
        }

        public AvslutadBestallningListItemBuilder withUtredningsTyp(String utredningsTyp) {
            this.utredningsTyp = utredningsTyp;
            return this;
        }

        public AvslutadBestallningListItemBuilder withVardgivareHsaId(String vardgivareHsaId) {
            this.vardgivareHsaId = vardgivareHsaId;
            return this;
        }

        public AvslutadBestallningListItemBuilder withVardgivareNamn(String vardgivareNamn) {
            this.vardgivareNamn = vardgivareNamn;
            return this;
        }

        public AvslutadBestallningListItemBuilder withStatus(UtredningStatus status) {
            this.status = status;
            return this;
        }

        public AvslutadBestallningListItemBuilder withAvslutsDatum(String avslutsDatum) {
            this.avslutsDatum = avslutsDatum;
            return this;
        }

        public AvslutadBestallningListItemBuilder withErsatts(boolean ersatts) {
            this.ersatts = ersatts;
            return this;
        }

        public AvslutadBestallningListItemBuilder withFakturerad(String fakturerad) {
            this.fakturerad = fakturerad;
            return this;
        }

        public AvslutadBestallningListItemBuilder withUtbetald(String utbetald) {
            this.utbetald = utbetald;
            return this;
        }


        public AvslutadBestallningListItem build() {
            AvslutadBestallningListItem avslutadBestallningListItem = new AvslutadBestallningListItem();
            avslutadBestallningListItem.setUtredningsId(utredningsId);
            avslutadBestallningListItem.setUtredningsTyp(utredningsTyp);
            avslutadBestallningListItem.setVardgivareHsaId(vardgivareHsaId);
            avslutadBestallningListItem.setVardgivareNamn(vardgivareNamn);
            avslutadBestallningListItem.setStatus(status);
            avslutadBestallningListItem.setAvslutsDatum(avslutsDatum);
            avslutadBestallningListItem.setErsatts(ersatts);
            avslutadBestallningListItem.setFakturerad(fakturerad);
            avslutadBestallningListItem.setUtbetald(utbetald);
            return avslutadBestallningListItem;
        }
    }
}
