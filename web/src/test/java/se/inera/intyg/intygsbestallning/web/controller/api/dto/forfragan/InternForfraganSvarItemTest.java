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
package se.inera.intyg.intygsbestallning.web.controller.api.dto.forfragan;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import se.inera.intyg.intygsbestallning.persistence.model.ForfraganSvar;
import se.inera.intyg.intygsbestallning.persistence.model.InternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.VardenhetPreference;
import se.inera.intyg.intygsbestallning.persistence.model.type.SvarTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.UtforareTyp;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.vardenhet.VardenhetPreferenceResponse;

import java.time.LocalDate;

/**
 * Created by marced on 2018-05-24.
 */
public class InternForfraganSvarItemTest {
    private static final String UTFORAR_NAMN = "utföraren";
    private static final String UTFORAR_ADRESS = "utföraradress";
    private static final String UTFORAR_POSTNR = "99999";
    private static final String UTFORAR_POSTORT = "utförarort";
    private static final String UTFORAR_TELEFON = "88888";
    private static final String UTFORAR_EPOST = "utfor@are.se";
    private static final String UTFORAR_KOMMENTAR = "Utför nu!";
    private static final Long INTERNFORFRAGAN_ID = 99L;
    private String borjaDatum = "2018-01-02";
    VardenhetPreferenceResponse vardenhetPreferenceResponse;
    InternForfragan internForfragan;
    ForfraganSvar internForfraganSvar;

    @Before
    public void setup() {
        VardenhetPreference vep = new VardenhetPreference();
        vep.setMottagarNamn("namn");
        vep.setAdress("adress");
        vep.setPostnummer("12345");
        vep.setPostort("orten");
        vep.setTelefonnummer("122-345");
        vep.setEpost("apa@bepa.se");
        vep.setVardenhetHsaId("HSAID");

        vardenhetPreferenceResponse = new VardenhetPreferenceResponse(vep);

        internForfraganSvar = ForfraganSvar.ForfraganSvarBuilder.aForfraganSvar()
                .withSvarTyp(SvarTyp.ACCEPTERA)
                .withUtforareNamn(UTFORAR_NAMN)
                .withUtforareAdress(UTFORAR_ADRESS)
                .withUtforarePostnr(UTFORAR_POSTNR)
                .withUtforarePostort(UTFORAR_POSTORT)
                .withUtforareTelefon(UTFORAR_TELEFON)
                .withUtforareEpost(UTFORAR_EPOST)
                .withBorjaDatum(LocalDate.parse(borjaDatum))
                .withKommentar(UTFORAR_KOMMENTAR).build();

        internForfragan = InternForfragan.InternForfraganBuilder.anInternForfragan().withId(INTERNFORFRAGAN_ID).build();
    }

    @Test
    public void fromInternForfraganWithExistingSvar() {
        internForfragan.setForfraganSvar(internForfraganSvar);

        InternForfraganSvarItem result = InternForfraganSvarItem.from(internForfragan);
        assertEquals(internForfragan.getId(), result.getForfraganId());
        assertEquals(internForfraganSvar.getSvarTyp(), result.getSvarTyp());
        assertEquals(borjaDatum, result.getBorjaDatum());
        assertEquals(internForfraganSvar.getUtforareTyp(), result.getUtforareTyp());
        assertEquals(internForfraganSvar.getUtforareNamn(), result.getUtforareNamn());
        assertEquals(internForfraganSvar.getUtforareAdress(), result.getUtforareAdress());
        assertEquals(internForfraganSvar.getUtforarePostnr(), result.getUtforarePostnr());
        assertEquals(internForfraganSvar.getUtforarePostort(), result.getUtforarePostort());
        assertEquals(internForfraganSvar.getUtforareTelefon(), result.getUtforareTelefon());
        assertEquals(internForfraganSvar.getKommentar(), result.getKommentar());
    }

    @Test
    public void fromInternForfraganWithoutExistingSvarIsBasedOnVardenhetPreference() {
        internForfragan.setForfraganSvar(null);

        InternForfraganSvarItem result = InternForfraganSvarItem.from(internForfragan, vardenhetPreferenceResponse);
        assertNull(result.getSvarTyp());
        assertNull(result.getBorjaDatum());
        assertEquals(UtforareTyp.ENHET, result.getUtforareTyp());
        assertEquals(vardenhetPreferenceResponse.getMottagarNamn(), result.getUtforareNamn());
        assertEquals(vardenhetPreferenceResponse.getAdress(), result.getUtforareAdress());
        assertEquals(vardenhetPreferenceResponse.getPostnummer(), result.getUtforarePostnr());
        assertEquals(vardenhetPreferenceResponse.getPostort(), result.getUtforarePostort());
        assertEquals(vardenhetPreferenceResponse.getTelefonnummer(), result.getUtforareTelefon());
        assertEquals(vardenhetPreferenceResponse.getStandardsvar(), result.getKommentar());
    }

}
