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

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.google.common.base.MoreObjects;

import se.inera.intyg.intygsbestallning.persistence.model.type.RegiFormTyp;

@Entity
@Table(name = "REGISTRERAD_VARDENHET")
public final class RegistreradVardenhet {

    @Id
    @GeneratedValue
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "VARDGIVARE_HSA_ID", nullable = false)
    private String vardgivareHsaId;

    @Column(name = "VARDENHET_VARDGIVARE_HSA_ID", nullable = false)
    private String vardenhetVardgivareHsaId;

    @Column(name = "VARDENHET_HSA_ID", nullable = false)
    private String vardenhetHsaId;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "VARDENHET_REGIFORM", nullable = false)
    private RegiFormTyp vardenhetRegiForm;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVardgivareHsaId() {
        return vardgivareHsaId;
    }

    public void setVardgivareHsaId(String vardgivareHsaId) {
        this.vardgivareHsaId = vardgivareHsaId;
    }

    public String getVardenhetVardgivareHsaId() {
        return vardenhetVardgivareHsaId;
    }

    public void setVardenhetVardgivareHsaId(String vardenhetVardgivareHsaId) {
        this.vardenhetVardgivareHsaId = vardenhetVardgivareHsaId;
    }

    public String getVardenhetHsaId() {
        return vardenhetHsaId;
    }

    public void setVardenhetHsaId(String vardenhetHsaId) {
        this.vardenhetHsaId = vardenhetHsaId;
    }

    public RegiFormTyp getVardenhetRegiForm() {
        return vardenhetRegiForm;
    }

    public void setVardenhetRegiFormTyp(RegiFormTyp vardenhetRegiForm) {
        this.vardenhetRegiForm = vardenhetRegiForm;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RegistreradVardenhet)) {
            return false;
        }
        final RegistreradVardenhet that = (RegistreradVardenhet) o;
        return Objects.equals(id, that.id)
                && Objects.equals(vardgivareHsaId, that.vardgivareHsaId)
                && Objects.equals(vardenhetVardgivareHsaId, that.vardenhetVardgivareHsaId)
                && Objects.equals(vardenhetHsaId, that.vardenhetHsaId)
                && vardenhetRegiForm == that.vardenhetRegiForm;
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, vardgivareHsaId, vardenhetVardgivareHsaId, vardenhetHsaId, vardenhetRegiForm);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("vardgivareHsaId", vardgivareHsaId)
                .add("vardenhetVardgivareHsaId", vardenhetVardgivareHsaId)
                .add("vardenhetHsaId", vardenhetHsaId)
                .add("vardenhetRegiForm", vardenhetRegiForm)
                .toString();
    }

    public static final class RegistreradVardenhetBuilder {
        private String vardgivareHsaId;
        private String vardenhetVardgivareHsaId;
        private String vardenhetHsaId;
        private String vardenhetNamn;
        private RegiFormTyp vardenhetRegiForm;

        private RegistreradVardenhetBuilder() {
        }

        public static RegistreradVardenhetBuilder aRegistreradVardenhet() {
            return new RegistreradVardenhetBuilder();
        }

        public RegistreradVardenhetBuilder withVardgivareHsaId(String vardgivareHsaId) {
            this.vardgivareHsaId = vardgivareHsaId;
            return this;
        }

        public RegistreradVardenhetBuilder withVardenhetVardgivareHsaId(String vardenhetVardgivareHsaId) {
            this.vardenhetVardgivareHsaId = vardenhetVardgivareHsaId;
            return this;
        }

        public RegistreradVardenhetBuilder withVardenhetHsaId(String vardenhetHsaId) {
            this.vardenhetHsaId = vardenhetHsaId;
            return this;
        }

        public RegistreradVardenhetBuilder withVardenhetNamn(String vardenhetNamn) {
            this.vardenhetNamn = vardenhetNamn;
            return this;
        }

        public RegistreradVardenhetBuilder withVardenhetRegiForm(RegiFormTyp vardenhetRegiForm) {
            this.vardenhetRegiForm = vardenhetRegiForm;
            return this;
        }

        public RegistreradVardenhet build() {
            RegistreradVardenhet registreradVardenhet = new RegistreradVardenhet();
            registreradVardenhet.setVardgivareHsaId(vardgivareHsaId);
            registreradVardenhet.setVardenhetVardgivareHsaId(vardenhetVardgivareHsaId);
            registreradVardenhet.setVardenhetHsaId(vardenhetHsaId);
            registreradVardenhet.setVardenhetRegiFormTyp(vardenhetRegiForm);
            return registreradVardenhet;
        }
    }
}
