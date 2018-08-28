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

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static se.inera.intyg.intygsbestallning.common.exception.IbResponderValidationErrorCode.GTA_FEL05;
import static se.inera.intyg.intygsbestallning.service.utredning.dto.Bestallare.BestallareBuilder.aBestallare;
import static se.inera.intyg.intygsbestallning.service.utredning.dto.OrderRequest.OrderRequestBuilder.anOrderRequest;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.primitives.Longs;
import org.apache.commons.lang3.BooleanUtils;
import se.inera.intyg.intygsbestallning.common.exception.IbErrorCodeEnum;
import se.inera.intyg.intygsbestallning.common.exception.IbResponderValidationErrorCode;
import se.inera.intyg.intygsbestallning.common.exception.IbResponderValidationException;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.integration.myndighet.service.TjanstekontraktUtils;
import se.inera.intyg.intygsbestallning.persistence.model.type.MyndighetTyp;
import se.riv.intygsbestallning.certificate.order.orderassessment.v1.OrderAssessmentType;
import se.riv.intygsbestallning.certificate.order.v1.AuthorityAdministrativeOfficialType;
import se.riv.intygsbestallning.certificate.order.v1.CitizenType;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import se.inera.intyg.intygsbestallning.common.util.SchemaDateUtil;
import se.inera.intyg.intygsbestallning.persistence.model.type.UtredningsTyp;
import se.inera.intyg.schemas.contract.Personnummer;

public final class OrderRequest {
    private Long utredningId;
    private String enhetId;
    private UtredningsTyp utredningsTyp;
    private Bestallare bestallare;
    private String invanarePersonnummer;
    private String invanareBakgrund;
    private String invanareBehov;
    private String invanareFornamn;
    private String invanareMellannamn;
    private String invanareEfternamn;
    private String syfte;
    private boolean tolkBehov;
    private String tolkSprak;
    private String kommentar;
    private LocalDate lastDateIntyg;
    private boolean handling;
    private String atgarder;

    private OrderRequest() {
    }

    public static OrderRequest from(OrderAssessmentType source) {
        validate(source);

        AuthorityAdministrativeOfficialType bestallareSource = source.getAuthorityAdministrativeOfficial();
        CitizenType invanareSource = source.getCitizen();

        return anOrderRequest()
                .withAtgarder(source.getPlannedActions())
                .withBestallare(aBestallare()
                        .withMyndighet(nonNull(bestallareSource.getAuthority())
                                ? bestallareSource.getAuthority().getCode()
                                : null)
                        .withEmail(bestallareSource.getEmail())
                        .withFullstandigtNamn(bestallareSource.getFullName())
                        .withAdress(nonNull(bestallareSource.getOfficeAddress())
                                ? bestallareSource.getOfficeAddress().getPostalAddress()
                                : null)
                        .withStad(nonNull(bestallareSource.getOfficeAddress())
                                ? bestallareSource.getOfficeAddress().getPostalCity()
                                : null)
                        .withPostnummer(nonNull(bestallareSource.getOfficeAddress())
                                ? bestallareSource.getOfficeAddress().getPostalCode()
                                : null)
                        .withKostnadsstalle(bestallareSource.getOfficeCostCenter())
                        .withKontor(bestallareSource.getOfficeName())
                        .withTelefonnummer(bestallareSource.getPhoneNumber())
                        .build())
                .withEnhetId(source.getCareUnitId().getExtension())
                .withHandling(nonNull(source.isDocumentsByPost()) && source.isDocumentsByPost())
                .withInvanareBakgrund(invanareSource.getSituationBackground())
                .withInvanareBehov(invanareSource.getSpecialNeeds())
                .withInvanarePersonnummer(invanareSource.getPersonalIdentity().getExtension())
                .withInvanareFornamn(invanareSource.getFirstName())
                .withInvanareMellannamn(invanareSource.getMiddleName())
                .withInvanareEfternamn(invanareSource.getLastName())
                .withKommentar(source.getComment())
                .withLastDateIntyg(nonNull(source.getLastDateForCertificateReceival())
                        ? SchemaDateUtil.toLocalDateFromDateType(source.getLastDateForCertificateReceival())
                        : null)
                .withSyfte(source.getPurpose())
                .withTolkBehov(BooleanUtils.toBoolean(source.isNeedForInterpreter()))
                .withTolkSprak((nonNull(source.isNeedForInterpreter()) && source.isNeedForInterpreter())
                        ? source.getInterpreterLanguage().getCode()
                        : null)
                .withUtredningId(nonNull(source.getAssessmentId())
                        ? Longs.tryParse(source.getAssessmentId().getExtension())
                        : null)
                .withUtredningsTyp(UtredningsTyp.valueOf(source.getCertificateType().getCode()))
                .build();
    }

    private static void validate(OrderAssessmentType source) {

        if (!source.getCertificateType().getCodeSystem().equals(TjanstekontraktUtils.KV_INTYGSTYP)) {
            throw new IbResponderValidationException(IbResponderValidationErrorCode.GTA_FEL01, source.getCertificateType().getCodeSystem());
        }

        try {
            UtredningsTyp.valueOf(source.getCertificateType().getCode());
        } catch (IllegalArgumentException iae) {
            throw new IbResponderValidationException(IbResponderValidationErrorCode.GTA_FEL02, source.getCertificateType().getCode(),
                    TjanstekontraktUtils.KV_INTYGSTYP);
        }

        if (!source.getAuthorityAdministrativeOfficial().getAuthority().getCodeSystem().equals(TjanstekontraktUtils.KV_MYNDIGHET)) {
            throw new IbResponderValidationException(IbResponderValidationErrorCode.GTA_FEL01,
                    source.getAuthorityAdministrativeOfficial().getAuthority().getCodeSystem());
        }
        try {
            final MyndighetTyp myndighetTyp = MyndighetTyp.valueOf(source.getAuthorityAdministrativeOfficial().getAuthority().getCode());

            if (!myndighetTyp.isActive()) {
                throw new IbResponderValidationException(IbResponderValidationErrorCode.GTA_FEL02, myndighetTyp,
                        TjanstekontraktUtils.KV_MYNDIGHET);
            }
        } catch (IllegalArgumentException iae) {
            throw new IbResponderValidationException(IbResponderValidationErrorCode.GTA_FEL02,
                    source.getAuthorityAdministrativeOfficial().getAuthority().getCode(), TjanstekontraktUtils.KV_MYNDIGHET);
        }

        String sourcePersonnummerString = "<null>";
        try {
            sourcePersonnummerString = source.getCitizen().getPersonalIdentity().getExtension();
            final Optional<Personnummer> pIdentity =
                    Personnummer.createPersonnummer(sourcePersonnummerString);

            if (!pIdentity.isPresent()
                    // Since createPersonnummer is lax by design, also check that input matches NORMALIZED personnummer.
                    || !pIdentity.get().getPersonnummer().equals(sourcePersonnummerString)) {
                throw new IbResponderValidationException(GTA_FEL05, sourcePersonnummerString);
            }
        } catch (Exception e) {
            throw new IbResponderValidationException(GTA_FEL05, sourcePersonnummerString);
        }

        List<String> errors = Lists.newArrayList();

        if (nonNull(source.isNeedForInterpreter()) && source.isNeedForInterpreter() && isNull(source.getInterpreterLanguage())) {
            errors.add("InterpreterLanguage is required when the need is declared");
        }

        // if FMU-order
        if (nonNull(source.getAssessmentId())) {
            if (isNull(Longs.tryParse(source.getAssessmentId().getExtension()))) {
                errors.add("AssessmentId is not parseable as a Long");
            }
            if (isNull(source.getLastDateForCertificateReceival())) {
                throw new IbResponderValidationException(IbResponderValidationErrorCode.TA_FEL07);
            }
            if (isNull(source.getCitizen().getFirstName())
                    && isNull(source.getCitizen().getMiddleName())
                    && isNull(source.getCitizen().getLastName())) {
                errors.add("Name is required when assessmentId is present");
            }
        }

        if (!errors.isEmpty()) {
            throw new IbServiceException(IbErrorCodeEnum.BAD_REQUEST, Joiner.on(", ").join(errors));
        }
    }

    public Long getUtredningId() {
        return utredningId;
    }

    public void setUtredningId(final Long utredningId) {
        this.utredningId = utredningId;
    }

    public String getEnhetId() {
        return enhetId;
    }

    public void setEnhetId(final String enhetId) {
        this.enhetId = enhetId;
    }

    public UtredningsTyp getUtredningsTyp() {
        return utredningsTyp;
    }

    public void setUtredningsTyp(final UtredningsTyp utredningsTyp) {
        this.utredningsTyp = utredningsTyp;
    }

    public Bestallare getBestallare() {
        return bestallare;
    }

    public void setBestallare(final Bestallare bestallare) {
        this.bestallare = bestallare;
    }

    public String getInvanarePersonnummer() {
        return invanarePersonnummer;
    }

    public void setInvanarePersonnummer(final String invanarePersonnummer) {
        this.invanarePersonnummer = invanarePersonnummer;
    }

    public String getInvanareBakgrund() {
        return invanareBakgrund;
    }

    public void setInvanareBakgrund(final String invanareBakgrund) {
        this.invanareBakgrund = invanareBakgrund;
    }

    public String getInvanareBehov() {
        return invanareBehov;
    }

    public void setInvanareBehov(final String invanareBehov) {
        this.invanareBehov = invanareBehov;
    }

    public String getSyfte() {
        return syfte;
    }

    public void setSyfte(final String syfte) {
        this.syfte = syfte;
    }

    public boolean isTolkBehov() {
        return tolkBehov;
    }

    public void setTolkBehov(final boolean tolkBehov) {
        this.tolkBehov = tolkBehov;
    }

    public String getTolkSprak() {
        return tolkSprak;
    }

    public void setTolkSprak(final String tolkSprak) {
        this.tolkSprak = tolkSprak;
    }

    public String getKommentar() {
        return kommentar;
    }

    public void setKommentar(final String kommentar) {
        this.kommentar = kommentar;
    }

    public LocalDate getLastDateIntyg() {
        return lastDateIntyg;
    }

    public void setLastDateIntyg(final LocalDate lastDateIntyg) {
        this.lastDateIntyg = lastDateIntyg;
    }

    public boolean isHandling() {
        return handling;
    }

    public void setHandling(final boolean handling) {
        this.handling = handling;
    }

    public String getAtgarder() {
        return atgarder;
    }

    public void setAtgarder(final String atgarder) {
        this.atgarder = atgarder;
    }

    public String getInvanareFornamn() {
        return invanareFornamn;
    }

    public void setInvanareFornamn(String invanareFornamn) {
        this.invanareFornamn = invanareFornamn;
    }

    public String getInvanareMellannamn() {
        return invanareMellannamn;
    }

    public void setInvanareMellannamn(String invanareMellannamn) {
        this.invanareMellannamn = invanareMellannamn;
    }

    public String getInvanareEfternamn() {
        return invanareEfternamn;
    }

    public void setInvanareEfternamn(String invanareEfternamn) {
        this.invanareEfternamn = invanareEfternamn;
    }


    public static final class OrderRequestBuilder {
        private Long utredningId;
        private String enhetId;
        private UtredningsTyp utredningsTyp;
        private Bestallare bestallare;
        private String invanarePersonnummer;
        private String invanareBakgrund;
        private String invanareBehov;
        private String invanareFornamn;
        private String invanareMellannamn;
        private String invanareEfternamn;
        private String syfte;
        private boolean tolkBehov;
        private String tolkSprak;
        private String kommentar;
        private LocalDate lastDateIntyg;
        private boolean handling;
        private String atgarder;

        private OrderRequestBuilder() {
        }

        public static OrderRequestBuilder anOrderRequest() {
            return new OrderRequestBuilder();
        }

        public OrderRequestBuilder withUtredningId(Long utredningId) {
            this.utredningId = utredningId;
            return this;
        }

        public OrderRequestBuilder withEnhetId(String enhetId) {
            this.enhetId = enhetId;
            return this;
        }

        public OrderRequestBuilder withUtredningsTyp(UtredningsTyp utredningsTyp) {
            this.utredningsTyp = utredningsTyp;
            return this;
        }

        public OrderRequestBuilder withBestallare(Bestallare bestallare) {
            this.bestallare = bestallare;
            return this;
        }

        public OrderRequestBuilder withInvanarePersonnummer(String invanarePersonnummer) {
            this.invanarePersonnummer = invanarePersonnummer;
            return this;
        }

        public OrderRequestBuilder withInvanareBakgrund(String invanareBakgrund) {
            this.invanareBakgrund = invanareBakgrund;
            return this;
        }

        public OrderRequestBuilder withInvanareBehov(String invanareBehov) {
            this.invanareBehov = invanareBehov;
            return this;
        }

        public OrderRequestBuilder withInvanareFornamn(String invanareFornamn) {
            this.invanareFornamn = invanareFornamn;
            return this;
        }

        public OrderRequestBuilder withInvanareMellannamn(String invanareMellannamn) {
            this.invanareMellannamn = invanareMellannamn;
            return this;
        }

        public OrderRequestBuilder withInvanareEfternamn(String invanareEfternamn) {
            this.invanareEfternamn = invanareEfternamn;
            return this;
        }

        public OrderRequestBuilder withSyfte(String syfte) {
            this.syfte = syfte;
            return this;
        }

        public OrderRequestBuilder withTolkBehov(boolean tolkBehov) {
            this.tolkBehov = tolkBehov;
            return this;
        }

        public OrderRequestBuilder withTolkSprak(String tolkSprak) {
            this.tolkSprak = tolkSprak;
            return this;
        }

        public OrderRequestBuilder withKommentar(String kommentar) {
            this.kommentar = kommentar;
            return this;
        }

        public OrderRequestBuilder withLastDateIntyg(LocalDate lastDateIntyg) {
            this.lastDateIntyg = lastDateIntyg;
            return this;
        }

        public OrderRequestBuilder withHandling(boolean handling) {
            this.handling = handling;
            return this;
        }

        public OrderRequestBuilder withAtgarder(String atgarder) {
            this.atgarder = atgarder;
            return this;
        }

        public OrderRequest build() {
            OrderRequest orderRequest = new OrderRequest();
            orderRequest.setUtredningId(utredningId);
            orderRequest.setEnhetId(enhetId);
            orderRequest.setUtredningsTyp(utredningsTyp);
            orderRequest.setBestallare(bestallare);
            orderRequest.setInvanarePersonnummer(invanarePersonnummer);
            orderRequest.setInvanareBakgrund(invanareBakgrund);
            orderRequest.setInvanareBehov(invanareBehov);
            orderRequest.setInvanareFornamn(invanareFornamn);
            orderRequest.setInvanareMellannamn(invanareMellannamn);
            orderRequest.setInvanareEfternamn(invanareEfternamn);
            orderRequest.setSyfte(syfte);
            orderRequest.setTolkBehov(tolkBehov);
            orderRequest.setTolkSprak(tolkSprak);
            orderRequest.setKommentar(kommentar);
            orderRequest.setLastDateIntyg(lastDateIntyg);
            orderRequest.setHandling(handling);
            orderRequest.setAtgarder(atgarder);
            return orderRequest;
        }
    }
}
