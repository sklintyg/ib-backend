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
import se.inera.intyg.intygsbestallning.common.exception.IbErrorCodeEnum;
import se.inera.intyg.intygsbestallning.common.exception.IbNotFoundException;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.persistence.model.EndReason;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.repository.UtredningRepository;
import se.inera.intyg.intygsbestallning.service.stateresolver.UtredningStateResolver;
import se.inera.intyg.intygsbestallning.service.utredning.dto.EndUtredningRequest;
import se.inera.intyg.intygsbestallning.service.utredning.dto.OrderRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.ForfraganListItem;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.GetUtredningResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.UtredningListItem;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static se.inera.intyg.intygsbestallning.persistence.model.ExternForfragan.ExternForfraganBuilder.anExternForfragan;
import static se.inera.intyg.intygsbestallning.persistence.model.Handlaggare.HandlaggareBuilder.aHandlaggare;
import static se.inera.intyg.intygsbestallning.persistence.model.InternForfragan.InternForfraganBuilder.anInternForfragan;
import static se.inera.intyg.intygsbestallning.persistence.model.Invanare.InvanareBuilder.anInvanare;
import static se.inera.intyg.intygsbestallning.persistence.model.Utredning.UtredningBuilder.anUtredning;
import static se.inera.intyg.intygsbestallning.persistence.model.UtredningsTyp.AFU;
import static se.inera.intyg.intygsbestallning.persistence.model.UtredningsTyp.LIAG;
import static se.inera.intyg.intygsbestallning.service.utredning.dto.Bestallare.BestallareBuilder.aBestallare;
import static se.inera.intyg.intygsbestallning.service.utredning.dto.EndUtredningRequest.EndUtredningRequestBuilder.anEndUtredningRequest;
import static se.inera.intyg.intygsbestallning.service.utredning.dto.OrderRequest.OrderRequestBuilder.anOrderRequest;

@RunWith(MockitoJUnitRunner.class)
public class UtredningServiceImplTest {

    @Mock
    private UtredningRepository utredningRepository;

    @Spy
    private UtredningStateResolver utredningStateResolver;

    @InjectMocks
    private UtredningServiceImpl utredningService;

    @Test
    public void findForfragningarForVardenhetHsaId() {
        final String enhetId = "enhet";
        final String utredningId = "utredningId";
        // Almost bare minimum, converting is not in the scope of utredningService
        Utredning utr = anUtredning()
                .withUtredningsTyp(AFU)
                .withUtredningId(utredningId)
                .withExternForfragan(anExternForfragan()
                        .withInternForfraganList(ImmutableList.of(
                                anInternForfragan()
                                        .withVardenhetHsaId(enhetId)
                                        .build()
                        ))
                        .build())
                .build();
        when(utredningRepository.findAllByExternForfragan_InternForfraganList_VardenhetHsaId(enhetId)).thenReturn(ImmutableList.of(utr));

        List<ForfraganListItem> response = utredningService.findForfragningarForVardenhetHsaId(enhetId);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(AFU.name(), response.get(0).getUtredningsTyp());
        assertEquals(utredningId, response.get(0).getUtredningsId());
    }

    @Test
    public void registerOrder() {
        final String utredningId = "utredningId";

        when(utredningRepository.findById(utredningId)).thenReturn(Optional.of(anUtredning()
                .withUtredningId(utredningId)
                .withUtredningsTyp(AFU)
                .withInvanare(anInvanare()
                        .withPostkod("invanarePostkod")
                        .build())
                .withExternForfragan(anExternForfragan().build())
                .build()));

        OrderRequest order = anOrderRequest()
                .withUtredningsTyp(AFU)
                .withUtredningId(utredningId)
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
        assertEquals("sv", response.getSprakTolk());
        assertEquals(AFU, response.getUtredningsTyp());
        assertEquals("kommentar", response.getBestallning().getKommentar());
        assertEquals("atgarder", response.getBestallning().getPlaneradeAktiviteter());
        assertEquals("syfte", response.getBestallning().getSyfte());
        assertEquals("enhet", response.getBestallning().getTilldeladVardenhetHsaId());
        assertEquals(LocalDate.of(2019, 1, 1).atStartOfDay(), response.getBestallning().getIntygKlartSenast());
        assertEquals(LocalDate.of(2018, 1, 1).atStartOfDay(), response.getBestallning().getOrderDatum());
        assertNull(response.getBestallning().getUppdateradDatum());
        assertEquals("behov", response.getInvanare().getSarskildaBehov());
        assertEquals("personnummer", response.getInvanare().getPersonId());
        assertEquals("bakgrund", response.getInvanare().getBakgrundNulage());
        assertEquals("invanarePostkod", response.getInvanare().getPostkod());
        assertNotNull("", response.getHandlingList());
        assertEquals(1, response.getHandlingList().size());
        assertNull(response.getHandlingList().get(0).getInkomDatum());
        assertNotNull("", response.getHandlingList().get(0).getSkickatDatum());
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
        final String utredningId = "utredningId";

        when(utredningRepository.findById(utredningId)).thenReturn(Optional.of(anUtredning()
                .withUtredningId(utredningId)
                .withUtredningsTyp(AFU)
                .withInvanare(anInvanare()
                        .withPostkod("invanarePostkod")
                        .build())
                .build()));

        OrderRequest order = anOrderRequest().withUtredningId(utredningId).build();

        utredningService.registerOrder(order);
    }

    @Test(expected = IbNotFoundException.class)
    public void registerOrderNoPreviousUtredning() {
        final String utredningId = "utredningId";

        when(utredningRepository.findById(utredningId)).thenReturn(Optional.empty());

        OrderRequest order = anOrderRequest().withUtredningId(utredningId).build();

        utredningService.registerOrder(order);
    }

    @Test
    public void registerNewUtredning() {
        OrderRequest order = anOrderRequest()
                .withUtredningsTyp(LIAG)
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
        assertNotNull(response.getUtredningId());
        assertEquals("sv", response.getSprakTolk());
        assertEquals(LIAG, response.getUtredningsTyp());
        assertEquals("kommentar", response.getBestallning().getKommentar());
        assertEquals("atgarder", response.getBestallning().getPlaneradeAktiviteter());
        assertEquals("syfte", response.getBestallning().getSyfte());
        assertEquals("enhet", response.getBestallning().getTilldeladVardenhetHsaId());
        assertEquals(LocalDate.of(2019, 1, 1).atStartOfDay(), response.getBestallning().getIntygKlartSenast());
        assertEquals(LocalDate.of(2018, 1, 1).atStartOfDay(), response.getBestallning().getOrderDatum());
        assertNull(response.getBestallning().getUppdateradDatum());
        assertEquals("behov", response.getInvanare().getSarskildaBehov());
        assertEquals("personnummer", response.getInvanare().getPersonId());
        assertEquals("bakgrund", response.getInvanare().getBakgrundNulage());
        assertNull(response.getInvanare().getPostkod());
        assertNotNull("", response.getHandlingList());
        assertEquals(1, response.getHandlingList().size());
        assertNull(response.getHandlingList().get(0).getInkomDatum());
        assertNotNull("", response.getHandlingList().get(0).getSkickatDatum());
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
    public void testGetUtredningSuccess() {
        final String utredningId = "utredningId";
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
        assertEquals(landstingHsaId, response.getVardgivareHsaId());
    }

    @Test(expected = IbNotFoundException.class)
    public void testGetUtredningIncorrectLandsting() {
        final String utredningId = "utredningId";
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
        final String utredningId = "utredningId";
        final String landstingHsaId = "landstingHsaId";
        when(utredningRepository.findById(utredningId)).thenReturn(Optional.empty());
        utredningService.getExternForfragan(utredningId, landstingHsaId);
    }

    @Test
    public void testFindUtredningarByLandstingHsaId() {
        final String landstingHsaId = "landstingHsaId";
        when(utredningRepository.findAllByExternForfragan_LandstingHsaId(landstingHsaId)).thenReturn(
                ImmutableList.of(anUtredning()
                        .withUtredningId("utredningId")
                        .withUtredningsTyp(AFU)
                        .withExternForfragan(anExternForfragan()
                                .withLandstingHsaId(landstingHsaId)
                                .build())
                        .withInvanare(anInvanare()
                                .withPersonId("personnummer")
                                .build())
                        .build()));

        List<UtredningListItem> response = utredningService.findExternForfraganByLandstingHsaId(landstingHsaId);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(landstingHsaId, response.get(0).getVardgivareNamn());
        assertEquals("utredningId", response.get(0).getUtredningsId());
        assertEquals(AFU.name(), response.get(0).getUtredningsTyp());
    }

    @Test
    public void testEndUtredningSuccess() {
        final String utredningId = "utredningId";

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
        final String utredningId = "utredningId";

        when(utredningRepository.findById(utredningId)).thenReturn(Optional.empty());

        EndUtredningRequest request = anEndUtredningRequest()
                .withUtredningId(utredningId)
                .withEndReason(EndReason.JAV)
                .build();

        utredningService.endUtredning(request);
    }

    @Test(expected = IbServiceException.class)
    public void testEndUtredningFailAlreadyEnded() {
        final String utredningId = "utredningId";

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
