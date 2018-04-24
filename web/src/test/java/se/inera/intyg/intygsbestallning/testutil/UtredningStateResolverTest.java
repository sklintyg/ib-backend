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
package se.inera.intyg.intygsbestallning.testutil;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import se.inera.intyg.intygsbestallning.persistence.model.Bestallning;
import se.inera.intyg.intygsbestallning.persistence.model.ExternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.ForfraganSvar;
import se.inera.intyg.intygsbestallning.persistence.model.Handling;
import se.inera.intyg.intygsbestallning.persistence.model.InternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.SvarTyp;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.service.stateresolver.Actor;
import se.inera.intyg.intygsbestallning.service.stateresolver.UtredningFas;
import se.inera.intyg.intygsbestallning.service.stateresolver.UtredningStateResolver;
import se.inera.intyg.intygsbestallning.service.stateresolver.UtredningStatus;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class UtredningStateResolverTest {

    @InjectMocks
    private UtredningStateResolver testee;

    @Test
    public void testResolvesForfraganInkommen() {
        Utredning utr = buildBaseUtredning();
        UtredningStatus status = testee.resolveStatus(utr);
        assertEquals(UtredningStatus.FORFRAGAN_INKOMMEN, status);
        assertEquals(UtredningFas.FORFRAGAN, status.getUtredningFas());
        assertEquals(Actor.SAMORDNARE, status.getNextActor());
    }

    @Test
    public void testResolvesVantarPaSvar() {
        Utredning utr = buildBaseUtredning();
        utr.getExternForfragan().getInternForfraganList().add(buildInternForfragan(null, null));
        UtredningStatus status = testee.resolveStatus(utr);
        assertEquals(UtredningStatus.VANTAR_PA_SVAR, status);
        assertEquals(UtredningFas.FORFRAGAN, status.getUtredningFas());
        assertEquals(Actor.VARDADMIN, status.getNextActor());
    }

    @Test
    public void testResolvesVantarPaSvarWhenAvbojd() {
        Utredning utr = buildBaseUtredning();
        utr.getExternForfragan().getInternForfraganList().add(buildInternForfragan(buildForfraganSvar(SvarTyp.AVBOJ), null));
        UtredningStatus status = testee.resolveStatus(utr);
        assertEquals(UtredningStatus.VANTAR_PA_SVAR, status);
        assertEquals(UtredningFas.FORFRAGAN, status.getUtredningFas());
        assertEquals(Actor.VARDADMIN, status.getNextActor());
    }

    @Test
    public void testResolvesTilldelaUtredningWhenAccepterad() {
        Utredning utr = buildBaseUtredning();
        utr.getExternForfragan().getInternForfraganList().add(buildInternForfragan(buildForfraganSvar(SvarTyp.ACCEPTERA), null));
        UtredningStatus status = testee.resolveStatus(utr);
        assertEquals(UtredningStatus.TILLDELA_UTREDNING, status);
        assertEquals(UtredningFas.FORFRAGAN, status.getUtredningFas());
        assertEquals(Actor.SAMORDNARE, status.getNextActor());
    }


    @Test
    public void testResolvesTilldelaUtredning() {
        Utredning utr = buildBaseUtredning();
        utr.getExternForfragan().getInternForfraganList().add(buildInternForfragan(buildForfraganSvar(SvarTyp.ACCEPTERA), LocalDateTime.now()));
        UtredningStatus status = testee.resolveStatus(utr);
        assertEquals(UtredningStatus.TILLDELAD_VANTAR_PA_BESTALLNING, status);
        assertEquals(UtredningFas.FORFRAGAN, status.getUtredningFas());
        assertEquals(Actor.FK, status.getNextActor());
    }


    @Test
    public void testResolvesBestallningMottagenVantarPaHandlingar() {
        Utredning utr = buildBaseUtredning();
        utr.getExternForfragan().getInternForfraganList().add(buildInternForfragan(buildForfraganSvar(SvarTyp.ACCEPTERA), LocalDateTime.now()));
        utr.setBestallning(buildBestallning(null));
        utr.getHandlingList().clear();

        UtredningStatus status = testee.resolveStatus(utr);
        assertEquals(UtredningStatus.BESTALLNING_MOTTAGEN_VANTAR_PA_HANDLINGAR, status);
        assertEquals(UtredningFas.UTREDNING, status.getUtredningFas());
        assertEquals(Actor.FK, status.getNextActor());
    }

    // UPPDATERAD_BESTALLNING_VANTAR_PA_HANDLINGAR
    @Test
    public void testResolvesUppdateradBestallningVantarPaHandlingar() {
        Utredning utr = buildBaseUtredning();
        utr.getExternForfragan().getInternForfraganList().add(buildInternForfragan(buildForfraganSvar(SvarTyp.ACCEPTERA), LocalDateTime.now()));
        utr.setBestallning(buildBestallning(LocalDateTime.now()));
        utr.getHandlingList().clear();

        UtredningStatus status = testee.resolveStatus(utr);
        assertEquals(UtredningStatus.UPPDATERAD_BESTALLNING_VANTAR_PA_HANDLINGAR, status);
        assertEquals(UtredningFas.UTREDNING, status.getUtredningFas());
        assertEquals(Actor.FK, status.getNextActor());
    }

    @Test
    public void testResolvesHandlingarMottagnaVantaPaBesok() {
        Utredning utr = buildBaseUtredning();
        utr.getExternForfragan().getInternForfraganList().add(buildInternForfragan(buildForfraganSvar(SvarTyp.ACCEPTERA), LocalDateTime.now()));
        utr.setBestallning(buildBestallning(null));
        utr.getHandlingList().add(buildHandling(LocalDateTime.now(), null));

        UtredningStatus status = testee.resolveStatus(utr);
        assertEquals(UtredningStatus.HANDLINGAR_MOTTAGNA_BOKA_BESOK, status);
        assertEquals(UtredningFas.UTREDNING, status.getUtredningFas());
        assertEquals(Actor.VARDADMIN, status.getNextActor());
    }

    // add(buildHandling(LocalDateTime.now(), null));

    private Handling buildHandling(LocalDateTime inkomDatum, LocalDateTime skickadDatum) {
        Handling h = new Handling();
        h.setInkomDatum(inkomDatum);
        h.setSkickatDatum(skickadDatum);
        return h;
    }

    private Bestallning buildBestallning(LocalDateTime uppdateradDatum) {
        Bestallning b  = new Bestallning();
        b.setUppdateradDatum(uppdateradDatum);
        return b;
    }


    private ForfraganSvar buildForfraganSvar(SvarTyp svarTyp) {
        ForfraganSvar fs = new ForfraganSvar();
        fs.setSvarTyp(svarTyp);
        return fs;
    }

    private InternForfragan buildInternForfragan(ForfraganSvar forfraganSvar, LocalDateTime tilldeladDatum) {
        InternForfragan internForfragan = new InternForfragan();
        internForfragan.setForfraganSvar(forfraganSvar);
        internForfragan.setTilldeladDatum(tilldeladDatum);
        return internForfragan;
    }

    private Utredning buildBaseUtredning() {
        Utredning utr = new Utredning();
        utr.setExternForfragan(buildBaseExternForfragan());
        return utr;
    }

    private ExternForfragan buildBaseExternForfragan() {
        ExternForfragan externForfragan = new ExternForfragan();

        return externForfragan;
    }

}
