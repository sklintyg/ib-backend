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
import static org.apache.commons.lang.StringUtils.isNotEmpty;

import org.apache.cxf.annotations.SchemaValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.monitoring.annotation.PrometheusTimeMethod;
import se.inera.intyg.intygsbestallning.common.util.ResultTypeUtil;
import se.riv.intygsbestallning.certificate.order.reportsupplementreceival.v1.ReportSupplementReceivalResponseType;
import se.riv.intygsbestallning.certificate.order.reportsupplementreceival.v1.ReportSupplementReceivalType;
import se.riv.intygsbestallning.certificate.order.reportsupplementreceival.v1.rivtabp21.ReportSupplementReceivalResponderInterface;
import se.inera.intyg.intygsbestallning.service.utredning.KompletteringService;
import se.inera.intyg.intygsbestallning.web.responder.dto.ReportKompletteringMottagenRequest;

@Service
@SchemaValidation
public class ReportSupplementReceivalResponderImpl implements ReportSupplementReceivalResponderInterface {

    private final KompletteringService kompletteringService;

    private final Logger log = LoggerFactory.getLogger(lookup().lookupClass());

    public ReportSupplementReceivalResponderImpl(final KompletteringService kompletteringService) {
        this.kompletteringService = kompletteringService;
    }

    @Override
    @PrometheusTimeMethod
    public ReportSupplementReceivalResponseType reportSupplementReceival(
            final String logicalAddress, final ReportSupplementReceivalType request) {

        log.info("Received ReportSupplementReceival request");

        checkArgument(isNotEmpty(logicalAddress), ResultTypeUtil.LOGICAL_ADDRESS);
        checkArgument(nonNull(request), ResultTypeUtil.REQUEST);

        kompletteringService.reportKompletteringMottagen(ReportKompletteringMottagenRequest.from(request));

        ReportSupplementReceivalResponseType response = new ReportSupplementReceivalResponseType();
        response.setResult(ResultTypeUtil.ok());
        return response;
    }
}
