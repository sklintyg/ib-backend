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
import se.inera.intyg.intygsbestallning.persistence.model.type.AvslutOrsak;
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
        utr.setAvbrutenOrsak(AvslutOrsak.UTREDNING_AVBRUTEN);
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
    /**
     * Om man står i utredningsfasen får uppdatering beställning MED handlingar, skall man efter det vara i UPPDATERAD_BESTALLNING_VANTAR_PA_HANDLINGAR,
     * även om man tidigare var i UTREDNING_PAGAR.
     * Se INTYG-6747.
     */
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
        utr.getBesokList().add(aBesok()
                .withBesokStatus(BesokStatusTyp.TIDBOKAD_VARDKONTAKT)
                .withDeltagareProfession(DeltagarProfessionTyp.FT)
                .build());
        UtredningStatus status = testee.resolveStatus(utr);
        assertEquals(UtredningStatus.UPPDATERAD_BESTALLNING_VANTAR_PA_HANDLINGAR, status);
        assertEquals(UtredningFas.UTREDNING, status.getUtredningFas());
        assertEquals(Actor.FK, status.getNextActor());
    }

    /**
     * Ett lite längre testflöde som verifiera att man hamnar i UPPDATERAD_BESTALLNING_VANTAR_PA_HANDLINGAR om man står i
     * HANDLINGAR_MOTTAGNA_BOKA_BESOK och får en uppdaterad beställning MED handlingar.
     * När man sedan sedan markerar handlingarna som mottagna, skall man backa tillbaka till HANDLINGAR_MOTTAGNA_BOKA_BESOK,
     * tills besök bokas. Då skall man vidare till UTREDNING_PAGAR.
     *
     * Se INTYG-6747.
     */
    @Test
    public void testUppdateradBestallningMedHandlingarMedOchUtanBesok() {
        Utredning utr = buildBaseUtredning();
        utr.getExternForfragan().map(ExternForfragan::getInternForfraganList)
                .map(iff -> iff.add(buildInternForfragan(buildForfraganSvar(SvarTyp.ACCEPTERA), LocalDateTime.now())));
        utr.setBestallning(buildBestallning(LocalDateTime.now()));
        utr.getIntygList().add(buildBestalltIntyg());
        utr.getHandlingList().clear();
        utr.getHandlingList().add(aHandling().withInkomDatum(LocalDateTime.now()).withUrsprung(HandlingUrsprungTyp.BESTALLNING).build());

        UtredningStatus status = testee.resolveStatus(utr);
        assertEquals(UtredningStatus.HANDLINGAR_MOTTAGNA_BOKA_BESOK, status);
        assertEquals(UtredningFas.UTREDNING, status.getUtredningFas());
        assertEquals(Actor.VARDADMIN, status.getNextActor());

        // Uppdatera beställning (med handlingar)
        utr.getBestallning().get().setUppdateradDatum(LocalDateTime.now());
        utr.getHandlingList().add(aHandling()
                .withUrsprung(HandlingUrsprungTyp.UPPDATERING)
                .withInkomDatum(null)
                .build());

        status = testee.resolveStatus(utr);
        assertEquals(UtredningStatus.UPPDATERAD_BESTALLNING_VANTAR_PA_HANDLINGAR, status);
        assertEquals(UtredningFas.UTREDNING, status.getUtredningFas());
        assertEquals(Actor.FK, status.getNextActor());

        // Markera alla handlingar mottagna...
        utr.getHandlingList().stream().forEach(handling -> handling.setInkomDatum(LocalDateTime.now()));

        // Skall vara tillbaka till HANDLINGAR_MOTTAGNA_BOKA_BESOK
        status = testee.resolveStatus(utr);
        assertEquals(UtredningStatus.HANDLINGAR_MOTTAGNA_BOKA_BESOK, status);
        assertEquals(UtredningFas.UTREDNING, status.getUtredningFas());
        assertEquals(Actor.VARDADMIN, status.getNextActor());

        // Lägg till ett besök...
        utr.getBesokList().add(aBesok()
                .withBesokStatus(BesokStatusTyp.TIDBOKAD_VARDKONTAKT)
                .withDeltagareProfession(DeltagarProfessionTyp.FT)
                .build());

        // Skall nu hoppa vidare till UTREDNING_PAGAR
        status = testee.resolveStatus(utr);
        assertEquals(UtredningStatus.UTREDNING_PAGAR, status);
        assertEquals(UtredningFas.UTREDNING, status.getUtredningFas());
        assertEquals(Actor.UTREDARE, status.getNextActor());

    }

    /**
     * Om man står i UTREDNING_PAGAR och gör en uppdatering UTAN handlingar, skall man även efter det vara kvar i UTREDNING_PAGAR.
     * Se INTYG-6747.
     */
    @Test
    public void testResolvesUtredningPagarWhenInUtredningPagarAndUppdateringUtanHandlingar() {
        Utredning utr = buildBaseUtredning();
        utr.getExternForfragan().map(ExternForfragan::getInternForfraganList)
                .map(iff -> iff.add(buildInternForfragan(buildForfraganSvar(SvarTyp.ACCEPTERA), LocalDateTime.now())));
        utr.setBestallning(buildBestallning(LocalDateTime.now()));
        utr.getIntygList().add(buildBestalltIntyg());
        utr.getHandlingList().clear();
        utr.getHandlingList().add(aHandling().withUrsprung(HandlingUrsprungTyp.BESTALLNING).build());
        utr.getHandlingList().add(aHandling()
                .withUrsprung(HandlingUrsprungTyp.UPPDATERING)
                .withInkomDatum(LocalDateTime.now())
                .build());
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
    public void testResolvesUtlatandeSkickatSistaDatumPassed() {
        // intyg sista datum should not affect utredningstatus
        Utredning utr = buildBaseUtredning();
        utr.getExternForfragan()
                .map(ExternForfragan::getInternForfraganList)
                .map(iff -> iff.add(buildInternForfragan(buildForfraganSvar(SvarTyp.ACCEPTERA), LocalDateTime.now())));
        utr.setBestallning(buildBestallning(null));
        Intyg intyg = buildBestalltIntyg();
        intyg.setSkickatDatum(LocalDateTime.now());
        intyg.setSistaDatum(LocalDateTime.now().minusDays(1));
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
        utr.setAvbrutenOrsak(AvslutOrsak.INGEN_KOMPLETTERING_BEGARD);
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
        utr.setAvbrutenOrsak(AvslutOrsak.INGEN_KOMPLETTERING_BEGARD);
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

        utr.setBesokList(ImmutableList.<Besok>of());

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
