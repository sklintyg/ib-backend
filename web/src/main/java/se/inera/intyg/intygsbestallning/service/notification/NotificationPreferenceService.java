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
package se.inera.intyg.intygsbestallning.service.notification;

import se.inera.intyg.intygsbestallning.auth.model.SelectableHsaEntityType;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.notification.GetNotificationPreferenceResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.notification.SetNotificationPreferenceRequest;

/**
 * Created by marced on 2018-05-31.
 */
public interface NotificationPreferenceService {
    GetNotificationPreferenceResponse getNotificationPreference(String hsaId, SelectableHsaEntityType hsaEntityType);

    GetNotificationPreferenceResponse setNotificationPreference(String hsaId, SelectableHsaEntityType hsaEntityType,
            SetNotificationPreferenceRequest notificationPreferenceRequest);
}