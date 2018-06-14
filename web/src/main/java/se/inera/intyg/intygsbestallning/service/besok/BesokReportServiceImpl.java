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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import se.inera.intyg.intygsbestallning.common.exception.IbErrorCodeEnum;
import se.inera.intyg.intygsbestallning.common.exception.IbExternalServiceException;
import se.inera.intyg.intygsbestallning.common.exception.IbNotFoundException;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.persistence.model.Besok;
import se.inera.intyg.intygsbestallning.persistence.model.Handelse;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.type.BesokStatusTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.TolkStatusTyp;
import se.inera.intyg.intygsbestallning.service.handelse.HandelseUtil;
import se.inera.intyg.intygsbestallning.service.pdl.LogService;
import se.inera.intyg.intygsbestallning.service.pdl.dto.PdlLogType;
import se.inera.intyg.intygsbestallning.service.pdl.dto.UtredningPdlLoggable;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.besok.RedovisaBesokRequest;

import java.lang.invoke.MethodHandles;
import java.text.MessageFormat;

@Service
public class BesokReportServiceImpl extends BaseBesokService implements BesokReportService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private LogService logService;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void redovisaBesokInNewTransaction(Utredning utredning, RedovisaBesokRequest.RedovisaBesokListItem besokRequest) {
        try {
            Besok besok = utredning.getBesokList().stream()
                    .filter(b -> b.getId().equals(besokRequest.getBesokId()))
                    .findAny()
                    .orElseThrow(() -> new IbNotFoundException(MessageFormat.format(
                            "Could not find besok {0} in utredning {1}", besokRequest.getBesokId(), utredning.getUtredningId()),
                            besokRequest.getBesokId()));

            if (besokRequest.isGenomfort()) {
                besok.setBesokStatus(BesokStatusTyp.AVSLUTAD_VARDKONTAKT);
            }

            besok.setTolkStatus(besokRequest.getTolkStatus());

            Handelse besokHandelse = HandelseUtil.createBesokRedovisat(besok, userService.getUser().getNamn());
            utredning.getHandelseList().add(besokHandelse);
            besok.getHandelseList().add(besokHandelse);
            utredningRepository.saveUtredning(utredning);

            logService.log(new UtredningPdlLoggable(utredning), PdlLogType.BESOK_REDOVISAT);

            reportBesok(utredning, besok);
        } catch (IbExternalServiceException e) {
            LOG.error("IbExternalServiceException occured", e);
            e.setErrorEntityId(besokRequest.getBesokId());
            throw e;
        } catch (IbNotFoundException e) {
            LOG.error("IbNotFoundException occured", e);
            e.setErrorEntityId(besokRequest.getBesokId());
            throw e;
        } catch (RuntimeException e) {
            LOG.error("RuntimeException occured", e);
            throw new IbServiceException(IbErrorCodeEnum.UNKNOWN_INTERNAL_PROBLEM, e.getMessage(), besokRequest.getBesokId());
        }
    }

}
