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
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringTyp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

//CHECKSTYLE:OFF MethodName
//CHECKSTYLE:OFF LineLength
public interface UtredningRepository extends JpaRepository<Utredning, Long> {
    List<Utredning> findAllByExternForfragan_InternForfraganList_VardenhetHsaId(String vardenhetHsaId);

    /**
     * Variant query that filters out any Utredningar that are in a closed state.
     * @param vardenhetHsaId
     * @return
     */
    List<Utredning> findAllByExternForfragan_InternForfraganList_VardenhetHsaId_AndArkiveradFalse(String vardenhetHsaId);

    /**
     * Returns utredningar tilldelad to vardenhet in archived state.
     *
     * @param vardenhetHsaId
     * @return
     */
    List<Utredning> findAllByBestallning_TilldeladVardenhetHsaId_AndArkiveradTrue(String vardenhetHsaId);

    List<Utredning> findAllByExternForfragan_LandstingHsaId(String landstingHsaId);

    List<Utredning> findByExternForfragan_LandstingHsaId_AndArkiveradFalse(String landstingHsaId);

    List<Utredning> findAllByBestallning_TilldeladVardenhetHsaId(String vardenhetHsaId);

    /**
     * Variant query that filters out any Utredningar that are in a closed state.
     *
     * @param vardenhetHsaId
     * @return
     */
    List<Utredning> findAllByBestallning_TilldeladVardenhetHsaId_AndArkiveradFalse(String vardenhetHsaId);

    @Query("SELECT ef.landstingHsaId FROM Utredning u JOIN u.bestallning b JOIN u.externForfragan ef WHERE b.tilldeladVardenhetHsaId = :vardenhetHsaId")
    List<String> findDistinctLandstingHsaIdByVardenhetHsaIdHavingBestallning(@Param("vardenhetHsaId") String vardenhetHsaId);

    @Query("SELECT ef.landstingHsaId FROM Utredning u JOIN u.bestallning b JOIN u.externForfragan ef WHERE b.tilldeladVardenhetHsaId = :vardenhetHsaId AND u.arkiverad = true")
    List<String> findDistinctLandstingHsaIdByVardenhetHsaIdHavingBestallningAndIsArkiverad(@Param("vardenhetHsaId") String vardenhetHsaId);

    @Query("SELECT MAX(il.id) FROM Utredning u JOIN u.intygList il WHERE il.komplettering = true")
    Optional<Long> findNewestKompletteringOnUtredning(Long utredningId);

    Optional<Utredning> findByBesokList_Id(Long id);

    /**
     * Alla utredningar med intyg med slutdatum inom intervall och som ej har notifiering av angiven typ.
     */
    @Query("SELECT u FROM Utredning u JOIN u.intygList i JOIN u.bestallning b WHERE b.tilldeladVardenhetHsaId is not null AND u.arkiverad = false AND i.komplettering = false AND i.sistaDatum is not null AND i.sistaDatum >= :fromDate AND i.sistaDatum <= :toDate AND u.utredningId NOT IN (SELECT u.utredningId FROM Utredning u JOIN u.notifieringList n WHERE n.notifieringTyp = :notifieringTyp)")
    List<Utredning> findNonNotifiedIntygSlutDatumBetween(@Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate, @Param("notifieringTyp") NotifieringTyp notifieringTyp);

    /**
     * Alla utredningar med intyg med slutdatum som passerats och som ej har notifiering av angiven typ.
     */
    @Query("SELECT u FROM Utredning u JOIN u.intygList i JOIN u.bestallning b WHERE b.tilldeladVardenhetHsaId is not null AND u.arkiverad = false AND i.mottagetDatum is null AND i.komplettering = false AND i.sistaDatum is not null AND i.sistaDatum < :now AND u.utredningId NOT IN (SELECT u.utredningId FROM Utredning u JOIN u.notifieringList n WHERE n.notifieringTyp = :notifieringTyp)")
    List<Utredning> findNonNotifiedSlutDatumBefore(@Param("now") LocalDateTime now, @Param("notifieringTyp") NotifieringTyp notifieringTyp);

}
//CHECKSTYLE:ON MethodName
//CHECKSTYLE:ON LineLength
