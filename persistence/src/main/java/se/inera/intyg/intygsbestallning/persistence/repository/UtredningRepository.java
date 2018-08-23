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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.status.InternForfraganStatus;
import se.inera.intyg.intygsbestallning.persistence.model.status.UtredningStatus;
import se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringMottagarTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringTyp;

//CHECKSTYLE:OFF MethodName
//CHECKSTYLE:OFF LineLength
//CHECKSTYLE:OFF OperatorWrap
public interface UtredningRepository extends UtredningRepositoryCustom, JpaRepository<Utredning, Long> {

    /**
     * Variant query that filters out any Utredningar that are in a closed state.
     * @param vardenhetHsaId
     * @return
     */
    List<Utredning> findAllByExternForfragan_InternForfraganList_VardenhetHsaId_AndArkiveradFalse(String vardenhetHsaId);

    @Query("SELECT u FROM Utredning u " +
           "JOIN u.externForfragan ex " +
           "JOIN ex.internForfraganList if " +
           "WHERE if.besvarasSenastDatum IS NOT NULL " +
           "AND if.besvarasSenastDatum < :besvarasSenastDatum " +
           "AND if.status in :statusar " +
           "AND u.utredningId NOT IN (SELECT u.utredningId FROM Utredning u " +
               "JOIN u.skickadNotifieringList n WHERE n.typ = :typ " +
               "AND n.mottagare = :mottagare " +
               "AND n.ersatts = false)"
    )
    List<Utredning> findNonNotifiedInternforfraganSlutDatumBefore(
            @Param("besvarasSenastDatum") LocalDateTime besvarasSenastDatum,
            @Param("statusar") Set<InternForfraganStatus> statusar,
            @Param("typ") NotifieringTyp typ,
            @Param("mottagare") NotifieringMottagarTyp mottagare);

    /**
     * Returns utredningar tilldelad to vardenhet in archived state.
     *
     * @param vardenhetHsaId
     * @return
     */
    @Query("SELECT u FROM Utredning u JOIN FETCH u.bestallning b LEFT JOIN FETCH u.invanare inv LEFT JOIN FETCH u.handlaggare h LEFT JOIN FETCH u.betalning bet WHERE u.arkiverad = true AND b.tilldeladVardenhetHsaId = :vardenhetHsaId AND b.tilldeladVardenhetOrgNr = :vardgivareOrgnr")
    List<Utredning> findAllByBestallning_TilldeladVardenhetHsaId_AndArkiveradTrue(@Param("vardenhetHsaId") String vardenhetHsaId, @Param("vardgivareOrgnr") String vardgivareOrgnr);

    List<Utredning> findAllByExternForfragan_LandstingHsaId(String landstingHsaId);

    @Query("SELECT DISTINCT u FROM Utredning u JOIN FETCH u.externForfragan ef LEFT JOIN FETCH ef.internForfraganList ifl LEFT JOIN FETCH ifl.forfraganSvar fs LEFT JOIN FETCH u.bestallning b LEFT JOIN FETCH u.invanare inv LEFT JOIN FETCH u.handlaggare h LEFT JOIN FETCH u.betalning bet WHERE u.arkiverad = :arkiverad AND ef.landstingHsaId = :landstingHsaId")
    List<Utredning> findByExternForfragan_LandstingHsaId_AndArkiverad(@Param("landstingHsaId") String landstingHsaId, @Param("arkiverad") boolean arkiverad);

    /**
     * Variant query that filters out any Utredningar that are in a closed state.
     *
     * @param vardenhetHsaId
     * @return
     */
    @Query("SELECT u FROM Utredning u JOIN FETCH u.bestallning b LEFT JOIN FETCH u.invanare inv LEFT JOIN FETCH u.handlaggare h LEFT JOIN FETCH u.betalning bet WHERE u.arkiverad = false AND b.tilldeladVardenhetHsaId = :vardenhetHsaId AND b.tilldeladVardenhetOrgNr = :vardgivareOrgnr")
    List<Utredning> findAllByBestallning_TilldeladVardenhetHsaId_AndArkiveradFalse(@Param("vardenhetHsaId") String vardenhetHsaId, @Param("vardgivareOrgnr") String vardgivareOrgnr);

    @Query("SELECT ef.landstingHsaId FROM Utredning u JOIN u.bestallning b JOIN u.externForfragan ef WHERE b.tilldeladVardenhetHsaId = :vardenhetHsaId AND b.tilldeladVardenhetOrgNr = :vardgivareOrgnr")
    List<String> findDistinctLandstingHsaIdByVardenhetHsaIdHavingBestallning(@Param("vardenhetHsaId") String vardenhetHsaId, @Param("vardgivareOrgnr") String vardgivareOrgnr);

    @Query("SELECT ef.landstingHsaId FROM Utredning u JOIN u.bestallning b JOIN u.externForfragan ef WHERE b.tilldeladVardenhetHsaId = :vardenhetHsaId AND b.tilldeladVardenhetOrgNr = :vardgivareOrgnr AND u.arkiverad = true")
    List<String> findDistinctLandstingHsaIdByVardenhetHsaIdHavingBestallningAndIsArkiverad(@Param("vardenhetHsaId") String vardenhetHsaId, @Param("vardgivareOrgnr") String vardgivareOrgnr);

    @Query("SELECT MAX(il.id) FROM Utredning u JOIN u.intygList il WHERE il.komplettering = true")
    Optional<Long> findNewestKompletteringOnUtredning(Long utredningId);

    Optional<Utredning> findByBesokList_Id(Long id);

    /**
     * Alla utredningar med intyg med slutdatum inom intervall och som ej har notifiering av angiven typ.
     */
    @Query("SELECT u FROM Utredning u " +
           "JOIN u.intygList i " +
           "JOIN u.bestallning b " +
           "WHERE b.tilldeladVardenhetHsaId is not null " +
           "AND u.arkiverad = false " +
           "AND i.komplettering = false " +
           "AND i.sistaDatum is not null " +
           "AND i.sistaDatum >= :fromDate " +
           "AND i.sistaDatum <= :toDate " +
           "AND u.utredningId NOT IN (" +
               "SELECT u.utredningId FROM Utredning u " +
               "JOIN u.skickadNotifieringList n " +
               "WHERE n.typ = :typ " +
               "AND n.mottagare = :mottagare)"
    )
    List<Utredning> findNonNotifiedIntygSlutDatumBetween(@Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate, @Param("typ") NotifieringTyp typ, @Param("mottagare") NotifieringMottagarTyp mottagare);

    /**
     * Alla utredningar med intyg med slutdatum som passerats och som ej har notifiering av angiven typ.
     */
    @Query("SELECT u FROM Utredning u " +
           "JOIN u.intygList i " +
           "JOIN u.bestallning b " +
           "WHERE b.tilldeladVardenhetHsaId is not null " +
           "AND u.arkiverad = false " +
           "AND i.mottagetDatum is null " +
           "AND i.komplettering = false " +
           "AND i.sistaDatum is not null " +
           "AND i.sistaDatum < :now " +
           "AND u.utredningId NOT IN (" +
               "SELECT u.utredningId FROM Utredning u " +
               "JOIN u.skickadNotifieringList n " +
               "WHERE n.typ = :typ " +
               "AND n.mottagare = :mottagare " +
               "AND n.ersatts = false)"
    )
    List<Utredning> findNonNotifiedSlutDatumBefore(@Param("now") LocalDateTime now, @Param("typ") NotifieringTyp typ, @Param("mottagare") NotifieringMottagarTyp mottagare);

    /**
     * Alla utredningar med externförfrågan som har besvarassenast inom intervall och som ej har notifiering av angiven typ.
     */
    @Query("SELECT u FROM Utredning u " +
           "JOIN u.externForfragan e " +
           "WHERE u.arkiverad = false " +
           "AND e.besvarasSenastDatum >= :fromDate  " +
           "AND e.besvarasSenastDatum <= :toDate " +
           "AND u.utredningId NOT IN (" +
               "SELECT u.utredningId FROM Utredning u " +
               "JOIN u.skickadNotifieringList n WHERE n.typ = :typ " +
               "AND n.mottagare = :mottagare " +
               "AND n.ersatts = false)"
    )
    List<Utredning> findNonNotifiedExternforfraganBesvarasSenastBetween(@Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate, @Param("typ") NotifieringTyp typ, @Param("mottagare") NotifieringMottagarTyp mottagare);

    /**
     * Alla utredningar med intyg som är kompletteringar med slutdatum som passerats och som ej har notifiering av angiven typ.
     */
//    @Query("SELECT u,i FROM Utredning u " +
//           "JOIN u.intygList i " +
//           "JOIN u.bestallning b " +
//           "WHERE b.tilldeladVardenhetHsaId is not null " +
//           "AND u.arkiverad = false " +
//           "AND i.komplettering = true " +
//           "AND i.skickatDatum is null " +
//           "AND i.sistaDatum is not null " +
//           "AND i.sistaDatum < :date " +
//           "AND i.id NOT IN (" +
//               "SELECT n.intygId FROM SkickadNotifiering n " +
//               "WHERE n.typ = :typ " +
//               "AND n.mottagare = :mottagare " +
//               "AND n.ersatts = false)"
//    )
//    List<Object[]> findNonNotifiedSistadatumKompletteringBefore(@Param("date") LocalDateTime date, @Param("typ") NotifieringTyp typ, @Param("mottagare") NotifieringMottagarTyp mottagare);
//
//

    @Query("SELECT new se.inera.intyg.intygsbestallning.persistence.repository.UtredningAndIntyg(u, i) FROM Utredning u " +
           "JOIN u.intygList i " +
           "JOIN u.bestallning b " +
           "WHERE b.tilldeladVardenhetHsaId is not null " +
           "AND u.arkiverad = false " +
           "AND i.komplettering = true " +
           "AND i.skickatDatum is null " +
           "AND i.sistaDatum is not null " +
           "AND i.sistaDatum < :date " +
           "AND i.id NOT IN (" +
               "SELECT n.intygId FROM SkickadNotifiering n " +
               "WHERE n.typ = :typ " +
               "AND n.mottagare = :mottagare " +
               "AND n.ersatts = false)"
    )
    List<UtredningAndIntyg> findNonNotifiedSistadatumKompletteringBefore(
            @Param("date") LocalDateTime date,
            @Param("typ") NotifieringTyp typ,
            @Param("mottagare") NotifieringMottagarTyp mottagare);

    @Query("SELECT DISTINCT u FROM Utredning u " +
           "JOIN u.intygList i " +
           "WHERE u.arkiverad = false " +
           "AND i.sistaDatumKompletteringsbegaran is not null " +
           "AND i.sistaDatumKompletteringsbegaran < :now"
    )
    List<Utredning> findSistaDatumKompletteringsBegaranBefore(
            @Param("now") LocalDateTime now);

    List<Utredning> findByStatus(UtredningStatus status);
    List<Utredning> findByStatusIn(List<UtredningStatus> status);
}
//CHECKSTYLE:ON MethodName
//CHECKSTYLE:ON LineLength
//CHECKSTYLE:ON OperatorWrap
