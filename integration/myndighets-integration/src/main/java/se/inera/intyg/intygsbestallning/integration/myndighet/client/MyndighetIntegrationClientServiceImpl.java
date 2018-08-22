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
package se.inera.intyg.intygsbestallning.integration.myndighet.client;

import static se.inera.intyg.intygsbestallning.integration.myndighet.service.TjanstekontraktUtils.aReportCareContact;
import static se.inera.intyg.intygsbestallning.integration.myndighet.service.TjanstekontraktUtils.aReportDeviation;
import static se.inera.intyg.intygsbestallning.integration.myndighet.service.TjanstekontraktUtils.aRespondToPerformerRequest;
import static se.inera.intyg.intygsbestallning.integration.myndighet.service.TjanstekontraktUtils.anUpdateAssessmentType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.riv.intygsbestallning.certificate.order.reportcarecontact.v1.ReportCareContactResponseType;
import se.riv.intygsbestallning.certificate.order.reportcarecontact.v1.rivtabp21.ReportCareContactResponderInterface;
import se.riv.intygsbestallning.certificate.order.reportdeviation.v1.ReportDeviationResponseType;
import se.riv.intygsbestallning.certificate.order.reportdeviation.v1.rivtabp21.ReportDeviationResponderInterface;
import se.riv.intygsbestallning.certificate.order.respondtoperformerrequest.v1.RespondToPerformerRequestResponseType;
import se.riv.intygsbestallning.certificate.order.respondtoperformerrequest.v1.rivtabp21.RespondToPerformerRequestResponderInterface;
import se.riv.intygsbestallning.certificate.order.updateassessment.v1.UpdateAssessmentResponseType;
import se.riv.intygsbestallning.certificate.order.updateassessment.v1.rivtabp21.UpdateAssessmentResponderInterface;
import se.inera.intyg.intygsbestallning.integration.myndighet.dto.ReportCareContactRequestDto;
import se.inera.intyg.intygsbestallning.integration.myndighet.dto.ReportDeviationRequestDto;
import se.inera.intyg.intygsbestallning.integration.myndighet.dto.RespondToPerformerRequestDto;

@Service
public class MyndighetIntegrationClientServiceImpl implements MyndighetIntegrationClientService {

    @Autowired
    private RespondToPerformerRequestResponderInterface respondToPerformerRequestResponder;

    @Autowired
    private ReportCareContactResponderInterface reportCareContactResponder;

    @Autowired
    private UpdateAssessmentResponderInterface updateAssessmentResponder;

    @Autowired
    @Qualifier("reportDeviationResponderMyndighet")
    private ReportDeviationResponderInterface reportDeviationResponder;

    @Value("${source.system.hsaid:}")
    private String sourceSystemHsaId;

    @Override
    public RespondToPerformerRequestResponseType respondToPerformerRequest(final RespondToPerformerRequestDto request) {
        return respondToPerformerRequestResponder.respondToPerformerRequest(
                sourceSystemHsaId, aRespondToPerformerRequest(sourceSystemHsaId, request));
    }

    @Override
    public ReportCareContactResponseType reportCareContact(final ReportCareContactRequestDto request) {
        return reportCareContactResponder.reportCareContact(
                sourceSystemHsaId, aReportCareContact(sourceSystemHsaId, request));
    }

    @Override
    public ReportDeviationResponseType reportDeviation(final ReportDeviationRequestDto request) {
        return reportDeviationResponder.reportDeviation(
                sourceSystemHsaId, aReportDeviation(sourceSystemHsaId, request));
    }

    @Override
    public UpdateAssessmentResponseType updateAssessment(final Long assessmentId, final String certificateType) {
        return updateAssessmentResponder.updateAssessment(
                sourceSystemHsaId, anUpdateAssessmentType(sourceSystemHsaId, assessmentId, certificateType));
    }
}
