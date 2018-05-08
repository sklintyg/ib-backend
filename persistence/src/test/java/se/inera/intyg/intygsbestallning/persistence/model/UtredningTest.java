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
package se.inera.intyg.intygsbestallning.persistence.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static se.inera.intyg.intygsbestallning.persistence.model.Anteckning.AnteckningBuilder.anAnteckning;
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
import static se.inera.intyg.intygsbestallning.persistence.model.TidigareUtforare.TidigareUtforareBuilder.aTidigareUtforare;
import static se.inera.intyg.intygsbestallning.persistence.model.Utredning.UtredningBuilder.anUtredning;

import com.google.common.collect.ImmutableList;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;
import se.inera.intyg.intygsbestallning.persistence.model.type.AvvikelseOrsak;
import se.inera.intyg.intygsbestallning.persistence.model.type.BesokStatusTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.DeltagarProfessionTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.EndReason;
import se.inera.intyg.intygsbestallning.persistence.model.type.HandelseTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.HandlingUrsprungTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.KallelseFormTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.SvarTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.TolkStatusTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.UtforareTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.UtredningsTyp;

import java.time.LocalDateTime;

public class UtredningTest {
    @Test
    public void equalsTest() {
        EqualsVerifier.forClass(Utredning.class).verify();
    }

    @Test
    public void copyTest() {

        final LocalDateTime datum = LocalDateTime.of(2018, 12, 12, 12, 12, 12, 12);

        final Utredning utredning = anUtredning()
                .withUtredningId("utredningsId")
                .withUtredningsTyp(UtredningsTyp.AFU)
                .withBestallning(aBestallning()
                        .withId(1)
                        .withTilldeladVardenhetHsaId("hsaId")
                        .withOrderDatum(datum)
                        .withUppdateradDatum(datum.plusDays(2))
                        .withSyfte("syfte")
                        .withPlaneradeAktiviteter("pingis")
                        .withKommentar("kommentar")
                        .build())
                .withTolkBehov(true)
                .withTolkSprak("sv")
                .withAvbrutenDatum(datum.plusDays(6))
                .withAvbrutenAnledning(EndReason.UTREDNING_AVBRUTEN)
                .withExternForfragan(anExternForfragan()
                        .withId(1)
                        .withLandstingHsaId("hsaId")
                        .withBesvarasSenastDatum(datum.plusMonths(3))
                        .withKommentar("kommentar")
                        .withAvvisatKommentar("kommentar")
                        .withAvvisatDatum(datum.plusMonths(4))
                        .withInkomDatum(datum)
                        .withDirekttilldelad(true)
                        .withInternForfraganList(ImmutableList.of(anInternForfragan()
                                .withId(1)
                                .withVardenhetHsaId("hsaId")
                                .withTilldeladDatum(datum)
                                .withBesvarasSenastDatum(datum.plusMonths(1))
                                .withSkapadDatum(datum)
                                .withKommentar("kommentar")
                                .withForfraganSvar(aForfraganSvar()
                                        .withId(1)
                                        .withSvarTyp(SvarTyp.ACCEPTERA)
                                        .withUtforareTyp(UtforareTyp.ENHET)
                                        .withUtforareNamn("namn")
                                        .withUtforareAdress("address")
                                        .withUtforarePostnr("postnummer")
                                        .withUtforarePostort("postort")
                                        .withUtforareTelefon("tele")
                                        .withUtforareEpost("epost")
                                        .withKommentar("kommentar")
                                        .withBorjaDatum(datum.toLocalDate())
                                        .build())
                                .build()))
                        .build())
                .withHandelseList(ImmutableList.of(aHandelse()
                        .withId(1)
                        .withHandelseTyp(HandelseTyp.FORFRAGAN_ACCEPTERAD)
                        .withSkapad(datum)
                        .withAnvandare("anvandare")
                        .withHandelseText("text")
                        .withKommentar("kommentar")
                        .build()))
                .withHandlingList(ImmutableList.of(aHandling()
                        .withId(1)
                        .withSkickatDatum(datum)
                        .withInkomDatum(datum)
                        .withUrsprung(HandlingUrsprungTyp.UPPDATERING)
                        .build()))
                .withBesokList(ImmutableList.of(aBesok()
                        .withId(1)
                        .withBesokStartTid(datum)
                        .withBesokSlutTid(datum.plusHours(1))
                        .withKallelseDatum(datum)
                        .withBesokStatus(BesokStatusTyp.TIDBOKAD_VARDKONTAKT)
                        .withTolkStatus(TolkStatusTyp.BOKAT)
                        .withKallelseForm(KallelseFormTyp.BREVKONTAKT)
                        .withErsatts(true)
                        .withDeltagareProfession(DeltagarProfessionTyp.AT)
                        .withDeltagareFullstandigtNamn("namn")
                        .withAvvikelse(anAvvikelse()
                                .withAvvikelseId("id")
                                .withOrsakatAv(AvvikelseOrsak.PATIENT)
                                .withBeskrivning("beskrivning")
                                .withTidpunkt(datum)
                                .withInvanareUteblev(true)
                                .build())
                        .build()))
                .withIntygList(ImmutableList.of(anIntyg()
                        .withId(1)
                        .withKompletteringsId("id")
                        .withSistaDatum(datum.plusMonths(1))
                        .withMottagetDatum(datum)
                        .withSistaDatumKompletteringsbegaran(datum.plusMonths(2))
                        .build()))
                .withHandlaggare(aHandlaggare()
                        .withId(1)
                        .withFullstandigtNamn("namn")
                        .withTelefonnummer("tele")
                        .withEmail("mail")
                        .withMyndighet("mynd")
                        .withKontor("kont")
                        .withKostnadsstalle("kost")
                        .withAdress("adress")
                        .withPostkod("kod")
                        .withStad("staden")
                        .build())
                .withInvanare(anInvanare()
                        .withId(1)
                        .withPersonId("id")
                        .withSarskildaBehov("hiss")
                        .withBakgrundNulage("dåligt")
                        .withPostkod("postkod")
                        .withTidigareUtforare(ImmutableList.of(aTidigareUtforare()
                                .withId(1)
                                .withTidigareEnhetId("id")
                                .build()))
                        .build())
                .withAnteckningList(ImmutableList.of(anAnteckning()
                        .withAnvandare("användare")
                        .withSkapat(datum)
                        .withText("text")
                        .withVardenhetHsaId("id")
                        .build()))
                .build();

        final Utredning copy = Utredning.copyFrom(utredning);

        assertEquals(utredning, copy);
    }
}