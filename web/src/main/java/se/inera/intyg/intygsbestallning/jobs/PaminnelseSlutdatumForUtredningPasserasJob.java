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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import se.inera.intyg.intygsbestallning.persistence.model.Notifiering;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.repository.UtredningRepository;
import se.inera.intyg.intygsbestallning.service.notification.MailNotificationService;
import se.inera.intyg.intygsbestallning.service.stateresolver.UtredningFas;
import se.inera.intyg.intygsbestallning.service.stateresolver.UtredningStatus;
import se.inera.intyg.intygsbestallning.service.stateresolver.UtredningStatusResolver;
import se.inera.intyg.intygsbestallning.service.util.BusinessDaysBean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringTyp.PAMINNELSE_SLUTDATUM_UTREDNING_PASSERAS;

@Component
@Transactional
public class PaminnelseSlutdatumForUtredningPasserasJob {

    private static final Logger LOG = LoggerFactory.getLogger(PaminnelseSlutdatumForUtredningPasserasJob.class);

    private static final long LOCK_AT_MOST = 20000L;
    private static final String JOB_NAME = "paminnelseSlutdatumForUtredningPasserasJob";

    @Autowired
    private UtredningRepository utredningRepository;

    @Autowired
    private MailNotificationService mailNotificationService;

    @Autowired
    private BusinessDaysBean businessDaysBean;

    private UtredningStatusResolver utredningStatusResolver = new UtredningStatusResolver();

    @Value("${ib.utredning.paminnelse.arbetsdagar}")
    private Integer paminnelseArbetsdagar;

    @Scheduled(cron = "${job.paminnelse.slutdatum.utredning.passeras.cron}")
    @SchedulerLock(name = JOB_NAME, lockAtMostFor = LOCK_AT_MOST)
    public void executeJob() {

        // Calculate last date
        LocalDate notifyIfBefore = businessDaysBean.addBusinessDays(LocalDate.now(), paminnelseArbetsdagar);

        // Find all Utredningar having slutDatum pretty soon that has not gotten the specified notification.
        List<Utredning> utredningList = utredningRepository.findNonNotifiedIntygSlutDatumBetween(
                LocalDate.now().atStartOfDay(), notifyIfBefore.atStartOfDay(), PAMINNELSE_SLUTDATUM_UTREDNING_PASSERAS);

        for (Utredning utredning : utredningList) {
            UtredningStatus utredningStatus = utredningStatusResolver.resolveStatus(utredning);

            if (utredningStatus.getUtredningFas() == UtredningFas.REDOVISA_TOLK
                    || utredningStatus.getUtredningFas() == UtredningFas.AVSLUTAD) {
                continue;
            }

            mailNotificationService.notifySlutdatumPaVagPasseras(utredning);
            utredning.getNotifieringList().add(Notifiering.NotifieringBuilder.aNotifiering()
                    .withNotifieringSkickad(LocalDateTime.now())
                    .withNotifieringTyp(PAMINNELSE_SLUTDATUM_UTREDNING_PASSERAS)
                    .build());
            utredningRepository.save(utredning);
            LOG.info("Sent notification {} for utredning {}.", PAMINNELSE_SLUTDATUM_UTREDNING_PASSERAS, utredning.getUtredningId());
        }
    }
}
