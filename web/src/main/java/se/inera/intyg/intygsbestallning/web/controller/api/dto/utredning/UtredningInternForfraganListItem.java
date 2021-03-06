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
package se.inera.intyg.intygsbestallning.web.controller.api.dto.utredning;

import static java.util.Objects.nonNull;

import java.time.format.DateTimeFormatter;
import se.inera.intyg.intygsbestallning.persistence.model.ForfraganSvar;
import se.inera.intyg.intygsbestallning.persistence.model.InternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.status.InternForfraganFas;
import se.inera.intyg.intygsbestallning.persistence.model.status.InternForfraganStatus;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.VardenhetEnrichable;

public class UtredningInternForfraganListItem implements VardenhetEnrichable {

    private static DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;

    private String vardenhetHsaId;
    private String vardenhetNamn;
    private InternForfraganStatus status;
    private InternForfraganFas fas;
    private String borjaDatum;
    private String meddelande;
    private String mottagare;
    private String adress;
    private String postnummer;
    private String postort;
    private String telefon;
    private String epost;

    public static UtredningInternForfraganListItem from(InternForfragan internForfragan) {
        final ForfraganSvar forfraganSvar = internForfragan.getForfraganSvar();
        return UtredningInternForfraganListItemBuilder.aUtredningInternForfraganListItem()
                .withVardenhetHsaId(internForfragan.getVardenhetHsaId())
                .withStatusAndFas(internForfragan.getStatus())
                .withBorjaDatum(nonNull(forfraganSvar) && nonNull(forfraganSvar.getBorjaDatum())
                        ? forfraganSvar.getBorjaDatum().format(formatter) : null)
                .withMeddelande(nonNull(forfraganSvar) ? forfraganSvar.getKommentar() : null)
                .withMottagare(nonNull(forfraganSvar) ? forfraganSvar.getUtforareNamn() : null)
                .withAdress(nonNull(forfraganSvar) ? forfraganSvar.getUtforareAdress() : null)
                .withPostnummer(nonNull(forfraganSvar) ? forfraganSvar.getUtforarePostnr() : null)
                .withPostort(nonNull(forfraganSvar) ? forfraganSvar.getUtforarePostort() : null)
                .withTelefon(nonNull(forfraganSvar) ? forfraganSvar.getUtforareTelefon() : null)
                .withEpost(nonNull(forfraganSvar) ? forfraganSvar.getUtforareEpost() : null)
                .build();
    }

    @Override
    public String getVardenhetHsaId() {
        return vardenhetHsaId;
    }

    public void setVardenhetHsaId(String vardenhetHsaId) {
        this.vardenhetHsaId = vardenhetHsaId;
    }

    public String getVardenhetNamn() {
        return vardenhetNamn;
    }

    @Override
    public void setVardenhetNamn(String vardenhetNamn) {
        this.vardenhetNamn = vardenhetNamn;
    }

    @Override
    public void setVardenhetFelmeddelande(String vardenhetFelmeddelande) {
    }

    public InternForfraganStatus getStatus() {
        return status;
    }

    public void setStatus(InternForfraganStatus status) {
        this.status = status;
    }

    public InternForfraganFas getFas() {
        return fas;
    }

    public void setFas(InternForfraganFas fas) {
        this.fas = fas;
    }

    public String getBorjaDatum() {
        return borjaDatum;
    }

    public void setBorjaDatum(String borjaDatum) {
        this.borjaDatum = borjaDatum;
    }

    public String getMeddelande() {
        return meddelande;
    }

    public void setMeddelande(String meddelande) {
        this.meddelande = meddelande;
    }

    public String getMottagare() {
        return mottagare;
    }

    public void setMottagare(String mottagare) {
        this.mottagare = mottagare;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getPostnummer() {
        return postnummer;
    }

    public void setPostnummer(String postnummer) {
        this.postnummer = postnummer;
    }

    public String getPostort() {
        return postort;
    }

    public void setPostort(String postort) {
        this.postort = postort;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public String getEpost() {
        return epost;
    }

    public void setEpost(String epost) {
        this.epost = epost;
    }

    public static final class UtredningInternForfraganListItemBuilder {
        private String vardenhetHsaId;
        private InternForfraganStatus status;
        private InternForfraganFas fas;
        private String borjaDatum;
        private String meddelande;
        private String mottagare;
        private String adress;
        private String postnummer;
        private String postort;
        private String telefon;
        private String epost;

        private UtredningInternForfraganListItemBuilder() {
        }

        public static UtredningInternForfraganListItem.UtredningInternForfraganListItemBuilder aUtredningInternForfraganListItem() {
            return new UtredningInternForfraganListItem.UtredningInternForfraganListItemBuilder();
        }

        public UtredningInternForfraganListItem.UtredningInternForfraganListItemBuilder withVardenhetHsaId(String vardenhetHsaId) {
            this.vardenhetHsaId = vardenhetHsaId;
            return this;
        }

        public UtredningInternForfraganListItem.UtredningInternForfraganListItemBuilder withStatusAndFas(InternForfraganStatus status) {
            this.status = status;
            this.fas = status.getInternForfraganFas();
            return this;
        }

        public UtredningInternForfraganListItem.UtredningInternForfraganListItemBuilder withBorjaDatum(String borjaDatum) {
            this.borjaDatum = borjaDatum;
            return this;
        }

        public UtredningInternForfraganListItem.UtredningInternForfraganListItemBuilder withMeddelande(String meddelande) {
            this.meddelande = meddelande;
            return this;
        }

        public UtredningInternForfraganListItem.UtredningInternForfraganListItemBuilder withMottagare(String mottagare) {
            this.mottagare = mottagare;
            return this;
        }

        public UtredningInternForfraganListItem.UtredningInternForfraganListItemBuilder withAdress(String adress) {
            this.adress = adress;
            return this;
        }

        public UtredningInternForfraganListItem.UtredningInternForfraganListItemBuilder withPostnummer(String postnummer) {
            this.postnummer = postnummer;
            return this;
        }

        public UtredningInternForfraganListItem.UtredningInternForfraganListItemBuilder withPostort(String postort) {
            this.postort = postort;
            return this;
        }

        public UtredningInternForfraganListItem.UtredningInternForfraganListItemBuilder withTelefon(String telefon) {
            this.telefon = telefon;
            return this;
        }

        public UtredningInternForfraganListItem.UtredningInternForfraganListItemBuilder withEpost(String epost) {
            this.epost = epost;
            return this;
        }

        public UtredningInternForfraganListItem build() {
            UtredningInternForfraganListItem utredningInternForfraganListItem = new UtredningInternForfraganListItem();
            utredningInternForfraganListItem.setVardenhetHsaId(vardenhetHsaId);
            utredningInternForfraganListItem.setAdress(adress);
            utredningInternForfraganListItem.setBorjaDatum(borjaDatum);
            utredningInternForfraganListItem.setEpost(epost);
            utredningInternForfraganListItem.setMeddelande(meddelande);
            utredningInternForfraganListItem.setMottagare(mottagare);
            utredningInternForfraganListItem.setPostnummer(postnummer);
            utredningInternForfraganListItem.setPostort(postort);
            utredningInternForfraganListItem.setStatus(status);
            utredningInternForfraganListItem.setFas(fas);
            utredningInternForfraganListItem.setTelefon(telefon);
            return utredningInternForfraganListItem;
        }
    }
}
