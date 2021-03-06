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

import com.google.common.base.MoreObjects;
import se.inera.intyg.intygsbestallning.persistence.model.type.UtforareTyp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "VARDENHET_PREFERENCE")
public final class VardenhetPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "VARDENHET_HSA_ID", nullable = false)
    private String vardenhetHsaId;

    @Enumerated(EnumType.STRING)
    @Column(name = "UTFORARE_TYP", nullable = false)
    private UtforareTyp utforareTyp;

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

    public UtforareTyp getUtforareTyp() {
        return utforareTyp;
    }

    public void setUtforareTyp(UtforareTyp utforareTyp) {
        this.utforareTyp = utforareTyp;
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
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VardenhetPreference)) {
            return false;
        }
        final VardenhetPreference that = (VardenhetPreference) o;
        return Objects.equals(id, that.id)
                && Objects.equals(vardenhetHsaId, that.vardenhetHsaId)
                && Objects.equals(utforareTyp, that.utforareTyp)
                && Objects.equals(mottagarNamn, that.mottagarNamn)
                && Objects.equals(adress, that.adress)
                && Objects.equals(postnummer, that.postnummer)
                && Objects.equals(postort, that.postort)
                && Objects.equals(telefonnummer, that.telefonnummer)
                && Objects.equals(epost, that.epost)
                && Objects.equals(standardsvar, that.standardsvar);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, vardenhetHsaId, utforareTyp, mottagarNamn, adress, postnummer, postort, telefonnummer, epost, standardsvar);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("vardenhetHsaId", vardenhetHsaId)
                .add("utforareTyp", utforareTyp)
                .add("mottagarNamn", mottagarNamn)
                .add("adress", adress)
                .add("postnummer", postnummer)
                .add("postort", postort)
                .add("telefonnummer", telefonnummer)
                .add("epost", epost)
                .add("standardsvar", standardsvar)
                .toString();
    }
}
