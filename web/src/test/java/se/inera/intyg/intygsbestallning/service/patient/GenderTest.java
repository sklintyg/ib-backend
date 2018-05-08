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
package se.inera.intyg.intygsbestallning.service.patient;

import org.junit.Test;
import se.inera.intyg.schemas.contract.Personnummer;

import static org.junit.Assert.assertEquals;

public class GenderTest {

    @Test
    public void testGetMale() {
        assertEquals(Gender.M, Gender.getGenderFromString("1"));
        assertEquals(Gender.M, Gender.getGenderFromString("3"));
        assertEquals(Gender.M, Gender.getGenderFromString("5"));
        assertEquals(Gender.M, Gender.getGenderFromString("7"));
        assertEquals(Gender.M, Gender.getGenderFromString("9"));
    }

    @Test
    public void testGetFemale() {
        assertEquals(Gender.F, Gender.getGenderFromString("0"));
        assertEquals(Gender.F, Gender.getGenderFromString("2"));
        assertEquals(Gender.F, Gender.getGenderFromString("4"));
        assertEquals(Gender.F, Gender.getGenderFromString("6"));
        assertEquals(Gender.F, Gender.getGenderFromString("8"));
    }

    @Test
    public void testGetUnknown() {
        assertEquals(Gender.UNKNOWN, Gender.getGenderFromString("A"));
        assertEquals(Gender.UNKNOWN, Gender.getGenderFromString(""));
        assertEquals(Gender.UNKNOWN, Gender.getGenderFromString(null));
        assertEquals(Gender.UNKNOWN, Gender.getGenderFromString("+"));
    }

    @Test
    public void testPersonnummer() {
        Personnummer pnr = Personnummer.createPersonnummer("19121212-1212").get();
        assertEquals(Gender.getGenderFromPersonnummer(pnr), Gender.M);
    }

    @Test
    public void testPersonnummerF() {
        Personnummer pnr = Personnummer.createPersonnummer("197705232382").get();
        assertEquals(Gender.getGenderFromPersonnummer(pnr), Gender.F);
    }

    @Test
    public void testPersonnummerShortId() {
        Personnummer pnr = Personnummer.createPersonnummer("1212121212").get();
        assertEquals(Gender.getGenderFromPersonnummer(pnr), Gender.M);
    }

    @Test
    public void testSamordningnummer() {
        Personnummer pnr = Personnummer.createPersonnummer("19701063-2391").get();
        assertEquals(Gender.getGenderFromPersonnummer(pnr), Gender.M);
    }

    @Test
    public void testShortSamordningnummer() {
        Personnummer pnr = Personnummer.createPersonnummer("701063-2391").get();
        assertEquals(Gender.getGenderFromPersonnummer(pnr), Gender.M);
    }
}
