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
package se.inera.intyg.intygsbestallning.auth;

import org.apache.cxf.staxutils.StaxUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.opensaml.DefaultBootstrap;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.NameID;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.w3c.dom.Document;
import se.inera.intyg.infra.integration.hsa.model.Mottagning;
import se.inera.intyg.infra.integration.hsa.model.UserAuthorizationInfo;
import se.inera.intyg.infra.integration.hsa.model.UserCredentials;
import se.inera.intyg.infra.integration.hsa.model.Vardenhet;
import se.inera.intyg.infra.integration.hsa.model.Vardgivare;
import se.inera.intyg.infra.integration.hsa.services.HsaOrganizationsService;
import se.inera.intyg.infra.integration.hsa.services.HsaPersonService;
import se.inera.intyg.infra.security.authorities.CommonAuthoritiesResolver;
import se.inera.intyg.infra.security.authorities.bootstrap.SecurityConfigurationLoader;
import se.inera.intyg.infra.security.common.exception.GenericAuthenticationException;
import se.inera.intyg.infra.security.common.model.IntygUser;
import se.inera.intyg.infra.security.common.model.Role;
import se.inera.intyg.infra.security.common.model.UserOrigin;
import se.inera.intyg.infra.security.common.model.UserOriginType;
import se.inera.intyg.infra.security.common.service.AuthenticationLogger;
import se.inera.intyg.infra.security.exception.HsaServiceException;
import se.inera.intyg.infra.security.exception.MissingMedarbetaruppdragException;
import se.inera.intyg.intygsbestallning.auth.authorities.AuthoritiesConstants;
import se.inera.intyg.intygsbestallning.auth.authorities.validation.AuthoritiesValidator;
import se.inera.intyg.intygsbestallning.auth.model.IbVardgivare;
import se.inera.intyg.intygsbestallning.auth.util.SystemRolesParser;
import se.inera.intyg.intygsbestallning.persistence.model.AnvandarPreference;
import se.inera.intyg.intygsbestallning.persistence.repository.AnvandarPreferenceRepository;
import se.inera.intyg.intygsbestallning.testutil.TestDataGen;
import se.riv.infrastructure.directory.v1.HsaSystemRoleType;
import se.riv.infrastructure.directory.v1.PaTitleType;
import se.riv.infrastructure.directory.v1.PersonInformationType;

import javax.xml.transform.stream.StreamSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Created by marced on 29/01/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class IbUserDetailsServiceTest {

    protected static final String AUTHORITIES_CONFIGURATION_FILE = "AuthoritiesConfigurationLoaderTest/authorities-test.yaml";
    protected static final String FEATURES_CONFIGURATION_FILE = "AuthoritiesConfigurationLoaderTest/features-test.yaml";

    protected static final SecurityConfigurationLoader CONFIGURATION_LOADER = new SecurityConfigurationLoader(
            AUTHORITIES_CONFIGURATION_FILE, FEATURES_CONFIGURATION_FILE);
    protected static final CommonAuthoritiesResolver AUTHORITIES_RESOLVER = new CommonAuthoritiesResolver();
    protected static final AuthoritiesValidator AUTHORITIES_VALIDATOR = new AuthoritiesValidator();
    private static final String PERSONAL_HSAID = "TST5565594230-106J";

    private static final String VARDGIVARE_HSAID = "IFV1239877878-0001";
    private static final String ENHET_HSAID_1 = "IFV1239877878-103H";
    private static final String ENHET_HSAID_2 = "IFV1239877878-103P";
    private static final String MOTTAGNING_HSAID_1 = "IFV1239877878-103M";
    private static final String MOTTAGNING_HSAID_2 = "IFV1239877878-103N";
    private static final String VARDGIVARE_HSAID2 = "IFV2222";
    private static final String ENHET_HSAID_21 = "IFV_222222111";
    private static final String ENHET_HSAID_22 = "IFV_22";
    private static final String ENHET_HSAID_23 = "IFV_23";

    @InjectMocks
    private IbUserDetailsService userDetailsService = new IbUserDetailsService();

    @Mock
    private HsaOrganizationsService hsaOrganizationsService;

    @Mock
    private HsaPersonService hsaPersonService;

    @Mock
    private UserOrigin userOrigin;

    @Mock
    private AuthenticationLogger monitoringLogService;

    @Mock
    private AnvandarPreferenceRepository anvandarPreferenceRepository;

    @BeforeClass
    public static void setupAuthoritiesConfiguration() throws Exception {

        DefaultBootstrap.bootstrap();

        // Load configuration
        CONFIGURATION_LOADER.afterPropertiesSet();

        // Setup resolver class
        AUTHORITIES_RESOLVER.setConfigurationLoader(CONFIGURATION_LOADER);
    }

    @Before
    public void setup() {
        // Setup a servlet request
        MockHttpServletRequest request = mockHttpServletRequest("/any/path");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        ReflectionTestUtils.setField(userDetailsService, "userOrigin", Optional.of(userOrigin));

        when(hsaPersonService.getHsaPersonInfo(anyString())).thenReturn(Collections.emptyList());
        when(userOrigin.resolveOrigin(request)).thenReturn(UserOriginType.NORMAL.name());
        userDetailsService.setCommonAuthoritiesResolver(AUTHORITIES_RESOLVER);

        AnvandarPreference anvandarPreference = new AnvandarPreference(PERSONAL_HSAID, "user_pdl_consent_given", "true");
        when(anvandarPreferenceRepository.findByHsaIdAndKey(PERSONAL_HSAID, "user_pdl_consent_given")).thenReturn(anvandarPreference);

        when(hsaOrganizationsService.getVardgivareInfo("vg1")).thenReturn(new Vardgivare("vg1", "Vårdgivare 1"));
        when(hsaOrganizationsService.getVardgivareInfo("vg3")).thenReturn(new Vardgivare("vg3", "Vårdgivare 3"));

    }

    @Test
    public void assertLoadsOkWhenHasMatchingSystemRole() throws Exception {
        // given
        SAMLCredential samlCredential = createSamlCredential("saml-assertion-uppdragslos.xml");
        UserCredentials userCredz = new UserCredentials();
        userCredz.getHsaSystemRole().addAll(buildSystemRoles());
        when(hsaOrganizationsService.getAuthorizedEnheterForHosPerson(PERSONAL_HSAID))
                .thenReturn(new UserAuthorizationInfo(userCredz, buildVardgivareList(), buildMiuPerCareUnitMap()));

        setupCallToGetHsaPersonInfoNonDoctor();

        // then
        IntygUser ibUser = (IntygUser) userDetailsService.loadUserBySAML(samlCredential);

        AUTHORITIES_VALIDATOR.given(ibUser).roles(AuthoritiesConstants.ROLE_FMU_VARDADMIN).orThrow();
        assertEquals(4, ibUser.getTotaltAntalVardenheter());
        assertNull(ibUser.getValdVardenhet());

    }

   // @Test
    public void assertSelectsDefaultVardenhetWhenOnlyOneExists() throws Exception {
        // given
        SAMLCredential samlCredential = createSamlCredential("saml-assertion-uppdragslos.xml");
        UserCredentials userCredz = new UserCredentials();
        userCredz.getHsaSystemRole().addAll(buildSystemRoles());

        // Just return one enhet
        Vardgivare vardgivare = new Vardgivare(VARDGIVARE_HSAID, "IFV Testlandsting");
        vardgivare.getVardenheter().add(new Vardenhet(ENHET_HSAID_2, "VårdEnhet2A"));
        List<Vardgivare> vardgivarList = new ArrayList<>();
        vardgivarList.add(vardgivare);

        when(hsaOrganizationsService.getAuthorizedEnheterForHosPerson(PERSONAL_HSAID))
                .thenReturn(new UserAuthorizationInfo(userCredz, vardgivarList, buildMiuPerCareUnitMap()));

        setupCallToGetHsaPersonInfoNonDoctor();

        // then
        IbUser ibUser = (IbUser) userDetailsService.loadUserBySAML(samlCredential);

        AUTHORITIES_VALIDATOR.given(ibUser).roles(AuthoritiesConstants.ROLE_FMU_VARDADMIN).orThrow();
        assertEquals(1, ibUser.getTotaltAntalVardenheter());
        assertEquals(ENHET_HSAID_2 + " should have been selected as valdVardgivare", ENHET_HSAID_2,
                ibUser.getCurrentlyLoggedInAt().getId());
    }


    @Test(expected = GenericAuthenticationException.class)
    public void testGenericAuthenticationExceptionIsThrownWhenNoSamlCredentialsGiven() throws Exception {
        userDetailsService.loadUserBySAML(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetAssertionWithNullThrowsIllegalArgumentException() throws Exception {
        userDetailsService.getAssertion(null);
    }

    @Test(expected = HsaServiceException.class)
    public void testHsaServiceExceptionIsThrownWhenHsaGetPersonThrowsUncheckedException() throws Exception {
        // given
        when(hsaPersonService.getHsaPersonInfo(anyString())).thenThrow(new RuntimeException("some-exception"));
        SAMLCredential samlCredential = createSamlCredential("saml-assertion-uppdragslos.xml");
        setupCallToAuthorizedEnheterForHosPerson();

        // then
        userDetailsService.loadUserBySAML(samlCredential);
    }

    @Test(expected = HsaServiceException.class)
    public void testHsaServiceExceptionIsThrownWhenHsaThrowsException() throws Exception {
        // given
        when(hsaOrganizationsService.getAuthorizedEnheterForHosPerson(PERSONAL_HSAID))
                .thenThrow(new RuntimeException("some-hsa-exception"));
        SAMLCredential samlCredential = createSamlCredential("saml-assertion-uppdragslos.xml");

        // then
        userDetailsService.loadUserBySAML(samlCredential);
    }

    @Test(expected = MissingMedarbetaruppdragException.class)
    public void testMissingMedarbetaruppdragExceptionIsThrownWhenEmployeeHasNoVardgivare() throws Exception {
        // given
        setupCallToGetHsaPersonInfo();
        when(hsaOrganizationsService.getAuthorizedEnheterForHosPerson(PERSONAL_HSAID))
                .thenReturn(new UserAuthorizationInfo(new UserCredentials(), new ArrayList<>(), new HashMap<>()));
        SAMLCredential samlCredential = createSamlCredential("saml-assertion-uppdragslos.xml");

        // then
        userDetailsService.loadUserBySAML(samlCredential);
    }

    @Test(expected = MissingMedarbetaruppdragException.class)
    public void testMissingMedarbetaruppdragExceptionIsThrownWhenEmployeeHasNoMIU() throws Exception {
        // given
        when(hsaOrganizationsService.getAuthorizedEnheterForHosPerson(PERSONAL_HSAID))
                .thenReturn(new UserAuthorizationInfo(new UserCredentials(), new ArrayList<>(), new HashMap<>()));
        setupCallToGetHsaPersonInfo();
        SAMLCredential samlCredential = createSamlCredential("saml-assertion-uppdragslos.xml");

        // then
        userDetailsService.loadUserBySAML(samlCredential);
    }

    // INTYG-2629
    @Test
    public void testUserWithTitleLakareBecomesVardadmin() throws Exception {
        UserCredentials userCredz = new UserCredentials();
        userCredz.getHsaSystemRole().addAll(buildSystemRoles());
        when(hsaOrganizationsService.getAuthorizedEnheterForHosPerson(PERSONAL_HSAID))
                .thenReturn(new UserAuthorizationInfo(userCredz, buildVardgivareList(), buildMiuPerCareUnitMap()));
        setupCallToGetHsaPersonInfoNonDoctor("Läkare");

        SAMLCredential samlCredential = createSamlCredential("saml-assertion-uppdragslos.xml");
        IntygUser ibUser = (IntygUser) userDetailsService.loadUserBySAML(samlCredential);

        AUTHORITIES_VALIDATOR.given(ibUser).roles(AuthoritiesConstants.ROLE_FMU_VARDADMIN).orThrow();

    }

    @Test
    public void testBuildAuthTreeFromPreparedSystemRoles() {
        IbUser ibUser =  prepareUserForSelectTest();





        // Assert that we have an expected tree
        List<IbVardgivare> sa = ibUser.getSystemAuthorities();
        assertEquals(3, sa.size()); // Three VG

        assertEquals("vg1", sa.get(0).getId());
        assertEquals(1, sa.get(0).getVardenheter().size());
        assertTrue(sa.get(0).isSamordnare());
        assertEquals("ve11", sa.get(0).getVardenheter().get(0).getId());

        assertEquals("vg2", sa.get(1).getId());
        assertEquals(1, sa.get(1).getVardenheter().size());
        assertFalse(sa.get(1).isSamordnare());
        assertEquals("ve21", sa.get(1).getVardenheter().get(0).getId());

        assertEquals("vg3", sa.get(2).getId());
        assertEquals(0, sa.get(2).getVardenheter().size());
        assertTrue(sa.get(2).isSamordnare());

    }

    @Test
    public void testSelectVG1() {
        IbUser ibUser = prepareUserForSelectTest();
        ibUser.changeValdVardenhet("vg1");
        assertEquals(AuthoritiesConstants.ROLE_FMU_SAMORDNARE, ibUser.getCurrentRole().getName());
        assertEquals("Vårdgivare 1", ibUser.getCurrentlyLoggedInAt().getName());
    }

    @Test
    public void testSelectVE11() {
        IbUser ibUser = prepareUserForSelectTest();
        ibUser.changeValdVardenhet("ve11");
        assertEquals(AuthoritiesConstants.ROLE_FMU_VARDADMIN, ibUser.getCurrentRole().getName());
        assertEquals("ve11", ibUser.getCurrentlyLoggedInAt().getName());
    }

    @Test
    public void testSelectVE21() {
        IbUser ibUser = prepareUserForSelectTest();
        ibUser.changeValdVardenhet("ve21");
        assertEquals(AuthoritiesConstants.ROLE_FMU_VARDADMIN, ibUser.getCurrentRole().getName());
        assertEquals("ve21", ibUser.getCurrentlyLoggedInAt().getName());
    }

    @Test
    public void testSelectVG3() {
        when(hsaOrganizationsService.getVardgivareInfo(anyString())).thenReturn(new Vardgivare("vg3", "Vårdgivare 3"));
        IbUser ibUser = prepareUserForSelectTest();
        ibUser.changeValdVardenhet("vg3");
        assertEquals(AuthoritiesConstants.ROLE_FMU_SAMORDNARE, ibUser.getCurrentRole().getName());
        assertEquals("Vårdgivare 3", ibUser.getCurrentlyLoggedInAt().getName());
    }


    // Prepare systemRoles and VG->VE tree:
    // SAM VG1 -> VDM VE11
    // VG2 -> VDM VE21
    // SAM VG3          <-- NOT IN MIU
    // VG4 -> VE41      <-- NO SYSTEMROLES HERE!!

    private IbUser prepareUserForSelectTest() {
        IbUser ibUser = new IbUser("id", "name");
        ibUser.setRoles(buildIbRoles());
        buildDefaultIbUserSystemRolesAndTree(ibUser);
        userDetailsService.buildSystemAuthoritiesTree(ibUser);
        return ibUser;
    }


    private Map<String, Role> buildIbRoles() {
        Map<String, Role> ibRoles = new HashMap<>();
        Role r = new Role();
        r.setName(AuthoritiesConstants.ROLE_FMU_SAMORDNARE);
        ibRoles.put(AuthoritiesConstants.ROLE_FMU_SAMORDNARE, r);

        Role r2 = new Role();
        r2.setName(AuthoritiesConstants.ROLE_FMU_VARDADMIN);
        ibRoles.put(AuthoritiesConstants.ROLE_FMU_VARDADMIN, r2);

        return ibRoles;
    }

    private void buildDefaultIbUserSystemRolesAndTree(IbUser ibUser) {
        List<Vardgivare> vardgivare = TestDataGen.buildDefaultVardgivareTree();
        ibUser.setVardgivare(vardgivare);

        // Next, prepare systemroles.
        List<String> systemRoles = TestDataGen.buildDefaultSystemRoles();
        ibUser.setSystemRoles(systemRoles);
    }




    private List<HsaSystemRoleType> buildSystemRoles() {
        return Arrays.asList(SystemRolesParser.HSA_SYSTEMROLE_FMU_VARDADMIN_UNIT_PREFIX + ENHET_HSAID_2,
                SystemRolesParser.HSA_SYSTEMROLE_FMU_VARDADMIN_UNIT_PREFIX + ENHET_HSAID_21,
                SystemRolesParser.HSA_SYSTEMROLE_FMU_VARDADMIN_UNIT_PREFIX + ENHET_HSAID_23)
                .stream()
                .map(s -> {
                    HsaSystemRoleType hsaSystemRole = new HsaSystemRoleType();
                    hsaSystemRole.setRole(s);
                    return hsaSystemRole;
                }).collect(Collectors.toList());
    }

    private SAMLCredential createSamlCredential(String filename) throws Exception {
        Document doc = StaxUtils.read(new StreamSource(new ClassPathResource(
                "IBUserDetailsServiceTest/" + filename).getInputStream()));
        UnmarshallerFactory unmarshallerFactory = Configuration.getUnmarshallerFactory();
        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(Assertion.DEFAULT_ELEMENT_NAME);

        Assertion assertion = (Assertion) unmarshaller.unmarshall(doc.getDocumentElement());
        NameID nameId = assertion.getSubject().getNameID();
        return new SAMLCredential(nameId, assertion, "remoteId", "localId");
    }

    private void setupCallToAuthorizedEnheterForHosPerson() {
        when(hsaOrganizationsService.getAuthorizedEnheterForHosPerson(PERSONAL_HSAID))
                .thenReturn(new UserAuthorizationInfo(new UserCredentials(), buildVardgivareList(), buildMiuPerCareUnitMap()));
    }

    private List<Vardgivare> buildVardgivareList() {
        Vardgivare vardgivare = new Vardgivare(VARDGIVARE_HSAID, "IFV Testlandsting");
        vardgivare.getVardenheter().add(new Vardenhet(ENHET_HSAID_1, "VårdEnhet2A"));

        final Vardenhet enhet2 = new Vardenhet(ENHET_HSAID_2, "Vårdcentralen");
        enhet2.setMottagningar(Arrays.asList(
                new Mottagning(MOTTAGNING_HSAID_1, "onkologi-mottagningen"),
                new Mottagning(MOTTAGNING_HSAID_2, "protes-mottagningen")));

        vardgivare.getVardenheter().add(enhet2);

        return new ArrayList<>(Arrays.asList(vardgivare));
    }

    private Map<String, String> buildMiuPerCareUnitMap() {
        Map<String, String> mius = new HashMap<>();
        mius.put(ENHET_HSAID_1, "Läkare på VårdEnhet2A");
        mius.put(ENHET_HSAID_2, "Stafettläkare på Vårdcentralen");
        return mius;
    }

    private void setupCallToGetHsaPersonInfo() {
        setupCallToGetHsaPersonInfo(AuthoritiesConstants.ROLE_FMU_VARDADMIN);
    }

    private void setupCallToGetHsaPersonInfo(String title) {
        List<String> specs = Arrays.asList("Kirurgi", "Öron-, näs- och halssjukdomar", "Reumatologi");
        List<String> legitimeradeYrkesgrupper = Arrays.asList("Läkare", "Psykoterapeut");
        List<String> befattningar = Collections.emptyList();

        List<PersonInformationType> userTypes = Collections
                .singletonList(buildPersonInformationType(PERSONAL_HSAID, title, specs, legitimeradeYrkesgrupper, befattningar));

        when(hsaPersonService.getHsaPersonInfo(PERSONAL_HSAID)).thenReturn(userTypes);
    }

    private void setupCallToGetHsaPersonInfoNonDoctor() {
        setupCallToGetHsaPersonInfoNonDoctor("");
    }

    private void setupCallToGetHsaPersonInfoNonDoctor(String title) {
        List<String> specs = new ArrayList<>();
        List<String> legitimeradeYrkesgrupper = Arrays.asList("Vårdadministratör");
        List<String> befattningar = Collections.emptyList();

        List<PersonInformationType> userTypes = Collections
                .singletonList(buildPersonInformationType(PERSONAL_HSAID, title, specs, legitimeradeYrkesgrupper, befattningar));

        when(hsaPersonService.getHsaPersonInfo(PERSONAL_HSAID)).thenReturn(userTypes);
    }

    private PersonInformationType buildPersonInformationType(String hsaId, String title, List<String> specialities,
            List<String> legitimeradeYrkesgrupper,
            List<String> befattningar) {

        PersonInformationType type = new PersonInformationType();
        type.setPersonHsaId(hsaId);
        type.setGivenName("Danne");
        type.setMiddleAndSurName("Doktorsson");

        if (title != null) {
            type.setTitle(title);
        }

        if (legitimeradeYrkesgrupper != null && legitimeradeYrkesgrupper.size() > 0) {
            for (String t : legitimeradeYrkesgrupper) {
                type.getHealthCareProfessionalLicence().add(t);
            }
        }

        if (befattningar != null) {
            for (String befattningsKod : befattningar) {
                PaTitleType paTitleType = new PaTitleType();
                paTitleType.setPaTitleCode(befattningsKod);
                type.getPaTitle().add(paTitleType);
            }
        }

        if ((specialities != null) && (specialities.size() > 0)) {
            type.getSpecialityName().addAll(specialities);
        }
        return type;
    }

    private MockHttpServletRequest mockHttpServletRequest(String requestURI) {
        MockHttpServletRequest request = new MockHttpServletRequest();

        if ((requestURI != null) && (requestURI.length() > 0)) {
            request.setRequestURI(requestURI);
        }
        return request;
    }

}
