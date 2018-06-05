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
package se.inera.intyg.intygsbestallning.service.statistics;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import se.inera.intyg.intygsbestallning.persistence.model.Handling;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.status.UtredningStatusResolver;
import se.inera.intyg.intygsbestallning.persistence.repository.UtredningRepository;
import se.inera.intyg.intygsbestallning.service.util.BusinessDaysBean;
import se.inera.intyg.intygsbestallning.service.util.BusinessDaysStub;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.bestallning.BestallningListItemFactory;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.forfragan.InternForfraganListItemFactory;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.statistics.SamordnarStatisticsResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.statistics.VardadminStatisticsResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.utredning.UtredningListItemFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static se.inera.intyg.intygsbestallning.persistence.model.Bestallning.BestallningBuilder.aBestallning;
import static se.inera.intyg.intygsbestallning.persistence.model.ExternForfragan.ExternForfraganBuilder.anExternForfragan;
import static se.inera.intyg.intygsbestallning.persistence.model.InternForfragan.InternForfraganBuilder.anInternForfragan;
import static se.inera.intyg.intygsbestallning.persistence.model.Intyg.IntygBuilder.anIntyg;
import static se.inera.intyg.intygsbestallning.persistence.model.Invanare.InvanareBuilder.anInvanare;
import static se.inera.intyg.intygsbestallning.persistence.model.Utredning.UtredningBuilder.anUtredning;
import static se.inera.intyg.intygsbestallning.persistence.model.type.UtredningsTyp.AFU;

/**
 * Created by marced on 2018-05-04.
 */
@RunWith(MockitoJUnitRunner.class)
public class StatisticsServiceImplTest {

    private static final String VG_ID = "VG-HsaId1";
    private static final String VE_ID = "VE-HsaId1";

    private static final UtredningStatusResolver utredningStatusResolver = new UtredningStatusResolver();

    @Mock
    private UtredningRepository utredningRepository;

    @Spy
    private BestallningListItemFactory bestallningListItemFactory;

    @Spy
    private UtredningListItemFactory utredningListItemFactory = new UtredningListItemFactory(new BusinessDaysStub());

    @Spy
    private InternForfraganListItemFactory internForfraganListItemFactory = new InternForfraganListItemFactory(new BusinessDaysStub());

    @Spy
    private BusinessDaysBean businessDays = new BusinessDaysStub();

    @InjectMocks
    private StatisticsServiceImpl testee;

    private static List<Utredning> buildUtredningarWithExternforfragningar(int num, boolean addInternForfragning) {
        List<Utredning> utredningList = new ArrayList<>();
        for (long a = 0; a < num; a++) {
            Utredning.UtredningBuilder utrBuilder = anUtredning().withUtredningsTyp(AFU).withUtredningId(a);

            if (addInternForfragning) {
                utrBuilder.withExternForfragan(anExternForfragan().withLandstingHsaId(VG_ID).withBesvarasSenastDatum(LocalDateTime.now())
                        .withInternForfraganList(ImmutableList.of(anInternForfragan().withVardenhetHsaId(VE_ID).build())).build());
            } else {
                utrBuilder.withExternForfragan(
                        anExternForfragan().withLandstingHsaId(VG_ID).withBesvarasSenastDatum(LocalDateTime.now()).build());
            }
            Utredning utr = utrBuilder.build();
            utr.setStatus(utredningStatusResolver.resolveStatus(utr));
            utredningList.add(utr);
        }

        return utredningList;
    }

    private static List<Utredning> buildBestallningar(int num, boolean handlingarMottagna) {
        List<Utredning> utredningList = new ArrayList<>();
        for (long a = 0; a < num; a++) {
            Utredning utr = anUtredning()
                    .withUtredningsTyp(AFU)
                    .withUtredningId(a)
                    .withExternForfragan(anExternForfragan()
                            .withInternForfraganList(ImmutableList.of(
                                    anInternForfragan()
                                            .withVardenhetHsaId(VE_ID)
                                            .build()))
                            .withLandstingHsaId(VG_ID)
                            .build())
                    .withBestallning(aBestallning()
                            .withTilldeladVardenhetHsaId(VE_ID)
                            .build())
                    .withInvanare(anInvanare().withPersonId("19121212-121" + a).build())
                    .withIntygList(Collections.singletonList(anIntyg()
                            .withKomplettering(false)
                            .withSistaDatum(LocalDateTime.now().plusDays(10L))
                            .build()))
                    .build();
            if (handlingarMottagna) {
                utr.setHandlingList(buildHandlingsLista());
            }
            // use the resolver to set status even in the test...
            utr.setStatus(utredningStatusResolver.resolveStatus(utr));
            utredningList.add(utr);

        }
        return utredningList;
    }

    private static List<Handling> buildHandlingsLista() {
        List<Handling> handlingar = new ArrayList<>();
        handlingar.add(new Handling());
        return handlingar;
    }

    @Test
    public void testGetStatsForVardadmin() {
        List<Utredning> repoContents = buildUtredningarWithExternforfragningar(3, true);
        when(utredningRepository.findAllByExternForfragan_InternForfraganList_VardenhetHsaId_AndArkiveradFalse(VE_ID))
                .thenReturn(repoContents);

        List<Utredning> bestallningsUtredningar = buildBestallningar(4, true);
        // Add one that will resolve to the an irrelevant status
        bestallningsUtredningar.addAll(buildBestallningar(1, false));
        when(utredningRepository.findAllByBestallning_TilldeladVardenhetHsaId_AndArkiveradFalse(VE_ID)).thenReturn(bestallningsUtredningar);

        final VardadminStatisticsResponse statsForVardadmin = testee.getStatsForVardadmin(VE_ID);

        assertNotNull(statsForVardadmin);
        assertEquals(3, statsForVardadmin.getForfraganRequiringActionCount());
        assertEquals(4, statsForVardadmin.getBestallningarRequiringActionCount());
    }

    @Test
    public void testGetStatsForSamordnare() {
        List<Utredning> repoContents = buildUtredningarWithExternforfragningar(3, false);
        repoContents.addAll(buildUtredningarWithExternforfragningar(2, true));

        when(utredningRepository.findByExternForfragan_LandstingHsaId_AndArkiveradFalse(VG_ID)).thenReturn(repoContents);

        final SamordnarStatisticsResponse result = testee.getStatsForSamordnare(VG_ID);

        assertNotNull(result);
        assertEquals(3, result.getRequireSamordnarActionCount());
    }
}
