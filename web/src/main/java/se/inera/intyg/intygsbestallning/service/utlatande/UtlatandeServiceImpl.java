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
package se.inera.intyg.intygsbestallning.service.utlatande;

import static com.google.common.collect.MoreCollectors.onlyElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.function.Predicate;
import se.inera.intyg.intygsbestallning.auth.IbUser;
import se.inera.intyg.intygsbestallning.common.exception.IbErrorCodeEnum;
import se.inera.intyg.intygsbestallning.common.exception.IbNotFoundException;
import se.inera.intyg.intygsbestallning.common.exception.IbResponderValidationErrorCode;
import se.inera.intyg.intygsbestallning.common.exception.IbResponderValidationException;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.persistence.model.Intyg;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.status.UtredningFas;
import se.inera.intyg.intygsbestallning.persistence.model.status.UtredningStatus;
import se.inera.intyg.intygsbestallning.persistence.model.status.UtredningStatusResolver;
import se.inera.intyg.intygsbestallning.service.handelse.HandelseUtil;
import se.inera.intyg.intygsbestallning.service.pdl.LogService;
import se.inera.intyg.intygsbestallning.service.pdl.dto.PdlLogType;
import se.inera.intyg.intygsbestallning.service.pdl.dto.UtredningPdlLoggable;
import se.inera.intyg.intygsbestallning.service.utredning.BaseUtredningService;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.utlatande.SendUtlatandeRequest;
import se.inera.intyg.intygsbestallning.web.responder.dto.ReportUtlatandeMottagetRequest;

@Service
@Transactional
public class UtlatandeServiceImpl extends BaseUtredningService implements UtlatandeService {

    public static final Logger LOG = LoggerFactory.getLogger(UtlatandeServiceImpl.class);

    @Autowired
    private LogService logService;

    private static Predicate<Intyg> isNotKomplettering() {
        return i -> !i.isKomplettering();
    }

    private static Predicate<Utredning> isKorrektStatus() {
        return utr -> UtredningStatus.UTLATANDE_SKICKAT == UtredningStatusResolver.resolveStaticStatus(utr);
    }

    @Override
    public UtredningStatus sendUtlatande(Long utredningId, SendUtlatandeRequest request) {

        LocalDateTime utlatandeSentDate = parseDate(request).atStartOfDay();

        Utredning utredning = utredningRepository.findById(utredningId).orElseThrow(
                () -> new IbNotFoundException("Utredning with id '" + utredningId + "' does not exist."));
        /*
         * Visas endast i fas Utredning
         * Inaktiverad i utredningsstatus Utlåtande skickat och Utlåtande mottaget
         */
        UtredningStatus status = UtredningStatusResolver.resolveStaticStatus(utredning);
        if (status.getUtredningFas() != UtredningFas.UTREDNING || status == UtredningStatus.UTLATANDE_SKICKAT
                || status == UtredningStatus.UTLATANDE_MOTTAGET) {
            throw new IbServiceException(IbErrorCodeEnum.BAD_STATE, "Utredning with id '" + utredningId + "' is in an incorrect state.");
        }

        checkUserVardenhetTilldeladToBestallning(utredning);

        Intyg intyg = utredning.getIntygList().stream()
                .filter(isNotKomplettering())
                .collect(onlyElement());

        intyg.setSkickatDatum(utlatandeSentDate);

        logService.log(new UtredningPdlLoggable(utredning), PdlLogType.UTREDNING_UPPDATERAD);

        IbUser user = userService.getUser();
        utredning.getHandelseList().add(HandelseUtil.createUtlatandeSkickat(user.getNamn(), utlatandeSentDate));

        utredningRepository.saveUtredning(utredning);

        return UtredningStatusResolver.resolveStaticStatus(utredning);
    }

    @Override
    public void reportUtlatandeMottaget(final ReportUtlatandeMottagetRequest request) {

        Optional<Utredning> optionalUtredning = utredningRepository.findById(request.getUtredningId());

        optionalUtredning
                .orElseThrow(() -> new IbResponderValidationException(IbResponderValidationErrorCode.TA_FEL06, request.getUtredningId()));

        optionalUtredning.filter(isKorrektStatus())
                .orElseThrow(() -> new IbServiceException(
                        IbErrorCodeEnum.BAD_STATE, "Utredning with id '" + request.getUtredningId() + "' is in an incorrect state."));

        Intyg intyg = optionalUtredning.get().getIntygList().stream()
                .filter(isNotKomplettering())
                .collect(onlyElement());
        intyg.setMottagetDatum(request.getMottagetDatum());
        intyg.setSistaDatumKompletteringsbegaran(request.getSistaKompletteringsDatum());

        optionalUtredning.get().getHandelseList().add(HandelseUtil.createUtlatandeMottaget(request.getMottagetDatum()));
        utredningRepository.saveUtredning(optionalUtredning.get());
    }

    private LocalDate parseDate(SendUtlatandeRequest request) {
        if (request.getUtlatandeSentDate() == null) {
            LOG.error("SendUtlatandeRequest utlatandeSentDate is null");
            throw new IbServiceException(IbErrorCodeEnum.BAD_REQUEST, "utlatandeSentDate is missing");
        }
        try {
            return LocalDate.parse(request.getUtlatandeSentDate(), DateTimeFormatter.ISO_DATE);
        } catch (DateTimeParseException e) {
            LOG.error("Unable to parse utlatandeSentDate, message: {}", e.getMessage());
            throw new IbServiceException(IbErrorCodeEnum.BAD_REQUEST, "Unable to parse utlatandeSentDate. "
                    + "Valid format is yyyy-MM-dd");
        }
    }
}
