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

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.builder.RequestSpecBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import se.inera.intyg.intygsbestallning.web.BaseRestIntegrationTest;

public class RequestPerformerForAssessmentIT extends BaseRestIntegrationTest {

    private static final String BASE = "Envelope.Body.RequestPerformerForAssessmentResponse.";

    private ST requestTemplate;

    private STGroup templateGroup;

    @Before
    public void setupTestSpecific() {
        RestAssured.requestSpecification = new RequestSpecBuilder().setContentType("application/xml;charset=utf-8").build();

        templateGroup = new STGroupFile("integrationtests/RequestPerformerAssessment/request1.stg");
        requestTemplate = templateGroup.getInstanceOf("request");
    }

    @Test
    public void requestPerformerForAssessmentWorks() {
        requestTemplate.add("data", new RequestPerformerAssessment("20180512",
                "IFV1239877878-1043",
                "Kommentar",
                "true", "en", "Engelska", "Hasse Handläggarsson",
                "123-123456", "handlaggaren@inera.se"));

        given().body(requestTemplate.render()).when().post("/services/request-performer-for-assessment-responder").then()
                .statusCode(200).rootPath(BASE)
                .body("result.resultCode", is("OK"));
    }

    @After
    public void cleanup() {
        // Radera?
    }

    @SuppressWarnings("unused")
    private static class RequestPerformerAssessment {
        public final String lastResponseDate;
        public final String vardgivareHsaId;
        public final String comment;
        public final String behovTolk;
        public final String tolkSprakCode;
        public final String tolkSprakName;
        public final String handlaggareNamn;
        public final String handlaggareTelefonnummer;
        public final String handlaggareEpost;

        public RequestPerformerAssessment(String lastResponseDate, String vardgivareHsaId, String comment, String behovTolk,
                                          String tolkSprakCode, String tolkSprakName, String handlaggareNamn, String handlaggareTelefonnummer,
                                          String handlaggareEpost) {
            this.lastResponseDate = lastResponseDate;
            this.vardgivareHsaId = vardgivareHsaId;
            this.comment = comment;
            this.behovTolk = behovTolk;
            this.tolkSprakCode = tolkSprakCode;
            this.tolkSprakName = tolkSprakName;
            this.handlaggareNamn = handlaggareNamn;
            this.handlaggareTelefonnummer = handlaggareTelefonnummer;
            this.handlaggareEpost = handlaggareEpost;
        }
    }

}
