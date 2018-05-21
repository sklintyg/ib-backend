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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.inera.intyg.intygsbestallning.common.exception.IbErrorCodeEnum;
import se.inera.intyg.intygsbestallning.common.exception.IbNotFoundException;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.persistence.model.Intyg;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.service.handelse.HandelseUtil;
import se.inera.intyg.intygsbestallning.service.stateresolver.UtredningStatus;
import se.inera.intyg.intygsbestallning.service.stateresolver.UtredningStatusResolver;
import se.inera.intyg.intygsbestallning.service.utredning.BaseUtredningService;
import se.inera.intyg.intygsbestallning.web.responder.dto.RegistreraUtlatandeMottagetRequest;

import java.util.Optional;
import java.util.function.Predicate;

import static com.google.common.collect.MoreCollectors.onlyElement;
import static java.lang.invoke.MethodHandles.lookup;

@Service
@Transactional
public class UtlatandeServiceImpl extends BaseUtredningService implements UtlatandeService {

    private final Logger log = LoggerFactory.getLogger(lookup().lookupClass());

    private static Predicate<Intyg> isNotKomplettering() {
        return i -> !i.isKomplettering();
    }

    private static Predicate<Utredning> isKorrektStatus() {
        return utr -> UtredningStatus.UTLATANDE_SKICKAT == UtredningStatusResolver.resolveStaticStatus(utr);
    }

    @Override
    public void registreraUtlatandeMottaget(final RegistreraUtlatandeMottagetRequest request) {

        Optional<Utredning> optionalUtredning = utredningRepository.findById(request.getUtredningId());

        optionalUtredning
                .orElseThrow(() -> new IbNotFoundException("Utredning with id '" + request.getUtredningId() + "' does not exist."));

        optionalUtredning.filter(isKorrektStatus())
                .orElseThrow(() -> new IbServiceException(
                        IbErrorCodeEnum.BAD_STATE, "Utredning with id '" + request.getUtredningId() + "' is in an incorrect state."));

        Intyg intyg = optionalUtredning.get().getIntygList().stream()
                .filter(isNotKomplettering())
                .collect(onlyElement());

        intyg.setMottagetDatum(request.getMottagetDatum());
        intyg.setSistaDatumKompletteringsbegaran(request.getSistaKompletteringsDatum());

        optionalUtredning.get().getHandelseList().add(HandelseUtil.createUtlatandeMottaget(request.getMottagetDatum()));
    }

}
