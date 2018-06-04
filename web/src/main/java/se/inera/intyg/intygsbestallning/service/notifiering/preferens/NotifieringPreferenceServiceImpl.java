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
package se.inera.intyg.intygsbestallning.service.notifiering.preferens;

import static se.inera.intyg.intygsbestallning.web.controller.api.dto.notification.GetNotificationPreferenceResponse.recipientFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import se.inera.intyg.intygsbestallning.auth.model.SelectableHsaEntityType;
import se.inera.intyg.intygsbestallning.persistence.model.NotifieringPreference;
import se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringTyp;
import se.inera.intyg.intygsbestallning.persistence.repository.NotifieringPreferenceRepository;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.notification.GetNotificationPreferenceResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.notification.SetNotificationPreferenceRequest;

/**
 * Created by marced on 2018-05-31.
 */
@Service
public class NotifieringPreferenceServiceImpl implements NotifieringPreferenceService {

    @Autowired
    private NotifieringPreferenceRepository notifieringPreferenceRepository;

    @Override
    public GetNotificationPreferenceResponse getNotificationPreference(String hsaId, SelectableHsaEntityType hsaEntityType) {

        return GetNotificationPreferenceResponse.from(
                notifieringPreferenceRepository.findByHsaId(hsaId).orElseGet(() -> createDefaultEntity(hsaId, hsaEntityType)),
                hsaEntityType);
    }

    @Override
    public GetNotificationPreferenceResponse setNotificationPreference(String hsaId, SelectableHsaEntityType hsaEntityType,
            SetNotificationPreferenceRequest notificationPreferenceRequest) {

        // Get previously stored or new instance
        final NotifieringPreference notifieringPreference = notifieringPreferenceRepository.findByHsaId(hsaId)
                .orElseGet(() -> NotifieringPreference.NotifieringPreferenceBuilder.aNotifieringPreference().withHsaId(hsaId).build());

        return GetNotificationPreferenceResponse
                .from(updateEntityFromRequest(notifieringPreference, notificationPreferenceRequest), hsaEntityType);
    }

    private NotifieringPreference updateEntityFromRequest(NotifieringPreference entity, SetNotificationPreferenceRequest request) {

        if (request.getItems() != null) {
            String validatedEnabledItemsString = request.getItems().stream()
                    .filter(item -> item.isEnabled())
                    .map(npi -> NotifieringTyp.valueOf(npi.getId()).getId()) // enum roundtrip makes sure we only persist valid enum values
                    .collect(Collectors.joining(","));
            entity.setEnabledNotifications(validatedEnabledItemsString);
        } else {
            entity.setEnabledNotifications(null);
        }
        entity.setLandstingEpost(request.getLandstingEpost());

        return notifieringPreferenceRepository.save(entity);
    }

    private NotifieringPreference createDefaultEntity(String hsaId, SelectableHsaEntityType hsaEntityType) {

        // Create a comma-separated string of all applicable notification types to this hsaEntityType
        String enabledNotifications = Stream.of(NotifieringTyp.values())
                .filter(nft -> recipientFilter(nft, hsaEntityType))
                .map(NotifieringTyp::getId)
                .collect(Collectors.joining(","));

        NotifieringPreference preference = new NotifieringPreference();
        preference.setHsaId(hsaId);
        preference.setEnabledNotifications(enabledNotifications);
        return notifieringPreferenceRepository.save(preference);
    }

}
