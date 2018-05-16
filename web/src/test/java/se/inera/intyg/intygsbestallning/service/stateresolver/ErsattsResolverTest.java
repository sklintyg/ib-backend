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

import org.junit.Test;
import se.inera.intyg.intygsbestallning.persistence.model.Avvikelse;
import se.inera.intyg.intygsbestallning.persistence.model.Besok;
import se.inera.intyg.intygsbestallning.persistence.model.Intyg;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.type.AvvikelseOrsak;
import se.inera.intyg.intygsbestallning.persistence.model.type.EndReason;
import se.inera.intyg.intygsbestallning.persistence.model.type.KallelseFormTyp;
import se.inera.intyg.intygsbestallning.service.util.BusinessDaysBean;
import se.inera.intyg.intygsbestallning.service.util.BusinessDaysStub;
import se.inera.intyg.intygsbestallning.service.utredning.ServiceTestUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ErsattsResolverTest {

    private static final LocalDateTime FEB_20 = LocalDateTime.of(2018, 2, 20, 11, 0);
    private static final LocalDateTime FEB_20_MORGON = LocalDateTime.of(2018, 2, 20, 8, 0);
    private static final LocalDateTime FEB_13 = LocalDateTime.of(2018, 2, 13, 0, 0);
    private static final LocalDateTime FEB_8 = LocalDateTime.of(2018, 2, 8, 0, 0);

    private BusinessDaysBean businessDays = new BusinessDaysStub();

    @Test
    public void testErsattsEjVidJav() {
        Utredning utr = Utredning.UtredningBuilder.anUtredning()
                .withAvbrutenDatum(LocalDateTime.now())
                .withAvbrutenAnledning(EndReason.JAV)
                .build();
        assertFalse(ErsattsResolver.resolveUtredningErsatts(utr, businessDays));
    }

    @Test
    public void testErsattsEjVidIngenBestallning() {
        Utredning utr = Utredning.UtredningBuilder.anUtredning()
                .withAvbrutenDatum(LocalDateTime.now())
                .withAvbrutenAnledning(EndReason.INGEN_BESTALLNING)
                .build();
        assertFalse(ErsattsResolver.resolveUtredningErsatts(utr, businessDays));
    }

    // Slutdatum för utredningen passeras innan utlåtandet är mottaget av Försäkringskassan
    @Test
    public void testErsattsEjNarSlutdatumPasseratsForeMottagetAvFK() {
        Utredning utredning = ServiceTestUtil.buildBestallningar(1).get(0);
        assertFalse(ErsattsResolver.resolveUtredningErsatts(utredning, businessDays));
    }

    @Test
    public void testErsattsEjNarMottagetAvFKForeSlutdatumMenBesokEjFinns() {
        Utredning utredning = ServiceTestUtil.buildBestallningar(1).get(0);

        utredning.getIntygList().get(0).setSistaDatum(LocalDateTime.now().minusDays(10L));
        utredning.getIntygList().get(0).setMottagetDatum(LocalDateTime.now().minusDays(20L));
        assertFalse(ErsattsResolver.resolveUtredningErsatts(utredning, businessDays));
    }

    @Test
    public void testErsattsEjNarMottagetAvFKForeSlutdatumMenBesokFinnsDockEjErsatts() {
        Utredning utredning = ServiceTestUtil.buildBestallningar(1).get(0);
        utredning.setBesokList(buildBesokList(false, FEB_20));
        utredning.getIntygList().get(0).setSistaDatum(LocalDateTime.now().minusDays(10L));
        utredning.getIntygList().get(0).setMottagetDatum(LocalDateTime.now().minusDays(20L));
        assertFalse(ErsattsResolver.resolveUtredningErsatts(utredning, businessDays));
    }

    @Test
    public void testErsattsNarMottagetAvFKForeSlutdatumOchBesokFinns() {
        Utredning utredning = ServiceTestUtil.buildBestallningar(1).get(0);
        utredning.setBesokList(buildBesokList(true, FEB_20));
        utredning.getIntygList().get(0).setSistaDatum(LocalDateTime.now().minusDays(10L));
        utredning.getIntygList().get(0).setMottagetDatum(LocalDateTime.now().minusDays(20L));
        assertTrue(ErsattsResolver.resolveUtredningErsatts(utredning, businessDays));
    }

    @Test
    public void testResolveBesokErsattsEjPgaPatientAvvikelseForeSistaAvbokning() {
        Utredning utredning = ServiceTestUtil.buildBestallningar(1).get(0);
        utredning.setBesokList(buildBesokList(true, FEB_20));

        Avvikelse avvikelse = buildAvvikelse(false, AvvikelseOrsak.PATIENT, FEB_13);
        utredning.getBesokList().get(0).setAvvikelse(avvikelse);
        assertFalse(ErsattsResolver.resolveBesokErsatts(utredning, utredning.getBesokList().get(0), businessDays));
    }

    @Test
    public void testResolveBesokErsattsPgaPatientAvvikelseEfterSistaAvbokning() {
        Utredning utredning = ServiceTestUtil.buildBestallningar(1).get(0);
        utredning.setBesokList(buildBesokList(true, FEB_20));

        Avvikelse avvikelse = buildAvvikelse(false, AvvikelseOrsak.PATIENT, FEB_20_MORGON);
        utredning.getBesokList().get(0).setAvvikelse(avvikelse);
        assertTrue(ErsattsResolver.resolveBesokErsatts(utredning, utredning.getBesokList().get(0), businessDays));
    }

    // Anrop till EndAssessment har inkommit mer än MAX_AVBOKNING_TIMMAR timmar innan besökets starttidpunkt.
    @Test
    public void testErsattsEjNarEndAssessmentInkomIGodTid() {
        Utredning utredning = ServiceTestUtil.buildBestallningar(1).get(0);
        utredning.setBesokList(buildBesokList(true, FEB_20));
        utredning.setAvbrutenAnledning(EndReason.UTREDNING_AVBRUTEN);
        utredning.setAvbrutenDatum(FEB_13);
        assertFalse(ErsattsResolver.resolveBesokErsatts(utredning, utredning.getBesokList().get(0), businessDays));
    }

    @Test
    public void testResolveBesokErsattsEjPgaAvvikelseOrsakadAvVarden() {
        Utredning utredning = ServiceTestUtil.buildBestallningar(1).get(0);
        utredning.setBesokList(buildBesokList(true, FEB_20));

        Avvikelse avvikelse = buildAvvikelse(false, AvvikelseOrsak.VARDEN, FEB_13);
        utredning.getBesokList().get(0).setAvvikelse(avvikelse);
        assertFalse(ErsattsResolver.resolveBesokErsatts(utredning, utredning.getBesokList().get(0), businessDays));
    }

    @Test
    public void testResolveBesokErsattsEjPgaPatientUteblevOchKallelseSkickadesITid() {
        Utredning utredning = ServiceTestUtil.buildBestallningar(1).get(0);
        utredning.setBesokList(buildBesokList(true, FEB_20, FEB_13, KallelseFormTyp.BREVKONTAKT));

        Avvikelse avvikelse = buildAvvikelse(true, AvvikelseOrsak.PATIENT, FEB_20);
        utredning.getBesokList().get(0).setAvvikelse(avvikelse);
        assertFalse(ErsattsResolver.resolveBesokErsatts(utredning, utredning.getBesokList().get(0), businessDays));
    }

    @Test
    public void testResolveBesokErsattsPgaPatientUteblevMenKallelseSkickadesEjITid() {
        Utredning utredning = ServiceTestUtil.buildBestallningar(1).get(0);
        utredning.setBesokList(buildBesokList(true, FEB_20, FEB_8, KallelseFormTyp.TELEFONKONTAKT));

        Avvikelse avvikelse = buildAvvikelse(true, AvvikelseOrsak.PATIENT, FEB_20);
        utredning.getBesokList().get(0).setAvvikelse(avvikelse);
        assertTrue(ErsattsResolver.resolveBesokErsatts(utredning, utredning.getBesokList().get(0), businessDays));
    }

    // Avvikelse rapporteras för ett besök där invånaren har kallats > Senast kallelsedatum
    // (se FMU-G005 Ersättningsberäkning), oavsett när avvikelsetidpunkten inträffade.
    @Test
    public void testResolveBesokErsattsEjPgaAvvikelseDarPatientKalladesITid() {
        Utredning utredning = ServiceTestUtil.buildBestallningar(1).get(0);
        utredning.setBesokList(buildBesokList(true, FEB_20, FEB_13, KallelseFormTyp.BREVKONTAKT));

        Avvikelse avvikelse = buildAvvikelse(false, AvvikelseOrsak.PATIENT, FEB_20);
        utredning.getBesokList().get(0).setAvvikelse(avvikelse);
        assertFalse(ErsattsResolver.resolveBesokErsatts(utredning, utredning.getBesokList().get(0), businessDays));
    }

    private Avvikelse buildAvvikelse(boolean invanareUteblev, AvvikelseOrsak orsak, LocalDateTime avvikelseTidpunkt) {
        Avvikelse avvikelse = Avvikelse.AvvikelseBuilder.anAvvikelse()
                .withInvanareUteblev(invanareUteblev)
                .withOrsakatAv(orsak)
                .withTidpunkt(avvikelseTidpunkt)
                .build();

        return avvikelse;
    }

    @Test
    public void testResolveSenasteKallelseDatumPost() {
        // 20th of february was a tuesday.
        LocalDate kallelseDatum = ErsattsResolver.resolveSenasteKallelseDatum(
                LocalDateTime.of(2018, 02, 20, 0, 0, 0),
                KallelseFormTyp.BREVKONTAKT, businessDays);
        // 5 + 3 + four weekend days
        assertEquals(8, kallelseDatum.getDayOfMonth());
    }

    @Test
    public void testResolveSenasteKallelseDatumTelefon() {
        // 20th of february was a tuesday.
        LocalDate kallelseDatum = ErsattsResolver.resolveSenasteKallelseDatum(
                LocalDateTime.of(2018, 02, 20, 0, 0, 0),
                KallelseFormTyp.TELEFONKONTAKT, businessDays);
        // 5 + sat & sun == 13.
        assertEquals(13, kallelseDatum.getDayOfMonth());
    }

    private List<Besok> buildBesokList(boolean ersatts, LocalDateTime besokStartTid) {
        return buildBesokList(ersatts, besokStartTid, null, null);
    }

    private List<Besok> buildBesokList(boolean ersatts, LocalDateTime besokStartTid, LocalDateTime kallelseDatum,
            KallelseFormTyp kallelseFormTyp) {
        Besok b = Besok.BesokBuilder.aBesok()
                .withBesokStartTid(besokStartTid)
                .withKallelseDatum(kallelseDatum)
                .withKallelseForm(kallelseFormTyp)
                .withErsatts(ersatts)
                .build();
        return Arrays.asList(b);
    }

    private List<Intyg> buildIntygList() {
        return Arrays.asList(Intyg.IntygBuilder.anIntyg()
                .withKomplettering(false)
                .withMottagetDatum(LocalDateTime.now())
                .build());

    }
}
