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
package se.inera.intyg.intygsbestallning.service.patient;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.primitives.Ints;
import se.inera.intyg.schemas.contract.Personnummer;

import java.util.Optional;

public enum Gender {

    F("Kvinna"), M("Man"), UNKNOWN("Okänt");

    private static final int GENDER_START = 10;
    private static final int GENDER_END = 11;

    private final String desc;

    Gender(String desc) {
        this.desc = desc;
    }

    @VisibleForTesting
    public static Gender getGenderFromPersonnummer(Personnummer pnr) {
        return Gender.getGenderFromString(pnr.getPersonnummer().substring(GENDER_START, GENDER_END));
    }

    protected static Gender getGenderFromString(String genderString) {
        return Optional.ofNullable(genderString)
                .map(Ints::tryParse)
                .map(i -> i % 2 == 0 ? F : M)
                .orElse(UNKNOWN);
    }

    public String getDescription() {
        return this.desc;
    }

}
