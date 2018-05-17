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
package se.inera.intyg.intygsbestallning.service.vardgivare;

import se.inera.intyg.intygsbestallning.service.vardgivare.dto.VardgivarVardenhetListItem;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.GetVardenheterForVardgivareResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.ListVardenheterForVardgivareRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.ListVardenheterForVardgivareResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.SearchForVardenhetResponse;

public interface VardgivareService {
    /**
     * Return a list of all registered {@link se.inera.intyg.intygsbestallning.service.vardgivare.dto.VardenhetItem} for a
     * vardgivare, grouped by {@link se.inera.intyg.intygsbestallning.persistence.model.type.RegiFormTyp}.
     *
     * @param vardgivareHsaId
     *            - Id for the vardgivare to list vardenheter for.
     * @return
     */
    GetVardenheterForVardgivareResponse listVardenheterForVardgivare(String vardgivareHsaId);

    /**
     * Return a filtered and paged list of {@link VardgivarVardenhetListItem}
     * for a
     * vardgivare.
     *
     * @param vardgivareHsaId
     *            - Id for the vardgivare to list vardenheter for.
     * @return
     */
    ListVardenheterForVardgivareResponse findVardenheterForVardgivareWithFilter(String vardgivareHsaId,
            ListVardenheterForVardgivareRequest request);

    /**
     * Return a filtered and paged list of {@link VardgivarVardenhetListItem}
     * for a
     * vardgivare.
     *
     * @param vardgivareHsaId
     *            - Id for the vardgivare to update vardenhet regiForm for.
     * @param vardenhetHsaId
     *            - Id for the vardenehet to update vardenhet regiForm for.
     * @param regiForm
     *            - Enum value compatible with to update vardenhet regiForm to.
     * @return
     */
    VardgivarVardenhetListItem updateRegiForm(String vardgivareHsaId, String vardenhetHsaId, String regiForm);

    /**
     * Remove the registered vardenhet identified by vardgivareHsaId and vardenhetHsaId.
     *
     * @param vardgivareHsaId
     * @param vardenhetHsaId
     */
    void delete(String vardgivareHsaId, String vardenhetHsaId);

    /**
     * Search for vardenhet in hsa identified by vardenhetHsaId and correlate against existing vardeneheter for
     * vardgivarHsaId.
     *
     * @param vardgivarHsaId
     * @param vardenhetHsaId
     * @return
     */
    SearchForVardenhetResponse searchVardenhetByHsaId(String vardgivarHsaId, String vardenhetHsaId);

    /**
     * Creates a new RegistreradVardenhet for the @vardenhetHsaId. If the vardenhet is already added, and error is thrown.
     *
     * @param vardgivarHsaId
     * @param vardenhetHsaId
     * @param regiForm
     * @return
     */
    VardgivarVardenhetListItem addVardenhet(String vardgivarHsaId, String vardenhetHsaId, String regiForm);

}
