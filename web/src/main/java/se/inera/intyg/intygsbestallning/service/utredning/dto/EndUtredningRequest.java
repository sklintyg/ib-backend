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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.primitives.Longs.tryParse;
import static java.util.Objects.nonNull;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.primitives.Longs;
import se.riv.intygsbestallning.certificate.order.endassessment.v1.EndAssessmentType;
import java.util.List;
import java.util.Optional;
import se.inera.intyg.intygsbestallning.auth.IbUser;
import se.inera.intyg.intygsbestallning.common.exception.IbErrorCodeEnum;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.persistence.model.type.AvslutOrsak;

public final class EndUtredningRequest {
    private final Long utredningId;
    private final AvslutOrsak avslutOrsak;
    private final IbUser user;

    private EndUtredningRequest(final EndUtredningRequestBuilder builder) {
        this.utredningId = builder.utredningId;
        this.avslutOrsak = builder.avslutOrsak;
        this.user = builder.user;
    }

    public static EndUtredningRequest from(EndAssessmentType endAssessmentType) {
        validate(endAssessmentType);

        return EndUtredningRequestBuilder.anEndUtredningRequest()
                .withEndReason(
                        nonNull(endAssessmentType.getEndingCondition())
                                ? AvslutOrsak.valueOf(endAssessmentType.getEndingCondition().getCode())
                                : null)
                .withUtredningId(tryParse(endAssessmentType.getAssessmentId().getExtension()))
                .build();
    }

    public static EndUtredningRequest from(final String utredningId, final IbUser user) {

        checkArgument(nonNull(utredningId));
        checkArgument(nonNull(user));

        return EndUtredningRequestBuilder.anEndUtredningRequest()
                .withEndReason(AvslutOrsak.INGEN_KOMPLETTERING_BEGARD)
                .withUtredningId(tryParse(utredningId))
                .withUser(user)
                .build();
    }

    private static void validate(EndAssessmentType endAssessmentType) {
        List<String> errors = Lists.newArrayList();
        if (nonNull(endAssessmentType.getEndingCondition())) {
            try {
                AvslutOrsak.valueOf(endAssessmentType.getEndingCondition().getCode());
            } catch (IllegalArgumentException iae) {
                errors.add("EndingCondition is not of a known type");
            }
        }

        if (isNotEmpty(errors)) {
            throw new IbServiceException(IbErrorCodeEnum.BAD_REQUEST, Joiner.on(", ").join(errors));
        }
    }

    public Long getUtredningId() {
        return utredningId;
    }

    public AvslutOrsak getAvslutOrsak() {
        return avslutOrsak;
    }

    public Optional<IbUser> getUser() {
        return Optional.ofNullable(user);
    }

    public static final class EndUtredningRequestBuilder {
        private Long utredningId;
        private AvslutOrsak avslutOrsak;
        private IbUser user;

        private EndUtredningRequestBuilder() {
        }

        public static EndUtredningRequestBuilder anEndUtredningRequest() {
            return new EndUtredningRequestBuilder();
        }

        public EndUtredningRequestBuilder withUtredningId(Long utredningId) {
            this.utredningId = utredningId;
            return this;
        }

        public EndUtredningRequestBuilder withEndReason(AvslutOrsak avslutOrsak) {
            this.avslutOrsak = avslutOrsak;
            return this;
        }

        public EndUtredningRequestBuilder withUser(IbUser user) {
            this.user = user;
            return this;
        }

        public EndUtredningRequest build() {
            return new EndUtredningRequest(this);
        }
    }
}
