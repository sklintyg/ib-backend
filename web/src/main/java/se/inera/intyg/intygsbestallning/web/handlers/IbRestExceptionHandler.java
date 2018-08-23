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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import javax.servlet.http.HttpServletRequest;
import se.inera.intyg.intygsbestallning.common.exception.IbAuthorizationException;
import se.inera.intyg.intygsbestallning.common.exception.IbErrorCodeEnum;
import se.inera.intyg.intygsbestallning.common.exception.IbExternalServiceException;
import se.inera.intyg.intygsbestallning.common.exception.IbJMSException;
import se.inera.intyg.intygsbestallning.common.exception.IbNotFoundException;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.common.util.LogIdGenerator;

import java.text.MessageFormat;

@ControllerAdvice
public class IbRestExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(IbRestExceptionHandler.class);

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public IbRestExceptionResponse authorizationExceptionHandler(HttpServletRequest request, IbAuthorizationException e) {
        LOG.warn("IbAuthorizationException occurred! Internal error code: {}, Log ID: {}, Error message: {}",
                e.getLogId(), e.getErrorCode(), e.getMessage());
        IbRestExceptionResponse response =
                new IbAuthorizationRestExceptionResponse(e.getErrorCode(), e.getMessage(), e.getErrorEntityId(),
                        e.getAuthorizationErrorCode(), e.getLogId());
        return response;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public IbRestExceptionResponse notFoundExceptionHandler(HttpServletRequest request, IbNotFoundException e) {
        LOG.warn("IbNotFoundException occurred! Internal error code: {}, Log ID: {}, Error message: {}",
                e.getLogId(), e.getErrorCode(), e.getMessage());
        IbRestExceptionResponse response =
                new IbRestExceptionResponse(e.getErrorCode(), e.getMessage(), e.getErrorEntityId(), e.getLogId());
        return response;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public IbRestExceptionResponse serviceExceptionHandler(HttpServletRequest request, IllegalArgumentException iae) {
        String logId = LogIdGenerator.generate();
        LOG.warn("IllegalArgumentException occurred! Error message: {}", logId, iae.getMessage());
        return new IbRestExceptionResponse(IbErrorCodeEnum.BAD_REQUEST, iae.getMessage(), logId);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public IbRestExceptionResponse serviceExceptionHandler(HttpServletRequest request, IllegalStateException ise) {
        String logId = LogIdGenerator.generate();
        LOG.warn("IllegalStateException occurred! Log ID: {}, Error message: {}", logId, ise.getMessage());
        return new IbRestExceptionResponse(IbErrorCodeEnum.BAD_REQUEST, ise.getMessage(), logId);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public IbRestExceptionResponse serviceExceptionHandler(HttpServletRequest request, IbServiceException e) {
        String logId = LogIdGenerator.generate();
        LOG.warn("IbServiceException occurred! Internal error code: {}, Error message: {}",
                logId, e.getErrorCode(), e.getMessage());
        IbRestExceptionResponse response =
                new IbRestExceptionResponse(e.getErrorCode(), e.getMessage(), e.getErrorEntityId(), e.getLogId());
        return response;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public IbRestExceptionResponse serviceExceptionHandler(HttpServletRequest request, IbJMSException re) {
        String logId = LogIdGenerator.generate();
        LOG.error(MessageFormat.format("IbJMSException occurred! Log ID: {}", logId), re);
        IbRestExceptionResponse response = new IbRestExceptionResponse(
                IbErrorCodeEnum.UNKNOWN_INTERNAL_PROBLEM, re.getMessage(), re.getErrorEntityId(), re.getLogId());
        return response;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public IbRestExceptionResponse serviceExceptionHandler(HttpServletRequest request, IbExternalServiceException e) {
        String logId = LogIdGenerator.generate();
        LOG.error(MessageFormat.format("IbExternalServiceException occurred! Log ID: {}", logId), e);
        IbRestExceptionResponse response = new IbRestExceptionResponse(
                IbErrorCodeEnum.EXTERNAL_ERROR, e.getExternalSystem(), e.getMessage(), e.getErrorEntityId(), e.getLogId());
        return response;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public IbRestExceptionResponse serviceExceptionHandler(HttpServletRequest request, RuntimeException re) {
        String logId = LogIdGenerator.generate();
        LOG.error(MessageFormat.format("RuntimeException occurred! Log ID: {}", logId), re);
        IbRestExceptionResponse response = new IbRestExceptionResponse(
                IbErrorCodeEnum.UNKNOWN_INTERNAL_PROBLEM, "Unhandled runtime exception", logId);
        return response;
    }

}
