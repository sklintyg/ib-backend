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
package se.inera.intyg.intygsbestallning.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import se.inera.intyg.intygsbestallning.persistence.model.RegistreradVardenhet;

/**
 * Created by eriklupander on 2015-08-05.
 */
public interface RegistreradVardenhetRepository extends JpaRepository<RegistreradVardenhet, Long> {
    List<RegistreradVardenhet> findByVardgivareHsaId(@Param("vardgivareHsaId") String vardgivareHsaId);

    Optional<RegistreradVardenhet> findByVardgivareHsaIdAndVardenhetHsaId(@Param("vardgivareHsaId") String vardgivareHsaId,
            @Param("vardenhetHsaId") String vardenhetHsaId);

    @Query("SELECT DISTINCT(rve.vardgivareHsaId) FROM RegistreradVardenhet rve WHERE rve.vardenhetHsaId = :vardenhetHsaId")
    List<String> findVardgivareHsaIdRegisteredForVardenhet(@Param("vardenhetHsaId") String vardenhetHsaId);
}
