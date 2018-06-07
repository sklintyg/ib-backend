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

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import java.time.LocalDateTime;
import se.inera.intyg.intygsbestallning.persistence.model.ExternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.InternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.status.InternForfraganStatus;
import se.inera.intyg.intygsbestallning.persistence.model.status.InternForfraganStatusResolver;
import se.inera.intyg.intygsbestallning.persistence.model.type.AvslutOrsak;
import se.inera.intyg.intygsbestallning.persistence.model.type.SvarTyp;

public class InternForfraganStatusResolverTest extends BaseResolverTest {

    private InternForfraganStatusResolver testee = new InternForfraganStatusResolver();

    @Test
    public void testInkommen() {
        Utredning utredning = buildBaseUtredning();
        ExternForfragan externForfragan = buildBaseExternForfragan();
        utredning.setExternForfragan(externForfragan);
        InternForfragan internForfragan = buildInternForfragan(null, null);
        externForfragan.getInternForfraganList().add(internForfragan);


        InternForfraganStatus status = testee.resolveStatus(utredning, internForfragan);
        assertEquals(InternForfraganStatus.INKOMMEN, status);
    }

    @Test
    public void testIngenBestallningSetButPreviouslyAvvisad() {
        Utredning utredning = buildBaseUtredning();
        utredning.setAvbrutenAnledning(AvslutOrsak.INGEN_BESTALLNING);
        ExternForfragan externForfragan = buildBaseExternForfragan();
        utredning.setExternForfragan(externForfragan);
        InternForfragan internForfragan = buildInternForfragan(buildForfraganSvar(SvarTyp.AVBOJ), null);
        externForfragan.getInternForfraganList().add(internForfragan);

        InternForfraganStatus status = testee.resolveStatus(utredning, internForfragan);
        assertEquals(InternForfraganStatus.AVVISAD, status);
    }

    @Test
    public void testAvvisad() {
        Utredning utredning = buildBaseUtredning();
        ExternForfragan externForfragan = buildBaseExternForfragan();
        utredning.setExternForfragan(externForfragan);
        InternForfragan internForfragan = buildInternForfragan(buildForfraganSvar(SvarTyp.AVBOJ), null);
        externForfragan.getInternForfraganList().add(internForfragan);

        InternForfraganStatus status = testee.resolveStatus(utredning, internForfragan);
        assertEquals(InternForfraganStatus.AVVISAD, status);
    }

    @Test
    public void testAccepteradWithOtherInternForfragan() {
        Utredning utredning = buildBaseUtredning();
        ExternForfragan externForfragan = buildBaseExternForfragan();
        utredning.setExternForfragan(externForfragan);
        InternForfragan internForfragan1 = buildInternForfragan(buildForfraganSvar(SvarTyp.ACCEPTERA), null);
        InternForfragan internForfragan2 = buildInternForfragan(buildForfraganSvar(SvarTyp.ACCEPTERA), null);
        externForfragan.getInternForfraganList().add(internForfragan1);
        externForfragan.getInternForfraganList().add(internForfragan2);

        InternForfraganStatus status = testee.resolveStatus(utredning, internForfragan1);
        assertEquals(InternForfraganStatus.ACCEPTERAD_VANTAR_PA_TILLDELNINGSBESLUT, status);
    }

    @Test
    public void testDirektTilldeladToSelf() {
        Utredning utredning = buildBaseUtredning();
        ExternForfragan externForfragan = buildBaseExternForfragan();
        utredning.setExternForfragan(externForfragan);
        InternForfragan internForfragan1 = buildInternForfragan(buildForfraganSvar(SvarTyp.ACCEPTERA), null);
        InternForfragan internForfragan2 = buildInternForfragan(buildForfraganSvar(SvarTyp.ACCEPTERA), null);
        externForfragan.getInternForfraganList().add(internForfragan1);
        externForfragan.getInternForfraganList().add(internForfragan2);
        internForfragan1.setDirekttilldelad(true);

        InternForfraganStatus status = testee.resolveStatus(utredning, internForfragan1);
        assertEquals(InternForfraganStatus.DIREKTTILLDELAD, status);

        InternForfraganStatus statusOther = testee.resolveStatus(utredning, internForfragan2);
        assertEquals(InternForfraganStatus.ACCEPTERAD_VANTAR_PA_TILLDELNINGSBESLUT, statusOther);
    }

    @Test
    public void testTilldeladToSelf() {
        Utredning utredning = buildBaseUtredning();
        ExternForfragan externForfragan = buildBaseExternForfragan();
        utredning.setExternForfragan(externForfragan);
        InternForfragan internForfragan1 = buildInternForfragan(buildForfraganSvar(SvarTyp.ACCEPTERA), LocalDateTime.now());
        InternForfragan internForfragan2 = buildInternForfragan(buildForfraganSvar(SvarTyp.ACCEPTERA), null);
        externForfragan.getInternForfraganList().add(internForfragan1);
        externForfragan.getInternForfraganList().add(internForfragan2);
        internForfragan1.setDirekttilldelad(false);

        InternForfraganStatus status = testee.resolveStatus(utredning, internForfragan1);
        assertEquals(InternForfraganStatus.TILLDELAD_VANTAR_PA_BESTALLNING, status);

        InternForfraganStatus statusOther = testee.resolveStatus(utredning, internForfragan2);
        assertEquals(InternForfraganStatus.EJ_TILLDELAD, statusOther);
    }

    @Test
    public void testTilldeladToSelfButIngenBestallning() {
        Utredning utredning = buildBaseUtredning();
        utredning.setAvbrutenAnledning(AvslutOrsak.INGEN_BESTALLNING);
        ExternForfragan externForfragan = buildBaseExternForfragan();
        utredning.setExternForfragan(externForfragan);
        InternForfragan internForfragan1 = buildInternForfragan(buildForfraganSvar(SvarTyp.ACCEPTERA), LocalDateTime.now());
        InternForfragan internForfragan2 = buildInternForfragan(buildForfraganSvar(SvarTyp.ACCEPTERA), null);
        externForfragan.getInternForfraganList().add(internForfragan1);
        externForfragan.getInternForfraganList().add(internForfragan2);
        internForfragan1.setDirekttilldelad(false);

        InternForfraganStatus status = testee.resolveStatus(utredning, internForfragan1);
        assertEquals(InternForfraganStatus.INGEN_BESTALLNING, status);

        InternForfraganStatus statusOther = testee.resolveStatus(utredning, internForfragan2);
        assertEquals(InternForfraganStatus.EJ_TILLDELAD, statusOther);
    }

    @Test
    public void testBestalldToSelf() {
        Utredning utredning = buildBaseUtredning();
        utredning.setBestallning(buildBestallning(null));
        ExternForfragan externForfragan = buildBaseExternForfragan();
        utredning.setExternForfragan(externForfragan);
        InternForfragan internForfragan1 = buildInternForfragan(buildForfraganSvar(SvarTyp.ACCEPTERA), LocalDateTime.now());
        InternForfragan internForfragan2 = buildInternForfragan(buildForfraganSvar(SvarTyp.ACCEPTERA), null);
        externForfragan.getInternForfraganList().add(internForfragan1);
        externForfragan.getInternForfraganList().add(internForfragan2);
        internForfragan1.setDirekttilldelad(false);

        InternForfraganStatus status = testee.resolveStatus(utredning, internForfragan1);
        assertEquals(InternForfraganStatus.BESTALLD, status);

        InternForfraganStatus statusOther = testee.resolveStatus(utredning, internForfragan2);
        assertEquals(InternForfraganStatus.EJ_TILLDELAD, statusOther);
    }

    @Test
    public void testExternForfraganAvvisad() {
        Utredning utredning = buildBaseUtredning();
        ExternForfragan externForfragan = buildBaseExternForfragan();
        utredning.setExternForfragan(externForfragan);
        InternForfragan internForfragan = buildInternForfragan(null, null);
        externForfragan.getInternForfraganList().add(internForfragan);

        externForfragan.setAvvisatDatum(LocalDateTime.now());
        externForfragan.setAvvisatKommentar("Kommentar");

        InternForfraganStatus status = testee.resolveStatus(utredning, internForfragan);
        assertEquals(InternForfraganStatus.EJ_TILLDELAD, status);
    }

    @Test
    public void testInternForfraganAvbojExternForfraganAvvisad() {
        Utredning utredning = buildBaseUtredning();
        ExternForfragan externForfragan = buildBaseExternForfragan();
        utredning.setExternForfragan(externForfragan);
        InternForfragan internForfragan = buildInternForfragan(buildForfraganSvar(SvarTyp.AVBOJ), null);
        externForfragan.getInternForfraganList().add(internForfragan);

        externForfragan.setAvvisatDatum(LocalDateTime.now());
        externForfragan.setAvvisatKommentar("Kommentar");

        InternForfraganStatus status = testee.resolveStatus(utredning, internForfragan);
        assertEquals(InternForfraganStatus.AVVISAD, status);
    }
}
