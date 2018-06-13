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
import org.hamcrest.Matchers;
import org.junit.Test;
import se.inera.intyg.intygsbestallning.web.BaseRestIntegrationTest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.forfragan.ListForfraganRequest;

import static com.jayway.restassured.RestAssured.given;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

public class InternForfraganControllerIT extends BaseRestIntegrationTest {

    private static final String INTERN_FORFRAGAN_API_ENDPOINT = "/api/vardadmin/internforfragningar";

    @Test
    public void testListForfragningar() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        RestAssured.sessionId = getAuthSession(DEFAULT_VARDADMIN);
        given().contentType(APPLICATION_JSON)
                .body(mapper.writeValueAsString(new ListForfraganRequest()))
                .expect().statusCode(OK).when().post(INTERN_FORFRAGAN_API_ENDPOINT)
                .then()
                .body("forfragningar", Matchers.notNullValue());
    }

    @Test
    public void testGetForfraganById() {
        RestAssured.sessionId = getAuthSession(DEFAULT_VARDADMIN);
        given().expect().statusCode(OK).when().get(INTERN_FORFRAGAN_API_ENDPOINT + "/1")
                .then()
                .body("utredning.status", Matchers.notNullValue());
    }

    @Test
    public void testGetForfraganByIdReturns404WhenNotExists() {
        RestAssured.sessionId = getAuthSession(DEFAULT_VARDADMIN);
        given().expect().statusCode(404).when().get(INTERN_FORFRAGAN_API_ENDPOINT + "/999191919");
    }
}
