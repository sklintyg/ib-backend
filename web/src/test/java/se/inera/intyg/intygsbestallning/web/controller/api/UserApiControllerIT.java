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
import org.junit.Test;
import se.inera.intyg.intygsbestallning.web.BaseRestIntegrationTest;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * @author Martin Hesslund on 03/02/16.
 */
public class UserApiControllerIT extends BaseRestIntegrationTest {

    private static final String DEFAULT_VARDADMIN_NAME = "Ingbritt Filt";

    @Test
    public void testGetAnvandare() {
        RestAssured.sessionId = getAuthSession(DEFAULT_VARDADMIN);
        given().expect().statusCode(OK).when().get(USER_API_ENDPOINT).
                then().
                body(matchesJsonSchemaInClasspath("jsonschema/ib-user-response-schema.json")).
                body("hsaId", equalTo(DEFAULT_VARDADMIN.getHsaId())).
                body("namn", equalTo(DEFAULT_VARDADMIN_NAME));
    }
}
