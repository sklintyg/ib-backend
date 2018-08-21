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

import static se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringTyp.SLUTDATUM_KOMPLETTERING_PASSERAT;

import net.javacrumbs.shedlock.core.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import se.inera.intyg.infra.monitoring.annotation.PrometheusTimeMethod;
import se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringMottagarTyp;
import se.inera.intyg.intygsbestallning.persistence.repository.UtredningRepository;
import se.inera.intyg.intygsbestallning.service.notifiering.send.NotifieringSendService;

@Component
public class SlutdatumKompletteringPasseratJob {

    private static final Logger LOG = LoggerFactory.getLogger(SlutdatumKompletteringPasseratJob.class);

    private static final long LOCK_AT_LEAST = 1000 * 15L;
    private static final long LOCK_AT_MOST = 1000 * 30;

    private static final String JOB_NAME = "slutdatumKompletteringPasseratJob";

    @Autowired
    private UtredningRepository utredningRepository;

    @Autowired
    private NotifieringSendService notifieringSendService;

    @Scheduled(cron = "${job.slutdatum.komplettering.passerad.cron}")
    @SchedulerLock(name = JOB_NAME, lockAtLeastFor = LOCK_AT_LEAST, lockAtMostFor = LOCK_AT_MOST)
    @PrometheusTimeMethod
    @Transactional
    public void executeJob() {

        // To vardenhet
        utredningRepository.findNonNotifiedSistadatumKompletteringBefore(
                LocalDate.now().atStartOfDay(), SLUTDATUM_KOMPLETTERING_PASSERAT, NotifieringMottagarTyp.VARDENHET).forEach(object ->
                notifieringSendService.notifieraVardenhetSlutdatumPasseratKomplettering(object.getUtredning(), object.getIntyg()));

        // To landsting
        utredningRepository.findNonNotifiedSistadatumKompletteringBefore(
                LocalDate.now().atStartOfDay(), SLUTDATUM_KOMPLETTERING_PASSERAT, NotifieringMottagarTyp.LANDSTING).forEach(object ->
                notifieringSendService.notifieraLandstingSlutdatumPasseratKomplettering(object.getUtredning(), object.getIntyg()));
    }
}
