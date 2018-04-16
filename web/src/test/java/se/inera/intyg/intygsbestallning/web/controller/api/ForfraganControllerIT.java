package se.inera.intyg.intygsbestallning.web.controller.api;

import com.jayway.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.Test;
import se.inera.intyg.intygsbestallning.web.BaseRestIntegrationTest;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertTrue;

public class ForfraganControllerIT extends BaseRestIntegrationTest {

    private static final String FORFRAGAN_API_ENDPOINT = "/api/forfragningar" ;

    @Test
    public void testGetAnvandare() {
        assertTrue(true);
        RestAssured.sessionId = getAuthSession(DEFAULT_VARDADMIN);
        given().expect().statusCode(OK).when().get(FORFRAGAN_API_ENDPOINT)
        .then().body("forfragningar", Matchers.notNullValue());
//        RestAssured.sessionId = getAuthSession(DEFAULT_VARDADMIN);
//        given().expect().statusCode(OK).when().get(USER_API_ENDPOINT).
//                then().
//                body(matchesJsonSchemaInClasspath("jsonschema/ib-user-response-schema.json")).
//                body("hsaId", equalTo(DEFAULT_VARDADMIN.getHsaId())).
//                body("valdVardenhet.id", equalTo(DEFAULT_VARDADMIN.getEnhetId())).
//                body("namn", equalTo(DEFAULT_VARDADMIN));
    }
}
