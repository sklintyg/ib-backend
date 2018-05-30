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

public class HandelseListItem {

    private static DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;

    private String skapad;
    private HandelseTyp.Typ typ;
    private String anvandare;
    private String handelseText;
    private String kommentar;

    public static HandelseListItem from(Handelse handelse, boolean includeKommentar) {
        HandelseListItemBuilder handelseListItemBuilder = HandelseListItemBuilder.aUtredningHandelseListItem()
                .withSkapad(handelse.getSkapad().format(formatter))
                .withTyp(handelse.getHandelseTyp().getTyp())
                .withAnvandare(handelse.getAnvandare())
                .withHandelseText(handelse.getHandelseText());
        if (includeKommentar) {
            handelseListItemBuilder.withKommentar(handelse.getKommentar());
        }
        return handelseListItemBuilder.build();
    }

    public String getSkapad() {
        return skapad;
    }

    public void setSkapad(String skapad) {
        this.skapad = skapad;
    }

    public HandelseTyp.Typ getTyp() {
        return typ;
    }

    public void setTyp(HandelseTyp.Typ typ) {
        this.typ = typ;
    }

    public String getAnvandare() {
        return anvandare;
    }

    public void setAnvandare(String anvandare) {
        this.anvandare = anvandare;
    }

    public String getHandelseText() {
        return handelseText;
    }

    public void setHandelseText(String handelseText) {
        this.handelseText = handelseText;
    }

    public String getKommentar() {
        return kommentar;
    }

    public void setKommentar(String kommentar) {
        this.kommentar = kommentar;
    }

    public static final class HandelseListItemBuilder {
        private String skapad;
        private HandelseTyp.Typ typ;
        private String anvandare;
        private String handelseText;
        private String kommentar;

        private HandelseListItemBuilder() {
        }

        public static HandelseListItem.HandelseListItemBuilder aUtredningHandelseListItem() {
            return new HandelseListItem.HandelseListItemBuilder();
        }

        public HandelseListItemBuilder withSkapad(String skapad) {
            this.skapad = skapad;
            return this;
        }

        public HandelseListItemBuilder withTyp(HandelseTyp.Typ typ) {
            this.typ = typ;
            return this;
        }

        public HandelseListItemBuilder withAnvandare(String anvandare) {
            this.anvandare = anvandare;
            return this;
        }

        public HandelseListItemBuilder withHandelseText(String handelseText) {
            this.handelseText = handelseText;
            return this;
        }

        public HandelseListItemBuilder withKommentar(String kommentar) {
            this.kommentar = kommentar;
            return this;
        }

        public HandelseListItem build() {
            HandelseListItem handelseListItem = new HandelseListItem();
            handelseListItem.setAnvandare(anvandare);
            handelseListItem.setHandelseText(handelseText);
            handelseListItem.setSkapad(skapad);
            handelseListItem.setTyp(typ);
            handelseListItem.setKommentar(kommentar);
            return handelseListItem;
        }
    }
}
