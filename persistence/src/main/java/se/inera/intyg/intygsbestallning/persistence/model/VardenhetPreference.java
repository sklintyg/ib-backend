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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

/**
 * Created by marced on 2018-04-23.
 */

@Entity
@Table(name = "VARDENHET_PREFERENCE")
public class  VardenhetPreference {

    @Id
    @Column(name = "VARDENHET_HSA_ID", nullable = false)
    private String vardenhetHsaId;

    @Column(name = "MOTTAGAR_NAMN")
    private String mottagarNamn;

    @Column(name = "ADRESS")
    private String adress;

    @Column(name = "POSTNUMMER")
    private String postnummer;

    @Column(name = "POSTORT")
    private String postort;

    @Column(name = "TELEFONNUMMER")
    private String telefonnummer;

    @Column(name = "EPOST")
    private String epost;

    @Column(name = "STANDARDSVAR")
    private String standardsvar;

    public String getVardenhetHsaId() {
        return vardenhetHsaId;
    }

    public void setVardenhetHsaId(String vardenhetHsaId) {
        this.vardenhetHsaId = vardenhetHsaId;
    }

    public String getMottagarNamn() {
        return mottagarNamn;
    }

    public void setMottagarNamn(String mottagarNamn) {
        this.mottagarNamn = mottagarNamn;
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

    public String getTelefonnummer() {
        return telefonnummer;
    }

    public void setTelefonnummer(String telefonnummer) {
        this.telefonnummer = telefonnummer;
    }

    public String getEpost() {
        return epost;
    }

    public void setEpost(String epost) {
        this.epost = epost;
    }

    public String getStandardsvar() {
        return standardsvar;
    }

    public void setStandardsvar(String standardsvar) {
        this.standardsvar = standardsvar;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        VardenhetPreference that = (VardenhetPreference) o;
        return Objects.equals(vardenhetHsaId, that.vardenhetHsaId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vardenhetHsaId);
    }
}
