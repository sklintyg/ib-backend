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
package se.inera.intyg.intygsbestallning.web.controller.api.dto;

import se.inera.intyg.intygsbestallning.persistence.model.Handelse;
import se.inera.intyg.intygsbestallning.persistence.model.type.HandelseTyp;

import java.time.format.DateTimeFormatter;

public class UtredningHandelseListItem {

    private static DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;

    private String skapad;
    private HandelseTyp typ;
    private String anvandare;
    private String kommentar;

    public static UtredningHandelseListItem from(Handelse handelse) {
        return UtredningHandelseListItemBuilder.aUtredningHandelseListItem()
                .withSkapad(handelse.getSkapad().format(formatter))
                .withTyp(handelse.getHandelseTyp())
                .withAnvandare(handelse.getAnvandare())
                .withKommentar(handelse.getKommentar())
                .build();
    }

    public String getSkapad() {
        return skapad;
    }

    public void setSkapad(String skapad) {
        this.skapad = skapad;
    }

    public HandelseTyp getTyp() {
        return typ;
    }

    public void setTyp(HandelseTyp typ) {
        this.typ = typ;
    }

    public String getAnvandare() {
        return anvandare;
    }

    public void setAnvandare(String anvandare) {
        this.anvandare = anvandare;
    }

    public String getKommentar() {
        return kommentar;
    }

    public void setKommentar(String kommentar) {
        this.kommentar = kommentar;
    }

    public static final class UtredningHandelseListItemBuilder {
        private String skapad;
        private HandelseTyp typ;
        private String anvandare;
        private String kommentar;

        private UtredningHandelseListItemBuilder() {
        }

        public static UtredningHandelseListItem.UtredningHandelseListItemBuilder aUtredningHandelseListItem() {
            return new UtredningHandelseListItem.UtredningHandelseListItemBuilder();
        }

        public UtredningHandelseListItem.UtredningHandelseListItemBuilder withSkapad(String skapad) {
            this.skapad = skapad;
            return this;
        }

        public UtredningHandelseListItem.UtredningHandelseListItemBuilder withTyp(HandelseTyp typ) {
            this.typ = typ;
            return this;
        }

        public UtredningHandelseListItem.UtredningHandelseListItemBuilder withAnvandare(String anvandare) {
            this.anvandare = anvandare;
            return this;
        }

        public UtredningHandelseListItem.UtredningHandelseListItemBuilder withKommentar(String kommentar) {
            this.kommentar = kommentar;
            return this;
        }

        public UtredningHandelseListItem build() {
            UtredningHandelseListItem utredningHandelseListItem = new UtredningHandelseListItem();
            utredningHandelseListItem.setAnvandare(anvandare);
            utredningHandelseListItem.setKommentar(kommentar);
            utredningHandelseListItem.setSkapad(skapad);
            utredningHandelseListItem.setTyp(typ);
            return utredningHandelseListItem;
        }
    }
}
