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
package se.inera.intyg.intygsbestallning.web.controller.api.dto.user;

import se.inera.intyg.infra.security.common.model.Feature;
import se.inera.intyg.infra.security.common.model.Role;
import se.inera.intyg.intygsbestallning.auth.IbUser;
import se.inera.intyg.intygsbestallning.auth.model.IbSelectableHsaEntity;
import se.inera.intyg.intygsbestallning.auth.model.IbVardgivare;

import java.util.List;
import java.util.Map;

/**
 * Reponse dto for the getUser api.
 */
public class GetUserResponse {

    private String hsaId;
    private String namn;
    private String titel;
    private String authenticationScheme;

    private Map<String, Role> roles;
    private Map<String, Feature> features;

    private List<IbVardgivare> authoritiesTree;
    private Role currentRole;
    private IbSelectableHsaEntity currentlyLoggedInAt;

    public GetUserResponse(IbUser user) {
        this.hsaId = user.getHsaId();
        this.namn = user.getNamn();
        this.roles = user.getRoles();
        this.titel = user.getTitel();

        this.authenticationScheme = user.getAuthenticationScheme();
        this.features = user.getFeatures();

        this.authoritiesTree = user.getSystemAuthorities();
        this.currentRole = user.getCurrentRole();
        this.currentlyLoggedInAt = user.getCurrentlyLoggedInAt();
    }

    public String getHsaId() {
        return hsaId;
    }

    public void setHsaId(String hsaId) {
        this.hsaId = hsaId;
    }

    public String getNamn() {
        return namn;
    }

    public void setNamn(String namn) {
        this.namn = namn;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public String getAuthenticationScheme() {
        return authenticationScheme;
    }

    public void setAuthenticationScheme(String authenticationScheme) {
        this.authenticationScheme = authenticationScheme;
    }


    public Map<String, Role> getRoles() {
        return roles;
    }

    public void setRoles(Map<String, Role> roles) {
        this.roles = roles;
    }




    public Map<String, Feature> getFeatures() {
        return features;
    }

    public void setFeatures(Map<String, Feature> features) {
        this.features = features;
    }

    public List<IbVardgivare> getAuthoritiesTree() {
        return authoritiesTree;
    }

    public void setAuthoritiesTree(List<IbVardgivare> authoritiesTree) {
        this.authoritiesTree = authoritiesTree;
    }

    public Role getCurrentRole() {
        return currentRole;
    }

    public void setCurrentRole(Role currentRole) {
        this.currentRole = currentRole;
    }

    public IbSelectableHsaEntity getCurrentlyLoggedInAt() {
        return currentlyLoggedInAt;
    }

    public void setCurrentlyLoggedInAt(IbSelectableHsaEntity currentlyLoggedInAt) {
        this.currentlyLoggedInAt = currentlyLoggedInAt;
    }
}
