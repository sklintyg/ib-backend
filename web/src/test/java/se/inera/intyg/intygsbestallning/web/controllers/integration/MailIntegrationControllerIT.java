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
package se.inera.intyg.intygsbestallning.web.controllers.integration;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static se.inera.intyg.intygsbestallning.web.controller.integration.MailIntegrationController.ERROR_FORFRAGAN_NO_HSA_AUTH;
import static se.inera.intyg.intygsbestallning.web.controller.integration.MailIntegrationController.ERROR_LINK_ENITY_NOT_FOUND;
import static se.inera.intyg.intygsbestallning.web.controller.integration.MailIntegrationController.ERROR_UTREDNING_NO_HSA_AUTH;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.springframework.http.HttpHeaders;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.ResponseBodyExtractionOptions;

import se.inera.intyg.intygsbestallning.web.BaseRestIntegrationTest;

/**
 * Created by marced on 2018-08-15.
 */
public class MailIntegrationControllerIT extends BaseRestIntegrationTest {

    @Test
    public void testRedirectInternForfragan() throws IOException {

        RestAssured.sessionId = getAuthSession(DEFAULT_VARDADMIN);

        // Set up Utredning that has all required properties.
        String json = loadJson("integrationtests/MailIntegrationController/utredning.json");

        ResponseBodyExtractionOptions body = given().body(json).when().contentType("application/json")
                .post("/api/test/utredningar").then()
                .statusCode(200).extract().body();
        Integer utredningId = body.jsonPath().get("entity.utredningId");
        Integer internforfraganId = body.jsonPath().get("entity.externForfragan.internForfraganList[0].id");

        // Perform test
        given().redirects().follow(false).and()
                .pathParam("utredningId", utredningId)
                .pathParam("internforfraganId", internforfraganId)
                .expect()
                .statusCode(HttpServletResponse.SC_FOUND).when().get("maillink/internforfragan/{utredningId}/{internforfraganId}")
                .then().header(HttpHeaders.LOCATION, containsString("visaInternForfragan/" + utredningId));

        // Clean up
        deleteUtredning(utredningId);
    }

    @Test
    public void testVardadminRedirectInternForfraganFailsNoHsaAuth() throws IOException {

        RestAssured.sessionId = getAuthSession(OTHER_SAMORDNARE);

        // Set up Utredning that has all required properties.
        String json = loadJson("integrationtests/MailIntegrationController/utredning.json");

        ResponseBodyExtractionOptions body = given().body(json).when().contentType("application/json")
                .post("/api/test/utredningar").then()
                .statusCode(200).extract().body();
        Integer utredningId = body.jsonPath().get("entity.utredningId");
        Integer internforfraganId = body.jsonPath().get("entity.externForfragan.internForfraganList[0].id");

        // Perform test
        given().redirects().follow(false).and()
                .pathParam("utredningId", utredningId)
                .pathParam("internforfraganId", internforfraganId)
                .expect()
                .statusCode(HttpServletResponse.SC_FOUND).when().get("maillink/internforfragan/{utredningId}/{internforfraganId}")
                .then().header(HttpHeaders.LOCATION, containsString(ERROR_FORFRAGAN_NO_HSA_AUTH));
        // Clean up
        deleteUtredning(utredningId);
    }

    @Test
    public void testVardadminRedirectInternForfraganFailsNotFound() throws IOException {
        RestAssured.sessionId = getAuthSession(DEFAULT_VARDADMIN);

        // Perform test
        given().redirects().follow(false).and()
                .pathParam("utredningId", "999999")
                .pathParam("internforfraganId", "9999999")
                .expect()
                .statusCode(HttpServletResponse.SC_FOUND).when().get("maillink/internforfragan/{utredningId}/{internforfraganId}")
                .then().header(HttpHeaders.LOCATION, containsString(ERROR_LINK_ENITY_NOT_FOUND));

    }

    @Test
    public void testVardadminRedirectBestallning() throws IOException {

        RestAssured.sessionId = getAuthSession(DEFAULT_VARDADMIN);

        // Set up Utredning that has all required properties.
        String json = loadJson("integrationtests/MailIntegrationController/utredning.json");

        ResponseBodyExtractionOptions body = given().body(json).when().contentType("application/json")
                .post("/api/test/utredningar").then()
                .statusCode(200).extract().body();
        Integer utredningId = body.jsonPath().get("entity.utredningId");

        // Perform test
        given().redirects().follow(false).and().pathParam("id", utredningId).expect()
                .statusCode(HttpServletResponse.SC_FOUND).when().get("maillink/bestallning/{id}")
                .then().header(HttpHeaders.LOCATION, containsString("visaBestallning/" + utredningId));

        // Clean up
        deleteUtredning(utredningId);
    }

    @Test
    public void testVardadminRedirectBestallningFailsNoHsaAuth() throws IOException {

        RestAssured.sessionId = getAuthSession(OTHER_SAMORDNARE);

        // Set up Utredning that has all required properties.
        String json = loadJson("integrationtests/MailIntegrationController/utredning.json");

        ResponseBodyExtractionOptions body = given().body(json).when().contentType("application/json")
                .post("/api/test/utredningar").then()
                .statusCode(200).extract().body();
        Integer utredningId = body.jsonPath().get("entity.utredningId");

        // Perform test
        given().redirects().follow(false).and().pathParam("id", utredningId).expect()
                .statusCode(HttpServletResponse.SC_FOUND).when().get("maillink/bestallning/{id}")
                .then().header(HttpHeaders.LOCATION, containsString(ERROR_UTREDNING_NO_HSA_AUTH));

        // Clean up
        deleteUtredning(utredningId);
    }

    @Test
    public void testVardadminRedirectBestallningFailsNotFound() throws IOException {
        RestAssured.sessionId = getAuthSession(DEFAULT_VARDADMIN);

        // Perform test
        given().redirects().follow(false).and().pathParam("id", "99999991").expect()
                .statusCode(HttpServletResponse.SC_FOUND).when().get("maillink/bestallning/{id}")
                .then().header(HttpHeaders.LOCATION, containsString(ERROR_LINK_ENITY_NOT_FOUND));

    }

    @Test
    public void testSamordnareRedirectExternforfragan() throws IOException {

        RestAssured.sessionId = getAuthSession(DEFAULT_SAMORDNARE);

        // Set up Utredning that has all required properties.
        String json = loadJson("integrationtests/MailIntegrationController/utredning.json");

        ResponseBodyExtractionOptions body = given().body(json).when().contentType("application/json")
                .post("/api/test/utredningar").then()
                .statusCode(200).extract().body();
        Integer utredningId = body.jsonPath().get("entity.utredningId");

        // Perform test
        given().redirects().follow(false).and().pathParam("id", utredningId).expect()
                .statusCode(HttpServletResponse.SC_FOUND).when().get("maillink/externforfragan/{id}")
                .then().header(HttpHeaders.LOCATION, containsString("visaUtredning/" + utredningId));

        // Clean up
        deleteUtredning(utredningId);
    }

    @Test
    public void testSamordnareRedirectExternforfraganFailsNoHsaAuth() throws IOException {

        RestAssured.sessionId = getAuthSession(OTHER_SAMORDNARE);

        // Set up Utredning that has all required properties.
        String json = loadJson("integrationtests/MailIntegrationController/utredning.json");

        ResponseBodyExtractionOptions body = given().body(json).when().contentType("application/json")
                .post("/api/test/utredningar").then()
                .statusCode(200).extract().body();
        Integer utredningId = body.jsonPath().get("entity.utredningId");

        // Perform test
        given().redirects().follow(false).and().pathParam("id", utredningId).expect()
                .statusCode(HttpServletResponse.SC_FOUND).when().get("maillink/externforfragan/{id}")
                .then().header(HttpHeaders.LOCATION, containsString(ERROR_UTREDNING_NO_HSA_AUTH));

        // Clean up
        deleteUtredning(utredningId);
    }

    @Test
    public void testSamordnareRedirectExternforfraganFailsNotFound() throws IOException {
        RestAssured.sessionId = getAuthSession(OTHER_SAMORDNARE);

        // Perform test
        given().redirects().follow(false).and().pathParam("id", "99999991").expect()
                .statusCode(HttpServletResponse.SC_FOUND).when().get("maillink/externforfragan/{id}")
                .then().header(HttpHeaders.LOCATION, containsString(ERROR_LINK_ENITY_NOT_FOUND));

    }

    @Test
    public void testSamordnareRedirectUtredning() throws IOException {

        RestAssured.sessionId = getAuthSession(DEFAULT_SAMORDNARE);

        // Set up Utredning that has all required properties.
        String json = loadJson("integrationtests/MailIntegrationController/utredning.json");

        ResponseBodyExtractionOptions body = given().body(json).when().contentType("application/json")
                .post("/api/test/utredningar").then()
                .statusCode(200).extract().body();
        Integer utredningId = body.jsonPath().get("entity.utredningId");

        // Perform test
        given().redirects().follow(false).and().pathParam("id", utredningId).expect()
                .statusCode(HttpServletResponse.SC_FOUND).when().get("maillink/utredning/{id}")
                .then().header(HttpHeaders.LOCATION, containsString("visaUtredning/" + utredningId));

        // Clean up
        deleteUtredning(utredningId);
    }

    @Test
    public void testSamordnareRedirectUtredningFailsNoHsaAuth() throws IOException {

        RestAssured.sessionId = getAuthSession(OTHER_SAMORDNARE);

        // Set up Utredning that has all required properties.
        String json = loadJson("integrationtests/MailIntegrationController/utredning.json");

        ResponseBodyExtractionOptions body = given().body(json).when().contentType("application/json")
                .post("/api/test/utredningar").then()
                .statusCode(200).extract().body();
        Integer utredningId = body.jsonPath().get("entity.utredningId");

        // Perform test
        given().redirects().follow(false).and().pathParam("id", utredningId).expect()
                .statusCode(HttpServletResponse.SC_FOUND).when().get("maillink/utredning/{id}")
                .then().header(HttpHeaders.LOCATION, containsString(ERROR_UTREDNING_NO_HSA_AUTH));

        // Clean up
        deleteUtredning(utredningId);
    }

    @Test
    public void testSamordnareRedirectUtredningFailsNotFound() throws IOException {
        RestAssured.sessionId = getAuthSession(OTHER_SAMORDNARE);

        // Perform test
        given().redirects().follow(false).and().pathParam("id", "99999991").expect()
                .statusCode(HttpServletResponse.SC_FOUND).when().get("maillink/utredning/{id}")
                .then().header(HttpHeaders.LOCATION, containsString(ERROR_LINK_ENITY_NOT_FOUND));

    }

}
