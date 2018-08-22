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
package se.inera.intyg.intygsbestallning.auth.bp;

import org.springframework.security.saml.SAMLCredential;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.integration.hsa.model.Vardenhet;
import se.inera.intyg.infra.integration.hsa.model.Vardgivare;
import se.inera.intyg.infra.security.common.model.IntygUser;
import se.inera.intyg.intygsbestallning.auth.IbUser;
import se.inera.intyg.intygsbestallning.auth.IbUserDetailsService;
import se.inera.intyg.intygsbestallning.auth.exceptions.MissingIBSystemRoleException;
import se.inera.intyg.intygsbestallning.auth.model.IbRelayStateType;
import se.inera.intyg.intygsbestallning.auth.model.IbVardenhet;
import se.inera.intyg.intygsbestallning.auth.model.IbVardgivare;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides BP-specific authorization code overrides.
 */
@Service
public class IbBpUserDetailsService extends IbUserDetailsService {

    @Override
    protected IbUser buildUserPrincipal(SAMLCredential credential) {
        // All IB customization is done in the overridden decorateXXX methods, so just return a new IbUSer
        IntygUser intygUser = super.buildUserPrincipal(credential);
        IbUser ibUser = new IbUser(intygUser, IbRelayStateType.BP);
        ibUser.setPossibleRoles(commonAuthoritiesResolver.getRoles());
        buildSystemAuthoritiesTree(ibUser);

        if (ibUser.getSystemAuthorities().size() == 0) {
            throw new MissingIBSystemRoleException(ibUser.getHsaId());
        }

        // If only a single possible entity to select as loggedInAt exists, do that...
        tryToSelectHsaEntity(ibUser);

        return ibUser;
    }

    @Override
    public void buildSystemAuthoritiesTree(IbUser user) {
        List<IbVardgivare> authSystemTree = new ArrayList<>();

        for (Vardgivare vg : user.getVardgivare()) {
            IbVardgivare ibVardgivare = new IbVardgivare(vg.getId(), vg.getNamn(), false);
            for (Vardenhet ve : vg.getVardenheter()) {
                ibVardgivare.getVardenheter().add(new IbVardenhet(ve.getId(), ve.getNamn(), ibVardgivare, ve.getVardgivareOrgnr()));
            }
            authSystemTree.add(ibVardgivare);
        }
        user.setSystemAuthorities(authSystemTree);
    }

    @Override
    protected void tryToSelectHsaEntity(IbUser ibUser) {
        if (ibUser.getTotaltAntalVardenheter() == 1) {
            ibUser.changeValdVardenhet(ibUser.getVardgivare().get(0).getVardenheter().get(0).getId());
        }
    }
}
