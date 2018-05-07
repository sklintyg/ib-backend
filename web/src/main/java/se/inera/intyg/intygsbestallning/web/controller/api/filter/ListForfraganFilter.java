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
package se.inera.intyg.intygsbestallning.web.controller.api.filter;

import java.util.List;

/**
 * Provides possible filter values for ListBestallning.
 */
public class ListForfraganFilter {

    private List<SelectItem> vardgivare;
    private List<ListForfraganFilterStatus> statuses;

    private ListForfraganFilter() {

    }

    public ListForfraganFilter(List<SelectItem> vardgivare, List<ListForfraganFilterStatus> statuses) {
        this.vardgivare = vardgivare;
        this.statuses = statuses;
    }

    public List<ListForfraganFilterStatus> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<ListForfraganFilterStatus> statuses) {
        this.statuses = statuses;
    }

    public List<SelectItem> getVardgivare() {
        return vardgivare;
    }

    public void setVardgivare(List<SelectItem> vardgivare) {
        this.vardgivare = vardgivare;
    }
}
