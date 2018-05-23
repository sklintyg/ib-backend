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
package se.inera.intyg.intygsbestallning.web.controller.api.dto.forfragan;

import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import se.inera.intyg.intygsbestallning.persistence.model.Bestallning;
import se.inera.intyg.intygsbestallning.persistence.model.InternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.service.stateresolver.Actor;
import se.inera.intyg.intygsbestallning.service.stateresolver.InternForfraganStateResolver;
import se.inera.intyg.intygsbestallning.service.stateresolver.InternForfraganStatus;
import se.inera.intyg.intygsbestallning.service.util.BusinessDaysBean;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.nonNull;

@Component
public class InternForfraganListItemFactory {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE;

    private static final int DEFAULT_BESVARA_FORFRAGAN_ARBETSDAGAR = 2;
    private static final int DEFAULT_AFU_UTREDNING_ARBETSDAGAR = 25;
    private static final int DEFAULT_POSTGANG_ARBETSDAGAR = 3;

    @Value("${ib.besvara.forfragan.arbetsdagar}")
    private String ibBesvaraForfraganArbetsdagar;

    @Value("${ib.afu.utredning.arbetsdagar}")
    private String ibAfuUtredningArbetsdagar;

    @Value("${ib.postgang.arbetsdagar}")
    private String ibPostgangArbetsdagar;

    private int besvaraForfraganArbetsdagar = DEFAULT_BESVARA_FORFRAGAN_ARBETSDAGAR;
    private int afuUtredningArbetsdagar = DEFAULT_AFU_UTREDNING_ARBETSDAGAR;
    private int postgangArbetsdagar = DEFAULT_POSTGANG_ARBETSDAGAR;

    private InternForfraganStateResolver internForfraganStateResolver = new InternForfraganStateResolver();

    private BusinessDaysBean businessDays;

    public InternForfraganListItemFactory(final BusinessDaysBean businessDays) {
        this.businessDays = businessDays;
    }

    @PostConstruct
    public void init() {
        if (!Strings.isNullOrEmpty(ibBesvaraForfraganArbetsdagar)) {
            besvaraForfraganArbetsdagar = Integer.parseInt(ibBesvaraForfraganArbetsdagar);
        }
        if (!Strings.isNullOrEmpty(ibAfuUtredningArbetsdagar)) {
            afuUtredningArbetsdagar = Integer.parseInt(ibAfuUtredningArbetsdagar);
        }
        if (!Strings.isNullOrEmpty(ibPostgangArbetsdagar)) {
            postgangArbetsdagar = Integer.parseInt(ibPostgangArbetsdagar);
        }
    }

    public InternForfraganListItem from(Utredning utredning, String vardenhetId) {

        InternForfragan internForfragan = utredning.getExternForfragan().getInternForfraganList()
                .stream()
                .filter(i -> Objects.equals(i.getVardenhetHsaId(), vardenhetId))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);

        InternForfraganStatus status = internForfraganStateResolver.resolveStatus(utredning, internForfragan);

        return InternForfraganListItem.ForfraganListItemBuilder.aForfraganListItem()
                .withBesvarasSenastDatum(nonNull(internForfragan.getBesvarasSenastDatum())
                        ? internForfragan.getBesvarasSenastDatum().format(FORMATTER)
                        : null)
                .withBesvarasSenastDatumPaVagPasseras(
                        resolveBesvarasSenastPaVagPasseras(internForfragan.getBesvarasSenastDatum(), businessDays))
                .withBesvarasSenastDatumPasserat(resolveBesvaraSenastPasserat(internForfragan))
                .withInkomDatum(nonNull(internForfragan.getSkapadDatum())
                        ? internForfragan.getSkapadDatum().format(FORMATTER)
                        : null)
                .withPlaneringsDatum(resolvePlaneringsDatum(utredning.getBestallning(), businessDays))
                .withStatus(status)
                .withUtredningsId(utredning.getUtredningId())
                .withUtredningsTyp(utredning.getUtredningsTyp().name())
                .withVardgivareHsaId(utredning.getExternForfragan().getLandstingHsaId())
                .withVardgivareNamn(utredning.getExternForfragan().getLandstingHsaId())
                .withKraverAtgard(status.getNextActor() == Actor.VARDADMIN)
                .build();
    }

    private String resolvePlaneringsDatum(Optional<Bestallning> bestallning, BusinessDaysBean businessDays) {

        // Om redan beställd, ska vi då utgå från orderdatumet istället?? Dvs planeringsdatum blir orderdatum + 31 arbetsdagar?
        if (bestallning.isPresent() && bestallning.get().getOrderDatum() != null) {
            return null;
        }

        LocalDate startDatum = LocalDate.now();
        LocalDate planeringsDatum = LocalDate.from(startDatum);
        int total = postgangArbetsdagar + afuUtredningArbetsdagar + postgangArbetsdagar;
        while (businessDays.daysBetween(startDatum, planeringsDatum) < total) {
            planeringsDatum = planeringsDatum.plusDays(1);
        }
        return planeringsDatum.format(FORMATTER);
    }

    private boolean resolveBesvaraSenastPasserat(InternForfragan internForfragan) {
        if (internForfragan.getBesvarasSenastDatum() == null) {
            return false;
        }
        return LocalDateTime.now().compareTo(internForfragan.getBesvarasSenastDatum()) > 0;
    }

    private boolean resolveBesvarasSenastPaVagPasseras(LocalDateTime besvarasSenastDatum, BusinessDaysBean businessDays) {
        if (besvarasSenastDatum == null) {
            return false;
        }

        // Om datumet redan passerats skall vi ej flagga.
        if (besvarasSenastDatum.toLocalDate().compareTo(LocalDate.now()) < 0) {
            return false;
        }
        return businessDays.daysBetween(LocalDate.now(), besvarasSenastDatum.toLocalDate(), false) < besvaraForfraganArbetsdagar;
    }
}
