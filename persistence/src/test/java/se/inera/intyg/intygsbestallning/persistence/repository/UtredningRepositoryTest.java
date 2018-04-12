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
import se.inera.intyg.intygsbestallning.persistence.model.Forfragan;
import se.inera.intyg.intygsbestallning.persistence.model.ForfraganSvar;
import se.inera.intyg.intygsbestallning.persistence.model.Handelse;
import se.inera.intyg.intygsbestallning.persistence.model.HandelseTyp;
import se.inera.intyg.intygsbestallning.persistence.model.SvarTyp;
import se.inera.intyg.intygsbestallning.persistence.model.TidigareUtredning;
import se.inera.intyg.intygsbestallning.persistence.model.UtforareTyp;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;

import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;


/**
 * Created by eriklupander on 2015-08-05.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { PersistenceConfigTest.class, PersistenceConfigDev.class })
@ActiveProfiles({ "dev" })
@Transactional
public class UtredningRepositoryTest {

    private static final Logger LOG = LoggerFactory.getLogger(UtredningRepositoryTest.class);

    private static final String VG_HSA_ID = "vg-1";
    public static final String VE_HSA_ID = "enhet-1";
    public static final String VALUE_1 = "value1";
    public static final String VALUE_2 = "value2";
    public static final String KEY_2 = "key2";

    @Autowired
    private UtredningRepository utredningRepository;


    @Test
    public void testFindOne() {
        // Create and save a base Utredning
        Utredning saved = buildUtredning();


        TidigareUtredning tidigareUtredning = new TidigareUtredning();
        tidigareUtredning.setTidigareUtredningId("tidigare-utredning-1");
        saved.getInvanareTidigareUtredning().add(tidigareUtredning);

        Handelse skapad = buildHandelse();
        saved.getHandelseList().add(skapad);
        saved = utredningRepository.save(saved);
        logJson(saved);

        // Next, load it from the DB and assert that they are equal.
        Utredning readUtredning = utredningRepository.findOne(saved.getUtredningId());
        assertEquals(saved, readUtredning);
        assertEquals(1, saved.getHandelseList().size());
        assertEquals(1, saved.getInvanareTidigareUtredning().size());

        // Build a forfragan and add it to the Utredning. Call save.
        Forfragan forfragan = buildForfragan();
        readUtredning.getForfraganList().add(forfragan);
        readUtredning = utredningRepository.save(readUtredning);
        logJson(readUtredning);

        // Assert that the updated readUtredning contains the Forfragan
        assertEquals(1, readUtredning.getForfraganList().size());
        assertNotNull(readUtredning.getForfraganList().get(0).getInternreferens());
        assertEquals(readUtredning.getForfraganList().get(0).getVardenhetHsaId(), forfragan.getVardenhetHsaId());

        ForfraganSvar forfraganSvar = buildForfraganSvar(forfragan.getInternreferens());
        readUtredning.getForfraganList().get(0).setForfraganSvar(forfraganSvar);

        readUtredning = utredningRepository.save(readUtredning);
        assertNotNull(readUtredning.getForfraganList().get(0).getForfraganSvar());


        // Lägg beställning
        Bestallning bestallning = buildBestallning();
        readUtredning.setBestallning(bestallning);
    }

    @Test
    public void testFindByVardgivareHsaId() {
        Utredning utredning = buildUtredning();
        utredningRepository.save(utredning);

        List<Utredning> utredningar = utredningRepository.findByVardgivareHsaId(VG_HSA_ID);
        assertEquals(1, utredningar.size());
    }

    @Test
    public void testFindByVardgivareHsaIdUnknownId() {
        Utredning utredning = buildUtredning();
        utredningRepository.save(utredning);

        List<Utredning> utredningar = utredningRepository.findByVardgivareHsaId("other-id");
        assertEquals(0, utredningar.size());
    }

    @Test
    public void testFindForfraganByVardenhetHsaId() {
        Utredning utredning = buildUtredning();
        utredning = utredningRepository.save(utredning);

        // Add forfragan and persist.
        Forfragan f = buildForfragan();
        f.setUtredningId(utredning.getUtredningId());
        utredning.getForfraganList().add(f);
        utredningRepository.save(utredning);


        List<Forfragan> forfragningar = utredningRepository.findForfragningarForVardenhetHsaId(VE_HSA_ID);
        assertEquals(1, forfragningar.size());
    }

    @Test
    public void testFindForfraganByIdAndVardenhet() {
        Utredning utredning = buildUtredning();
        utredning = utredningRepository.save(utredning);

        // Add forfragan and persist.
        Forfragan f = buildForfragan();
        f.setUtredningId(utredning.getUtredningId());
        utredning.getForfraganList().add(f);
        utredning = utredningRepository.save(utredning);

        Long forfraganId = utredning.getForfraganList().get(0).getInternreferens();

        Forfragan forfragan = utredningRepository.findForfraganByIdAndVardenhet(forfraganId, VE_HSA_ID);
        assertNotNull(forfragan);
    }

    @Test
    public void testFindForfraganByIdAndNoneMatchingVardenhet() {
        Utredning utredning = buildUtredning();
        utredning = utredningRepository.save(utredning);

        // Add forfragan and persist.
        Forfragan f = buildForfragan();
        f.setUtredningId(utredning.getUtredningId());
        utredning.getForfraganList().add(f);
        utredning = utredningRepository.save(utredning);

        Long forfraganId = utredning.getForfraganList().get(0).getInternreferens();

        Forfragan forfragan = utredningRepository.findForfraganByIdAndVardenhet(forfraganId, "other-id");
        assertNull(forfragan);
    }

    private void logJson(Utredning saved) {
        try {
            StringWriter sw = new StringWriter();
            new CustomObjectMapper().writeValue(sw, saved);
            LOG.info("STARTJSON");
            LOG.info(sw.toString());
        } catch (IOException e) {

        }
    }

    private Bestallning buildBestallning() {
        Bestallning b = new Bestallning();
        b.setIntygKlartSenast(LocalDateTime.now().plusDays(8));
        b.setInvanarePersonId("19121212-1212");
        b.setKommentar("Kommentera inte mig");
        b.setPlaneradeAktiviteter("Gå på cirkus");
        b.setSyfte("Bli fin");
        b.setTilldeladVardenhetHsaId(VE_HSA_ID);
        b.setOrderDatum(LocalDateTime.now());
        return b;
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
        utr.setUtredningId("abc-123");
        utr.setInkomDatum(LocalDateTime.now());
        utr.setBesvarasSenastDatum(LocalDateTime.now().plusDays(14));

        utr.setUtredningsTyp("AFS");
        utr.setVardgivareHsaId(VG_HSA_ID);

        utr.setInvanarePostort("Stockholm");
        utr.setInvanareSpecialbehov("Gillar glass");

        utr.setKommentar("Detta är en kommentar");
        utr.setHandlaggareNamn("Hanna Handläggarsson");
        utr.setHandlaggareEpost("epost@inera.se");
        utr.setHandlaggareTelefonnummer("031-9999999");
        utr.setBehovTolk(true);
        utr.setSprakTolk("sv");

        return utr;
    }

    private Forfragan buildForfragan() {
        Forfragan ff = new Forfragan();
        ff.setBesvarasSenastDatum(LocalDateTime.now().plusDays(14));
        ff.setKommentar("Bered skyndsamt!");
        ff.setVardenhetHsaId(VE_HSA_ID);
        ff.setStatus("Skapad");
        return ff;
    }

    private ForfraganSvar buildForfraganSvar(Long forfraganId) {
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
        return ff;
    }

}
