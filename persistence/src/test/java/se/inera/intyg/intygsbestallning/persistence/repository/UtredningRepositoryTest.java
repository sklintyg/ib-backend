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
package se.inera.intyg.intygsbestallning.persistence.repository;

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;
import se.inera.intyg.intygsbestallning.persistence.config.PersistenceConfigDev;
import se.inera.intyg.intygsbestallning.persistence.config.PersistenceConfigTest;
import se.inera.intyg.intygsbestallning.persistence.model.Besok;
import se.inera.intyg.intygsbestallning.persistence.model.type.BesokStatusTyp;
import se.inera.intyg.intygsbestallning.persistence.model.Bestallning;
import se.inera.intyg.intygsbestallning.persistence.model.type.DeltagarProfessionTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.EndReason;
import se.inera.intyg.intygsbestallning.persistence.model.ExternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.ForfraganSvar;
import se.inera.intyg.intygsbestallning.persistence.model.Handelse;
import se.inera.intyg.intygsbestallning.persistence.model.type.HandelseTyp;
import se.inera.intyg.intygsbestallning.persistence.model.Handlaggare;
import se.inera.intyg.intygsbestallning.persistence.model.Handling;
import se.inera.intyg.intygsbestallning.persistence.model.type.HandlingUrsprungTyp;
import se.inera.intyg.intygsbestallning.persistence.model.InternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.Invanare;
import se.inera.intyg.intygsbestallning.persistence.model.type.KallelseFormTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.SvarTyp;
import se.inera.intyg.intygsbestallning.persistence.model.TidigareUtforare;
import se.inera.intyg.intygsbestallning.persistence.model.type.UtforareTyp;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.type.UtredningsTyp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static se.inera.intyg.intygsbestallning.persistence.model.Bestallning.BestallningBuilder.aBestallning;
import static se.inera.intyg.intygsbestallning.persistence.model.ExternForfragan.ExternForfraganBuilder.anExternForfragan;
import static se.inera.intyg.intygsbestallning.persistence.model.ForfraganSvar.ForfraganSvarBuilder.aForfraganSvar;
import static se.inera.intyg.intygsbestallning.persistence.model.Handelse.HandelseBuilder.aHandelse;
import static se.inera.intyg.intygsbestallning.persistence.model.Handlaggare.HandlaggareBuilder.aHandlaggare;
import static se.inera.intyg.intygsbestallning.persistence.model.Handling.HandlingBuilder.aHandling;
import static se.inera.intyg.intygsbestallning.persistence.model.InternForfragan.InternForfraganBuilder.anInternForfragan;
import static se.inera.intyg.intygsbestallning.persistence.model.Invanare.InvanareBuilder.anInvanare;
import static se.inera.intyg.intygsbestallning.persistence.model.TidigareUtforare.TidigareUtforareBuilder.aTidigareUtforare;
import static se.inera.intyg.intygsbestallning.persistence.model.Utredning.UtredningBuilder.anUtredning;

/**
 * Created by eriklupander on 2015-08-05.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { PersistenceConfigTest.class, PersistenceConfigDev.class })
@ActiveProfiles({ "dev" })
@Transactional
public class UtredningRepositoryTest {

    private static final String VE_HSA_ID = "enhet-1";
    private static final String VG_HSA_ID = "vg-1";
    private static final String UTREDNING_ID = "abc-123";

    @Autowired
    private UtredningRepository utredningRepository;

    @Before
    public void before() {
        utredningRepository.deleteAll();
    }

    @Test
    public void testBuildPersistAndReadFullGraph() {
        // Create and save a base Utredning
        Utredning utr = buildUtredning();

        utr.setBestallning(buildBestallning());
        utr.setExternForfragan(buildExternForfragan());

        utr.getHandelseList().add(buildHandelse());
        utr.getHandelseList().add(buildHandelse());

        utr.getHandlingList().add(buildHandling());
        utr.getHandlingList().add(buildHandling());

        utr.setHandlaggare(buildHandlaggare());

        utr.setInvanare(buildInvanare());

        utr.getBesokList().add(buildBesok());

        utredningRepository.save(utr);

        Optional<Utredning> saved = utredningRepository.findById(UTREDNING_ID);
        assertTrue(saved.isPresent());
        Utredning utredning = saved.get();

        assertEquals(UTREDNING_ID, utredning.getUtredningId());
        assertEquals(UtredningsTyp.AFU, utredning.getUtredningsTyp());
        assertFalse(utredning.getTolkBehov());
        assertNotNull(utredning.getAvbrutenDatum());
        assertEquals(EndReason.JAV, utredning.getAvbrutenAnledning());

        Bestallning bestallning = utredning.getBestallning().orElse(null);
        assertNotNull(bestallning);
//        assertNotNull(bestallning.getIntygKlartSenast());
        assertEquals("kommentar", bestallning.getKommentar());
        assertEquals("aktiviteter", bestallning.getPlaneradeAktiviteter());
        assertEquals("syfte", bestallning.getSyfte());
        assertEquals(VE_HSA_ID, bestallning.getTilldeladVardenhetHsaId());

        ExternForfragan externForfragan = utredning.getExternForfragan();
        assertNotNull(externForfragan);
        assertEquals("avvisatKommentar", externForfragan.getAvvisatKommentar());
        assertEquals("kommentar", externForfragan.getKommentar());
        assertEquals(VG_HSA_ID, externForfragan.getLandstingHsaId());
        assertNotNull(externForfragan.getAvvisatDatum());
        assertNotNull(externForfragan.getBesvarasSenastDatum());
        assertEquals(1, externForfragan.getInternForfraganList().size());
        assertNotNull(externForfragan.getInkomDatum());

        InternForfragan internForfragan = externForfragan.getInternForfraganList().get(0);
        assertNotNull(internForfragan);
        assertEquals("kommentar", internForfragan.getKommentar());
        assertNotNull(internForfragan.getBesvarasSenastDatum());
        assertNotNull(internForfragan.getTilldeladDatum());
        assertNotNull(internForfragan.getSkapadDatum());
        assertNotNull(VE_HSA_ID, internForfragan.getVardenhetHsaId());

        ForfraganSvar forfraganSvar = internForfragan.getForfraganSvar();
        assertNotNull(forfraganSvar);
        assertNotNull(forfraganSvar.getBorjaDatum());
        assertEquals("Bered skyndsamt!", forfraganSvar.getKommentar());
        assertEquals("Utförarvägen 1", forfraganSvar.getUtforareAdress());
        assertEquals("utforare@inera.se", forfraganSvar.getUtforareEpost());
        assertEquals("Utförarenheten", forfraganSvar.getUtforareNamn());
        assertEquals("12345", forfraganSvar.getUtforarePostnr());
        assertEquals("Utförhult", forfraganSvar.getUtforarePostort());
        assertEquals("123-123412", forfraganSvar.getUtforareTelefon());
        assertEquals(UtforareTyp.ENHET, forfraganSvar.getUtforareTyp());
        assertEquals(SvarTyp.ACCEPTERA, forfraganSvar.getSvarTyp());

        assertEquals(2, utredning.getHandelseList().size());
        Handelse handelse = utredning.getHandelseList().get(0);
        assertEquals("Kotte Korv", handelse.getAnvandare());
        assertEquals("Utredning skapades", handelse.getHandelseText());
        assertEquals("Detta är en kommentar", handelse.getKommentar());
        assertEquals(HandelseTyp.FORFRAGAN_MOTTAGEN, handelse.getHandelseTyp());
        assertNotNull(handelse.getSkapad());

        assertEquals(2, utredning.getHandlingList().size());
        Handling handling = utredning.getHandlingList().get(0);
        assertNotNull(handling.getInkomDatum());
        assertNotNull(handling.getSkickatDatum());
        assertEquals(HandlingUrsprungTyp.BESTALLNING, handling.getUrsprung());

        Handlaggare handlaggare = utredning.getHandlaggare();
        assertNotNull(handlaggare);
        assertEquals("adress", handlaggare.getAdress());
        assertEquals("authority", handlaggare.getMyndighet());
        assertEquals("email", handlaggare.getEmail());
        assertEquals("fullstandigtNamn", handlaggare.getFullstandigtNamn());
        assertEquals("kontor", handlaggare.getKontor());
        assertEquals("kontorCostCenter", handlaggare.getKostnadsstalle());
        assertEquals("postkod", handlaggare.getPostkod());
        assertEquals("stad", handlaggare.getStad());
        assertEquals("telefonnummer", handlaggare.getTelefonnummer());

        Invanare invanare = utredning.getInvanare();
        assertNotNull(invanare);
        assertEquals("bakgrund", invanare.getBakgrundNulage());
        assertEquals("personId", invanare.getPersonId());
        assertEquals("postkod", invanare.getPostkod());
        assertEquals("behov", invanare.getSarskildaBehov());

        assertEquals(1, invanare.getTidigareUtforare().size());
        TidigareUtforare tu = invanare.getTidigareUtforare().get(0);
        assertEquals(VE_HSA_ID, tu.getTidigareEnhetId());

        Besok besok = utredning.getBesokList().get(0);
        assertEquals(BesokStatusTyp.TIDBOKAD_VARDKONTAKT, besok.getBesokStatus());
        assertEquals(DeltagarProfessionTyp.FT, besok.getDeltagareProfession());
        assertEquals(KallelseFormTyp.TELEFONKONTAKT, besok.getKallelseForm());
        assertNotNull(besok.getBesokTid());
        assertNull(besok.getTolkStatus());
        assertNull(besok.getErsatts());

    }



    @Test
    public void testFindInternalForfraganByVardenhet() {

        Utredning utr = buildUtredning();
        utr.setBestallning(buildBestallning());
        utr.setExternForfragan(buildExternForfragan());

        List<Utredning> response = utredningRepository.findAllByExternForfragan_InternForfraganList_VardenhetHsaId(VE_HSA_ID);
        assertNotNull(response);
        assertTrue(response.isEmpty());
        utredningRepository.save(utr);
        response = utredningRepository.findAllByExternForfragan_InternForfraganList_VardenhetHsaId(VE_HSA_ID);
        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    public void testFindAllByLandstingHsaId() {
        Utredning utr = buildUtredning();
        utr.setBestallning(buildBestallning());
        utr.setExternForfragan(buildExternForfragan());

        List<Utredning> response = utredningRepository.findAllByExternForfragan_LandstingHsaId(VG_HSA_ID);
        assertNotNull(response);
        assertTrue(response.isEmpty());
        utredningRepository.save(utr);
        response = utredningRepository.findAllByExternForfragan_LandstingHsaId(VG_HSA_ID);
        assertNotNull(response);
        assertEquals(1, response.size());

    }

    @Test
    public void testFindAllWithBestallningForVardenhetHsaId() {
        Utredning utr = buildUtredning();
        utr.setBestallning(buildBestallning());
        utredningRepository.save(utr);

        List<Utredning> resultList = utredningRepository.findAllByBestallning_TilldeladVardenhetHsaId(VE_HSA_ID);
        assertEquals(1, resultList.size());
        assertNotNull(resultList.get(0).getBestallning());
    }

    private Invanare buildInvanare() {
        return anInvanare()
                .withBakgrundNulage("bakgrund")
                .withPersonId("personId")
                .withPostkod("postkod")
                .withSarskildaBehov("behov")
                .withTidigareUtforare(Collections.singletonList(buildTidigareUtforare()))
                .build();
    }

    private TidigareUtforare buildTidigareUtforare() {
        return aTidigareUtforare()
                .withTidigareEnhetId(VE_HSA_ID)
                .build();
    }

    private Handlaggare buildHandlaggare() {
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

    private Handling buildHandling() {
        return aHandling()
                .withInkomDatum(LocalDateTime.now())
                .withSkickatDatum(LocalDateTime.now())
                .withUrsprung(HandlingUrsprungTyp.BESTALLNING)
                .build();
    }

    private ExternForfragan buildExternForfragan() {
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

    private Bestallning buildBestallning() {
        return aBestallning()
//                .withIntygKlartSenast(LocalDateTime.now())
                .withKommentar("kommentar")
                .withOrderDatum(LocalDateTime.now())
                .withPlaneradeAktiviteter("aktiviteter")
                .withSyfte("syfte")
                .withTilldeladVardenhetHsaId(VE_HSA_ID)
                .build();
    }

    private Handelse buildHandelse() {
        return aHandelse()
                .withAnvandare("Kotte Korv")
                .withHandelseText("Utredning skapades")
                .withHandelseTyp(HandelseTyp.FORFRAGAN_MOTTAGEN)
                .withSkapad(LocalDateTime.now())
                .withKommentar("Detta är en kommentar")
                .build();
    }

    private Utredning buildUtredning() {
        return anUtredning()
                .withUtredningId(UTREDNING_ID)
                .withUtredningsTyp(UtredningsTyp.AFU)
                .withTolkBehov(false)
                .withAvbrutenAnledning(EndReason.JAV)
                .withAvbrutenDatum(LocalDateTime.now())
                .withArkiverad(true)
                .build();
    }

    private ForfraganSvar buildForfraganSvar() {
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

    private Besok buildBesok() {
        return Besok.BesokBuilder.aBesok()
                .withBesokStatus(BesokStatusTyp.TIDBOKAD_VARDKONTAKT)
                .withBesokTid(LocalDateTime.now().plusDays(5))
                .withKallelseForm(KallelseFormTyp.TELEFONKONTAKT)
                .withKallelseDatum(LocalDateTime.now())
                .withDeltagareProfession(DeltagarProfessionTyp.FT)
                .withDeltagareFullstandigtNamn("Håkan Fysiosson")
                .build();
    }
}
