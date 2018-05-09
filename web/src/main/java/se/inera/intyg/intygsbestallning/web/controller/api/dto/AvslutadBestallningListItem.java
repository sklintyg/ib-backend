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
package se.inera.intyg.intygsbestallning.web.controller.api.dto;

import se.inera.intyg.intygsbestallning.persistence.model.Intyg;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.type.EndReason;
import se.inera.intyg.intygsbestallning.service.stateresolver.UtredningStateResolver;
import se.inera.intyg.intygsbestallning.service.stateresolver.UtredningStatus;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Optional;

import static java.util.Objects.nonNull;

public class AvslutadBestallningListItem implements FreeTextSearchable, VardgivareEnrichable {

    private String utredningsId;
    private String utredningsTyp;
    private String vardgivareHsaId;
    private String vardgivareNamn;

    private UtredningStatus status;

    private String avslutsDatum;
    private boolean ersatts;
    private String fakturerad;
    private String utbetald;

    public static AvslutadBestallningListItem from(Utredning utredning, UtredningStateResolver utredningStateResolver) {
        return AvslutadBestallningListItemBuilder.anAvslutadBestallningListItem()
                .withUtredningsId(utredning.getUtredningId())
                .withUtredningsTyp(utredning.getUtredningsTyp().name())
                .withStatus(utredningStateResolver.resolveStatus(utredning))
                .withVardgivareHsaId(utredning.getExternForfragan().getLandstingHsaId())
                .withVardgivareNamn("Enriched later")
                .withAvslutsDatum(resolveAvslutsDatum(utredning))
                .withErsatts(resolveErsatts(utredning))
                .withFakturerad(nonNull(utredning.getBetalning()) ? utredning.getBetalning().getFakturaId() : null)
                .withUtbetald(nonNull(utredning.getBetalning()) ? utredning.getBetalning().getBetalningsId() : null)
                .build();
    }

    private static boolean resolveErsatts(Utredning utredning) {

        // Utredningen avslutad med orsak ”Jäv” eller ”Ingen beställning"
        if (utredning.getAvbrutenDatum() != null) {
            if (utredning.getAvbrutenAnledning() == EndReason.INGEN_BESTALLNING || utredning.getAvbrutenAnledning() == EndReason.JAV) {
                return false;
            }
        }

        // Slutdatum för utredningen passeras innan utlåtandet är mottaget av Försäkringskassan
        // Slutdatum för komplettering passerat innan kompletteringen är mottagen av Försäkringskassan
        // Nedanstående letar reda på det senaste sista-datumet (gäller både kompletteringar och intyg) och jämför sedan.
        Optional<Intyg> maxSistaDatumOptional = utredning.getIntygList().stream().max(Comparator.comparing(Intyg::getSistaDatum));
        if (maxSistaDatumOptional.isPresent()) {
            Intyg intyg = maxSistaDatumOptional.get();
            if (intyg.getMottagetDatum() == null || intyg.getSistaDatum().compareTo(intyg.getMottagetDatum()) > 0) {
                return false;
            }
        }

        // Det finns Inget besök i utredning som är ersättningsberättigat.
        if (utredning.getBesokList().stream().noneMatch(besok -> besok.getErsatts() != null && besok.getErsatts())) {
            return false;
        }

        // Om inget av ovanstående är sant ersätts utredningen
        return true;
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

    public String getUtredningsId() {
        return utredningsId;
    }

    public void setUtredningsId(String utredningsId) {
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

    public boolean isErsatts() {
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
        private String utredningsId;
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

        public AvslutadBestallningListItemBuilder withUtredningsId(String utredningsId) {
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
