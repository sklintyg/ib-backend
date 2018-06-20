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
package se.inera.intyg.intygsbestallning.common.model;

import java.io.Serializable;

public class NotificationEmail implements Serializable {

    private String toAddress;
    private String subject;
    private String body;

    public String getToAddress() {
        return toAddress;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setBody(String body) {
        this.body = body;
    }


    public static final class NotificationEmailBuilder {
        private String toAddress;
        private String subject;
        private String body;

        private NotificationEmailBuilder() {
        }

        public static NotificationEmailBuilder aNotificationEmail() {
            return new NotificationEmailBuilder();
        }

        public NotificationEmailBuilder withToAddress(String toAddress) {
            this.toAddress = toAddress;
            return this;
        }

        public NotificationEmailBuilder withSubject(String subject) {
            this.subject = subject;
            return this;
        }

        public NotificationEmailBuilder withBody(String body) {
            this.body = body;
            return this;
        }

        public NotificationEmail build() {
            NotificationEmail notificationEmail = new NotificationEmail();
            notificationEmail.setToAddress(toAddress);
            notificationEmail.setSubject(subject);
            notificationEmail.setBody(body);
            return notificationEmail;
        }
    }
}
