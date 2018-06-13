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
import se.inera.intyg.intygsbestallning.persistence.model.status.UtredningStatus;
import se.inera.intyg.intygsbestallning.web.BaseRestIntegrationTest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;
import static se.inera.intyg.intygsbestallning.persistence.model.status.InternForfraganStatus.BESTALLD;

public class RequestOrderMedicalAssessmentIT extends BaseRestIntegrationTest {

    private static final String BASE = "Envelope.Body.OrderMedicalAssessmentResponse.";

    private ST requestTemplate;

    private STGroup templateGroup;

    @Before
    public void setupTestSpecific() {
        RestAssured.requestSpecification = new RequestSpecBuilder().setContentType("application/xml;charset=utf-8").build();

        templateGroup = new STGroupFile("integrationtests/RequestOrderMedicalAssessment/request1.stg");
        requestTemplate = templateGroup.getInstanceOf("request");
    }

    @Test
    public void requestOrderMedicalAssessmentWorks() {
        // First, utilize testability API to inject an Utredning ready to be supplemented.
        String json = loadJson("integrationtests/RequestOrderMedicalAssessment/utredning-tilldelad.json");

        ResponseBodyExtractionOptions body = given().body(json).when().contentType("application/json")
                .post("/api/test/utredningar").then()
                .statusCode(200).extract().body();

        Integer utredningId = body.jsonPath().get("entity.utredningId");

        requestTemplate.add("data",
                new RequestOrderMedicalAssessmentIT.RequestOrderMedicalAssessment("" + utredningId,
                        "IFV1239877878-1042",
                        "AFU", true, "sv", "Detta är en kommentar",
                        LocalDate.now().plusDays(25).format(DateTimeFormatter.ISO_DATE), false,
                        "Bli frisk", "Bowla", LocalDate.now().format(DateTimeFormatter.ISO_DATE),
                        "Hanna Handläggare", "123-123123", "handlaggare@ineratestar.se",
                        "19121212-1212", "Tolvan", "Tolvansson", "Rullator",
                        "Kommer från Tolvmåla"));


        given().body(requestTemplate.render()).when().post("/services/order-medical-assessment-responder").then()
                .statusCode(200).rootPath(BASE)
                .body("result.resultCode", is("OK"))
                .body("assessmentId.extension", Matchers.notNullValue());

        // Verify stuff by reading the Bestallning from the REST API.
        RestAssured.sessionId = getAuthSession(DEFAULT_VARDADMIN);

        // Utredningen har status Beställningen mottagen, väntar på handlingar
        given().when().get("/api/vardadmin/bestallningar/" + utredningId).then()
                .statusCode(200)
                .body("utredningsId", is(utredningId))
                .body("status.id", is(UtredningStatus.BESTALLNING_MOTTAGEN_VANTAR_PA_HANDLINGAR.getId()))
                .body("invanare.personId", is("19121212-1212"))
                .body("tolkSprak", is("sv"))
                .body("handlaggareNamn", is("Hanna Handläggare"))
                .body("handlaggareTelefonnummer", is("123-123123"))
                .body("handlaggareEpost", is("handlaggare@ineratestar.se"));

        // Förfrågan till vårdenheten har status Beställd
        given().when().get("/api/vardadmin/internforfragningar/" + utredningId).then()
                .statusCode(200)
                .body("internForfragan.status.id", is(BESTALLD.getId()));

        // Delete it.
        deleteUtredning(utredningId);
    }

    private static class RequestOrderMedicalAssessment {
        public final String assessmentId;
        public final String vardenhetHsaId;
        public final String utredningTyp;
        public final boolean behovTolk;
        public final String tolkSprak;
        public final String kommentar;
        public final String sistaDatum;
        public final boolean dokumentViaPost;
        public final String syfte;
        public final String planeradeAtgarder;
        public final String orderDatum;
        public final String handlaggareNamn;
        public final String handlaggareTelefon;
        public final String handlaggareEpost;
        public final String patientPersonId;
        public final String patientFornamn;
        public final String patientEfternamn;
        public final String patientBehov;
        public final String patientBakgrund;

        public RequestOrderMedicalAssessment(String assessmentId, String vardenhetHsaId, String utredningTyp, boolean behovTolk,
                String tolkSprak, String kommentar, String sistaDatum, boolean dokumentViaPost, String syfte, String planeradeAtgarder,
                String orderDatum, String handlaggareNamn, String handlaggareTelefon, String handlaggareEpost, String patientPersonId,
                String patientFornamn, String patientEfternamn, String patientBehov, String patientBakgrund) {
            this.assessmentId = assessmentId;
            this.vardenhetHsaId = vardenhetHsaId;
            this.utredningTyp = utredningTyp;
            this.behovTolk = behovTolk;
            this.tolkSprak = tolkSprak;
            this.kommentar = kommentar;
            this.sistaDatum = sistaDatum;
            this.dokumentViaPost = dokumentViaPost;
            this.syfte = syfte;
            this.planeradeAtgarder = planeradeAtgarder;
            this.orderDatum = orderDatum;
            this.handlaggareNamn = handlaggareNamn;
            this.handlaggareTelefon = handlaggareTelefon;
            this.handlaggareEpost = handlaggareEpost;
            this.patientPersonId = patientPersonId;
            this.patientFornamn = patientFornamn;
            this.patientEfternamn = patientEfternamn;
            this.patientBehov = patientBehov;
            this.patientBakgrund = patientBakgrund;
        }
    }
}
