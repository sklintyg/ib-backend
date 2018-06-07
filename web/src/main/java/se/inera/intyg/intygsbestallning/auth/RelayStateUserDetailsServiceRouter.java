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

import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;
import org.springframework.stereotype.Component;
import se.inera.intyg.intygsbestallning.auth.bp.IbBpUserDetailsService;
import se.inera.intyg.intygsbestallning.auth.model.IbRelayStateType;
import se.inera.intyg.intygsbestallning.common.exception.IbAuthorizationException;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static se.inera.intyg.intygsbestallning.auth.model.IbRelayStateType.*;

/**
 * Picks the appropriate UserDetailsService depending on the relayState.
 *
 * If no relayState is set, default to FMU.
 */
@Component
public class RelayStateUserDetailsServiceRouter implements SAMLUserDetailsService {

    @Autowired
    private IbUserDetailsService ibUserDetailsService;

    @Autowired
    private IbBpUserDetailsService ibBpUserDetailsService;

    @Override
    public Object loadUserBySAML(SAMLCredential credential) throws UsernameNotFoundException {
        String relayState = Strings.isNullOrEmpty(credential.getRelayState()) ? FMU.name() : credential.getRelayState();
        try {
            IbRelayStateType relayStateType = valueOf(relayState);
            switch (relayStateType) {
                case BP:
                    return ibBpUserDetailsService.loadUserBySAML(credential);
                case FMU:
                default:
                    return ibUserDetailsService.loadUserBySAML(credential);
            }
        } catch (IllegalArgumentException e) {
            throw new IbAuthorizationException("Unknown RelayState: '" + relayState + "'. Allowed types are: "
                    + Stream.of(values())
                    .map(IbRelayStateType::name)
                    .collect(Collectors.joining(",")));
        }
    }

    public void setIbUserDetailsService(IbUserDetailsService ibUserDetailsService) {
        this.ibUserDetailsService = ibUserDetailsService;
    }

    public void setIbBpUserDetailsService(IbBpUserDetailsService ibBpUserDetailsService) {
        this.ibBpUserDetailsService = ibBpUserDetailsService;
    }
}
