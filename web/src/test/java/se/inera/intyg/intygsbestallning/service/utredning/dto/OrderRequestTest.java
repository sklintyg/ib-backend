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

import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import se.inera.intyg.intygsbestallning.common.exception.IbErrorCodeEnum;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.riv.intygsbestallning.certificate.order.ordermedicalassessment.v1.OrderMedicalAssessmentType;
import se.riv.intygsbestallning.certificate.order.v1.AddressType;
import se.riv.intygsbestallning.certificate.order.v1.AuthorityAdministrativeOfficialType;
import se.riv.intygsbestallning.certificate.order.v1.CitizenType;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static se.inera.intyg.intygsbestallning.common.exception.IbErrorCodeEnum.BAD_REQUEST;
import static se.inera.intyg.intygsbestallning.common.util.RivtaTypesUtil.aCv;
import static se.inera.intyg.intygsbestallning.common.util.RivtaTypesUtil.anII;
import static se.inera.intyg.intygsbestallning.persistence.model.type.UtredningsTyp.AFU;

public class OrderRequestTest {

    @Test
    public void testFromRequestComplete() {
        OrderMedicalAssessmentType request = createFullRequest();

        OrderRequest result = OrderRequest.from(request);
        assertNotNull(result);
        assertEquals("atgarder", result.getAtgarder());
        assertEquals("enhet", result.getEnhetId());
        assertEquals("bakgrund", result.getInvanareBakgrund());
        assertEquals("behov", result.getInvanareBehov());
        assertEquals("personnummer", result.getInvanarePersonnummer());
        assertEquals("kommentar", result.getKommentar());
        assertEquals("syfte", result.getSyfte());
        assertEquals("sv", result.getTolkSprak());
        assertEquals(Long.valueOf(1L), result.getUtredningId());
        assertEquals(LocalDate.of(2019, 1, 1), result.getLastDateIntyg());
        assertEquals(LocalDate.of(2018, 1, 1), result.getOrderDate());
        assertEquals(AFU, result.getUtredningsTyp());
        assertEquals("adress", result.getBestallare().getAdress());
        assertEquals("email", result.getBestallare().getEmail());
        assertEquals("fullstandigtNamn", result.getBestallare().getFullstandigtNamn());
        assertEquals("kontor", result.getBestallare().getKontor());
        assertEquals("kostnadsstalle", result.getBestallare().getKostnadsstalle());
        assertEquals("myndighet", result.getBestallare().getMyndighet());
        assertEquals("12345", result.getBestallare().getPostnummer());
        assertEquals("stad", result.getBestallare().getStad());
        assertEquals("telefonnummer", result.getBestallare().getTelefonnummer());
        assertEquals("firstname middlename lastname", result.getInvanareFullstandigtNamn());
    }

    @Test
    public void testFromRequestAf() {
        OrderMedicalAssessmentType request = createFullRequest();
        request.setAssessmentId(null);
        request.setOrderDate(null);
        request.setLastDateForCertificateReceival(null);

        OrderRequest result = OrderRequest.from(request);
        assertNotNull(result);
        assertEquals("atgarder", result.getAtgarder());
        assertEquals("enhet", result.getEnhetId());
        assertEquals("bakgrund", result.getInvanareBakgrund());
        assertEquals("behov", result.getInvanareBehov());
        assertEquals("personnummer", result.getInvanarePersonnummer());
        assertEquals("kommentar", result.getKommentar());
        assertEquals("syfte", result.getSyfte());
        assertEquals("sv", result.getTolkSprak());
        assertNull(result.getUtredningId());
        assertNull(result.getLastDateIntyg());
        assertNull(result.getOrderDate());
        assertEquals(AFU, result.getUtredningsTyp());
        assertEquals("adress", result.getBestallare().getAdress());
        assertEquals("email", result.getBestallare().getEmail());
        assertEquals("fullstandigtNamn", result.getBestallare().getFullstandigtNamn());
        assertEquals("kontor", result.getBestallare().getKontor());
        assertEquals("kostnadsstalle", result.getBestallare().getKostnadsstalle());
        assertEquals("myndighet", result.getBestallare().getMyndighet());
        assertEquals("12345", result.getBestallare().getPostnummer());
        assertEquals("stad", result.getBestallare().getStad());
        assertEquals("telefonnummer", result.getBestallare().getTelefonnummer());
    }

    @Test(expected = IbServiceException.class)
    public void testConvertFailCertificateType() {
        OrderMedicalAssessmentType request = createFullRequest();
        request.setCertificateType(aCv("notExisting", null, null));

        assertErrorCode(request, BAD_REQUEST);
    }

    @Test(expected = IbServiceException.class)
    public void testConvertFailOrderDate() {
        OrderMedicalAssessmentType request = createFullRequest();
        request.setOrderDate(null);

        assertErrorCode(request, BAD_REQUEST);
    }

    @Test(expected = IbServiceException.class)
    public void testConvertFailOrderNameMissing() {
        OrderMedicalAssessmentType request = createFullRequest();
        request.getCitizen().setFirstName(null);
        request.getCitizen().setMiddleName(null);
        request.getCitizen().setLastName(null);

        assertErrorCode(request, BAD_REQUEST);
    }

    @Test(expected = IbServiceException.class)
    public void testConvertFailIntygSentDate() {
        OrderMedicalAssessmentType request = createFullRequest();
        request.setLastDateForCertificateReceival(null);

        assertErrorCode(request, BAD_REQUEST);
    }

    @NotNull
    private OrderMedicalAssessmentType createFullRequest() {
        OrderMedicalAssessmentType request = new OrderMedicalAssessmentType();
        request.setCertificateType(aCv(AFU.name(), null, null));
        request.setOrderDate("2018-01-01");
        request.setLastDateForCertificateReceival("2019-01-01");
        CitizenType citizen = new CitizenType();
        citizen.setPersonalIdentity(anII(null, "personnummer"));
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
        bestallare.setAuthority(aCv("myndighet", null, null));
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

    private void assertErrorCode(OrderMedicalAssessmentType request,
            IbErrorCodeEnum errorCodeEnum) {
        try {
            OrderRequest.from(request);
        } catch (IbServiceException ise) {
            assertEquals(errorCodeEnum, ise.getErrorCode());
            assertNotNull(ise.getMessage());
            throw ise;
        }
    }
}
