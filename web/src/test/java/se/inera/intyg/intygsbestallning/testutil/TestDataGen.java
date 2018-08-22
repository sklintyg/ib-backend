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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import se.inera.intyg.infra.integration.hsa.model.SelectableVardenhet;
import se.inera.intyg.infra.integration.hsa.model.Vardenhet;
import se.inera.intyg.infra.integration.hsa.model.Vardgivare;
import se.inera.intyg.infra.security.common.model.Role;
import se.inera.intyg.intygsbestallning.auth.IbUser;
import se.inera.intyg.intygsbestallning.auth.IbUserDetailsService;
import se.inera.intyg.intygsbestallning.auth.authorities.AuthoritiesConstants;
import se.inera.intyg.intygsbestallning.auth.util.SystemRolesParser;
import se.inera.intyg.intygsbestallning.persistence.model.Besok;
import se.inera.intyg.intygsbestallning.persistence.model.Bestallning;
import se.inera.intyg.intygsbestallning.persistence.model.BestallningHistorik;
import se.inera.intyg.intygsbestallning.persistence.model.ExternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.Handlaggare;
import se.inera.intyg.intygsbestallning.persistence.model.Handling;
import se.inera.intyg.intygsbestallning.persistence.model.Intyg;
import se.inera.intyg.intygsbestallning.persistence.model.Invanare;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.status.UtredningStatusResolver;
import se.inera.intyg.intygsbestallning.persistence.model.type.BesokStatusTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.DeltagarProfessionTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.KallelseFormTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.MyndighetTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.TolkStatusTyp;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.besok.RegisterBesokRequest;
import se.riv.intygsbestallning.certificate.order.requestperformerforassessment.v1.RequestPerformerForAssessmentType;
import se.riv.intygsbestallning.certificate.order.updateorder.v1.UpdateOrderType;
import se.riv.intygsbestallning.certificate.order.v1.AddressType;
import se.riv.intygsbestallning.certificate.order.v1.AuthorityAdministrativeOfficialType;
import se.riv.intygsbestallning.certificate.order.v1.CitizenLimitedType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static se.inera.intyg.intygsbestallning.common.util.RivtaTypesUtil.aCv;
import static se.inera.intyg.intygsbestallning.common.util.RivtaTypesUtil.anII;
import static se.inera.intyg.intygsbestallning.persistence.model.Besok.BesokBuilder.aBesok;
import static se.inera.intyg.intygsbestallning.persistence.model.Bestallning.BestallningBuilder.aBestallning;
import static se.inera.intyg.intygsbestallning.persistence.model.ExternForfragan.ExternForfraganBuilder.anExternForfragan;
import static se.inera.intyg.intygsbestallning.persistence.model.Handlaggare.HandlaggareBuilder.aHandlaggare;
import static se.inera.intyg.intygsbestallning.persistence.model.Handling.HandlingBuilder.aHandling;
import static se.inera.intyg.intygsbestallning.persistence.model.Intyg.IntygBuilder.anIntyg;
import static se.inera.intyg.intygsbestallning.persistence.model.Invanare.InvanareBuilder.anInvanare;
import static se.inera.intyg.intygsbestallning.persistence.model.Utredning.UtredningBuilder.anUtredning;
import static se.inera.intyg.intygsbestallning.persistence.model.type.UtredningsTyp.AFU_UTVIDGAD;
import static se.inera.intyg.intygsbestallning.persistence.model.BestallningHistorik.BestallningHistorikBuilder.aBestallningHistorik;
/**
 * Helper base class, provides data setup for tests.
 */
public final class TestDataGen {

    public final static LocalDateTime DATE_TIME = LocalDateTime.now().plusMonths(6L).withHour(0).withMinute(0).withSecond(0).withNano(0);
    public final static LocalDate DATE = LocalDate.now().plusMonths(6L);

    private static final String USER_HSA_ID = "user-1";
    private static final String USER_NAME = "Läkar Läkarsson";
    private static final String CAREUNIT_ID = "careunit-1";
    private static final String CAREUNIT_NAME = "Vårdenhet 1";
    private static final String CAREUNIT_ORGNR = "testorgnr";
    private static final String LANDSTING_ID = "landsting-1";
    private static final Long UTREDNING_ID = 1L;
    private static final String PERSON_ID = "19121212-1212";
    private static final Long BESOK_ID = 1L;

    private TestDataGen() {

    }

    // CHECKSTYLE:OFF MagicNumber

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

    public static List<Vardgivare> buildDefaultVardgivareTree() {
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

    public static RequestPerformerForAssessmentType createFullRequest() {
        CitizenLimitedType citizen = new CitizenLimitedType();
        citizen.setPostalCity("postalCity");
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
        authorityAdmin.setAuthority(aCv(MyndighetTyp.FKASSA.name(), null, null));
        authorityAdmin.setOfficeName("officeName");
        authorityAdmin.setOfficeCostCenter("officeCostCenter");
        authorityAdmin.setOfficeAddress(address);

        RequestPerformerForAssessmentType request = new RequestPerformerForAssessmentType();
        request.setCertificateType(aCv("AFU", null, null));
        request.setLastResponseDate("20181010");
        request.setCoordinatingCountyCouncilId(anII("root", "coordinatingCountyCouncilId"));
        request.setComment("comment");
        request.setNeedForInterpreter(true);
        request.setInterpreterLanguage(aCv("language", null, null));
        request.setAuthorityAdministrativeOfficial(authorityAdmin);
        request.setCitizen(citizen);

        return request;
    }

    public static UpdateOrderType createUpdateOrderType(final Boolean tolkBehov, final String tolkSprak, final Boolean documentsByPost) {
        UpdateOrderType type = new UpdateOrderType();
        type.setAssessmentId(anII("root", "1"));
        type.setComment("kommentar");
        type.setLastDateForCertificateReceival(DATE.format(DateTimeFormatter.BASIC_ISO_DATE));
        type.setNeedForInterpreter(tolkBehov);
        type.setInterpreterLanguage(aCv(tolkSprak, null, null));
        type.setDocumentsByPost(documentsByPost);
        type.setUpdatedAuthorityAdministrativeOfficial(createAuthorityAdministrativeOfficialType());

        return type;
    }

    public static UpdateOrderType createUpdateOrderType() {
        return createUpdateOrderType(true, "sv", false);
    }

    public static AuthorityAdministrativeOfficialType createAuthorityAdministrativeOfficialType() {
        AuthorityAdministrativeOfficialType admin = new AuthorityAdministrativeOfficialType();
        admin.setFullName("fullName");
        admin.setPhoneNumber("phoneNumber");
        admin.setEmail("email");
        admin.setAuthority(aCv("authority", null, null));
        admin.setOfficeName("officeName");
        admin.setOfficeCostCenter("officeCostCenter");
        admin.setOfficeAddress(createAddressType());

        return admin;
    }

    public static AddressType createAddressType() {
        AddressType address = new AddressType();
        address.setPostalAddress("postalAddress");
        address.setPostalCode("postalCode");
        address.setPostalCity("postalCity");
        return address;
    }

    public static Utredning createUtredning() {
        final Utredning utredning = anUtredning()
                .withUtredningId(UTREDNING_ID)
                .withUtredningsTyp(AFU_UTVIDGAD)
                .withExternForfragan(createExternForfragan())
                .withHandlaggare(createHandlaggare())
                .withBestallning(createBestallning())
                .withIntygList(createIntyg())
                .withHandelseList(Lists.newArrayList())
                .withInvanare(createInvanare())
                .build();
        utredning.setStatus(UtredningStatusResolver.resolveStaticStatus(utredning));
        return utredning;
    }

    public static Utredning createUtredningForKompletterandeFragestallningMottagen() {
        Utredning utredning = TestDataGen.createUtredning();
        utredning.setIntygList(ImmutableList.of(
                anIntyg()
                        .withId(1L)
                        .withKomplettering(false)
                        .withMottagetDatum(TestDataGen.DATE_TIME)
                        .withSkickatDatum(TestDataGen.DATE_TIME)
                        .withSistaDatumKompletteringsbegaran(LocalDateTime.now().plusDays(7))
                        .build(),
                anIntyg()
                        .withId(2L)
                        .withKomplettering(true)
                        .withSistaDatum(LocalDateTime.now().plusDays(14))
                        .build()));
        utredning.setStatus(UtredningStatusResolver.resolveStaticStatus(utredning));
        return utredning;
    }

    public static Utredning createUtredningForSkickaKomplettering() {
        Utredning utredning = TestDataGen.createUtredning();
        utredning.setIntygList(ImmutableList.of(
                anIntyg()
                        .withId(1L)
                        .withKomplettering(false)
                        .withMottagetDatum(TestDataGen.DATE_TIME)
                        .withSkickatDatum(TestDataGen.DATE_TIME)
                        .withSistaDatumKompletteringsbegaran(LocalDateTime.now().plusDays(7))
                        .build(),
                anIntyg()
                        .withId(2L)
                        .withKomplettering(true)
                        .withFragestallningMottagenDatum(LocalDateTime.now())
                        .withSistaDatum(LocalDateTime.now().plusDays(14))
                        .build()));
        utredning.setStatus(UtredningStatusResolver.resolveStaticStatus(utredning));
        return utredning;
    }

    private static List<Intyg> createIntyg() {
        return ImmutableList.of(anIntyg()
                .withId(1L)
                .withKomplettering(false)
                .withMottagetDatum(DATE_TIME)
                .withSkickatDatum(DATE_TIME)
                .build());
    }

    public static List<Besok> createBesok() {
        return ImmutableList.of(aBesok()
                .withBesokStatus(BesokStatusTyp.TIDBOKAD_VARDKONTAKT)
                .build());
    }

    public static List<Besok> createBesok(final RegisterBesokRequest request) {
        return ImmutableList.of(aBesok()
                .withId(BESOK_ID)
                .withKallelseDatum(request.getKallelseDatum())
                .withKallelseForm(request.getKallelseForm())
                .withBesokStartTid(LocalDateTime.of(request.getBesokDatum(), request.getBesokStartTid()))
                .withBesokSlutTid(LocalDateTime.of(request.getBesokDatum(), request.getBesokSlutTid()))
                .withDeltagareProfession(request.getProfession())
                .withTolkStatus(request.getTolkStatus())
                .withDeltagareFullstandigtNamn(request.getUtredandeVardPersonalNamn().orElse(null))
                .withBesokStatus(BesokStatusTyp.TIDBOKAD_VARDKONTAKT)
                .build());
    }

    public static Besok createBesok(long besokId) {
        return aBesok()
                .withId(besokId)
                .withBesokStartTid(DATE_TIME)
                .withBesokSlutTid(DATE_TIME.plusHours(1))
                .withDeltagareProfession(DeltagarProfessionTyp.LK)
                .withKallelseDatum(DATE_TIME)
                .withKallelseForm(KallelseFormTyp.BREVKONTAKT)
                .withBesokStatus(BesokStatusTyp.TIDBOKAD_VARDKONTAKT)
                .withTolkStatus(TolkStatusTyp.BOKAT)
                .build();
    }

    public static Handlaggare createHandlaggare() {
        return aHandlaggare()
                .withAdress("address")
                .withEmail("email")
                .withFullstandigtNamn("fullstandigtNamn")
                .withKontor("kontor")
                .withKostnadsstalle("kostnadsstalle")
                .withMyndighet("myndighet")
                .withPostnummer("12345")
                .withStad("stad")
                .withTelefonnummer("telefonnummer")
                .build();
    }

    public static Bestallning createBestallning() {
        return aBestallning()
                .withBestallningHistorik(Lists.newArrayList(aBestallningHistorik()
                        .withDatum(LocalDateTime.now())
                        .withKommentar("2")
                        .build()))
                .withTilldeladVardenhetHsaId(getCareunitId())
                .withTilldeladVardenhetOrgNr(CAREUNIT_ORGNR)
                .build();
    }

    public static ExternForfragan createExternForfragan() {
        return anExternForfragan()
                .withLandstingHsaId(LANDSTING_ID)
                .withInkomDatum(DATE_TIME)
                .withBesvarasSenastDatum(DATE_TIME)
                .withKommentar("kommentar")
                .build();

    }

    public static Invanare createInvanare() {
        return anInvanare()
                .withPersonId(PERSON_ID)
                .build();
    }

    public static List<Handling> createHandling() {
        return ImmutableList.of(aHandling()

                .build());
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

    public static Long getUtredningId() {
        return UTREDNING_ID;
    }

    public static String getCareunitId() {
        return CAREUNIT_ID;
    }

    public static String getLandstingId() {
        return LANDSTING_ID;
    }

    public static String getPersonId() {
        return PERSON_ID;
    }

    // CHECKSTYLE:ON MagicNumber
}
