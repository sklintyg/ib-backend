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
package se.inera.intyg.intygsbestallning.service.vardgivare;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import se.inera.intyg.intygsbestallning.persistence.model.RegiFormTyp;
import se.inera.intyg.intygsbestallning.persistence.model.RegistreradVardenhet;
import se.inera.intyg.intygsbestallning.persistence.repository.RegistreradVardenhetRepository;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.GetVardenheterForVardgivareResponse;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class VardgivareServiceImplTest {

    @Mock
    private RegistreradVardenhetRepository registreradVardenhetRepository;

    @InjectMocks
    private VardgivareServiceImpl testee;

    @Test
    public void testGroupsCorrectly() {
        when(registreradVardenhetRepository.findByVardgivareHsaId(anyString())).thenReturn(buildRegVardenhetList());

        GetVardenheterForVardgivareResponse response = testee.listVardenheterForVardgivare("vg-1");

        assertEquals(1, response.getAnnatLandsting().size());
        assertEquals(1, response.getEgetLandsting().size());
        assertEquals(1, response.getPrivat().size());

        assertEquals("ve-1", response.getEgetLandsting().get(0).getId());
        assertEquals("ve-2", response.getAnnatLandsting().get(0).getId());
        assertEquals("ve-3", response.getPrivat().get(0).getId());
    }

    private List<RegistreradVardenhet> buildRegVardenhetList() {
        RegistreradVardenhet ve1 = RegistreradVardenhet.RegistreradVardenhetBuilder.aRegistreradVardenhet()
                .withVardenhetVardgivareHsaId("vg-1")
                .withVardgivareHsaId("vg-1")
                .withVardenhetHsaId("ve-1")
                .withVardenhetNamn("VE1")
                .withVardenhetRegiForm(RegiFormTyp.LANDSTING)
                .build();

        RegistreradVardenhet ve2 = RegistreradVardenhet.RegistreradVardenhetBuilder.aRegistreradVardenhet()
                .withVardenhetVardgivareHsaId("vg-2")
                .withVardgivareHsaId("vg-1")
                .withVardenhetHsaId("ve-2")
                .withVardenhetNamn("VE2")
                .withVardenhetRegiForm(RegiFormTyp.LANDSTING)
                .build();

        RegistreradVardenhet ve3 = RegistreradVardenhet.RegistreradVardenhetBuilder.aRegistreradVardenhet()
                .withVardenhetVardgivareHsaId("vg-3")
                .withVardgivareHsaId("vg-1")
                .withVardenhetHsaId("ve-3")
                .withVardenhetNamn("VE3")
                .withVardenhetRegiForm(RegiFormTyp.PRIVAT)
                .build();

        return Arrays.asList(ve1, ve2, ve3);
    }

}
