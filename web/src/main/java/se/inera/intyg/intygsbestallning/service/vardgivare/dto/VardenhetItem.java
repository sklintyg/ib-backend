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

import se.inera.intyg.intygsbestallning.persistence.model.RegiFormTyp;
import se.inera.intyg.intygsbestallning.persistence.model.RegistreradVardenhet;

public class VardenhetItem {
    private String id;
    private String label;
    private RegiFormTyp regiForm;

    public static VardenhetItem from(RegistreradVardenhet hsaVardenhet) {
        return VardenhetItemBuilder.aVardenhetItem()
                .withId(hsaVardenhet.getVardenhetHsaId())
                .withLabel(hsaVardenhet.getVardenhetNamn())
                .withRegiForm(hsaVardenhet.getVardenhetRegiForm())
                .build();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public RegiFormTyp getRegiForm() {
        return regiForm;
    }

    public void setRegiForm(RegiFormTyp regiForm) {
        this.regiForm = regiForm;
    }

    public static final class VardenhetItemBuilder {
        private String id;
        private String label;
        private RegiFormTyp regiForm;

        private VardenhetItemBuilder() {
        }

        public static VardenhetItemBuilder aVardenhetItem() {
            return new VardenhetItemBuilder();
        }

        public VardenhetItemBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public VardenhetItemBuilder withLabel(String label) {
            this.label = label;
            return this;
        }

        public VardenhetItemBuilder withRegiForm(RegiFormTyp regiForm) {
            this.regiForm = regiForm;
            return this;
        }

        public VardenhetItem build() {
            VardenhetItem vardenhetItem = new VardenhetItem();
            vardenhetItem.setId(id);
            vardenhetItem.setLabel(label);
            vardenhetItem.setRegiForm(regiForm);
            return vardenhetItem;
        }
    }
}
