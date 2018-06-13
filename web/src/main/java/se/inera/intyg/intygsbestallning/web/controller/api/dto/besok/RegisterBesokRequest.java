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
package se.inera.intyg.intygsbestallning.web.controller.api.dto.besok;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.nonNull;
import static se.inera.intyg.intygsbestallning.web.controller.api.dto.besok.RegisterBesokRequest.RegisterBesokRequestBuilder.aRegisterBesokRequest;

import se.inera.intyg.intygsbestallning.persistence.model.type.DeltagarProfessionTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.KallelseFormTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.TolkStatusTyp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

public class RegisterBesokRequest {

    private String utredandeVardPersonalNamn;
    private DeltagarProfessionTyp profession;
    private TolkStatusTyp tolkStatus;

    private KallelseFormTyp kallelseForm;
    private LocalDateTime kallelseDatum;

    private LocalDate besokDatum;
    private LocalTime besokStartTid;
    private LocalTime besokSlutTid;

    public static RegisterBesokRequest from(final RegisterBesokRequest request) {
        validate(request);

        return aRegisterBesokRequest()
                .withUtredandeVardPersonalNamn(request.getUtredandeVardPersonalNamn().orElse(null))
                .withProfession(request.getProfession())
                .withTolkStatus(request.getTolkStatus())
                .withKallelseForm(request.getKallelseForm())
                .withKallelseDatum(request.getKallelseDatum())
                .withBesokDatum(request.getBesokDatum())
                .withBesokStartTid(request.getBesokStartTid())
                .withBesokSlutTid(request.getBesokSlutTid())
                .build();
    }

    public Optional<String> getUtredandeVardPersonalNamn() {
        return Optional.ofNullable(utredandeVardPersonalNamn);
    }

    public void setUtredandeVardPersonalNamn(final String utredandeVardPersonalNamn) {
        this.utredandeVardPersonalNamn = utredandeVardPersonalNamn;
    }

    public DeltagarProfessionTyp getProfession() {
        return profession;
    }

    public void setProfession(final DeltagarProfessionTyp profession) {
        this.profession = profession;
    }

    public TolkStatusTyp getTolkStatus() {
        return tolkStatus;
    }

    public void setTolkStatus(final TolkStatusTyp tolkStatus) {
        this.tolkStatus = tolkStatus;
    }

    public KallelseFormTyp getKallelseForm() {
        return kallelseForm;
    }

    public void setKallelseForm(final KallelseFormTyp kallelseForm) {
        this.kallelseForm = kallelseForm;
    }

    public LocalDateTime getKallelseDatum() {
        return kallelseDatum;
    }

    public void setKallelseDatum(final LocalDateTime kallelseDatum) {
        this.kallelseDatum = kallelseDatum;
    }

    public LocalDate getBesokDatum() {
        return besokDatum;
    }

    public void setBesokDatum(final LocalDate besokDatum) {
        this.besokDatum = besokDatum;
    }

    public LocalTime getBesokStartTid() {
        return besokStartTid;
    }

    public void setBesokStartTid(final LocalTime besokStartTid) {
        this.besokStartTid = besokStartTid;
    }

    public LocalTime getBesokSlutTid() {
        return besokSlutTid;
    }

    public void setBesokSlutTid(final LocalTime besokSlutTid) {
        this.besokSlutTid = besokSlutTid;
    }

    public static void validate(RegisterBesokRequest request) {
        checkArgument(nonNull(request), "request may not be null");
        checkArgument(nonNull(request.getProfession()), "profession may not be null");
        checkArgument(nonNull(request.getBesokSlutTid()), "besokSlutTid may not be null");
        checkArgument(nonNull(request.getKallelseForm()), "kallelseForm may not be null");
        checkArgument(nonNull(request.getKallelseDatum()), "kallelseDatum may not be null");
        checkArgument(nonNull(request.getBesokDatum()), "besokDatum may not be null");
        checkArgument(nonNull(request.getBesokStartTid()), "besokStartTid may not be null");
    }


    public static final class RegisterBesokRequestBuilder {
        private String utredandeVardPersonalNamn;
        private DeltagarProfessionTyp profession;
        private TolkStatusTyp tolkStatus;
        private KallelseFormTyp kallelseForm;
        private LocalDateTime kallelseDatum;
        private LocalDate besokDatum;
        private LocalTime besokStartTid;
        private LocalTime besokSlutTid;

        private RegisterBesokRequestBuilder() {
        }

        public static RegisterBesokRequestBuilder aRegisterBesokRequest() {
            return new RegisterBesokRequestBuilder();
        }

        public RegisterBesokRequestBuilder withUtredandeVardPersonalNamn(String utredandeVardPersonalNamn) {
            this.utredandeVardPersonalNamn = utredandeVardPersonalNamn;
            return this;
        }

        public RegisterBesokRequestBuilder withProfession(DeltagarProfessionTyp profession) {
            this.profession = profession;
            return this;
        }

        public RegisterBesokRequestBuilder withTolkStatus(TolkStatusTyp tolkStatus) {
            this.tolkStatus = tolkStatus;
            return this;
        }

        public RegisterBesokRequestBuilder withKallelseForm(KallelseFormTyp kallelseForm) {
            this.kallelseForm = kallelseForm;
            return this;
        }

        public RegisterBesokRequestBuilder withKallelseDatum(LocalDateTime kallelseDatum) {
            this.kallelseDatum = kallelseDatum;
            return this;
        }

        public RegisterBesokRequestBuilder withBesokDatum(LocalDate besokDatum) {
            this.besokDatum = besokDatum;
            return this;
        }

        public RegisterBesokRequestBuilder withBesokStartTid(LocalTime besokStartTid) {
            this.besokStartTid = besokStartTid;
            return this;
        }

        public RegisterBesokRequestBuilder withBesokSlutTid(LocalTime besokSlutTid) {
            this.besokSlutTid = besokSlutTid;
            return this;
        }

        public RegisterBesokRequest build() {
            RegisterBesokRequest registerBesokRequest = new RegisterBesokRequest();
            registerBesokRequest.setUtredandeVardPersonalNamn(utredandeVardPersonalNamn);
            registerBesokRequest.setProfession(profession);
            registerBesokRequest.setTolkStatus(tolkStatus);
            registerBesokRequest.setKallelseForm(kallelseForm);
            registerBesokRequest.setKallelseDatum(kallelseDatum);
            registerBesokRequest.setBesokDatum(besokDatum);
            registerBesokRequest.setBesokStartTid(besokStartTid);
            registerBesokRequest.setBesokSlutTid(besokSlutTid);
            return registerBesokRequest;
        }
    }
}
