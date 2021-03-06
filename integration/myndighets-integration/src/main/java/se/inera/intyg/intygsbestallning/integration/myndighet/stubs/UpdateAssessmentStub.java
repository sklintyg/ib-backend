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
package se.inera.intyg.intygsbestallning.integration.myndighet.stubs;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.riv.intygsbestallning.certificate.order.updateassessment.v1.UpdateAssessmentResponseType;
import se.riv.intygsbestallning.certificate.order.updateassessment.v1.UpdateAssessmentType;
import se.riv.intygsbestallning.certificate.order.updateassessment.v1.rivtabp21.UpdateAssessmentResponderInterface;
import se.riv.intygsbestallning.certificate.order.v1.ResultCodeType;
import se.riv.intygsbestallning.certificate.order.v1.ResultType;
import java.time.LocalDateTime;
import se.inera.intyg.intygsbestallning.common.util.SchemaDateUtil;

public class UpdateAssessmentStub implements UpdateAssessmentResponderInterface {

    private static final Logger LOG = LoggerFactory.getLogger(UpdateAssessmentStub.class);
    private static final long DAYS = 7L;

    @Override
    public UpdateAssessmentResponseType updateAssessment(String logicalAddress, UpdateAssessmentType updateAssessmentType) {

        LOG.info("UpdateAssessmentStub received request");

        ResultType rt = new ResultType();

        if (isInvalidRequest(updateAssessmentType)) {
            rt.setResultCode(ResultCodeType.ERROR);
        } else {
            rt.setResultCode(ResultCodeType.OK);
        }
        UpdateAssessmentResponseType response = new UpdateAssessmentResponseType();
        response.setLastDateForCertificateReceival(SchemaDateUtil.toDateStringFromLocalDateTime(LocalDateTime.now().plusDays(DAYS)));
        response.setResult(rt);
        return response;
    }

    private boolean isInvalidRequest(UpdateAssessmentType updateAssessmentType) {
        return Strings.isNullOrEmpty(updateAssessmentType.getAssessmentId().getRoot())
                || Strings.isNullOrEmpty(updateAssessmentType.getAssessmentId().getExtension())
                || Strings.isNullOrEmpty(updateAssessmentType.getCertificateType().getCode())
                || Strings.isNullOrEmpty(updateAssessmentType.getCertificateType().getCodeSystem());
    }
}
