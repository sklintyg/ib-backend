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
package se.inera.intyg.intygsbestallning.common.exception;

public class IbExternalServiceException extends IbServiceException {
    private final IbExternalSystemEnum externalSystem;
    private final IbFailingServiceMethodEnum failingServiceMethod;

    public IbExternalServiceException(
            final IbErrorCodeEnum errorCode,
            final IbExternalSystemEnum externalSystem,
            final IbFailingServiceMethodEnum failingServiceMethod) {
        super(errorCode, null, null);
        this.externalSystem = externalSystem;
        this.failingServiceMethod = failingServiceMethod;
    }

    public IbExternalServiceException(final IbErrorCodeEnum errorCode, final IbExternalSystemEnum externalSystem,
                                      final String message, final IbFailingServiceMethodEnum failingServiceMethod, Long errorEntityId) {
        super(errorCode, message, errorEntityId);
        this.externalSystem = externalSystem;
        this.failingServiceMethod = failingServiceMethod;
    }

    public IbExternalServiceException(final IbErrorCodeEnum errorCode, final IbExternalSystemEnum externalSystem,
                                      final String message, final IbFailingServiceMethodEnum failingServiceMethod) {
        super(errorCode, message);
        this.externalSystem = externalSystem;
        this.failingServiceMethod = failingServiceMethod;
    }


    public IbExternalSystemEnum getExternalSystem() {
        return externalSystem;
    }

    public IbFailingServiceMethodEnum getFailingServiceMethod() {
        return failingServiceMethod;
    }
}
