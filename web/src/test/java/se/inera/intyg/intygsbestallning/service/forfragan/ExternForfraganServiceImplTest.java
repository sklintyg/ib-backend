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
package se.inera.intyg.intygsbestallning.service.forfragan;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import se.inera.intyg.intygsbestallning.persistence.model.ExternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.InternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.type.UtredningsTyp;
import se.inera.intyg.intygsbestallning.persistence.repository.ExternForfraganRepository;
import se.inera.intyg.intygsbestallning.service.util.BusinessDaysStub;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.forfragan.GetForfraganListResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.forfragan.InternForfraganListItemFactory;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.forfragan.ListForfraganRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExternForfraganServiceImplTest {

    @Mock
    private ExternForfraganRepository externForfraganRepository;

    @Spy
    private InternForfraganListItemFactory internForfraganListItemFactory = new InternForfraganListItemFactory(new BusinessDaysStub());

    @InjectMocks
    private ExternForfraganServiceImpl testee;

//    @Before
//    public void initMocks() {
//        internForfraganListItemFactory = spy(InternForfraganListItemFactory.class);
//        internForfraganListItemFactory.setBusinessDays(new BusinessDaysStub());
//    }

    @Test
    public void testListForfragningar() {
        when(externForfraganRepository.findByExternForfraganAndVardenhetHsaIdAndArkiveradFalse("ve-1")).thenReturn(buildUtredningar());

        ListForfraganRequest request = buildListForfraganRequest();
        GetForfraganListResponse response = testee.findForfragningarForVardenhetHsaIdWithFilter("ve-1", request);

        assertEquals(3, response.getForfragningar().size());
        assertEquals(3, response.getTotalCount());
    }

    @Test
    public void testListForfragningarWithVardgivareFilter() {
        when(externForfraganRepository.findByExternForfraganAndVardenhetHsaIdAndArkiveradFalse("ve-1")).thenReturn(buildUtredningar());

        ListForfraganRequest request = buildListForfraganRequest();
        request.setVardgivareHsaId("vg-1");
        GetForfraganListResponse response = testee.findForfragningarForVardenhetHsaIdWithFilter("ve-1", request);

        assertEquals(2, response.getForfragningar().size());
        assertEquals(2, response.getTotalCount());
    }

    private List<Utredning> buildUtredningar() {
        List<Utredning> list = new ArrayList<>();
        list.add(buildUtredning("vg-1"));
        list.add(buildUtredning("vg-1"));
        list.add(buildUtredning("vg-2"));
        return list;
    }

    private Utredning buildUtredning(String vardgivareHsaId) {
        return Utredning.UtredningBuilder.anUtredning()
                .withUtredningsTyp(UtredningsTyp.AFU)
                .withArkiverad(false)
                .withExternForfragan(
                        ExternForfragan.ExternForfraganBuilder.anExternForfragan()
                                .withLandstingHsaId(vardgivareHsaId)
                                .withInkomDatum(LocalDateTime.now())
                                .withInternForfraganList(buildInternForfraganList())
                                .build())
                .build();
    }

    private List<InternForfragan> buildInternForfraganList() {
        List<InternForfragan> list = new ArrayList<>();
        list.add(InternForfragan.InternForfraganBuilder.anInternForfragan().withVardenhetHsaId("ve-1").build());
        return list;
    }

    private ListForfraganRequest buildListForfraganRequest() {
        ListForfraganRequest request = new ListForfraganRequest();

        return request;
    }
}
