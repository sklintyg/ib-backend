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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.status.UtredningStatusResolver;

@Transactional
public class UtredningRepositoryImpl implements UtredningRepositoryCustom {

    @Autowired
    private EntityManager entityManager;

    @Override
    public void persist(Utredning utredning) {
        entityManager.persist(utredning);
    }

    @Override
    public <S extends Utredning> S saveUtredning(S utredning) {
        JpaEntityInformation<Utredning, ?> entityInformation =
                JpaEntityInformationSupport.getEntityInformation(Utredning.class, entityManager);

        utredning.setStatus(UtredningStatusResolver.resolveStaticStatus(utredning));

        if (entityInformation.isNew(utredning)) {
            entityManager.persist(utredning);
            return utredning;
        } else {
            return entityManager.merge(utredning);
        }
    }
}
