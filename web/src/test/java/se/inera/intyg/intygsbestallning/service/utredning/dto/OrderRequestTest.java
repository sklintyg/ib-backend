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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static se.inera.intyg.intygsbestallning.common.exception.IbErrorCodeEnum.BAD_REQUEST;
import static se.inera.intyg.intygsbestallning.common.exception.IbResponderValidationErrorCode.GTA_FEL05;
import static se.inera.intyg.intygsbestallning.common.exception.IbResponderValidationErrorCode.TA_FEL07;
import static se.inera.intyg.intygsbestallning.common.util.RivtaTypesUtil.aCv;
import static se.inera.intyg.intygsbestallning.common.util.RivtaTypesUtil.anII;
import static se.inera.intyg.intygsbestallning.persistence.model.type.MyndighetTyp.FKASSA;
import static se.inera.intyg.intygsbestallning.persistence.model.type.UtredningsTyp.AFU;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import se.inera.intyg.intygsbestallning.common.exception.IbResponderValidationException;
import se.inera.intyg.intygsbestallning.integration.myndighet.service.TjanstekontraktUtils;
import se.riv.intygsbestallning.certificate.order.orderassessment.v1.OrderAssessmentType;
import se.riv.intygsbestallning.certificate.order.v1.AddressType;
import se.riv.intygsbestallning.certificate.order.v1.AuthorityAdministrativeOfficialType;
import se.riv.intygsbestallning.certificate.order.v1.CitizenType;

import java.text.MessageFormat;
import java.time.LocalDate;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;

public class OrderRequestTest {

    @Test
    public void testFromRequestComplete() {
        OrderAssessmentType request = createFullRequest();

        OrderRequest result = OrderRequest.from(request);
        assertNotNull(result);
        assertEquals("atgarder", result.getAtgarder());
        assertEquals("enhet", result.getEnhetId());
        assertEquals("bakgrund", result.getInvanareBakgrund());
        assertEquals("behov", result.getInvanareBehov());
        assertEquals("191212121212", result.getInvanarePersonnummer());
        assertEquals("kommentar", result.getKommentar());
        assertEquals("syfte", result.getSyfte());
        assertEquals("sv", result.getTolkSprak());
        assertEquals(Long.valueOf(1L), result.getUtredningId());
        assertEquals(LocalDate.of(2019, 1, 1), result.getLastDateIntyg());
        assertEquals(AFU, result.getUtredningsTyp());
        assertEquals("adress", result.getBestallare().getAdress());
        assertEquals("email", result.getBestallare().getEmail());
        assertEquals("fullstandigtNamn", result.getBestallare().getFullstandigtNamn());
        assertEquals("kontor", result.getBestallare().getKontor());
        assertEquals("kostnadsstalle", result.getBestallare().getKostnadsstalle());
        assertEquals("FKASSA", result.getBestallare().getMyndighet());
        assertEquals("12345", result.getBestallare().getPostnummer());
        assertEquals("stad", result.getBestallare().getStad());
        assertEquals("telefonnummer", result.getBestallare().getTelefonnummer());
        assertEquals("firstname", result.getInvanareFornamn());
        assertEquals("middlename", result.getInvanareMellannamn());
        assertEquals("lastname", result.getInvanareEfternamn());
    }

    @Test
    public void testFromRequestAf() {
        OrderAssessmentType request = createFullRequest();
        request.setAssessmentId(null);
        request.setOrderDate(null);
        request.setLastDateForCertificateReceival(null);

        OrderRequest result = OrderRequest.from(request);
        assertNotNull(result);
        assertEquals("atgarder", result.getAtgarder());
        assertEquals("enhet", result.getEnhetId());
        assertEquals("bakgrund", result.getInvanareBakgrund());
        assertEquals("behov", result.getInvanareBehov());
        assertEquals("191212121212", result.getInvanarePersonnummer());
        assertEquals("kommentar", result.getKommentar());
        assertEquals("syfte", result.getSyfte());
        assertEquals("sv", result.getTolkSprak());
        assertNull(result.getUtredningId());
        assertNull(result.getLastDateIntyg());
        assertEquals(AFU, result.getUtredningsTyp());
        assertEquals("adress", result.getBestallare().getAdress());
        assertEquals("email", result.getBestallare().getEmail());
        assertEquals("fullstandigtNamn", result.getBestallare().getFullstandigtNamn());
        assertEquals("kontor", result.getBestallare().getKontor());
        assertEquals("kostnadsstalle", result.getBestallare().getKostnadsstalle());
        assertEquals("FKASSA", result.getBestallare().getMyndighet());
        assertEquals("12345", result.getBestallare().getPostnummer());
        assertEquals("stad", result.getBestallare().getStad());
        assertEquals("telefonnummer", result.getBestallare().getTelefonnummer());
    }

    @Test
    public void testConvertFailCertificateType() {
        OrderAssessmentType request = createFullRequest();
        request.setCertificateType(aCv("notExisting", TjanstekontraktUtils.KV_INTYGSTYP, null));

        assertThatThrownBy(() -> OrderRequest.from(request))
                .isExactlyInstanceOf(IbResponderValidationException.class)
                .hasMessage(MessageFormat.format("Unknown code: {0} for codeSystem: {1}", "notExisting",
                        TjanstekontraktUtils.KV_INTYGSTYP));
    }

    @Test
    public void testConvertFailOrderNameMissing() {
        OrderAssessmentType request = createFullRequest();
        request.getCitizen().setFirstName(null);
        request.getCitizen().setMiddleName(null);
        request.getCitizen().setLastName(null);

        assertThatThrownBy(() -> OrderRequest.from(request))
                .isExactlyInstanceOf(IbServiceException.class)
                .hasFieldOrPropertyWithValue("errorCode", BAD_REQUEST)
                .hasMessage("Name is required when assessmentId is present");
    }

    @Test
    public void testConvertFailIntygSentDate() {
        OrderAssessmentType request = createFullRequest();
        request.setLastDateForCertificateReceival(null);

        assertThatThrownBy(() -> OrderRequest.from(request))
                .isExactlyInstanceOf(IbResponderValidationException.class)
                .hasFieldOrPropertyWithValue("errorCode", TA_FEL07)
                .hasMessage("Slutdatum för utredningen måste anges");
    }

    @Test
    public void testConvertFailIncorrectCitizenPersonalId() {
        OrderAssessmentType request = createFullRequest();
        request.getCitizen().setPersonalIdentity(anII(null, "Very Bad Format"));

        assertThatThrownBy(() -> OrderRequest.from(request))
                .isExactlyInstanceOf(IbResponderValidationException.class)
                .hasFieldOrPropertyWithValue("errorCode", GTA_FEL05)
                .hasMessage("Very Bad Format does not match expected format YYYYMMDDNNNN");
    }

    @Test
    public void testConvertFailCitizenPersonalIdWithDash() {
        OrderAssessmentType request = createFullRequest();
        request.getCitizen().setPersonalIdentity(anII(null, "19121212-1212"));

        assertThatThrownBy(() -> OrderRequest.from(request))
                .isExactlyInstanceOf(IbResponderValidationException.class)
                .hasFieldOrPropertyWithValue("errorCode", GTA_FEL05)
                .hasMessage("19121212-1212 does not match expected format YYYYMMDDNNNN");
    }


    @NotNull
    private OrderAssessmentType createFullRequest() {
        OrderAssessmentType request = new OrderAssessmentType();
        request.setCertificateType(aCv(AFU.name(), TjanstekontraktUtils.KV_INTYGSTYP, null));
        request.setOrderDate("20180101");
        request.setLastDateForCertificateReceival("20190101");
        CitizenType citizen = new CitizenType();
        citizen.setPersonalIdentity(anII(null, "191212121212"));
        citizen.setFirstName("firstname");
        citizen.setMiddleName("middlename");
        citizen.setLastName("lastname");
        citizen.setSituationBackground("bakgrund");
        citizen.setSpecialNeeds("behov");
        request.setCitizen(citizen);
        request.setAssessmentId(anII(null, "1"));
        request.setComment("kommentar");
        request.setDocumentsByPost(true);
        request.setNeedForInterpreter(true);
        request.setInterpreterLanguage(aCv("sv", null, null));
        AuthorityAdministrativeOfficialType bestallare = new AuthorityAdministrativeOfficialType();
        bestallare.setAuthority(aCv(FKASSA.name(), TjanstekontraktUtils.KV_MYNDIGHET, null));
        bestallare.setEmail("email");
        bestallare.setFullName("fullstandigtNamn");
        bestallare.setOfficeCostCenter("kostnadsstalle");
        bestallare.setOfficeName("kontor");
        bestallare.setPhoneNumber("telefonnummer");
        AddressType address = new AddressType();
        address.setPostalAddress("adress");
        address.setPostalCity("stad");
        address.setPostalCode("12345");
        bestallare.setOfficeAddress(address);
        request.setAuthorityAdministrativeOfficial(bestallare);
        request.setPlannedActions("atgarder");
        request.setPurpose("syfte");
        request.setCareUnitId(anII(null, "enhet"));
        return request;
    }
}
