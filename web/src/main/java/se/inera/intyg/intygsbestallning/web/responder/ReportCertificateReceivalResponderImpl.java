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

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.annotations.SchemaValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import se.riv.intygsbestallning.certificate.order.reportcertificatereceival.v1.ReportCertificateReceivalResponseType;
import se.riv.intygsbestallning.certificate.order.reportcertificatereceival.v1.ReportCertificateReceivalType;
import se.riv.intygsbestallning.certificate.order.reportcertificatereceival.v1.rivtabp21.ReportCertificateReceivalResponderInterface;
import se.inera.intyg.intygsbestallning.service.utlatande.UtlatandeService;
import se.inera.intyg.intygsbestallning.web.responder.dto.ReportUtlatandeMottagetRequest;
import se.inera.intyg.intygsbestallning.web.responder.resulthandler.ResultFactory;

@Service
@SchemaValidation
public class ReportCertificateReceivalResponderImpl implements ReportCertificateReceivalResponderInterface, ResultFactory {

    private final UtlatandeService utlatandeService;

    private final Logger log = LoggerFactory.getLogger(lookup().lookupClass());

    public ReportCertificateReceivalResponderImpl(final UtlatandeService utlatandeService) {
        this.utlatandeService = utlatandeService;
    }

    @Override
    public ReportCertificateReceivalResponseType reportCertificateReceival(
            final String logicalAddress, final ReportCertificateReceivalType request) {

        log.info("Received ReportCertificateReceival request");

        try {
            checkArgument(StringUtils.isNotEmpty(logicalAddress), LOGICAL_ADDRESS);
            checkArgument(nonNull(request), REQUEST);

            utlatandeService.reportUtlatandeMottaget(ReportUtlatandeMottagetRequest.from(request));

            ReportCertificateReceivalResponseType response = new ReportCertificateReceivalResponseType();
            response.setResult(toResultTypeOK());
            return response;
        } catch (final Exception e) {
            ReportCertificateReceivalResponseType response = new ReportCertificateReceivalResponseType();
            response.setResult(toResultTypeError(e));
            return response;
        }
    }
}
