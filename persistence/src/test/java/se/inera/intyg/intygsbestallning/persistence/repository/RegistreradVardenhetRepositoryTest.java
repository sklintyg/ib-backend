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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;
import se.inera.intyg.intygsbestallning.persistence.config.PersistenceConfigDev;
import se.inera.intyg.intygsbestallning.persistence.config.PersistenceConfigTest;
import se.inera.intyg.intygsbestallning.persistence.model.type.RegiFormTyp;
import se.inera.intyg.intygsbestallning.persistence.model.RegistreradVardenhet;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { PersistenceConfigTest.class, PersistenceConfigDev.class })
@ActiveProfiles({ "dev" })
@Transactional
public class RegistreradVardenhetRepositoryTest {

    @Autowired
    private RegistreradVardenhetRepository registreradVardenhetRepository;

    @Test
    public void testSave() {
        RegistreradVardenhet saved = buildRegistreradVardenhet();
        saved = registreradVardenhetRepository.save(saved);

        long internreferens = saved.getId();
        assertTrue(internreferens > -1);
    }

    @Test
    public void testFindByVardgivareHsaId() {
        RegistreradVardenhet saved = buildRegistreradVardenhet();
        registreradVardenhetRepository.save(saved);

        List<RegistreradVardenhet> list = registreradVardenhetRepository.findByVardgivareHsaId("vg-1");
        assertEquals(1, list.size());
    }

    @Test
    public void testFindByOtherVardgivareHsaIdReturnsZeroResults() {
        RegistreradVardenhet saved = buildRegistreradVardenhet();
        registreradVardenhetRepository.save(saved);

        List<RegistreradVardenhet> list = registreradVardenhetRepository.findByVardgivareHsaId("vg-2");
        assertEquals(0, list.size());
    }

    private RegistreradVardenhet buildRegistreradVardenhet() {
        return RegistreradVardenhet.RegistreradVardenhetBuilder.aRegistreradVardenhet()
                .withVardenhetHsaId("ve-1")
                .withVardenhetNamn("Enhet 1")
                .withVardenhetRegiForm(RegiFormTyp.LANDSTING)
                .withVardenhetVardgivareHsaId("vg-1")
                .withVardgivareHsaId("vg-1")
                .build();
    }
}
