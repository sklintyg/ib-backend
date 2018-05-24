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

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import se.inera.intyg.infra.integration.hsa.model.Vardenhet;
import se.inera.intyg.infra.integration.hsa.services.HsaOrganizationsService;
import se.inera.intyg.intygsbestallning.common.exception.IbAuthorizationException;
import se.inera.intyg.intygsbestallning.common.exception.IbErrorCodeEnum;
import se.inera.intyg.intygsbestallning.common.exception.IbNotFoundException;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.type.EndReason;
import se.inera.intyg.intygsbestallning.persistence.repository.UtredningRepository;
import se.inera.intyg.intygsbestallning.service.notification.MailNotificationService;
import se.inera.intyg.intygsbestallning.service.user.UserService;
import se.inera.intyg.intygsbestallning.service.util.BusinessDaysBean;
import se.inera.intyg.intygsbestallning.service.util.BusinessDaysStub;
import se.inera.intyg.intygsbestallning.service.utredning.dto.AssessmentRequest;
import se.inera.intyg.intygsbestallning.service.utredning.dto.EndUtredningRequest;
import se.inera.intyg.intygsbestallning.service.utredning.dto.OrderRequest;
import se.inera.intyg.intygsbestallning.service.utredning.dto.UpdateOrderRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.forfragan.InternForfraganListItem;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.forfragan.InternForfraganListItemFactory;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.utredning.GetUtredningResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.utredning.UtredningListItem;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.utredning.UtredningListItemFactory;
import se.riv.intygsbestallning.certificate.order.updateorder.v1.UpdateOrderType;

import javax.xml.ws.WebServiceException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static se.inera.intyg.intygsbestallning.common.util.RivtaTypesUtil.anII;
import static se.inera.intyg.intygsbestallning.persistence.model.ExternForfragan.ExternForfraganBuilder.anExternForfragan;
import static se.inera.intyg.intygsbestallning.persistence.model.Handlaggare.HandlaggareBuilder.aHandlaggare;
import static se.inera.intyg.intygsbestallning.persistence.model.InternForfragan.InternForfraganBuilder.anInternForfragan;
import static se.inera.intyg.intygsbestallning.persistence.model.Invanare.InvanareBuilder.anInvanare;
import static se.inera.intyg.intygsbestallning.persistence.model.TidigareUtforare.TidigareUtforareBuilder.aTidigareUtforare;
import static se.inera.intyg.intygsbestallning.persistence.model.Utredning.UtredningBuilder.anUtredning;
import static se.inera.intyg.intygsbestallning.persistence.model.type.HandlingUrsprungTyp.BESTALLNING;
import static se.inera.intyg.intygsbestallning.persistence.model.type.UtredningsTyp.AFU;
import static se.inera.intyg.intygsbestallning.persistence.model.type.UtredningsTyp.AFU_UTVIDGAD;
import static se.inera.intyg.intygsbestallning.persistence.model.type.UtredningsTyp.LIAG;
import static se.inera.intyg.intygsbestallning.service.utredning.dto.AssessmentRequest.AssessmentRequestBuilder.anAssessmentRequest;
import static se.inera.intyg.intygsbestallning.service.utredning.dto.Bestallare.BestallareBuilder.aBestallare;
import static se.inera.intyg.intygsbestallning.service.utredning.dto.EndUtredningRequest.EndUtredningRequestBuilder.anEndUtredningRequest;
import static se.inera.intyg.intygsbestallning.service.utredning.dto.OrderRequest.OrderRequestBuilder.anOrderRequest;
import static se.inera.intyg.intygsbestallning.testutil.TestDataGen.createBestallning;
import static se.inera.intyg.intygsbestallning.testutil.TestDataGen.createExternForfragan;
import static se.inera.intyg.intygsbestallning.testutil.TestDataGen.createHandlaggare;
import static se.inera.intyg.intygsbestallning.testutil.TestDataGen.createUpdateOrderType;
import static se.inera.intyg.intygsbestallning.testutil.TestDataGen.createUtredning;

@RunWith(MockitoJUnitRunner.class)
public class UtredningServiceImplTest {

    @Mock
    private UtredningRepository utredningRepository;

    @Mock
    private HsaOrganizationsService organizationUnitService;

    @Mock
    private UserService userService;

    @Mock
    private MailNotificationService mailNotificationService;

    @Spy
    private InternForfraganListItemFactory internForfraganListItemFactory = new InternForfraganListItemFactory(new BusinessDaysStub());

    @Spy
    private UtredningListItemFactory utredningListItemFactory = new UtredningListItemFactory(new BusinessDaysStub());

    @InjectMocks
    private UtredningServiceImpl utredningService;

    @Test
    public void findForfragningarForVardenhetHsaId() {
        final String enhetId = "enhet";
        final Long utredningId = 1L;
        // Almost bare minimum, converting is not in the scope of utredningService
        Utredning utr = anUtredning()
                .withUtredningsTyp(AFU)
                .withUtredningId(utredningId)
                .withExternForfragan(anExternForfragan()
                        .withInternForfraganList(ImmutableList.of(
                                anInternForfragan()
                                        .withVardenhetHsaId(enhetId)
                                        .build()))
                        .build())
                .build();
        when(utredningRepository.findAllByExternForfragan_InternForfraganList_VardenhetHsaId(enhetId)).thenReturn(ImmutableList.of(utr));

        List<InternForfraganListItem> response = utredningService.findForfragningarForVardenhetHsaId(enhetId);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(AFU.name(), response.get(0).getUtredningsTyp());
        assertEquals(utredningId, response.get(0).getUtredningsId());
    }

    @Test
    public void registerOrder() {
        final Long utredningId = 1L;

        when(utredningRepository.findById(utredningId)).thenReturn(Optional.of(anUtredning()
                .withUtredningId(utredningId)
                .withUtredningsTyp(AFU)
                .withInvanare(anInvanare()
                        .withPostort("invanarePostort")
                        .build())
                .withExternForfragan(anExternForfragan().build())
                .build()));

        OrderRequest order = anOrderRequest()
                .withUtredningsTyp(AFU)
                .withUtredningId(utredningId)
                .withTolkBehov(true)
                .withTolkSprak("sv")
                .withSyfte("syfte")
                .withOrderDate(LocalDate.of(2018, 1, 1))
                .withLastDateIntyg(LocalDate.of(2019, 1, 1))
                .withKommentar("kommentar")
                .withInvanarePersonnummer("personnummer")
                .withInvanareBehov("behov")
                .withInvanareBakgrund("bakgrund")
                .withHandling(true)
                .withEnhetId("enhet")
                .withAtgarder("atgarder")
                .withBestallare(aBestallare()
                        .withEmail("email")
                        .withAdress("adress")
                        .withFullstandigtNamn("fullstandigtNamn")
                        .withKontor("kontor")
                        .withKostnadsstalle("kostnadsstalle")
                        .withMyndighet("myndighet")
                        .withPostkod("postkod")
                        .withStad("stad")
                        .withTelefonnummer("telefonnummer")
                        .build())
                .build();

        Utredning response = utredningService.registerOrder(order);

        assertNotNull(response);
        assertEquals(utredningId, response.getUtredningId());
        assertTrue(response.getTolkBehov());
        assertEquals("sv", response.getTolkSprak());
        assertEquals(AFU, response.getUtredningsTyp());
        assertEquals("kommentar", response.getBestallning().get().getKommentar());
        assertEquals("atgarder", response.getBestallning().get().getPlaneradeAktiviteter());
        assertEquals("syfte", response.getBestallning().get().getSyfte());
        assertEquals("enhet", response.getBestallning().get().getTilldeladVardenhetHsaId());
        assertFalse(response.getIntygList().isEmpty());
        assertEquals(LocalDate.of(2019, 1, 1).atStartOfDay(), response.getIntygList().get(0).getSistaDatum());
        assertEquals(LocalDate.of(2018, 1, 1).atStartOfDay(), response.getBestallning().get().getOrderDatum());
        assertNull(response.getBestallning().get().getUppdateradDatum());
        assertEquals("behov", response.getInvanare().getSarskildaBehov());
        assertEquals("personnummer", response.getInvanare().getPersonId());
        assertEquals("bakgrund", response.getInvanare().getBakgrundNulage());
        assertEquals("invanarePostort", response.getInvanare().getPostort());
        assertNotNull("", response.getHandlingList());
        assertEquals(1, response.getHandlingList().size());
        assertNull(response.getHandlingList().get(0).getInkomDatum());
        assertNotNull("", response.getHandlingList().get(0).getSkickatDatum());
        assertEquals(BESTALLNING, response.getHandlingList().get(0).getUrsprung());
        assertEquals("adress", response.getHandlaggare().getAdress());
        assertEquals("email", response.getHandlaggare().getEmail());
        assertEquals("fullstandigtNamn", response.getHandlaggare().getFullstandigtNamn());
        assertEquals("kontor", response.getHandlaggare().getKontor());
        assertEquals("kostnadsstalle", response.getHandlaggare().getKostnadsstalle());
        assertEquals("myndighet", response.getHandlaggare().getMyndighet());
        assertEquals("postkod", response.getHandlaggare().getPostkod());
        assertEquals("stad", response.getHandlaggare().getStad());
        assertEquals("telefonnummer", response.getHandlaggare().getTelefonnummer());
    }

    @Test(expected = IllegalArgumentException.class)
    public void registerOrderNoForfragan() {
        final Long utredningId = 1L;

        when(utredningRepository.findById(utredningId)).thenReturn(Optional.of(anUtredning()
                .withUtredningId(utredningId)
                .withUtredningsTyp(AFU)
                .withInvanare(anInvanare()
                        .withPostort("invanarePostkod")
                        .build())
                .build()));

        OrderRequest order = anOrderRequest().withUtredningId(utredningId).build();

        utredningService.registerOrder(order);
    }

    @Test(expected = IbNotFoundException.class)
    public void registerOrderNoPreviousUtredning() {
        final Long utredningId = 1L;

        when(utredningRepository.findById(utredningId)).thenReturn(Optional.empty());

        OrderRequest order = anOrderRequest().withUtredningId(utredningId).build();

        utredningService.registerOrder(order);
    }

    @Test
    public void registerNewUtredning() {
        OrderRequest order = anOrderRequest()
                .withUtredningsTyp(LIAG)
                .withTolkBehov(true)
                .withTolkSprak("sv")
                .withSyfte("syfte")
                .withOrderDate(LocalDate.of(2018, 1, 1))
                .withLastDateIntyg(LocalDate.of(2019, 1, 1))
                .withKommentar("kommentar")
                .withInvanarePersonnummer("personnummer")
                .withInvanareBehov("behov")
                .withInvanareBakgrund("bakgrund")
                .withHandling(true)
                .withEnhetId("enhet")
                .withAtgarder("atgarder")
                .withBestallare(aBestallare()
                        .withEmail("email")
                        .withAdress("adress")
                        .withFullstandigtNamn("fullstandigtNamn")
                        .withKontor("kontor")
                        .withKostnadsstalle("kostnadsstalle")
                        .withMyndighet("myndighet")
                        .withPostkod("postkod")
                        .withStad("stad")
                        .withTelefonnummer("telefonnummer")
                        .build())
                .build();

        Utredning response = utredningService.registerNewUtredning(order);

        assertNotNull(response);
        assertTrue(response.getTolkBehov());
        assertEquals("sv", response.getTolkSprak());
        assertEquals(LIAG, response.getUtredningsTyp());
        assertEquals("kommentar", response.getBestallning().get().getKommentar());
        assertEquals("atgarder", response.getBestallning().get().getPlaneradeAktiviteter());
        assertEquals("syfte", response.getBestallning().get().getSyfte());
        assertEquals("enhet", response.getBestallning().get().getTilldeladVardenhetHsaId());
        assertFalse(response.getIntygList().isEmpty());
        assertEquals(LocalDate.of(2019, 1, 1).atStartOfDay(), response.getIntygList().get(0).getSistaDatum());
        assertEquals(LocalDate.of(2018, 1, 1).atStartOfDay(), response.getBestallning().get().getOrderDatum());
        assertNull(response.getBestallning().get().getUppdateradDatum());
        assertEquals("behov", response.getInvanare().getSarskildaBehov());
        assertEquals("personnummer", response.getInvanare().getPersonId());
        assertEquals("bakgrund", response.getInvanare().getBakgrundNulage());
        assertNull(response.getInvanare().getPostort());
        assertNotNull("", response.getHandlingList());
        assertEquals(1, response.getHandlingList().size());
        assertNull(response.getHandlingList().get(0).getInkomDatum());
        assertNotNull("", response.getHandlingList().get(0).getSkickatDatum());
        assertEquals(BESTALLNING, response.getHandlingList().get(0).getUrsprung());
        assertEquals("adress", response.getHandlaggare().getAdress());
        assertEquals("email", response.getHandlaggare().getEmail());
        assertEquals("fullstandigtNamn", response.getHandlaggare().getFullstandigtNamn());
        assertEquals("kontor", response.getHandlaggare().getKontor());
        assertEquals("kostnadsstalle", response.getHandlaggare().getKostnadsstalle());
        assertEquals("myndighet", response.getHandlaggare().getMyndighet());
        assertEquals("postkod", response.getHandlaggare().getPostkod());
        assertEquals("stad", response.getHandlaggare().getStad());
        assertEquals("telefonnummer", response.getHandlaggare().getTelefonnummer());
    }

    @Test
    public void registerNewUtredningFromRequestHealthCarePerformerForAssesment() {

        final LocalDateTime dateTime = LocalDateTime.of(2018, 12, 12, 12, 12, 12, 12);

        final Utredning utredning = anUtredning()
                .withUtredningId(1L)
                .withUtredningsTyp(AFU_UTVIDGAD)
                .withExternForfragan(anExternForfragan()
                        .withLandstingHsaId("id")
                        .withInkomDatum(LocalDateTime.now())
                        .withBesvarasSenastDatum(dateTime)
                        .withKommentar("kommentar")
                        .build())
                .withHandlaggare(aHandlaggare()
                        .withAdress("address")
                        .withEmail("email")
                        .withFullstandigtNamn("fullstandigtNamn")
                        .withKontor("kontor")
                        .withKostnadsstalle("kostnadsstalle")
                        .withMyndighet("myndighet")
                        .withPostkod("postkod")
                        .withStad("stad")
                        .withTelefonnummer("telefonnummer")
                        .build())
                .build();

        doReturn(utredning)
                .when(utredningRepository)
                .save(any(Utredning.class));

        final AssessmentRequest request = anAssessmentRequest()
                .withUtredningsTyp(AFU_UTVIDGAD)
                .withLandstingHsaId("id")
                .withInvanareTidigareUtforare(ImmutableList.of("1", "2", "3"))
                .withInvanareSarskildaBehov("sarskiltBehov")
                .withInvanarePostort("postort")
                .withBesvaraSenastDatum(dateTime)
                .withKommentar("kommentar")
                .withTolkBehov(true)
                .withTolkSprak("tolksprak")
                .withBestallare(aBestallare()
                        .withAdress("adress")
                        .withEmail("email")
                        .withFullstandigtNamn("fullstandigtNamn")
                        .withKontor("kontor")
                        .withKostnadsstalle("kostnadsstalle")
                        .withMyndighet("myndighet")
                        .withPostkod("postkod")
                        .withStad("stad")
                        .withTelefonnummer("telefonnummer")
                        .build())
                .build();

        final Utredning sparadUtredning = utredningService.registerNewUtredning(request);

        assertEquals(utredning, sparadUtredning);
    }

    @Test
    public void uppdateraOrderOk() {

        final String tolkSprak = "tolkSprak";

        Utredning modifieradUtrening = Utredning.copyFrom(createUtredning());
        modifieradUtrening.setTolkBehov(true);
        modifieradUtrening.setTolkSprak(tolkSprak);

        final UpdateOrderRequest updateOrderRequest = UpdateOrderRequest.from(createUpdateOrderType(true, tolkSprak));

        doReturn(Optional.of(createUtredning()))
                .when(utredningRepository)
                .findById(anyLong());

        doReturn(modifieradUtrening)
                .when(utredningRepository)
                .save(any(Utredning.class));

        final Utredning uppdateradUtredning = utredningService.updateOrder(updateOrderRequest);
        assertEquals(AFU_UTVIDGAD, uppdateradUtredning.getUtredningsTyp());

        assertEquals(createBestallning(), uppdateradUtredning.getBestallning().orElse(null));
        assertEquals(createHandlaggare(), uppdateradUtredning.getHandlaggare());
        assertEquals(createExternForfragan(), uppdateradUtredning.getExternForfragan());
        assertTrue(uppdateradUtredning.getTolkBehov());
        assertEquals(tolkSprak, uppdateradUtredning.getTolkSprak());
    }

    @Test
    public void uppdateraOrderUtanForandringNok() {

        doReturn(Optional.of(createUtredning()))
                .when(utredningRepository)
                .findById(anyLong());

        UpdateOrderType update = new UpdateOrderType();
        update.setAssessmentId(anII("root", "1"));

        assertThatThrownBy(() -> utredningService.updateOrder(UpdateOrderRequest.from(update)));
    }

    @Test
    public void testGetUtredningSuccess() {
        final Long utredningId = 1L;
        final String landstingHsaId = "landstingHsaId";

        when(utredningRepository.findById(utredningId)).thenReturn(Optional.of(anUtredning()
                .withUtredningId(utredningId)
                .withUtredningsTyp(AFU)
                .withExternForfragan(anExternForfragan()
                        .withLandstingHsaId(landstingHsaId)
                        .withInkomDatum(LocalDateTime.now())
                        .withBesvarasSenastDatum(LocalDateTime.now())
                        .build())
                .withInvanare(anInvanare()
                        .build())
                .withHandlaggare(aHandlaggare()
                        .build())
                .build()));

        GetUtredningResponse response = utredningService.getExternForfragan(utredningId, landstingHsaId);

        assertNotNull(response);
        assertEquals(utredningId, response.getUtredningsId());
    }

    @Test
    public void testGetUtredningWithInternforfraganSuccess() {
        final Long utredningId = 1L;
        final String landstingHsaId = "landstingHsaId";
        final String vardenhetHsaId = "vardenhetHsaId";
        final String vardenhetNamn = "vardenhetens namn";

        when(organizationUnitService.getVardenhet(vardenhetHsaId)).thenReturn(new Vardenhet(vardenhetHsaId, vardenhetNamn));

        when(utredningRepository.findById(utredningId)).thenReturn(Optional.of(anUtredning()
                .withUtredningId(utredningId)
                .withUtredningsTyp(AFU)
                .withExternForfragan(anExternForfragan()
                        .withLandstingHsaId(landstingHsaId)
                        .withInkomDatum(LocalDateTime.now())
                        .withBesvarasSenastDatum(LocalDateTime.now())
                        .withInternForfraganList(ImmutableList.of(anInternForfragan()
                                .withVardenhetHsaId(vardenhetHsaId)
                                .build()))
                        .build())
                .withInvanare(anInvanare()
                        .build())
                .withHandlaggare(aHandlaggare()
                        .build())
                .build()));

        GetUtredningResponse response = utredningService.getExternForfragan(utredningId, landstingHsaId);

        assertNotNull(response);
        assertEquals(utredningId, response.getUtredningsId());
        assertEquals(vardenhetNamn, response.getInternForfraganList().get(0).getVardenhetNamn());
    }

    @Test
    public void testGetUtredningWithTidigareUtforareHsaLookupFails() {
        final Long utredningId = 1L;
        final String landstingHsaId = "landstingHsaId";
        final String tidigareVardenhetHsaId1 = "vardenhetHsaId1";
        final String tidigareVardenhetNamn1 = "vardenhetens namn";
        final String tidigareVardenhetHsaId2 = "vardenhetHsaId2";
        final String hsaError2 = "Fel från HSA";

        when(organizationUnitService.getVardenhet(tidigareVardenhetHsaId1))
                .thenReturn(new Vardenhet(tidigareVardenhetHsaId1, tidigareVardenhetNamn1));
        when(organizationUnitService.getVardenhet(tidigareVardenhetHsaId2)).thenThrow(new WebServiceException(hsaError2));

        when(utredningRepository.findById(utredningId)).thenReturn(Optional.of(anUtredning()
                .withUtredningId(utredningId)
                .withUtredningsTyp(AFU)
                .withExternForfragan(anExternForfragan()
                        .withLandstingHsaId(landstingHsaId)
                        .withInkomDatum(LocalDateTime.now())
                        .withBesvarasSenastDatum(LocalDateTime.now())
                        .build())
                .withInvanare(anInvanare()
                        .withTidigareUtforare(ImmutableList.of(aTidigareUtforare()
                                .withTidigareEnhetId(tidigareVardenhetHsaId1)
                                .build(), aTidigareUtforare()
                                .withTidigareEnhetId(tidigareVardenhetHsaId2)
                                .build()))
                        .build())
                .withHandlaggare(aHandlaggare()
                        .build())
                .build()));

        GetUtredningResponse response = utredningService.getExternForfragan(utredningId, landstingHsaId);

        assertNotNull(response);
        assertEquals(utredningId, response.getUtredningsId());
        assertEquals(tidigareVardenhetNamn1, response.getTidigareEnheter().get(0).getVardenhetNamn());
        assertNull(response.getTidigareEnheter().get(0).getVardenhetFelmeddelande());
        assertNull(response.getTidigareEnheter().get(1).getVardenhetNamn());
        assertEquals(hsaError2, response.getTidigareEnheter().get(1).getVardenhetFelmeddelande());
    }

    @Test(expected = IbAuthorizationException.class)
    public void testGetUtredningIncorrectLandsting() {
        final Long utredningId = 1L;
        final String landstingHsaId = "landstingHsaId";
        when(utredningRepository.findById(utredningId)).thenReturn(Optional.of(anUtredning()
                .withUtredningId(utredningId)
                .withExternForfragan(anExternForfragan()
                        .withLandstingHsaId("wrongId")
                        .build())
                .build()));
        utredningService.getExternForfragan(utredningId, landstingHsaId);
    }

    @Test(expected = IbNotFoundException.class)
    public void testGetUtredningNoUtredning() {
        final Long utredningId = 1L;
        final String landstingHsaId = "landstingHsaId";
        when(utredningRepository.findById(utredningId)).thenReturn(Optional.empty());
        utredningService.getExternForfragan(utredningId, landstingHsaId);
    }

    @Test
    public void testFindUtredningarByLandstingHsaId() {
        final String landstingHsaId = "landstingHsaId";
        when(utredningRepository.findAllByExternForfragan_LandstingHsaId(landstingHsaId)).thenReturn(
                ImmutableList.of(anUtredning()
                        .withUtredningId(1L)
                        .withUtredningsTyp(AFU)
                        .withExternForfragan(anExternForfragan()
                                .withLandstingHsaId(landstingHsaId)
                                .withBesvarasSenastDatum(LocalDateTime.now())
                                .build())
                        .withInvanare(anInvanare()
                                .withPersonId("personnummer")
                                .build())
                        .build()));

        List<UtredningListItem> response = utredningService.findExternForfraganByLandstingHsaId(landstingHsaId);

        assertNotNull(response);
        assertEquals(1, response.size());
        // assertEquals(landstingHsaId, response.get(0).getVardenhetNamn());
        assertEquals(Long.valueOf(1L), response.get(0).getUtredningsId());
        assertEquals(AFU.name(), response.get(0).getUtredningsTyp());
    }

    @Test
    public void testEndUtredningSuccess() {
        final Long utredningId = 1L;

        when(utredningRepository.findById(utredningId)).thenReturn(Optional.of(anUtredning()
                .withUtredningId(utredningId)
                .build()));

        EndUtredningRequest request = anEndUtredningRequest()
                .withUtredningId(utredningId)
                .withEndReason(EndReason.JAV)
                .build();

        utredningService.endUtredning(request);

        ArgumentCaptor<Utredning> captor = ArgumentCaptor.forClass(Utredning.class);
        verify(utredningRepository).save(captor.capture());

        Utredning utredning = captor.getValue();
        assertNotNull(utredning);
        assertEquals(utredningId, utredning.getUtredningId());
        assertNotNull(utredning.getAvbrutenDatum());
        assertEquals(EndReason.JAV, utredning.getAvbrutenAnledning());
    }

    @Test(expected = IbNotFoundException.class)
    public void testEndUtredningFailUtredningNotExisting() {
        final Long utredningId = 1L;

        when(utredningRepository.findById(utredningId)).thenReturn(Optional.empty());

        EndUtredningRequest request = anEndUtredningRequest()
                .withUtredningId(utredningId)
                .withEndReason(EndReason.JAV)
                .build();

        utredningService.endUtredning(request);
    }

    @Test(expected = IbServiceException.class)
    public void testEndUtredningFailAlreadyEnded() {
        final Long utredningId = 1L;

        when(utredningRepository.findById(utredningId)).thenReturn(Optional.of(anUtredning()
                .withUtredningId(utredningId)
                .withAvbrutenDatum(LocalDateTime.now())
                .build()));

        EndUtredningRequest request = anEndUtredningRequest()
                .withUtredningId(utredningId)
                .withEndReason(EndReason.JAV)
                .build();

        try {
            utredningService.endUtredning(request);
        } catch (IbServiceException ise) {
            assertEquals(IbErrorCodeEnum.ALREADY_EXISTS, ise.getErrorCode());
            throw ise;
        }
    }

}
