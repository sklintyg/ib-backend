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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
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
import se.inera.intyg.intygsbestallning.persistence.model.type.SvarTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.UtredningsTyp;
import se.inera.intyg.intygsbestallning.persistence.repository.ExternForfraganRepository;
import se.inera.intyg.intygsbestallning.persistence.repository.UtredningRepository;
import se.inera.intyg.intygsbestallning.service.util.BusinessDaysStub;
import se.inera.intyg.intygsbestallning.service.utredning.UtredningService;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.forfragan.GetForfraganListResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.forfragan.InternForfraganListItemFactory;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.forfragan.ListForfraganRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @Test
    public void testAvvisaExternForfraganSuccess() {
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

        testee.avvisaExternForfragan(utredningId, landstingHsaId, kommentar);

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
    public void testAcceptInternForfraganUtredningNotExisting() {
        final Long utredningId = 1L;
        final String landstingHsaId = "landstingHsaId";
        final String kommentar = "Ingen kommentar";

        when(utredningRepository.findById(utredningId)).thenReturn(Optional.empty());

        testee.avvisaExternForfragan(utredningId, landstingHsaId, kommentar);
    }

    @Test(expected = IbAuthorizationException.class)
    public void testAcceptInternForfraganFailDifferentLandsting() {
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
    public void testAcceptInternForfraganFailIncorrectState() {
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
