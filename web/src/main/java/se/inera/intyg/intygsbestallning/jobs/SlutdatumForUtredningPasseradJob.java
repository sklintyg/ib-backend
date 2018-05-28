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

import net.javacrumbs.shedlock.core.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import se.inera.intyg.intygsbestallning.persistence.model.Notifiering;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.repository.NotifieringRepository;
import se.inera.intyg.intygsbestallning.persistence.repository.UtredningRepository;
import se.inera.intyg.intygsbestallning.service.notification.MailNotificationService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringTyp.SLUTDATUM_UTREDNING_PASSERAT;

@Component
@Transactional
public class SlutdatumForUtredningPasseradJob {

    private static final Logger LOG = LoggerFactory.getLogger(SlutdatumForUtredningPasseradJob.class);

    private static final long LOCK_AT_MOST = 20000L;
    private static final String JOB_NAME = "slutdatumForUtredningPasseradJob";

    @Autowired
    private UtredningRepository utredningRepository;

    @Autowired
    private NotifieringRepository notifieringRepository;

    @Autowired
    private MailNotificationService mailNotificationService;

    @Scheduled(cron = "${job.slutdatum.utredning.passerad.cron}")
    @SchedulerLock(name = JOB_NAME, lockAtMostFor = LOCK_AT_MOST)
    public void executeJob() {
        LOG.info("START - executeJob");
        List<Utredning> utredningList = utredningRepository.findAllNonArchivedWithIntygSlutDatumBefore(LocalDate.now().atStartOfDay());

        for (Utredning utredning : utredningList) {

            // Check so not already notified
            if (notifieringRepository.isNotified(utredning.getUtredningId(), SLUTDATUM_UTREDNING_PASSERAT) > 0) {
                continue;
            }

            mailNotificationService.notifySlutDatumPasserat(utredning);
            notifieringRepository.save(Notifiering.NotifieringBuilder.aNotifiering()
                    .withNotifieringSkickad(LocalDateTime.now())
                    .withNotifieringTyp(SLUTDATUM_UTREDNING_PASSERAT)
                    .withUtredningId(utredning.getUtredningId())
                    .build());

            LOG.info("Sent notification {} for utredning {}.", SLUTDATUM_UTREDNING_PASSERAT, utredning.getUtredningId());
        }

        LOG.info("END - executeJob");
    }
}
