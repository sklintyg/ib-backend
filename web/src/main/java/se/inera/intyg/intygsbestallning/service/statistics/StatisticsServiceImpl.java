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
package se.inera.intyg.intygsbestallning.service.statistics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import se.inera.intyg.intygsbestallning.persistence.repository.UtredningRepository;
import se.inera.intyg.intygsbestallning.service.stateresolver.Actor;
import se.inera.intyg.intygsbestallning.service.stateresolver.InternForfraganStateResolver;
import se.inera.intyg.intygsbestallning.service.stateresolver.UtredningFas;
import se.inera.intyg.intygsbestallning.service.stateresolver.UtredningStatusResolver;
import se.inera.intyg.intygsbestallning.service.util.BusinessDaysBean;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.BestallningListItem;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.ForfraganListItem;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.SamordnarStatisticsResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.UtredningListItem;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.VardadminStatisticsResponse;

/**
 * Created by marced on 2018-05-04.
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {

    protected UtredningStatusResolver utredningStatusResolver = new UtredningStatusResolver();
    protected InternForfraganStateResolver internForfraganStateResolver = new InternForfraganStateResolver();

    @Autowired
    private UtredningRepository utredningRepository;

    @Autowired
    private BusinessDaysBean businessDays;

    @Override
    public SamordnarStatisticsResponse getStatsForSamordnare(String vardgivarHsaId) {
        long requireSamordnarActionCount = utredningRepository
                .findByExternForfragan_LandstingHsaId_AndArkiveradFalse(vardgivarHsaId)
                .stream()
                .map(u -> UtredningListItem.from(u, utredningStatusResolver.resolveStatus(u)))
                .filter(uli -> uli.getStatus().getNextActor().equals(Actor.SAMORDNARE)).count();
        return new SamordnarStatisticsResponse(requireSamordnarActionCount);
    }

    @Override
    public VardadminStatisticsResponse getStatsForVardadmin(String enhetsHsaId) {

        // Calculate nr of forfragningar for this vardenhet where next actor is VARDADMIN
        long forfraganRequiringActionCount = utredningRepository
                .findAllByExternForfragan_InternForfraganList_VardenhetHsaId_AndArkiveradFalse(enhetsHsaId)
                .stream()
                .map(utr -> ForfraganListItem.from(utr, enhetsHsaId, internForfraganStateResolver, businessDays))
                .filter(ffli -> ffli.getStatus().getNextActor().equals(Actor.VARDADMIN)).count();

        // Calculate nr of bestallningar for this vardenhet where action is required from actor VARDADMIN and not in AVSLUTAD or
        // FORFRAGAN fas.
        long bestallningarRequiringActionCount = utredningRepository
                .findAllByBestallning_TilldeladVardenhetHsaId_AndArkiveradFalse(enhetsHsaId)
                .stream()
                .map(u -> BestallningListItem.from(u, utredningStatusResolver.resolveStatus(u), Actor.VARDADMIN))
                .filter(bli -> bli.getKraverAtgard() && !bli.getFas().equals(UtredningFas.AVSLUTAD)
                        && !bli.getFas().equals(UtredningFas.FORFRAGAN))
                .count();

        return new VardadminStatisticsResponse(forfraganRequiringActionCount, bestallningarRequiringActionCount);
    }
}
