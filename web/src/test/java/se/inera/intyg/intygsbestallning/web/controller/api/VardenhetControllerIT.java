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
package se.inera.intyg.intygsbestallning.web.controller.api;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.Test;
import se.inera.intyg.intygsbestallning.web.BaseRestIntegrationTest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.vardenhet.VardenhetPreferenceRequest;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

/**
 * Created by marced on 2018-04-24.
 */
public class VardenhetControllerIT extends BaseRestIntegrationTest {
    private static final String VARDENHET_API_ENDPOINT = "/api/vardadmin/vardenhet";
    private static final String MOTTAGAR_NAMN = "Nytt namn";
    private static final String ADRESS = "Vårdgatan 44";
    private static final String POSTNUMMER = "12345";
    private static final String POSTORT = "Vårdberga";
    private static final String TELEFONNUMMER = "000-12345";
    private static final String EPOST = "ve@vg.se";
    private static final String UTFORARE_TYP_ENHET = "ENHET";


    @Test
    public void testGetVardenhetPreference() {
        RestAssured.sessionId = getAuthSession(DEFAULT_VARDADMIN);
        given().expect().statusCode(OK).when().get(VARDENHET_API_ENDPOINT + "/preference/ENHET").then()
                .body(matchesJsonSchemaInClasspath("jsonschema/ib-vardenhet-preference-response-schema.json"));
    }
    @Test
    public void testGetVardenhetUnderleverantorPreference() {
        RestAssured.sessionId = getAuthSession(DEFAULT_VARDADMIN);
        given().expect().statusCode(OK).when().get(VARDENHET_API_ENDPOINT + "/preference/UNDERLEVERANTOR").then()
                .body(matchesJsonSchemaInClasspath("jsonschema/ib-vardenhet-preference-response-schema.json"));
    }

    @Test
    public void testSetVardenhetPreference() {
        RestAssured.sessionId = getAuthSession(DEFAULT_VARDADMIN);
        VardenhetPreferenceRequest request = new VardenhetPreferenceRequest();
        request.setMottagarNamn(MOTTAGAR_NAMN);
        request.setUtforareTyp(UTFORARE_TYP_ENHET);
        request.setAdress(ADRESS);
        request.setPostnummer(POSTNUMMER);
        request.setPostort(POSTORT);
        request.setTelefonnummer(TELEFONNUMMER);
        request.setEpost(EPOST);

        given().contentType(ContentType.JSON).body(request).expect().statusCode(OK).when().put(VARDENHET_API_ENDPOINT + "/preference")
                .then()
                .body(matchesJsonSchemaInClasspath("jsonschema/ib-vardenhet-preference-response-schema.json"))
                .body("mottagarNamn", Matchers.is(request.getMottagarNamn()))
                .body("adress", Matchers.is(request.getAdress()))
                .body("postnummer", Matchers.is(request.getPostnummer()))
                .body("postort", Matchers.is(request.getPostort()))
                .body("telefonnummer", Matchers.is(request.getTelefonnummer()))
                .body("epost", Matchers.is(request.getEpost()));

        // reset all properties and save again
        VardenhetPreferenceRequest emptyRequest = new VardenhetPreferenceRequest();
        emptyRequest.setUtforareTyp(UTFORARE_TYP_ENHET);
        given().contentType(ContentType.JSON).body(emptyRequest).expect().statusCode(OK).when()
                .put(VARDENHET_API_ENDPOINT + "/preference")
                .then()
                .body(matchesJsonSchemaInClasspath("jsonschema/ib-vardenhet-preference-response-schema.json"))
                .body("mottagarNamn", Matchers.isEmptyOrNullString())
                .body("adress", Matchers.isEmptyOrNullString())
                .body("postnummer", Matchers.isEmptyOrNullString())
                .body("postort", Matchers.isEmptyOrNullString())
                .body("telefonnummer", Matchers.isEmptyOrNullString())
                .body("epost", Matchers.isEmptyOrNullString());
    }

}
