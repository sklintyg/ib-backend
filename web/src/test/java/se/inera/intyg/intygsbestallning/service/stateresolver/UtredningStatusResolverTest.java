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
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import se.inera.intyg.intygsbestallning.persistence.model.Avvikelse;
import se.inera.intyg.intygsbestallning.persistence.model.Besok;
import se.inera.intyg.intygsbestallning.persistence.model.Handling;
import se.inera.intyg.intygsbestallning.persistence.model.Intyg;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.type.AvvikelseOrsak;
import se.inera.intyg.intygsbestallning.persistence.model.type.BesokStatusTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.DeltagarProfessionTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.SvarTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.TolkStatusTyp;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class UtredningStatusResolverTest extends BaseResolverTest {

    @InjectMocks
    private UtredningStatusResolver testee;

    @Test
    public void testAvbruten() {
        Utredning utr = buildBaseUtredning();
        utr.setAvbrutenDatum(LocalDateTime.now());
        UtredningStatus status = testee.resolveStatus(utr);
        assertEquals(UtredningStatus.AVBRUTEN, status);
        assertEquals(UtredningFas.AVSLUTAD, status.getUtredningFas());
        assertEquals(Actor.NONE, status.getNextActor());
    }

    @Test
    public void testAvvisad() {
        Utredning utr = buildBaseUtredning();
        utr.getExternForfragan().setAvvisatDatum(LocalDateTime.now());
        UtredningStatus status = testee.resolveStatus(utr);
        assertEquals(UtredningStatus.AVVISAD, status);
        assertEquals(UtredningFas.AVSLUTAD, status.getUtredningFas());
        assertEquals(Actor.NONE, status.getNextActor());
    }

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
        utr.getExternForfragan().getInternForfraganList()
                .add(buildInternForfragan(buildForfraganSvar(SvarTyp.ACCEPTERA), LocalDateTime.now()));
        UtredningStatus status = testee.resolveStatus(utr);
        assertEquals(UtredningStatus.TILLDELAD_VANTAR_PA_BESTALLNING, status);
        assertEquals(UtredningFas.FORFRAGAN, status.getUtredningFas());
        assertEquals(Actor.FK, status.getNextActor());
    }

    @Test
    public void testResolvesBestallningMottagenVantarPaHandlingar() {
        Utredning utr = buildBaseUtredning();
        utr.getExternForfragan().getInternForfraganList()
                .add(buildInternForfragan(buildForfraganSvar(SvarTyp.ACCEPTERA), LocalDateTime.now()));
        utr.setBestallning(buildBestallning(null));
        utr.getIntygList().add(buildBestalltIntyg());
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
        utr.getExternForfragan().getInternForfraganList()
                .add(buildInternForfragan(buildForfraganSvar(SvarTyp.ACCEPTERA), LocalDateTime.now()));
        utr.setBestallning(buildBestallning(LocalDateTime.now()));
        utr.getIntygList().add(buildBestalltIntyg());
        utr.getHandlingList().clear();

        UtredningStatus status = testee.resolveStatus(utr);
        assertEquals(UtredningStatus.UPPDATERAD_BESTALLNING_VANTAR_PA_HANDLINGAR, status);
        assertEquals(UtredningFas.UTREDNING, status.getUtredningFas());
        assertEquals(Actor.FK, status.getNextActor());
    }

    @Test
    public void testResolvesHandlingarMottagnaVantaPaBesok() {
        Utredning utr = buildBaseUtredning();
        utr.getExternForfragan().getInternForfraganList()
                .add(buildInternForfragan(buildForfraganSvar(SvarTyp.ACCEPTERA), LocalDateTime.now()));
        utr.setBestallning(buildBestallning(null));
        utr.getIntygList().add(buildBestalltIntyg());
        utr.getHandlingList().add(buildHandling(LocalDateTime.now(), null));

        UtredningStatus status = testee.resolveStatus(utr);
        assertEquals(UtredningStatus.HANDLINGAR_MOTTAGNA_BOKA_BESOK, status);
        assertEquals(UtredningFas.UTREDNING, status.getUtredningFas());
        assertEquals(Actor.VARDADMIN, status.getNextActor());
    }

    @Test
    public void testResolvesAvvikelseMottagen() {
        Utredning utr = buildBaseUtredning();
        utr.getExternForfragan().getInternForfraganList()
                .add(buildInternForfragan(buildForfraganSvar(SvarTyp.ACCEPTERA), LocalDateTime.now()));
        utr.setBestallning(buildBestallning(null));
        utr.getIntygList().add(buildBestalltIntyg());
        utr.getHandlingList().add(buildHandling(LocalDateTime.now(), null));
        utr.getBesokList().add(Besok.BesokBuilder.aBesok()
                .withBesokStatus(BesokStatusTyp.TIDBOKAD_VARDKONTAKT)
                .withDeltagareProfession(DeltagarProfessionTyp.FT)
                .withAvvikelse(Avvikelse.AvvikelseBuilder.anAvvikelse()
                        .withOrsakatAv(AvvikelseOrsak.PATIENT)
                        .withTidpunkt(LocalDateTime.now())
                        .build())
                .build());

        UtredningStatus status = testee.resolveStatus(utr);
        assertEquals(UtredningStatus.AVVIKELSE_MOTTAGEN, status);
        assertEquals(UtredningFas.UTREDNING, status.getUtredningFas());
        assertEquals(Actor.VARDADMIN, status.getNextActor());
    }

    @Test
    public void testResolvesUtredningPagar() {
        Utredning utr = buildBaseUtredning();
        utr.getExternForfragan().getInternForfraganList()
                .add(buildInternForfragan(buildForfraganSvar(SvarTyp.ACCEPTERA), LocalDateTime.now()));
        utr.setBestallning(buildBestallning(null));
        utr.getIntygList().add(buildBestalltIntyg());
        utr.getHandlingList().add(buildHandling(LocalDateTime.now(), null));
        utr.getBesokList().add(Besok.BesokBuilder.aBesok()
                .withBesokStatus(BesokStatusTyp.TIDBOKAD_VARDKONTAKT)
                .withDeltagareProfession(DeltagarProfessionTyp.FT)
                .build());

        UtredningStatus status = testee.resolveStatus(utr);
        assertEquals(UtredningStatus.UTREDNING_PAGAR, status);
        assertEquals(UtredningFas.UTREDNING, status.getUtredningFas());
        assertEquals(Actor.UTREDARE, status.getNextActor());
    }

    @Test
    public void testResolvesUtlatandeSkickat() {
        Utredning utr = buildBaseUtredning();
        utr.getExternForfragan().getInternForfraganList()
                .add(buildInternForfragan(buildForfraganSvar(SvarTyp.ACCEPTERA), LocalDateTime.now()));
        utr.setBestallning(buildBestallning(null));
        Intyg intyg = buildBestalltIntyg();
        intyg.setSkickatDatum(LocalDateTime.now());
        utr.getIntygList().add(intyg);
        utr.getHandlingList().add(buildHandling(LocalDateTime.now(), null));
        utr.getBesokList().add(Besok.BesokBuilder.aBesok()
                .withBesokStatus(BesokStatusTyp.TIDBOKAD_VARDKONTAKT)
                .withDeltagareProfession(DeltagarProfessionTyp.FT)
                .build());

        UtredningStatus status = testee.resolveStatus(utr);
        assertEquals(UtredningStatus.UTLATANDE_SKICKAT, status);
        assertEquals(UtredningFas.UTREDNING, status.getUtredningFas());
        assertEquals(Actor.FK, status.getNextActor());
    }

    @Test
    public void testResolvesUtlatandeMottaget() {
        Utredning utr = buildBaseUtredning();
        utr.getExternForfragan().getInternForfraganList()
                .add(buildInternForfragan(buildForfraganSvar(SvarTyp.ACCEPTERA), LocalDateTime.now()));
        utr.setBestallning(buildBestallning(null));
        Intyg intyg = buildBestalltIntyg();
        intyg.setSkickatDatum(LocalDateTime.now());
        intyg.setMottagetDatum(LocalDateTime.now());
        intyg.setSistaDatumKompletteringsbegaran(LocalDateTime.now().plusDays(1));
        utr.getIntygList().add(intyg);
        utr.getHandlingList().add(buildHandling(LocalDateTime.now(), null));
        utr.getBesokList().add(Besok.BesokBuilder.aBesok()
                .withBesokStatus(BesokStatusTyp.TIDBOKAD_VARDKONTAKT)
                .withDeltagareProfession(DeltagarProfessionTyp.FT)
                .build());

        UtredningStatus status = testee.resolveStatus(utr);
        assertEquals(UtredningStatus.UTLATANDE_MOTTAGET, status);
        assertEquals(UtredningFas.UTREDNING, status.getUtredningFas());
        assertEquals(Actor.FK, status.getNextActor());
    }

    @Test
    public void testResolveAvslutadEjTolkEjKomplt() {
        Utredning utr = buildBaseUtredning();
        utr.getExternForfragan().getInternForfraganList()
                .add(buildInternForfragan(buildForfraganSvar(SvarTyp.ACCEPTERA), LocalDateTime.now()));
        utr.setBestallning(buildBestallning(null));
        Intyg intyg = buildBestalltIntyg();
        intyg.setSkickatDatum(LocalDateTime.now());
        intyg.setMottagetDatum(LocalDateTime.now());
        intyg.setSistaDatumKompletteringsbegaran(LocalDateTime.now().minusDays(1));
        utr.getIntygList().add(intyg);
        utr.getHandlingList().add(buildHandling(LocalDateTime.now(), null));
        utr.getBesokList().add(Besok.BesokBuilder.aBesok()
                .withBesokStatus(BesokStatusTyp.TIDBOKAD_VARDKONTAKT)
                .withDeltagareProfession(DeltagarProfessionTyp.FT)
                .withTolkStatus(null)
                .build());

        UtredningStatus status = testee.resolveStatus(utr);
        assertEquals(UtredningStatus.AVSLUTAD, status);
        assertEquals(UtredningFas.AVSLUTAD, status.getUtredningFas());
        assertEquals(Actor.NONE, status.getNextActor());
    }

    @Test
    public void testResolveAvslutadTolkRedovisadEjKomplt() {
        Utredning utr = buildBaseUtredning();
        utr.getExternForfragan().getInternForfraganList()
                .add(buildInternForfragan(buildForfraganSvar(SvarTyp.ACCEPTERA), LocalDateTime.now()));
        utr.setBestallning(buildBestallning(null));
        Intyg intyg = buildBestalltIntyg();
        intyg.setSkickatDatum(LocalDateTime.now());
        intyg.setMottagetDatum(LocalDateTime.now());
        intyg.setSistaDatumKompletteringsbegaran(LocalDateTime.now().minusDays(1));
        utr.getIntygList().add(intyg);
        utr.getHandlingList().add(buildHandling(LocalDateTime.now(), null));
        utr.getBesokList().add(Besok.BesokBuilder.aBesok()
                .withBesokStatus(BesokStatusTyp.TIDBOKAD_VARDKONTAKT)
                .withDeltagareProfession(DeltagarProfessionTyp.FT)
                .withTolkStatus(TolkStatusTyp.DELTAGIT)
                .withErsatts(true)
                .build());

        UtredningStatus status = testee.resolveStatus(utr);
        assertEquals(UtredningStatus.AVSLUTAD, status);
        assertEquals(UtredningFas.AVSLUTAD, status.getUtredningFas());
        assertEquals(Actor.NONE, status.getNextActor());
    }

    @Test
    public void testResolveAvslutadTolkEjRedovisadEjKomplt() {
        Utredning utr = buildBaseUtredning();
        utr.getExternForfragan().getInternForfraganList()
                .add(buildInternForfragan(buildForfraganSvar(SvarTyp.ACCEPTERA), LocalDateTime.now()));
        utr.setBestallning(buildBestallning(null));
        Intyg intyg = buildBestalltIntyg();
        intyg.setSkickatDatum(LocalDateTime.now());
        intyg.setMottagetDatum(LocalDateTime.now());
        intyg.setSistaDatumKompletteringsbegaran(LocalDateTime.now().minusDays(1));
        utr.getIntygList().add(intyg);
        utr.getHandlingList().add(buildHandling(LocalDateTime.now(), null));
        utr.getBesokList().add(Besok.BesokBuilder.aBesok()
                .withBesokStatus(BesokStatusTyp.TIDBOKAD_VARDKONTAKT)
                .withDeltagareProfession(DeltagarProfessionTyp.FT)
                .withTolkStatus(TolkStatusTyp.BOKAT)
                .withErsatts(true)
                .build());

        UtredningStatus status = testee.resolveStatus(utr);
        assertEquals(UtredningStatus.REDOVISA_TOLK, status);
        assertEquals(UtredningFas.REDOVISA_TOLK, status.getUtredningFas());
        assertEquals(Actor.VARDADMIN, status.getNextActor());
    }

    private Handling buildHandling(LocalDateTime inkomDatum, LocalDateTime skickadDatum) {
        Handling h = new Handling();
        h.setInkomDatum(inkomDatum);
        h.setSkickatDatum(skickadDatum);
        return h;
    }

    // Den post som skapas i samband med inkommen beställning. Dvs EJ komplettering.
    private Intyg buildBestalltIntyg() {
        return Intyg.IntygBuilder.anIntyg()
                .withId(1L)
                .withKomplettering(false)
                .withSistaDatum(LocalDateTime.now().plusDays(25))
                .build();
    }

}
