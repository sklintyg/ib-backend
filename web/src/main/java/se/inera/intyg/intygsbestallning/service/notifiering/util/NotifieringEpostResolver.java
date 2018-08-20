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
package se.inera.intyg.intygsbestallning.service.notifiering.util;

import static java.util.Objects.nonNull;

import org.springframework.stereotype.Component;
import java.util.Optional;
import se.inera.intyg.intygsbestallning.persistence.model.ExternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.ForfraganSvar;
import se.inera.intyg.intygsbestallning.persistence.model.InternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.service.vardenhet.VardenhetService;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.vardenhet.VardenhetPreferenceResponse;

@Component
public class NotifieringEpostResolver {

    private final VardenhetService vardenhetService;

    public NotifieringEpostResolver(VardenhetService vardenhetService) {
        this.vardenhetService = vardenhetService;
    }

    public Optional<String> resolveVardenhetNotifieringEpost(final String hsaId, final Utredning utredning) {
        Optional<String> epost = Optional.of(utredning)
                .flatMap(Utredning::getExternForfragan)
                .map(ExternForfragan::getInternForfraganList)
                .flatMap(internForfraganList -> internForfraganList.stream()
                        .filter(internForfragan -> nonNull(internForfragan.getTilldeladDatum()))
                        .findFirst()
                        .map(InternForfragan::getForfraganSvar)
                        .map(ForfraganSvar::getUtforareEpost));

        if (!epost.isPresent()) {
            epost = Optional.of(vardenhetService.getVardEnhetPreference(hsaId)).map(VardenhetPreferenceResponse::getEpost);
        }

        return epost;
    }
}
