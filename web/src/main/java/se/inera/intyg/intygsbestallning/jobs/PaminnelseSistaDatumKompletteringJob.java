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

import static se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringTyp.PAMINNELSEDATUM_KOMPLETTERING_PASSERAS;


import java.lang.invoke.MethodHandles;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import net.javacrumbs.shedlock.core.SchedulerLock;
import se.inera.intyg.infra.monitoring.annotation.PrometheusTimeMethod;
import se.inera.intyg.intygsbestallning.persistence.model.Intyg;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringMottagarTyp;
import se.inera.intyg.intygsbestallning.persistence.repository.UtredningRepository;
import se.inera.intyg.intygsbestallning.service.notifiering.send.NotifieringSendService;
import se.inera.intyg.intygsbestallning.service.util.BusinessDaysBean;

@Component
public class PaminnelseSistaDatumKompletteringJob {

    private static final long LOCK_AT_LEAST = 1000 * 15L;
    private static final long LOCK_AT_MOST = 1000 * 30L;
    private static final String JOB_NAME = "paminnelseSistaDatumKompletteringsbegaranJob";

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private BusinessDaysBean businessDaysBean;

    @Autowired
    private UtredningRepository utredningRepository;

    @Autowired
    private NotifieringSendService notifieringSendService;

    @Value("${ib.utredning.paminnelse.arbetsdagar}")
    private Integer paminnelseArbetsdagar;

    @Scheduled(cron = "${job.paminnelse.sista.datum.kompletteringsbegaran.cron}")
    @SchedulerLock(name = JOB_NAME, lockAtLeastFor = LOCK_AT_LEAST, lockAtMostFor = LOCK_AT_MOST)
    @PrometheusTimeMethod
    @Transactional
    public void executeJob() {
        LOG.info(MessageFormat.format("Starting: {0} from Scheduled Cron Expression", JOB_NAME));

        final LocalDateTime paminnelseDatum = businessDaysBean.addBusinessDays(LocalDate.now(), paminnelseArbetsdagar).atStartOfDay();
        final List<Object[]> utredningList = utredningRepository.findNonNotifiedSistadatumKompletteringBefore(
                paminnelseDatum, PAMINNELSEDATUM_KOMPLETTERING_PASSERAS, NotifieringMottagarTyp.VARDENHET);

        utredningList.forEach(utredningIntyg -> {
            Utredning utredning = (Utredning) utredningIntyg[0];
            Intyg intyg = (Intyg) utredningIntyg[1];
            LOG.debug(MessageFormat.format("Starting {0} for utredning with id {1}", JOB_NAME, utredning.getUtredningId()));
            notifieringSendService.notifieraVardenhetPaminnelseSlutdatumKomplettering(utredning, intyg);
        });
    }
}
