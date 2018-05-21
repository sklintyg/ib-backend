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
package se.inera.intyg.intygsbestallning.service.utlatande;

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.type.HandlingUrsprungTyp;
import se.inera.intyg.intygsbestallning.persistence.repository.UtredningRepository;
import se.inera.intyg.intygsbestallning.testutil.TestDataGen;
import se.inera.intyg.intygsbestallning.web.responder.ReportCertificateReceivalResponderImpl;
import se.inera.intyg.intygsbestallning.web.responder.dto.RegistreraUtlatandeMottagetRequest;
import se.riv.intygsbestallning.certificate.order.reportcertificatereceival.v1.ReportCertificateReceivalType;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doReturn;
import static se.inera.intyg.intygsbestallning.common.util.RivtaTypesUtil.anII;
import static se.inera.intyg.intygsbestallning.persistence.model.Besok.BesokBuilder.aBesok;
import static se.inera.intyg.intygsbestallning.persistence.model.Handling.HandlingBuilder.aHandling;
import static se.inera.intyg.intygsbestallning.persistence.model.Intyg.IntygBuilder.anIntyg;

@RunWith(MockitoJUnitRunner.class)
public class UtlatandeServiceImplTest {

    private static final String RECEIVAL_DATE = "20180909";
    private static final String LAST_DATE_FOR_SUPPLEMENT_REQUEST = "20181009";

    @Mock
    private UtredningRepository utredningRepository;

    @InjectMocks
    private UtlatandeServiceImpl utlatandeService;

    @Test
    public void registreraUtlatandeMottagetOk() {

        final Utredning utredning = TestDataGen.createUtredning();
        utredning.getHandlingList().add(aHandling()
                .withSkickatDatum(LocalDateTime.now())
                .withUrsprung(HandlingUrsprungTyp.BESTALLNING)
                .withInkomDatum(LocalDateTime.now())
                .build());
        utredning.getBesokList().add(aBesok()
                .build());
        utredning.setIntygList(ImmutableList.of(anIntyg()
                .withSkickatDatum(LocalDateTime.now())
                .build()));

        ReportCertificateReceivalType type = new ReportCertificateReceivalType();
        type.setAssessmentId(anII("", utredning.getUtredningId().toString()));
        type.setReceivedDate(RECEIVAL_DATE);
        type.setLastDateForSupplementRequest(LAST_DATE_FOR_SUPPLEMENT_REQUEST);


        final RegistreraUtlatandeMottagetRequest request = RegistreraUtlatandeMottagetRequest.from(type);

        doReturn(Optional.of(utredning))
                .when(utredningRepository)
                .findById(request.getUtredningId());

        utlatandeService.registreraUtlatandeMottaget(request);
    }

    @Test
    public void registreraUtlatandeMottagetIncorrectStateNok() {

        final Utredning utredning = TestDataGen.createUtredning();

        ReportCertificateReceivalType type = new ReportCertificateReceivalType();
        type.setAssessmentId(anII("", utredning.getUtredningId().toString()));
        type.setReceivedDate(RECEIVAL_DATE);
        type.setLastDateForSupplementRequest(LAST_DATE_FOR_SUPPLEMENT_REQUEST);


        final RegistreraUtlatandeMottagetRequest request = RegistreraUtlatandeMottagetRequest.from(type);

        doReturn(Optional.of(utredning))
                .when(utredningRepository)
                .findById(request.getUtredningId());

        assertThatThrownBy(() -> utlatandeService.registreraUtlatandeMottaget(request))
                .isExactlyInstanceOf(IbServiceException.class);
    }
}