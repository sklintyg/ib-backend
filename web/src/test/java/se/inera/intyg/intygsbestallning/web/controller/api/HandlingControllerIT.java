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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.ResponseBodyExtractionOptions;
import org.junit.Test;
import se.inera.intyg.intygsbestallning.web.BaseRestIntegrationTest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.handling.RegisterHandlingRequest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.jayway.restassured.RestAssured.given;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

public class HandlingControllerIT extends BaseRestIntegrationTest {

    private static final String HANDLINGAR_API_ENDPOINT = "/api/vardadmin/handlingar";

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testRegisterHandling() throws JsonProcessingException {

        RestAssured.sessionId = getAuthSession(DEFAULT_VARDADMIN);

        // Set up Utredning.
        String json = loadJson("integrationtests/RequestSupplement/utredning.json");

        ResponseBodyExtractionOptions body = given().body(json).when().contentType("application/json")
                .post("/api/test/utredningar").then()
                .statusCode(200).extract().body();
        Integer utredningId = body.jsonPath().get("entity.utredningId");

        // Perform test
        given().contentType(APPLICATION_JSON)
                .body(mapper.writeValueAsString(new RegisterHandlingRequest(LocalDate.now().format(DateTimeFormatter.ISO_DATE))))
                .expect().statusCode(OK).when().put(HANDLINGAR_API_ENDPOINT + "/" + utredningId);

        // Clean up
        deleteUtredning(utredningId);
    }


    @Test
    public void testRegisterHandlingReturns404WhenUtredningNotExists() throws JsonProcessingException {
        RestAssured.sessionId = getAuthSession(DEFAULT_VARDADMIN);



        given().contentType(APPLICATION_JSON)
                .body(mapper.writeValueAsString(new RegisterHandlingRequest(LocalDate.now().format(DateTimeFormatter.ISO_DATE))))
                .expect().statusCode(404).when().put(HANDLINGAR_API_ENDPOINT + "/902101337");
    }
}
