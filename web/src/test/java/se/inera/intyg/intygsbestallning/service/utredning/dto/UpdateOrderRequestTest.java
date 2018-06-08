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
package se.inera.intyg.intygsbestallning.service.utredning.dto;

import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static se.inera.intyg.intygsbestallning.testutil.TestDataGen.DATE_TIME;
import static se.inera.intyg.intygsbestallning.testutil.TestDataGen.createUpdateOrderType;

public class UpdateOrderRequestTest {

    @Test
    public void testConvertFranOrderUpdateType() {
        final UpdateOrderRequest request = UpdateOrderRequest.from(createUpdateOrderType());

        assertEquals(Long.valueOf(1L), request.getUtredningId());
        assertEquals("kommentar", request.getKommentar().get());

        assertTrue(request.getTolkBehov().get());
        assertEquals("sv", request.getTolkSprak().get());

        assertEquals(DATE_TIME, request.getLastDateIntyg().get());
        assertFalse(request.getHandling().get());

        final Bestallare bestallare = request.getBestallare().get();
        assertEquals("email", bestallare.getEmail());
        assertEquals("fullName", bestallare.getFullstandigtNamn());
        assertEquals("officeName", bestallare.getKontor());
        assertEquals("officeCostCenter", bestallare.getKostnadsstalle());
        assertEquals("authority", bestallare.getMyndighet());
        assertEquals("postalAddress", bestallare.getAdress());
        assertEquals("postalCode", bestallare.getPostnummer());
        assertEquals("postalCity", bestallare.getStad());
        assertEquals("phoneNumber", bestallare.getTelefonnummer());
    }

    @Test
    public void testConvertFranOrderUpdateTypeMedTolkBehovUtanTolkSprak() {
        final UpdateOrderRequest request = UpdateOrderRequest.from(createUpdateOrderType(true, null, false));

        assertEquals(Long.valueOf(1L), request.getUtredningId());
        assertEquals("kommentar", request.getKommentar().get());

        assertTrue(request.getTolkBehov().get());
        assertEquals(Optional.empty(), request.getTolkSprak());

        assertEquals(DATE_TIME, request.getLastDateIntyg().get());
        assertFalse(request.getHandling().get());

        final Bestallare bestallare = request.getBestallare().get();
        assertEquals("email", bestallare.getEmail());
        assertEquals("fullName", bestallare.getFullstandigtNamn());
        assertEquals("officeName", bestallare.getKontor());
        assertEquals("officeCostCenter", bestallare.getKostnadsstalle());
        assertEquals("authority", bestallare.getMyndighet());
        assertEquals("postalAddress", bestallare.getAdress());
        assertEquals("postalCode", bestallare.getPostnummer());
        assertEquals("postalCity", bestallare.getStad());
        assertEquals("phoneNumber", bestallare.getTelefonnummer());
    }

    @Test
    public void testConvertFranOrderUpdateTypeUtanTolkBehovUtanTolkSprak() {
        final UpdateOrderRequest request = UpdateOrderRequest.from(createUpdateOrderType(false, null, false));

        assertEquals(Long.valueOf(1L), request.getUtredningId());
        assertEquals("kommentar", request.getKommentar().get());

        assertFalse(request.getTolkBehov().get());
        assertEquals(Optional.empty(), request.getTolkSprak());

        assertEquals(DATE_TIME, request.getLastDateIntyg().get());
        assertFalse(request.getHandling().get());

        final Bestallare bestallare = request.getBestallare().get();
        assertEquals("email", bestallare.getEmail());
        assertEquals("fullName", bestallare.getFullstandigtNamn());
        assertEquals("officeName", bestallare.getKontor());
        assertEquals("officeCostCenter", bestallare.getKostnadsstalle());
        assertEquals("authority", bestallare.getMyndighet());
        assertEquals("postalAddress", bestallare.getAdress());
        assertEquals("postalCode", bestallare.getPostnummer());
        assertEquals("postalCity", bestallare.getStad());
        assertEquals("phoneNumber", bestallare.getTelefonnummer());
    }
}
