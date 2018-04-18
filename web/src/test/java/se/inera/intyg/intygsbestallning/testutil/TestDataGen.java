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
package se.inera.intyg.intygsbestallning.testutil;

import static se.inera.intyg.intygsbestallning.common.util.RivtaTypesUtil.aCv;
import static se.inera.intyg.intygsbestallning.common.util.RivtaTypesUtil.anII;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import se.inera.intyg.infra.integration.hsa.model.SelectableVardenhet;
import se.inera.intyg.infra.integration.hsa.model.Vardenhet;
import se.inera.intyg.infra.integration.hsa.model.Vardgivare;
import se.inera.intyg.infra.security.common.model.Role;
import se.inera.intyg.intygsbestallning.auth.IbUser;
import se.inera.intyg.intygsbestallning.auth.IbUserDetailsService;
import se.inera.intyg.intygsbestallning.auth.authorities.AuthoritiesConstants;
import se.inera.intyg.intygsbestallning.auth.util.SystemRolesParser;
import se.riv.intygsbestallning.certificate.order.requesthealthcareperformerforassessment.v1.RequestHealthcarePerformerForAssessmentType;
import se.riv.intygsbestallning.certificate.order.v1.AddressType;
import se.riv.intygsbestallning.certificate.order.v1.AuthorityAdministrativeOfficialType;
import se.riv.intygsbestallning.certificate.order.v1.CitizenLimitedType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper base class, provides data setup for tests.
 */
public final class TestDataGen {

    public final static LocalDateTime DATE_TIME = LocalDateTime.of(2018, 11, 11, 11, 11, 11);

    private static final String USER_HSA_ID = "user-1";
    private static final String USER_NAME = "Läkar Läkarsson";
    private static final String CAREUNIT_ID = "careunit-1";
    private static final String CAREUNIT_NAME = "Vårdenhet 1";
    private static final String CAREGIVER_ID = "caregiver-1";
    private static final String CAREGIVER_NAME = "Vårdgivare 1";

    private TestDataGen() {

    }

    // CHECKSTYLE:OFF MagicNumber


    public static List<String> buildDiagnosGrupper() {
        List<String> diagnosGrupper = new ArrayList<>();
        diagnosGrupper.add("H00-H59: Sjukdomar i ögat och närliggande organ");
        diagnosGrupper.add("J00-J99: Andningsorganens sjukdomar");
        diagnosGrupper.add("M00-M99: Sjukdomar i muskuloskeletala systemet och bindväven");
        return diagnosGrupper;
    }

    public static List<String> buildPersonnummerList() {
        List<String> personnummerList = new ArrayList<>();
        personnummerList.add("19121212-1212");
        return personnummerList;
    }

    public static List<String> buildDefaultSystemRoles() {
        List<String> systemRoles = new ArrayList<>();
        systemRoles.add(SystemRolesParser.HSA_SYSTEMROLE_FMU_SAMORDNARE_CAREGIVER_PREFIX + "vg1");
        systemRoles.add(SystemRolesParser.HSA_SYSTEMROLE_FMU_SAMORDNARE_CAREGIVER_PREFIX + "vg3");
        systemRoles.add(SystemRolesParser.HSA_SYSTEMROLE_FMU_VARDADMIN_UNIT_PREFIX + "ve11");
        systemRoles.add(SystemRolesParser.HSA_SYSTEMROLE_FMU_VARDADMIN_UNIT_PREFIX + "ve21");
        return systemRoles;
    }


    public static IbUser buildIBVardadminUser() {
        IbUser user = new IbUser(USER_HSA_ID, USER_NAME);
        user.setMiuNamnPerEnhetsId(buildMiUPerEnhetsIdMap());
        user.setTitel("Vårdadministratör");
        Role r = new Role();
        r.setName(AuthoritiesConstants.ROLE_FMU_VARDADMIN);
        user.setRoles(ImmutableMap.of(AuthoritiesConstants.ROLE_FMU_VARDADMIN, r));
        user.setSystemRoles(buildDefaultSystemRoles());
        user.setVardgivare(buildDefaultVardgivareTree());
        new IbUserDetailsService().buildSystemAuthoritiesTree(user);
        return user;
    }

    public static  List<Vardgivare> buildDefaultVardgivareTree() {
        Vardgivare vg1 = new Vardgivare("vg1", "Vårdgivare 1");
        Vardenhet ve11 = new Vardenhet("ve11", "ve11");
        vg1.getVardenheter().add(ve11);

        Vardgivare vg2 = new Vardgivare("vg2", "Vårdgivare 2");
        Vardenhet ve21 = new Vardenhet("ve21", "ve21");
        vg2.getVardenheter().add(ve21);

        Vardgivare vg4 = new Vardgivare("vg4", "Vårdgivare 4");

        List<Vardgivare> vardgivare = new ArrayList<>();
        vardgivare.add(vg1);
        vardgivare.add(vg2);
        vardgivare.add(vg4);
        return vardgivare;
    }

    public static RequestHealthcarePerformerForAssessmentType createFullRequest() {
        CitizenLimitedType citizen = new CitizenLimitedType();
        citizen.setPostalCity(aCv("postalCity", null, null));
        citizen.setSpecialNeeds("specialNeeds");
        citizen.getEarlierAssessmentPerformer().addAll(ImmutableList.of(
                anII("root", "1"),
                anII("root", "2"),
                anII("root", "3")));

        AddressType address = new AddressType();
        address.setPostalAddress("postalAddress");
        address.setPostalCode("postalCode");
        address.setPostalCity("postalCity");

        AuthorityAdministrativeOfficialType authorityAdmin = new AuthorityAdministrativeOfficialType();
        authorityAdmin.setFullName("fullName");
        authorityAdmin.setPhoneNumber("phoneNumber");
        authorityAdmin.setEmail("email");
        authorityAdmin.setAuthority(aCv("authority", null, null));
        authorityAdmin.setOfficeName("officeName");
        authorityAdmin.setOfficeCostCenter("officeCostCenter");
        authorityAdmin.setOfficeAddress(address);

        RequestHealthcarePerformerForAssessmentType request = new RequestHealthcarePerformerForAssessmentType();
        request.setCertificateType(aCv("AFU", null, null));
        request.setLastResponseDate(DATE_TIME.toString());
        request.setCoordinatingCountyCouncilId(anII("root", "coordinatingCountyCouncilId"));
        request.setComment("comment");
        request.setNeedForInterpreter(true);
        request.setInterpreterLanguage(aCv("language", null, null));
        request.setAuthorityAdministrativeOfficial(authorityAdmin);
        request.setCitizen(citizen);

        return request;
    }

    private static Map<String, String> buildMiUPerEnhetsIdMap() {
        Map<String, String> map = new HashMap<>();
        map.put(CAREUNIT_ID, "Läkare på " + CAREUNIT_NAME);
        return map;
    }

    private static SelectableVardenhet buildValdGivare(String hsaId, String namn) {
        return new Vardgivare(hsaId, namn);
    }

    private static SelectableVardenhet buildValdVardenhet(String hsaId, String namn) {
        return new Vardenhet(hsaId, namn);
    }

    // CHECKSTYLE:ON MagicNumber
}
