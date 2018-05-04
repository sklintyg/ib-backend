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

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

import org.junit.Test;

import com.jayway.restassured.RestAssured;

import se.inera.intyg.intygsbestallning.web.BaseRestIntegrationTest;

/**
 * Created by marced on 2018-05-03.
 */
public class StatisticsControllerIT extends BaseRestIntegrationTest {
    private static final String SAMORDNARE_API_ENDPOINT = "/api/stats/samordnare";

    @Test
    public void testGetSamordnarStats() {
        RestAssured.sessionId = getAuthSession(DEFAULT_SAMORDNARE);
        given().expect().statusCode(OK).when().get(SAMORDNARE_API_ENDPOINT).then()
                .body(matchesJsonSchemaInClasspath("jsonschema/ib-stats-samordnare-response-schema.json"));
    }

    @Test
    public void testGetSamordnarStatsForVardenhetsFails() {
        RestAssured.sessionId = getAuthSession(DEFAULT_VARDADMIN);
        given().expect().statusCode(403).when().get(SAMORDNARE_API_ENDPOINT);
    }

}
