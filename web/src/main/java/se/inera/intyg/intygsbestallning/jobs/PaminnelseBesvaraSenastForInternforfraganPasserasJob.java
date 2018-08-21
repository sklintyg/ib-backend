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

import static se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringMottagarTyp.VARDENHET;
import static se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringTyp.PAMINNELSE_SLUTDATUM_INTERNFORFRAGAN_PASSERAS;

import com.google.common.collect.Sets;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.lang.invoke.MethodHandles;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import se.inera.intyg.infra.monitoring.annotation.PrometheusTimeMethod;
import se.inera.intyg.intygsbestallning.persistence.model.ExternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.status.InternForfraganFas;
import se.inera.intyg.intygsbestallning.persistence.model.status.InternForfraganStatus;
import se.inera.intyg.intygsbestallning.persistence.repository.UtredningRepository;
import se.inera.intyg.intygsbestallning.service.notifiering.send.NotifieringSendService;
import se.inera.intyg.intygsbestallning.service.util.BusinessDaysBean;

@Component
public class PaminnelseBesvaraSenastForInternforfraganPasserasJob {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final long LOCK_AT_LEAST = 1000 * 15L;
    private static final long LOCK_AT_MOST = 1000 * 30L;
    private static final String JOB_NAME = "paminnelseBesvaraSenastForInternforfraganPasserasJob";

    @Autowired
    private BusinessDaysBean businessDaysBean;

    @Autowired
    private NotifieringSendService service;

    @Autowired
    private UtredningRepository utredningRepository;

    @Value("${ib.utredning.paminnelse.arbetsdagar}")
    private Integer arbetsdagar;

    @Scheduled(cron = "${job.paminnelse.besvarassenast.internforfragan.passeras.cron}")
    @SchedulerLock(name = JOB_NAME, lockAtLeastFor = LOCK_AT_LEAST, lockAtMostFor = LOCK_AT_MOST)
    @PrometheusTimeMethod
    @Transactional
    public void executeJob() {
        LOG.info(MessageFormat.format("Starting: {0} from Scheduled Cron Expression", JOB_NAME));

        final LocalDateTime paminnelseDatum = businessDaysBean.addBusinessDays(LocalDate.now(), arbetsdagar - 1).atStartOfDay();
        final Set<InternForfraganStatus> statusarInFasAktiv = Sets.immutableEnumSet(Stream.of(InternForfraganStatus.values())
                .filter(status -> status.getInternForfraganFas() != InternForfraganFas.AVSLUTAD)
                .collect(Collectors.toSet()));

        utredningRepository.findNonNotifiedInternforfraganSlutDatumBefore(
                paminnelseDatum, statusarInFasAktiv, PAMINNELSE_SLUTDATUM_INTERNFORFRAGAN_PASSERAS, VARDENHET)
                .forEach(utredning -> utredning.getExternForfragan()
                        .map(Stream::of).orElseGet(Stream::empty)
                        .map(ExternForfragan::getInternForfraganList)
                        .flatMap(Collection::stream)
                        .forEach(internForfragan -> service.notifieraVardenhetPaminnelseSvaraInternforfragan(utredning, internForfragan)));
    }
}
