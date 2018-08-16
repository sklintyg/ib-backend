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
import se.inera.intyg.infra.monitoring.annotation.PrometheusTimeMethod;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringMottagarTyp;
import se.inera.intyg.intygsbestallning.persistence.repository.UtredningRepository;
import se.inera.intyg.intygsbestallning.service.notifiering.send.NotifieringSendService;
import se.inera.intyg.intygsbestallning.service.util.BusinessDaysBean;

import java.time.LocalDate;
import java.util.List;

import static se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringTyp.PAMINNELSE_SLUTDATUM_EXTERNFORFRAGAN_PASSERAS;

@Component
public class PaminnelseBesvarasSenastForExternforfraganPasserasJob {

    private static final Logger LOG = LoggerFactory.getLogger(PaminnelseBesvarasSenastForExternforfraganPasserasJob.class);

    private static final long LOCK_AT_LEAST = 1000 * 15L;
    private static final long LOCK_AT_MOST = 1000 * 30L;
    private static final String JOB_NAME = "paminnelseBesvarasSenastForExternforfraganPasserasJob";

    @Autowired
    private UtredningRepository utredningRepository;

    @Autowired
    private NotifieringSendService notifieringSendService;

    @Autowired
    private BusinessDaysBean businessDaysBean;

    @Value("${ib.externforfragan.paminnelse.arbetsdagar}")
    private Integer paminnelseArbetsdagarSamordnare;

    @Scheduled(cron = "${job.paminnelse.besvarassenast.externforfragan.passeras.cron}")
    @SchedulerLock(name = JOB_NAME, lockAtLeastFor = LOCK_AT_LEAST, lockAtMostFor = LOCK_AT_MOST)
    @PrometheusTimeMethod
    @Transactional
    public void executeJob() {

        LocalDate paminnelseDatum = businessDaysBean.addBusinessDays(LocalDate.now(), paminnelseArbetsdagarSamordnare - 1);

        List<Utredning> utredningList = utredningRepository.findNonNotifiedExternforfraganBesvarasSenastBetween(
                LocalDate.now().atStartOfDay(), paminnelseDatum.atStartOfDay(), PAMINNELSE_SLUTDATUM_EXTERNFORFRAGAN_PASSERAS,
                NotifieringMottagarTyp.LANDSTING);

        for (Utredning utredning : utredningList) {
            if (utredning.getExternForfragan().get().getInternForfraganList().stream()
                    .noneMatch(internforfragan -> internforfragan.getTilldeladDatum() != null)) {
                notifieringSendService.notifieraLandstingPaminnelseSvaraExternforfragan(utredning);
            }
        }

    }
}
