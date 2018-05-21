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

import org.apache.cxf.annotations.SchemaValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.intyg.intygsbestallning.service.utlatande.UtlatandeService;
import se.inera.intyg.intygsbestallning.web.responder.dto.RegistreraUtlatandeMottagetRequest;
import se.riv.intygsbestallning.certificate.order.reportcertificatereceival.v1.ReportCertificateReceivalResponseType;
import se.riv.intygsbestallning.certificate.order.reportcertificatereceival.v1.ReportCertificateReceivalType;
import se.riv.intygsbestallning.certificate.order.reportcertificatereceival.v1.rivtabp21.ReportCertificateReceivalResponderInterface;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.nonNull;
import static se.inera.intyg.intygsbestallning.common.util.ResultTypeUtil.ok;

@Service
@SchemaValidation
public class ReportCertificateReceivalResponderImpl implements ReportCertificateReceivalResponderInterface {

    @Autowired
    private UtlatandeService utlatandeService;

    @Override
    public ReportCertificateReceivalResponseType reportCertificateReceival(
            final String logicalAddress, final ReportCertificateReceivalType request) {

        checkArgument(nonNull(logicalAddress));
        checkArgument(nonNull(request));

        utlatandeService.registreraUtlatandeMottaget(RegistreraUtlatandeMottagetRequest.from(request));

        ReportCertificateReceivalResponseType response = new ReportCertificateReceivalResponseType();
        response.setResult(ok());
        return response;
    }
}
