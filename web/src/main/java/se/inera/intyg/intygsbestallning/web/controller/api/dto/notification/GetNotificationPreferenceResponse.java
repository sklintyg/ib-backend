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

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.base.Strings;

import se.inera.intyg.intygsbestallning.auth.model.SelectableHsaEntityType;
import se.inera.intyg.intygsbestallning.persistence.model.NotifieringPreference;
import se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringTyp;

/**
 * Created by marced on 2018-05-31.
 */
public class GetNotificationPreferenceResponse {
    private String hsaId;
    private String landstingEpost;
    private List<NotificationPreferenceItem> items;

    public GetNotificationPreferenceResponse() {
    }

    public GetNotificationPreferenceResponse(String hsaId, String landstingEpost, List<NotificationPreferenceItem> items) {
        this.hsaId = hsaId;
        this.landstingEpost = landstingEpost;
        this.items = items;
    }

    public static GetNotificationPreferenceResponse from(NotifieringPreference notifieringPreference,
            SelectableHsaEntityType hsaEntityType) {
        final Stream<String> stringStream = Strings.isNullOrEmpty(notifieringPreference.getEnabledNotifications()) ? Stream.empty()
                : Stream.of(notifieringPreference.getEnabledNotifications().split(","));

        // All notifications stored in entity are by definition enabled
        final List<NotificationPreferenceItem> enabledItems = stringStream
                .map(typeString -> NotifieringTyp.valueOf(typeString))
                .map(nt -> new NotificationPreferenceItem(nt, true))
                .collect(Collectors.toList());

        // Iterate all defined notifications (applicable for this hsa type) and set correct enabledstate for them
        List<NotificationPreferenceItem> itemsWithState = Stream.of(NotifieringTyp.values())
                .filter(nft -> recipientFilter(nft, hsaEntityType))
                .map(nt -> new NotificationPreferenceItem(nt, exists(enabledItems, nt))).collect(Collectors.toList());

        return new GetNotificationPreferenceResponse(notifieringPreference.getHsaId(), notifieringPreference.getLandstingEpost(),
                itemsWithState);

    }

    private static boolean exists(List<NotificationPreferenceItem> enabledItems, NotifieringTyp nt) {
        return enabledItems.stream().anyMatch(ei -> ei.getId().equals(nt.getId()));
    }

    public static boolean recipientFilter(NotifieringTyp notifieringTyp, SelectableHsaEntityType hsaEntityType) {
        switch (hsaEntityType) {
        case VE:
            return notifieringTyp.getRecipient().equals(NotifieringTyp.NotificationRecipientType.ALL)
                    || notifieringTyp.getRecipient().equals(NotifieringTyp.NotificationRecipientType.VARDENHET);
        case VG:
            return notifieringTyp.getRecipient().equals(NotifieringTyp.NotificationRecipientType.ALL)
                    || notifieringTyp.getRecipient().equals(NotifieringTyp.NotificationRecipientType.LANDSTING);
        }
        throw new IllegalArgumentException("Unhandled SelectableHsaEntityType " + hsaEntityType);
    }

    public String getLandstingEpost() {
        return landstingEpost;
    }

    public void setLandstingEpost(String landstingEpost) {
        this.landstingEpost = landstingEpost;
    }

    public List<NotificationPreferenceItem> getItems() {
        return items;
    }

    public void setItems(List<NotificationPreferenceItem> items) {
        this.items = items;
    }

    public String getHsaId() {
        return hsaId;
    }

    public void setHsaId(String hsaId) {
        this.hsaId = hsaId;
    }
}
