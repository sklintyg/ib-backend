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
package se.inera.intyg.intygsbestallning.web.handlers;

import se.inera.intyg.intygsbestallning.common.exception.IbErrorCodeEnum;
import se.inera.intyg.intygsbestallning.common.exception.IbExternalSystemEnum;

public class IbRestExceptionResponse {

    private IbErrorCodeEnum errorCode;

    private IbExternalSystemEnum externalSystem;

    private String message;

    private String logId;

    private Long errorEntityId;

    public IbRestExceptionResponse(IbErrorCodeEnum errorCode, String message, String logId) {
        this.errorCode = errorCode;
        this.message = message;
        this.logId = logId;
    }

    public IbRestExceptionResponse(IbErrorCodeEnum errorCode, String message, Long errorEntityId, String logId) {
        this.errorCode = errorCode;
        this.message = message;
        this.errorEntityId = errorEntityId;
        this.logId = logId;
    }

    public IbRestExceptionResponse(IbErrorCodeEnum errorCode, IbExternalSystemEnum externalSystem, String message) {
        this.errorCode = errorCode;
        this.externalSystem = externalSystem;
        this.message = message;
    }

    public IbRestExceptionResponse(IbErrorCodeEnum errorCode,
                                   IbExternalSystemEnum externalSystem,
                                   String message,
                                   Long errorEntityId,
                                   String logId) {
        this.errorCode = errorCode;
        this.externalSystem = externalSystem;
        this.message = message;
        this.errorEntityId = errorEntityId;
        this.logId = logId;
    }

    public IbErrorCodeEnum getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(IbErrorCodeEnum errorCode) {
        this.errorCode = errorCode;
    }

    public IbExternalSystemEnum getExternalSystemId() {
        return externalSystem;
    }

    public void setExternalSystemId(IbExternalSystemEnum externalSystem) {
        this.externalSystem = externalSystem;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
