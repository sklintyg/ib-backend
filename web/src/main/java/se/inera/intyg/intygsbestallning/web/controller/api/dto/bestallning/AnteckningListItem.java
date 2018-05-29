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

import se.inera.intyg.intygsbestallning.persistence.model.Anteckning;

import java.time.LocalDateTime;

public class AnteckningListItem {

    private LocalDateTime skapat;
    private String anvandare;
    private String text;

    public static AnteckningListItem from(Anteckning anteckning) {
        return AnteckningListItemBuilder.anAnteckningListItem()
                .withAnvandare(anteckning.getAnvandare())
                .withSkapat(anteckning.getSkapat())
                .withText(anteckning.getText())
                .build();
    }

    public LocalDateTime getSkapat() {
        return skapat;
    }

    public void setSkapat(LocalDateTime skapat) {
        this.skapat = skapat;
    }

    public String getAnvandare() {
        return anvandare;
    }

    public void setAnvandare(String anvandare) {
        this.anvandare = anvandare;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public static final class AnteckningListItemBuilder {
        private LocalDateTime skapat;
        private String anvandare;
        private String text;

        public static AnteckningListItem.AnteckningListItemBuilder anAnteckningListItem() {
            return new AnteckningListItem.AnteckningListItemBuilder();
        }

        public AnteckningListItemBuilder withSkapat(LocalDateTime skapat) {
            this.skapat = skapat;
            return this;
        }

        public AnteckningListItemBuilder withAnvandare(String anvandare) {
            this.anvandare = anvandare;
            return this;
        }

        public AnteckningListItemBuilder withText(String text) {
            this.text = text;
            return this;
        }

        public AnteckningListItem build() {
            AnteckningListItem anteckningListItem = new AnteckningListItem();
            anteckningListItem.setSkapat(skapat);
            anteckningListItem.setAnvandare(anvandare);
            anteckningListItem.setText(text);
            return anteckningListItem;
        }
    }
}
