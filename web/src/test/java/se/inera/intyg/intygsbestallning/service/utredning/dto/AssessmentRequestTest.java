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

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.persistence.model.type.UtredningsTyp;
import se.riv.intygsbestallning.certificate.order.requesthealthcareperformerforassessment.v1.RequestHealthcarePerformerForAssessmentType;

import java.text.MessageFormat;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static se.inera.intyg.intygsbestallning.common.util.RivtaTypesUtil.aCv;
import static se.inera.intyg.intygsbestallning.testutil.TestDataGen.createFullRequest;

public class AssessmentRequestTest {

    @Test
    public void testToConvertFromRequest() {

        final RequestHealthcarePerformerForAssessmentType request = createFullRequest();
        final AssessmentRequest converted = AssessmentRequest.from(request);

        assertEquals(UtredningsTyp.AFU, converted.getUtredningsTyp());
        assertEquals(LocalDateTime.of(2018, 10, 10, 0, 0, 0), converted.getBesvaraSenastDatum());
        assertEquals("coordinatingCountyCouncilId", converted.getLandstingHsaId());
        assertEquals("comment", converted.getKommentar());
        assertEquals("language", converted.getTolkSprak());

        assertNotNull(converted.getBestallare());
        assertEquals("fullName", converted.getBestallare().getFullstandigtNamn());
        assertEquals("phoneNumber", converted.getBestallare().getTelefonnummer());
        assertEquals("email", converted.getBestallare().getEmail());
        assertEquals("authority", converted.getBestallare().getMyndighet());
        assertEquals("officeName", converted.getBestallare().getKontor());
        assertEquals("officeCostCenter", converted.getBestallare().getKostnadsstalle());
        assertEquals("postalAddress", converted.getBestallare().getAdress());
        assertEquals("postalCode", converted.getBestallare().getPostkod());
        assertEquals("postalCity", converted.getBestallare().getStad());

        assertEquals("postalCity", converted.getInvanarePostort());
        assertEquals("specialNeeds", converted.getInvanareSarskildaBehov());
        assertThat(converted.getInvanareTidigareUtforare())
                .containsExactly("1", "2", "3");
    }

    @Test
    public void testToConvertFromRequestOkandUtredningsTyp() {

        final String okandUtredningsTyp = "okand-typ";

        RequestHealthcarePerformerForAssessmentType request = createFullRequest();
        request.setCertificateType(aCv(okandUtredningsTyp, null, null));

        assertThatThrownBy(() -> AssessmentRequest.from(request))
                .isExactlyInstanceOf(IbServiceException.class)
                .hasMessage(MessageFormat.format(
                "CertificateType: {0} is not of a known type",
                okandUtredningsTyp,
                ImmutableList.of(UtredningsTyp.AFU, UtredningsTyp.AFU_UTVIDGAD)));
    }

    @Test
    public void testToConvertFromRequestFelaktigUtredningsTyp() {

        final UtredningsTyp felakrigUtredningsTyp = UtredningsTyp.LIAG;

        RequestHealthcarePerformerForAssessmentType request = createFullRequest();
        request.setCertificateType(aCv(felakrigUtredningsTyp.name(), null, null));

        assertThatThrownBy(() -> AssessmentRequest.from(request))
                .isExactlyInstanceOf(IbServiceException.class)
                .hasMessage(MessageFormat.format(
                        "CertificateType: {0} is not a valid a valid type. Use one of the following types: {1})",
                        felakrigUtredningsTyp,
                        ImmutableList.of(UtredningsTyp.AFU, UtredningsTyp.AFU_UTVIDGAD)));

    }
}
