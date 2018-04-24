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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

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
import se.inera.intyg.intygsbestallning.persistence.model.VardenhetPreference;

/**
 * Created by marced on 2018-04-23.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { PersistenceConfigTest.class, PersistenceConfigDev.class })
@ActiveProfiles({ "dev" })
@Transactional
public class VardenhetPreferenceRepositoryTest {

    private static final String DEFAULT_HSA_ID = "HSAID-123-A";
    private static final String DEFAULT_MOTTAGARNAMN = "Mottagaren";
    private static final String DEFAULT_ADRESS = "Vårdgatan 3";
    private static final String DEFAULT_POSTNUMMER = "12345";
    private static final String DEFAULT_POSTORT = "Vårdinge";
    private static final String DEFAULT_TELEFONNUMMER = "01-123456789";
    private static final String DEFAULT_EPOST = "enheten@vg.se";
    private static final String DEFAULT_SVAR = "Vi gör allt vi kan för att hjälpa";
    private static final String UPDATED_SVAR = "Vill inte göra mer";
    private static final String UPDATED_MOTTAGARNAMN = "Nya mottagningsnamnet";

    @Autowired
    private VardenhetPreferenceRepository vardenhetPreferenceRepository;

    @Test
    public void testSave() {
        VardenhetPreference saved = buildSampleVardenhetPreference();
        saved = vardenhetPreferenceRepository.save(saved);

        String internreferens = saved.getVardenhetHsaId();

        assertNotNull(internreferens);

    }

    @Test
    public void testFindByVardenhetHsaIdOptionalEmptyWhenNotFound() {
        VardenhetPreference saved = buildSampleVardenhetPreference();
        saved = vardenhetPreferenceRepository.save(saved);

        String internreferens = saved.getVardenhetHsaId();

        assertNotNull(internreferens);

        Optional<VardenhetPreference> read = vardenhetPreferenceRepository.findByVardenhetHsaId("finns ej");
        assertFalse(read.isPresent());
    }

    @Test
    public void testFindByVardenhetHsaId() {
        VardenhetPreference saved = buildSampleVardenhetPreference();
        saved = vardenhetPreferenceRepository.save(saved);

        String internreferens = saved.getVardenhetHsaId();

        assertNotNull(internreferens);

        Optional<VardenhetPreference> read = vardenhetPreferenceRepository.findByVardenhetHsaId(DEFAULT_HSA_ID);
        assertTrue(read.isPresent());
        assertEquals(DEFAULT_HSA_ID, read.get().getVardenhetHsaId());
        assertEquals(DEFAULT_MOTTAGARNAMN, read.get().getMottagarNamn());
        assertEquals(DEFAULT_ADRESS, read.get().getAdress());
        assertEquals(DEFAULT_POSTORT, read.get().getPostort());
        assertEquals(DEFAULT_TELEFONNUMMER, read.get().getTelefonnummer());
        assertEquals(DEFAULT_EPOST, read.get().getEpost());
        assertEquals(DEFAULT_SVAR, read.get().getStandardsvar());

    }

    @Test
    public void testUpdate() {
        VardenhetPreference saved = buildSampleVardenhetPreference();
        saved = vardenhetPreferenceRepository.save(saved);

        String internreferens = saved.getVardenhetHsaId();

        assertNotNull(internreferens);

        VardenhetPreference read = vardenhetPreferenceRepository.findByVardenhetHsaId(DEFAULT_HSA_ID).get();
        read.setStandardsvar(UPDATED_SVAR);
        read.setMottagarNamn(UPDATED_MOTTAGARNAMN);
        VardenhetPreference updated = vardenhetPreferenceRepository.saveAndFlush(read);

        assertEquals(UPDATED_SVAR, updated.getStandardsvar());
        assertEquals(UPDATED_MOTTAGARNAMN, updated.getMottagarNamn());

    }

    private VardenhetPreference buildSampleVardenhetPreference() {
        VardenhetPreference vp = new VardenhetPreference();

        vp.setVardenhetHsaId(DEFAULT_HSA_ID);

        vp.setMottagarNamn(DEFAULT_MOTTAGARNAMN);
        vp.setAdress(DEFAULT_ADRESS);
        vp.setPostnummer(DEFAULT_POSTNUMMER);
        vp.setPostort(DEFAULT_POSTORT);
        vp.setTelefonnummer(DEFAULT_TELEFONNUMMER);
        vp.setEpost(DEFAULT_EPOST);

        vp.setStandardsvar(DEFAULT_SVAR);
        return vp;

    }
}
