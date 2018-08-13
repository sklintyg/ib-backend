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
import org.springframework.transaction.annotation.Transactional;
import se.inera.intyg.intygsbestallning.persistence.model.status.Actor;
import se.inera.intyg.intygsbestallning.persistence.model.status.UtredningFas;
import se.inera.intyg.intygsbestallning.persistence.repository.UtredningRepository;
import se.inera.intyg.intygsbestallning.service.util.BusinessDaysBean;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.bestallning.BestallningListItemFactory;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.forfragan.InternForfraganListItemFactory;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.statistics.SamordnarStatisticsResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.statistics.VardadminStatisticsResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.utredning.UtredningListItemFactory;

/**
 * Created by marced on 2018-05-04.
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private BestallningListItemFactory bestallningListItemFactory;

    @Autowired
    private InternForfraganListItemFactory internForfraganListItemFactory;

    @Autowired
    private UtredningListItemFactory utredningListItemFactory;

    @Autowired
    private UtredningRepository utredningRepository;

    @Autowired
    private BusinessDaysBean businessDays;

    @Override
    @Transactional(readOnly = true)
    public SamordnarStatisticsResponse getStatsForSamordnare(String vardgivarHsaId) {
        long requireSamordnarActionCount = utredningRepository
                .findByExternForfragan_LandstingHsaId_AndArkiverad(vardgivarHsaId, false)
                .stream()
                .map(u -> utredningListItemFactory.from(u))
                .filter(uli -> uli.getStatus().getNextActor().equals(Actor.SAMORDNARE))
                .count();
        return new SamordnarStatisticsResponse(requireSamordnarActionCount);
    }

    @Override
    @Transactional(readOnly = true)
    public VardadminStatisticsResponse getStatsForVardadmin(String enhetsHsaId) {

        // Calculate nr of forfragningar for this vardenhet where next actor is VARDADMIN
        long forfraganRequiringActionCount = utredningRepository
                .findAllByExternForfragan_InternForfraganList_VardenhetHsaId_AndArkiveradFalse(enhetsHsaId)
                .stream()
                .map(utr -> internForfraganListItemFactory.from(utr, enhetsHsaId))
                .filter(ffli -> ffli.getStatus().getNextActor().equals(Actor.VARDADMIN))
                .count();

        // Calculate nr of bestallningar for this vardenhet where action is required from actor VARDADMIN and not in AVSLUTAD or
        // FORFRAGAN fas.
        long bestallningarRequiringActionCount = utredningRepository
                .findAllByBestallning_TilldeladVardenhetHsaId_AndArkiveradFalse(enhetsHsaId)
                .stream()
                .map(u -> bestallningListItemFactory.from(u, Actor.VARDADMIN))
                .filter(bli -> bli.getKraverAtgard() && !bli.getFas().equals(UtredningFas.AVSLUTAD)
                        && !bli.getFas().equals(UtredningFas.FORFRAGAN))
                .count();

        return new VardadminStatisticsResponse(forfraganRequiringActionCount, bestallningarRequiringActionCount);
    }
}
