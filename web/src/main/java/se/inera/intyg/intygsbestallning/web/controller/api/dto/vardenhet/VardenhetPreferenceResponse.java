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
package se.inera.intyg.intygsbestallning.web.controller.api.dto.vardenhet;

import se.inera.intyg.intygsbestallning.persistence.model.VardenhetPreference;

/**
 * Created by marced on 2018-04-23.
 */
public class VardenhetPreferenceResponse {

    private String vardenhetHsaId;
    private String utforareTyp;
    private String mottagarNamn;
    private String adress;
    private String postnummer;
    private String postort;
    private String telefonnummer;
    private String epost;
    private String standardsvar;

    public VardenhetPreferenceResponse(VardenhetPreference vardenhetPreference) {
        this.vardenhetHsaId = vardenhetPreference.getVardenhetHsaId();
        this.utforareTyp = vardenhetPreference.getUtforareTyp().name();
        this.mottagarNamn = vardenhetPreference.getMottagarNamn();
        this.adress = vardenhetPreference.getAdress();
        this.postnummer = vardenhetPreference.getPostnummer();
        this.postort = vardenhetPreference.getPostort();
        this.telefonnummer = vardenhetPreference.getTelefonnummer();
        this.epost = vardenhetPreference.getEpost();
        this.standardsvar = vardenhetPreference.getStandardsvar();
    }

    public String getVardenhetHsaId() {
        return vardenhetHsaId;
    }

    public void setVardenhetHsaId(String vardenhetHsaId) {
        this.vardenhetHsaId = vardenhetHsaId;
    }

    public String getUtforareTyp() {
        return utforareTyp;
    }

    public void setUtforareTyp(String utforareTyp) {
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
}
