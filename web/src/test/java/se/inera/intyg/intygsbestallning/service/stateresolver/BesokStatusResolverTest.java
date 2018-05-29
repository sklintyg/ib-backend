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
package se.inera.intyg.intygsbestallning.service.stateresolver;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import se.inera.intyg.intygsbestallning.persistence.model.Besok;
import se.inera.intyg.intygsbestallning.persistence.model.type.AvvikelseOrsak;
import se.inera.intyg.intygsbestallning.persistence.model.type.HandelseTyp;

import static org.junit.Assert.assertEquals;
import static se.inera.intyg.intygsbestallning.persistence.model.Avvikelse.AvvikelseBuilder.anAvvikelse;
import static se.inera.intyg.intygsbestallning.persistence.model.Besok.BesokBuilder.aBesok;
import static se.inera.intyg.intygsbestallning.persistence.model.Handelse.HandelseBuilder.aHandelse;

@RunWith(MockitoJUnitRunner.class)
public class BesokStatusResolverTest {

    @InjectMocks
    private BesokStatusResolver testee;

    @Test
    public void TestBokat() {
        Besok besok = aBesok().build();
        BesokStatus status = testee.resolveStatus(besok);
        assertEquals(BesokStatus.BOKAT, status);
    }

    @Test
    public void TestOmbokat() {
        Besok besok = aBesok()
                .withHandelseList(ImmutableList.of(
                        aHandelse().withHandelseTyp(HandelseTyp.OMBOKAT_BESOK).build()))
                .build();
        BesokStatus status = testee.resolveStatus(besok);
        assertEquals(BesokStatus.OMBOKAT, status);
    }

    @Test
    public void TestAvbokat() {
        Besok besok = aBesok()
                .withHandelseList(ImmutableList.of(
                        aHandelse().withHandelseTyp(HandelseTyp.AVVIKELSE_MOTTAGEN).build(),
                        aHandelse().withHandelseTyp(HandelseTyp.AVBOKAT_BESOK).build()))
                .build();
        BesokStatus status = testee.resolveStatus(besok);
        assertEquals(BesokStatus.AVBOKAT, status);
    }

    @Test
    public void TestAvvikelseRapporterad() {
        Besok besok = aBesok()
                .withAvvikelse(anAvvikelse()
                        .withOrsakatAv(AvvikelseOrsak.VARDEN)
                        .withInvanareUteblev(false)
                        .build())
                .withHandelseList(ImmutableList.of(
                        aHandelse().withHandelseTyp(HandelseTyp.AVVIKELSE_RAPPORTERAD).build()))
                .build();
        BesokStatus status = testee.resolveStatus(besok);
        assertEquals(BesokStatus.AVVIKELSE_RAPPORTERAD, status);
    }

    @Test
    public void TestAvvikelseMottagen() {
        Besok besok = aBesok()
                .withHandelseList(ImmutableList.of(
                        aHandelse().withHandelseTyp(HandelseTyp.AVVIKELSE_MOTTAGEN).build()))
                .build();
        BesokStatus status = testee.resolveStatus(besok);
        assertEquals(BesokStatus.AVVIKELSE_MOTTAGEN, status);
    }

    @Test
    public void TestPatientUteblev() {
        Besok besok = aBesok()
                .withAvvikelse(anAvvikelse()
                        .withOrsakatAv(AvvikelseOrsak.PATIENT)
                        .withInvanareUteblev(true)
                        .build())
                .withHandelseList(ImmutableList.of(
                        aHandelse().withHandelseTyp(HandelseTyp.AVVIKELSE_RAPPORTERAD).build()))
                .build();
        BesokStatus status = testee.resolveStatus(besok);
        assertEquals(BesokStatus.PATIENT_UTEBLEV, status);
    }

}
