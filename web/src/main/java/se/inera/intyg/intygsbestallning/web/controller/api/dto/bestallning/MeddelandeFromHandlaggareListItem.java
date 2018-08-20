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
package se.inera.intyg.intygsbestallning.web.controller.api.dto.bestallning;

import se.inera.intyg.intygsbestallning.persistence.model.BestallningHistorik;
import java.time.format.DateTimeFormatter;

public final class MeddelandeFromHandlaggareListItem {
    private static DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
    private String kommentar;
    private String datum;

    private MeddelandeFromHandlaggareListItem() {
    }

    public String getKommentar() {
        return kommentar;
    }

    public void setKommentar(String kommentar) {
        this.kommentar = kommentar;
    }

    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }

    public static MeddelandeFromHandlaggareListItem from(BestallningHistorik bestallningHistorik) {
        return MeddelandeFromHandlaggareListItemBuilder.aMeddelandeFromHandlaggareListItem()
                .withKommentar(bestallningHistorik.getKommentar())
                .withDatum(bestallningHistorik.getDatum().format(formatter))
                .build();
    }

    public static final class MeddelandeFromHandlaggareListItemBuilder {
        private String kommentar;
        private String datum;

        private MeddelandeFromHandlaggareListItemBuilder() {
        }

        public String getKommentar() {
            return kommentar;
        }

        public void setKommentar(String kommentar) {
            this.kommentar = kommentar;
        }

        public String getDatum() {
            return datum;
        }

        public void setDatum(String datum) {
            this.datum = datum;
        }

        public static MeddelandeFromHandlaggareListItemBuilder aMeddelandeFromHandlaggareListItem() {
            return new MeddelandeFromHandlaggareListItemBuilder();
        }

        public MeddelandeFromHandlaggareListItemBuilder withKommentar(String kommentar) {
            this.kommentar = kommentar;
            return this;
        }

        public MeddelandeFromHandlaggareListItemBuilder withDatum(String datum) {
            this.datum = datum;
            return this;
        }

        public MeddelandeFromHandlaggareListItem build() {
            MeddelandeFromHandlaggareListItem item = new MeddelandeFromHandlaggareListItem();
            item.setKommentar(kommentar);
            item.setDatum(datum);
            return item;
        }
    }
}
