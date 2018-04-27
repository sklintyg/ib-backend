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

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Objects.nonNull;
import static se.inera.intyg.intygsbestallning.service.utredning.dto.Bestallare.BestallareBuilder;
import static se.inera.intyg.intygsbestallning.service.utredning.dto.Bestallare.BestallareBuilder.aBestallare;
import static se.inera.intyg.intygsbestallning.service.utredning.dto.UpdateOrderRequest.UpdateOrderRequestBuilder.anUpdateOrderRequest;

import se.inera.intyg.intygsbestallning.common.exception.IbErrorCodeEnum;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.riv.intygsbestallning.certificate.order.updateorder.v1.UpdateOrderType;

import java.time.LocalDateTime;
import java.util.Optional;

public class UpdateOrderRequest {

    public static final String INTERPRETER_ERROR_TEXT = "May not set interpreter language if there is no need for interpreter";

    private String utredningId;
    private String kommentar;
    private Bestallare bestallare;
    private Boolean tolkBehov;
    private String tolkSprak;
    private LocalDateTime lastDateIntyg;
    private Boolean handling;

    public static UpdateOrderRequest from(final UpdateOrderType request) {

        UpdateOrderRequestBuilder updateOrderRequestBuilder = anUpdateOrderRequest()
                .withUtredningId(request.getAssessmentId().getExtension())
                .withKommentar(request.getComment())
                .withHandling(request.isDocumentsByPost());

        Optional.ofNullable(request.getLastDateForCertificateReceival())
                .map(LocalDateTime::parse)
                .map(updateOrderRequestBuilder::withLastDateIntyg);

        Optional.ofNullable(request.getUpdatedAuthorityAdministrativeOfficial()).ifPresent(admin -> {
            BestallareBuilder updateBestallareBuilder = aBestallare();

            Optional.ofNullable(admin.getEmail()).ifPresent(updateBestallareBuilder::withEmail);
            Optional.ofNullable(admin.getFullName()).ifPresent(updateBestallareBuilder::withFullstandigtNamn);
            Optional.ofNullable(admin.getPhoneNumber()).ifPresent(updateBestallareBuilder::withTelefonnummer);
            Optional.ofNullable(admin.getOfficeCostCenter()).ifPresent(updateBestallareBuilder::withKostnadsstalle);
            Optional.ofNullable(admin.getOfficeName()).ifPresent(updateBestallareBuilder::withKontor);

            Optional.ofNullable(admin.getAuthority()).ifPresent(typ -> updateBestallareBuilder.withMyndighet(typ.getCode()));

            Optional.ofNullable(admin.getOfficeAddress()).ifPresent(typ -> updateBestallareBuilder.withAdress(typ.getPostalAddress()));
            Optional.ofNullable(admin.getOfficeAddress()).ifPresent(typ -> updateBestallareBuilder.withPostkod(typ.getPostalCode()));
            Optional.ofNullable(admin.getOfficeAddress()).ifPresent(typ -> updateBestallareBuilder.withStad(typ.getPostalCity()));

            updateOrderRequestBuilder.withBestallare(updateBestallareBuilder.build());
        });

        if (nonNull(request.isNeedForInterpreter())) {

            if (request.isNeedForInterpreter() && nonNull(request.getInterpreterLanguage())) {
                updateOrderRequestBuilder.withTolkSprak(request.getInterpreterLanguage().getCode());
            } else if (!request.isNeedForInterpreter() && nonNull(request.getInterpreterLanguage())
                    && !isNullOrEmpty(request.getInterpreterLanguage().getCode())) {
                throw new IbServiceException(
                        IbErrorCodeEnum.BAD_REQUEST, INTERPRETER_ERROR_TEXT);
            }

            updateOrderRequestBuilder.withTolkBehov(request.isNeedForInterpreter());
        }

        return updateOrderRequestBuilder.build();
    }

    public String getUtredningId() {
        return utredningId;
    }

    public Optional<String> getKommentar() {
        return Optional.ofNullable(kommentar);
    }

    public Optional<Bestallare> getBestallare() {
        return Optional.ofNullable(bestallare);
    }

    public Optional<Boolean> getTolkBehov() {
        return Optional.ofNullable(tolkBehov);
    }

    public Optional<String> getTolkSprak() {
        return Optional.ofNullable(tolkSprak);
    }

    public Optional<LocalDateTime> getLastDateIntyg() {
        return Optional.ofNullable(lastDateIntyg);
    }

    public Optional<Boolean> getHandling() {
        return Optional.ofNullable(handling);
    }

    public static final class UpdateOrderRequestBuilder {
        private String utredningId;
        private String kommentar;
        private Bestallare bestallare;
        private Boolean tolkBehov;
        private String tolkSprak;
        private LocalDateTime lastDateIntyg;
        private Boolean handling;

        private UpdateOrderRequestBuilder() {
        }

        public static UpdateOrderRequestBuilder anUpdateOrderRequest() {
            return new UpdateOrderRequestBuilder();
        }

        public UpdateOrderRequestBuilder withUtredningId(String utredningId) {
            this.utredningId = utredningId;
            return this;
        }

        public UpdateOrderRequestBuilder withKommentar(String kommentar) {
            this.kommentar = kommentar;
            return this;
        }

        public UpdateOrderRequestBuilder withBestallare(Bestallare bestallare) {
            this.bestallare = bestallare;
            return this;
        }

        public UpdateOrderRequestBuilder withTolkBehov(Boolean tolkBehov) {
            this.tolkBehov = tolkBehov;
            return this;
        }

        public UpdateOrderRequestBuilder withTolkSprak(String tolkSprak) {
            this.tolkSprak = tolkSprak;
            return this;
        }

        public UpdateOrderRequestBuilder withLastDateIntyg(LocalDateTime lastDateIntyg) {
            this.lastDateIntyg = lastDateIntyg;
            return this;
        }

        public UpdateOrderRequestBuilder withHandling(Boolean handling) {
            this.handling = handling;
            return this;
        }

        public UpdateOrderRequest build() {
            UpdateOrderRequest updateOrderRequest = new UpdateOrderRequest();
            updateOrderRequest.lastDateIntyg = this.lastDateIntyg;
            updateOrderRequest.tolkSprak = this.tolkSprak;
            updateOrderRequest.kommentar = this.kommentar;
            updateOrderRequest.bestallare = this.bestallare;
            updateOrderRequest.utredningId = this.utredningId;
            updateOrderRequest.tolkBehov = this.tolkBehov;
            updateOrderRequest.handling = this.handling;
            return updateOrderRequest;
        }
    }
}
