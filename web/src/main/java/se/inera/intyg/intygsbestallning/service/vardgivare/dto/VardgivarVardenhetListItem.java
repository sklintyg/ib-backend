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
package se.inera.intyg.intygsbestallning.service.vardgivare.dto;

import se.inera.intyg.infra.integration.hsa.model.Vardenhet;
import se.inera.intyg.intygsbestallning.persistence.model.RegistreradVardenhet;
import se.inera.intyg.intygsbestallning.persistence.model.type.RegiFormTyp;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.FreeTextSearchable;

/**
 * Created by marced on 2018-05-11.
 */
public class VardgivarVardenhetListItem implements FreeTextSearchable {
    private String vardgivarHsaId;
    private String vardenhetVardgivarHsaId;
    private String vardenhetHsaId;
    private String namn;
    private String postadress;
    private String postnummer;
    private String postort;
    private String telefon;
    private String epost;
    private RegiFormTyp regiForm;
    private String regiFormLabel;

    public static VardgivarVardenhetListItem from(RegistreradVardenhet rv, Vardenhet hsaVardenhet) {
        return VardenhetListItemBuilder.anVardenhetListItem()
                .withVardgivarHsaId(rv.getVardgivareHsaId())
                .withVardenhetHsaId(rv.getVardenhetHsaId())
                .withVardenhetVardgivarHsaId(rv.getVardenhetVardgivareHsaId())
                .withNamn(hsaVardenhet.getNamn())
                .withPostadress(hsaVardenhet.getPostadress())
                .withPostnummer(hsaVardenhet.getPostnummer())
                .withPostort(hsaVardenhet.getPostort())
                .withTelefon(hsaVardenhet.getTelefonnummer())
                .withEpost(hsaVardenhet.getEpost())
                .withRegiForm(rv.getVardenhetRegiForm())
                .build();
    }
    public static VardgivarVardenhetListItem from(Vardenhet hsaVardenhet) {
        return VardenhetListItemBuilder.anVardenhetListItem()
                .withVardgivarHsaId(null)
                .withVardenhetHsaId(hsaVardenhet.getId())
                .withVardenhetVardgivarHsaId(hsaVardenhet.getVardgivareHsaId())
                .withNamn(hsaVardenhet.getNamn())
                .withPostadress(hsaVardenhet.getPostadress())
                .withPostnummer(hsaVardenhet.getPostnummer())
                .withPostort(hsaVardenhet.getPostort())
                .withTelefon(hsaVardenhet.getTelefonnummer())
                .withEpost(hsaVardenhet.getEpost())
                .withRegiForm(null)
                .build();
    }

    public String getVardgivarHsaId() {
        return vardgivarHsaId;
    }

    public void setVardgivarHsaId(String vardgivarHsaId) {
        this.vardgivarHsaId = vardgivarHsaId;
    }

    public String getVardenhetVardgivarHsaId() {
        return vardenhetVardgivarHsaId;
    }

    public void setVardenhetVardgivarHsaId(String vardenhetVardgivarHsaId) {
        this.vardenhetVardgivarHsaId = vardenhetVardgivarHsaId;
    }

    public String getVardenhetHsaId() {
        return vardenhetHsaId;
    }

    public void setVardenhetHsaId(String vardenhetHsaId) {
        this.vardenhetHsaId = vardenhetHsaId;
    }

    public String getNamn() {
        return namn;
    }

    public void setNamn(String namn) {
        this.namn = namn;
    }

    public String getPostadress() {
        return postadress;
    }

    public void setPostadress(String postadress) {
        this.postadress = postadress;
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

    public RegiFormTyp getRegiForm() {
        return regiForm;
    }

    public void setRegiForm(RegiFormTyp regiForm) {
        this.regiForm = regiForm;
    }

    public String getRegiFormLabel() {
        return regiForm != null ? regiForm.getLabel() : "";
    }

    @Override
    public String toSearchString() {
        return String.join(" ", namn, postadress, postnummer, postort, telefon, epost, getRegiFormLabel());
    }

    public static final class VardenhetListItemBuilder {
        private String vardgivarHsaId;
        private String vardenhetVardgivarHsaId;
        private String vardenhetHsaId;
        private String namn;
        private String postadress;
        private String postnummer;
        private String postort;
        private String telefon;
        private String epost;
        private RegiFormTyp regiForm;

        private VardenhetListItemBuilder() {
        }

        public static VardgivarVardenhetListItem.VardenhetListItemBuilder anVardenhetListItem() {
            return new VardgivarVardenhetListItem.VardenhetListItemBuilder();
        }

        public VardenhetListItemBuilder withVardgivarHsaId(String vardgivarHsaId) {
            this.vardgivarHsaId = vardgivarHsaId;
            return this;
        }

        public VardenhetListItemBuilder withVardenhetVardgivarHsaId(String vardenhetVardgivarHsaId) {
            this.vardenhetVardgivarHsaId = vardenhetVardgivarHsaId;
            return this;
        }

        public VardenhetListItemBuilder withVardenhetHsaId(String vardenhetHsaId) {
            this.vardenhetHsaId = vardenhetHsaId;
            return this;
        }

        public VardenhetListItemBuilder withNamn(String namn) {
            this.namn = namn;
            return this;
        }

        public VardenhetListItemBuilder withPostadress(String postadress) {
            this.postadress = postadress;
            return this;
        }

        public VardenhetListItemBuilder withPostnummer(String postnummer) {
            this.postnummer = postnummer;
            return this;
        }

        public VardenhetListItemBuilder withPostort(String postort) {
            this.postort = postort;
            return this;
        }

        public VardenhetListItemBuilder withTelefon(String telefon) {
            this.telefon = telefon;
            return this;
        }

        public VardenhetListItemBuilder withEpost(String epost) {
            this.epost = epost;
            return this;
        }

        public VardenhetListItemBuilder withRegiForm(RegiFormTyp regiForm) {
            this.regiForm = regiForm;
            return this;
        }

        public VardgivarVardenhetListItem build() {
            VardgivarVardenhetListItem veli = new VardgivarVardenhetListItem();
            veli.setVardgivarHsaId(vardgivarHsaId);
            veli.setVardenhetVardgivarHsaId(vardenhetVardgivarHsaId);
            veli.setVardenhetHsaId(vardenhetHsaId);
            veli.setNamn(namn);
            veli.setPostadress(postadress);
            veli.setPostnummer(postnummer);
            veli.setPostort(postort);
            veli.setTelefon(telefon);
            veli.setEpost(epost);
            veli.setRegiForm(regiForm);
            return veli;
        }
    }
}
