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

import static se.inera.intyg.intygsbestallning.persistence.model.UtredningsTyp.AFU;
import static se.inera.intyg.intygsbestallning.persistence.model.UtredningsTyp.AFU_UTVIDGAD;
import static se.inera.intyg.intygsbestallning.persistence.model.UtredningsTyp.valueOf;
import static se.inera.intyg.intygsbestallning.service.utredning.dto.AssessmentRequest.AssessmentRequestBuilder.anAssessmentRequest;
import static se.inera.intyg.intygsbestallning.service.utredning.dto.Bestallare.BestallareBuilder.aBestallare;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import se.inera.intyg.intygsbestallning.common.exception.IbErrorCodeEnum;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.persistence.model.UtredningsTyp;
import se.riv.intygsbestallning.certificate.order.requesthealthcareperformerforassessment.v1.RequestHealthcarePerformerForAssessmentType;
import se.riv.intygsbestallning.certificate.order.v1.AddressType;
import se.riv.intygsbestallning.certificate.order.v1.AuthorityAdministrativeOfficialType;
import se.riv.intygsbestallning.certificate.order.v1.CVType;
import se.riv.intygsbestallning.certificate.order.v1.IIType;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AssessmentRequest {

    private UtredningsTyp utredningsTyp;
    private LocalDateTime besvaraSenastDatum;
    private String kommentar;
    private boolean tolkBehov;
    private String tolkSprak;
    private Bestallare bestallare;
    private String landstingHsaId;
    private String invanareSarskildaBehov;
    private String invanarePostkod;
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

    public boolean isTolkBehov() {
        return tolkBehov;
    }

    public String getTolkSprak() {
        return tolkSprak;
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

    public String getInvanarePostkod() {
        return invanarePostkod;
    }

    public List<String> getInvanareTidigareUtforare() {
        return invanareTidigareUtforare;
    }

    public static AssessmentRequest from(final RequestHealthcarePerformerForAssessmentType request) {

        validate(request);

        AuthorityAdministrativeOfficialType bestallareSource = request.getAuthorityAdministrativeOfficial();

        return anAssessmentRequest()
                .withUtredningsTyp(Optional.ofNullable(request.getCertificateType())
                        .map(cvType -> valueOf(cvType.getCode()))
                        .orElse(null))
                .withBesvaraSenastDatum(Optional.ofNullable(request.getLastResponseDate())
                        .map(date -> LocalDateTime.parse(date))
                        .orElse(null))
                .withKommentar(request.getComment())
                .withLandstingHsaId(Optional.ofNullable(request.getCoordinatingCountyCouncilId())
                        .map(IIType::getExtension)
                        .orElse(null))
                .withTolkSprak(Optional.ofNullable(request.getInterpreterLanguage())
                        .map(cvType -> cvType.getCode())
                        .orElse(null))
                .withInvanarePostkod(Optional.ofNullable(request.getCitizen())
                        .map(cit -> cit.getPostalCity())
                        .map(cvType -> cvType.getCode())
                        .orElse(null))
                .withInvanareSarskildaBehov(Optional.ofNullable(request.getCitizen())
                        .map(cit -> cit.getSpecialNeeds())
                        .orElse(null))
                .withInvanareTidigareUtforare(Optional.ofNullable(request.getCitizen())
                        .map(cit -> cit.getEarlierAssessmentPerformer()
                                .stream()
                                .map(iiType -> iiType.getExtension())
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
                        .withPostkod(Optional.ofNullable(bestallareSource.getOfficeAddress())
                                .map(AddressType::getPostalCode)
                                .orElse(null))
                        .withKostnadsstalle(bestallareSource.getOfficeCostCenter())
                        .withKontor(bestallareSource.getOfficeName())
                        .withTelefonnummer(bestallareSource.getPhoneNumber())
                        .build())
                .build();
    }

    private static void validate(final RequestHealthcarePerformerForAssessmentType source) {
        List<String> errors = Lists.newArrayList();
        final List<UtredningsTyp> godkandaUtredningsTyper = ImmutableList.of(AFU, AFU_UTVIDGAD);

        try {
            final UtredningsTyp utredningsTyp = valueOf(source.getCertificateType().getCode());

            boolean korrektTyp = godkandaUtredningsTyper.contains(utredningsTyp);
            if (!korrektTyp) {
                errors.add(MessageFormat.format(
                        "CertificateType: {0} is not a valid a valid type. Use one of the following types: {1})",
                        utredningsTyp,
                        godkandaUtredningsTyper));
            }
        } catch (IllegalArgumentException iae) {

            errors.add(MessageFormat.format(
                    "CertificateType: {0} is not of a known type",
                    source.getCertificateType().getCode()));
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
                java.util.Objects.equals(invanarePostkod, that.invanarePostkod) &&
                java.util.Objects.equals(invanareTidigareUtforare, that.invanareTidigareUtforare);
    }

    @Override
    public int hashCode() {

        return java.util.Objects.hash(utredningsTyp, besvaraSenastDatum, kommentar, tolkSprak, bestallare, landstingHsaId,
                invanareSarskildaBehov, invanarePostkod, invanareTidigareUtforare);
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
        private String invanarePostkod;
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

        public AssessmentRequestBuilder withInvanarePostkod(String invanarePostkod) {
            this.invanarePostkod = invanarePostkod;
            return this;
        }

        public AssessmentRequestBuilder withInvanareTidigareUtforare(List<String> invanareTidigareUtforare) {
            this.invanareTidigareUtforare = invanareTidigareUtforare;
            return this;
        }

        public AssessmentRequest build() {
            AssessmentRequest assessmentRequest = new AssessmentRequest();
            assessmentRequest.bestallare = this.bestallare;
            assessmentRequest.utredningsTyp = this.utredningsTyp;
            assessmentRequest.tolkSprak = this.tolkSprak;
            assessmentRequest.landstingHsaId = this.landstingHsaId;
            assessmentRequest.invanareSarskildaBehov = this.invanareSarskildaBehov;
            assessmentRequest.invanareTidigareUtforare = this.invanareTidigareUtforare;
            assessmentRequest.tolkBehov = this.tolkBehov;
            assessmentRequest.kommentar = this.kommentar;
            assessmentRequest.invanarePostkod = this.invanarePostkod;
            assessmentRequest.besvaraSenastDatum = this.besvaraSenastDatum;
            return assessmentRequest;
        }
    }

    //CHECKSTYLE:ON OperatorWrap
}
