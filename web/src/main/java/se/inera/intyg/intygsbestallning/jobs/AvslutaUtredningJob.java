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

import static se.inera.intyg.intygsbestallning.persistence.model.status.UtredningStatus.KOMPLETTERING_MOTTAGEN;
import static se.inera.intyg.intygsbestallning.persistence.model.status.UtredningStatus.UTLATANDE_MOTTAGET;

import com.google.common.collect.ImmutableList;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.lang.invoke.MethodHandles;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.springframework.transaction.annotation.Transactional;
import se.inera.intyg.infra.monitoring.annotation.PrometheusTimeMethod;
import se.inera.intyg.intygsbestallning.persistence.model.Intyg;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.status.UtredningStatus;
import se.inera.intyg.intygsbestallning.persistence.model.type.TolkStatusTyp;
import se.inera.intyg.intygsbestallning.persistence.repository.UtredningRepository;
import se.inera.intyg.intygsbestallning.service.stateresolver.BesokStatus;
import se.inera.intyg.intygsbestallning.service.stateresolver.BesokStatusResolver;
import se.inera.intyg.intygsbestallning.service.utredning.UtredningService;
import se.inera.intyg.intygsbestallning.service.utredning.dto.AvslutaUtredningRequest;

@Component
public class AvslutaUtredningJob {

    private static final long LOCK_AT_LEAST = 1000 * 15L;
    private static final long LOCK_AT_MOST = 1000 * 30L;
    private static final String JOB_NAME = "avslutaUtredningJob";

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private UtredningRepository utredningRepository;

    @Autowired
    private UtredningService utredningService;

    @Scheduled(cron = "${job.avsluta.utredning.cron}")
    @SchedulerLock(name = JOB_NAME, lockAtLeastFor = LOCK_AT_LEAST, lockAtMostFor = LOCK_AT_MOST)
    @PrometheusTimeMethod
    @Transactional
    public void executeJob() {

        LOG.info(MessageFormat.format("Starting: {0} from Scheduled Cron Expression", JOB_NAME));

        final LocalDateTime tidpunkt = LocalDate.now().atStartOfDay();

        utredningRepository.findSistaDatumKompletteringsBegaranBefore(tidpunkt).stream()
                .filter(isKorrektStatus())
                .filter(isSistaDatumKompletteringsbegaranPasserat(tidpunkt))
                .forEach(avslutaUtredningOrRedovisaBesok);
    }

    private Predicate<Utredning> isKorrektStatus() {
        final List<UtredningStatus> godkandaStatusar = ImmutableList.of(UTLATANDE_MOTTAGET, KOMPLETTERING_MOTTAGEN);
        return utr -> godkandaStatusar.contains(utr.getStatus());
    }

    private Predicate<Utredning> isSistaDatumKompletteringsbegaranPasserat(LocalDateTime tidpunkt) {
        return utredning -> {
            Optional<LocalDateTime> sistaDatumKompletteringsbegaran = utredning.getIntygList().stream()
                    .filter(intyg -> intyg.getSistaDatumKompletteringsbegaran() != null)
                    .max(Comparator.comparing(Intyg::getSistaDatumKompletteringsbegaran))
                    .map(Intyg::getSistaDatumKompletteringsbegaran);
            if (sistaDatumKompletteringsbegaran.isPresent()) {
                return sistaDatumKompletteringsbegaran.get().isBefore(tidpunkt);
            }
            return false;
        };
    }


    private Predicate<Utredning> isQualifiedForAvslutaUtredning() {
        return utr -> utr.getBesokList().stream()
                .noneMatch(besok -> (BesokStatusResolver.resolveStaticStatus(besok) == BesokStatus.BOKAT)
                        || (BesokStatusResolver.resolveStaticStatus(besok) == BesokStatus.OMBOKAT));
    }

    private Predicate<Utredning> isQualifiedForRedovisaBesok() {
        return utr -> utr.getBesokList().stream()
                .anyMatch(besok -> besok.getTolkStatus() == TolkStatusTyp.BOKAD);
    }

    private final Consumer<Utredning> avslutaUtredningOrRedovisaBesok = utr -> {
        Optional.of(utr)
                .filter(isQualifiedForAvslutaUtredning())
                .ifPresent(utredning -> utredningService.avslutaUtredning(AvslutaUtredningRequest.from(utr.getUtredningId().toString())));

        Optional.of(utr)
                .filter(isQualifiedForRedovisaBesok())
                .ifPresent(utredningService::updateStatusToRedovisaBesok);
    };
}
