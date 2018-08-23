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

import se.inera.intyg.intygsbestallning.common.util.LogIdGenerator;

public class IbServiceException extends RuntimeException {
    private final IbErrorCodeEnum errorCode;
    private Long errorEntityId;
    private String logId;

    public IbServiceException(IbErrorCodeEnum errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.errorEntityId = null;
        this.setLogId(LogIdGenerator.generate());
    }

    public IbServiceException(IbErrorCodeEnum errorCode, String message, Long errorEntityId) {
        super(message);
        this.errorCode = errorCode;
        this.errorEntityId = errorEntityId;
        this.setLogId(LogIdGenerator.generate());
    }

    public IbErrorCodeEnum getErrorCode() {
        return errorCode;
    }

    public Long getErrorEntityId() {
        return errorEntityId;
    }

    public void setErrorEntityId(Long errorEntityId) {
        this.errorEntityId = errorEntityId;
    }

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }
}
