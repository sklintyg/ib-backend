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
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;


/**
 * Created by eriklupander on 2015-08-05.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { PersistenceConfigTest.class, PersistenceConfigDev.class })
@ActiveProfiles({ "dev" })
@Transactional
public class UtredningRepositoryTest {

    private static final String HSA_ID = "hsaId1";
    public static final String KEY_1 = "key1";
    public static final String VALUE_1 = "value1";
    public static final String VALUE_2 = "value2";
    public static final String KEY_2 = "key2";

    @Autowired
    private UtredningRepository forfraganRepository;

    @Test
    public void testFindOne() {
        Utredning saved = buildUtredning();
        forfraganRepository.save(saved);
        Utredning read = forfraganRepository.findOne(saved.getUtredningId());
        assertEquals(saved, read);
    }

    private Utredning buildUtredning() {
        Utredning ff = new Utredning();
        ff.setUtredningId("abc-123");
        ff.setInkomDatum(LocalDateTime.now());
        ff.setBesvarasSenastDatum(LocalDateTime.now().plusDays(14));

        ff.setUtredningsTyp("AFS");
        ff.setVardgivareHsaId(HSA_ID);
        ff.setStatus("Inkommen");
        ff.setInvanarePersonId("19121212-1212");
        ff.setHandlaggareNamn("Hanna Handläggarsson");
        ff.setHandlaggareEpost("epost@inera.se");
        ff.setHandlaggareTelefonnummer("031-9999999");
        ff.setBehovTolk(true);
        ff.setSprakTolk("sv");

        return ff;
    }

}
