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
import se.inera.intyg.intygsbestallning.persistence.model.ExternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.InternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.type.HandelseTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.SvarTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.UtredningsTyp;
import se.inera.intyg.intygsbestallning.persistence.repository.ExternForfraganRepository;
import se.inera.intyg.intygsbestallning.persistence.repository.UtredningRepository;
import se.inera.intyg.intygsbestallning.service.stateresolver.InternForfraganStatus;
import se.inera.intyg.intygsbestallning.persistence.model.status.UtredningStatus;
import se.inera.intyg.intygsbestallning.service.user.UserService;
import se.inera.intyg.intygsbestallning.service.util.BusinessDaysStub;
import se.inera.intyg.intygsbestallning.service.utredning.UtredningService;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.forfragan.GetForfraganListResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.forfragan.InternForfraganListItemFactory;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.forfragan.ListForfraganRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.utredning.GetUtredningResponse;

import javax.xml.ws.WebServiceException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
public class ExternForfraganServiceImplTest {

    @Mock
    private ExternForfraganRepository externForfraganRepository;

    @Mock
    private UtredningRepository utredningRepository;

    @Mock
    private UtredningService utredningService;

    @Mock
    private MyndighetIntegrationService myndighetIntegrationService;

    @Mock
    private HsaOrganizationsService organizationUnitService;

    @Mock
    private UserService userService;

    @Spy
    private InternForfraganListItemFactory internForfraganListItemFactory = new InternForfraganListItemFactory(new BusinessDaysStub());

    @InjectMocks
    private ExternForfraganServiceImpl testee;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

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

    @Test
    public void testAcceptExternForfraganSuccess() {
        final Long utredningId = 1L;
        final String landstingHsaId = "landstingHsaId";
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
                createValidUtredningForAcceptExternForfragan(utredningId, landstingHsaId, vardenhetId1));

        GetUtredningResponse response = testee.acceptExternForfragan(utredningId, landstingHsaId, vardenhetId1);

        ArgumentCaptor<RespondToPerformerRequestDto> respondToPerformerRequestArgument = ArgumentCaptor.forClass(RespondToPerformerRequestDto.class);
        verify(myndighetIntegrationService).respondToPerformerRequest(respondToPerformerRequestArgument.capture());

        assertEquals(UtredningStatus.TILLDELAD_VANTAR_PA_BESTALLNING, response.getStatus());
        assertEquals(InternForfraganStatus.TILLDELAD_VANTAR_PA_BESTALLNING, response.getInternForfraganList().get(0).getStatus());

        RespondToPerformerRequestDto respondToPerformerRequest = respondToPerformerRequestArgument.getValue();
        assertEquals(utredningId, respondToPerformerRequest.getAssessmentId());
        assertEquals("ACCEPTERAT", respondToPerformerRequest.getResponseCode());
        assertEquals("Utforarekommentar", respondToPerformerRequest.getComment());
        assertEquals(vardgivareId1, respondToPerformerRequest.getCareGiverId());
        assertEquals(vardgivareNamn1, respondToPerformerRequest.getCareGiverName());
        assertEquals(vardenhetId1, respondToPerformerRequest.getCareUnitId());
        assertEquals(vardenhetNamn1, respondToPerformerRequest.getCareUnitName());
        assertEquals("epost", respondToPerformerRequest.getEmail());
        assertEquals("telefon", respondToPerformerRequest.getPhoneNumber());
        assertEquals("adress", respondToPerformerRequest.getPostalAddress());
        assertEquals("postort", respondToPerformerRequest.getPostalCity());
        assertEquals("postnr", respondToPerformerRequest.getPostalCode());

        ArgumentCaptor<Utredning> utredningArgument = ArgumentCaptor.forClass(Utredning.class);
        verify(utredningRepository).save(utredningArgument.capture());

        Utredning utredning = utredningArgument.getValue();
        assertNotNull(utredning.getExternForfragan().getInternForfraganList().get(0).getTilldeladDatum());
        assertEquals(1, utredning.getHandelseList().size());
        assertEquals(HandelseTyp.FORFRAGAN_BESVARAD, utredning.getHandelseList().get(0).getHandelseTyp());
        assertEquals(userName, utredning.getHandelseList().get(0).getAnvandare());
    }

    @Test
    public void testAcceptExternForfraganFailHsaLookup() {
        final Long utredningId = 1L;
        final String landstingHsaId = "landstingHsaId";
        final String vardenhetId1 = "vardenhetId1";

        thrown.expect(IbExternalServiceException.class);
        thrown.expect(hasProperty("externalSystem", is(IbExternalSystemEnum.HSA)));

        when(organizationUnitService.getVardenhet(vardenhetId1)).thenThrow(new WebServiceException("hsa error"));

        when(utredningRepository.findById(utredningId)).thenReturn(
                createValidUtredningForAcceptExternForfragan(utredningId, landstingHsaId, vardenhetId1));

        GetUtredningResponse response = testee.acceptExternForfragan(utredningId, landstingHsaId, vardenhetId1);
    }

    @Test
    public void testAcceptExternForfraganFailMyndighet() {
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
                createValidUtredningForAcceptExternForfragan(utredningId, landstingHsaId, vardenhetId1));

        doThrow(new IbExternalServiceException(IbErrorCodeEnum.EXTERNAL_ERROR, IbExternalSystemEnum.MYNDIGHET, "")).when(
                myndighetIntegrationService).respondToPerformerRequest(ArgumentMatchers.any(RespondToPerformerRequestDto.class));

        testee.acceptExternForfragan(utredningId, landstingHsaId, vardenhetId1);
    }

    @Test(expected = IbNotFoundException.class)
    public void testAcceptExternForfraganUtredningNotExisting() {
        final Long utredningId = 1L;
        final String landstingHsaId = "landstingHsaId";
        final String vardenhetId1 = "vardenhetId1";

        when(utredningRepository.findById(utredningId)).thenReturn(Optional.empty());

        testee.acceptExternForfragan(utredningId, landstingHsaId, vardenhetId1);
    }

    @Test(expected = IbAuthorizationException.class)
    public void testAcceptExternForfraganFailDifferentLandsting() {
        final Long utredningId = 1L;
        final String landstingHsaId = "landstingHsaId";
        final String vardenhetId1 = "vardenhetId1";

        when(utredningRepository.findById(utredningId)).thenReturn(
                createValidUtredningForAcceptExternForfragan(utredningId, landstingHsaId, vardenhetId1));

        testee.acceptExternForfragan(utredningId, "annatLandsting", vardenhetId1);
    }

    @Test(expected = IbServiceException.class)
    public void testAcceptExternForfraganFailIncorrectState() {
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

        testee.acceptExternForfragan(utredningId, landstingHsaId, vardenhetId1);
    }

    @Test(expected = IbServiceException.class)
    public void testAcceptExternForfraganFailIncorrectStateAlreadyAccepted() {
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

        testee.acceptExternForfragan(utredningId, landstingHsaId, vardenhetId1);
    }

    @Test
    public void testAvvisaExternForfraganSuccess() {
        final Long utredningId = 1L;
        final String landstingHsaId = "landstingHsaId";
        final String kommentar = "Ingen kommentar";
        final String userName = "TestUser";

        when(userService.getUser()).thenReturn(new IbUser("", userName));

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

        GetUtredningResponse response = testee.avvisaExternForfragan(utredningId, landstingHsaId, kommentar);
        assertEquals(UtredningStatus.AVVISAD, response.getStatus());

        ArgumentCaptor<RespondToPerformerRequestDto> respondToPerformerRequestArgument = ArgumentCaptor.forClass(RespondToPerformerRequestDto.class);
        verify(myndighetIntegrationService).respondToPerformerRequest(respondToPerformerRequestArgument.capture());

        RespondToPerformerRequestDto respondToPerformerRequest = respondToPerformerRequestArgument.getValue();
        assertEquals("AVVISAT", respondToPerformerRequest.getResponseCode());
        assertEquals(kommentar, respondToPerformerRequest.getComment());

        ArgumentCaptor<Utredning> utredningArgument = ArgumentCaptor.forClass(Utredning.class);
        verify(utredningRepository).save(utredningArgument.capture());

        Utredning utredning = utredningArgument.getValue();
        assertNotNull(utredning.getExternForfragan().getAvvisatDatum());
        assertEquals(kommentar, utredning.getExternForfragan().getAvvisatKommentar());
        assertEquals(1, utredning.getHandelseList().size());
        assertEquals(HandelseTyp.FORFRAGAN_BESVARAD, utredning.getHandelseList().get(0).getHandelseTyp());
        assertEquals(userName, utredning.getHandelseList().get(0).getAnvandare());
    }

    @Test(expected = IbServiceException.class)
    public void testAvvisaExternForfraganFailCommentRequired() {
        final Long utredningId = 1L;
        final String landstingHsaId = "landstingHsaId";
        final String kommentar = "";

        testee.avvisaExternForfragan(utredningId, landstingHsaId, kommentar);
    }

    @Test(expected = IbExternalServiceException.class)
    public void testAvvisaExternForfraganFailMyndighet() {
        final Long utredningId = 1L;
        final String landstingHsaId = "landstingHsaId";
        final String kommentar = "Ingen kommentar";

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

        doThrow(new IbExternalServiceException(IbErrorCodeEnum.EXTERNAL_ERROR, IbExternalSystemEnum.MYNDIGHET, "")).when(
                myndighetIntegrationService).respondToPerformerRequest(ArgumentMatchers.any(RespondToPerformerRequestDto.class));

        testee.avvisaExternForfragan(utredningId, landstingHsaId, kommentar);
    }

    @Test(expected = IbNotFoundException.class)
    public void testAvvisaExternForfraganUtredningNotExisting() {
        final Long utredningId = 1L;
        final String landstingHsaId = "landstingHsaId";
        final String kommentar = "Ingen kommentar";

        when(utredningRepository.findById(utredningId)).thenReturn(Optional.empty());

        testee.avvisaExternForfragan(utredningId, landstingHsaId, kommentar);
    }

    @Test(expected = IbAuthorizationException.class)
    public void testAvvisaExternForfraganFailDifferentLandsting() {
        final Long utredningId = 1L;
        final String landstingHsaId = "landstingHsaId";
        final String kommentar = "Ingen kommentar";

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

        testee.avvisaExternForfragan(utredningId, "annatLandsting", kommentar);
    }

    /*
     * Not allowed to Avvisa ExternForfragan if InternForfragan has already been Tilldelad
     */
    @Test(expected = IbServiceException.class)
    public void testAvvisaExternForfraganFailIncorrectState() {
        final Long utredningId = 1L;
        final String landstingHsaId = "landstingHsaId";
        final String kommentar = "Ingen kommentar";

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

        testee.avvisaExternForfragan(utredningId, landstingHsaId, kommentar);
    }

    private Optional<Utredning> createValidUtredningForAcceptExternForfragan(Long utredningId, String landstingHsaId, String vardenhetId1) {
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
                                        .withKommentar("Utforarekommentar")
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
        list.add(anInternForfragan().withVardenhetHsaId("ve-1").build());
        return list;
    }

    private ListForfraganRequest buildListForfraganRequest() {
        ListForfraganRequest request = new ListForfraganRequest();

        return request;
    }
}
