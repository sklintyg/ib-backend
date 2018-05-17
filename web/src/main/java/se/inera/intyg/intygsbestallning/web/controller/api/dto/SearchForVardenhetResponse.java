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
package se.inera.intyg.intygsbestallning.web.controller.api.dto;

import se.inera.intyg.intygsbestallning.service.vardgivare.dto.VardgivarVardenhetListItem;

/**
 * Created by marced on 2018-05-16.
 */
public class SearchForVardenhetResponse {

    private VardgivarVardenhetListItem vardenhet;
    private SearchFormVardenhetResultCodesEnum resultCode;
    private String errorMessage;

    public SearchForVardenhetResponse() {
    }

    public SearchForVardenhetResponse(VardgivarVardenhetListItem vardenhet, SearchFormVardenhetResultCodesEnum resultCode) {
        this.vardenhet = vardenhet;
        this.resultCode = resultCode;
    }

    public SearchForVardenhetResponse(VardgivarVardenhetListItem vardenhet, SearchFormVardenhetResultCodesEnum resultCode,
            String errorMessage) {
        this.vardenhet = vardenhet;
        this.resultCode = resultCode;
        this.errorMessage = errorMessage;
    }

    public SearchFormVardenhetResultCodesEnum getResultCode() {
        return resultCode;
    }

    public void setResultCode(SearchFormVardenhetResultCodesEnum resultCode) {
        this.resultCode = resultCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public VardgivarVardenhetListItem getVardenhet() {
        return vardenhet;
    }

    public void setVardenhet(VardgivarVardenhetListItem vardenhet) {
        this.vardenhet = vardenhet;
    }

}
