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
package se.inera.intyg.intygsbestallning.service.utredning;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import se.inera.intyg.infra.integration.hsa.services.HsaOrganizationsService;
import se.inera.intyg.intygsbestallning.auth.IbUser;
import se.inera.intyg.intygsbestallning.auth.model.IbVardenhet;
import se.inera.intyg.intygsbestallning.common.exception.IbAuthorizationErrorCodeEnum;
import se.inera.intyg.intygsbestallning.common.exception.IbAuthorizationException;
import se.inera.intyg.intygsbestallning.common.exception.IbErrorCodeEnum;
import se.inera.intyg.intygsbestallning.common.exception.IbNotFoundException;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.repository.UtredningRepository;
import se.inera.intyg.intygsbestallning.service.patient.PatientNameEnricher;
import se.inera.intyg.intygsbestallning.service.pdl.LogService;
import se.inera.intyg.intygsbestallning.service.user.UserService;
import se.inera.intyg.intygsbestallning.service.util.BusinessDaysStub;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.bestallning.AvslutadBestallningListItemFactory;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.bestallning.BestallningListItem;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.bestallning.BestallningListItemFactory;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.bestallning.GetBestallningListResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.bestallning.GetBestallningResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.bestallning.ListBestallningRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.filter.ListFilterStatus;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static se.inera.intyg.intygsbestallning.persistence.model.status.UtredningStatus.FORFRAGAN_INKOMMEN;

@RunWith(MockitoJUnitRunner.class)
public class BestallningServiceImplTest {

    @Mock
    private UtredningRepository utredningRepository;

    @Mock
    private UserService userService;

    @Mock
    private LogService logService;

    @Mock
    private PatientNameEnricher patientNameEnricher;

    @Mock
    private HsaOrganizationsService hsaOrganizationsService;

    @Spy
    private BestallningListItemFactory bestallningListItemFactory;

    @Spy
    private AvslutadBestallningListItemFactory avslutadBestallningListItemFactory = new AvslutadBestallningListItemFactory(new BusinessDaysStub());

    @InjectMocks
    private BestallningServiceImpl bestallningService;

    @Before
    public void injectSpringBeans() {
        // Since we are not using a Spring context, and, injectmocks doesnt seem to work on subclasses (?),
        // DP inject/Autowire manually.
        ReflectionTestUtils.setField(bestallningListItemFactory, "businessDays", new BusinessDaysStub());
    }

    @Before
    public void setupMocks() {
        doNothing().when(logService).logList(anyList(), any());
        doNothing().when(patientNameEnricher).enrichWithPatientNames(anyList());
    }

    @Test
    public void testFilterListBestallningar() {
        when(userService.getUser()).thenReturn(ServiceTestUtil.buildUser());
        when(utredningRepository.findAllByBestallning_TilldeladVardenhetHsaId_AndArkiveradFalse(anyString(), anyString())).thenReturn(ServiceTestUtil.buildBestallningar(7));
        GetBestallningListResponse response = bestallningService.findOngoingBestallningarForVardenhet((IbVardenhet) ServiceTestUtil.buildUser().getCurrentlyLoggedInAt(),
                buildFilter(ListFilterStatus.ALL));
        assertEquals(7, response.getTotalCount());
    }

    @Test
    public void testFilterListBestallningarOrderByDesc() {
        when(userService.getUser()).thenReturn(ServiceTestUtil.buildUser());
        when(utredningRepository.findAllByBestallning_TilldeladVardenhetHsaId_AndArkiveradFalse(anyString(), anyString())).thenReturn(ServiceTestUtil.buildBestallningar(7));
        GetBestallningListResponse response = bestallningService.findOngoingBestallningarForVardenhet((IbVardenhet) ServiceTestUtil.buildUser().getCurrentlyLoggedInAt(),
                buildFilter(ListFilterStatus.ALL, null, null, "patientId", false));
        assertEquals(7, response.getTotalCount());

        // Check sort by patientId DESC
        int startIndex = 6;
        for (BestallningListItem bli : response.getBestallningar()) {
            assertEquals("19121212-121" + startIndex, bli.getPatientId());
            startIndex--;
        }
    }

    @Test
    public void testFilterListBestallningarWithFreeTextMatchingSingleBestallningInvanarId() {
        when(userService.getUser()).thenReturn(ServiceTestUtil.buildUser());
        when(utredningRepository.findAllByBestallning_TilldeladVardenhetHsaId_AndArkiveradFalse(anyString(), anyString())).thenReturn(ServiceTestUtil.buildBestallningar(7));
        GetBestallningListResponse response = bestallningService.findOngoingBestallningarForVardenhet((IbVardenhet) ServiceTestUtil.buildUser().getCurrentlyLoggedInAt(),
                buildFilter(ListFilterStatus.ALL, "19121212-1216"));
        assertEquals(1, response.getTotalCount());
    }

    @Test
    public void testFilterListBestallningarWithUnknownVg() {
        when(utredningRepository.findAllByBestallning_TilldeladVardenhetHsaId_AndArkiveradFalse(anyString(), anyString())).thenReturn(ServiceTestUtil.buildBestallningar(7));
        GetBestallningListResponse response = bestallningService.findOngoingBestallningarForVardenhet((IbVardenhet) ServiceTestUtil.buildUser().getCurrentlyLoggedInAt(),
                buildFilter(ListFilterStatus.ALL, null, "vg-other"));
        assertEquals(0, response.getTotalCount());
    }

    @Test
    public void testGetBestallning() {
        when(userService.getUser()).thenReturn(ServiceTestUtil.buildUser());
        when(utredningRepository.findById(anyLong())).thenReturn(Optional.of(ServiceTestUtil.buildBestallningar(1).get(0)));

        GetBestallningResponse response = bestallningService.getBestallning(0L, ServiceTestUtil.buildUser().getCurrentlyLoggedInAt());
    }

    @Test(expected = IbNotFoundException.class)
    public void testGetBestallninNotFound() {
        GetBestallningResponse response = bestallningService.getBestallning(0L, ServiceTestUtil.buildUser().getCurrentlyLoggedInAt());
    }

    @Test
    public void testGetBestallningDifferentVE() {
        IbUser user = ServiceTestUtil.buildUser("another-ve", "");
        when(userService.getUser()).thenReturn(user);
        when(utredningRepository.findById(anyLong())).thenReturn(Optional.of(ServiceTestUtil.buildBestallningar(1).get(0)));

        assertThatThrownBy(() -> bestallningService.getBestallning(0L, user.getCurrentlyLoggedInAt()))
                .isExactlyInstanceOf(IbAuthorizationException.class)
                .hasFieldOrPropertyWithValue("authorizationErrorCode", IbAuthorizationErrorCodeEnum.VARDENHET_MISMATCH);
    }

    @Test
    public void testGetBestallningOrgnrChanged() {
        IbUser user = ServiceTestUtil.buildUser("careunit-1", "another-orgnr");
        when(userService.getUser()).thenReturn(user);
        when(utredningRepository.findById(anyLong())).thenReturn(Optional.of(ServiceTestUtil.buildBestallningar(1).get(0)));

        assertThatThrownBy(() -> bestallningService.getBestallning(0L, user.getCurrentlyLoggedInAt()))
                .isExactlyInstanceOf(IbAuthorizationException.class)
                .hasFieldOrPropertyWithValue("authorizationErrorCode", IbAuthorizationErrorCodeEnum.VARDGIVARE_ORGNR_MISMATCH);
    }

    private ListBestallningRequest buildFilter(ListFilterStatus status, String freeText, String vgId, String orderBy, boolean isAsc) {
        ListBestallningRequest req = buildFilter(status, freeText, vgId);
        req.setOrderBy(orderBy);
        req.setOrderByAsc(isAsc);
        return req;
    }

    private ListBestallningRequest buildFilter(ListFilterStatus status, String freeText, String vgId) {
        ListBestallningRequest req = buildFilter(status, freeText);
        req.setVardgivareHsaId(vgId);
        return req;
    }

    private ListBestallningRequest buildFilter(ListFilterStatus status, String freeText) {
        ListBestallningRequest req = buildFilter(status);
        req.setFreeText(freeText);
        return req;
    }

    private ListBestallningRequest buildFilter(ListFilterStatus status) {
        ListBestallningRequest req = new ListBestallningRequest();
        req.setPageSize(10);
        req.setCurrentPage(0);
        req.setStatus(status.getId());
        return req;
    }
}
