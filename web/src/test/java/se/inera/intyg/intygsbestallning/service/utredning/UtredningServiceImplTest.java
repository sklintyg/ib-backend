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
package se.inera.intyg.intygsbestallning.service.utredning;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import se.inera.intyg.infra.integration.hsa.model.Vardgivare;
import se.inera.intyg.infra.integration.hsa.services.HsaOrganizationsService;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.repository.UtredningRepository;
import se.riv.intygsbestallning.certificate.order.requesthealthcareperformerforassessment.v1.RequestHealthcarePerformerForAssessmentType;
import se.riv.intygsbestallning.certificate.order.v1.AddressType;
import se.riv.intygsbestallning.certificate.order.v1.AuthorityAdministrativeOfficialType;
import se.riv.intygsbestallning.certificate.order.v1.CVType;
import se.riv.intygsbestallning.certificate.order.v1.CitizenLimitedType;
import se.riv.intygsbestallning.certificate.order.v1.IIType;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UtredningServiceImplTest {

    private static final String VG_HSA_ID = "vg-1";
    private static final String VG_NAMN = "vg-namn";
    @Mock
    private UtredningRepository utredningRepository;

    @Mock
    private HsaOrganizationsService hsaOrganizationsService;

    @InjectMocks
    private UtredningServiceImpl testee;

    @Test
    public void testRegister() {
        when(hsaOrganizationsService.getVardgivareInfo(anyString())).thenReturn(buildVardgivare());
        when(utredningRepository.save(any(Utredning.class))).thenReturn(new Utredning());

        RequestHealthcarePerformerForAssessmentType req = buildRequest();

        Utredning utredning = testee.registerNewUtredning(req);
        assertNotNull(utredning);
        verify(utredningRepository).save(any(Utredning.class));
    }

    private Vardgivare buildVardgivare() {
        Vardgivare vg = new Vardgivare(VG_HSA_ID, VG_NAMN);
        return vg;
    }

    private RequestHealthcarePerformerForAssessmentType buildRequest() {
        RequestHealthcarePerformerForAssessmentType req = new RequestHealthcarePerformerForAssessmentType();
        req.setCoordinatingCountyCouncilId(buildIIType("vg-1"));
        req.setCertificateType(buildCVType(null, "FMU"));
        req.setLastResponseDate("2018-11-30");
        req.setInterpreterLanguage(buildCVType("en", "Engelska"));
        req.setNeedForInterpreter(true);
        req.setCitizen(buildLimitedCitizen());
        req.setComment("Detta är en kommentar!");
        req.setAuthorityAdministrativeOfficial(buildAuthorityAdmin());
        return req;
    }

    private AuthorityAdministrativeOfficialType buildAuthorityAdmin() {
        AuthorityAdministrativeOfficialType aaot = new AuthorityAdministrativeOfficialType();
        aaot.setAuthority(buildCVType(null, "Försäkringskassan"));
        aaot.setEmail("epost@inera.se");
        aaot.setFullName("Handläggar Handläggarsson");
        aaot.setOfficeAddress(buildAddressType());
        aaot.setPhoneNumber("123-123456");
        return aaot;
    }

    private AddressType buildAddressType() {
        AddressType addressType = new AddressType();
        addressType.setPostalAddress("Postgatan 1");
        addressType.setPostalCode("12345");
        addressType.setPostalCity("Poststaden");
        return addressType;
    }

    private CitizenLimitedType buildLimitedCitizen() {
        CitizenLimitedType citizenLimitedType = new CitizenLimitedType();
        citizenLimitedType.setPostalCity(buildCVType(null, "Stockholm"));
        citizenLimitedType.setSpecialNeeds("Kebab");
        return citizenLimitedType;
    }

    private IIType buildIIType(String extension) {
        IIType iiType = new IIType();
        iiType.setExtension(extension);
        return iiType;
    }

    private CVType buildCVType(String code, String displayName) {
        CVType cv = new CVType();
        cv.setCode(code);
        cv.setDisplayName(displayName);
        return cv;
    }
}
