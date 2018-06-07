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
package se.inera.intyg.intygsbestallning.web.controller.api.dto.user;

/**
 * Created by marced on 2016-01-18.
 */
public class GetConfigResponse {
    private String version;

    private int kallelseArbetsdagar;
    private int postgangArbetsdagar;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getPostgangArbetsdagar() {
        return postgangArbetsdagar;
    }

    public void setPostgangArbetsdagar(int postgangArbetsdagar) {
        this.postgangArbetsdagar = postgangArbetsdagar;
    }

    public int getKallelseArbetsdagar() {
        return kallelseArbetsdagar;
    }

    public void setKallelseArbetsdagar(int kallelseArbetsdagar) {
        this.kallelseArbetsdagar = kallelseArbetsdagar;
    }

    public static final class GetConfigResponseBuilder {
        private String version;
        private int kallelseArbetsdagar;
        private int postgangArbetsdagar;

        public static GetConfigResponse.GetConfigResponseBuilder aGetConfigResponse() {
            return new GetConfigResponse.GetConfigResponseBuilder();
        }

        public GetConfigResponseBuilder withVersion(String version) {
            this.version = version;
            return this;
        }

        public GetConfigResponseBuilder withKallelseArbetsdagar(int kallelseArbetsdagar) {
            this.kallelseArbetsdagar = kallelseArbetsdagar;
            return this;
        }

        public GetConfigResponseBuilder withPostgangArbetsdagar(int postgangArbetsdagar) {
            this.postgangArbetsdagar = postgangArbetsdagar;
            return this;
        }

        public GetConfigResponse build() {
            GetConfigResponse getConfigResponse = new GetConfigResponse();
            getConfigResponse.setVersion(version);
            getConfigResponse.setPostgangArbetsdagar(postgangArbetsdagar);
            getConfigResponse.setKallelseArbetsdagar(kallelseArbetsdagar);
            return getConfigResponse;
        }
    }
}
