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
package se.inera.intyg.intygsbestallning.service.handling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.inera.intyg.intygsbestallning.auth.IbUser;
import se.inera.intyg.intygsbestallning.common.exception.IbErrorCodeEnum;
import se.inera.intyg.intygsbestallning.common.exception.IbNotFoundException;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.persistence.model.Handelse;
import se.inera.intyg.intygsbestallning.persistence.model.Handling;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.type.HandlingUrsprungTyp;
import se.inera.intyg.intygsbestallning.service.handelse.HandelseUtil;
import se.inera.intyg.intygsbestallning.service.pdl.LogService;
import se.inera.intyg.intygsbestallning.service.stateresolver.UtredningFas;
import se.inera.intyg.intygsbestallning.service.stateresolver.UtredningStatus;
import se.inera.intyg.intygsbestallning.service.stateresolver.UtredningStatusResolver;
import se.inera.intyg.intygsbestallning.service.utredning.BaseUtredningService;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.handling.RegisterHandlingRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.handling.RegisterHandlingResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

@Service
public class HandlingServiceImpl extends BaseUtredningService implements HandlingService {

    public static final Logger LOG = LoggerFactory.getLogger(HandlingServiceImpl.class);

    @Autowired
    private LogService logService;

    private UtredningStatusResolver utredningStatusResolver = new UtredningStatusResolver();

    @Override
    @Transactional
    public RegisterHandlingResponse registerNewHandling(Long utredningId, RegisterHandlingRequest request) {

        LocalDate mottagenDatum = parseDate(request);

        Optional<Utredning> utredningOptional = utredningRepository.findById(utredningId);
        if (!utredningOptional.isPresent()) {
            throw new IbNotFoundException("No utredning matching ID " + utredningId + " found.");
        }

        Utredning utredning = utredningOptional.get();
        UtredningStatus status = utredningStatusResolver.resolveStatus(utredning);
        // Check state - can this utredning really register handlingar?
        if (status.getUtredningFas() == UtredningFas.FORFRAGAN || status.getUtredningFas() == UtredningFas.AVSLUTAD) {
            throw new IbServiceException(IbErrorCodeEnum.BAD_STATE,
                    "This utredning is in phase " + status.getUtredningFas().getLabel() + " and cannot accept new Handlingar.");
        }

        Handling handling = Handling.HandlingBuilder.aHandling()
                .withInkomDatum(mottagenDatum.atStartOfDay())
                .withSkickatDatum(LocalDateTime.now())
                .withUrsprung(HandlingUrsprungTyp.UPPDATERING)
                .build();
        utredning.getHandlingList().add(handling);
        utredningRepository.save(utredning);

        IbUser user = userService.getUser();

        // Store handelse.
        Handelse handelse = HandelseUtil.createHandlingMottagen(user.getNamn(), request.getHandlingarMottogsDatum());
        utredning.getHandelseList().add(handelse);

        // PDL log
        logService.logHandlingMottagen(utredning);

        return new RegisterHandlingResponse();
    }

    private LocalDate parseDate(RegisterHandlingRequest request) {
        try {
            return LocalDate.parse(request.getHandlingarMottogsDatum(), DateTimeFormatter.ISO_DATE);
        } catch (DateTimeParseException e) {
            LOG.error("Unable to parse handlingarMottogsDatum, message: {}", e.getMessage());
            throw new IbServiceException(IbErrorCodeEnum.BAD_REQUEST, "Unable to parse handlingarMottogsDatum. "
                    + "Valid format is yyyy-MM-dd");
        }
    }
}
