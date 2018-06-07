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
package se.inera.intyg.intygsbestallning.web.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import se.inera.intyg.intygsbestallning.auth.IbUser;
import se.inera.intyg.intygsbestallning.monitoring.PrometheusTimeMethod;
import se.inera.intyg.intygsbestallning.service.notifiering.preferens.NotifieringPreferenceService;
import se.inera.intyg.intygsbestallning.service.user.UserService;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.notification.GetNotificationPreferenceResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.notification.SetNotificationPreferenceRequest;

/**
 * Created by marced on 2018-05-31.
 */
@RestController
@RequestMapping("/api/notifiering/preference")
public class NotifieringPreferenceController {

    @Autowired
    private UserService userService;

    @Autowired
    private NotifieringPreferenceService notifieringPreferenceService;

    @PrometheusTimeMethod(name = "get_notifiering_preference_filter_duration_seconds", help = "Some helpful info here")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetNotificationPreferenceResponse> getNotificationPreferences() {
        IbUser user = userService.getUser();

        final GetNotificationPreferenceResponse notificationPreference = notifieringPreferenceService
                .getNotificationPreference(user.getCurrentlyLoggedInAt().getId(), user.getCurrentlyLoggedInAt().getType());
        return ResponseEntity.ok(notificationPreference);
    }

    @PrometheusTimeMethod(name = "set_notifiering_preference_filter_duration_seconds", help = "Some helpful info here")
    @PutMapping(consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<GetNotificationPreferenceResponse> setNotificationPreferences(
            @RequestBody final SetNotificationPreferenceRequest setNotificationPreferenceRequest) {
        IbUser user = userService.getUser();

        final GetNotificationPreferenceResponse notificationPreference = notifieringPreferenceService
                .setNotificationPreference(user.getCurrentlyLoggedInAt().getId(), user.getCurrentlyLoggedInAt().getType(),
                        setNotificationPreferenceRequest);
        return ResponseEntity.ok(notificationPreference);
    }
}
