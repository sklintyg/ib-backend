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
package se.inera.intyg.intygsbestallning.service.utredning.dto;

import org.junit.Test;
import se.inera.intyg.intygsbestallning.persistence.model.type.EndReason;
import se.riv.intygsbestallning.certificate.order.endassessment.v1.EndAssessmentType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static se.inera.intyg.intygsbestallning.common.util.RivtaTypesUtil.aCv;
import static se.inera.intyg.intygsbestallning.common.util.RivtaTypesUtil.anII;

public class EndUtredningRequestTest {

    @Test
    public void testFrom() {
        EndAssessmentType endAssessmentType = new EndAssessmentType();
        endAssessmentType.setAssessmentId(anII(null, "1"));
        endAssessmentType.setEndingCondition(aCv(EndReason.JAV.name(), null, null));

        EndUtredningRequest result = EndUtredningRequest.from(endAssessmentType);

        assertNotNull(result);
        assertEquals(Long.valueOf(1L), result.getUtredningId());
        assertEquals(EndReason.JAV, result.getEndReason());
    }
}
