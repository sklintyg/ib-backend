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
package se.inera.intyg.intygsbestallning.persistence.model;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.google.common.base.MoreObjects;

/**
 * Created by marced on 2018-06-01.
 */

@Entity
@Table(name = "NOTIFIERING_PREFERENCE")
public final class NotifieringPreference {

    @Id
    @Column(name = "HSA_ID", nullable = false)
    private String hsaId;

    @Column(name = "LANDSTING_EPOST")
    private String landstingEpost;

    @Column(name = "ENABLED_NOTIFICATIONS")
    private String enabledNotifications;

    public String getHsaId() {
        return hsaId;
    }

    public void setHsaId(String hsaId) {
        this.hsaId = hsaId;
    }

    public String getLandstingEpost() {
        return landstingEpost;
    }

    public void setLandstingEpost(String landstingEpost) {
        this.landstingEpost = landstingEpost;
    }

    public String getEnabledNotifications() {
        return enabledNotifications;
    }

    public void setEnabledNotifications(String enabledNotifications) {
        this.enabledNotifications = enabledNotifications;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("hsaId", hsaId)
                .add("landstingEpost", landstingEpost)
                .add("enabledNotifications", enabledNotifications)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o == null || getClass() != o.getClass()) {
            return false;
        } else {
            NotifieringPreference that = (NotifieringPreference) o;

            return Objects.equals(hsaId, that.hsaId) && Objects.equals(landstingEpost, that.landstingEpost)
                    && Objects.equals(enabledNotifications, that.enabledNotifications);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(hsaId, landstingEpost, enabledNotifications);
    }

    public static final class NotifieringPreferenceBuilder {
        private String hsaId;
        private String landstingEpost;
        private String enabledNotifications;

        private NotifieringPreferenceBuilder() {
        }

        public static NotifieringPreferenceBuilder aNotifieringPreference() {
            return new NotifieringPreferenceBuilder();
        }

        public NotifieringPreferenceBuilder withHsaId(String hsaId) {
            this.hsaId = hsaId;
            return this;
        }

        public NotifieringPreferenceBuilder withLandstingEpost(String landstingEpost) {
            this.landstingEpost = landstingEpost;
            return this;
        }

        public NotifieringPreferenceBuilder withEnabledNotifications(String enabledNotifications) {
            this.enabledNotifications = enabledNotifications;
            return this;
        }

        public NotifieringPreference build() {
            NotifieringPreference notifieringPreference = new NotifieringPreference();
            notifieringPreference.enabledNotifications = this.enabledNotifications;
            notifieringPreference.landstingEpost = this.landstingEpost;
            notifieringPreference.hsaId = this.hsaId;
            return notifieringPreference;
        }
    }
}
