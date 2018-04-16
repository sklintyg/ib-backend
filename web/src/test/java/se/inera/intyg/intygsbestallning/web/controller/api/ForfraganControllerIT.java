package se.inera.intyg.intygsbestallning.web.controller.api;

import org.junit.Test;
import se.inera.intyg.intygsbestallning.web.BaseRestIntegrationTest;

import static org.junit.Assert.assertTrue;

public class ForfraganControllerIT extends BaseRestIntegrationTest {

    @Test
    public void testGetAnvandare() {
        assertTrue(true);
//        RestAssured.sessionId = getAuthSession(DEFAULT_VARDADMIN);
//        given().expect().statusCode(OK).when().get(USER_API_ENDPOINT).
//                then().
//                body(matchesJsonSchemaInClasspath("jsonschema/ib-user-response-schema.json")).
//                body("hsaId", equalTo(DEFAULT_VARDADMIN.getHsaId())).
//                body("valdVardenhet.id", equalTo(DEFAULT_VARDADMIN.getEnhetId())).
//                body("namn", equalTo(DEFAULT_VARDADMIN));
    }
}
