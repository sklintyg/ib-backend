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
package se.inera.intyg.intygsbestallning.service.notifiering.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static se.inera.intyg.intygsbestallning.persistence.model.ForfraganSvar.ForfraganSvarBuilder.aForfraganSvar;
import static se.inera.intyg.intygsbestallning.persistence.model.InternForfragan.InternForfraganBuilder.anInternForfragan;
import static se.inera.intyg.intygsbestallning.testutil.TestDataGen.DATE_TIME;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import java.util.Optional;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.VardenhetPreference;
import se.inera.intyg.intygsbestallning.persistence.model.type.UtforareTyp;
import se.inera.intyg.intygsbestallning.service.vardenhet.VardenhetService;
import se.inera.intyg.intygsbestallning.testutil.TestDataGen;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.vardenhet.VardenhetPreferenceResponse;

@RunWith(MockitoJUnitRunner.class)
public class NotifieringEpostResolverTest {

    @Mock
    private VardenhetService vardenhetService;

    @InjectMocks
    private NotifieringEpostResolver epostResolver;

    @Test
    public void resolveEpostFromForfraganSvar() {

        final String epost = "tolvan.tolvansson@epost.se";

        final Utredning utredning = TestDataGen.createUtredning();
        utredning.getExternForfragan()
                .map(externForfragan -> externForfragan.getInternForfraganList().add(anInternForfragan()
                        .withId(1L)
                        .withTilldeladDatum(DATE_TIME.plusMonths(3))
                        .withForfraganSvar(aForfraganSvar()
                                .withUtforareEpost(epost)
                                .build())
                        .build()));

        final Optional<String> resolvedEpost = epostResolver.resolveVardenhetNotifieringEpost("hsaId", utredning);

        verifyZeroInteractions(vardenhetService);

        assertThat(resolvedEpost).isPresent();
        assertThat(resolvedEpost.get()).isEqualTo(epost);
    }

    @Test
    public void resolveEpostFromVardenhetService() {

        final String epost = "tolvan.tolvansson@epost.se";

        VardenhetPreference preferenceEntity = new VardenhetPreference();
        preferenceEntity.setEpost(epost);
        preferenceEntity.setUtforareTyp(UtforareTyp.ENHET);

        final VardenhetPreferenceResponse vardenhetPreference = new VardenhetPreferenceResponse(preferenceEntity);

        doReturn(vardenhetPreference)
                .when(vardenhetService)
                .getVardEnhetPreference(anyString());

        final Utredning utredning = TestDataGen.createUtredning();

        final Optional<String> resolvedEpost = epostResolver.resolveVardenhetNotifieringEpost("hsaId", utredning);

        verify(vardenhetService, times(1)).getVardEnhetPreference(anyString());

        assertThat(resolvedEpost).isPresent();
        assertThat(resolvedEpost.get()).isEqualTo(epost);
    }

    @Test
    public void resolveEpostNoEpostFound() {

        VardenhetPreference preferenceEntity = new VardenhetPreference();
        preferenceEntity.setUtforareTyp(UtforareTyp.ENHET);

        final VardenhetPreferenceResponse vardenhetPreference = new VardenhetPreferenceResponse(preferenceEntity);

        doReturn(vardenhetPreference)
                .when(vardenhetService)
                .getVardEnhetPreference(anyString());

        final Utredning utredning = TestDataGen.createUtredning();

        final Optional<String> resolvedEpost = epostResolver.resolveVardenhetNotifieringEpost("hsaId", utredning);

        verify(vardenhetService, times(1)).getVardEnhetPreference(anyString());

        assertThat(resolvedEpost).isNotPresent();
    }

}