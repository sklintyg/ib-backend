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
package se.inera.intyg.intygsbestallning.integration;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.response.ResponseBodyExtractionOptions;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import se.inera.intyg.intygsbestallning.web.BaseRestIntegrationTest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;

public class RequestSupplementIT extends BaseRestIntegrationTest {

    private static final String BASE = "Envelope.Body.RequestSupplementResponse.";

    private ST requestTemplate;

    private STGroup templateGroup;

    @Before
    public void setupTestSpecific() {
        RestAssured.requestSpecification = new RequestSpecBuilder().setContentType("application/xml;charset=utf-8").build();

        templateGroup = new STGroupFile("integrationtests/RequestSupplement/request1.stg");
        requestTemplate = templateGroup.getInstanceOf("request");
    }

    @Test
    public void requestRequestSupplementWorks() {
        // First, utilize testability API to inject an Utredning ready to be supplemented.
        String json = loadJson("integrationtests/RequestSupplement/utredning.json");

        ResponseBodyExtractionOptions body = given().body(json).when().contentType("application/json")
                .post("/api/test/utredningar").then()
                .statusCode(200).extract().body();


        Integer utredningId = body.jsonPath().get("entity.utredningId");

        requestTemplate.add("data",
                new RequestSupplement("" + utredningId, LocalDate.now().plusDays(7).format(DateTimeFormatter.ofPattern("yyyyMMdd"))));

        given().body(requestTemplate.render()).when().post("/services/request-supplement-responder").then()
                .statusCode(200).rootPath(BASE)
                .body("result.resultCode", is("OK"))
                .body("assessmentId.extension", Matchers.notNullValue());

        // Delete it.
        deleteUtredning(utredningId);
    }

    @Test
    public void requestRequestSupplementError() {
        // First, utilize testability API to inject an Utredning ready to be supplemented.
        String json = loadJson("integrationtests/RequestSupplement/utredning.json");

        ResponseBodyExtractionOptions body = given().body(json).when().contentType("application/json")
                .post("/api/test/utredningar").then()
                .statusCode(200).extract().body();


        Integer utredningId = body.jsonPath().get("entity.utredningId");

        requestTemplate.add("data",
                new RequestSupplement("" + utredningId, null));

        given().body(requestTemplate.render()).when().post("/services/request-supplement-responder").then()
                .statusCode(200).rootPath(BASE)
                .body("result.resultCode", is("ERROR"))
                .body("assessmentId.extension", Matchers.notNullValue());

        // Delete it.
        deleteUtredning(utredningId);
    }

    @SuppressWarnings("unused")
    private static class RequestSupplement {
        public final String assessmentId;
        public final String sistaDatum;

        public RequestSupplement(String assessmentId, String sistaDatum) {
            this.assessmentId = assessmentId;
            this.sistaDatum = sistaDatum;
        }
    }

}
