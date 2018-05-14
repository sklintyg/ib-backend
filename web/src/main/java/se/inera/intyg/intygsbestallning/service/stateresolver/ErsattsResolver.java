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
package se.inera.intyg.intygsbestallning.service.stateresolver;

import se.inera.intyg.intygsbestallning.persistence.model.Besok;
import se.inera.intyg.intygsbestallning.persistence.model.Intyg;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.type.AvvikelseOrsak;
import se.inera.intyg.intygsbestallning.persistence.model.type.EndReason;
import se.inera.intyg.intygsbestallning.persistence.model.type.KallelseFormTyp;
import se.inera.intyg.intygsbestallning.service.util.BusinessDaysBean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Optional;

/**
 * Encapsulates business logic regarding when a Utredning is eligible for ersattning.
 */
public final class ErsattsResolver {

    private static final int MAX_AVBOKNING_TIMMAR = 24;
    private static final int KALLELSE_ARBETSDAGAR = 5;
    private static final int POSTGANG_ARBETSDAGAR = 3;

    private ErsattsResolver() {

    }

    public static boolean resolveUtredningErsatts(Utredning utredning, BusinessDaysBean businessDays) {

        // Utredningen avslutad med orsak ”Jäv” eller ”Ingen beställning"
        if (utredning.getAvbrutenDatum() != null) {
            if (utredning.getAvbrutenAnledning() == EndReason.INGEN_BESTALLNING || utredning.getAvbrutenAnledning() == EndReason.JAV) {
                return false;
            }
        }

        // Slutdatum för utredningen passeras innan utlåtandet är mottaget av Försäkringskassan
        // Slutdatum för komplettering passerat innan kompletteringen är mottagen av Försäkringskassan
        // Nedanstående letar reda på det senaste sista-datumet (gäller både kompletteringar och intyg) och jämför sedan.
        Optional<Intyg> maxSistaDatumOptional = utredning.getIntygList().stream().max(Comparator.comparing(Intyg::getSistaDatum));
        if (maxSistaDatumOptional.isPresent()) {
            Intyg intyg = maxSistaDatumOptional.get();
            if (intyg.getMottagetDatum() == null || intyg.getSistaDatum().isBefore(intyg.getMottagetDatum())) {
                return false;
            }
        }

        // Det finns Inget besök i utredning som är ersättningsberättigat.
        boolean minstEttBesokErsatts = false;
        for (Besok besok : utredning.getBesokList()) {
            if (besok.getErsatts() && resolveBesokErsatts(utredning, besok, businessDays)) {
                minstEttBesokErsatts = true;
                break;
            }
        }

        if (!minstEttBesokErsatts) {
            return false;
        }

        // Om inget av ovanstående är sant ersätts utredningen
        return true;
    }

    public static boolean resolveBesokErsatts(Utredning utredning, Besok besok, BusinessDaysBean businessDays) {
        // Det finns en avvikelse där avvikelsetidpunkten som anges i avvikelsen ligger mer än MAX_AVBOKNING_TIMMAR timmar
        // innan besökets starttidpunkt. Gäller enbart avvikelser som är orsakade av patient.
        if (besok.getErsatts() && besok.getAvvikelse() != null
                && besok.getAvvikelse().getOrsakatAv() == AvvikelseOrsak.PATIENT
                && besok.getAvvikelse().getTidpunkt().isBefore(besok.getBesokStartTid().minusHours(MAX_AVBOKNING_TIMMAR))) {
            return false;
        }

        // Anrop till EndAssessment har inkommit mer än MAX_AVBOKNING_TIMMAR timmar innan besökets starttidpunkt.
        if (utredning.getAvbrutenAnledning() != null
                && utredning.getAvbrutenAnledning() == EndReason.UTREDNING_AVBRUTEN
                && besok.getErsatts()
                && utredning.getAvbrutenDatum().isBefore(besok.getBesokStartTid().minusHours(MAX_AVBOKNING_TIMMAR))) {
            return false;
        }

        // Besöket är avbokat efter en avvikelse orsakad av vården.
        if (besok.getAvvikelse() != null
                && besok.getAvvikelse().getOrsakatAv() == AvvikelseOrsak.VARDEN) {
            return false;
        }

        // Patient uteblir från ett besök där kallelsen skickades > Senast kallelsedatum (se FMU-G006 Datumberäkning)
        // för den valda kallelseformen.
        if (besok.getAvvikelse() != null && besok.getAvvikelse().getInvanareUteblev() && besok.getKallelseDatum() != null
                && besok.getKallelseDatum().toLocalDate()
                        .isAfter(resolveSenasteKallelseDatum(besok.getBesokStartTid(), besok.getKallelseForm(), businessDays))) {
            return false;
        }

        // Avvikelse rapporteras för ett besök där invånaren har kallats > Senast kallelsedatum
        // (se FMU-G005 Ersättningsberäkning), oavsett när avvikelsetidpunkten inträffade.
        if (besok.getAvvikelse() != null && besok.getKallelseDatum() != null
                && besok.getKallelseDatum().toLocalDate()
                        .isAfter(resolveSenasteKallelseDatum(besok.getBesokStartTid(), besok.getKallelseForm(), businessDays))) {
            return false;
        }

        return true;
    }

    // Vårdenheten måste kalla invånaren i god tid före besökstiden för att invånaren ska ha en möjligt att
    // boka eventuella resor till utföraren. Enligt redovisningskrav och rutiner 2018 ska invånaren har
    // tagit emot kallelsen 5 arbetsdagar innan besöket. Det innebär att för kallelser som skickats per post
    // måste ytterligare 3 arbetsdagar för postgång läggas till.
    // För kallelser per telefon gäller 5 arbetsdagar före besöket. Antalet dagar för postgång och:
    // - Antalet arbetsdagar innan besöks datum som invånaren ska kallelsen måste vara konfigurerbart
    // (KALLELSE_ARBETSDAGAR). Default är KALLELSE_ARBETSDAGAR= 5.
    // - Antalet arbetsdagar för postgång måste vara konfigurerbart (POSTGÅNG_ARBETSDAGAR). Default är POSTGANG_ARBETSDAGAR
    // = 3

    // - Senast kallelsedatum vid kallelse per post = Besöksdatum - (KALLELSE_ARBETSDAGAR + POSTGANG_ARBETSDAGAR )
    // arbetsdagar exklusive semesterperioder
    // - Senast kallelsedatum vid kallelse per telefon = Besöksdatum - KALLELSE_ARBETSDAGAR arbetsdagar exklusive
    // semesterperioder
    static LocalDate resolveSenasteKallelseDatum(LocalDateTime besoksDatum, KallelseFormTyp kallelseForm, BusinessDaysBean businessDays) {

        int arbetsDagar = resolveNumberOfWorkingDays(kallelseForm);

        int days = 0;
        LocalDate kallelseDatum = LocalDate.from(besoksDatum.toLocalDate());
        while (days < arbetsDagar) {
            kallelseDatum = kallelseDatum.minusDays(1);
            days = businessDays.daysBetween(kallelseDatum, besoksDatum.toLocalDate());
        }
        return kallelseDatum;
    }

    private static int resolveNumberOfWorkingDays(KallelseFormTyp kallelseForm) {

        if (kallelseForm == KallelseFormTyp.BREVKONTAKT) {
            return KALLELSE_ARBETSDAGAR + POSTGANG_ARBETSDAGAR;
        } else {
            return KALLELSE_ARBETSDAGAR;
        }
    }
}
