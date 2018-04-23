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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;
import se.inera.intyg.intygsbestallning.common.integration.json.CustomObjectMapper;
import se.inera.intyg.intygsbestallning.persistence.config.PersistenceConfigDev;
import se.inera.intyg.intygsbestallning.persistence.config.PersistenceConfigTest;
import se.inera.intyg.intygsbestallning.persistence.model.Bestallning;
import se.inera.intyg.intygsbestallning.persistence.model.ExternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.ForfraganSvar;
import se.inera.intyg.intygsbestallning.persistence.model.Handelse;
import se.inera.intyg.intygsbestallning.persistence.model.HandelseTyp;
import se.inera.intyg.intygsbestallning.persistence.model.Handlaggare;
import se.inera.intyg.intygsbestallning.persistence.model.Handling;
import se.inera.intyg.intygsbestallning.persistence.model.InternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.Invanare;
import se.inera.intyg.intygsbestallning.persistence.model.SvarTyp;
import se.inera.intyg.intygsbestallning.persistence.model.TidigareUtforare;
import se.inera.intyg.intygsbestallning.persistence.model.UtforareTyp;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.UtredningsTyp;

import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by eriklupander on 2015-08-05.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { PersistenceConfigTest.class, PersistenceConfigDev.class })
@ActiveProfiles({ "dev" })
@Transactional
public class UtredningRepositoryTest {

    private static final Logger LOG = LoggerFactory.getLogger(UtredningRepositoryTest.class);

    private static final String VE_HSA_ID = "enhet-1";
    private static final String VG_HSA_ID = "vg-1";
    private static final String UTREDNING_ID = "abc-123";

    @Autowired
    private UtredningRepository utredningRepository;

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

        utredningRepository.save(utr);

        Optional<Utredning> saved = utredningRepository.findById(UTREDNING_ID);
        assertTrue(saved.isPresent());
        Utredning utredning = saved.get();

        assertEquals(UTREDNING_ID, utredning.getUtredningId());
        assertEquals(UtredningsTyp.AFU, utredning.getUtredningsTyp());
        assertEquals("sv", utredning.getSprakTolk());

        Bestallning bestallning = utredning.getBestallning();
        assertNotNull(bestallning);
        assertNotNull(bestallning.getIntygKlartSenast());
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

        InternForfragan internForfragan = externForfragan.getInternForfraganList().get(0);
        assertNotNull(internForfragan);
        assertEquals("kommentar", internForfragan.getKommentar());
        assertNotNull(internForfragan.getBesvarasSenastDatum());
        assertNotNull(internForfragan.getTilldeladDatum());
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

        Handlaggare handlaggare = utredning.getHandlaggare();
        assertNotNull(handlaggare);
        assertEquals("adress", handlaggare.getAdress());
        assertEquals("authority", handlaggare.getAuthority());
        assertEquals("email", handlaggare.getEmail());
        assertEquals("fullstandigtNamn", handlaggare.getFullstandigtNamn());
        assertEquals("kontor", handlaggare.getKontor());
        assertEquals("kontorCostCenter", handlaggare.getKontorCostCenter());
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
    }

    private Invanare buildInvanare() {
        Invanare invanare = new Invanare();
        invanare.setBakgrundNulage("bakgrund");
        invanare.setPersonId("personId");
        invanare.setPostkod("postkod");
        invanare.setSarskildaBehov("behov");
        invanare.getTidigareUtforare().add(buildTidigareUtforare());
        return invanare;
    }

    private TidigareUtforare buildTidigareUtforare() {
        TidigareUtforare tidigareUtforare = new TidigareUtforare();
        tidigareUtforare.setTidigareEnhetId(VE_HSA_ID);
        return tidigareUtforare;
    }

    private Handlaggare buildHandlaggare() {
        Handlaggare handlaggare = new Handlaggare();
        handlaggare.setAdress("adress");
        handlaggare.setAuthority("authority");
        handlaggare.setEmail("email");
        handlaggare.setFullstandigtNamn("fullstandigtNamn");
        handlaggare.setKontor("kontor");
        handlaggare.setKontorCostCenter("kontorCostCenter");
        handlaggare.setPostkod("postkod");
        handlaggare.setStad("stad");
        handlaggare.setTelefonnummer("telefonnummer");
        return handlaggare;
    }

    private Handling buildHandling() {

        Handling handling = new Handling();
        handling.setInkomDatum(LocalDateTime.now());
        handling.setSkickatDatum(LocalDateTime.now());
        return handling;
    }

    private ExternForfragan buildExternForfragan() {
        ExternForfragan externForfragan = new ExternForfragan();
        externForfragan.setLandstingHsaId(VG_HSA_ID);
        externForfragan.setBesvarasSenastDatum(LocalDateTime.now());
        externForfragan.setAvvisatDatum(LocalDateTime.now());
        externForfragan.setAvvisatKommentar("avvisatKommentar");
        externForfragan.setKommentar("kommentar");

        InternForfragan internForfragan = new InternForfragan();
        internForfragan.setVardenhetHsaId(VE_HSA_ID);
        internForfragan.setBesvarasSenastDatum(LocalDateTime.now());
        internForfragan.setKommentar("kommentar");
        internForfragan.setTilldeladDatum(LocalDateTime.now());

        internForfragan.setForfraganSvar(buildForfraganSvar());

        externForfragan.getInternForfraganList().add(internForfragan);
        return externForfragan;
    }

    private Bestallning buildBestallning() {
        Bestallning bestallning = new Bestallning();
        bestallning.setIntygKlartSenast(LocalDateTime.now());
        bestallning.setKommentar("kommentar");
        bestallning.setOrderDatum(LocalDateTime.now());
        bestallning.setPlaneradeAktiviteter("aktiviteter");
        bestallning.setSyfte("syfte");
        bestallning.setTilldeladVardenhetHsaId(VE_HSA_ID);
        return bestallning;
    }

    private Handelse buildHandelse() {
        Handelse h = new Handelse();
        h.setAnvandare("Kotte Korv");
        h.setHandelseText("Utredning skapades");
        h.setHandelseTyp(HandelseTyp.FORFRAGAN_MOTTAGEN);
        h.setSkapad(LocalDateTime.now());
        h.setKommentar("Detta är en kommentar");

        return h;
    }

    private Utredning buildUtredning() {
        Utredning utr = new Utredning();
        utr.setUtredningId(UTREDNING_ID);

        utr.setUtredningsTyp(UtredningsTyp.AFU);

        utr.setSprakTolk("sv");

        return utr;
    }

    private ForfraganSvar buildForfraganSvar() {
        ForfraganSvar ff = new ForfraganSvar();
        ff.setSvarTyp(SvarTyp.ACCEPTERA);
        ff.setUtforareNamn("Utförarenheten");
        ff.setUtforareAdress("Utförarvägen 1");
        ff.setUtforarePostnr("12345");
        ff.setUtforarePostort("Utförhult");
        ff.setUtforareEpost("utforare@inera.se");
        ff.setUtforareTelefon("123-123412");
        ff.setKommentar("Bered skyndsamt!");
        ff.setUtforareTyp(UtforareTyp.ENHET);
        ff.setBorjaDatum(LocalDate.now());
        return ff;
    }

}
