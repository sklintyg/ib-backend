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
package se.inera.intyg.intygsbestallning.service.besok;

import static java.util.Objects.nonNull;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import se.inera.intyg.intygsbestallning.service.utredning.BaseUtredningService;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.RegisterBesokRequest;
import se.riv.intygsbestallning.certificate.order.reportcarecontact.v1.rivtabp21.ReportCareContactResponderInterface;

import java.lang.invoke.MethodHandles;
import java.text.MessageFormat;

@Service
public class BesokServiceImpl extends BaseUtredningService implements BesokService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ReportCareContactResponderInterface reportCareContact;

    public BesokServiceImpl(final ReportCareContactResponderInterface reportCareContact) {
        this.reportCareContact = reportCareContact;
    }

    @Override
    public void registerNewBesok(final RegisterBesokRequest request) {
        LOG.debug(MessageFormat.format("Received a request to register new besok for utredning with id {}", "bra-id"));

        Preconditions.checkArgument(nonNull(request), "request may not be null");
    }
}
