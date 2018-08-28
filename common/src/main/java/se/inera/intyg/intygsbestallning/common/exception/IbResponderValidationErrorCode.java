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
package se.inera.intyg.intygsbestallning.common.exception;

import se.riv.intygsbestallning.certificate.order.v1.ErrorIdType;

/**
 * Created by marced on 2018-08-23.
 */
public enum IbResponderValidationErrorCode {

    GTA_FEL01("Unexpected codeSystem: {0}", ErrorIdType.VALIDATION_ERROR),
    GTA_FEL02("Unknown code: {0} for codeSystem: {1}", ErrorIdType.VALIDATION_ERROR),
    GTA_FEL03("Received technical error from HSA", ErrorIdType.TECHNICAL_ERROR),
    GTA_FEL04("Det uppstod ett fel när HSA Katalogen anropades. Beställningen kunde därför inte tas emot.", ErrorIdType.APPLICATION_ERROR),
    GTA_FEL05("{0} does not match expected format YYYYMMDDNNNN", ErrorIdType.VALIDATION_ERROR),
    GTA_FEL06("Vårdgivare saknar organisationsnummer", ErrorIdType.APPLICATION_ERROR),

    TA_FEL01("Okänt HSA id för landsting: {0}", ErrorIdType.APPLICATION_ERROR),
    TA_FEL04("Utredning {0} tillhör inte vårdenhet {1}", ErrorIdType.APPLICATION_ERROR),
    TA_FEL05("Felaktigt vårdkontaktsid: {0}. Vårdkontakten existerar inte.", ErrorIdType.APPLICATION_ERROR),
    TA_FEL06("Felaktig utredningsid: {0}. Utredningen existerar inte.", ErrorIdType.APPLICATION_ERROR),
    TA_FEL07("Slutdatum för utredningen måste anges", ErrorIdType.APPLICATION_ERROR),
    TA_FEL08("Slutdatum för kompletteringen måste anges", ErrorIdType.APPLICATION_ERROR),
    TA_FEL09("Id för kompletteringsbegäran måste anges", ErrorIdType.APPLICATION_ERROR),
    TA_FEL10("Felaktigt id för kompletteringsbegäran: {0}. Kompletteringsbegäran existerar inte.", ErrorIdType.APPLICATION_ERROR),
    TA_FEL13("Utredningen har redan blivit beställd eller är avbruten", ErrorIdType.APPLICATION_ERROR),
    TA_FEL14("Besöket är i fel status. Avvikelsen kunde inte tas emot.", ErrorIdType.APPLICATION_ERROR);

    private final String errorMsgTemplate;
    private final ErrorIdType errorIdType;

    IbResponderValidationErrorCode(String errorMessageTemplate, ErrorIdType errorIdType) {
        this.errorMsgTemplate = errorMessageTemplate;
        this.errorIdType = errorIdType;
    }

    public String getErrorMsgTemplate() {
        return errorMsgTemplate;
    }

    public ErrorIdType getErrorIdType() {
        return errorIdType;
    }
}
