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

import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import se.inera.intyg.intygsbestallning.auth.IbUser;
import se.inera.intyg.intygsbestallning.auth.model.SelectableHsaEntityType;
import se.inera.intyg.intygsbestallning.common.exception.IbAuthorizationException;
import se.inera.intyg.intygsbestallning.monitoring.PrometheusTimeMethod;
import se.inera.intyg.intygsbestallning.persistence.repository.UtredningRepository;
import se.inera.intyg.intygsbestallning.service.stateresolver.UtredningStateResolver;
import se.inera.intyg.intygsbestallning.service.stateresolver.UtredningStatus;
import se.inera.intyg.intygsbestallning.service.user.UserService;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.SamordnarStatisticsResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.UtredningListItem;

/**
 * Created by marced on 2018-05-02.
 */
@RestController
@RequestMapping("/api/stats")
public class StatisticsController {

    @Autowired
    private UserService userService;

    @Autowired
    private UtredningRepository utredningRepository;

    protected UtredningStateResolver utredningStateResolver = new UtredningStateResolver();

    @PrometheusTimeMethod(name = "get_samordnare_stats_duration_seconds", help = "Some helpful info here")
    @GetMapping(path = "/samordnare", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SamordnarStatisticsResponse> getStatsForSamordnare() {
        IbUser user = userService.getUser();

        validateCorrectLoginContext(user, SelectableHsaEntityType.VG);

        // Calculate nr of utredningar for this vg/landsting's in a status that requires action from actor 'samordnare'
        long requireSamordnarActionCount = utredningRepository
                .findByExternForfragan_LandstingHsaId_AndArkiveradFalse(user.getCurrentlyLoggedInAt().getId())
                .stream()
                .map(u -> UtredningListItem.from(u, utredningStateResolver.resolveStatus(u)))
                .filter(statusIsEligibleForSamordnareStats()).count();
        return ResponseEntity.ok(new SamordnarStatisticsResponse(requireSamordnarActionCount));
    }

    private Predicate<UtredningListItem> statusIsEligibleForSamordnareStats() {
        return uli -> uli.getStatus() == UtredningStatus.FORFRAGAN_INKOMMEN || uli.getStatus() == UtredningStatus.TILLDELA_UTREDNING;
    }

    private void validateCorrectLoginContext(IbUser user, SelectableHsaEntityType selectedEntityType) {
        if (user == null || user.getCurrentlyLoggedInAt() == null) {
            throw new IbAuthorizationException("getCurrentlyLoggedInAt must be set but was null");
        } else if (!user.getCurrentlyLoggedInAt().getType().equals(selectedEntityType)) {
            throw new IbAuthorizationException("Expected getCurrentlyLoggedInAt to be a " + selectedEntityType.name() + ", but was a "
                    + user.getCurrentlyLoggedInAt().getType().name());
        }

    }

}
