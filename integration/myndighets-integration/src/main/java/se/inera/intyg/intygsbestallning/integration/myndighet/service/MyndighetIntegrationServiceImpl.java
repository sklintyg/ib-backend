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
package se.inera.intyg.intygsbestallning.integration.myndighet.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.intyg.intygsbestallning.common.exception.IbExternalServiceException;
import se.inera.intyg.intygsbestallning.common.exception.IbErrorCodeEnum;
import se.inera.intyg.intygsbestallning.common.exception.IbExternalSystemEnum;
import se.inera.intyg.intygsbestallning.common.exception.IbFailingServiceMethodEnum;
import se.inera.intyg.intygsbestallning.common.util.SchemaDateUtil;
import se.inera.intyg.intygsbestallning.integration.myndighet.client.MyndighetIntegrationClientService;
import se.inera.intyg.intygsbestallning.integration.myndighet.dto.ReportCareContactRequestDto;
import se.inera.intyg.intygsbestallning.integration.myndighet.dto.ReportDeviationRequestDto;
import se.inera.intyg.intygsbestallning.integration.myndighet.dto.RespondToPerformerRequestDto;
import se.riv.intygsbestallning.certificate.order.reportcarecontact.v1.ReportCareContactResponseType;
import se.riv.intygsbestallning.certificate.order.reportdeviation.v1.ReportDeviationResponseType;
import se.riv.intygsbestallning.certificate.order.respondtoperformerrequest.v1.RespondToPerformerRequestResponseType;
import se.riv.intygsbestallning.certificate.order.updateassessment.v1.UpdateAssessmentResponseType;
import se.riv.intygsbestallning.certificate.order.v1.ResultCodeType;
import se.riv.intygsbestallning.certificate.order.v1.ResultType;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;

@Service
public class MyndighetIntegrationServiceImpl implements MyndighetIntegrationService {

    private static Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private MyndighetIntegrationClientService clientService;

    @Override
    public void respondToPerformerRequest(final RespondToPerformerRequestDto request) {
        try {
            RespondToPerformerRequestResponseType response = clientService.respondToPerformerRequest(request);
            handleResponse(response.getResult(), IbFailingServiceMethodEnum.RESPOND_TO_PERFORMER);
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            throw new IbExternalServiceException(IbErrorCodeEnum.EXTERNAL_ERROR, IbExternalSystemEnum.MYNDIGHET,
                    e.getMessage(), IbFailingServiceMethodEnum.RESPOND_TO_PERFORMER);
        }
    }

    @Override
    public void reportCareContactInteraction(final ReportCareContactRequestDto request) {
        try {
            final ReportCareContactResponseType response = clientService.reportCareContact(request);
            handleResponse(response.getResult(), IbFailingServiceMethodEnum.REPORT_CARE_CONTACT);
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            throw new IbExternalServiceException(IbErrorCodeEnum.EXTERNAL_ERROR, IbExternalSystemEnum.MYNDIGHET,
                    e.getMessage(), IbFailingServiceMethodEnum.REPORT_CARE_CONTACT);
        }
    }

    @Override
    public LocalDateTime updateAssessment(final Long id, final String certificateType) {
        try {
            final UpdateAssessmentResponseType response = clientService.updateAssessment(id, certificateType);
            handleResponse(response.getResult(), IbFailingServiceMethodEnum.UPDATE_ASSESSMENT);
            return SchemaDateUtil.toLocalDateTimeFromDateType(response.getLastDateForCertificateReceival());
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            throw new IbExternalServiceException(IbErrorCodeEnum.EXTERNAL_ERROR, IbExternalSystemEnum.MYNDIGHET,
                    e.getMessage(), IbFailingServiceMethodEnum.UPDATE_ASSESSMENT);
        }
    }

    @Override
    public void reportDeviation(final ReportDeviationRequestDto request) {
        try {
            final ReportDeviationResponseType response = clientService.reportDeviation(request);
            handleResponse(response.getResult(), IbFailingServiceMethodEnum.REPORT_DEVIATION);
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            throw new IbExternalServiceException(IbErrorCodeEnum.EXTERNAL_ERROR, IbExternalSystemEnum.MYNDIGHET,
                    e.getMessage(), IbFailingServiceMethodEnum.REPORT_DEVIATION);
        }
    }

    private void handleResponse(final ResultType resultType, final IbFailingServiceMethodEnum failingServiceMethod) {
        final ResultCodeType resultCode = resultType.getResultCode();
        final String resultText = resultType.getResultText();

        if (resultCode.equals(ResultCodeType.ERROR)) {
            log.error(resultText);
            throw new IbExternalServiceException(IbErrorCodeEnum.EXTERNAL_ERROR, IbExternalSystemEnum.MYNDIGHET,
                    resultText, failingServiceMethod);
        } else if (resultCode.equals(ResultCodeType.INFO)) {
            log.info(resultText);
        }
    }
}
