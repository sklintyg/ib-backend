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
package se.inera.intyg.intygsbestallning.integration.myndighet.service;

import static se.inera.intyg.intygsbestallning.common.util.RivtaTypesUtil.aCv;
import static se.inera.intyg.intygsbestallning.common.util.RivtaTypesUtil.anII;

import se.riv.intygsbestallning.certificate.order.reportcarecontact.v1.ReportCareContactType;
import se.riv.intygsbestallning.certificate.order.reportdeviation.v1.ReportDeviationType;
import se.riv.intygsbestallning.certificate.order.respondtoperformerrequest.v1.RespondToPerformerRequestType;
import se.riv.intygsbestallning.certificate.order.v1.AddressType;
import se.riv.intygsbestallning.certificate.order.v1.CareUnitType;
import se.riv.intygsbestallning.certificate.order.v1.PerformerRequestResponseType;
import se.riv.intygsbestallning.certificate.order.v1.TimePeriodType;
import java.time.LocalDateTime;
import se.inera.intyg.intygsbestallning.common.util.SchemaDateUtil;
import se.inera.intyg.intygsbestallning.integration.myndighet.dto.ReportCareContactRequestDto;
import se.inera.intyg.intygsbestallning.integration.myndighet.dto.ReportDeviationRequestDto;
import se.inera.intyg.intygsbestallning.integration.myndighet.dto.RespondToPerformerRequestDto;

public final class TjanstekontraktUtils {

    private static final String HSA_ID_ROOT = "1.2.752.129.2.1.4.1";

    private static final String KV_SVAR_BESTALLNING_CODESYSTEM = "d9d51e92-e2c0-49d8-bbec-3fd7e1b60c85";
    private static final String KV_ORSAKAT_AV_CODE_SYSTEM = "be9c99a7-25ff-432b-9ba4-3a74bc45e2be";
    private static final String KV_TOLK_STATUS = "c074d6f5-fc15-4c10-bdd5-115e29888ff5";
    private static final String KV_DELTAGANDE_PROFESSION = "1.2.752.129.2.2.1.4";
    private static final String KV_SNOMED_CT = "1.2.752.116.2.1.1";

    public static final String KV_INTYGSTYP = "b64ea353-e8f6-4832-b563-fc7d46f29548";
    public static final String KV_MYNDIGHET = "769bb12b-bd9f-4203-a5cd-fd14f2eb3b80";

    private TjanstekontraktUtils() {
    }

    public static RespondToPerformerRequestType aRespondToPerformerRequest(final String sourceSystemHsaId,
                                                                           final RespondToPerformerRequestDto dto) {
        RespondToPerformerRequestType request = new RespondToPerformerRequestType();
        request.setAssessmentId(anII(sourceSystemHsaId, dto.getAssessmentId().toString()));
        PerformerRequestResponseType performerRequestResponseType = new PerformerRequestResponseType();
        performerRequestResponseType.setComment(dto.getComment());
        if (dto.getCareUnitId() != null || dto.getCareGiverId() != null) {
            CareUnitType careUnitType = new CareUnitType();
            careUnitType.setCareGiverId(anII(HSA_ID_ROOT, dto.getCareGiverId()));
            careUnitType.setCareGiverName(dto.getCareGiverName());
            careUnitType.setCareUnitId(anII(HSA_ID_ROOT, dto.getCareUnitId()));
            careUnitType.setCareUnitName(dto.getCareUnitName());
            careUnitType.setPhoneNumber(dto.getPhoneNumber());
            careUnitType.setEmail(dto.getEmail());
            careUnitType.setSubcontractorName(dto.getSubcontractorName());
            careUnitType.setPostalAddress(anAddressType(dto.getPostalAddress(), dto.getPostalCity(), dto.getPostalCode()));
            performerRequestResponseType.setPerformerCareUnit(careUnitType);
        }
        performerRequestResponseType.setResponse(aCv(dto.getResponseCode(), KV_SVAR_BESTALLNING_CODESYSTEM, null));
        request.setResponse(performerRequestResponseType);
        return request;
    }

    public static ReportCareContactType aReportCareContact(final String sourceSystemHsaId, final ReportCareContactRequestDto dto) {
        ReportCareContactType request = new ReportCareContactType();
        request.setAssessmentId(anII(sourceSystemHsaId, dto.getAssessmentId().toString()));
        request.setAssessmentCareContactId(anII(sourceSystemHsaId, dto.getAssessmentCareContactId()));
        request.setParticipatingProfession(aCv(dto.getParticipatingProfession(), KV_DELTAGANDE_PROFESSION, null));
        if (dto.getInterpreterStatus() != null) {
            request.setInterpreterStatus(aCv(dto.getInterpreterStatus(), KV_TOLK_STATUS, null));
        }
        request.setInvitationDate(dto.getInvitationDate());
        request.setInvitationChannel(aCv(dto.getInvitationChannel(), KV_SNOMED_CT, null));
        request.setTime(aTimePeriod(dto.getStartTime(), dto.getEndTime()));
        request.setVisitStatus(aCv(dto.getVisitStatus(), KV_SNOMED_CT, null));
        return request;
    }

    public static ReportDeviationType aReportDeviation(final String sourceSystemHsaId, final ReportDeviationRequestDto dto) {
        ReportDeviationType request = new ReportDeviationType();
        request.setAssessmentCareContactId(anII(sourceSystemHsaId, dto.getBesokId()));
        request.setDeviationId(anII(sourceSystemHsaId, dto.getAvvikelseId()));
        request.setCausedBy(aCv(dto.getOrsakatAv(), KV_ORSAKAT_AV_CODE_SYSTEM, null));
        request.setDescription(dto.getBeskrivning());
        request.setDeviationTime(dto.getTidpunkt());
        request.setCitizenFailedToArrive(dto.getInvanareUteblev());
        return request;
    }

    public static AddressType anAddressType(final String postalAddress, final String postalCity, final String postalCode) {
        AddressType adressType = new AddressType();
        adressType.setPostalAddress(postalAddress);
        adressType.setPostalCity(postalCity);
        adressType.setPostalCode(postalCode);
        return adressType;
    }

    public static TimePeriodType aTimePeriod(final LocalDateTime start, final LocalDateTime end) {
        TimePeriodType period = new TimePeriodType();
        period.setStart(SchemaDateUtil.toDateTimeStringFromLocalDateTime(start));
        period.setEnd(SchemaDateUtil.toDateTimeStringFromLocalDateTime(end));
        return period;
    }
}
