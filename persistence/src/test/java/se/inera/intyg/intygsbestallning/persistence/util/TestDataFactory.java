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
package se.inera.intyg.intygsbestallning.persistence.util;

import com.google.common.collect.ImmutableList;
import se.inera.intyg.intygsbestallning.persistence.model.Anteckning;
import se.inera.intyg.intygsbestallning.persistence.model.Besok;
import se.inera.intyg.intygsbestallning.persistence.model.Bestallning;
import se.inera.intyg.intygsbestallning.persistence.model.ExternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.ForfraganSvar;
import se.inera.intyg.intygsbestallning.persistence.model.Handelse;
import se.inera.intyg.intygsbestallning.persistence.model.Handlaggare;
import se.inera.intyg.intygsbestallning.persistence.model.Handling;
import se.inera.intyg.intygsbestallning.persistence.model.Intyg;
import se.inera.intyg.intygsbestallning.persistence.model.Invanare;
import se.inera.intyg.intygsbestallning.persistence.model.RegistreradVardenhet;
import se.inera.intyg.intygsbestallning.persistence.model.TidigareUtforare;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.type.AvvikelseOrsak;
import se.inera.intyg.intygsbestallning.persistence.model.type.BesokStatusTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.DeltagarProfessionTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.EndReason;
import se.inera.intyg.intygsbestallning.persistence.model.type.HandelseTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.HandlingUrsprungTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.KallelseFormTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.RegiFormTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.SvarTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.UtforareTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.UtredningsTyp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

import static se.inera.intyg.intygsbestallning.persistence.model.Anteckning.AnteckningBuilder.anAnteckning;
import static se.inera.intyg.intygsbestallning.persistence.model.Avvikelse.AvvikelseBuilder.anAvvikelse;
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

public final class TestDataFactory {

    public static final String VE_HSA_ID = "enhet-1";
    public static final String VG_HSA_ID = "vg-1";
    public static final String UTREDNING_ID = "abc-123";

    private TestDataFactory() {
    }

    public static Invanare buildInvanare() {
        return anInvanare()
                .withBakgrundNulage("bakgrund")
                .withPersonId("personId")
                .withPostort("postort")
                .withSarskildaBehov("behov")
                .withTidigareUtforare(Collections.singletonList(buildTidigareUtforare()))
                .build();
    }

    public static TidigareUtforare buildTidigareUtforare() {
        return aTidigareUtforare()
                .withTidigareEnhetId(VE_HSA_ID)
                .build();
    }

    public static Handlaggare buildHandlaggare() {
        return aHandlaggare()
                .withAdress("adress")
                .withMyndighet("authority")
                .withEmail("email")
                .withFullstandigtNamn("fullstandigtNamn")
                .withKontor("kontor")
                .withKostnadsstalle("kontorCostCenter")
                .withPostkod("postkod")
                .withStad("stad")
                .withTelefonnummer("telefonnummer")
                .build();
    }

    public static Handling buildHandling() {
        return aHandling()
                .withInkomDatum(LocalDateTime.now())
                .withSkickatDatum(LocalDateTime.now())
                .withUrsprung(HandlingUrsprungTyp.BESTALLNING)
                .build();
    }

    public static ExternForfragan buildExternForfragan() {
        return anExternForfragan()
                .withLandstingHsaId(VG_HSA_ID)
                .withBesvarasSenastDatum(LocalDateTime.now())
                .withAvvisatDatum(LocalDateTime.now())
                .withAvvisatKommentar("avvisatKommentar")
                .withKommentar("kommentar")
                .withInkomDatum(LocalDateTime.now())
                .withInternForfraganList(ImmutableList.of(
                        anInternForfragan()
                                .withForfraganSvar(buildForfraganSvar())
                                .withVardenhetHsaId(VE_HSA_ID)
                                .withBesvarasSenastDatum(LocalDateTime.now())
                                .withKommentar("kommentar")
                                .withTilldeladDatum(LocalDateTime.now())
                                .withSkapadDatum(LocalDateTime.now())
                                .build()))
                .build();
    }

    public static Bestallning buildBestallning() {
        return aBestallning()
                .withKommentar("kommentar")
                .withOrderDatum(LocalDateTime.now())
                .withPlaneradeAktiviteter("aktiviteter")
                .withSyfte("syfte")
                .withTilldeladVardenhetHsaId(VE_HSA_ID)
                .build();
    }

    public static Handelse buildHandelse() {
        return aHandelse()
                .withAnvandare("Kotte Korv")
                .withHandelseText("Utredning skapades")
                .withHandelseTyp(HandelseTyp.FORFRAGAN_MOTTAGEN)
                .withSkapad(LocalDateTime.now())
                .withKommentar("Detta är en kommentar")
                .build();
    }

    public static Utredning buildUtredning() {
        return anUtredning()
                .withUtredningId(UTREDNING_ID)
                .withUtredningsTyp(UtredningsTyp.AFU)
                .withTolkBehov(false)
                .withAvbrutenAnledning(EndReason.JAV)
                .withAvbrutenDatum(LocalDateTime.now())
                .withArkiverad(false)
                .build();
    }

    public static ForfraganSvar buildForfraganSvar() {
        return aForfraganSvar()
                .withSvarTyp(SvarTyp.ACCEPTERA)
                .withUtforareNamn("Utförarenheten")
                .withUtforareAdress("Utförarvägen 1")
                .withUtforarePostnr("12345")
                .withUtforarePostort("Utförhult")
                .withUtforareEpost("utforare@inera.se")
                .withUtforareTelefon("123-123412")
                .withKommentar("Bered skyndsamt!")
                .withUtforareTyp(UtforareTyp.ENHET)
                .withBorjaDatum(LocalDate.now())
                .build();
    }

    public static Besok buildBesok() {
        return Besok.BesokBuilder.aBesok()
                .withBesokStatus(BesokStatusTyp.TIDBOKAD_VARDKONTAKT)
                .withBesokStartTid(LocalDateTime.now().plusDays(5))
                .withBesokSlutTid(LocalDateTime.now().plusDays(5).plusHours(2))
                .withKallelseForm(KallelseFormTyp.TELEFONKONTAKT)
                .withKallelseDatum(LocalDateTime.now())
                .withDeltagareProfession(DeltagarProfessionTyp.FT)
                .withDeltagareFullstandigtNamn("Håkan Fysiosson")
                .withAvvikelse(anAvvikelse()
                        .withTidpunkt(LocalDateTime.now())
                        .withOrsakatAv(AvvikelseOrsak.PATIENT)
                        .withInvanareUteblev(true)
                        .withBeskrivning("avvikelseBeskrivning")
                        .withAvvikelseId("avvikelseId")
                        .build())
                .build();
    }

    public static RegistreradVardenhet buildRegistreradVardenhet() {
        return RegistreradVardenhet.RegistreradVardenhetBuilder.aRegistreradVardenhet()
                .withVardenhetHsaId("ve-1")
                .withVardenhetNamn("Enhet 1")
                .withVardenhetRegiForm(RegiFormTyp.LANDSTING)
                .withVardenhetVardgivareHsaId("vg-1")
                .withVardgivareHsaId("vg-1")
                .build();
    }

    public static Anteckning buildAnteckning() {
        return anAnteckning()
                .withAnvandare("anvandare")
                .withSkapat(LocalDateTime.now())
                .withText("text")
                .withVardenhetHsaId("anteckningVardenhetHsaId")
                .build();
    }

    public static Intyg buildIntyg() {
        return anIntyg()
                .withSistaDatumKompletteringsbegaran(LocalDateTime.now())
                .withSistaDatum(LocalDateTime.now())
                .withMottagetDatum(LocalDateTime.now())
                .withKompletteringsId("kompletteringsId")
                .withSkickatDatum(LocalDateTime.now())
                .build();
    }
}
