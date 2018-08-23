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

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import se.inera.intyg.intygsbestallning.persistence.model.ExternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;

import java.util.List;

//CHECKSTYLE:OFF MethodName
//CHECKSTYLE:OFF LineLength
public interface ExternForfraganRepository extends JpaRepository<ExternForfragan, Long> {

    @Query("SELECT u FROM Utredning u JOIN FETCH u.externForfragan ef JOIN FETCH ef.internForfraganList intf LEFT JOIN FETCH intf.forfraganSvar ffs LEFT JOIN FETCH u.invanare inv LEFT JOIN FETCH u.handlaggare h LEFT JOIN FETCH u.bestallning b LEFT JOIN FETCH u.betalning bet WHERE (u.arkiverad = false OR u.avbrutenOrsak = 'INGEN_BESTALLNING') AND intf.vardenhetHsaId = :vardenhetHsaId")
    List<Utredning> findByExternForfraganAndVardenhetHsaIdAndArkiveradFalseOrIngenBestallning(@Param("vardenhetHsaId") String vardenhetHsaId);
}
//CHECKSTYLE:ON MethodName
//CHECKSTYLE:ON LineLength
