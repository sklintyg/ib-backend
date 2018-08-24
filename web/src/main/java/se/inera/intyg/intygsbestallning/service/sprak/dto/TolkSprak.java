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
package se.inera.intyg.intygsbestallning.service.sprak.dto;

import net.sf.jsefa.csv.annotation.CsvDataType;
import net.sf.jsefa.csv.annotation.CsvField;

@CsvDataType
public final class TolkSprak {

    // CHECKSTYLE:OFF MagicNumber
    @CsvField(pos = 1) private String id;
    @CsvField(pos = 2) private String part2b;
    @CsvField(pos = 3) private String part2t;
    @CsvField(pos = 4) private String part1;
    @CsvField(pos = 5) private String scope;
    @CsvField(pos = 6) private String languageType;
    @CsvField(pos = 7) private String refName;
    @CsvField(pos = 8) private String comment;
    // CHECKSTYLE:ON MagicNumber

    public String getId() {
        return id;
    }

    public String getPart2b() {
        return part2b;
    }

    public String getPart2t() {
        return part2t;
    }

    public String getPart1() {
        return part1;
    }

    public String getScope() {
        return scope;
    }

    public String getLanguageType() {
        return languageType;
    }

    public String getRefName() {
        return refName;
    }

    public String getComment() {
        return comment;
    }
}
