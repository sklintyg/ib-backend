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

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import se.inera.intyg.intygsbestallning.persistence.model.Anteckning;
import se.inera.intyg.intygsbestallning.persistence.model.Avvikelse;
import se.inera.intyg.intygsbestallning.persistence.model.Besok;
import se.inera.intyg.intygsbestallning.persistence.model.Bestallning;
import se.inera.intyg.intygsbestallning.persistence.model.ExternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.ForfraganSvar;
import se.inera.intyg.intygsbestallning.persistence.model.Handlaggare;
import se.inera.intyg.intygsbestallning.persistence.model.Handling;
import se.inera.intyg.intygsbestallning.persistence.model.InternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.Intyg;
import se.inera.intyg.intygsbestallning.persistence.model.Invanare;
import se.inera.intyg.intygsbestallning.persistence.model.SkickadNotifiering;
import se.inera.intyg.intygsbestallning.persistence.model.TidigareUtforare;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.Handelse;
import se.inera.intyg.intygsbestallning.persistence.model.status.Actor;
import se.inera.intyg.intygsbestallning.persistence.model.status.InternForfraganStatus;
import se.inera.intyg.intygsbestallning.persistence.model.status.UtredningFas;
import se.inera.intyg.intygsbestallning.persistence.model.status.UtredningStatus;
import se.inera.intyg.intygsbestallning.persistence.model.status.UtredningStatusResolver;
import se.inera.intyg.intygsbestallning.persistence.model.type.AvvikelseOrsak;
import se.inera.intyg.intygsbestallning.persistence.model.type.BesokStatusTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.DeltagarProfessionTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.HandlingUrsprungTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.KallelseFormTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringMottagarTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.SvarTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.TolkStatusTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.HandelseTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.UtforareTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.UtredningsTyp;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static se.inera.intyg.intygsbestallning.persistence.model.Avvikelse.AvvikelseBuilder.anAvvikelse;
import static se.inera.intyg.intygsbestallning.persistence.model.Besok.BesokBuilder.aBesok;
import static se.inera.intyg.intygsbestallning.persistence.model.Bestallning.BestallningBuilder.aBestallning;
import static se.inera.intyg.intygsbestallning.persistence.model.ExternForfragan.ExternForfraganBuilder.anExternForfragan;
import static se.inera.intyg.intygsbestallning.persistence.model.ForfraganSvar.ForfraganSvarBuilder.aForfraganSvar;
import static se.inera.intyg.intygsbestallning.persistence.model.Handelse.HandelseBuilder.aHandelse;
import static se.inera.intyg.intygsbestallning.persistence.model.Handlaggare.HandlaggareBuilder.aHandlaggare;
import static se.inera.intyg.intygsbestallning.persistence.model.Handling.HandlingBuilder.aHandling;
import static se.inera.intyg.intygsbestallning.persistence.model.InternForfragan.InternForfraganBuilder.anInternForfragan;
import static se.inera.intyg.intygsbestallning.persistence.model.Intyg.IntygBuilder.anIntyg;
import static se.inera.intyg.intygsbestallning.persistence.model.Invanare.InvanareBuilder.anInvanare;
import static se.inera.intyg.intygsbestallning.persistence.model.SkickadNotifiering.SkickadNotifieringBuilder.aSkickadNotifiering;
import static se.inera.intyg.intygsbestallning.persistence.model.Utredning.UtredningBuilder.anUtredning;

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
        utr.getExternForfragan().get().setAvvisatDatum(LocalDateTime.now());
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
        utr.getExternForfragan().map(ExternForfragan::getInternForfraganList).map(iff -> iff.add(buildInternForfragan(null, null)));
        UtredningStatus status = testee.resolveStatus(utr);
        assertEquals(UtredningStatus.VANTAR_PA_SVAR, status);
        assertEquals(UtredningFas.FORFRAGAN, status.getUtredningFas());
        assertEquals(Actor.VARDADMIN, status.getNextActor());
    }

    @Test
    public void testResolvesVantarPaSvarWhenAvbojd() {
        Utredning utr = buildBaseUtredning();
        utr.getExternForfragan().map(ExternForfragan::getInternForfraganList).map(iff -> iff.add(buildInternForfragan(buildForfraganSvar(SvarTyp.AVBOJ), null)));
        UtredningStatus status = testee.resolveStatus(utr);
        assertEquals(UtredningStatus.VANTAR_PA_SVAR, status);
        assertEquals(UtredningFas.FORFRAGAN, status.getUtredningFas());
        assertEquals(Actor.VARDADMIN, status.getNextActor());
    }

    @Test
    public void testResolvesTilldelaUtredningWhenAccepterad() {
        Utredning utr = buildBaseUtredning();
        utr.getExternForfragan()
                .map(ExternForfragan::getInternForfraganList)
                .map(iff -> iff.add(buildInternForfragan(buildForfraganSvar(SvarTyp.ACCEPTERA), null)));
        UtredningStatus status = testee.resolveStatus(utr);
        assertEquals(UtredningStatus.TILLDELA_UTREDNING, status);
        assertEquals(UtredningFas.FORFRAGAN, status.getUtredningFas());
        assertEquals(Actor.SAMORDNARE, status.getNextActor());
    }

    @Test
    public void testResolvesTilldelaUtredning() {
        Utredning utr = buildBaseUtredning();
        utr.getExternForfragan()
                .map(ExternForfragan::getInternForfraganList)
                .map(iff -> iff.add(buildInternForfragan(buildForfraganSvar(SvarTyp.ACCEPTERA), LocalDateTime.now())));
        UtredningStatus status = testee.resolveStatus(utr);
        assertEquals(UtredningStatus.TILLDELAD_VANTAR_PA_BESTALLNING, status);
        assertEquals(UtredningFas.FORFRAGAN, status.getUtredningFas());
        assertEquals(Actor.FK, status.getNextActor());
    }

    @Test
    public void testResolvesBestallningMottagenVantarPaHandlingar() {
        Utredning utr = buildBaseUtredning();
        utr.getExternForfragan()
                .map(ExternForfragan::getInternForfraganList)
                .map(iff -> iff.add(buildInternForfragan(buildForfraganSvar(SvarTyp.ACCEPTERA), LocalDateTime.now())));
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
        utr.getExternForfragan().map(ExternForfragan::getInternForfraganList)
                .map(iff -> iff.add(buildInternForfragan(buildForfraganSvar(SvarTyp.ACCEPTERA), LocalDateTime.now())));
        utr.setBestallning(buildBestallning(LocalDateTime.now()));
        utr.getIntygList().add(buildBestalltIntyg());
        utr.getHandlingList().clear();

        UtredningStatus status = testee.resolveStatus(utr);
        assertEquals(UtredningStatus.UPPDATERAD_BESTALLNING_VANTAR_PA_HANDLINGAR, status);
        assertEquals(UtredningFas.UTREDNING, status.getUtredningFas());
        assertEquals(Actor.FK, status.getNextActor());
    }

    @Test
    public void testResolvesUppdateradBestallningVantarPaHandlingarWhenIngaUppdateradeHandlingarMottagna() {
        Utredning utr = buildBaseUtredning();
        utr.getExternForfragan().map(ExternForfragan::getInternForfraganList)
                .map(iff -> iff.add(buildInternForfragan(buildForfraganSvar(SvarTyp.ACCEPTERA), LocalDateTime.now())));
        utr.setBestallning(buildBestallning(LocalDateTime.now()));
        utr.getIntygList().add(buildBestalltIntyg());
        utr.getHandlingList().clear();
        utr.getHandlingList().add(aHandling().withUrsprung(HandlingUrsprungTyp.BESTALLNING).build());
        utr.getHandlingList().add(aHandling()
                .withUrsprung(HandlingUrsprungTyp.UPPDATERING)
                .withInkomDatum(null)
                .build());

        UtredningStatus status = testee.resolveStatus(utr);
        assertEquals(UtredningStatus.UPPDATERAD_BESTALLNING_VANTAR_PA_HANDLINGAR, status);
        assertEquals(UtredningFas.UTREDNING, status.getUtredningFas());
        assertEquals(Actor.FK, status.getNextActor());
    }

    @Test
    public void testResolvesBokaBesokWhenUppdateringHandlingIsInkommen() {
        Utredning utr = buildBaseUtredning();
        utr.getExternForfragan().map(ExternForfragan::getInternForfraganList)
                .map(iff -> iff.add(buildInternForfragan(buildForfraganSvar(SvarTyp.ACCEPTERA), LocalDateTime.now())));
        utr.setBestallning(buildBestallning(LocalDateTime.now()));
        utr.getIntygList().add(buildBestalltIntyg());

        utr.getHandlingList().clear();
        utr.getHandlingList().add(aHandling()
                .withUrsprung(HandlingUrsprungTyp.BESTALLNING)
                .withInkomDatum(LocalDateTime.now())
                .build());
        utr.getHandlingList().add(aHandling()
                .withUrsprung(HandlingUrsprungTyp.UPPDATERING)
                .withInkomDatum(LocalDateTime.now())
                .build());

        UtredningStatus status = testee.resolveStatus(utr);
        assertEquals(UtredningStatus.HANDLINGAR_MOTTAGNA_BOKA_BESOK, status);
        assertEquals(UtredningFas.UTREDNING, status.getUtredningFas());
        assertEquals(Actor.VARDADMIN, status.getNextActor());
    }

    @Test
    public void testResolvesHandlingarMottagnaVantaPaBesok() {
        Utredning utr = buildBaseUtredning();
        utr.getExternForfragan().map(ExternForfragan::getInternForfraganList)
                .map(iff -> iff.add(buildInternForfragan(buildForfraganSvar(SvarTyp.ACCEPTERA), LocalDateTime.now())));
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
        utr.getExternForfragan()
                .map(ExternForfragan::getInternForfraganList)
                .map(iff -> iff.add(buildInternForfragan(buildForfraganSvar(SvarTyp.ACCEPTERA), LocalDateTime.now())));
        utr.setBestallning(buildBestallning(null));
        utr.getIntygList().add(buildBestalltIntyg());
        utr.getHandlingList().add(buildHandling(LocalDateTime.now(), null));
        utr.getBesokList().add(aBesok()
                .withBesokStatus(BesokStatusTyp.TIDBOKAD_VARDKONTAKT)
                .withDeltagareProfession(DeltagarProfessionTyp.FT)
                .withAvvikelse(Avvikelse.AvvikelseBuilder.anAvvikelse()
                        .withOrsakatAv(AvvikelseOrsak.PATIENT)
                        .withTidpunkt(LocalDateTime.now())
                        .build())
                .withHandelseList(ImmutableList.of(aHandelse()
                        .withHandelseTyp(HandelseTyp.AVVIKELSE_MOTTAGEN)
                        .build()))
                .build());

        UtredningStatus status = testee.resolveStatus(utr);
        assertEquals(UtredningStatus.AVVIKELSE_MOTTAGEN, status);
        assertEquals(UtredningFas.UTREDNING, status.getUtredningFas());
        assertEquals(Actor.VARDADMIN, status.getNextActor());
    }

    @Test
    public void testResolvesUtredningPagar() {
        Utredning utr = buildBaseUtredning();
        utr.getExternForfragan()
                .map(ExternForfragan::getInternForfraganList)
                .map(iff -> iff.add(buildInternForfragan(buildForfraganSvar(SvarTyp.ACCEPTERA), LocalDateTime.now())));
        utr.setBestallning(buildBestallning(null));
        utr.getIntygList().add(buildBestalltIntyg());
        utr.getHandlingList().add(buildHandling(LocalDateTime.now(), null));
        utr.getBesokList().add(aBesok()
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
        utr.getExternForfragan()
                .map(ExternForfragan::getInternForfraganList)
                .map(iff -> iff.add(buildInternForfragan(buildForfraganSvar(SvarTyp.ACCEPTERA), LocalDateTime.now())));
        utr.setBestallning(buildBestallning(null));
        Intyg intyg = buildBestalltIntyg();
        intyg.setSkickatDatum(LocalDateTime.now());
        utr.getIntygList().add(intyg);
        utr.getHandlingList().add(buildHandling(LocalDateTime.now(), null));
        utr.getBesokList().add(aBesok()
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
        utr.getExternForfragan()
                .map(ExternForfragan::getInternForfraganList)
                .map(iff -> iff.add(buildInternForfragan(buildForfraganSvar(SvarTyp.ACCEPTERA), LocalDateTime.now())));
        utr.setBestallning(buildBestallning(null));
        Intyg intyg = buildBestalltIntyg();
        intyg.setSkickatDatum(LocalDateTime.now());
        intyg.setMottagetDatum(LocalDateTime.now());
        intyg.setSistaDatumKompletteringsbegaran(LocalDateTime.now().plusDays(1));
        utr.getIntygList().add(intyg);
        utr.getHandlingList().add(buildHandling(LocalDateTime.now(), null));
        utr.getBesokList().add(aBesok()
                .withBesokStatus(BesokStatusTyp.TIDBOKAD_VARDKONTAKT)
                .withDeltagareProfession(DeltagarProfessionTyp.FT)
                .build());

        UtredningStatus status = testee.resolveStatus(utr);
        assertEquals(UtredningStatus.UTLATANDE_MOTTAGET, status);
        assertEquals(UtredningFas.UTREDNING, status.getUtredningFas());
        assertEquals(Actor.FK, status.getNextActor());
    }

    @Test
    public void testResolveAvslutadBesokRedovisadeEjTolkEjKomplt() {
        Utredning utr = buildBaseUtredning();
        utr.getExternForfragan()
                .map(ExternForfragan::getInternForfraganList)
                .map(iff -> iff.add(buildInternForfragan(buildForfraganSvar(SvarTyp.ACCEPTERA), LocalDateTime.now())));
        utr.setBestallning(buildBestallning(null));
        Intyg intyg = buildBestalltIntyg();
        intyg.setSkickatDatum(LocalDateTime.now());
        intyg.setMottagetDatum(LocalDateTime.now());
        intyg.setSistaDatumKompletteringsbegaran(LocalDateTime.now().minusDays(1));
        utr.getIntygList().add(intyg);
        utr.getHandlingList().add(buildHandling(LocalDateTime.now(), null));
        utr.getBesokList().add(aBesok()
                .withBesokStatus(BesokStatusTyp.AVSLUTAD_VARDKONTAKT)
                .withDeltagareProfession(DeltagarProfessionTyp.FT)
                .withTolkStatus(null)
                .build());

        UtredningStatus status = testee.resolveStatus(utr);
        assertEquals(UtredningStatus.AVSLUTAD, status);
        assertEquals(UtredningFas.AVSLUTAD, status.getUtredningFas());
        assertEquals(Actor.NONE, status.getNextActor());
    }

    @Test
    public void testResolveAvslutadBesokRedovisadeEjKomplt() {
        Utredning utr = buildBaseUtredning();
        utr.getExternForfragan().map(ExternForfragan::getInternForfraganList)
                .map(iff -> iff.add(buildInternForfragan(buildForfraganSvar(SvarTyp.ACCEPTERA), LocalDateTime.now())));
        utr.setBestallning(buildBestallning(null));
        Intyg intyg = buildBestalltIntyg();
        intyg.setSkickatDatum(LocalDateTime.now());
        intyg.setMottagetDatum(LocalDateTime.now());
        intyg.setSistaDatumKompletteringsbegaran(LocalDateTime.now().minusDays(1));
        utr.getIntygList().add(intyg);
        utr.getHandlingList().add(buildHandling(LocalDateTime.now(), null));
        utr.getBesokList().add(aBesok()
                .withBesokStatus(BesokStatusTyp.AVSLUTAD_VARDKONTAKT)
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
    public void testResolveAvslutadBesokEjRedovisadEjKomplt() {
        Utredning utr = buildBasicUtredningForKompletteringTest();

        UtredningStatus status = testee.resolveStatus(utr);
        assertEquals(UtredningStatus.REDOVISA_BESOK, status);
        assertEquals(UtredningFas.REDOVISA_BESOK, status.getUtredningFas());
        assertEquals(Actor.VARDADMIN, status.getNextActor());
    }

    @Test
    public void testResolveKompletteringBegard() {
        Utredning utr = buildBasicUtredningForKompletteringTest();

        Intyg komplt = buildKompletteringIntyg();
        utr.getIntygList().add(komplt);

        UtredningStatus status = testee.resolveStatus(utr);
        assertEquals(UtredningStatus.KOMPLETTERINGSBEGARAN_MOTTAGEN_VANTAR_PA_FRAGESTALLNING, status);
        assertEquals(UtredningFas.KOMPLETTERING, status.getUtredningFas());
        assertEquals(Actor.FK, status.getNextActor());
    }

    @Test
    public void testResolveKompletteringFragestallningMottagen() {
        Utredning utr = buildBasicUtredningForKompletteringTest();

        Intyg komplt = buildKompletteringIntyg();
        komplt.setFragestallningMottagenDatum(LocalDateTime.now());
        utr.getIntygList().add(komplt);

        UtredningStatus status = testee.resolveStatus(utr);
        assertEquals(UtredningStatus.KOMPLETTERANDE_FRAGESTALLNING_MOTTAGEN, status);
        assertEquals(UtredningFas.KOMPLETTERING, status.getUtredningFas());
        assertEquals(Actor.UTREDARE, status.getNextActor());
    }

    @Test
    public void testResolveKompletteringSkickad() {
        Utredning utr = buildBasicUtredningForKompletteringTest();

        Intyg komplt = buildKompletteringIntyg();
        komplt.setFragestallningMottagenDatum(LocalDateTime.now());
        komplt.setSkickatDatum(LocalDateTime.now());
        utr.getIntygList().add(komplt);

        UtredningStatus status = testee.resolveStatus(utr);
        assertEquals(UtredningStatus.KOMPLETTERING_SKICKAD, status);
        assertEquals(UtredningFas.KOMPLETTERING, status.getUtredningFas());
        assertEquals(Actor.FK, status.getNextActor());
    }

    @Test
    public void testResolveKompletteringMottagenSistaDatumEjPasserat() {
        Utredning utr = buildBasicUtredningForKompletteringTest();

        Intyg komplt = buildKompletteringIntyg();
        komplt.setFragestallningMottagenDatum(LocalDateTime.now());
        komplt.setSkickatDatum(LocalDateTime.now());
        komplt.setMottagetDatum(LocalDateTime.now());
        utr.getIntygList().add(komplt);

        UtredningStatus status = testee.resolveStatus(utr);
        assertEquals(UtredningStatus.KOMPLETTERING_MOTTAGEN, status);
        assertEquals(UtredningFas.KOMPLETTERING, status.getUtredningFas());
        assertEquals(Actor.FK, status.getNextActor());
    }

    @Test
    public void bugRegistreraSkickatUtlatandeWithoutRegistreraMottagenHandling() {
        /* Återskapandet av bugg: om användare klickar på "Registera skickat utlåtande" utan att ha klickat på
           "Registrera mottagen handling" först så ändras status som följande:
           BESTALLNING_MOTTAGEN_VANTAR_PA_HANDLINGAR --> BESTALLNING_MOTTAGEN_VANTAR_PA_HANDLINGAR

           Statusändringen borde istället vara
           BESTALLNING_MOTTAGEN_VANTAR_PA_HANDLINGAR --> UTLATANDE_SKICKAT
         */
        Utredning utr = buildBaseUtredning();
        utr.getExternForfragan()
                .map(ExternForfragan::getInternForfraganList)
                .map(iff -> iff.add(buildInternForfragan(buildForfraganSvar(SvarTyp.ACCEPTERA), LocalDateTime.now())));
        utr.setBestallning(buildBestallning(null));
        Intyg intyg = buildBestalltIntyg();
        intyg.setSkickatDatum(LocalDateTime.now()); // Sätts när vårdadmin klickar på "Registrera skickat utlåtande"
        utr.getIntygList().add(intyg);

        Handling h = buildHandling(null, LocalDateTime.now());
        h.setSkickatDatum(LocalDateTime.now());
        h.setUrsprung(HandlingUrsprungTyp.BESTALLNING);
        utr.getHandlingList().add(h);

        UtredningStatus status = testee.resolveStatus(utr);
        assertEquals(UtredningStatus.UTLATANDE_SKICKAT, status);
        assertEquals(UtredningFas.UTREDNING, status.getUtredningFas());
        assertEquals(Actor.FK, status.getNextActor());
    }

    @Test
    public void bugGetStuckInAvvikelseMottagenInsteadOfGoingToSkickaUtlatande() {
        /* Om vårdadmin i en utredning med 1 besök får en avvikelse, och sedan skickar utlåtande, så misslyckas state att gå
           AVVIKELSE_MOTTAGEN --> UTLATANDE_SKICKAT, och fastnar på AVVIKELSE_MOTTAGEN
        */
        Utredning utr = buildBaseUtredning();
        utr.getExternForfragan()
                .map(ExternForfragan::getInternForfraganList)
                .map(iff -> iff.add(buildInternForfragan(buildForfraganSvar(SvarTyp.ACCEPTERA), LocalDateTime.now())));
        utr.setBestallning(buildBestallning(null));
        Intyg intyg = buildBestalltIntyg();
        intyg.setKomplettering(false);
        intyg.setSistaDatum(LocalDateTime.parse("2018-08-25T00:00"));
        intyg.setSkickatDatum(LocalDateTime.parse("2018-07-08T00:00")); // Sätts när vårdadmin klickar på "Registrera skickat utlåtande"
        utr.getIntygList().add(intyg);

        Handling h = buildHandling(null, LocalDateTime.now());
        h.setSkickatDatum(LocalDateTime.now());
        h.setUrsprung(HandlingUrsprungTyp.BESTALLNING);
        utr.getHandlingList().add(h);
        // 1 besök som har status avvikelse, med tillhörande händelse
        utr.getBesokList().add(aBesok()
                .withBesokStartTid(LocalDateTime.parse("2018-07-09T16:02"))
                .withBesokSlutTid(LocalDateTime.parse("2018-08-09T17:02"))
                .withBesokStatus(BesokStatusTyp.TIDBOKAD_VARDKONTAKT)
                .withTolkStatus(TolkStatusTyp.EJ_BOKAT)
                .withDeltagareProfession(DeltagarProfessionTyp.LK)
                .withErsatts(null)
                .withAvvikelse(Avvikelse.AvvikelseBuilder.anAvvikelse()
                        .withAvvikelseId(1L)
                        .withOrsakatAv(AvvikelseOrsak.VARDEN)
                        .withTidpunkt(LocalDateTime.parse("2018-07-11T16:02:28"))
                        .withInvanareUteblev(false)
                        .build())
                .withHandelseList(ImmutableList.of(
                        aHandelse()
                                .withId(15L)
                                .withSkapad(LocalDateTime.parse("2018-07-11T16:00:00"))
                                .withHandelseTyp(HandelseTyp.NYTT_BESOK)
                                .build(),
                        aHandelse()
                                .withId(16L)
                                .withSkapad(LocalDateTime.parse("2018-07-11T16:04:40"))
                                .withHandelseTyp(HandelseTyp.AVVIKELSE_MOTTAGEN)
                                .build()))
                .build());

        UtredningStatus status = testee.resolveStatus(utr);
        assertEquals(UtredningStatus.UTLATANDE_SKICKAT, status);
        assertEquals(UtredningFas.UTREDNING, status.getUtredningFas());
        assertEquals(Actor.FK, status.getNextActor());
    }

    @Test
    public void bugGetStuckInAvvikelseMottagenInsteadOfGoingToUtredningPagar() {
        /* Om FK rapporterar in en avvikelse (för patient) för ett inbokat möte, och vårdadmin sedan avbokar mötet,
           så är utredningen fortfarande kvar i state AVVIKELSE_MOTTAGEN istället för att gå till UTREDNING_PAGAR.
         */
        Utredning utr = buildBaseUtredning();
        utr.getExternForfragan()
                .map(ExternForfragan::getInternForfraganList)
                .map(iff -> iff.add(buildInternForfragan(buildForfraganSvar(SvarTyp.ACCEPTERA), LocalDateTime.now())));
        utr.setBestallning(buildBestallning(null));
        Intyg intyg = buildBestalltIntyg();
        intyg.setKomplettering(false);
        intyg.setSistaDatum(LocalDateTime.parse("2018-08-25T00:00"));
        utr.getIntygList().add(intyg);

        Handling h = buildHandling(null, LocalDateTime.now());
        h.setSkickatDatum(LocalDateTime.now());
        h.setUrsprung(HandlingUrsprungTyp.BESTALLNING);
        utr.getHandlingList().add(h);
        // 1 avvikelse, 1 händelse samt 1 avbokning av ett besök.
        utr.getBesokList().add(aBesok()
                .withBesokStartTid(LocalDateTime.parse("2018-07-09T16:02"))
                .withBesokSlutTid(LocalDateTime.parse("2018-08-09T17:02"))
                .withBesokStatus(BesokStatusTyp.INSTALLD_VARDKONTAKT)
                .withTolkStatus(TolkStatusTyp.EJ_BOKAT)
                .withDeltagareProfession(DeltagarProfessionTyp.LK)
                .withErsatts(null)
                .withAvvikelse(anAvvikelse()
                        .withAvvikelseId(1L)
                        .withOrsakatAv(AvvikelseOrsak.VARDEN)
                        .withTidpunkt(LocalDateTime.parse("2018-07-11T16:02:28"))
                        .withInvanareUteblev(false)
                        .build())
                .withHandelseList(ImmutableList.of(
                        aHandelse()
                                .withId(15L)
                                .withSkapad(LocalDateTime.parse("2018-07-11T16:00:00"))
                                .withHandelseTyp(HandelseTyp.NYTT_BESOK)
                                .build(),
                        aHandelse()
                                .withId(16L)
                                .withSkapad(LocalDateTime.parse("2018-07-11T16:04:40"))
                                .withHandelseTyp(HandelseTyp.AVVIKELSE_MOTTAGEN)
                                .build(),
                        aHandelse()
                                .withId(17L)
                                .withHandelseTyp(HandelseTyp.AVBOKAT_BESOK)
                                .withSkapad(LocalDateTime.parse("2018-07-11T16:10:40"))
                                .build()))
                .build());

        UtredningStatus status = testee.resolveStatus(utr);
        assertEquals(UtredningStatus.UTREDNING_PAGAR, status);
        assertEquals(UtredningFas.UTREDNING, status.getUtredningFas());
        assertEquals(Actor.UTREDARE, status.getNextActor());
    }

    @Test
    public void bugUtredningChangesStatusToAvslutadBeforeCronJob() {
        /* I slutet av en utredning så går utredningen till status AVSLUTAD trots att cronjobbet inte har
           processerat utredningen.
         */
        Utredning utr = anUtredning()
                .withUtredningId(1L)
                .withUtredningsTyp(UtredningsTyp.AFU)
                .withBestallning(aBestallning()
                    .withId(1L)
                    .withTilldeladVardenhetHsaId("IFV1239877878-1042")
                    .withOrderDatum(LocalDateTime.parse("2018-08-15T14:53:52.074"))
                    .build()
                )
                .withTolkBehov(false)
                .withArkiverad(false)
                .withExternForfragan(anExternForfragan()
                        .withId(1L)
                        .withLandstingHsaId("IFV1239877878-1041")
                        .withBesvarasSenastDatum(LocalDateTime.parse("2018-08-22T00:00"))
                        .withInkomDatum(LocalDateTime.parse("2018-08-15T14:53:17.374"))
                        .withInternForfraganList(ImmutableList.of(anInternForfragan()
                            .withId(1L)
                            .withVardenhetHsaId("IFV1239877878-1042")
                            .withTilldeladDatum(LocalDateTime.parse("2018-08-15T14:53:29.773"))
                            .withBesvarasSenastDatum(LocalDateTime.parse("2018-08-17T14:53:28.938"))
                            .withSkapadDatum(LocalDateTime.parse("2018-08-15T14:53:28.938"))
                            .withDirekttilldelad(true)
                            .withForfraganSvar(aForfraganSvar()
                                .withId(1L)
                                .withSvarTyp(SvarTyp.ACCEPTERA)
                                .withUtforareTyp(UtforareTyp.ENHET)
                                .withUtforareNamn("")
                                .withUtforareAdress("")
                                .withUtforarePostnr("")
                                .withUtforarePostort("")
                                .withUtforareTelefon(null)
                                .withUtforareEpost("")
                                .withKommentar(null)
                                .withBorjaDatum(null)
                                .build()
                            )
                            .withStatus(InternForfraganStatus.BESTALLD)
                            .build()
                        ))
                        .build()
                )
                .withHandelseList(ImmutableList.of(
                        aHandelse().withId(1L)
                                .withHandelseTyp(HandelseTyp.EXTERNFORFRAGAN_MOTTAGEN)
                                .withSkapad(LocalDateTime.parse("2018-08-15T14:53:17.388"))
                                .withHandelseText("Förfrågan mottagen av IFV1239877878-1041")
                                .build(),
                        aHandelse().withId(2L)
                                .withHandelseTyp(HandelseTyp.EXTERNFORFRAGAN_BESVARAD)
                                .withSkapad(LocalDateTime.parse("2018-08-15T14:53:29.773"))
                                .withAnvandare("Harald Alltsson")
                                .withHandelseText("Förfrågan accepterades av landstinget. Utredningen tilldelad till WebCert-Enhet1")
                                .build(),
                        aHandelse().withId(3L)
                                .withHandelseTyp(HandelseTyp.BESTALLNING_MOTTAGEN)
                                .withSkapad(LocalDateTime.parse("2018-08-15T14:53:52.076"))
                                .withAnvandare("FKASSA")
                                .withHandelseText("Beställning mottagen från FKASSA. Slutdatum: 2018-08-22")
                                .build(),
                        aHandelse().withId(4L)
                                .withHandelseTyp(HandelseTyp.HANDLING_MOTTAGEN)
                                .withSkapad(LocalDateTime.parse("2018-08-15T14:53:59.836"))
                                .withAnvandare("Harald Alltsson")
                                .withHandelseText("Handlingar mottagna 2018-08-15")
                                .build(),
                        aHandelse().withId(5L)
                                .withHandelseTyp(HandelseTyp.NYTT_BESOK)
                                .withSkapad(LocalDateTime.parse("2018-08-15T14:54:12.914"))
                                .withAnvandare("Harald Alltsson")
                                .withHandelseText("Besök bokat 2018-08-15 14:54 - 14:55 hos Läkare. Invånaren kallades 2018-08-01 per brevkontakt. Tolk bokad: Ej bokat ")
                                .withKommentar("")
                                .build(),
                        aHandelse().withId(6L)
                                .withHandelseTyp(HandelseTyp.REDOVISAT_BESOK)
                                .withSkapad(LocalDateTime.parse("2018-08-15T14:54:16.331"))
                                .withAnvandare("Harald Alltsson")
                                .withHandelseText("Besök 2018-08-15 14:54 redovisades som genomfört")
                                .build(),
                        aHandelse().withId(7L)
                                .withHandelseTyp(HandelseTyp.UTLATANDE_SKICKAT)
                                .withSkapad(LocalDateTime.parse("2018-08-15T14:54:19.227"))
                                .withAnvandare("Harald Alltsson")
                                .withHandelseText("Utlåtandet skickat 2018-08-15")
                                .build(),
                        aHandelse().withId(8L)
                                .withHandelseTyp(HandelseTyp.UTLATANDE_MOTTAGET)
                                .withSkapad(LocalDateTime.parse("2018-08-15T14:56:19.227"))
                                .withAnvandare("Försäkringskassan")
                                .withHandelseText("Utlåtandet mottaget av Försäkringskassan 2018-08-15")
                                .build()
                ))
                .withHandlingList(ImmutableList.of(
                        aHandling().withId(1L)
                                .withSkickatDatum(LocalDateTime.parse("2018-08-15T00:00"))
                                .withUrsprung(HandlingUrsprungTyp.BESTALLNING)
                                .build(),
                        aHandling().withId(2L)
                                .withSkickatDatum(LocalDateTime.parse("2018-08-15T14:53:59.834"))
                                .withInkomDatum(LocalDateTime.parse("2018-08-15T00:00"))
                                .withUrsprung(HandlingUrsprungTyp.UPPDATERING)
                                .build()
                ))
                .withBesokList(ImmutableList.of(
                        aBesok().withId(1L)
                                .withBesokStartTid(LocalDateTime.parse("2018-08-15T14:54"))
                                .withBesokSlutTid(LocalDateTime.parse("2018-08-15T14:55"))
                                .withKallelseDatum(LocalDateTime.parse("2018-08-01T00:00"))
                                .withBesokStatus(BesokStatusTyp.AVSLUTAD_VARDKONTAKT)
                                .withTolkStatus(TolkStatusTyp.EJ_BOKAT)
                                .withKallelseForm(KallelseFormTyp.BREVKONTAKT)
                                .withDeltagareProfession(DeltagarProfessionTyp.LK)
                                .withDeltagareFullstandigtNamn("")
                                .withHandelseList(ImmutableList.of(
                                        aHandelse().withId(5L)
                                                .withHandelseTyp(HandelseTyp.NYTT_BESOK)
                                                .withSkapad(LocalDateTime.parse("2018-08-15T14:54:12.914"))
                                                .withAnvandare("Harald Alltsson")
                                                .withHandelseText("Besök bokat 2018-08-15 14:54 - 14:55 hos Läkare. Invånaren kallades 2018-08-01 per brevkontakt. Tolk bokad: Ej bokat ")
                                                .withKommentar("")
                                                .build(),
                                        aHandelse().withId(6L)
                                                .withHandelseTyp(HandelseTyp.REDOVISAT_BESOK)
                                                .withSkapad(LocalDateTime.parse("2018-08-15T14:54:16.331"))
                                                .withAnvandare("Harald Alltsson")
                                                .withHandelseText("Besök 2018-08-15 14:54 redovisades som genomfört")
                                                .build()
                                ))
                                .build()
                ))
                .withIntygList(ImmutableList.of(
                        anIntyg().withId(1L)
                                .withKomplettering(false)
                                .withSistaDatum(LocalDateTime.parse("2018-08-22T00:00"))
                                .withSkickatDatum(LocalDateTime.parse("2018-08-15T00:00"))
                                .withMottagetDatum(LocalDateTime.parse("2018-08-15T00:00")) // Sätts vid ReportCertificateReceival av FK
                                .withSistaDatumKompletteringsbegaran(LocalDateTime.parse("2018-08-15T00:00"))
                                .withFragestallningMottagenDatum(null)
                                .build()
                ))
                .withAnteckningList(ImmutableList.<Anteckning>of())
                .withSkickadNotifieringList(ImmutableList.of(
                        aSkickadNotifiering().withId(1L)
                                .withIntygId(null)
                                .withTyp(NotifieringTyp.UTREDNING_TILLDELAD)
                                .withMottagare(NotifieringMottagarTyp.VARDENHET)
                                .withSkickad(LocalDateTime.parse("2018-08-15T14:53:29.808"))
                                .build(),
                        aSkickadNotifiering().withId(2L)
                                .withIntygId(null)
                                .withTyp(NotifieringTyp.NY_BESTALLNING)
                                .withMottagare(NotifieringMottagarTyp.VARDENHET)
                                .withSkickad(LocalDateTime.parse("2018-08-15T14:53:52.097"))
                                .build(),
                        aSkickadNotifiering().withId(3L)
                                .withIntygId(null)
                                .withTyp(NotifieringTyp.NY_BESTALLNING)
                                .withMottagare(NotifieringMottagarTyp.VARDENHET)
                                .withSkickad(LocalDateTime.parse("2018-08-15T14:53:59.852"))
                                .build(),
                        aSkickadNotifiering().withId(4L)
                                .withIntygId(null)
                                .withTyp(NotifieringTyp.PAMINNELSE_SLUTDATUM_UTREDNING_PASSERAS)
                                .withMottagare(NotifieringMottagarTyp.VARDENHET)
                                .withSkickad(LocalDateTime.parse("2018-08-15T14:54:00.428"))
                                .build()
                ))
                .withHandlaggare(aHandlaggare()
                        .withId(2L)
                        .withFullstandigtNamn("Handläggaren")
                        .withTelefonnummer("000111")
                        .withEmail("")
                        .withMyndighet("FKASSA")
                        .withKontor("Handläggarkontor")
                        .withKostnadsstalle("Handläggaradress 1")
                        .withPostnummer("00000")
                        .withStad("Handläggarstaden")
                        .build()
                )
                .withInvanare(anInvanare()
                        .withId(1L)
                        .withPersonId("191212121212")
                        .withFornamn("Tolvan")
                        .withMellannamn("")
                        .withEfternamn("Tolvansson")
                        .withSarskildaBehov("")
                        .withBakgrundNulage("")
                        .withPostort("Invånarstaden")
                        .withTidigareUtforare(ImmutableList.<TidigareUtforare>of())
                        .build()
                )
                .withStatus(UtredningStatus.UTLATANDE_SKICKAT) //Tidigare status, cachat
                .build();

        UtredningStatus status = testee.resolveStatus(utr);

        assertEquals(UtredningStatus.UTLATANDE_MOTTAGET, status);
        assertEquals(UtredningFas.UTREDNING, status.getUtredningFas());
        assertEquals(Actor.FK, status.getNextActor());
    }


    private Utredning buildBasicUtredningForKompletteringTest() {
        Utredning utr = buildBaseUtredning();
        utr.getExternForfragan().map(ExternForfragan::getInternForfraganList)
                .map(iff -> iff.add(buildInternForfragan(buildForfraganSvar(SvarTyp.ACCEPTERA), LocalDateTime.now())));
        utr.setBestallning(buildBestallning(null));
        Intyg intyg = buildBestalltIntyg();
        intyg.setSkickatDatum(LocalDateTime.now());
        intyg.setMottagetDatum(LocalDateTime.now());
        intyg.setSistaDatumKompletteringsbegaran(LocalDateTime.now().minusDays(1));
        utr.getIntygList().add(intyg);
        utr.getHandlingList().add(buildHandling(LocalDateTime.now(), null));
        utr.getBesokList().add(aBesok()
                .withBesokStatus(BesokStatusTyp.TIDBOKAD_VARDKONTAKT)
                .withDeltagareProfession(DeltagarProfessionTyp.FT)
                .withTolkStatus(TolkStatusTyp.BOKAT)
                .withErsatts(true)
                .build());
        return utr;
    }

    private Handling buildHandling(LocalDateTime inkomDatum, LocalDateTime skickadDatum) {
        Handling h = new Handling();
        h.setInkomDatum(inkomDatum);
        h.setSkickatDatum(skickadDatum);
        return h;
    }

    // Den post som skapas i samband med inkommen beställning. Dvs EJ komplettering.
    private Intyg buildBestalltIntyg() {
        return anIntyg()
                .withId(1L)
                .withKomplettering(false)
                .withSistaDatum(LocalDateTime.now().plusDays(25))
                .build();
    }

    // Den post som skapas i samband med inkommen beställning. Dvs EJ komplettering.
    private Intyg buildKompletteringIntyg() {
        return anIntyg()
                .withId(2L)
                .withKomplettering(true)
                .withSistaDatum(LocalDateTime.now().plusDays(25))
                .withSistaDatumKompletteringsbegaran(LocalDateTime.now().plusDays(35))
                .build();
    }

}
