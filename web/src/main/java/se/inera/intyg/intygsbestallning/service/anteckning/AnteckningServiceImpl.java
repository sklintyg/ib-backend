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
package se.inera.intyg.intygsbestallning.service.anteckning;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.inera.intyg.intygsbestallning.common.exception.IbNotFoundException;
import se.inera.intyg.intygsbestallning.persistence.model.Anteckning;
import se.inera.intyg.intygsbestallning.persistence.model.Handelse;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.service.handelse.HandelseUtil;
import se.inera.intyg.intygsbestallning.service.pdl.LogService;
import se.inera.intyg.intygsbestallning.service.pdl.dto.PdlLogType;
import se.inera.intyg.intygsbestallning.service.pdl.dto.UtredningPdlLoggable;
import se.inera.intyg.intygsbestallning.service.utredning.BaseUtredningService;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.anteckning.CreateAnteckningRequest;

import java.time.LocalDateTime;

import static se.inera.intyg.intygsbestallning.persistence.model.Anteckning.AnteckningBuilder.anAnteckning;

@Service
public class AnteckningServiceImpl extends BaseUtredningService implements AnteckningService {

    @Autowired
    private LogService logService;

    @Override
    @Transactional
    public void createAnteckning(Long utredningId, CreateAnteckningRequest request) {

        request.validate();

        final Utredning utredning = utredningRepository.findById(utredningId)
                .orElseThrow(() -> new IbNotFoundException("Utredning with id '" + utredningId + "' does not exist."));

        checkUserVardenhetTilldeladToBestallning(utredning);

        Anteckning anteckning = anAnteckning()
                .withAnvandare(userService.getUser().getNamn())
                .withSkapat(LocalDateTime.now())
                .withText(request.getText())
                .withVardenhetHsaId(userService.getUser().getCurrentlyLoggedInAt().getId())
                .build();

        utredning.getAnteckningList().add(anteckning);

        Handelse handelse = HandelseUtil.createAnteckning(request.getText(), userService.getUser().getNamn());
        utredning.getHandelseList().add(handelse);

        utredningRepository.saveUtredning(utredning);

        logService.log(new UtredningPdlLoggable(utredning), PdlLogType.ANTECKNING_SKAPAD);
    }

}
