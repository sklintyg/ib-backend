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
package se.inera.intyg.intygsbestallning.auth.authorities;

/**
 * Created by mango on 25/11/15.
 */
public final class AuthoritiesConstants {


    // Known roles (these roles are copied from authorities.yaml which is the master authorities configuration)
    public static final String ROLE_FMU_VARDADMIN = "FMU_VARDADMIN";
    public static final String ROLE_FMU_SAMORDNARE = "FMU_SAMORDNARE";

    // Privileges
    public static final String PRIVILEGE_LISTA_UTREDNINGAR = "LISTA_UTREDNINGAR";
    public static final String PRIVILEGE_LISTA_FORFRAGNINGAR = "LISTA_FORFRAGNINGAR";
    public static final String PRIVILEGE_VISA_UTREDNING = "VISA_UTREDNING";
    public static final String PRIVILEGE_VISA_FORFRAGAN = "VISA_FORFRAGAN";

    // constructors

    private AuthoritiesConstants() {
    }

}
