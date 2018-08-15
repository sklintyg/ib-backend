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

import static java.util.Objects.nonNull;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import se.inera.intyg.intygsbestallning.persistence.model.ExternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.InternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.status.Actor;
import se.inera.intyg.intygsbestallning.persistence.model.status.InternForfraganFas;
import se.inera.intyg.intygsbestallning.service.util.BusinessDaysBean;

@Component
public class InternForfraganListItemFactory {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE;


    @Value("${ib.besvara.forfragan.arbetsdagar}")
    private int besvaraForfraganArbetsdagar;

    @Value("${ib.afu.utredning.arbetsdagar}")
    private int afuUtredningArbetsdagar;

    @Value("${ib.postgang.arbetsdagar}")
    private int postgangArbetsdagar;

    @Autowired
    private BusinessDaysBean businessDays;

    public InternForfraganListItem from(Utredning utredning, String vardenhetId) {

        InternForfragan internForfragan = utredning.getExternForfragan()
                .map(ExternForfragan::getInternForfraganList).orElse(Lists.newArrayList()).stream()
                .filter(i -> Objects.equals(i.getVardenhetHsaId(), vardenhetId))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);

        final boolean pavagPasseras = resolveBesvarasSenastPaVagPasseras(internForfragan.getBesvarasSenastDatum(), businessDays);
        final boolean passerat = resolveBesvaraSenastPasserat(internForfragan);

        return InternForfraganListItem.ForfraganListItemBuilder.aForfraganListItem()
                .withBesvarasSenastDatum(nonNull(internForfragan.getBesvarasSenastDatum())
                        ? internForfragan.getBesvarasSenastDatum().format(FORMATTER)
                        : null)
                .withBesvarasSenastDatumPaVagPasseras(pavagPasseras && !passerat)
                .withBesvarasSenastDatumPasserat(passerat)
                .withInkomDatum(nonNull(internForfragan.getSkapadDatum())
                        ? internForfragan.getSkapadDatum().format(FORMATTER)
                        : null)
                .withPlaneringsDatum(resolvePlaneringsDatum(internForfragan, businessDays))
                .withForfraganId(internForfragan.getId())
                .withStatus(internForfragan.getStatus())
                .withUtredningsId(utredning.getUtredningId())
                .withUtredningsTyp(utredning.getUtredningsTyp())
                .withVardgivareHsaId(utredning.getExternForfragan().map(ExternForfragan::getLandstingHsaId).orElse(null))
                .withVardgivareNamn(utredning.getExternForfragan().map(ExternForfragan::getLandstingHsaId).orElse(null))
                .withStatus(internForfragan.getStatus())
                .withKraverAtgard(internForfragan.getStatus().getNextActor() == Actor.VARDADMIN)
                .withKommentar(internForfragan.getKommentar())
                .build();
    }

    private String resolvePlaneringsDatum(final InternForfragan internForfragan, final BusinessDaysBean businessDays) {

        // 180509 Planeringsdatum ska visas som blankt när internförfrågan är avslutad
        if (internForfragan.getStatus().getInternForfraganFas() == InternForfraganFas.AVSLUTAD) {
            return null;
        }

        LocalDate planeringsDatum = LocalDate.now();

        // semesterdagar ska ej hoppas över för postgång
        // semesterdagar ska hoppas över för utredningsdagar
        planeringsDatum = businessDays.addBusinessDays(planeringsDatum, postgangArbetsdagar, false);
        planeringsDatum = businessDays.addBusinessDays(planeringsDatum, afuUtredningArbetsdagar, true);
        planeringsDatum = businessDays.addBusinessDays(planeringsDatum, postgangArbetsdagar, false);

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
