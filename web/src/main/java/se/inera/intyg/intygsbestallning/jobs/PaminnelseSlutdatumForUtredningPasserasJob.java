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
package se.inera.intyg.intygsbestallning.jobs;

import java.time.LocalDate;
import java.util.List;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import se.inera.intyg.infra.monitoring.annotation.PrometheusTimeMethod;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.status.UtredningFas;
import se.inera.intyg.intygsbestallning.persistence.model.status.UtredningStatus;
import se.inera.intyg.intygsbestallning.persistence.model.status.UtredningStatusResolver;
import se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringMottagarTyp;
import se.inera.intyg.intygsbestallning.persistence.repository.UtredningRepository;
import se.inera.intyg.intygsbestallning.service.notifiering.send.NotifieringSendService;
import se.inera.intyg.intygsbestallning.service.util.BusinessDaysBean;


import static se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringTyp.PAMINNELSE_SLUTDATUM_UTREDNING_PASSERAS;

@Component
public class PaminnelseSlutdatumForUtredningPasserasJob {

    private static final Logger LOG = LoggerFactory.getLogger(PaminnelseSlutdatumForUtredningPasserasJob.class);

    private static final long LOCK_AT_LEAST = 1000 * 15L;
    private static final long LOCK_AT_MOST = 1000 * 30L;
    private static final String JOB_NAME = "paminnelseSlutdatumForUtredningPasserasJob";

    @Autowired
    private UtredningRepository utredningRepository;

    @Autowired
    private NotifieringSendService notifieringSendService;

    @Autowired
    private BusinessDaysBean businessDaysBean;

    private UtredningStatusResolver utredningStatusResolver = new UtredningStatusResolver();

    @Value("${ib.utredning.paminnelse.arbetsdagar}")
    private Integer paminnelseArbetsdagar;

    @Scheduled(cron = "${job.paminnelse.slutdatum.utredning.passeras.cron}")
    @SchedulerLock(name = JOB_NAME, lockAtLeastFor = LOCK_AT_LEAST, lockAtMostFor = LOCK_AT_MOST)
    @PrometheusTimeMethod
    public void executeJob() {

        // Calculate last date
        LocalDate notifyIfBefore = businessDaysBean.addBusinessDays(LocalDate.now(), paminnelseArbetsdagar);

        // Find all Utredningar having slutDatum pretty soon that has not gotten the specified notification.
        List<Utredning> utredningList = utredningRepository.findNonNotifiedIntygSlutDatumBetween(
                LocalDate.now().atStartOfDay(), notifyIfBefore.atStartOfDay(), PAMINNELSE_SLUTDATUM_UTREDNING_PASSERAS,
                NotifieringMottagarTyp.VARDENHET);

        for (Utredning utredning : utredningList) {
            UtredningStatus utredningStatus = utredningStatusResolver.resolveStatus(utredning);

            if (utredningStatus.getUtredningFas() == UtredningFas.REDOVISA_BESOK
                    || utredningStatus.getUtredningFas() == UtredningFas.AVSLUTAD) {
                continue;
            }
            notifieringSendService.notifieraVardenhetPaminnelseSlutdatumUtredning(utredning);
        }
    }
}
