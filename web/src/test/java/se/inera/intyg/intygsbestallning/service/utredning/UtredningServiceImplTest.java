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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static se.inera.intyg.intygsbestallning.common.util.RivtaTypesUtil.anII;
import static se.inera.intyg.intygsbestallning.persistence.model.Besok.BesokBuilder.aBesok;
import static se.inera.intyg.intygsbestallning.persistence.model.Bestallning.BestallningBuilder.aBestallning;
import static se.inera.intyg.intygsbestallning.persistence.model.ExternForfragan.ExternForfraganBuilder.anExternForfragan;
import static se.inera.intyg.intygsbestallning.persistence.model.ForfraganSvar.ForfraganSvarBuilder.aForfraganSvar;
import static se.inera.intyg.intygsbestallning.persistence.model.Handelse.HandelseBuilder.aHandelse;
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
import static se.inera.intyg.intygsbestallning.service.utredning.dto.AvslutaUtredningRequest.EndUtredningRequestBuilder.anEndUtredningRequest;
import static se.inera.intyg.intygsbestallning.service.utredning.dto.Bestallare.BestallareBuilder.aBestallare;
import static se.inera.intyg.intygsbestallning.service.utredning.dto.OrderRequest.OrderRequestBuilder.anOrderRequest;
import static se.inera.intyg.intygsbestallning.testutil.TestDataGen.DATE_TIME;
import static se.inera.intyg.intygsbestallning.testutil.TestDataGen.createBestallning;
import static se.inera.intyg.intygsbestallning.testutil.TestDataGen.createExternForfragan;
import static se.inera.intyg.intygsbestallning.testutil.TestDataGen.createHandlaggare;
import static se.inera.intyg.intygsbestallning.testutil.TestDataGen.createUpdateOrderType;
import static se.inera.intyg.intygsbestallning.testutil.TestDataGen.createUtredning;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import se.riv.infrastructure.directory.organization.getunitresponder.v1.UnitType;
import se.riv.intygsbestallning.certificate.order.updateorder.v1.UpdateOrderType;
import se.riv.intygsbestallning.certificate.order.v1.AuthorityAdministrativeOfficialType;
import javax.xml.ws.WebServiceException;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import se.inera.intyg.infra.integration.hsa.client.OrganizationUnitService;
import se.inera.intyg.infra.integration.hsa.exception.HsaServiceCallException;
import se.inera.intyg.infra.integration.hsa.model.Vardenhet;
import se.inera.intyg.infra.integration.hsa.services.HsaOrganizationsService;
import se.inera.intyg.intygsbestallning.auth.IbUser;
import se.inera.intyg.intygsbestallning.common.exception.IbAuthorizationException;
import se.inera.intyg.intygsbestallning.common.exception.IbErrorCodeEnum;
import se.inera.intyg.intygsbestallning.common.exception.IbNotFoundException;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.persistence.model.InternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.RegistreradVardenhet;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.status.UtredningStatus;
import se.inera.intyg.intygsbestallning.persistence.model.type.AvslutOrsak;
import se.inera.intyg.intygsbestallning.persistence.model.type.BesokStatusTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.HandelseTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.MyndighetTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.RegiFormTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.SvarTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.TolkStatusTyp;
import se.inera.intyg.intygsbestallning.persistence.repository.RegistreradVardenhetRepository;
import se.inera.intyg.intygsbestallning.persistence.repository.UtredningRepository;
import se.inera.intyg.intygsbestallning.service.notifiering.send.NotifieringSendService;
import se.inera.intyg.intygsbestallning.service.user.UserService;
import se.inera.intyg.intygsbestallning.service.util.BusinessDaysBean;
import se.inera.intyg.intygsbestallning.service.util.BusinessDaysStub;
import se.inera.intyg.intygsbestallning.service.utredning.dto.AssessmentRequest;
import se.inera.intyg.intygsbestallning.service.utredning.dto.AvslutaUtredningRequest;
import se.inera.intyg.intygsbestallning.service.utredning.dto.OrderRequest;
import se.inera.intyg.intygsbestallning.service.utredning.dto.UpdateOrderRequest;
import se.inera.intyg.intygsbestallning.testutil.TestDataGen;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.forfragan.InternForfraganListItemFactory;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.utredning.GetUtredningResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.utredning.SaveBetalningForUtredningRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.utredning.SaveUtbetalningForUtredningRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.utredning.UtredningListItemFactory;

@RunWith(MockitoJUnitRunner.class)
public class UtredningServiceImplTest {

    @Mock
    private UtredningRepository utredningRepository;

    @Mock
    private HsaOrganizationsService hsaOrganizationService;

    @Mock
    private OrganizationUnitService organizationUnitService;

    @Mock
    private UserService userService;

    @Mock
    private NotifieringSendService notifieringSendService;

    @Mock
    private RegistreradVardenhetRepository registreradVardenhetRepository;

    @Spy
    private BusinessDaysBean businessDays = new BusinessDaysStub();

    @Spy
    private InternForfraganListItemFactory internForfraganListItemFactory = new InternForfraganListItemFactory();

    @Spy
    private UtredningListItemFactory utredningListItemFactory = new UtredningListItemFactory();

    @InjectMocks
    private UtredningServiceImpl utredningService;

    @Captor
    private ArgumentCaptor<Utredning> argumentCaptor;

    @Before
    public void injectSpringBeans() {
        // Since we are not using a Spring context, and, injectmocks doesnt seem to work on subclasses (?),
        // DP inject/Autowire manually.
        ReflectionTestUtils.setField(utredningListItemFactory, "businessDays", businessDays);
        ReflectionTestUtils.setField(internForfraganListItemFactory, "businessDays", businessDays);
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
                        .withMyndighet(MyndighetTyp.FKASSA.name())
                        .withPostnummer("12345")
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
        assertEquals(MyndighetTyp.FKASSA.name(), response.getHandlaggare().getMyndighet());
        assertEquals("12345", response.getHandlaggare().getPostnummer());
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
                        .withPostort("invanarePostort")
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
        final OrderRequest order = anOrderRequest()
                .withUtredningsTyp(LIAG)
                .withTolkBehov(true)
                .withTolkSprak("sv")
                .withSyfte("syfte")
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
                        .withMyndighet(MyndighetTyp.FKASSA.name())
                        .withPostnummer("12345")
                        .withStad("stad")
                        .withTelefonnummer("telefonnummer")
                        .build())
                .build();

        utredningService.registerNewUtredning(order);
        Mockito.verify(utredningRepository).saveUtredning(argumentCaptor.capture());

        final Utredning captured = argumentCaptor.getValue();
        assertNotNull(captured);
        assertTrue(captured.getTolkBehov());
        assertEquals("sv", captured.getTolkSprak());
        assertEquals(LIAG, captured.getUtredningsTyp());
        assertEquals("kommentar", captured.getBestallning().get().getKommentar());
        assertEquals("atgarder", captured.getBestallning().get().getPlaneradeAktiviteter());
        assertEquals("syfte", captured.getBestallning().get().getSyfte());
        assertEquals("enhet", captured.getBestallning().get().getTilldeladVardenhetHsaId());
        assertFalse(captured.getIntygList().isEmpty());
        assertEquals(LocalDate.of(2019, 1, 1).atStartOfDay(), captured.getIntygList().get(0).getSistaDatum());
        assertNull(captured.getBestallning().get().getUppdateradDatum());
        assertEquals("behov", captured.getInvanare().getSarskildaBehov());
        assertEquals("personnummer", captured.getInvanare().getPersonId());
        assertEquals("bakgrund", captured.getInvanare().getBakgrundNulage());
        assertNull(captured.getInvanare().getPostort());
        assertNotNull("", captured.getHandlingList());
        assertEquals(1, captured.getHandlingList().size());
        assertNull(captured.getHandlingList().get(0).getInkomDatum());
        assertNotNull("", captured.getHandlingList().get(0).getSkickatDatum());
        assertEquals(BESTALLNING, captured.getHandlingList().get(0).getUrsprung());
        assertEquals("adress", captured.getHandlaggare().getAdress());
        assertEquals("email", captured.getHandlaggare().getEmail());
        assertEquals("fullstandigtNamn", captured.getHandlaggare().getFullstandigtNamn());
        assertEquals("kontor", captured.getHandlaggare().getKontor());
        assertEquals("kostnadsstalle", captured.getHandlaggare().getKostnadsstalle());
        assertEquals(MyndighetTyp.FKASSA.name(), captured.getHandlaggare().getMyndighet());
        assertEquals("12345", captured.getHandlaggare().getPostnummer());
        assertEquals("stad", captured.getHandlaggare().getStad());
        assertEquals("telefonnummer", captured.getHandlaggare().getTelefonnummer());
    }

    @Test
    public void registerNewUtredningFromRequestPerformerForAssesment() throws HsaServiceCallException {

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
                        .withPostnummer("12345")
                        .withStad("stad")
                        .withTelefonnummer("telefonnummer")
                        .build())
                .build();

        doReturn(utredning)
                .when(utredningRepository)
                .saveUtredning(any(Utredning.class));

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
                        .withMyndighet(MyndighetTyp.FKASSA.name())
                        .withPostnummer("12345")
                        .withStad("stad")
                        .withTelefonnummer("telefonnummer")
                        .build())
                .build();

        final UnitType unit1 = new UnitType();
        unit1.setUnitName("enhetsNamn1");

        final UnitType unit2 = new UnitType();
        unit1.setUnitName("enhetsNamn2");

        final UnitType unit3 = new UnitType();
        unit1.setUnitName("enhetsNamn3");

        final Utredning sparadUtredning = utredningService.registerNewUtredning(request);

        assertEquals(utredning, sparadUtredning);
        verify(notifieringSendService, times(1)).notifieraLandstingNyExternforfragan(any(Utredning.class));
    }

    @Test
    public void registerNewUtredningFromRequestPerformerForAssessmentAlternativeFlow1() throws HsaServiceCallException {

        final LocalDateTime dateTime = LocalDateTime.of(2019, 12, 12, 12, 12, 12, 12);

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
                        .withPostnummer("12345")
                        .withStad("stad")
                        .withTelefonnummer("telefonnummer")
                        .build())
                .build();

        doReturn(utredning)
                .when(utredningRepository)
                .saveUtredning(any(Utredning.class));

        //Just one vardenhet registered and it in "egen regiform"
        List<RegistreradVardenhet> vardenheter = new ArrayList<>();
        vardenheter.add(RegistreradVardenhet.RegistreradVardenhetBuilder.aRegistreradVardenhet()
                .withVardenhetRegiForm(RegiFormTyp.EGET_LANDSTING).build());
        doReturn(vardenheter)
                .when(registreradVardenhetRepository)
                .findByVardgivareHsaId("id");

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
                        .withMyndighet(MyndighetTyp.FKASSA.name())
                        .withPostnummer("12345")
                        .withStad("stad")
                        .withTelefonnummer("telefonnummer")
                        .build())
                .build();

        final Utredning sparadUtredning = utredningService.registerNewUtredning(request);

        assertEquals(utredning, sparadUtredning);
        verify(notifieringSendService, times(1)).notifieraVardenhetNyInternforfragan(any(Utredning.class));
    }

    @Test
    public void uppdateraOrderOk() {

        final String tolkSprak = "SV";

        Utredning utredning = createUtredning();

        UpdateOrderType updateOrderType = createUpdateOrderType(true, "SV", null);

        AuthorityAdministrativeOfficialType admin = new AuthorityAdministrativeOfficialType();
        admin.setEmail("uppdatera");
        admin.setFullName("uppdatera");
        admin.setOfficeName("uppdatera");

        updateOrderType.setUpdatedAuthorityAdministrativeOfficial(admin);

        final UpdateOrderRequest updateOrderRequest = UpdateOrderRequest.from(updateOrderType);

        doReturn(Optional.of(utredning))
                .when(utredningRepository)
                .findById(anyLong());

        final Utredning uppdateradUtredning = utredningService.updateOrder(updateOrderRequest);
        assertEquals(AFU_UTVIDGAD, uppdateradUtredning.getUtredningsTyp());

        assertThat(uppdateradUtredning.getBestallning()).isNotEqualTo(createBestallning());
        assertThat(uppdateradUtredning.getHandlaggare()).isNotEqualTo(createHandlaggare());
        assertThat(uppdateradUtredning.getUtredningsTyp()).isEqualTo(AFU_UTVIDGAD);
        assertThat(uppdateradUtredning.getExternForfragan()).isPresent().isEqualTo(Optional.of(createExternForfragan()));
        assertThat(uppdateradUtredning.getTolkBehov()).isTrue();
        assertThat(uppdateradUtredning.getTolkSprak()).isEqualTo(tolkSprak);

        verify(notifieringSendService, times(1)).notifieraVardenhetUppdateradBestallning(any(Utredning.class));
    }

    @Test
    public void uppdateraOrderAndraOptionalHandlaggareFaltOk() {

        final String emptyString = "";
        final String nullString = null;
        final String newofficeName = "NewOfficeName";

        Utredning utredning = createUtredning();
        utredning.setHandlaggare(createHandlaggare());

        final String handlaggareNamn = utredning.getHandlaggare().getFullstandigtNamn();

        UpdateOrderType updateOrderType = createUpdateOrderType(null, null, null);

        AuthorityAdministrativeOfficialType admin = new AuthorityAdministrativeOfficialType();
        admin.setEmail(emptyString); // should be resolved to a null column
        admin.setFullName(nullString); //should not be updated
        admin.setOfficeName(newofficeName); //should be updated

        updateOrderType.setUpdatedAuthorityAdministrativeOfficial(admin);

        doReturn(Optional.of(utredning))
                .when(utredningRepository)
                .findById(anyLong());

        assertThat(utredning.getHandlaggare()).isNotNull();

        final Utredning updated = utredningService.updateOrder(UpdateOrderRequest.from(updateOrderType));

        assertThat(updated.getHandlaggare()).isNotNull();
        assertThat(updated.getHandlaggare().getEmail()).isNull();
        assertThat(updated.getHandlaggare().getFullstandigtNamn()).isEqualTo(handlaggareNamn);
        assertThat(updated.getHandlaggare().getKontor()).isEqualTo(newofficeName);

        verify(notifieringSendService, times(1)).notifieraVardenhetUppdateradBestallning(any(Utredning.class));
    }

    @Test
    public void uppdateraOrderAndraOptionalHandlaggareFaltUtanTidigareHandlaggareOk() {

        final String emptyString = "";
        final String nullString = null;
        final String newofficeName = "NewOfficeName";

        Utredning utredning = createUtredning();
        utredning.setHandlaggare(null);

        UpdateOrderType updateOrderType = createUpdateOrderType(null, null, null);

        AuthorityAdministrativeOfficialType admin = new AuthorityAdministrativeOfficialType();
        admin.setEmail(emptyString); // should be resolved to a null column
        admin.setFullName(nullString); //should not be updated
        admin.setOfficeName(newofficeName); //should be updated

        updateOrderType.setUpdatedAuthorityAdministrativeOfficial(admin);

        doReturn(Optional.of(utredning))
                .when(utredningRepository)
                .findById(anyLong());

        assertThat(utredning.getHandlaggare()).isNull();

        final Utredning updated = utredningService.updateOrder(UpdateOrderRequest.from(updateOrderType));

        assertThat(updated.getHandlaggare()).isNotNull();
        assertThat(updated.getHandlaggare().getEmail()).isNull();
        assertThat(updated.getHandlaggare().getFullstandigtNamn()).isNull();
        assertThat(updated.getHandlaggare().getKontor()).isEqualTo(newofficeName);

        verify(notifieringSendService, times(1)).notifieraVardenhetUppdateradBestallning(any(Utredning.class));
    }

    @Test
    public void uppdateraOrderUtanForandringNok() {

        doReturn(Optional.of(createUtredning()))
                .when(utredningRepository)
                .findById(anyLong());

        UpdateOrderType update = new UpdateOrderType();
        update.setAssessmentId(anII("root", "1"));

        assertThatThrownBy(() -> utredningService.updateOrder(UpdateOrderRequest.from(update)))
                .isExactlyInstanceOf(IbServiceException.class)
                .hasMessage("No info to update");
    }

    @Test
    public void uppdateraOrderWithFasAvslutadNok() {

        Utredning utredning = createUtredning();
        utredning.setStatus(UtredningStatus.AVSLUTAD);

        final UpdateOrderRequest updateOrderRequest = UpdateOrderRequest.from(createUpdateOrderType(true, "sv", false));

        doReturn(Optional.of(utredning))
                .when(utredningRepository)
                .findById(anyLong());

        assertThatThrownBy(() -> utredningService.updateOrder(updateOrderRequest))
                .isExactlyInstanceOf(IbServiceException.class)
                .hasFieldOrPropertyWithValue("errorCode", IbErrorCodeEnum.BAD_STATE);

        verify(notifieringSendService, times(0)).notifieraVardenhetUppdateradBestallning(any(Utredning.class));
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

        when(hsaOrganizationService.getVardenhet(vardenhetHsaId)).thenReturn(new Vardenhet(vardenhetHsaId, vardenhetNamn));

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

        when(hsaOrganizationService.getVardenhet(tidigareVardenhetHsaId1))
                .thenReturn(new Vardenhet(tidigareVardenhetHsaId1, tidigareVardenhetNamn1));
        when(hsaOrganizationService.getVardenhet(tidigareVardenhetHsaId2)).thenThrow(new WebServiceException(hsaError2));

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
    public void testAvslutaUtredningJavSuccess() {
        final Long utredningId = 1L;

        doReturn(Optional.of(anUtredning()
                .withUtredningId(utredningId)
                .withStatus(UtredningStatus.BESTALLNING_MOTTAGEN_VANTAR_PA_HANDLINGAR)
                .build()))
                .when(utredningRepository)
                .findById(utredningId);

        doReturn(anUtredning()
                .withUtredningId(utredningId)
                .withAvbrutenDatum(DATE_TIME.plusMonths(2))
                .withAvbrutenOrsak(AvslutOrsak.JAV)
                .build())
                .when(utredningRepository)
                .saveUtredning(any(Utredning.class));

        AvslutaUtredningRequest request = anEndUtredningRequest()
                .withUtredningId(utredningId)
                .withEndReason(AvslutOrsak.JAV)
                .build();

        utredningService.avslutaUtredning(request);

        ArgumentCaptor<Utredning> captor = ArgumentCaptor.forClass(Utredning.class);
        verify(utredningRepository).saveUtredning(captor.capture());

        Utredning utredning = captor.getValue();
        assertNotNull(utredning);
        assertEquals(utredningId, utredning.getUtredningId());
        assertNotNull(utredning.getAvbrutenDatum());
        assertEquals(AvslutOrsak.JAV, utredning.getAvbrutenOrsak());
    }

    @Test
    public void testAvslutaUtredningJavIncorrectStateNok() {
        final Utredning utredning = createUtredning();
        utredning.setStatus(UtredningStatus.KOMPLETTERING_MOTTAGEN); //Felaktig status -> Ska generera exception

        final AvslutaUtredningRequest request = anEndUtredningRequest()
                .withUtredningId(utredning.getUtredningId())
                .withEndReason(AvslutOrsak.JAV)
                .build();

        doReturn(Optional.of(utredning))
                .when(utredningRepository)
                .findById(utredning.getUtredningId());

        assertThatThrownBy(() -> utredningService.avslutaUtredning(request))
                .isExactlyInstanceOf(IllegalStateException.class)
                .hasMessage(MessageFormat.format("Utredning with id {0} is in an incorrect state", utredning.getUtredningId()));
    }

    @Test
    public void testAvslutaUtredningIngenBestallningSuccess() {
        final Long utredningId = 1L;

        final Utredning utredning = anUtredning()
                .withUtredningId(utredningId)
                .withStatus(UtredningStatus.TILLDELAD_VANTAR_PA_BESTALLNING)
                .withExternForfragan(anExternForfragan()
                        .withInternForfraganList(Lists.newArrayList(
                                anInternForfragan()
                                        .withId(1L)
                                        .withTilldeladDatum(DATE_TIME.plusMonths(2))
                                        .withForfraganSvar(aForfraganSvar()
                                                .withSvarTyp(SvarTyp.ACCEPTERA)
                                                .build())
                                        .build()))
                        .build())
                .build();

        Utredning uppdateradUtredning = Utredning.copyFrom(utredning);
        assertNotNull(uppdateradUtredning);
        uppdateradUtredning.setAvbrutenDatum(DATE_TIME.plusMonths(4));
        uppdateradUtredning.setAvbrutenOrsak(AvslutOrsak.INGEN_BESTALLNING);

        doReturn(Optional.of(utredning))
                .when(utredningRepository)
                .findById(utredningId);

        doReturn(uppdateradUtredning)
                .when(utredningRepository)
                .saveUtredning(any(Utredning.class));

        AvslutaUtredningRequest request = anEndUtredningRequest()
                .withUtredningId(utredningId)
                .withEndReason(AvslutOrsak.INGEN_BESTALLNING)
                .build();

        utredningService.avslutaUtredning(request);

        ArgumentCaptor<Utredning> captor = ArgumentCaptor.forClass(Utredning.class);
        verify(utredningRepository).saveUtredning(captor.capture());

        Utredning utredningCaptor = captor.getValue();
        assertNotNull(utredningCaptor);
        assertEquals(utredningId, utredningCaptor.getUtredningId());
        assertNotNull(utredningCaptor.getAvbrutenDatum());
        assertEquals(AvslutOrsak.INGEN_BESTALLNING, utredningCaptor.getAvbrutenOrsak());

        verify(notifieringSendService, times(1)).notifieraLandstingIngenBestallning(any(Utredning.class), any(InternForfragan.class));
        verify(notifieringSendService, times(1)).notifieraVardenhetIngenBestallning(any(Utredning.class), any(InternForfragan.class));
    }

    @Test
    public void testAvslutaUtredningIngenBestallningIncorrectStateNok() {
        final Utredning utredning = createUtredning();
        utredning.setStatus(UtredningStatus.KOMPLETTERING_MOTTAGEN); //Felaktig status -> Ska generera exception

        final AvslutaUtredningRequest request = anEndUtredningRequest()
                .withUtredningId(utredning.getUtredningId())
                .withEndReason(AvslutOrsak.INGEN_BESTALLNING)
                .build();

        doReturn(Optional.of(utredning))
                .when(utredningRepository)
                .findById(utredning.getUtredningId());

        assertThatThrownBy(() -> utredningService.avslutaUtredning(request))
                .isExactlyInstanceOf(IllegalStateException.class)
                .hasMessage(MessageFormat.format("Utredning with id {0} is in an incorrect state", utredning.getUtredningId()));
    }

    @Test
    public void testAvslutaUtredningUtredningAvbrutenSuccess() {
        final Long utredningId = 1L;

        final Utredning utredning = anUtredning()
                .withUtredningId(utredningId)
                .withStatus(UtredningStatus.UTREDNING_PAGAR)
                .withExternForfragan(anExternForfragan()
                        .withInternForfraganList(Lists.newArrayList(
                                anInternForfragan()
                                        .withId(1L)
                                        .withTilldeladDatum(DATE_TIME.plusMonths(2))
                                        .withForfraganSvar(aForfraganSvar()
                                                .withSvarTyp(SvarTyp.ACCEPTERA)
                                                .build())
                                        .build()))
                        .build())
                .build();

        Utredning uppdateradUtredning = Utredning.copyFrom(utredning);
        assertNotNull(uppdateradUtredning);
        uppdateradUtredning.setAvbrutenDatum(DATE_TIME.plusMonths(4));
        uppdateradUtredning.setAvbrutenOrsak(AvslutOrsak.UTREDNING_AVBRUTEN);

        doReturn(Optional.of(utredning))
                .when(utredningRepository)
                .findById(utredningId);

        doReturn(uppdateradUtredning)
                .when(utredningRepository)
                .saveUtredning(any(Utredning.class));

        AvslutaUtredningRequest request = anEndUtredningRequest()
                .withUtredningId(utredningId)
                .withEndReason(AvslutOrsak.UTREDNING_AVBRUTEN)
                .build();

        utredningService.avslutaUtredning(request);

        ArgumentCaptor<Utredning> captor = ArgumentCaptor.forClass(Utredning.class);
        verify(utredningRepository).saveUtredning(captor.capture());

        Utredning utredningCaptor = captor.getValue();
        assertNotNull(utredningCaptor);
        assertEquals(utredningId, utredningCaptor.getUtredningId());
        assertNotNull(utredningCaptor.getAvbrutenDatum());
        assertEquals(AvslutOrsak.UTREDNING_AVBRUTEN, utredningCaptor.getAvbrutenOrsak());

        verify(notifieringSendService, times(1)).notifieraLandstingAvslutadUtredning(any(Utredning.class));
        verify(notifieringSendService, times(1)).notifieraVardenhetAvslutadUtredning(any(Utredning.class));
    }

    @Test
    public void testAvslutaUtredningUtredningAvbrutenIncorrectStateNok() {
        final Utredning utredning = createUtredning();
        utredning.setStatus(UtredningStatus.KOMPLETTERING_MOTTAGEN); //Felaktig status -> Ska generera exception

        final AvslutaUtredningRequest request = anEndUtredningRequest()
                .withUtredningId(utredning.getUtredningId())
                .withEndReason(AvslutOrsak.UTREDNING_AVBRUTEN)
                .build();

        doReturn(Optional.of(utredning))
                .when(utredningRepository)
                .findById(utredning.getUtredningId());

        assertThatThrownBy(() -> utredningService.avslutaUtredning(request))
                .isExactlyInstanceOf(IllegalStateException.class)
                .hasMessage(MessageFormat.format("Utredning with id {0} is in an incorrect state", utredning.getUtredningId()));
    }

    @Test
    public void testAvslutaUtredningIngenKompletteringBegardSuccess() {
        final Long utredningId = 1L;

        final Utredning utredning = anUtredning()
                .withUtredningId(utredningId)
                .withBestallning(aBestallning()
                        .withId(1L)
                        .build())
                .withBesokList(Lists.newArrayList(
                        aBesok()
                                .withTolkStatus(TolkStatusTyp.DELTAGIT)
                                .withBesokStatus(BesokStatusTyp.TIDBOKAD_VARDKONTAKT)
                                .withHandelseList(Lists.newArrayList(
                                        aHandelse()
                                                .withHandelseTyp(HandelseTyp.AVVIKELSE_RAPPORTERAD)
                                                .build()))
                                .build(),
                        aBesok()
                                .withTolkStatus(TolkStatusTyp.DELTAGIT)
                                .withBesokStatus(BesokStatusTyp.AVSLUTAD_VARDKONTAKT)
                                .withHandelseList(Lists.newArrayList(
                                        aHandelse()
                                                .withHandelseTyp(HandelseTyp.AVVIKELSE_RAPPORTERAD)
                                                .build()))
                                .build()
                ))
                .withExternForfragan(anExternForfragan()
                        .withInternForfraganList(Lists.newArrayList(
                                anInternForfragan()
                                        .withId(1L)
                                        .withTilldeladDatum(DATE_TIME.plusMonths(2))
                                        .withForfraganSvar(aForfraganSvar()
                                                .withSvarTyp(SvarTyp.ACCEPTERA)
                                                .build())
                                        .build()))
                        .build())
                .build();

        Utredning uppdateradUtredning = Utredning.copyFrom(utredning);
        assertNotNull(uppdateradUtredning);
        uppdateradUtredning.setAvbrutenDatum(DATE_TIME.plusMonths(4));
        uppdateradUtredning.setAvbrutenOrsak(AvslutOrsak.INGEN_KOMPLETTERING_BEGARD);

        doReturn(Optional.of(utredning))
                .when(utredningRepository)
                .findById(utredningId);

        doReturn(uppdateradUtredning)
                .when(utredningRepository)
                .saveUtredning(any(Utredning.class));

        final AvslutaUtredningRequest request = anEndUtredningRequest()
                .withUtredningId(utredningId)
                .withEndReason(AvslutOrsak.INGEN_KOMPLETTERING_BEGARD)
                .withUser(IbUser.of("hsa-id", "Test-Are Testsson"))
                .build();

        utredningService.avslutaUtredning(request);

        ArgumentCaptor<Utredning> captor = ArgumentCaptor.forClass(Utredning.class);
        verify(utredningRepository).saveUtredning(captor.capture());

        Utredning utredningCaptor = captor.getValue();
        assertNotNull(utredningCaptor);
        assertEquals(utredningId, utredningCaptor.getUtredningId());
        assertNotNull(utredningCaptor.getAvbrutenDatum());
        assertEquals(AvslutOrsak.INGEN_KOMPLETTERING_BEGARD, utredningCaptor.getAvbrutenOrsak());

        verifyZeroInteractions(notifieringSendService);
    }

    @Test
    public void testAvslutaUtredningFailUtredningNotExisting() {
        final Long utredningId = 1L;

        when(utredningRepository.findById(utredningId)).thenReturn(Optional.empty());

        AvslutaUtredningRequest request = anEndUtredningRequest()
                .withUtredningId(utredningId)
                .withEndReason(AvslutOrsak.JAV)
                .build();

        assertThatThrownBy(() -> utredningService.avslutaUtredning(request))
                .isExactlyInstanceOf(IbNotFoundException.class)
                .hasMessage("Angivet utredningsid existerar inte")
                .hasFieldOrPropertyWithValue("errorCode", IbErrorCodeEnum.NOT_FOUND);
    }

    @Test
    public void testAvslutaUtredningFailAlreadyEnded() {
        final Long utredningId = 1L;

        when(utredningRepository.findById(utredningId)).thenReturn(Optional.of(anUtredning()
                .withUtredningId(utredningId)
                .withAvbrutenDatum(LocalDateTime.now())
                .build()));

        AvslutaUtredningRequest request = anEndUtredningRequest()
                .withUtredningId(utredningId)
                .withEndReason(AvslutOrsak.JAV)
                .build();

        assertThatThrownBy(() -> utredningService.avslutaUtredning(request))
                .isExactlyInstanceOf(IbServiceException.class)
                .hasMessage(MessageFormat.format("EndAssessment has already been performed for Utredning {0}", utredningId))
                .hasFieldOrPropertyWithValue("errorCode", IbErrorCodeEnum.ALREADY_EXISTS);
    }

    @Test
    public void testSaveBetalningForUtredningSuccess() {
        doReturn(Optional.of(TestDataGen.createUtredning())).when(utredningRepository).findById(TestDataGen.getUtredningId());

        SaveBetalningForUtredningRequest request = new SaveBetalningForUtredningRequest();
        request.setBetalningId("testBetalningId");
        utredningService.saveBetalningsIdForUtredning(TestDataGen.getUtredningId(), request, TestDataGen.getLandstingId());

        ArgumentCaptor<Utredning> utredingCaptor = ArgumentCaptor.forClass(Utredning.class);
        verify(utredningRepository, times(1)).saveUtredning(utredingCaptor.capture());

        assertEquals("testBetalningId", utredingCaptor.getValue().getBetalning().getBetalningsId());
    }

    @Test(expected = IbNotFoundException.class)
    public void testSaveBetalningForUtredningFailNotFound() {
        doReturn(Optional.empty()).when(utredningRepository).findById(TestDataGen.getUtredningId());

        SaveBetalningForUtredningRequest request = new SaveBetalningForUtredningRequest();
        request.setBetalningId("testBetalningId");
        utredningService.saveBetalningsIdForUtredning(TestDataGen.getUtredningId(), request, TestDataGen.getLandstingId());
    }

    @Test(expected = IbAuthorizationException.class)
    public void testSaveBetalningForUtredningFailDifferentLandsting() {
        doReturn(Optional.of(TestDataGen.createUtredning())).when(utredningRepository).findById(TestDataGen.getUtredningId());

        SaveBetalningForUtredningRequest request = new SaveBetalningForUtredningRequest();
        request.setBetalningId("testBetalningId");
        utredningService.saveBetalningsIdForUtredning(TestDataGen.getUtredningId(), request, "AnnatLandsting");
    }

    @Test
    public void testSaveUtbetalningForUtredningSuccess() {
        doReturn(Optional.of(TestDataGen.createUtredning())).when(utredningRepository).findById(TestDataGen.getUtredningId());

        SaveUtbetalningForUtredningRequest request = new SaveUtbetalningForUtredningRequest();
        request.setUtbetalningId("testUtbetalningId");
        utredningService.saveUtbetalningsIdForUtredning(TestDataGen.getUtredningId(), request, TestDataGen.getLandstingId());

        ArgumentCaptor<Utredning> utredingCaptor = ArgumentCaptor.forClass(Utredning.class);
        verify(utredningRepository, times(1)).saveUtredning(utredingCaptor.capture());

        assertEquals("testUtbetalningId", utredingCaptor.getValue().getBetalning().getUtbetalningsId());
    }

    @Test(expected = IbNotFoundException.class)
    public void testSaveUtbetalningForUtredningFailNotFound() {
        doReturn(Optional.empty()).when(utredningRepository).findById(TestDataGen.getUtredningId());

        SaveUtbetalningForUtredningRequest request = new SaveUtbetalningForUtredningRequest();
        request.setUtbetalningId("testUtbetalningId");
        utredningService.saveUtbetalningsIdForUtredning(TestDataGen.getUtredningId(), request, TestDataGen.getLandstingId());
    }

    @Test(expected = IbAuthorizationException.class)
    public void testSaveUtbetalningForUtredningFailDifferentLandsting() {
        doReturn(Optional.of(TestDataGen.createUtredning())).when(utredningRepository).findById(TestDataGen.getUtredningId());

        SaveUtbetalningForUtredningRequest request = new SaveUtbetalningForUtredningRequest();
        request.setUtbetalningId("testUtbetalningId");
        utredningService.saveUtbetalningsIdForUtredning(TestDataGen.getUtredningId(), request, "AnnatLandsting");
    }
}
