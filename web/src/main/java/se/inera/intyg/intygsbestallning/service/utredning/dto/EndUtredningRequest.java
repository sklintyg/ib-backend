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
import com.google.common.primitives.Longs;
import se.inera.intyg.intygsbestallning.common.exception.IbErrorCodeEnum;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.persistence.model.type.EndReason;
import se.riv.intygsbestallning.certificate.order.endassessment.v1.EndAssessmentType;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

public final class EndUtredningRequest {
    private Long utredningId;
    private EndReason endReason;

    private EndUtredningRequest() {

    }

    public static EndUtredningRequest from(EndAssessmentType endAssessmentType) {
        validate(endAssessmentType);

        return EndUtredningRequestBuilder.anEndUtredningRequest()
                .withEndReason(!isNull(endAssessmentType.getEndingCondition())
                        ? EndReason.valueOf(endAssessmentType.getEndingCondition().getCode()) : null)
                .withUtredningId(Longs.tryParse(endAssessmentType.getAssessmentId().getExtension()))
                .build();
    }

    private static void validate(EndAssessmentType endAssessmentType) {
        List<String> errors = new ArrayList<>();
        if (!isNull(endAssessmentType.getEndingCondition())) {
            try {
                EndReason.valueOf(endAssessmentType.getEndingCondition().getCode());
            } catch (IllegalArgumentException iae) {
                errors.add("EndingCondition is not of a known type");
            }
        }

        if (!errors.isEmpty()) {
            throw new IbServiceException(IbErrorCodeEnum.BAD_REQUEST, Joiner.on(", ").join(errors));
        }
    }

    public Long getUtredningId() {
        return utredningId;
    }

    public EndReason getEndReason() {
        return endReason;
    }

    public static final class EndUtredningRequestBuilder {
        private Long utredningId;
        private EndReason endReason;

        private EndUtredningRequestBuilder() {
        }

        public static EndUtredningRequestBuilder anEndUtredningRequest() {
            return new EndUtredningRequestBuilder();
        }

        public EndUtredningRequestBuilder withUtredningId(Long utredningId) {
            this.utredningId = utredningId;
            return this;
        }

        public EndUtredningRequestBuilder withEndReason(EndReason endReason) {
            this.endReason = endReason;
            return this;
        }

        public EndUtredningRequest build() {
            EndUtredningRequest endUtredningRequest = new EndUtredningRequest();
            endUtredningRequest.endReason = this.endReason;
            endUtredningRequest.utredningId = this.utredningId;
            return endUtredningRequest;
        }
    }
}
