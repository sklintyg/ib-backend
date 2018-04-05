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
package se.inera.intyg.intygsbestallning.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import se.inera.intyg.infra.integration.hsa.model.SelectableVardenhet;
import se.inera.intyg.infra.security.common.model.IntygUser;
import se.inera.intyg.infra.security.common.model.Role;
import se.inera.intyg.intygsbestallning.auth.model.IbSelectableHsaEntity;
import se.inera.intyg.intygsbestallning.auth.model.IbVardenhet;
import se.inera.intyg.intygsbestallning.auth.model.IbVardgivare;
import se.inera.intyg.intygsbestallning.auth.pdl.PDLActivityEntry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static se.inera.intyg.intygsbestallning.auth.authorities.AuthoritiesConstants.ROLE_FMU_SAMORDNARE;
import static se.inera.intyg.intygsbestallning.auth.authorities.AuthoritiesConstants.ROLE_FMU_VARDADMIN;

/**
 * The IB user overrides a lot of the default behaviour. Since users can be logged in on both Vårdgivare and on Vårdenhet
 * where the VG level doesn't require a Medarbetaruppdrag (IB uses system roles), some data structures are quite
 * unused while others have been added.
 *
 * @author eriklupander
 */
public class IbUser extends IntygUser implements Serializable {

    private static final long serialVersionUID = 8711015219408194075L;

    // Tree for handling which VG and VE the user has access to as VARDADMIN or SAMORDNARE
    private List<IbVardgivare> systemAuthorities = new ArrayList<>();

    // An IB-user must always have a current role and a current IbSelectableHsaEntity
    private Role currentRole;

    private IbSelectableHsaEntity currentlyLoggedInAt;

    // Handles PDL logging state
    private Map<String, List<PDLActivityEntry>> storedActivities;

    /**
     * Typically used by unit tests.
     */
    public IbUser(String hsaId, String namn) {
        super(hsaId);
        this.storedActivities = new HashMap<>();
        this.hsaId = hsaId;
        this.namn = namn;
    }

    /**
     * Copy-constructor that takes a populated {@link IntygUser} and booleans for whether the user has given PDL consent
     * and whether the user has LAKARE privileges.
     *
     * The "isLakare" backs the overridden "isLakare()" method, i.e. the IbUser class doesn't derive LAKARE status
     * from the underlying roles once the isLakare value has been set. This is due to the requirement that LAKARE having
     * systemRoles for being Rehabkoordinator on one or more care units must be able to "switch" between roles when
     * changing units without losing the original "isLakare" information. See INTYG-5068.
     *
     * @param intygUser
     *            User principal, typically constructed in the
     *            {@link org.springframework.security.saml.userdetails.SAMLUserDetailsService}
     *            implementor.
     */
    public IbUser(IntygUser intygUser) {
        super(intygUser.getHsaId());
        this.personId = intygUser.getPersonId();

        this.namn = intygUser.getNamn();
        this.titel = intygUser.getTitel();
//        this.forskrivarkod = intygUser.getForskrivarkod();
        this.authenticationScheme = intygUser.getAuthenticationScheme();
        this.vardgivare = intygUser.getVardgivare();
//        this.befattningar = intygUser.getBefattningar();
//        this.specialiseringar = intygUser.getSpecialiseringar();
//        this.legitimeradeYrkesgrupper = intygUser.getLegitimeradeYrkesgrupper();
        this.systemRoles = intygUser.getSystemRoles();

    //    this.valdVardenhet = intygUser.getValdVardenhet();
    //    this.valdVardgivare = intygUser.getValdVardgivare();
        this.authenticationMethod = intygUser.getAuthenticationMethod();

        this.features = intygUser.getFeatures();
        this.roles = intygUser.getRoles();
        this.authorities = intygUser.getAuthorities();
        this.origin = intygUser.getOrigin();

        this.storedActivities = new HashMap<>();

        this.miuNamnPerEnhetsId = intygUser.getMiuNamnPerEnhetsId();
    }

    public Map<String, List<PDLActivityEntry>> getStoredActivities() {
        return storedActivities;
    }

    @Override
    public int getTotaltAntalVardenheter() {
        // count all hasid's in the datastructure
        return (int) getVardgivare().stream().flatMap(vg -> vg.getHsaIds().stream()).count();
    }


    /**
     * For IB, we select from the systemAuthorities tree rather than the traditional VG -> VE -> E tree.
     *
     * We also sets the currentRole based on the selection.
     *
     * @param vgOrVeHsaId
     * @return
     */
    @Override
    public boolean changeValdVardenhet(String vgOrVeHsaId) {
        // FOR IB, we ignore the original HSA VG->VE tree, all authority is managed through the custom systemAuthorities tree.
        for (IbVardgivare ibVg : systemAuthorities) {
            if (ibVg.getId().equalsIgnoreCase(vgOrVeHsaId)) {
                this.currentlyLoggedInAt = ibVg;
                this.currentRole = this.roles.get(ROLE_FMU_SAMORDNARE);
                return true;
            }
            for (IbVardenhet ibVardenhet : ibVg.getVardenheter()) {
                if (ibVardenhet.getId().equalsIgnoreCase(vgOrVeHsaId)) {
                    this.currentlyLoggedInAt = ibVardenhet;
                    this.currentRole = this.roles.get(ROLE_FMU_VARDADMIN);
                    return true;
                }
            }
        }

        return true;
    }

    @Override
    public String getSelectedMedarbetarUppdragNamn() {
        return null;
    }

    public List<IbVardgivare> getSystemAuthorities() {
        return systemAuthorities;
    }

    public void setSystemAuthorities(List<IbVardgivare> systemAuthorities) {
        this.systemAuthorities = systemAuthorities;
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

    public void setStoredActivities(Map<String, List<PDLActivityEntry>> storedActivities) {
        this.storedActivities = storedActivities;
    }

    // Overridden stuff not used by IB
    @Override
    @JsonIgnore
    public SelectableVardenhet getValdVardgivare() {
        return null;
    }

    @Override
    @JsonIgnore
    public SelectableVardenhet getValdVardenhet() {
        return null;
    }

    @Override
    @JsonIgnore
    public List<String> getSpecialiseringar() {
        return new ArrayList<>();
    }

    @Override
    @JsonIgnore
    public List<String> getBefattningar() {
        return new ArrayList<>();
    }

    @Override
    @JsonIgnore
    public List<String> getLegitimeradeYrkesgrupper() {
        return new ArrayList<>();
    }

    // private scope
    private void writeObject(java.io.ObjectOutputStream stream) throws java.io.IOException {
        stream.defaultWriteObject();
    }

    private void readObject(java.io.ObjectInputStream stream) throws java.io.IOException, ClassNotFoundException {
        stream.defaultReadObject();
    }
}
