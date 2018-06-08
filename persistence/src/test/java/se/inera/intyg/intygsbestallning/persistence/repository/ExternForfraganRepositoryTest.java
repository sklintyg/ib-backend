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
import se.inera.intyg.intygsbestallning.persistence.model.ExternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.util.TestDataFactory;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { PersistenceConfigTest.class, PersistenceConfigDev.class })
@ActiveProfiles({ "dev" })
@Transactional
public class ExternForfraganRepositoryTest {

    @Autowired
    private UtredningRepository utredningRepository;

    @Autowired
    private ExternForfraganRepository externForfraganRepository;

    @Test
    public void testFindByExternForfraganAndVardenhetHsaIdAndArkiveradFalse() {
        Utredning saved = TestDataFactory.buildUtredning();
        ExternForfragan externForfragan = TestDataFactory.buildExternForfragan();
        saved.setExternForfragan(externForfragan);
        saved = utredningRepository.saveUtredning(saved);

        List<Utredning> list = externForfraganRepository.findByExternForfraganAndVardenhetHsaIdAndArkiveradFalse(TestDataFactory.VE_HSA_ID);
        assertEquals(1, list.size());
    }
}
