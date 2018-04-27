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

import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.service.stateresolver.UtredningFas;
import se.inera.intyg.intygsbestallning.service.stateresolver.UtredningStatus;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class UtredningListItem implements FreeTextSearchable, FilterableListItem {

    private String utredningsId;
    private String utredningsTyp;
    private String vardenhetHsaId;
    private String vardenhetNamn;
    private UtredningFas fas;
    private String slutdatumFas;
    private UtredningStatus status;

    public static UtredningListItem from(Utredning utredning, UtredningStatus utredningStatus) {

        return UtredningListItemBuilder.anUtredningListItem()
                .withFas(utredningStatus.getUtredningFas())
                .withSlutdatumFas(resolveSlutDatum(utredning, utredningStatus))
                .withStatus(utredningStatus)
                .withUtredningsId(utredning.getUtredningId())
                .withUtredningsTyp(utredning.getUtredningsTyp().name())
                .withVardenhetHsaId(resolveTilldeladVardenhetHsaId(utredning))
                .withVardenhetNamn("")
                .build();
    }

    private static String resolveTilldeladVardenhetHsaId(Utredning utredning) {
        if (utredning.getExternForfragan() != null) {
            Optional<String> optionalVardenhetHsaId = utredning.getExternForfragan().getInternForfraganList().stream()
                    .filter(intf -> intf.getTilldeladDatum() != null)
                    .map(intf -> intf.getVardenhetHsaId())
                    .findFirst();

            return optionalVardenhetHsaId.orElse(null);
        }
        return null;
    }

    /**
     * Om utredningsfas = Förfrågan är slutdatum = Utredning.förfrågan.svarsdatum.
     * Om utredningsfas = Utredning är slutdatum = Utredning.intyg.sista datum för mottagning
     * Om utredningsfas = Komplettering är slutdatum = Utredning.kompletteringsbegäran.komplettering.sista datum för
     * mottagning.
     * Om utredningsfas = Redovisa tolk så är fältet tomt.
     */
    private static String resolveSlutDatum(Utredning utredning, UtredningStatus utredningStatus) {
        switch (utredningStatus.getUtredningFas()) {
        case FORFRAGAN:
            return utredning.getExternForfragan().getBesvarasSenastDatum().format(DateTimeFormatter.ISO_DATE);
        case UTREDNING:
            return utredning.getBestallning().get().getIntygKlartSenast().format(DateTimeFormatter.ISO_DATE);
        case KOMPLETTERING:
            return "TODO";
        case REDOVISA_TOLK:
        case AVSLUTAD:
            return null;
        }
        return null;
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

    public String getVardenhetHsaId() {
        return vardenhetHsaId;
    }

    public void setVardenhetHsaId(String vardenhetHsaId) {
        this.vardenhetHsaId = vardenhetHsaId;
    }

    public String getVardenhetNamn() {
        return vardenhetNamn;
    }

    public void setVardenhetNamn(String vardenhetNamn) {
        this.vardenhetNamn = vardenhetNamn;
    }

    public UtredningFas getFas() {
        return fas;
    }

    public void setFas(UtredningFas fas) {
        this.fas = fas;
    }

    @Override
    public String getSlutdatumFas() {
        return slutdatumFas;
    }

    public void setSlutdatumFas(String slutdatumFas) {
        this.slutdatumFas = slutdatumFas;
    }

    @Override
    public UtredningStatus getStatus() {
        return status;
    }

    public void setStatus(UtredningStatus status) {
        this.status = status;
    }

    @Override
    public String toSearchString() {
        return utredningsId + " "
                + utredningsTyp + " "
                + vardenhetNamn + " "
                + fas.getLabel() + " "
                + slutdatumFas + " "
                + status.getLabel() + " ";
    }

    public static final class UtredningListItemBuilder {
        private String utredningsId;
        private String utredningsTyp;
        private String vardenhetHsaId;
        private String vardenhetNamn;
        private UtredningFas fas;
        private String slutdatumFas;
        private UtredningStatus status;

        private UtredningListItemBuilder() {
        }

        public static UtredningListItemBuilder anUtredningListItem() {
            return new UtredningListItemBuilder();
        }

        public UtredningListItemBuilder withUtredningsId(String utredningsId) {
            this.utredningsId = utredningsId;
            return this;
        }

        public UtredningListItemBuilder withUtredningsTyp(String utredningsTyp) {
            this.utredningsTyp = utredningsTyp;
            return this;
        }

        public UtredningListItemBuilder withVardenhetHsaId(String vardenhetHsaId) {
            this.vardenhetHsaId = vardenhetHsaId;
            return this;
        }

        public UtredningListItemBuilder withVardenhetNamn(String vardenhetNamn) {
            this.vardenhetNamn = vardenhetNamn;
            return this;
        }

        public UtredningListItemBuilder withFas(UtredningFas fas) {
            this.fas = fas;
            return this;
        }

        public UtredningListItemBuilder withSlutdatumFas(String slutdatumFas) {
            this.slutdatumFas = slutdatumFas;
            return this;
        }

        public UtredningListItemBuilder withStatus(UtredningStatus status) {
            this.status = status;
            return this;
        }

        public UtredningListItem build() {
            UtredningListItem utredningListItem = new UtredningListItem();
            utredningListItem.setUtredningsId(utredningsId);
            utredningListItem.setUtredningsTyp(utredningsTyp);
            utredningListItem.setVardenhetHsaId(vardenhetHsaId);
            utredningListItem.setVardenhetNamn(vardenhetNamn);
            utredningListItem.setFas(fas);
            utredningListItem.setSlutdatumFas(slutdatumFas);
            utredningListItem.setStatus(status);
            return utredningListItem;
        }
    }
}
