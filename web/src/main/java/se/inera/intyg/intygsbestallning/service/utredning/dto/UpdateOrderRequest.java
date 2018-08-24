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

import com.google.common.primitives.Longs;
import se.inera.intyg.intygsbestallning.common.util.SchemaDateUtil;
import se.riv.intygsbestallning.certificate.order.updateorder.v1.UpdateOrderType;

import java.time.LocalDateTime;
import java.util.Optional;

import static se.inera.intyg.intygsbestallning.service.utredning.dto.Bestallare.BestallareBuilder;
import static se.inera.intyg.intygsbestallning.service.utredning.dto.Bestallare.BestallareBuilder.aBestallare;
import static se.inera.intyg.intygsbestallning.service.utredning.dto.UpdateOrderRequest.UpdateOrderRequestBuilder.anUpdateOrderRequest;

public class UpdateOrderRequest implements TolkRequest {

    private Long utredningId;
    private String kommentar;
    private Bestallare bestallare;
    private Boolean tolkBehov;
    private String tolkSprak;
    private LocalDateTime lastDateIntyg;
    private Boolean handling;

    public static UpdateOrderRequest from(final UpdateOrderType request) {

        UpdateOrderRequestBuilder updateOrderRequestBuilder = anUpdateOrderRequest()
                .withUtredningId(Longs.tryParse(request.getAssessmentId().getExtension()))
                .withKommentar(request.getComment())
                .withHandling(request.isDocumentsByPost());

        Optional.ofNullable(request.getLastDateForCertificateReceival())
                .map(SchemaDateUtil::toLocalDateTimeFromDateType)
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
            Optional.ofNullable(admin.getOfficeAddress()).ifPresent(typ -> updateBestallareBuilder.withPostnummer(typ.getPostalCode()));
            Optional.ofNullable(admin.getOfficeAddress()).ifPresent(typ -> updateBestallareBuilder.withStad(typ.getPostalCity()));

            updateOrderRequestBuilder.withBestallare(updateBestallareBuilder.build());
        });

        Optional.ofNullable(request.isNeedForInterpreter()).ifPresent(updateOrderRequestBuilder::withTolkBehov);
        Optional.ofNullable(request.getInterpreterLanguage()).ifPresent(typ -> updateOrderRequestBuilder.withTolkSprak(typ.getCode()));

        return updateOrderRequestBuilder.build();
    }

    public Long getUtredningId() {
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

    @Override
    public boolean isTolkBehov() {
        return tolkBehov;
    }

    @Override
    public void setTolkBehov(boolean tolkBehov) {
        this.tolkBehov = tolkBehov;
    }

    public Optional<String> getOptionalTolkSprak() {
        return Optional.ofNullable(tolkSprak);
    }

    @Override
    public void setTolkSprak(String tolkSprak) {
        this.tolkSprak = tolkSprak;
    }

    @Override
    public String getTolkSprak() {
        return tolkSprak;
    }

    public Optional<LocalDateTime> getLastDateIntyg() {
        return Optional.ofNullable(lastDateIntyg);
    }

    public Optional<Boolean> getHandling() {
        return Optional.ofNullable(handling);
    }

    public static final class UpdateOrderRequestBuilder {
        private Long utredningId;
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

        public UpdateOrderRequestBuilder withUtredningId(Long utredningId) {
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
