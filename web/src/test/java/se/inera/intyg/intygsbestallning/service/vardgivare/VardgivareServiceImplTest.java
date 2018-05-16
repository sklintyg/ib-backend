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

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import se.inera.intyg.infra.integration.hsa.model.Vardenhet;
import se.inera.intyg.infra.integration.hsa.services.HsaOrganizationsService;
import se.inera.intyg.intygsbestallning.common.exception.IbNotFoundException;
import se.inera.intyg.intygsbestallning.persistence.model.RegistreradVardenhet;
import se.inera.intyg.intygsbestallning.persistence.model.type.RegiFormTyp;
import se.inera.intyg.intygsbestallning.persistence.repository.RegistreradVardenhetRepository;
import se.inera.intyg.intygsbestallning.service.vardgivare.dto.VardgivarVardenhetListItem;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.GetVardenheterForVardgivareResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.ListVardenheterForVardgivareRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.ListVardenheterForVardgivareResponse;

@RunWith(MockitoJUnitRunner.class)
public class VardgivareServiceImplTest {

    private static final String VARDGIVARE_ID = "vg-1";
    private static final String ENHET_1 = "ve-1";
    private static final String ENHET_2 = "ve-2";
    private static final String ENHET_3 = "ve-3";
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @Mock
    private RegistreradVardenhetRepository registreradVardenhetRepository;
    @Mock
    private HsaOrganizationsService hsaOrganizationsService;
    @InjectMocks
    private VardgivareServiceImpl testee;

    @Test
    public void testGroupsCorrectly() {
        when(registreradVardenhetRepository.findByVardgivareHsaId(anyString())).thenReturn(buildRegVardenhetList());
        when(hsaOrganizationsService.getVardenhet(anyString())).thenAnswer(
                invocation -> buildVardEnhet(invocation.getArgument(0)));

        GetVardenheterForVardgivareResponse response = testee.listVardenheterForVardgivare(VARDGIVARE_ID);

        assertEquals(1, response.getAnnatLandsting().size());
        assertEquals(1, response.getEgetLandsting().size());
        assertEquals(1, response.getPrivat().size());

        assertEquals(ENHET_1, response.getEgetLandsting().get(0).getId());
        assertEquals(ENHET_2, response.getPrivat().get(0).getId());
        assertEquals(ENHET_3, response.getAnnatLandsting().get(0).getId());

    }

    @Test
    public void testFiltersCorrectly() {
        when(registreradVardenhetRepository.findByVardgivareHsaId(anyString())).thenReturn(buildRegVardenhetList());
        when(hsaOrganizationsService.getVardenhet(anyString())).thenAnswer(
                invocation -> buildVardEnhet(invocation.getArgument(0)));

        // Should match all
        ListVardenheterForVardgivareResponse response = testee.findVardenheterForVardgivareWithFilter(VARDGIVARE_ID, buildFilter(null));
        assertEquals(3, response.getTotalCount());

        // freetext should match 1
        response = testee.findVardenheterForVardgivareWithFilter(VARDGIVARE_ID, buildFilter("ve-2-na"));
        assertEquals(1, response.getTotalCount());
        assertEquals(ENHET_2, response.getVardenheter().get(0).getVardenhetHsaId());

        // No match
        response = testee.findVardenheterForVardgivareWithFilter(VARDGIVARE_ID, buildFilter("XXX"));
        assertEquals(0, response.getTotalCount());

    }

    @Test
    public void testUpdateRegiformSuccess() {
        when(registreradVardenhetRepository.findByVardgivareHsaIdAndVardenhetHsaId(anyString(), anyString()))
                .thenReturn(Optional.of(buildRegVardenhetList().get(0)));
        when(registreradVardenhetRepository.save(any(RegistreradVardenhet.class))).thenAnswer(
                invocation -> invocation.getArgument(0));
        when(hsaOrganizationsService.getVardenhet(anyString())).thenAnswer(
                invocation -> buildVardEnhet(invocation.getArgument(0)));

        final VardgivarVardenhetListItem vardgivarVardenhetListItem = testee.updateRegiForm(VARDGIVARE_ID, ENHET_1, RegiFormTyp.PRIVAT.name());

        assertEquals(RegiFormTyp.PRIVAT, vardgivarVardenhetListItem.getRegiForm());

    }

    @Test
    public void testUpdateRegiformFailsWhenEntityNotFound() {
        when(registreradVardenhetRepository.findByVardgivareHsaIdAndVardenhetHsaId(anyString(), anyString()))
                .thenReturn(Optional.empty());

        thrown.expect(IbNotFoundException.class);
        testee.updateRegiForm(VARDGIVARE_ID, ENHET_1, RegiFormTyp.PRIVAT.name());

    }

    @Test
    public void testDeleteSuccess() {
        RegistreradVardenhet rv = buildRegVardenhetList().get(0);
        when(registreradVardenhetRepository.findByVardgivareHsaIdAndVardenhetHsaId(anyString(), anyString()))
                .thenReturn(Optional.of(rv));

        testee.delete(VARDGIVARE_ID, ENHET_1);

        verify(registreradVardenhetRepository, times(1))
                .delete(rv);

    }

    @Test
    public void testDeleteFailsWhenEntityNotFound() {
        when(registreradVardenhetRepository.findByVardgivareHsaIdAndVardenhetHsaId(anyString(), anyString()))
                .thenReturn(Optional.empty());

        thrown.expect(IbNotFoundException.class);
        testee.delete(VARDGIVARE_ID, ENHET_1);

    }

    private ListVardenheterForVardgivareRequest buildFilter(String freeText) {
        ListVardenheterForVardgivareRequest req = new ListVardenheterForVardgivareRequest();
        req.setPageSize(10);
        req.setCurrentPage(0);
        req.setFreeText(freeText);
        return req;
    }

    private Vardenhet buildVardEnhet(String id) {
        Vardenhet vardenhet = new Vardenhet();
        vardenhet.setId(id);
        vardenhet.setNamn(id + "-name");
        return vardenhet;
    }

    private List<RegistreradVardenhet> buildRegVardenhetList() {
        RegistreradVardenhet ve1 = RegistreradVardenhet.RegistreradVardenhetBuilder.aRegistreradVardenhet()
                .withVardenhetVardgivareHsaId(VARDGIVARE_ID)
                .withVardgivareHsaId(VARDGIVARE_ID)
                .withVardenhetHsaId(ENHET_1)
                .withVardenhetNamn(ENHET_1 + "-name")
                .withVardenhetRegiForm(RegiFormTyp.EGET_LANDSTING)
                .build();

        RegistreradVardenhet ve2 = RegistreradVardenhet.RegistreradVardenhetBuilder.aRegistreradVardenhet()
                .withVardenhetVardgivareHsaId("vg-2")
                .withVardgivareHsaId(VARDGIVARE_ID)
                .withVardenhetHsaId(ENHET_2)
                .withVardenhetNamn(ENHET_2 + "-name")
                .withVardenhetRegiForm(RegiFormTyp.PRIVAT)
                .build();
        RegistreradVardenhet ve3 = RegistreradVardenhet.RegistreradVardenhetBuilder.aRegistreradVardenhet()
                .withVardenhetVardgivareHsaId("vg-3")
                .withVardgivareHsaId(VARDGIVARE_ID)
                .withVardenhetHsaId(ENHET_3)
                .withVardenhetNamn(ENHET_3 + "-name")
                .withVardenhetRegiForm(RegiFormTyp.ANNAT_LANDSTING)
                .build();

        return Arrays.asList(ve1, ve2, ve3);
    }

}
