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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static se.inera.intyg.intygsbestallning.persistence.model.Intyg.IntygBuilder.anIntyg;
import static se.inera.intyg.intygsbestallning.persistence.model.SkickadNotifiering.SkickadNotifieringBuilder.aSkickadNotifiering;
import static se.inera.intyg.intygsbestallning.persistence.util.TestDataFactory.buildAnteckning;
import static se.inera.intyg.intygsbestallning.persistence.util.TestDataFactory.buildBesok;
import static se.inera.intyg.intygsbestallning.persistence.util.TestDataFactory.buildBestallning;
import static se.inera.intyg.intygsbestallning.persistence.util.TestDataFactory.buildBetalning;
import static se.inera.intyg.intygsbestallning.persistence.util.TestDataFactory.buildExternForfragan;
import static se.inera.intyg.intygsbestallning.persistence.util.TestDataFactory.buildHandelse;
import static se.inera.intyg.intygsbestallning.persistence.util.TestDataFactory.buildHandlaggare;
import static se.inera.intyg.intygsbestallning.persistence.util.TestDataFactory.buildHandling;
import static se.inera.intyg.intygsbestallning.persistence.util.TestDataFactory.buildIntyg;
import static se.inera.intyg.intygsbestallning.persistence.util.TestDataFactory.buildInvanare;
import static se.inera.intyg.intygsbestallning.persistence.util.TestDataFactory.buildUtredning;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import se.inera.intyg.intygsbestallning.persistence.config.PersistenceConfigDev;
import se.inera.intyg.intygsbestallning.persistence.config.PersistenceConfigTest;
import se.inera.intyg.intygsbestallning.persistence.model.Anteckning;
import se.inera.intyg.intygsbestallning.persistence.model.Avvikelse;
import se.inera.intyg.intygsbestallning.persistence.model.Besok;
import se.inera.intyg.intygsbestallning.persistence.model.Bestallning;
import se.inera.intyg.intygsbestallning.persistence.model.ExternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.ForfraganSvar;
import se.inera.intyg.intygsbestallning.persistence.model.Handelse;
import se.inera.intyg.intygsbestallning.persistence.model.Handlaggare;
import se.inera.intyg.intygsbestallning.persistence.model.Handling;
import se.inera.intyg.intygsbestallning.persistence.model.InternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.Intyg;
import se.inera.intyg.intygsbestallning.persistence.model.Invanare;
import se.inera.intyg.intygsbestallning.persistence.model.TidigareUtforare;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.type.AvslutOrsak;
import se.inera.intyg.intygsbestallning.persistence.model.type.AvvikelseOrsak;
import se.inera.intyg.intygsbestallning.persistence.model.type.BesokStatusTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.DeltagarProfessionTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.HandelseTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.HandlingUrsprungTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.KallelseFormTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringMottagarTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.SvarTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.TolkStatusTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.UtforareTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.UtredningsTyp;

/**
 * Created by eriklupander on 2015-08-05.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = {PersistenceConfigTest.class, PersistenceConfigDev.class})
@ActiveProfiles({"dev"})
@Transactional
public class UtredningRepositoryTest {

    private static final String VE_HSA_ID = "enhet-1";
    private static final String VG_HSA_ID = "vg-1";

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

        utr.getAnteckningList().add(buildAnteckning());

        utr.getIntygList().add(buildIntyg());

        utr.setBetalning(buildBetalning());

        Utredning savedUtredning = utredningRepository.saveUtredning(utr);

        Optional<Utredning> savedAndRetreived = utredningRepository.findById(savedUtredning.getUtredningId());
        assertTrue(savedAndRetreived.isPresent());
        Utredning utredning = savedAndRetreived.get();

        assertEquals(savedUtredning.getUtredningId(), utredning.getUtredningId());
        assertEquals(UtredningsTyp.AFU, utredning.getUtredningsTyp());
        assertFalse(utredning.getTolkBehov());
        assertNotNull(utredning.getAvbrutenDatum());
        assertEquals(AvslutOrsak.JAV, utredning.getAvbrutenOrsak());

        Bestallning bestallning = utredning.getBestallning().orElse(null);
        assertNotNull(bestallning);
        assertEquals("kommentar", bestallning.getKommentar());
        assertEquals("aktiviteter", bestallning.getPlaneradeAktiviteter());
        assertEquals("syfte", bestallning.getSyfte());
        assertEquals(VE_HSA_ID, bestallning.getTilldeladVardenhetHsaId());

        ExternForfragan externForfragan = utredning.getExternForfragan().orElse(null);
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
        assertEquals(HandelseTyp.EXTERNFORFRAGAN_MOTTAGEN, handelse.getHandelseTyp());
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
        assertEquals("12345", handlaggare.getPostnummer());
        assertEquals("stad", handlaggare.getStad());
        assertEquals("telefonnummer", handlaggare.getTelefonnummer());

        Invanare invanare = utredning.getInvanare();
        assertNotNull(invanare);
        assertEquals("bakgrund", invanare.getBakgrundNulage());
        assertEquals("personId", invanare.getPersonId());
        assertEquals("postort", invanare.getPostort());
        assertEquals("behov", invanare.getSarskildaBehov());

        assertEquals(1, invanare.getTidigareUtforare().size());
        TidigareUtforare tu = invanare.getTidigareUtforare().get(0);
        assertEquals(VE_HSA_ID, tu.getTidigareEnhetId());

        Besok besok = utredning.getBesokList().get(0);
        assertEquals(BesokStatusTyp.TIDBOKAD_VARDKONTAKT, besok.getBesokStatus());
        assertEquals(DeltagarProfessionTyp.FT, besok.getDeltagareProfession());
        assertEquals(KallelseFormTyp.TELEFONKONTAKT, besok.getKallelseForm());
        assertNotNull(besok.getBesokStartTid());
        assertEquals(TolkStatusTyp.EJ_BOKAT, besok.getTolkStatus());
        assertNull(besok.getErsatts());

        Avvikelse avvikelse = besok.getAvvikelse();
        assertNotNull(avvikelse);
        assertNotNull(avvikelse.getTidpunkt());
        assertEquals(AvvikelseOrsak.PATIENT, avvikelse.getOrsakatAv());
        assertEquals(savedUtredning.getBesokList().get(0).getAvvikelse().getAvvikelseId(), avvikelse.getAvvikelseId());
        assertEquals("avvikelseBeskrivning", avvikelse.getBeskrivning());
        assertTrue(avvikelse.getInvanareUteblev());

        assertNotNull(utredning.getAnteckningList());
        assertFalse(utredning.getAnteckningList().isEmpty());
        Anteckning anteckning = utredning.getAnteckningList().get(0);
        assertEquals("text", anteckning.getText());
        assertEquals("anvandare", anteckning.getAnvandare());
        assertEquals("anteckningVardenhetHsaId", anteckning.getVardenhetHsaId());
        assertNotNull(anteckning.getSkapat());

        assertNotNull(utredning.getIntygList());
        assertEquals(1, utredning.getIntygList().size());
        Intyg intyg = utredning.getIntygList().get(0);
        assertNotNull(intyg);
        assertTrue(intyg.isKomplettering());
        assertNotNull(intyg.getMottagetDatum());
        assertNotNull(intyg.getSistaDatum());
        assertNotNull(intyg.getSistaDatumKompletteringsbegaran());
        assertNotNull(intyg.getSkickatDatum());

        Optional<Long> kompletteringsId = utredningRepository.findNewestKompletteringOnUtredning(utredning.getUtredningId());
        assertTrue(kompletteringsId.isPresent());
        assertEquals(intyg.getId(), kompletteringsId.get());

    }

    @Test
    public void testFindAllByExternForfragan_InternForfraganList_VardenhetHsaId_AndArkiveradFalse() {
        Utredning utr = buildUtredning();
        utr.setArkiverad(true);
        utr.setBestallning(buildBestallning());
        utr.setExternForfragan(buildExternForfragan());
        utr = utredningRepository.save(utr);

        List<Utredning> response = utredningRepository
                .findAllByExternForfragan_InternForfraganList_VardenhetHsaId_AndArkiveradFalse(VE_HSA_ID);
        assertNotNull(response);
        assertTrue(response.isEmpty());

        utr.setArkiverad(false);
        response = utredningRepository.findAllByExternForfragan_InternForfraganList_VardenhetHsaId_AndArkiveradFalse(VE_HSA_ID);
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
    public void testFindAllByBestallning_TilldeladVardenhetHsaId_AndArkiveradFalse() {
        Utredning utr = buildUtredning();
        utr.setBestallning(buildBestallning());
        utr.setArkiverad(true);
        utredningRepository.save(utr);

        List<Utredning> resultList = utredningRepository.findAllByBestallning_TilldeladVardenhetHsaId_AndArkiveradFalse(VE_HSA_ID);
        assertNotNull(resultList);
        assertTrue(resultList.isEmpty());

        utr.setArkiverad(false);
        utredningRepository.save(utr);
        resultList = utredningRepository.findAllByBestallning_TilldeladVardenhetHsaId_AndArkiveradFalse(VE_HSA_ID);
        assertEquals(1, resultList.size());
        assertNotNull(resultList.get(0).getBestallning());

    }

    @Test
    public void testFindAllWithBestallningForVardenhetHsaId() {
        Utredning utr = buildUtredning();
        utr.setBestallning(buildBestallning());
        utredningRepository.save(utr);

        List<Utredning> resultList = utredningRepository.findAllByBestallning_TilldeladVardenhetHsaId_AndArkiveradFalse(VE_HSA_ID);
        assertEquals(1, resultList.size());
        assertNotNull(resultList.get(0).getBestallning());
    }

    @Test
    public void testFindUtredningByBesokId() {
        Utredning utredning = buildUtredning();

        utredning.setBesokList(ImmutableList.of(buildBesok()));

        final Utredning saved = utredningRepository.save(utredning);
        final Besok besok = saved.getBesokList().get(0);
        final Utredning search = utredningRepository.findByBesokList_Id(besok.getId()).orElse(null);

        assertEquals(saved, search);
    }

    @Test
    public void findNonNotifiedUtredningWithIntygSlutDatumBetween() {
        Utredning utr = buildUtredning();
        utr.setBestallning(buildBestallning());
        Intyg intyg = buildIntyg();
        intyg.setKomplettering(false);
        intyg.setSistaDatum(LocalDateTime.now().plusDays(2));
        utr.getIntygList().add(intyg);
        final Utredning saved = utredningRepository.save(utr);

        List<Utredning> utredningList = utredningRepository.findNonNotifiedIntygSlutDatumBetween(
                LocalDate.now().atStartOfDay(),
                LocalDate.now().plusDays(3L).atStartOfDay(),
                NotifieringTyp.PAMINNELSE_SLUTDATUM_UTREDNING_PASSERAS,
                NotifieringMottagarTyp.VARDENHET);
        assertEquals(1, utredningList.size());
    }

    @Test
    public void findNonNotifiedUtredningWithIntygSlutDatumNotBetween() {
        Utredning utr = buildUtredning();
        utr.setBestallning(buildBestallning());
        Intyg intyg = buildIntyg();
        intyg.setKomplettering(false);
        intyg.setSistaDatum(LocalDateTime.now().plusDays(2));
        utr.getIntygList().add(intyg);
        final Utredning saved = utredningRepository.save(utr);

        List<Utredning> utredningList = utredningRepository.findNonNotifiedIntygSlutDatumBetween(
                LocalDate.now().plusDays(3L).atStartOfDay(),
                LocalDate.now().plusDays(5L).atStartOfDay(),
                NotifieringTyp.PAMINNELSE_SLUTDATUM_UTREDNING_PASSERAS,
                NotifieringMottagarTyp.VARDENHET);
        assertEquals(0, utredningList.size());
    }

    @Test
    public void findUtredningWithIntygSlutDatumBetweenButAlreadyNotified() {
        Utredning utr = buildUtredning();
        utr.getSkickadNotifieringList().add(aSkickadNotifiering()
                .withTyp(NotifieringTyp.PAMINNELSE_SLUTDATUM_UTREDNING_PASSERAS)
                .withMottagare(NotifieringTyp.PAMINNELSE_SLUTDATUM_UTREDNING_PASSERAS.getNotifieringMottagarTyp())
                .withSkickad(LocalDateTime.now())
                .build());
        utr.setBestallning(buildBestallning());
        Intyg intyg = buildIntyg();
        intyg.setKomplettering(false);
        intyg.setSistaDatum(LocalDateTime.now().plusDays(2));
        utr.getIntygList().add(intyg);
        final Utredning saved = utredningRepository.save(utr);

        List<Utredning> utredningList = utredningRepository.findNonNotifiedIntygSlutDatumBetween(
                LocalDate.now().atStartOfDay(),
                LocalDate.now().plusDays(3L).atStartOfDay(),
                NotifieringTyp.PAMINNELSE_SLUTDATUM_UTREDNING_PASSERAS,
                NotifieringMottagarTyp.VARDENHET);
        assertEquals(0, utredningList.size());
    }

    @Test
    public void findNonNotifiedUtredningWithIntygSlutDatumPasserat() {
        Utredning utr = buildUtredning();
        utr.setBestallning(buildBestallning());
        Intyg intyg = buildIntyg();
        intyg.setKomplettering(false);
        intyg.setMottagetDatum(null);
        intyg.setSistaDatum(LocalDate.now().minusDays(3).atStartOfDay());
        utr.getIntygList().add(intyg);
        final Utredning saved = utredningRepository.save(utr);

        List<Utredning> utredningList = utredningRepository.findNonNotifiedSlutDatumBefore(
                LocalDate.now().atStartOfDay(),
                NotifieringTyp.SLUTDATUM_UTREDNING_PASSERAT,
                NotifieringMottagarTyp.VARDENHET);
        assertEquals(1, utredningList.size());
    }

    @Test
    public void findNonNotifiedUtredningWithIntygSlutDatumPasseratRedanNotifierad() {
        Utredning utr = buildUtredning();
        utr.setBestallning(buildBestallning());
        utr.getSkickadNotifieringList().add(aSkickadNotifiering()
                .withTyp(NotifieringTyp.SLUTDATUM_UTREDNING_PASSERAT)
                .withMottagare(NotifieringMottagarTyp.VARDENHET)
                .withSkickad(LocalDateTime.now())
                .build());
        Intyg intyg = buildIntyg();
        intyg.setKomplettering(false);
        intyg.setMottagetDatum(null);
        intyg.setSistaDatum(LocalDate.now().minusDays(3).atStartOfDay());
        utr.getIntygList().add(intyg);
        final Utredning saved = utredningRepository.save(utr);

        List<Utredning> utredningList = utredningRepository.findNonNotifiedSlutDatumBefore(
                LocalDate.now().atStartOfDay(),
                NotifieringTyp.SLUTDATUM_UTREDNING_PASSERAT,
                NotifieringMottagarTyp.VARDENHET);
        assertEquals(0, utredningList.size());
    }

    @Test
    public void findEjNotifieradeIntygPaminnelseSistaDatumKomplettering() {

        final LocalDateTime localDateTime = LocalDateTime.of(2018, 9, 9, 9, 9, 9, 9);

        Utredning utredning = buildUtredning();
        utredning.setBestallning(buildBestallning());

        utredning.setIntygList(Lists.newArrayList(
                anIntyg()
                        .withKomplettering(false)
                        .withSistaDatumKompletteringsbegaran(localDateTime.minusDays(3))
                        .build(),
                anIntyg()
                        .withKomplettering(true)
                        .withSistaDatumKompletteringsbegaran(localDateTime.minusDays(2))
                        .build(),
                anIntyg()
                        .withKomplettering(true)
                        .withSistaDatumKompletteringsbegaran(localDateTime.minusDays(1))
                        .build(),
                anIntyg()
                        .withKomplettering(true)
                        .withSistaDatumKompletteringsbegaran(localDateTime.plusDays(10))
                        .build()
        ));

        utredning.setSkickadNotifieringList(Lists.newArrayList(
                aSkickadNotifiering()
                        .withIntygId(2L)
                        .withMottagare(NotifieringMottagarTyp.VARDENHET)
                        .withSkickad(localDateTime.minusDays(2))
                        .withTyp(NotifieringTyp.PAMINNELSEDATUM_KOMPLETTERING_PASSERAS)
                        .build()
        ));

        utredningRepository.saveUtredning(utredning);

        final List<Utredning> utredningList = utredningRepository.findNonNotifiedSistaDatumKompletteringsBegaranBefore(localDateTime, NotifieringTyp.PAMINNELSEDATUM_KOMPLETTERING_PASSERAS);String test = "hej";
        assertThat(utredningList.size()).isEqualTo(1);
        assertThat(utredningList.get(0)).isEqualTo(utredning);
    }
}
