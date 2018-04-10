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

import org.opensaml.saml2.core.Assertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.integration.hsa.model.UserCredentials;
import se.inera.intyg.infra.integration.hsa.model.Vardenhet;
import se.inera.intyg.infra.integration.hsa.model.Vardgivare;
import se.inera.intyg.infra.security.common.model.IntygUser;
import se.inera.intyg.infra.security.siths.BaseSakerhetstjanstAssertion;
import se.inera.intyg.infra.security.siths.BaseUserDetailsService;
import se.inera.intyg.intygsbestallning.auth.authorities.AuthoritiesConstants;
import se.inera.intyg.intygsbestallning.auth.exceptions.MissingIBSystemRoleException;
import se.inera.intyg.intygsbestallning.auth.model.IbVardenhet;
import se.inera.intyg.intygsbestallning.auth.model.IbVardgivare;
import se.inera.intyg.intygsbestallning.auth.util.SystemRolesParser;
import se.inera.intyg.intygsbestallning.persistence.repository.AnvandarPreferenceRepository;
import se.riv.infrastructure.directory.v1.PersonInformationType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author andreaskaltenbach
 */
@Service
public class IbUserDetailsService extends BaseUserDetailsService implements SAMLUserDetailsService {

    private static final Logger LOG = LoggerFactory.getLogger(IbUserDetailsService.class);

    @Autowired
    private AnvandarPreferenceRepository anvandarPreferenceRepository;


    // =====================================================================================
    // ~ Protected scope
    // =====================================================================================

    @Override
    protected IbUser buildUserPrincipal(SAMLCredential credential) {
        // All rehab customization is done in the overridden decorateXXX methods, so just return a new rehabuser
        IntygUser intygUser = super.buildUserPrincipal(credential);
        IbUser ibUser = new IbUser(intygUser);

        ibUser.setPossibleRoles(commonAuthoritiesResolver.getRoles());
        buildSystemAuthoritiesTree(ibUser);

        if (ibUser.getSystemAuthorities().size() == 0) {
            throw new MissingIBSystemRoleException(ibUser.getHsaId());
        }

        // If only a single possible entity to select as loggedInAt, do that...
        int count = 0;
        for (IbVardgivare vg : ibUser.getSystemAuthorities()) {
            if (vg.isSamordnare()) {
                count++;
            }
            count += vg.getVardenheter().size();
        }

        // Ugly, make something more readable...
        if (count == 1) {
            String oneAndOnly = ibUser.getSystemAuthorities().get(0).isSamordnare() ? ibUser.getSystemAuthorities().get(0).getId()
                    : ibUser.getSystemAuthorities().get(0).getVardenheter().get(0).getId();
            ibUser.changeValdVardenhet(oneAndOnly);
        }

        return ibUser;
    }

    /**
     * Overridden for IB. We cannot use "fallback" roles here so we must postpone population of this.roles.
     *
     * @param intygUser
     * @param personInfo
     * @param userCredentials
     */
    @Override
    protected void decorateIntygUserWithRoleAndAuthorities(IntygUser intygUser, List<PersonInformationType> personInfo,
                                                           UserCredentials userCredentials) {
       // Do nothing
    }

    @Override
    protected void decorateIntygUserWithDefaultVardenhet(IntygUser intygUser) {
        // Only set a default enhet if there is only one (mottagningar doesnt count).
        // If no default vardenhet can be determined - let it be null and force user to select one.
        if (getTotaltAntalVardenheterExcludingMottagningar(intygUser) == 1) {
            super.decorateIntygUserWithDefaultVardenhet(intygUser);
        }
    }

    /**
     * Builds a tree of caregivers and careunits where the user has either SAMORDNARE or VARDADM roles
     * and assigns it to the user principal. Is based on the vardgivare and systemRoles so those must
     * have been populated prior to calling this method.
     *
     * @param user
     */
    public void buildSystemAuthoritiesTree(IbUser user) {

        // Takes standard vardgivare + systemRoles.
        List<String> fmuVardadminCareUnitIds = SystemRolesParser.parseEnhetsIdsFromSystemRoles(user.getSystemRoles());
        List<String> samordnareCareGiverIds = SystemRolesParser.parseCaregiverIdsFromSystemRoles(user.getSystemRoles());

        List<Vardgivare> vardgivareWithMedarbetaruppdrag = user.getVardgivare();

        List<IbVardgivare> authSystemTree = new ArrayList<>();

        // First, the easy part. Add any VG where the VE has systemRole and the VG does NOT have systemRole.
        for (Vardgivare vg : vardgivareWithMedarbetaruppdrag) {

            // If the VG is also a Samordnare... add!
            if (samordnareCareGiverIds.contains(vg.getId())) {
                IbVardgivare ibVardgivare = new IbVardgivare(vg.getId(), vg.getNamn(), true);
                if (!authSystemTree.contains(ibVardgivare)) {
                    authSystemTree.add(ibVardgivare);

                } else {
                    // If it exists, we must update it to samordnare if not already so.
                    for (IbVardgivare ibVg : authSystemTree) {
                        if (ibVg.getId().equalsIgnoreCase(vg.getId()) && !ibVg.isSamordnare()) {
                            ibVg.setSamordnare(true);
                        }
                    }
                }
            }
            // Done handling adding or updating root IbVG

            for (Vardenhet ve : vg.getVardenheter()) {
                if (fmuVardadminCareUnitIds.contains(ve.getId())) {

                    // Make sure the VG is added as non-samordnare VG if not present
                    IbVardgivare ibVardgivareForVardadmin = new IbVardgivare(vg.getId(), vg.getNamn(), false);
                    if (!authSystemTree.contains(ibVardgivareForVardadmin)) {
                        // Add the VE...

                        ibVardgivareForVardadmin.getVardenheter().add(new IbVardenhet(ve.getId(), ve.getNamn(), ibVardgivareForVardadmin));

                        authSystemTree.add(ibVardgivareForVardadmin);
                    } else {
                        // Find the existing entry.
                        for (IbVardgivare existingIbVardgivare : authSystemTree) {
                            if (existingIbVardgivare.getId().equalsIgnoreCase(vg.getId())) {
                                existingIbVardgivare.getVardenheter().add(new IbVardenhet(ve.getId(), ve.getNamn(), existingIbVardgivare));
                            }
                        }
                    }
                }
            }

            // Finally, we need to add any "free" samrodnings-VG not already present. These can NOT have a VE as
            // those would already have been added in the section above.
            for (String ibVgId : samordnareCareGiverIds) {

                // Fetch name from HSA, note that this is a shallow copy without units on!
                Vardgivare vgFromHsa = getHsaOrganizationsService().getVardgivareInfo(ibVgId);

                IbVardgivare ibVardgivare = new IbVardgivare(ibVgId, vgFromHsa != null ? vgFromHsa.getNamn() : ibVgId, true);
                if (!authSystemTree.contains(ibVardgivare)) {
                    authSystemTree.add(ibVardgivare);
                }
            }

            // Sort VG by name...
            authSystemTree = authSystemTree.stream().sorted(Comparator.comparing(IbVardgivare::getName)).collect(Collectors.toList());

            // Sort underlying VE by name..
            authSystemTree.forEach(vgg -> vgg.setVardenheter(
                    vgg.getVardenheter().stream().sorted(Comparator.comparing(IbVardenhet::getName)).collect(Collectors.toList())));

            user.setSystemAuthorities(authSystemTree);
        }

        // Real careful here...
        // We may add either:
        // Vårdgivare that has at least one child VE for which there is a VARDADMIN systemRole.
        // or
        // Any vårdgivare we have careGiverId from a systemRole for. These do not require Medarbetaruppdrag V&B so we
        // basically need to fetch them seperately...

    }

    private int getTotaltAntalVardenheterExcludingMottagningar(IntygUser intygUser) {
        // count all vardenheter (not including mottagningar under vardenheter)
        return (int) intygUser.getVardgivare().stream().flatMap(vg -> vg.getVardenheter().stream()).count();
    }

    @Override
    protected void decorateIntygUserWithSystemRoles(IntygUser intygUser, UserCredentials userCredentials) {
        super.decorateIntygUserWithSystemRoles(intygUser, userCredentials);
    }

    @Override
    protected String getDefaultRole() {
        return AuthoritiesConstants.ROLE_FMU_VARDADMIN;
    }

    @Override
    protected BaseSakerhetstjanstAssertion getAssertion(Assertion assertion) {
        return super.getAssertion(assertion);
    }

}
