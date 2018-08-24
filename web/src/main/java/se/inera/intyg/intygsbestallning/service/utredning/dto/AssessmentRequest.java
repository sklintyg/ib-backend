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

import static se.inera.intyg.intygsbestallning.persistence.model.type.UtredningsTyp.AFU;
import static se.inera.intyg.intygsbestallning.persistence.model.type.UtredningsTyp.AFU_UTVIDGAD;
import static se.inera.intyg.intygsbestallning.persistence.model.type.UtredningsTyp.valueOf;
import static se.inera.intyg.intygsbestallning.service.utredning.dto.AssessmentRequest.AssessmentRequestBuilder.anAssessmentRequest;
import static se.inera.intyg.intygsbestallning.service.utredning.dto.Bestallare.BestallareBuilder.aBestallare;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.BooleanUtils;
import se.inera.intyg.intygsbestallning.integration.myndighet.service.TjanstekontraktUtils;
import se.riv.intygsbestallning.certificate.order.requestperformerforassessment.v1.RequestPerformerForAssessmentType;
import se.riv.intygsbestallning.certificate.order.v1.AddressType;
import se.riv.intygsbestallning.certificate.order.v1.AuthorityAdministrativeOfficialType;
import se.riv.intygsbestallning.certificate.order.v1.CVType;
import se.riv.intygsbestallning.certificate.order.v1.CitizenLimitedType;
import se.riv.intygsbestallning.certificate.order.v1.IIType;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import se.inera.intyg.intygsbestallning.common.exception.IbErrorCodeEnum;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.common.util.SchemaDateUtil;
import se.inera.intyg.intygsbestallning.persistence.model.type.UtredningsTyp;

public class AssessmentRequest implements TolkRequest {

    private UtredningsTyp utredningsTyp;
    private LocalDateTime besvaraSenastDatum;
    private String kommentar;
    private boolean tolkBehov;
    private String tolkSprak;
    private Bestallare bestallare;
    private String landstingHsaId;
    private String invanareSarskildaBehov;
    private String invanarePostort;
    private List<String> invanareTidigareUtforare;

    public UtredningsTyp getUtredningsTyp() {
        return utredningsTyp;
    }

    public LocalDateTime getBesvaraSenastDatum() {
        return besvaraSenastDatum;
    }

    public String getKommentar() {
        return kommentar;
    }

    @Override
    public boolean isTolkBehov() {
        return tolkBehov;
    }

    @Override
    public void setTolkBehov(boolean tolkBehov) {
        this.tolkBehov = tolkBehov;
    }

    @Override
    public String getTolkSprak() {
        return tolkSprak;
    }

    @Override
    public void setTolkSprak(String tolkSprak) {
        this.tolkSprak = tolkSprak;
    }

    public Bestallare getBestallare() {
        return bestallare;
    }

    public String getLandstingHsaId() {
        return landstingHsaId;
    }

    public String getInvanareSarskildaBehov() {
        return invanareSarskildaBehov;
    }

    public String getInvanarePostort() {
        return invanarePostort;
    }

    public List<String> getInvanareTidigareUtforare() {
        return invanareTidigareUtforare;
    }

    public static AssessmentRequest from(final RequestPerformerForAssessmentType request) {

        validate(request);

        AuthorityAdministrativeOfficialType bestallareSource = request.getAuthorityAdministrativeOfficial();

        return anAssessmentRequest()
                .withUtredningsTyp(Optional.ofNullable(request.getCertificateType())
                        .map(cvType -> valueOf(cvType.getCode()))
                        .orElse(null))
                .withBesvaraSenastDatum(Optional.ofNullable(request.getLastResponseDate())
                        .map(d -> SchemaDateUtil.toLocalDateTimeFromDateType(request.getLastResponseDate()))
                        .orElse(null))
                .withKommentar(request.getComment())
                .withLandstingHsaId(Optional.ofNullable(request.getCoordinatingCountyCouncilId())
                        .map(IIType::getExtension)
                        .orElse(null))
                .withTolkBehov(BooleanUtils.toBoolean(request.isNeedForInterpreter()))
                .withTolkSprak(Optional.ofNullable(request.getInterpreterLanguage())
                        .map(CVType::getCode)
                        .orElse(null))
                .withInvanarePostort(Optional.ofNullable(request.getCitizen())
                        .map(CitizenLimitedType::getPostalCity)
                        .orElse(null))
                .withInvanareSarskildaBehov(Optional.ofNullable(request.getCitizen())
                        .map(CitizenLimitedType::getSpecialNeeds)
                        .orElse(null))
                .withInvanareTidigareUtforare(Optional.ofNullable(request.getCitizen())
                        .map(cit -> cit.getEarlierAssessmentPerformer()
                                .stream()
                                .map(IIType::getExtension)
                                .collect(Collectors.toList()))
                        .orElse(null))
                .withBestallare(aBestallare()
                        .withMyndighet(Optional.ofNullable(bestallareSource.getAuthority())
                                .map(CVType::getCode)
                                .orElse(null))
                        .withEmail(bestallareSource.getEmail())
                        .withFullstandigtNamn(bestallareSource.getFullName())
                        .withAdress(Optional.ofNullable(bestallareSource.getOfficeAddress())
                                .map(AddressType::getPostalAddress)
                                .orElse(null))
                        .withStad(Optional.ofNullable(bestallareSource.getOfficeAddress())
                                .map(AddressType::getPostalCity)
                                .orElse(null))
                        .withPostnummer(Optional.ofNullable(bestallareSource.getOfficeAddress())
                                .map(AddressType::getPostalCode)
                                .orElse(null))
                        .withKostnadsstalle(bestallareSource.getOfficeCostCenter())
                        .withKontor(bestallareSource.getOfficeName())
                        .withTelefonnummer(bestallareSource.getPhoneNumber())
                        .build())
                .build();
    }

    private static void validate(final RequestPerformerForAssessmentType source) {
        List<String> errors = Lists.newArrayList();
        final List<UtredningsTyp> godkandaUtredningsTyper = ImmutableList.of(AFU, AFU_UTVIDGAD);

        try {
            final UtredningsTyp utredningsTyp = valueOf(source.getCertificateType().getCode());

            boolean korrektTyp = godkandaUtredningsTyper.contains(utredningsTyp);
            if (!korrektTyp) {
                errors.add(MessageFormat.format(
                        "Unknown code: {0} for codeSystem: {1}",
                        source.getCertificateType().getCode(), TjanstekontraktUtils.KV_INTYGSTYP));
            }
        } catch (IllegalArgumentException iae) {

            errors.add(MessageFormat.format(
                    "Unknown code: {0} for codeSystem: {1}",
                    source.getCertificateType().getCode(), TjanstekontraktUtils.KV_INTYGSTYP));
        }

        if (!errors.isEmpty()) {
            throw new IbServiceException(IbErrorCodeEnum.BAD_REQUEST, Joiner.on(", ").join(errors));
        }
    }
    //CHECKSTYLE:OFF OperatorWrap

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final AssessmentRequest that = (AssessmentRequest) o;
        return utredningsTyp == that.utredningsTyp &&
                java.util.Objects.equals(besvaraSenastDatum, that.besvaraSenastDatum) &&
                java.util.Objects.equals(kommentar, that.kommentar) &&
                java.util.Objects.equals(tolkSprak, that.tolkSprak) &&
                java.util.Objects.equals(bestallare, that.bestallare) &&
                java.util.Objects.equals(landstingHsaId, that.landstingHsaId) &&
                java.util.Objects.equals(invanareSarskildaBehov, that.invanareSarskildaBehov) &&
                java.util.Objects.equals(invanarePostort, that.invanarePostort) &&
                java.util.Objects.equals(invanareTidigareUtforare, that.invanareTidigareUtforare);
    }

    @Override
    public int hashCode() {

        return java.util.Objects.hash(utredningsTyp, besvaraSenastDatum, kommentar, tolkSprak, bestallare, landstingHsaId,
                invanareSarskildaBehov, invanarePostort, invanareTidigareUtforare);
    }

    public static final class AssessmentRequestBuilder {
        private UtredningsTyp utredningsTyp;
        private LocalDateTime besvaraSenastDatum;
        private String kommentar;
        private boolean tolkBehov;
        private String tolkSprak;
        private Bestallare bestallare;
        private String landstingHsaId;
        private String invanareSarskildaBehov;
        private String invanarePostort;
        private List<String> invanareTidigareUtforare;

        private AssessmentRequestBuilder() {
        }

        public static AssessmentRequestBuilder anAssessmentRequest() {
            return new AssessmentRequestBuilder();
        }

        public AssessmentRequestBuilder withUtredningsTyp(UtredningsTyp utredningsTyp) {
            this.utredningsTyp = utredningsTyp;
            return this;
        }

        public AssessmentRequestBuilder withBesvaraSenastDatum(LocalDateTime besvaraSenastDatum) {
            this.besvaraSenastDatum = besvaraSenastDatum;
            return this;
        }

        public AssessmentRequestBuilder withKommentar(String kommentar) {
            this.kommentar = kommentar;
            return this;
        }

        public AssessmentRequestBuilder withTolkBehov(boolean tolkBehov) {
            this.tolkBehov = tolkBehov;
            return this;
        }

        public AssessmentRequestBuilder withTolkSprak(String tolkSprak) {
            this.tolkSprak = tolkSprak;
            return this;
        }

        public AssessmentRequestBuilder withBestallare(Bestallare bestallare) {
            this.bestallare = bestallare;
            return this;
        }

        public AssessmentRequestBuilder withLandstingHsaId(String landstingHsaId) {
            this.landstingHsaId = landstingHsaId;
            return this;
        }

        public AssessmentRequestBuilder withInvanareSarskildaBehov(String invanareSarskildaBehov) {
            this.invanareSarskildaBehov = invanareSarskildaBehov;
            return this;
        }

        public AssessmentRequestBuilder withInvanarePostort(String invanarePostort) {
            this.invanarePostort = invanarePostort;
            return this;
        }

        public AssessmentRequestBuilder withInvanareTidigareUtforare(List<String> invanareTidigareUtforare) {
            this.invanareTidigareUtforare = invanareTidigareUtforare;
            return this;
        }

        public AssessmentRequest build() {
            AssessmentRequest assessmentRequest = new AssessmentRequest();
            assessmentRequest.bestallare = this.bestallare;
            assessmentRequest.landstingHsaId = this.landstingHsaId;
            assessmentRequest.utredningsTyp = this.utredningsTyp;
            assessmentRequest.invanareTidigareUtforare = this.invanareTidigareUtforare;
            assessmentRequest.kommentar = this.kommentar;
            assessmentRequest.tolkSprak = this.tolkSprak;
            assessmentRequest.tolkBehov = this.tolkBehov;
            assessmentRequest.invanareSarskildaBehov = this.invanareSarskildaBehov;
            assessmentRequest.invanarePostort = this.invanarePostort;
            assessmentRequest.besvaraSenastDatum = this.besvaraSenastDatum;
            return assessmentRequest;
        }
    }

    //CHECKSTYLE:ON OperatorWrap
}
