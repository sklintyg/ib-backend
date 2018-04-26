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

import se.inera.intyg.intygsbestallning.auth.model.IbVardgivare;
import se.inera.intyg.intygsbestallning.service.stateresolver.UtredningStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides possible filter values for ListBestallning.
 */
public class ListBestallningFilter {

    private List<IbVardgivare> vardgivare;
    private List<ListBestallningFilterStatus> statuses;
    private Map<UtredningStatus, ListBestallningFilterStatus> statusToFilterStatus = new HashMap<>();

    private ListBestallningFilter() {

    }

    public ListBestallningFilter(List<IbVardgivare> vardgivare, List<ListBestallningFilterStatus> statuses,
            Map<UtredningStatus, ListBestallningFilterStatus> statusToFilterStatus) {
        this.vardgivare = vardgivare;
        this.statuses = statuses;
        this.statusToFilterStatus = statusToFilterStatus;
    }

    public List<ListBestallningFilterStatus> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<ListBestallningFilterStatus> statuses) {
        this.statuses = statuses;
    }

    public Map<UtredningStatus, ListBestallningFilterStatus> getStatusToFilterStatus() {
        return statusToFilterStatus;
    }

    public void setStatusToFilterStatus(Map<UtredningStatus, ListBestallningFilterStatus> statusToFilterStatus) {
        this.statusToFilterStatus = statusToFilterStatus;
    }

    public List<IbVardgivare> getVardgivare() {
        return vardgivare;
    }

    public void setVardgivare(List<IbVardgivare> vardgivare) {
        this.vardgivare = vardgivare;
    }
}
