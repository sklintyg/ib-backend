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
package se.inera.intyg.intygsbestallning.web.controller.api.dto.notification;

import se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringTyp;

/**
 * Created by marced on 2018-05-31.
 */
public class NotificationPreferenceItem {
    private String id;
    private String label;
    private boolean enabled;

    public NotificationPreferenceItem() {
    }

    public NotificationPreferenceItem(String id, String label, boolean enabled) {
        this.id = id;
        this.label = label;
        this.enabled = enabled;
    }

    public NotificationPreferenceItem(NotifieringTyp notifieringTyp, boolean enabled) {
        this.id = notifieringTyp.getId();
        this.label = notifieringTyp.getLabel();
        this.enabled = enabled;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
