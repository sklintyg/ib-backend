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

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import se.inera.intyg.intygsbestallning.common.exception.IbErrorCodeEnum;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;

@ControllerAdvice
public class IbRestExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(IbRestExceptionHandler.class);

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public IbRestExceptionResponse serviceExceptionHandler(HttpServletRequest request, IbServiceException e) {
        LOG.warn("Internal exception occured! Internal error code: {} Error message: {}", e.getErrorCode(),
                e.getMessage());
       IbRestExceptionResponse response =
                new IbRestExceptionResponse(e.getErrorCode(), e.getMessage());
        return response;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public IbRestExceptionResponse serviceExceptionHandler(HttpServletRequest request, RuntimeException re) {
        LOG.error("Unhandled RuntimeException occured!", re);
        IbRestExceptionResponse response = new IbRestExceptionResponse(
                IbErrorCodeEnum.UNKNOWN_INTERNAL_PROBLEM, "Unhandled runtime exception");
        return response;
    }

}
