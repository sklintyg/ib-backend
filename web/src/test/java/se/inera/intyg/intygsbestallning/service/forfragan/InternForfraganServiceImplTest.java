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

import static junit.framework.Assert.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static se.inera.intyg.intygsbestallning.persistence.model.ExternForfragan.ExternForfraganBuilder.anExternForfragan;
import static se.inera.intyg.intygsbestallning.persistence.model.ForfraganSvar.ForfraganSvarBuilder.aForfraganSvar;
import static se.inera.intyg.intygsbestallning.persistence.model.Handlaggare.HandlaggareBuilder.aHandlaggare;
import static se.inera.intyg.intygsbestallning.persistence.model.InternForfragan.InternForfraganBuilder.anInternForfragan;
import static se.inera.intyg.intygsbestallning.persistence.model.Invanare.InvanareBuilder.anInvanare;
import static se.inera.intyg.intygsbestallning.persistence.model.Utredning.UtredningBuilder.anUtredning;
import static se.inera.intyg.intygsbestallning.persistence.model.type.UtredningsTyp.AFU;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import org.springframework.test.util.ReflectionTestUtils;
import se.inera.intyg.infra.integration.hsa.model.Vardenhet;
import se.inera.intyg.infra.integration.hsa.services.HsaOrganizationsService;
import se.inera.intyg.infra.security.common.model.Feature;
import se.inera.intyg.intygsbestallning.auth.IbUser;
import se.inera.intyg.intygsbestallning.common.exception.IbAuthorizationException;
import se.inera.intyg.intygsbestallning.common.exception.IbNotFoundException;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.integration.myndighet.service.MyndighetIntegrationService;
import se.inera.intyg.intygsbestallning.persistence.model.InternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.status.InternForfraganStatus;
import se.inera.intyg.intygsbestallning.persistence.model.status.UtredningStatus;
import se.inera.intyg.intygsbestallning.persistence.model.type.HandelseTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.RegiFormTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.SvarTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.UtforareTyp;
import se.inera.intyg.intygsbestallning.persistence.repository.UtredningRepository;
import se.inera.intyg.intygsbestallning.service.notifiering.send.NotifieringSendService;
import se.inera.intyg.intygsbestallning.service.user.UserService;
import se.inera.intyg.intygsbestallning.service.util.BusinessDaysBean;
import se.inera.intyg.intygsbestallning.service.util.BusinessDaysStub;
import se.inera.intyg.intygsbestallning.service.utredning.ServiceTestUtil;
import se.inera.intyg.intygsbestallning.service.utredning.UtredningService;
import se.inera.intyg.intygsbestallning.service.vardgivare.VardgivareService;
import se.inera.intyg.intygsbestallning.service.vardgivare.dto.VardenhetItem;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.forfragan.CreateInternForfraganRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.forfragan.ForfraganSvarRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.forfragan.GetInternForfraganResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.forfragan.InternForfraganListItemFactory;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.forfragan.InternForfraganSvarItem;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.forfragan.TilldelaDirektRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.utredning.GetUtredningResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.vardenhet.GetVardenheterForVardgivareResponse;

@RunWith(MockitoJUnitRunner.class)
public class InternForfraganServiceImplTest {

    private static final String INTERNFORFRAGAN_KOMMENTAR = "Svarskommentar";
    @Mock
    private UtredningRepository utredningRepository;

    @Mock
    private HsaOrganizationsService organizationUnitService;

    @Mock
    private UserService userService;

    @Mock
    private UtredningService utredningService;

    @Mock
    private NotifieringSendService notifieringSendService;

    @Mock
    private VardgivareService vardgivareService;

    @Mock
    private ExternForfraganService externForfraganService;

    @Mock
    private MyndighetIntegrationService myndighetIntegrationService;

    @Spy
    private BusinessDaysBean businessDays = new BusinessDaysStub();

    @Spy
    private InternForfraganListItemFactory internForfraganListItemFactory = new InternForfraganListItemFactory();

    @InjectMocks
    private InternForfraganServiceImpl internForfraganService;

    @Before
    public void injectSpringBeans() {
        // Since we are not using a Spring context, and, injectmocks doesnt seem to work on subclasses (?),
        // DP inject/Autowire manually.
        ReflectionTestUtils.setField(internForfraganListItemFactory, "businessDays", new BusinessDaysStub());
    }

    @Test
    public void testCreateInternForfraganSuccess() {
        final Long utredningId = 1L;
        final String landstingHsaId = "landstingHsaId";
        final String kommentar = "Ingen kommentar";
        final String vardenhetId1 = "vardenhetId1";
        final String vardenhetId2 = "vardenhetId2";
        final String vardenhetNamn1 = "vardenhetId1";
        final String vardenhetNamn2 = "vardenhetId2";
        final String userName = "TestUser";
        Utredning utredning = anUtredning()
                .withUtredningId(utredningId)
                .withUtredningsTyp(AFU)
                .withExternForfragan(anExternForfragan()
                        .withLandstingHsaId(landstingHsaId)
                        .withInkomDatum(LocalDateTime.now())
                        .withBesvarasSenastDatum(LocalDateTime.now())
                        .build())
                .withInvanare(anInvanare()
                        .withPersonId("personnummer")
                        .build())
                .withHandlaggare(aHandlaggare()
                        .build())
                .build();
        when(utredningRepository.findById(utredningId)).thenReturn(Optional.of(utredning));

        when(userService.getUser()).thenReturn(new IbUser("", userName));

        when(organizationUnitService.getVardenhet(vardenhetId1)).thenReturn(new Vardenhet(vardenhetId1, vardenhetNamn1));
        when(organizationUnitService.getVardenhet(vardenhetId2)).thenReturn(new Vardenhet(vardenhetId2, vardenhetNamn2));

        CreateInternForfraganRequest request = new CreateInternForfraganRequest();
        request.setVardenheter(ImmutableList.of(vardenhetId1, vardenhetId2));
        request.setKommentar(kommentar);
        doReturn(utredning).when(utredningRepository).saveUtredning(any(Utredning.class));

        GetUtredningResponse response = internForfraganService.createInternForfragan(utredningId, landstingHsaId, request);


        assertEquals(UtredningStatus.VANTAR_PA_SVAR, response.getStatus());
        assertEquals(vardenhetId1, response.getInternForfraganList().get(0).getVardenhetHsaId());
        assertEquals(vardenhetNamn1, response.getInternForfraganList().get(0).getVardenhetNamn());
        assertEquals(InternForfraganStatus.INKOMMEN, response.getInternForfraganList().get(0).getStatus());
        assertEquals(vardenhetId2, response.getInternForfraganList().get(1).getVardenhetHsaId());
        assertEquals(vardenhetNamn2, response.getInternForfraganList().get(1).getVardenhetNamn());
        assertEquals(InternForfraganStatus.INKOMMEN, response.getInternForfraganList().get(1).getStatus());

        ArgumentCaptor<Utredning> utredningArgument = ArgumentCaptor.forClass(Utredning.class);
        verify(utredningRepository).saveUtredning(utredningArgument.capture());

        assertEquals(kommentar, utredning.getExternForfragan().get().getInternForfraganList().get(0).getKommentar());
        assertEquals(kommentar, utredning.getExternForfragan().get().getInternForfraganList().get(1).getKommentar());

        assertEquals(2, utredning.getExternForfragan().get().getInternForfraganList().size());
        assertEquals(vardenhetId1, utredning.getExternForfragan().get().getInternForfraganList().get(0).getVardenhetHsaId());
        assertEquals(vardenhetId2, utredning.getExternForfragan().get().getInternForfraganList().get(1).getVardenhetHsaId());

        assertEquals(2, utredning.getHandelseList().size());
        assertEquals(HandelseTyp.INTERNFORFRAGAN_SKICKAD, utredning.getHandelseList().get(0).getHandelseTyp());
        assertEquals(userName, utredning.getHandelseList().get(0).getAnvandare());
        assertEquals("Förfrågan skickades till " + vardenhetNamn1, utredning.getHandelseList().get(0).getHandelseText());
        assertEquals(HandelseTyp.INTERNFORFRAGAN_SKICKAD, utredning.getHandelseList().get(1).getHandelseTyp());
        assertEquals(userName, utredning.getHandelseList().get(1).getAnvandare());
        assertEquals("Förfrågan skickades till " + vardenhetNamn2, utredning.getHandelseList().get(1).getHandelseText());
        verify(notifieringSendService, times(2)).notifieraVardenhetNyInternforfragan(any(Utredning.class), any(InternForfragan.class));
    }

    @Test(expected = IbNotFoundException.class)
    public void testCreateInternForfraganFailUtredningNotExisting() {
        final Long utredningId = 1L;
        final String landstingHsaId = "landstingHsaId";

        when(utredningRepository.findById(utredningId)).thenReturn(Optional.empty());

        internForfraganService.createInternForfragan(utredningId, landstingHsaId, new CreateInternForfraganRequest());
    }

    @Test(expected = IbAuthorizationException.class)
    public void testCreateInternForfraganFailDifferentLandsting() {
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
                        .withPersonId("personnummer")
                        .build())
                .withHandlaggare(aHandlaggare()
                        .build())
                .build()));

        internForfraganService.createInternForfragan(utredningId, "annatLandsting", new CreateInternForfraganRequest());
    }

    @Test(expected = IbServiceException.class)
    public void testCreateInternForfraganFailIncorrectState() {
        final Long utredningId = 1L;
        final String landstingHsaId = "landstingHsaId";
        final String vardenhetId1 = "vardenhetId1";

        when(utredningRepository.findById(utredningId)).thenReturn(Optional.of(anUtredning()
                .withUtredningId(utredningId)
                .withUtredningsTyp(AFU)
                .withExternForfragan(anExternForfragan()
                        .withLandstingHsaId(landstingHsaId)
                        .withInkomDatum(LocalDateTime.now())
                        .withBesvarasSenastDatum(LocalDateTime.now())
                        .withInternForfraganList(ImmutableList.of(anInternForfragan()
                                .withTilldeladDatum(LocalDateTime.now())
                                .withForfraganSvar(aForfraganSvar()
                                        .withSvarTyp(SvarTyp.ACCEPTERA)
                                        .build())
                                .build()))
                        .build())
                .withInvanare(anInvanare()
                        .withPersonId("personnummer")
                        .build())
                .withHandlaggare(aHandlaggare()
                        .build())
                .build()));

        CreateInternForfraganRequest request = new CreateInternForfraganRequest();
        request.setVardenheter(ImmutableList.of(vardenhetId1));

        internForfraganService.createInternForfragan(utredningId, landstingHsaId, request);
    }

    @Test(expected = IbServiceException.class)
    public void testCreateInternForfraganFailNoVardenhetSelected() {
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
                        .withPersonId("personnummer")
                        .build())
                .withHandlaggare(aHandlaggare()
                        .build())
                .build()));

        internForfraganService.createInternForfragan(utredningId, landstingHsaId, new CreateInternForfraganRequest());
    }

    @Test
    public void testTilldelaDirektSuccess() {
        final Long utredningId = 1L;
        final String landstingHsaId = "landstingHsaId";
        final String kommentar = "Ingen kommentar";
        final String vardenhetId1 = "vardenhetId1";
        final String vardenhetNamn1 = "vardenhetId1";
        final String userName = "TestUser";

        when(utredningRepository.findById(utredningId)).thenReturn(Optional.of(anUtredning()
                .withUtredningId(utredningId)
                .withUtredningsTyp(AFU)
                .withExternForfragan(anExternForfragan()
                        .withLandstingHsaId(landstingHsaId)
                        .withInkomDatum(LocalDateTime.now())
                        .withBesvarasSenastDatum(LocalDateTime.now())
                        .build())
                .withInvanare(anInvanare()
                        .withPersonId("personnummer")
                        .build())
                .withHandlaggare(aHandlaggare()
                        .build())
                .build()));

        when(organizationUnitService.getVardenhet(vardenhetId1)).thenReturn(new Vardenhet(vardenhetId1, vardenhetNamn1));

        TilldelaDirektRequest request = new TilldelaDirektRequest();
        request.setVardenhet(vardenhetId1);
        request.setKommentar(kommentar);

        GetUtredningResponse response = internForfraganService.tilldelaDirekt(utredningId, landstingHsaId, request);
        assertEquals(UtredningStatus.TILLDELA_UTREDNING, response.getStatus());
        assertEquals(InternForfraganStatus.DIREKTTILLDELAD, response.getInternForfraganList().get(0).getStatus());
        assertEquals(vardenhetId1, response.getInternForfraganList().get(0).getVardenhetHsaId());
        assertEquals(vardenhetNamn1, response.getInternForfraganList().get(0).getVardenhetNamn());

        ArgumentCaptor<Utredning> utredningArgument = ArgumentCaptor.forClass(Utredning.class);
        verify(utredningRepository).saveUtredning(utredningArgument.capture());

        Utredning utredning = utredningArgument.getValue();
        assertEquals(kommentar, utredning.getExternForfragan().get().getInternForfraganList().get(0).getKommentar());
        assertTrue(utredning.getExternForfragan().get().getInternForfraganList().get(0).getDirekttilldelad());

        assertEquals(1, utredning.getExternForfragan().get().getInternForfraganList().size());
        assertEquals(vardenhetId1, utredning.getExternForfragan().get().getInternForfraganList().get(0).getVardenhetHsaId());
    }

    @Test(expected = IbNotFoundException.class)
    public void testTilldelaDirektFailUtredningNotExisting() {
        final Long utredningId = 1L;
        final String landstingHsaId = "landstingHsaId";

        when(utredningRepository.findById(utredningId)).thenReturn(Optional.empty());

        internForfraganService.tilldelaDirekt(utredningId, landstingHsaId, new TilldelaDirektRequest());
    }

    @Test(expected = IbAuthorizationException.class)
    public void testTilldelaDirektFailDifferentLandsting() {
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
                        .withPersonId("personnummer")
                        .build())
                .withHandlaggare(aHandlaggare()
                        .build())
                .build()));

        internForfraganService.tilldelaDirekt(utredningId, "annatLandsting", new TilldelaDirektRequest());
    }

    @Test(expected = IbServiceException.class)
    public void testTilldelaDirektFailIncorrectState() {
        final Long utredningId = 1L;
        final String landstingHsaId = "landstingHsaId";
        final String vardenhetId1 = "vardenhetId1";

        when(utredningRepository.findById(utredningId)).thenReturn(Optional.of(anUtredning()
                .withUtredningId(utredningId)
                .withUtredningsTyp(AFU)
                .withExternForfragan(anExternForfragan()
                        .withLandstingHsaId(landstingHsaId)
                        .withInkomDatum(LocalDateTime.now())
                        .withBesvarasSenastDatum(LocalDateTime.now())
                        .withInternForfraganList(ImmutableList.of(anInternForfragan()
                                .withTilldeladDatum(LocalDateTime.now())
                                .withForfraganSvar(aForfraganSvar()
                                        .withSvarTyp(SvarTyp.ACCEPTERA)
                                        .build())
                                .build()))
                        .build())
                .withInvanare(anInvanare()
                        .withPersonId("personnummer")
                        .build())
                .withHandlaggare(aHandlaggare()
                        .build())
                .build()));

        TilldelaDirektRequest request = new TilldelaDirektRequest();
        request.setVardenhet(vardenhetId1);

        internForfraganService.tilldelaDirekt(utredningId, landstingHsaId, request);
    }

    @Test(expected = IbServiceException.class)
    public void testTilldelaDirektFailInternForfraganAlreadyExistsForVardenhet() {
        final Long utredningId = 1L;
        final String landstingHsaId = "landstingHsaId";
        final String vardenhetId1 = "vardenhetId1";

        when(utredningRepository.findById(utredningId)).thenReturn(Optional.of(anUtredning()
                .withUtredningId(utredningId)
                .withUtredningsTyp(AFU)
                .withExternForfragan(anExternForfragan()
                        .withLandstingHsaId(landstingHsaId)
                        .withInkomDatum(LocalDateTime.now())
                        .withBesvarasSenastDatum(LocalDateTime.now())
                        .withInternForfraganList(ImmutableList.of(anInternForfragan()
                                .withVardenhetHsaId(vardenhetId1)
                                .build()))
                        .build())
                .withInvanare(anInvanare()
                        .withPersonId("personnummer")
                        .build())
                .withHandlaggare(aHandlaggare()
                        .build())
                .build()));

        TilldelaDirektRequest request = new TilldelaDirektRequest();
        request.setVardenhet(vardenhetId1);

        internForfraganService.tilldelaDirekt(utredningId, landstingHsaId, request);
    }

    @Test(expected = IbServiceException.class)
    public void testTilldelaDirektFailNoVardenhetSelected() {
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
                        .withPersonId("personnummer")
                        .build())
                .withHandlaggare(aHandlaggare()
                        .build())
                .build()));

        internForfraganService.tilldelaDirekt(utredningId, landstingHsaId, new TilldelaDirektRequest());
    }

    @Test(expected = IbNotFoundException.class)
    public void testGetInternForfraganFailsForNonexistingUtredning() {
        final Long utredningId = 1L;
        final String vardenhetId = "vardenhetId";

        when(utredningRepository.findById(utredningId)).thenReturn(Optional.empty());

        internForfraganService.getInternForfragan(utredningId, vardenhetId);

    }

    @Test(expected = IbNotFoundException.class)
    public void testGetInternForfraganFailsForNonexistingInternforfragan() {
        final Long utredningId = 1L;
        final String landstingHsaId = "landstingHsaId";
        final String vardenhetId = "vardenhetIdHsaId";
        final String otherVardenhet = "otherVardenehetHsaId";

        when(utredningRepository.findById(utredningId)).thenReturn(Optional.of(anUtredning()
                .withUtredningId(utredningId)
                .withUtredningsTyp(AFU)
                .withExternForfragan(anExternForfragan()
                        .withLandstingHsaId(landstingHsaId)
                        .withInternForfraganList(ImmutableList.of(anInternForfragan()
                                .withVardenhetHsaId(otherVardenhet)
                                .build()))
                        .build())
                .build()));

        internForfraganService.getInternForfragan(utredningId, vardenhetId);

    }

    @Test
    public void testGetInternForfraganSuccess() {
        final Long utredningId = 1L;
        final String landstingHsaId = "landstingHsaId";
        final String vardenhetId = "vardenhetIdHsaId";
        String landstingKommentar = "Just do it!";
        String vardAdminKommentar = " Wer'e on it!";
        String utforarAdress = "Vårdgatan 3";

        when(utredningRepository.findById(utredningId)).thenReturn(Optional.of(anUtredning()
                .withUtredningId(utredningId)
                .withUtredningsTyp(AFU)
                .withInvanare(anInvanare()
                        .withPersonId("personnummer")
                        .build())
                .withHandlaggare(aHandlaggare()
                        .build())
                .withExternForfragan(anExternForfragan()
                        .withLandstingHsaId(landstingHsaId)
                        .withBesvarasSenastDatum(LocalDateTime.now())
                        .withInkomDatum(LocalDateTime.now())

                        .withInternForfraganList(ImmutableList.of(anInternForfragan()
                                .withVardenhetHsaId(vardenhetId)
                                .withKommentar(landstingKommentar)
                                .withForfraganSvar(
                                        aForfraganSvar()
                                                .withKommentar(vardAdminKommentar)
                                                .withSvarTyp(SvarTyp.ACCEPTERA)
                                                .withUtforareAdress(utforarAdress)
                                                .build())
                                .build()))
                        .build())
                .build()));

        GetVardenheterForVardgivareResponse vardenheter = new GetVardenheterForVardgivareResponse();
        vardenheter.setEgetLandsting(Arrays.asList(buildVardenhet(vardenhetId, RegiFormTyp.EGET_LANDSTING)));
        when(vardgivareService.listVardenheterForVardgivare(anyString())).thenReturn(vardenheter);

        when(userService.getUser()).thenReturn(ServiceTestUtil.buildUser());

        final GetInternForfraganResponse response = internForfraganService.getInternForfragan(utredningId, vardenhetId);

        assertEquals(utredningId, response.getUtredning().getUtredningsId());

        assertEquals(0, response.getUtredning().getInternForfraganList().size());
        assertTrue(response.getInternForfragan().isRejectIsProhibited());
        assertEquals(landstingKommentar, response.getInternForfragan().getKommentar());
        assertEquals(landstingHsaId, response.getInternForfragan().getVardgivareHsaId());
        assertEquals(SvarTyp.ACCEPTERA, response.getInternForfraganSvar().getSvarTyp());
        assertEquals(vardAdminKommentar, response.getInternForfraganSvar().getKommentar());
        assertEquals(utforarAdress, response.getInternForfraganSvar().getUtforareAdress());

    }

    @Test(expected = IbNotFoundException.class)
    public void testBesvaraInternForfraganFailsForNonexistingUtredning() {
        final Long utredningId = 1L;
        when(utredningRepository.findById(utredningId)).thenReturn(Optional.empty());
        internForfraganService.besvaraInternForfragan(utredningId, buildValidInternForfraganSvarRequest(SvarTyp.ACCEPTERA, 99L));
    }

    @Test(expected = IbNotFoundException.class)
    public void testBesvaraInternForfraganFailsForNonexistingInternForfragan() {
        final Long utredningId = 1L;
        final String landstingHsaId = "landstingHsaId";
        Long internForfragaId = 2L;
        when(utredningRepository.findById(utredningId)).thenReturn(Optional.of(anUtredning()
                .withUtredningId(utredningId)
                .withUtredningsTyp(AFU)
                .withExternForfragan(anExternForfragan()
                        .withLandstingHsaId(landstingHsaId)
                        .withInternForfraganList(ImmutableList.of(anInternForfragan()
                                .withId(internForfragaId)
                                .build()))
                        .build())
                .build()));
        internForfraganService.besvaraInternForfragan(utredningId, buildValidInternForfraganSvarRequest(SvarTyp.ACCEPTERA, 99L));
    }

    @Test
    public void testBesvaraInternForfraganRequestValidation() {

        ForfraganSvarRequest forfraganSvarRequest = buildValidInternForfraganSvarRequest(SvarTyp.ACCEPTERA, 1L);
        assertNull(internForfraganService.validateSvarRequest(forfraganSvarRequest));

        // Svarstyp
        forfraganSvarRequest = buildValidInternForfraganSvarRequest(SvarTyp.ACCEPTERA, 1L);
        forfraganSvarRequest.setSvarTyp("UNKNOWN");
        assertTrue(internForfraganService.validateSvarRequest(forfraganSvarRequest).contains("svarTypValue"));

        // UtforareTyp
        forfraganSvarRequest = buildValidInternForfraganSvarRequest(SvarTyp.ACCEPTERA, 1L);
        forfraganSvarRequest.setUtforareTyp("UNKNOWN");
        assertTrue(internForfraganService.validateSvarRequest(forfraganSvarRequest).contains("UtforareTyp"));

        // Namn
        forfraganSvarRequest = buildValidInternForfraganSvarRequest(SvarTyp.ACCEPTERA, 1L);
        forfraganSvarRequest.setUtforareNamn(null);
        assertTrue(internForfraganService.validateSvarRequest(forfraganSvarRequest).contains("UtforareNamn"));

        // Adress
        forfraganSvarRequest = buildValidInternForfraganSvarRequest(SvarTyp.ACCEPTERA, 1L);
        forfraganSvarRequest.setUtforareAdress(null);
        assertTrue(internForfraganService.validateSvarRequest(forfraganSvarRequest).contains("UtforareAdress"));

        // Postnr
        forfraganSvarRequest = buildValidInternForfraganSvarRequest(SvarTyp.ACCEPTERA, 1L);
        forfraganSvarRequest.setUtforarePostnr("apa");
        assertTrue(internForfraganService.validateSvarRequest(forfraganSvarRequest).contains("UtforarePostnr"));
        forfraganSvarRequest.setUtforarePostnr("1234");
        assertTrue(internForfraganService.validateSvarRequest(forfraganSvarRequest).contains("UtforarePostnr"));
        forfraganSvarRequest.setUtforarePostnr("12345");
        assertNull(internForfraganService.validateSvarRequest(forfraganSvarRequest));

        // PostOrt
        forfraganSvarRequest = buildValidInternForfraganSvarRequest(SvarTyp.ACCEPTERA, 1L);
        forfraganSvarRequest.setUtforarePostort(null);
        assertTrue(internForfraganService.validateSvarRequest(forfraganSvarRequest).contains("UtforarePostort"));

        // Epost
        forfraganSvarRequest = buildValidInternForfraganSvarRequest(SvarTyp.ACCEPTERA, 1L);
        forfraganSvarRequest.setUtforareEpost(null);
        assertNull(internForfraganService.validateSvarRequest(forfraganSvarRequest));
        forfraganSvarRequest.setUtforareEpost("a@@b.se");
        assertTrue(internForfraganService.validateSvarRequest(forfraganSvarRequest).contains("UtforareEpost"));
        forfraganSvarRequest.setUtforareEpost("a@@b.se.");
        assertTrue(internForfraganService.validateSvarRequest(forfraganSvarRequest).contains("UtforareEpost"));
        forfraganSvarRequest.setUtforareEpost("@b.se.");
        assertTrue(internForfraganService.validateSvarRequest(forfraganSvarRequest).contains("UtforareEpost"));
        forfraganSvarRequest.setUtforareEpost("a@bserver..se");
        assertTrue(internForfraganService.validateSvarRequest(forfraganSvarRequest).contains("UtforareEpost"));
        forfraganSvarRequest.setUtforareEpost("a@b.se.");
        assertTrue(internForfraganService.validateSvarRequest(forfraganSvarRequest).contains("UtforareEpost"));
        forfraganSvarRequest.setUtforareEpost("en.lang.adress@some.domain.at.se");
        assertNull(internForfraganService.validateSvarRequest(forfraganSvarRequest));

        // BorjaDatum
        forfraganSvarRequest = buildValidInternForfraganSvarRequest(SvarTyp.ACCEPTERA, 1L);
        forfraganSvarRequest.setBorjaDatum(null);
        assertNull(internForfraganService.validateSvarRequest(forfraganSvarRequest));
        forfraganSvarRequest.setBorjaDatum("1912-01-");
        assertTrue(internForfraganService.validateSvarRequest(forfraganSvarRequest).contains("BorjaDatum"));
        forfraganSvarRequest.setBorjaDatum("1912-01-12");
        assertTrue(internForfraganService.validateSvarRequest(forfraganSvarRequest).contains("before"));
        forfraganSvarRequest.setBorjaDatum(LocalDate.now().plusDays(1).format(DateTimeFormatter.ISO_DATE));
        assertNull(internForfraganService.validateSvarRequest(forfraganSvarRequest));

        //vid AVBOJ är endast kommentar mandatory
        forfraganSvarRequest = new ForfraganSvarRequest();
        forfraganSvarRequest.setSvarTyp(SvarTyp.AVBOJ.name());
        assertTrue(internForfraganService.validateSvarRequest(forfraganSvarRequest).contains("kommentar"));
        forfraganSvarRequest.setKommentar(INTERNFORFRAGAN_KOMMENTAR);
        assertNull(internForfraganService.validateSvarRequest(forfraganSvarRequest));
    }

    @Test(expected = IbServiceException.class)
    public void testBesvaraInternForfraganFailsAlreadyAnswered() {
        final Long utredningId = 1L;
        final String landstingHsaId = "landstingHsaId";
        Long internForfragaId = 2L;
        when(utredningRepository.findById(utredningId)).thenReturn(Optional.of(anUtredning()
                .withUtredningId(utredningId)
                .withUtredningsTyp(AFU)
                .withExternForfragan(anExternForfragan()
                        .withLandstingHsaId(landstingHsaId)
                        .withInternForfraganList(ImmutableList.of(anInternForfragan()
                                .withId(internForfragaId)
                                .withStatus(InternForfraganStatus.TILLDELAD_VANTAR_PA_BESTALLNING)
                                .withForfraganSvar(aForfraganSvar()
                                        .build())
                                .build()))
                        .build())
                .build()));
        internForfraganService.besvaraInternForfragan(utredningId,
                buildValidInternForfraganSvarRequest(SvarTyp.ACCEPTERA, internForfragaId));
    }

    @Test
    public void testBesvaraInternForfraganNormalFlode4() {
        // Normalflöde 4 - Vårdadministratör accepterar internförfrågan från landstinget
        final Long utredningId = 1L;
        final String landstingHsaId = "landstingHsaId";
        Long internForfragaId1 = 2L;
        Long internForfragaId2 = 22L;
        String vardenhetId1 = "vardenhetHsaId1";
        String vardenhetId2 = "vardenhetHsaId2";
        String vardenhetId1Namn = "vardenhetHsaId1Namn";
        String userName = "Anvandare1";
        Utredning utredningMock = anUtredning()
                .withUtredningId(utredningId)
                .withUtredningsTyp(AFU)
                .withExternForfragan(anExternForfragan()
                        .withLandstingHsaId(landstingHsaId)
                        .withInternForfraganList(Arrays.asList(
                                anInternForfragan()
                                        .withId(internForfragaId1)
                                        .withVardenhetHsaId(vardenhetId1)
                                        .withStatus(InternForfraganStatus.INKOMMEN)
                                        .build(),
                                anInternForfragan()
                                        .withId(internForfragaId2)
                                        .withStatus(InternForfraganStatus.INKOMMEN)
                                        .withVardenhetHsaId(vardenhetId2)
                                        .build()))
                        .build())
                .build();
        when(utredningRepository.findById(utredningId)).thenReturn(Optional.of(utredningMock));
        when(organizationUnitService.getVardenhet(vardenhetId1)).thenReturn(new Vardenhet(vardenhetId1, vardenhetId1Namn));
        when(userService.getUser()).thenReturn(new IbUser("", userName));
        when(utredningRepository.saveUtredning(any(Utredning.class))).thenAnswer(
                invocation -> invocation.getArgument(0));

        final InternForfraganSvarItem result = internForfraganService.besvaraInternForfragan(utredningId,
                buildValidInternForfraganSvarRequest(SvarTyp.ACCEPTERA, internForfragaId1));

        assertNotNull(result);
        assertEquals("Utforarnamn", result.getUtforareNamn());
    }

    @Test
    public void testBesvaraInternForfraganNormalFlode5() {
        // Normalflöde 5 - Samtliga vårdenheter har besvarat internförfrågningarna
        final Long utredningId = 1L;
        final String landstingHsaId = "landstingHsaId";
        Long internForfragaId1 = 2L;
        String vardenhetId1 = "vardenhetHsaId1";
        String vardenhetId1Namn = "vardenhetHsaId1Namn";
        String userName = "Anvandare1";
        Utredning utredningMock = anUtredning()
                .withUtredningId(utredningId)
                .withUtredningsTyp(AFU)
                .withExternForfragan(anExternForfragan()
                        .withLandstingHsaId(landstingHsaId)
                        .withInternForfraganList(Arrays.asList(
                                anInternForfragan()
                                        .withId(internForfragaId1)
                                        .withStatus(InternForfraganStatus.INKOMMEN)
                                        .withVardenhetHsaId(vardenhetId1)
                                        .build()))
                        .build())
                .build();
        when(utredningRepository.findById(utredningId)).thenReturn(Optional.of(utredningMock));
        when(vardgivareService.listVardenheterForVardgivare(landstingHsaId)).thenReturn(new GetVardenheterForVardgivareResponse());
        when(organizationUnitService.getVardenhet(vardenhetId1)).thenReturn(new Vardenhet(vardenhetId1, vardenhetId1Namn));
        when(userService.getUser()).thenReturn(new IbUser("", userName));
        when(utredningRepository.saveUtredning(any(Utredning.class))).thenAnswer(
                invocation -> invocation.getArgument(0));

        final InternForfraganSvarItem result = internForfraganService.besvaraInternForfragan(utredningId,
                buildValidInternForfraganSvarRequest(SvarTyp.ACCEPTERA, internForfragaId1));

        assertNotNull(result);
        assertEquals("Utforarnamn", result.getUtforareNamn());
        // Should notify as internforfragan have been answered
        verify(notifieringSendService, times(1)).notifieraLandstingSamtligaVardenheterHarSvaratPaInternforfragan(any(Utredning.class));
    }

    @Test
    public void testBesvaraInternForfraganAlternativFlode2() {
        // Alternativflöde 2 - Utredningen tilldelas automatiskt till en vårdenhet i egen regi
        final Long utredningId = 1L;
        final String landstingHsaId = "landstingHsaId";
        Long internForfragaId1 = 2L;
        String vardenhetId1 = "vardenhetHsaId1";
        String vardenhetId1Namn = "vardenhetHsaId1Namn";
        String userName = "Anvandare1";
        Utredning utredningMock = anUtredning()
                .withUtredningId(utredningId)
                .withUtredningsTyp(AFU)
                .withExternForfragan(anExternForfragan()
                        .withLandstingHsaId(landstingHsaId)
                        .withInternForfraganList(Arrays.asList(
                                anInternForfragan()
                                        .withId(internForfragaId1)
                                        .withStatus(InternForfraganStatus.INKOMMEN)
                                        .withVardenhetHsaId(vardenhetId1)
                                        .withForfraganSvar(null)
                                        .build()))
                        .build())
                .build();
        when(utredningRepository.findById(utredningId)).thenReturn(Optional.of(utredningMock));
        GetVardenheterForVardgivareResponse egetRegiResponse = new GetVardenheterForVardgivareResponse();
        egetRegiResponse.setEgetLandsting(ImmutableList.of(
                VardenhetItem.VardenhetItemBuilder
                        .aVardenhetItem()
                        .withId(vardenhetId1)
                        .withRegiForm(RegiFormTyp.EGET_LANDSTING)
                        .build()));
        when(vardgivareService.listVardenheterForVardgivare(landstingHsaId)).thenReturn(egetRegiResponse);
        when(organizationUnitService.getVardenhet(vardenhetId1)).thenReturn(new Vardenhet(vardenhetId1, vardenhetId1Namn));
        when(userService.getUser()).thenReturn(new IbUser("", userName));
        when(utredningRepository.saveUtredning(any(Utredning.class))).thenAnswer(
                invocation -> invocation.getArgument(0));
        when(externForfraganService.acceptExternForfragan(utredningId, landstingHsaId, vardenhetId1)).thenReturn(null);

        final InternForfraganSvarItem result = internForfraganService.besvaraInternForfragan(utredningId,
                buildValidInternForfraganSvarRequest(SvarTyp.ACCEPTERA, internForfragaId1));

        assertNotNull(result);
        assertEquals("Utforarnamn", result.getUtforareNamn());
        verify(notifieringSendService, times(0)).notifieraLandstingSamtligaVardenheterHarSvaratPaInternforfragan(any(Utredning.class));
        verify(externForfraganService, times(1)).acceptExternForfragan(utredningId, landstingHsaId, vardenhetId1);
    }

    @Test
    public void testBesvaraInternForfraganAlternativFlode5() {
        // F004: Alternativflöde 5 - Landstingets enda vårdenhet avvisar internförfrågan
        final Long utredningId = 1L;
        final String landstingHsaId = "landstingHsaId";
        Long internForfragaId1 = 2L;
        String vardenhetId1 = "vardenhetHsaId1";
        String vardenhetId1Namn = "vardenhetHsaId1Namn";
        String userName = "Anvandare1";
        Utredning utredningMock = anUtredning()
                .withUtredningId(utredningId)
                .withUtredningsTyp(AFU)
                .withExternForfragan(anExternForfragan()
                        .withLandstingHsaId(landstingHsaId)
                        .withInternForfraganList(Arrays.asList(
                                anInternForfragan()
                                        .withId(internForfragaId1)
                                        .withStatus(InternForfraganStatus.INKOMMEN)
                                        .withVardenhetHsaId(vardenhetId1)
                                        .withForfraganSvar(null)
                                        .build()))
                        .build())
                .build();
        when(utredningRepository.findById(utredningId)).thenReturn(Optional.of(utredningMock));
        GetVardenheterForVardgivareResponse egetRegiResponse = new GetVardenheterForVardgivareResponse();
        egetRegiResponse.setEgetLandsting(ImmutableList.of(
                VardenhetItem.VardenhetItemBuilder
                        .aVardenhetItem()
                        .withId(vardenhetId1)
                        .withRegiForm(RegiFormTyp.EGET_LANDSTING)
                        .build()));
        when(vardgivareService.listVardenheterForVardgivare(landstingHsaId)).thenReturn(egetRegiResponse);
        doNothing().when(notifieringSendService).notifieraLandstingSamtligaVardenheterHarSvaratPaInternforfragan(utredningMock);
        when(organizationUnitService.getVardenhet(vardenhetId1)).thenReturn(new Vardenhet(vardenhetId1, vardenhetId1Namn));

        IbUser ibUser = new IbUser("", userName);
        Feature feature = new Feature();
        feature.setName(se.inera.intyg.intygsbestallning.auth.authorities.AuthoritiesConstants.FEATURE_EXTERNFORFRAGAN_FAR_AVVISAS);
        feature.setGlobal(true);
        ibUser.setFeatures(ImmutableMap.of(feature.getName(), feature));
        when(userService.getUser()).thenReturn(ibUser);

        when(utredningRepository.saveUtredning(any(Utredning.class))).thenAnswer(
                invocation -> invocation.getArgument(0));
        when(externForfraganService.avvisaExternForfragan(utredningId, landstingHsaId, INTERNFORFRAGAN_KOMMENTAR)).thenReturn(null);

        final InternForfraganSvarItem result = internForfraganService.besvaraInternForfragan(utredningId,
                buildValidInternForfraganSvarRequest(SvarTyp.AVBOJ, internForfragaId1));

        assertNotNull(result);
        assertEquals(INTERNFORFRAGAN_KOMMENTAR, result.getKommentar());
        verify(notifieringSendService, times(1)).notifieraLandstingSamtligaVardenheterHarSvaratPaInternforfragan(any(Utredning.class));
        verify(externForfraganService, times(1)).avvisaExternForfragan(utredningId, landstingHsaId, INTERNFORFRAGAN_KOMMENTAR);
    }

    private ForfraganSvarRequest buildValidInternForfraganSvarRequest(SvarTyp svarTyp, Long internForfraganId) {
        ForfraganSvarRequest svar = new ForfraganSvarRequest();
        svar.setForfraganId(internForfraganId);
        svar.setSvarTyp(svarTyp.name());

        if (svarTyp.equals(SvarTyp.AVBOJ)) {
            svar.setKommentar(INTERNFORFRAGAN_KOMMENTAR);
            return svar;
        }


        svar.setUtforareTyp(UtforareTyp.ENHET.name());
        svar.setUtforareNamn("Utforarnamn");
        svar.setUtforareAdress("gatan");
        svar.setUtforarePostnr("12345");
        svar.setUtforarePostort("postort");
        svar.setUtforareEpost("example@example.com");
        svar.setKommentar(INTERNFORFRAGAN_KOMMENTAR);
        svar.setBorjaDatum(LocalDate.now().plusDays(1).format(DateTimeFormatter.ISO_DATE));
        return svar;
    }

    private VardenhetItem buildVardenhet(String vardenehetId, RegiFormTyp regiFormTyp) {
        return VardenhetItem.VardenhetItemBuilder.aVardenhetItem()
                .withId(vardenehetId)
                .withLabel(vardenehetId + "-name")
                .withRegiForm(regiFormTyp)
                .build();

    }
}
