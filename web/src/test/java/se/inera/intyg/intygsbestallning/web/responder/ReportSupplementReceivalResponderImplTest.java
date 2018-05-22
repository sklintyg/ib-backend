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
package se.inera.intyg.intygsbestallning.web.responder;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import se.inera.intyg.intygsbestallning.service.utredning.KompletteringServiceImpl;
import se.inera.intyg.intygsbestallning.web.responder.dto.ReportKompletteringMottagenRequest;
import se.riv.intygsbestallning.certificate.order.reportsupplementreceival.v1.ReportSupplementReceivalType;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static se.inera.intyg.intygsbestallning.common.util.RivtaTypesUtil.anII;

@RunWith(MockitoJUnitRunner.class)
public class ReportSupplementReceivalResponderImplTest {

    @Mock
    private KompletteringServiceImpl kompletteringService;

    @InjectMocks
    private ReportSupplementReceivalResponderImpl responder;

    @Test
    public void reportSupplementReceivalOk() {

        final String utredningId = "1";
        final String kompletteringId = "2";
        final String mottagetDatum = "20181111";
        final String sistaDatum = "20181211";

        ReportSupplementReceivalType type = new ReportSupplementReceivalType();
        type.setAssessmentId(anII("", utredningId));
        type.setReceivedDate(mottagetDatum);
        type.setSupplementRequestId(anII("", kompletteringId));
        type.setLastDateForSupplementRequest(sistaDatum);

        final ReportKompletteringMottagenRequest request = ReportKompletteringMottagenRequest.from(type);

        doNothing()
                .when(kompletteringService)
                .reportKompletteringMottagen(eq(request));

        responder.reportSupplementReceival("address", type);
    }

    @Test
    public void reportSupplementReceivalNoLogicalAddressNok() {

        final String utredningId = "1";
        final String kompletteringId = "2";
        final String mottagetDatum = "20181111";
        final String sistaDatum = "20181211";

        ReportSupplementReceivalType type = new ReportSupplementReceivalType();
        type.setAssessmentId(anII("", utredningId));
        type.setReceivedDate(mottagetDatum);
        type.setSupplementRequestId(anII("", kompletteringId));
        type.setLastDateForSupplementRequest(sistaDatum);

        final ReportKompletteringMottagenRequest request = ReportKompletteringMottagenRequest.from(type);

        assertThatThrownBy(() -> responder.reportSupplementReceival(null, type))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void reportSupplementReceivalNoTypeNok() {
        assertThatThrownBy(() -> responder.reportSupplementReceival("logicalAddress", null))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }
}