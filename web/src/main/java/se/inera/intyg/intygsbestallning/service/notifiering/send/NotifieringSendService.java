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
package se.inera.intyg.intygsbestallning.service.notifiering.send;

import java.util.List;
import se.inera.intyg.intygsbestallning.persistence.model.Besok;
import se.inera.intyg.intygsbestallning.persistence.model.InternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;

public interface NotifieringSendService {

    void notifieraLandstingNyExternforfragan(Utredning utredning);

    void notifieraVardenhetNyInternforfragan(Utredning utredning, InternForfragan internForfragan);

    void notifieraLandstingSamtligaVardenheterHarSvaratPaInternforfragan(Utredning utredning);

    void notifieraVardenhetTilldeladUtredning(Utredning utredning, InternForfragan tillDeladInternForfragan, String landstingNamn);

    void notifieraVardenhetPaminnelseSvaraInternforfragan(Utredning utredning);

    void notifieraLandstingPaminnelseSvaraExternforfragan(Utredning utredning);

    void notifieraLandstingIngenBestallning(Utredning utredning, InternForfragan internForfragan);

    void notifieraVardenhetIngenBestallning(Utredning utredning, InternForfragan internForfragan);

    void notifieraVardenhetNyBestallning(Utredning utredning);

    void notifieraLandstingAvslutadPgaJav(Utredning utredning);

    void notifieraVardenhetAvslutadPgaJav(Utredning utredning);

    void notifieraVardenhetUppdateradBestallning(Utredning utredning);

    void notifieraLandstingAvvikelseRapporteradAvVarden(Utredning utredning, Besok besok);

    void notifieraVardenhetAvvikelseMottagenFranFK(Utredning utredning, Besok besok);

    void notifieraLandstingAvvikelseMottagenFranFK(Utredning utredning, Besok besok);

    void notifieraVardenhetAvslutadUtredning(Utredning utredning);

    void notifieraLandstingAvslutadUtredning(Utredning utredning);

    void notifieraVardenhetPaminnelseSlutdatumUtredning(Utredning utredning);

    void notifieraVardenhetSlutdatumPasseratUtredning(Utredning utredning);

    void notifieraLandstingSlutdatumPasseratUtredning(Utredning utredning);

    void notifieraVardenhetKompletteringBegard(Utredning utredning);

    void notifieraVardenhetPaminnelseSlutdatumKomplettering(Utredning utredning, List<Long> intygIds);

    void notifieraVardenhetSlutdatumPasseratKomplettering(Utredning utredning);

    void notifieraLandstingSlutdatumPasseratKomplettering(Utredning utredning);

    void notifieraVardenhetRedovisaBesok(Utredning utredning);

}
