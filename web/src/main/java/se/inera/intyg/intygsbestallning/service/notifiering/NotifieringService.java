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
package se.inera.intyg.intygsbestallning.service.notifiering;

import se.inera.intyg.intygsbestallning.persistence.model.Utredning;

public interface NotifieringService {

    void notifieraLandstingNyExternforfragan(Utredning utredning);

    void notifieraVardenhetNyInternforfragan(Utredning utredning);

    void notifieraLandstingSamtligaVardenheterHarSvaratPaInternforfragan(Utredning utredning);

    void notifieraVardenhetTilldeladUtredning(Utredning utredning);

    void notifieraVardenhetPaminnelseSvaraInternforfragan(Utredning utredning);

    void notifieraLandstingPaminnelseSvaraExternforfragan(Utredning utredning);

    void notifieraLandstingIngenBestallning(Utredning utredning);

    void notifieraVardenhetIngenBestallning(Utredning utredning);

    void notifieraVardenhetNyBestallning(Utredning utredning);

    void notifieraLandstingAvslutadPgaJav(Utredning utredning);

    void notifieraVardenhetUppdateradBestallning(Utredning utredning);

    void notifieraLandstingAvvikelseRapporteradAvVarden(Utredning utredning);

    void notifieraVardenhetAvvikelseMottagenFranFK(Utredning utredning);

    void notifieraLandstingAvvikelseMottagenFranFK(Utredning utredning);

    void notifieraVardenhetAvslutadUtredning(Utredning utredning);

    void notifieraLandstingAvslutadUtredning(Utredning utredning);

    void notifieraVardenehtPaminnelseSlutdatumUtredning(Utredning utredning);

    void notifieraVardenhetSlutdatumPasseratUtredning(Utredning utredning);

    void notifieraLandstingSlutdatumPasseratUtredning(Utredning utredning);

    void notifieraVardenhetKompletteringBegard(Utredning utredning);

    void notifieraVardenhetPaminnelseSlutdatumKomplettering(Utredning utredning);

    void notifieraVardenhetSlutdatumPasseratKomplettering(Utredning utredning);

    void notifieraLandstingSlutdatumPasseratKomplettering(Utredning utredning);

    void notifieraVardenhetRedovisaBesok(Utredning utredning);

}
