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
import se.inera.intyg.intygsbestallning.service.utredning.ServiceTestUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ErsattsResolverTest {

    private static final LocalDateTime FEB_20 = LocalDateTime.of(2018,2,20, 0,0);
    private static final LocalDateTime FEB_13 = LocalDateTime.of(2018,2,13, 0,0);
    private static final LocalDateTime FEB_8 = LocalDateTime.of(2018,2,8, 0,0);


    @Test
    public void testErsattsEjVidJav() {
        Utredning utr = Utredning.UtredningBuilder.anUtredning()
                .withAvbrutenDatum(LocalDateTime.now())
                .withAvbrutenAnledning(EndReason.JAV)
                .build();
        assertFalse(ErsattsResolver.resolveUtredningErsatts(utr));
    }

    @Test
    public void testErsattsEjVidIngenBestallning() {
        Utredning utr = Utredning.UtredningBuilder.anUtredning()
                .withAvbrutenDatum(LocalDateTime.now())
                .withAvbrutenAnledning(EndReason.INGEN_BESTALLNING)
                .build();
        assertFalse(ErsattsResolver.resolveUtredningErsatts(utr));
    }

    // Slutdatum för utredningen passeras innan utlåtandet är mottaget av Försäkringskassan
    @Test
    public void testErsattsEjNarSlutdatumPasseratsForeMottagetAvFK() {
        List<Utredning> utredningList = ServiceTestUtil.buildBestallningar(1);
        assertFalse(ErsattsResolver.resolveUtredningErsatts(utredningList.get(0)));
    }

    @Test
    public void testErsattsEjNarMottagetAvFKForeSlutdatumMenBesokEjFinns() {
        List<Utredning> utredningList = ServiceTestUtil.buildBestallningar(1);

        utredningList.get(0).getIntygList().get(0).setSistaDatum(LocalDateTime.now().minusDays(10L));
        utredningList.get(0).getIntygList().get(0).setMottagetDatum(LocalDateTime.now().minusDays(20L));
        assertFalse(ErsattsResolver.resolveUtredningErsatts(utredningList.get(0)));
    }

    @Test
    public void testErsattsEjNarMottagetAvFKForeSlutdatumMenBesokFinnsDockEjErsatts() {
        List<Utredning> utredningList = ServiceTestUtil.buildBestallningar(1);
        utredningList.get(0).setBesokList(buildBesokList(false, FEB_20));
        utredningList.get(0).getIntygList().get(0).setSistaDatum(LocalDateTime.now().minusDays(10L));
        utredningList.get(0).getIntygList().get(0).setMottagetDatum(LocalDateTime.now().minusDays(20L));
        assertFalse(ErsattsResolver.resolveUtredningErsatts(utredningList.get(0)));
    }

    @Test
    public void testErsattsNarMottagetAvFKForeSlutdatumOchBesokFinns() {
        List<Utredning> utredningList = ServiceTestUtil.buildBestallningar(1);
        utredningList.get(0).setBesokList(buildBesokList(true, FEB_20));
        utredningList.get(0).getIntygList().get(0).setSistaDatum(LocalDateTime.now().minusDays(10L));
        utredningList.get(0).getIntygList().get(0).setMottagetDatum(LocalDateTime.now().minusDays(20L));
        assertTrue(ErsattsResolver.resolveUtredningErsatts(utredningList.get(0)));
    }

    @Test
    public void testResolveBesokErsattsEjPgaPatientAvvikelseForeSistaAvbokning() {
        List<Utredning> utredningList = ServiceTestUtil.buildBestallningar(1);
        utredningList.get(0).setBesokList(buildBesokList(true, FEB_20));

        Avvikelse avvikelse = buildAvvikelse(false, AvvikelseOrsak.PATIENT, FEB_13);
        utredningList.get(0).getBesokList().get(0).setAvvikelse(avvikelse);
        assertFalse(ErsattsResolver.resolveBesokErsatts(utredningList.get(0), utredningList.get(0).getBesokList().get(0)));
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
                KallelseFormTyp.BREVKONTAKT);
        // 5 + 3 + four weekend days
        assertEquals(8, kallelseDatum.getDayOfMonth());
    }

    @Test
    public void testResolveSenasteKallelseDatumTelefon() {
        // 20th of february was a tuesday.
        LocalDate kallelseDatum = ErsattsResolver.resolveSenasteKallelseDatum(
                LocalDateTime.of(2018, 02, 20, 0, 0, 0),
                KallelseFormTyp.TELEFONKONTAKT);
        // 5 + sat & sun == 13.
        assertEquals(13, kallelseDatum.getDayOfMonth());
    }

    private List<Besok> buildBesokList(boolean ersatts, LocalDateTime besokStartTid) {
        Besok b = Besok.BesokBuilder.aBesok()
                .withBesokStartTid(besokStartTid)
                .withErsatts(ersatts)
                .build();
        return Arrays.asList(b);
    }

    private List<Intyg> buildIntygList() {
        return Arrays.asList(Intyg.IntygBuilder.anIntyg()
            .withMottagetDatum(LocalDateTime.now())
                .build());

    }
}
