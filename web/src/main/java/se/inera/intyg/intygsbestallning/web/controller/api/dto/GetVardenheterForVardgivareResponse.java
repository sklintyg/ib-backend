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

import se.inera.intyg.intygsbestallning.service.vardgivare.dto.VardenhetItem;

import java.util.List;

public class GetVardenheterForVardgivareResponse {
    private List<VardenhetItem> vardenhetItemList;
    private int totalCount;

    private GetVardenheterForVardgivareResponse() {

    }

    public GetVardenheterForVardgivareResponse(List<VardenhetItem> vardenhetItemList, int totalCount) {
        this.vardenhetItemList = vardenhetItemList;
        this.totalCount = totalCount;
    }

    public List<VardenhetItem> getVardenhetItemList() {
        return vardenhetItemList;
    }

    public void setVardenhetItemList(List<VardenhetItem> vardenhetItemList) {
        this.vardenhetItemList = vardenhetItemList;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
