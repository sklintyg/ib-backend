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

import static se.inera.intyg.intygsbestallning.persistence.model.Anteckning.AnteckningBuilder.anAnteckning;
import static se.inera.intyg.intygsbestallning.persistence.model.Avvikelse.AvvikelseBuilder.anAvvikelse;
import static se.inera.intyg.intygsbestallning.persistence.model.Bestallning.BestallningBuilder.aBestallning;
import static se.inera.intyg.intygsbestallning.persistence.model.BestallningHistorik.BestallningHistorikBuilder.aBestallningHistorik;
import static se.inera.intyg.intygsbestallning.persistence.model.Betalning.BetalningBuilder.aBetalning;
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

import com.google.common.collect.Lists;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import se.inera.intyg.intygsbestallning.persistence.model.Anteckning;
import se.inera.intyg.intygsbestallning.persistence.model.Besok;
import se.inera.intyg.intygsbestallning.persistence.model.Bestallning;
import se.inera.intyg.intygsbestallning.persistence.model.Betalning;
import se.inera.intyg.intygsbestallning.persistence.model.ExternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.ForfraganSvar;
import se.inera.intyg.intygsbestallning.persistence.model.Handelse;
import se.inera.intyg.intygsbestallning.persistence.model.Handlaggare;
import se.inera.intyg.intygsbestallning.persistence.model.Handling;
import se.inera.intyg.intygsbestallning.persistence.model.InternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.Intyg;
import se.inera.intyg.intygsbestallning.persistence.model.Invanare;
import se.inera.intyg.intygsbestallning.persistence.model.RegistreradVardenhet;
import se.inera.intyg.intygsbestallning.persistence.model.TidigareUtforare;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.type.AvslutOrsak;
import se.inera.intyg.intygsbestallning.persistence.model.type.AvvikelseOrsak;
import se.inera.intyg.intygsbestallning.persistence.model.type.BesokStatusTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.DeltagarProfessionTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.HandelseTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.HandlingUrsprungTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.KallelseFormTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.RegiFormTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.SvarTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.TolkStatusTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.UtforareTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.UtredningsTyp;

public final class TestDataFactory {

    public static final String VE_HSA_ID = "enhet-1";
    public static final String VE_ORGNR = "orgnr-1";
    public static final String VG_HSA_ID = "vg-1";

    private TestDataFactory() {
    }

    public static Invanare buildInvanare() {
        List<TidigareUtforare> tidigareUtforareList = Lists.newArrayList();
        tidigareUtforareList.add(buildTidigareUtforare());

        return anInvanare()
                .withBakgrundNulage("bakgrund")
                .withPersonId("personId")
                .withPostort("postort")
                .withSarskildaBehov("behov")
                .withTidigareUtforare(tidigareUtforareList)
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
                .withPostnummer("12345")
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
        final InternForfragan internForfragan = anInternForfragan()
                .withForfraganSvar(buildForfraganSvar())
                .withVardenhetHsaId(VE_HSA_ID)
                .withBesvarasSenastDatum(LocalDateTime.now())
                .withKommentar("kommentar")
                .withTilldeladDatum(LocalDateTime.now())
                .withSkapadDatum(LocalDateTime.now())
                .build();

        List<InternForfragan> internForfraganList = Lists.newArrayList(internForfragan);

        return anExternForfragan()
                .withLandstingHsaId(VG_HSA_ID)
                .withBesvarasSenastDatum(LocalDateTime.now())
                .withAvvisatDatum(LocalDateTime.now())
                .withAvvisatKommentar("avvisatKommentar")
                .withKommentar("kommentar")
                .withInkomDatum(LocalDateTime.now())
                .withInternForfraganList(internForfraganList)
                .build();
    }

    public static Bestallning buildBestallning() {
        LocalDateTime orderDate = LocalDateTime.now();
        return aBestallning()
                .withBestallningHistorik(
                        Lists.newArrayList(
                                aBestallningHistorik()
                                        .withDatum(orderDate)
                                        .withKommentar("kommentar")
                                        .build()))
                .withOrderDatum(orderDate)
                .withPlaneradeAktiviteter("aktiviteter")
                .withSyfte("syfte")
                .withTilldeladVardenhetHsaId(VE_HSA_ID)
                .withTilldeladVardenhetOrgNr(VE_ORGNR)
                .build();
    }

    public static Handelse buildHandelse() {
        return aHandelse()
                .withAnvandare("Kotte Korv")
                .withHandelseText("Utredning skapades")
                .withHandelseTyp(HandelseTyp.EXTERNFORFRAGAN_MOTTAGEN)
                .withSkapad(LocalDateTime.now())
                .withKommentar("Detta är en kommentar")
                .build();
    }

    public static Utredning buildUtredning() {
        return anUtredning()
                .withUtredningsTyp(UtredningsTyp.AFU)
                .withTolkBehov(false)
                .withAvbrutenOrsak(AvslutOrsak.JAV)
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
                .withTolkStatus(TolkStatusTyp.EJ_BOKAT)
                .withAvvikelse(anAvvikelse()
                        .withTidpunkt(LocalDateTime.now())
                        .withOrsakatAv(AvvikelseOrsak.PATIENT)
                        .withInvanareUteblev(true)
                        .withBeskrivning("avvikelseBeskrivning")
                        .build())
                .build();
    }

    public static RegistreradVardenhet buildRegistreradVardenhet() {
        return RegistreradVardenhet.RegistreradVardenhetBuilder.aRegistreradVardenhet()
                .withVardenhetHsaId("ve-1")
                .withVardenhetNamn("Enhet 1")
                .withVardenhetRegiForm(RegiFormTyp.EGET_LANDSTING)
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
                .withKomplettering(true)
                .withSkickatDatum(LocalDateTime.now())
                .build();
    }

    public static Betalning buildBetalning() {
        return aBetalning()
                .withBetalningsDatum(LocalDateTime.now())
                .withBetaldVeId("betald-ve-id")
                .withFakturaVeId("faktura-ve-id")
                .withBetaldFkId("betald-fk-id")
                .withFakturaFkId("faktura-fk-id")
                .build();
    }
}
