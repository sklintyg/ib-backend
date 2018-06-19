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
package se.inera.intyg.intygsbestallning.web.responder;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.invoke.MethodHandles.lookup;
import static java.util.Objects.nonNull;
import static se.inera.intyg.intygsbestallning.common.util.RivtaTypesUtil.anII;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.annotations.SchemaValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.riv.intygsbestallning.certificate.order.reportdeviation.v1.ReportDeviationResponseType;
import se.riv.intygsbestallning.certificate.order.reportdeviation.v1.ReportDeviationType;
import se.riv.intygsbestallning.certificate.order.reportdeviation.v1.rivtabp21.ReportDeviationResponderInterface;
import se.inera.intyg.intygsbestallning.persistence.model.Avvikelse;
import se.inera.intyg.intygsbestallning.service.besok.BesokService;
import se.inera.intyg.intygsbestallning.web.responder.dto.ReportBesokAvvikelseRequest;
import se.inera.intyg.intygsbestallning.web.responder.resulthandler.ResultFactory;

@Service
@SchemaValidation
public class ReportDeviationInteractionResponderImpl implements ReportDeviationResponderInterface, ResultFactory {

    @Value("${source.system.hsaid:}")
    private String sourceSystemHsaId;

    private final BesokService besokService;

    private final Logger log = LoggerFactory.getLogger(lookup().lookupClass());

    public ReportDeviationInteractionResponderImpl(final BesokService besokService) {
        this.besokService = besokService;
    }

    @Override
    public ReportDeviationResponseType reportDeviation(
            final String logicalAddress, final ReportDeviationType request) {

        log.info("Received ReportDeviationInteraction request");

        try {
            checkArgument(StringUtils.isNotEmpty(logicalAddress), LOGICAL_ADDRESS);
            checkArgument(nonNull(request), REQUEST);

            final Avvikelse avvikelse = besokService.reportBesokAvvikelse(ReportBesokAvvikelseRequest.from(request));

            ReportDeviationResponseType response = new ReportDeviationResponseType();
            response.setDeviationId(anII(sourceSystemHsaId, avvikelse.getAvvikelseId().toString()));
            response.setResult(toResultTypeOK());
            return response;
        } catch (final Exception e) {
            ReportDeviationResponseType response = new ReportDeviationResponseType();
            response.setResult(toResultTypeError(e));
            return response;
        }
    }
}
