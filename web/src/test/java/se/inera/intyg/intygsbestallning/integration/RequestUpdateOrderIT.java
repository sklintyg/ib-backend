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
import se.inera.intyg.intygsbestallning.common.util.SchemaDateUtil;
import se.inera.intyg.intygsbestallning.web.BaseRestIntegrationTest;

import java.time.LocalDate;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;

public class RequestUpdateOrderIT extends BaseRestIntegrationTest {
    private static final String BASE = "Envelope.Body.UpdateOrderResponse.";

    private ST requestTemplate;

    private STGroup templateGroup;

    @Before
    public void setupTestSpecific() {
        RestAssured.requestSpecification = new RequestSpecBuilder().setContentType("application/xml;charset=utf-8").build();

        templateGroup = new STGroupFile("integrationtests/RequestUpdateOrder/request1.stg");
        requestTemplate = templateGroup.getInstanceOf("request");
    }

    @Test
    public void requestUpdateOrderWorks() {

        String json = loadJson("integrationtests/RequestUpdateOrder/utredning-bestalld.json");

        ResponseBodyExtractionOptions body = given().body(json).when().contentType("application/json")
                .post("/api/test/utredningar").then()
                .statusCode(200).extract().body();


        Integer utredningId = body.jsonPath().get("entity.utredningId");
        requestTemplate.add("data",
                new RequestUpdateOrder("" + utredningId, "Detta är en kommentar",
                        SchemaDateUtil.toDateStringFromLocalDate(LocalDate.now().plusDays(25)),
                        true,
                        "sv",
                        true,
                        "Harald Hundläggare",
                        "321-321321312",
                        "harald@ineratestar.se",
                        "Haraldsborgen",
                        "1234",
                        "Haraldsgatan 1337",
                        "13370",
                        "Haraldsmåla"));

        given().body(requestTemplate.render()).when().post("/services/update-order-responder").then()
                .statusCode(200).rootPath(BASE)
                .body("result.resultCode", is("OK"))
                .body("assessmentId.extension", Matchers.notNullValue());

        deleteUtredning(utredningId);
    }

    private final static class RequestUpdateOrder {
        public final String assessmentId;
        public final String kommentar;
        public final String sistaDatum;
        public final Boolean behovTolk;
        public final String tolkSprak;
        public final Boolean documentsByPost;
        public final String handlaggareNamn;
        public final String handlaggareTelefon;
        public final String handlaggareEpost;
        public final String kontorNamn;
        public final String kontorKostnadsstalle;
        public final String kontorAdress;
        public final String kontorPostnummer;
        public final String kontorStad;

        public RequestUpdateOrder(String assessmentId, String kommentar, String sistaDatum, Boolean behovTolk, String tolkSprak, Boolean documentsByPost, String handlaggareNamn, String handlaggareTelefon, String handlaggareEpost, String kontorNamn, String kontorKostnadsstalle, String kontorAdress, String kontorPostnummer, String kontorStad) {
            this.assessmentId = assessmentId;
            this.kommentar = kommentar;
            this.sistaDatum = sistaDatum;
            this.behovTolk = behovTolk;
            this.tolkSprak = tolkSprak;
            this.documentsByPost = documentsByPost;
            this.handlaggareNamn = handlaggareNamn;
            this.handlaggareTelefon = handlaggareTelefon;
            this.handlaggareEpost = handlaggareEpost;
            this.kontorNamn = kontorNamn;
            this.kontorKostnadsstalle = kontorKostnadsstalle;
            this.kontorAdress = kontorAdress;
            this.kontorPostnummer = kontorPostnummer;
            this.kontorStad = kontorStad;
        }
    }
}
