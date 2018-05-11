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

import java.util.List;

import se.inera.intyg.intygsbestallning.service.vardgivare.dto.VardenhetListItem;

/**
 * Created by marced on 2018-05-11.
 */
public class ListVardenheterForVardgivareResponse {
    private List<VardenhetListItem> vardenheter;
    private int totalCount;

    private ListVardenheterForVardgivareResponse() {
    }

    public ListVardenheterForVardgivareResponse(List<VardenhetListItem> vardenheter, int totalCount) {
        this.vardenheter = vardenheter;
        this.totalCount = totalCount;
    }

    public List<VardenhetListItem> getVardenheter() {
        return vardenheter;
    }

    public void setVardenheter(List<VardenhetListItem> vardenheter) {
        this.vardenheter = vardenheter;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

}
