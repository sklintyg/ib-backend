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
package se.inera.intyg.intygsbestallning.web.controller.api.helper;

import org.springframework.http.HttpHeaders;
import se.inera.intyg.intygsbestallning.auth.IbUser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class ControllerHelper {

    private ControllerHelper() {
    }

    public static HttpHeaders getHttpHeaders(String contentType, long contentLength, String filenamePrefix, String filenameExtension,
                                             IbUser user) {
        HttpHeaders respHeaders = new HttpHeaders();
        respHeaders.set(HttpHeaders.CONTENT_TYPE, contentType);
        respHeaders.setContentLength(contentLength);
        respHeaders.setContentDispositionFormData("attachment", getAttachmentFilename(user, filenamePrefix, filenameExtension));
        return respHeaders;
    }

    private static String getAttachmentFilename(IbUser user, String filenamePrefix, String extension) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm");
        return filenamePrefix + "-" + user.getCurrentlyLoggedInAt().getName() + "-" + LocalDateTime.now().format(dateTimeFormatter)
                + extension;
    }
}
