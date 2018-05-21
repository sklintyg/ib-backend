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

import com.google.common.collect.ImmutableList;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import se.inera.intyg.infra.integration.hsa.model.Vardenhet;
import se.inera.intyg.infra.integration.hsa.model.Vardgivare;
import se.inera.intyg.infra.integration.hsa.services.HsaOrganizationsService;
import se.inera.intyg.intygsbestallning.auth.IbUser;
import se.inera.intyg.intygsbestallning.common.exception.IbAuthorizationException;
import se.inera.intyg.intygsbestallning.common.exception.IbErrorCodeEnum;
import se.inera.intyg.intygsbestallning.common.exception.IbExternalServiceException;
import se.inera.intyg.intygsbestallning.common.exception.IbExternalSystemEnum;
import se.inera.intyg.intygsbestallning.common.exception.IbNotFoundException;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.integration.myndighet.dto.RespondToPerformerRequestDto;
import se.inera.intyg.intygsbestallning.integration.myndighet.service.MyndighetIntegrationService;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.type.HandelseTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.SvarTyp;
import se.inera.intyg.intygsbestallning.persistence.repository.UtredningRepository;
import se.inera.intyg.intygsbestallning.service.user.UserService;
import se.inera.intyg.intygsbestallning.service.util.BusinessDaysBean;
import se.inera.intyg.intygsbestallning.service.util.BusinessDaysStub;
import se.inera.intyg.intygsbestallning.service.utredning.UtredningService;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.GetUtredningResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.forfragan.CreateInternForfraganRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.forfragan.TilldelaDirektRequest;

import javax.xml.ws.WebServiceException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static se.inera.intyg.intygsbestallning.persistence.model.ExternForfragan.ExternForfraganBuilder.anExternForfragan;
import static se.inera.intyg.intygsbestallning.persistence.model.ForfraganSvar.ForfraganSvarBuilder.aForfraganSvar;
import static se.inera.intyg.intygsbestallning.persistence.model.Handlaggare.HandlaggareBuilder.aHandlaggare;
import static se.inera.intyg.intygsbestallning.persistence.model.InternForfragan.InternForfraganBuilder.anInternForfragan;
import static se.inera.intyg.intygsbestallning.persistence.model.Invanare.InvanareBuilder.anInvanare;
import static se.inera.intyg.intygsbestallning.persistence.model.Utredning.UtredningBuilder.anUtredning;
import static se.inera.intyg.intygsbestallning.persistence.model.type.UtredningsTyp.AFU;


@RunWith(MockitoJUnitRunner.class)
public class InternForfraganServiceImplTest {

    @Mock
    private UtredningRepository utredningRepository;

    @Mock
    private HsaOrganizationsService organizationUnitService;

    @Mock
    private UserService userService;

    @Mock
    private UtredningService utredningService;

    @Mock
    private MyndighetIntegrationService myndighetIntegrationService;

    @Spy
    private BusinessDaysBean businessDays = new BusinessDaysStub();

    @InjectMocks
    private InternForfraganServiceImpl internForfraganService;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

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

        when(userService.getUser()).thenReturn(new IbUser("", userName));

        when(organizationUnitService.getVardenhet(vardenhetId1)).thenReturn(new Vardenhet(vardenhetId1, vardenhetNamn1));
        when(organizationUnitService.getVardenhet(vardenhetId2)).thenReturn(new Vardenhet(vardenhetId2, vardenhetNamn2));

        CreateInternForfraganRequest request = new CreateInternForfraganRequest();
        request.setVardenheter(ImmutableList.of(vardenhetId1, vardenhetId2));
        request.setKommentar(kommentar);

        internForfraganService.createInternForfragan(utredningId, landstingHsaId, request);

        ArgumentCaptor<Utredning> utredningArgument = ArgumentCaptor.forClass(Utredning.class);
        verify(utredningRepository).save(utredningArgument.capture());

        Utredning utredning = utredningArgument.getValue();
        assertEquals(kommentar, utredning.getExternForfragan().getInternForfraganList().get(0).getKommentar());
        assertEquals(kommentar, utredning.getExternForfragan().getInternForfraganList().get(1).getKommentar());

        assertEquals(2, utredning.getExternForfragan().getInternForfraganList().size());
        assertEquals(vardenhetId1, utredning.getExternForfragan().getInternForfraganList().get(0).getVardenhetHsaId());
        assertEquals(vardenhetId2, utredning.getExternForfragan().getInternForfraganList().get(1).getVardenhetHsaId());

        assertEquals(2, utredning.getHandelseList().size());
        assertEquals(HandelseTyp.FORFRAGAN_SKICKAD, utredning.getHandelseList().get(0).getHandelseTyp());
        assertEquals(userName, utredning.getHandelseList().get(0).getAnvandare());
        assertEquals("Förfrågan skickades till " + vardenhetNamn1, utredning.getHandelseList().get(0).getHandelseText());
        assertEquals(HandelseTyp.FORFRAGAN_SKICKAD, utredning.getHandelseList().get(1).getHandelseTyp());
        assertEquals(userName, utredning.getHandelseList().get(1).getAnvandare());
        assertEquals("Förfrågan skickades till " + vardenhetNamn2, utredning.getHandelseList().get(1).getHandelseText());
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

        TilldelaDirektRequest request = new TilldelaDirektRequest();
        request.setVardenhet(vardenhetId1);
        request.setKommentar(kommentar);

        internForfraganService.tilldelaDirekt(utredningId, landstingHsaId, request);

        ArgumentCaptor<Utredning> utredningArgument = ArgumentCaptor.forClass(Utredning.class);
        verify(utredningRepository).save(utredningArgument.capture());

        Utredning utredning = utredningArgument.getValue();
        assertEquals(kommentar, utredning.getExternForfragan().getInternForfraganList().get(0).getKommentar());
        assertTrue(utredning.getExternForfragan().getInternForfraganList().get(0).getDirekttilldelad());

        assertEquals(1, utredning.getExternForfragan().getInternForfraganList().size());
        assertEquals(vardenhetId1, utredning.getExternForfragan().getInternForfraganList().get(0).getVardenhetHsaId());
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

    private Optional<Utredning> createValidUtredningForAcceptInternForfragan(Long utredningId, String landstingHsaId, String vardenhetId1) {
        return Optional.of(anUtredning()
                .withUtredningId(utredningId)
                .withUtredningsTyp(AFU)
                .withExternForfragan(anExternForfragan()
                        .withLandstingHsaId(landstingHsaId)
                        .withInkomDatum(LocalDateTime.now())
                        .withBesvarasSenastDatum(LocalDateTime.now())
                        .withInternForfraganList(ImmutableList.of(anInternForfragan()
                                .withVardenhetHsaId(vardenhetId1)
                                .withForfraganSvar(aForfraganSvar()
                                        .withUtforareTelefon("telefon")
                                        .withUtforarePostort("postort")
                                        .withUtforarePostnr("postnr")
                                        .withUtforareNamn("namn")
                                        .withUtforareEpost("epost")
                                        .withUtforareAdress("adress")
                                        .withSvarTyp(SvarTyp.ACCEPTERA)
                                        .build())
                                .build()))
                        .build())
                .withInvanare(anInvanare()
                        .withPersonId("personnummer")
                        .build())
                .withHandlaggare(aHandlaggare()
                        .build())
                .build());
    }

    @Test
    public void testAcceptInternForfraganSuccess() {
        final Long utredningId = 1L;
        final String landstingHsaId = "landstingHsaId";
        final String kommentar = "Ingen kommentar";
        final String vardenhetId1 = "vardenhetId1";
        final String vardenhetNamn1 = "vardenhetNamn1";
        final String vardgivareId1 = "vardgivareId1";
        final String vardgivareNamn1 = "vardgivareNamn1";
        final String userName = "TestUser";

        when(userService.getUser()).thenReturn(new IbUser("", userName));

        when(organizationUnitService.getVardenhet(vardenhetId1)).thenReturn(new Vardenhet(vardenhetId1, vardenhetNamn1));
        when(organizationUnitService.getVardgivareOfVardenhet(vardenhetId1)).thenReturn(vardgivareId1);
        when(organizationUnitService.getVardgivareInfo(vardgivareId1)).thenReturn(new Vardgivare(vardgivareId1, vardgivareNamn1));

        when(utredningRepository.findById(utredningId)).thenReturn(
                createValidUtredningForAcceptInternForfragan(utredningId, landstingHsaId, vardenhetId1));

        GetUtredningResponse response = internForfraganService.acceptInternForfragan(utredningId, landstingHsaId, vardenhetId1);

        ArgumentCaptor<Utredning> utredningArgument = ArgumentCaptor.forClass(Utredning.class);
        verify(utredningRepository).save(utredningArgument.capture());

        Utredning utredning = utredningArgument.getValue();
        assertNotNull(utredning.getExternForfragan().getInternForfraganList().get(0).getTilldeladDatum());
        assertEquals(1, utredning.getHandelseList().size());
        assertEquals(HandelseTyp.FORFRAGAN_BESVARAD, utredning.getHandelseList().get(0).getHandelseTyp());
    }

    @Test
    public void testAcceptInternForfraganFailHsaLookup() {
        final Long utredningId = 1L;
        final String landstingHsaId = "landstingHsaId";
        final String vardenhetId1 = "vardenhetId1";

        thrown.expect(IbExternalServiceException.class);
        thrown.expect(hasProperty("externalSystem", is(IbExternalSystemEnum.HSA)));

        when(organizationUnitService.getVardenhet(vardenhetId1)).thenThrow(new WebServiceException("hsa error"));

        when(utredningRepository.findById(utredningId)).thenReturn(
                createValidUtredningForAcceptInternForfragan(utredningId, landstingHsaId, vardenhetId1));

        GetUtredningResponse response = internForfraganService.acceptInternForfragan(utredningId, landstingHsaId, vardenhetId1);
    }

    @Test
    public void testAcceptInternForfraganFailMyndighet() {
        final Long utredningId = 1L;
        final String landstingHsaId = "landstingHsaId";
        final String vardenhetId1 = "vardenhetId1";
        final String vardenhetNamn1 = "vardenhetNamn1";
        final String vardgivareId1 = "vardgivareId1";
        final String vardgivareNamn1 = "vardgivareNamn1";

        thrown.expect(IbExternalServiceException.class);
        thrown.expect(hasProperty("externalSystem", is(IbExternalSystemEnum.MYNDIGHET)));

        when(organizationUnitService.getVardenhet(vardenhetId1)).thenReturn(new Vardenhet(vardenhetId1, vardenhetNamn1));
        when(organizationUnitService.getVardgivareOfVardenhet(vardenhetId1)).thenReturn(vardgivareId1);
        when(organizationUnitService.getVardgivareInfo(vardgivareId1)).thenReturn(new Vardgivare(vardgivareId1, vardgivareNamn1));

        when(utredningRepository.findById(utredningId)).thenReturn(
                createValidUtredningForAcceptInternForfragan(utredningId, landstingHsaId, vardenhetId1));

        doThrow(new IbExternalServiceException(IbErrorCodeEnum.EXTERNAL_ERROR, IbExternalSystemEnum.MYNDIGHET, "")).when(
                myndighetIntegrationService).respondToPerformerRequest(ArgumentMatchers.any(RespondToPerformerRequestDto.class));

        internForfraganService.acceptInternForfragan(utredningId, landstingHsaId, vardenhetId1);
    }

    @Test(expected = IbNotFoundException.class)
    public void testAcceptInternForfraganUtredningNotExisting() {
        final Long utredningId = 1L;
        final String landstingHsaId = "landstingHsaId";
        final String vardenhetId1 = "vardenhetId1";

        when(utredningRepository.findById(utredningId)).thenReturn(Optional.empty());

        internForfraganService.acceptInternForfragan(utredningId, landstingHsaId, vardenhetId1);
    }

    @Test(expected = IbAuthorizationException.class)
    public void testAcceptInternForfraganFailDifferentLandsting() {
        final Long utredningId = 1L;
        final String landstingHsaId = "landstingHsaId";
        final String vardenhetId1 = "vardenhetId1";

        when(utredningRepository.findById(utredningId)).thenReturn(
                createValidUtredningForAcceptInternForfragan(utredningId, landstingHsaId, vardenhetId1));

        internForfraganService.acceptInternForfragan(utredningId, "annatLandsting", vardenhetId1);
    }

    @Test(expected = IbServiceException.class)
    public void testAcceptInternForfraganFailIncorrectState() {
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
                                .withForfraganSvar(aForfraganSvar()
                                        .withUtforareTelefon("telefon")
                                        .withUtforarePostort("postort")
                                        .withUtforarePostnr("postnr")
                                        .withUtforareNamn("namn")
                                        .withUtforareEpost("epost")
                                        .withUtforareAdress("adress")
                                        .build())
                                .build()))
                        .build())
                .withInvanare(anInvanare()
                        .withPersonId("personnummer")
                        .build())
                .withHandlaggare(aHandlaggare()
                        .build())
                .build()));

        internForfraganService.acceptInternForfragan(utredningId, landstingHsaId, vardenhetId1);
    }

    @Test(expected = IbServiceException.class)
    public void testAcceptInternForfraganFailIncorrectStateAlreadyAccepted() {
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
                                .withTilldeladDatum(LocalDateTime.now())
                                .withForfraganSvar(aForfraganSvar()
                                        .withUtforareTelefon("telefon")
                                        .withUtforarePostort("postort")
                                        .withUtforarePostnr("postnr")
                                        .withUtforareNamn("namn")
                                        .withUtforareEpost("epost")
                                        .withUtforareAdress("adress")
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

        internForfraganService.acceptInternForfragan(utredningId, landstingHsaId, vardenhetId1);
    }
}
