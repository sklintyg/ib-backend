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
package se.inera.intyg.intygsbestallning.service.export;

import se.inera.intyg.intygsbestallning.auth.model.IbVardenhet;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.bestallning.ListAvslutadeBestallningarRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.bestallning.ListBestallningRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.utredning.ListAvslutadeUtredningarRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.utredning.ListUtredningRequest;

public interface XlsxExportService {
    byte[] export(String loggedInAtHsaId, ListUtredningRequest request);
    byte[] export(String loggedInAtHsaId, ListAvslutadeUtredningarRequest request);
    byte[] export(IbVardenhet loggedInAt, ListBestallningRequest request);
    byte[] export(IbVardenhet loggedInAt, ListAvslutadeBestallningarRequest request);
}
