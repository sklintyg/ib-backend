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

import com.google.common.base.Joiner;
import se.inera.intyg.intygsbestallning.common.exception.IbErrorCodeEnum;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.persistence.model.UtredningsTyp;
import se.riv.intygsbestallning.certificate.order.ordermedicalassessment.v1.OrderMedicalAssessmentType;
import se.riv.intygsbestallning.certificate.order.v1.AuthorityAdministrativeOfficialType;
import se.riv.intygsbestallning.certificate.order.v1.CitizenType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

public final class OrderRequest {
    private String utredningId;
    private String enhetId;
    private UtredningsTyp utredningsTyp;
    private Bestallare bestallare;
    private String invanarePersonnummer;
    private String invanareBakgrund;
    private String invanareBehov;
    private String syfte;
    private String tolkSprak;
    private String kommentar;
    private LocalDate lastDateIntyg;
    private LocalDate orderDate;
    private boolean handling;
    private String atgarder;

    private OrderRequest() {
    }

    public static OrderRequest fromRequest(OrderMedicalAssessmentType source) {
        validate(source);

        AuthorityAdministrativeOfficialType bestallareSource = source.getAuthorityAdministrativeOfficial();
        CitizenType invanareSource = source.getCitizen();

        return OrderRequestBuilder.anOrderRequest()
                .withAtgarder(source.getPlannedActions())
                .withBestallare(Bestallare.BestallareBuilder.aBestallare()
                        .withMyndighet(!isNull(bestallareSource.getAuthority()) ? bestallareSource.getAuthority().getCode() : null)
                        .withEmail(bestallareSource.getEmail())
                        .withFullstandigtNamn(bestallareSource.getFullName())
                        .withAdress(!isNull(bestallareSource.getOfficeAddress())
                                ? bestallareSource.getOfficeAddress().getPostalAddress() : null)
                        .withStad(!isNull(bestallareSource.getOfficeAddress())
                                ? bestallareSource.getOfficeAddress().getPostalCity() : null)
                        .withPostkod(!isNull(bestallareSource.getOfficeAddress())
                                ? bestallareSource.getOfficeAddress().getPostalCode() : null)
                        .withKostnadsstalle(bestallareSource.getOfficeCostCenter())
                        .withKontor(bestallareSource.getOfficeName())
                        .withTelefonnummer(bestallareSource.getPhoneNumber())
                        .build())
                .withEnhetId(source.getCareUnitId().getExtension())
                .withHandling(!isNull(source.isDocumentsByPost()) && source.isDocumentsByPost())
                .withInvanareBakgrund(invanareSource.getSituationBackground())
                .withInvanareBehov(invanareSource.getSpecialNeeds())
                .withInvanarePersonnummer(invanareSource.getPersonalIdentity().getExtension())
                .withKommentar(source.getComment())
                .withLastDateIntyg(!isNull(source.getLastDateForCertificateReceival())
                        ? LocalDate.parse(source.getLastDateForCertificateReceival()) : null)
                .withOrderDate(!isNull(source.getOrderDate())
                        ? LocalDate.parse(source.getOrderDate()) : null)
                .withSyfte(source.getPurpose())
                .withTolkSprak((!isNull(source.isNeedForInterpreter()) && source.isNeedForInterpreter())
                        ? source.getInterpreterLanguage().getCode() : null)
                .withUtredningId(!isNull(source.getAssessmentId()) ? source.getAssessmentId().getExtension() : null)
                .withUtredningsTyp(UtredningsTyp.valueOf(source.getCertificateType().getCode()))
                .build();
    }

    private static void validate(OrderMedicalAssessmentType source) {
        List<String> errors = new ArrayList<>();
        try {
            UtredningsTyp.valueOf(source.getCertificateType().getCode());
        } catch (IllegalArgumentException iae) {
            errors.add("CertificateType is not of a known type");
        }
        if (!isNull(source.isNeedForInterpreter()) && source.isNeedForInterpreter() && source.getInterpreterLanguage() == null) {
            errors.add("InterpreterLanguage is required when the need is declared");
        }
        // if FMU-order
        if (!isNull(source.getAssessmentId())) {
            if (isNull(source.getOrderDate())) {
                errors.add("OrderDate is required when assessmentId is present");
            }
            if (isNull(source.getLastDateForCertificateReceival())) {
                errors.add("LastDateForCertificateReceival is required when assessmentId is present");
            }

        }
        if (!errors.isEmpty()) {
            throw new IbServiceException(IbErrorCodeEnum.BAD_REQUEST, Joiner.on(", ").join(errors));
        }

    }

    public String getUtredningId() {
        return utredningId;
    }

    public String getEnhetId() {
        return enhetId;
    }

    public UtredningsTyp getUtredningsTyp() {
        return utredningsTyp;
    }

    public Bestallare getBestallare() {
        return bestallare;
    }

    public String getInvanarePersonnummer() {
        return invanarePersonnummer;
    }

    public String getInvanareBakgrund() {
        return invanareBakgrund;
    }

    public String getInvanareBehov() {
        return invanareBehov;
    }

    public String getSyfte() {
        return syfte;
    }

    public String getTolkSprak() {
        return tolkSprak;
    }

    public String getKommentar() {
        return kommentar;
    }

    public LocalDate getLastDateIntyg() {
        return lastDateIntyg;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public boolean isHandling() {
        return handling;
    }

    public String getAtgarder() {
        return atgarder;
    }

    private static final class OrderRequestBuilder {
        private String utredningId;
        private String enhetId;
        private UtredningsTyp utredningsTyp;
        private Bestallare bestallare;
        private String invanarePersonnummer;
        private String invanareBakgrund;
        private String invanareBehov;
        private String syfte;
        private String tolkSprak;
        private String kommentar;
        private LocalDate lastDateIntyg;
        private LocalDate orderDate;
        private boolean handling;
        private String atgarder;

        private OrderRequestBuilder() {
        }

        static OrderRequestBuilder anOrderRequest() {
            return new OrderRequestBuilder();
        }

        OrderRequestBuilder withUtredningId(String utredningId) {
            this.utredningId = utredningId;
            return this;
        }

        OrderRequestBuilder withEnhetId(String enhetId) {
            this.enhetId = enhetId;
            return this;
        }

        OrderRequestBuilder withUtredningsTyp(UtredningsTyp utredningsTyp) {
            this.utredningsTyp = utredningsTyp;
            return this;
        }

        OrderRequestBuilder withBestallare(Bestallare bestallare) {
            this.bestallare = bestallare;
            return this;
        }

        OrderRequestBuilder withInvanarePersonnummer(String invanarePersonnummer) {
            this.invanarePersonnummer = invanarePersonnummer;
            return this;
        }

        OrderRequestBuilder withInvanareBakgrund(String invanareBakgrund) {
            this.invanareBakgrund = invanareBakgrund;
            return this;
        }

        OrderRequestBuilder withInvanareBehov(String invanareBehov) {
            this.invanareBehov = invanareBehov;
            return this;
        }

        OrderRequestBuilder withSyfte(String syfte) {
            this.syfte = syfte;
            return this;
        }

        OrderRequestBuilder withTolkSprak(String tolkSprak) {
            this.tolkSprak = tolkSprak;
            return this;
        }

        OrderRequestBuilder withKommentar(String kommentar) {
            this.kommentar = kommentar;
            return this;
        }

        OrderRequestBuilder withLastDateIntyg(LocalDate lastDateIntyg) {
            this.lastDateIntyg = lastDateIntyg;
            return this;
        }

        OrderRequestBuilder withOrderDate(LocalDate orderDate) {
            this.orderDate = orderDate;
            return this;
        }

        OrderRequestBuilder withHandling(boolean handling) {
            this.handling = handling;
            return this;
        }

        OrderRequestBuilder withAtgarder(String atgarder) {
            this.atgarder = atgarder;
            return this;
        }

        public OrderRequest build() {
            OrderRequest orderRequest = new OrderRequest();
            orderRequest.invanarePersonnummer = this.invanarePersonnummer;
            orderRequest.enhetId = this.enhetId;
            orderRequest.handling = this.handling;
            orderRequest.invanareBehov = this.invanareBehov;
            orderRequest.orderDate = this.orderDate;
            orderRequest.tolkSprak = this.tolkSprak;
            orderRequest.kommentar = this.kommentar;
            orderRequest.atgarder = this.atgarder;
            orderRequest.invanareBakgrund = this.invanareBakgrund;
            orderRequest.lastDateIntyg = this.lastDateIntyg;
            orderRequest.utredningsTyp = this.utredningsTyp;
            orderRequest.utredningId = this.utredningId;
            orderRequest.bestallare = this.bestallare;
            orderRequest.syfte = this.syfte;
            return orderRequest;
        }
    }
}
