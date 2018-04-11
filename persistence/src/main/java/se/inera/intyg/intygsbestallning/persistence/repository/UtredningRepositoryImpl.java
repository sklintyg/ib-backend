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

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;

import se.inera.intyg.intygsbestallning.persistence.model.Forfragan;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;

/**
 * Created by eriklupander on 2015-08-05.
 */
public class UtredningRepositoryImpl implements UtredningRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Utredning> findByVardgivareHsaId(String vardgivareHsaId) {
        return entityManager
                .createQuery("SELECT u FROM Utredning u WHERE u.vardgivareHsaId = :vardgivareHsaId", Utredning.class)
                .setParameter("vardgivareHsaId", vardgivareHsaId)
                .getResultList();
    }

    @Override
    public List<Forfragan> findForfragningarForVardenhetHsaId(String vardenhetHsaId) {
        return entityManager
                .createQuery("SELECT f FROM Utredning u JOIN u.forfraganList f WHERE f.vardenhetHsaId = :vardenhetHsaId",
                        Forfragan.class)
                .setParameter("vardenhetHsaId", vardenhetHsaId)
                .getResultList();
    }

    @Override
    public Forfragan findForfraganByIdAndVardenhet(Long forfraganId, String vardenhetHsaId) {
        try {
            return entityManager.createQuery(
                    "SELECT f FROM Utredning u JOIN u.forfraganList f WHERE f.internreferens = :internreferens AND "
                    + "f.vardenhetHsaId = :vardenhetHsaId",
                    Forfragan.class)
                    .setParameter("internreferens", forfraganId)
                    .setParameter("vardenhetHsaId", vardenhetHsaId)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        } catch (NonUniqueResultException nure) {
            throw new IllegalStateException(
                    "Query for Forfragan returned multiple records, should never occur. internreferens: " + forfraganId
                            + ", vardenhetHsaId: " + vardenhetHsaId);
        }

    }
}
