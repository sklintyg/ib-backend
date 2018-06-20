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
package se.inera.intyg.intygsbestallning.mailsender.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Body;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import se.inera.intyg.intygsbestallning.common.model.NotificationEmail;
import se.inera.intyg.intygsbestallning.mailsender.exception.PermanentException;
import se.inera.intyg.intygsbestallning.mailsender.exception.TemporaryException;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Component
@Profile(value = {"demo", "qa", "prod"})
public class IbMailSenderImpl implements IbMailSender {

    private static final Logger LOG = LoggerFactory.getLogger(IbMailSenderImpl.class);

    @Value("${mail.admin}")
    private String adminMailAddress;

    @Value("${mail.from}")
    private String fromAddress;

    @Autowired
    private JavaMailSender mailSender;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void process(@Body String notificationEmailJson) throws Exception {
        LOG.info("ENTER - process notificationEmail");
        NotificationEmail notificationEmail = jsonToNotificationEmail(notificationEmailJson);

        MimeMessage message = mailSender.createMimeMessage();
        message.setFrom(new InternetAddress(fromAddress));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(notificationEmail.getToAddress()));

        message.setSubject(notificationEmail.getSubject());
        message.setContent(notificationEmail.getBody(), "text/html; charset=utf-8");
        try {
            mailSender.send(message);
        } catch (Exception e) {
            throw new TemporaryException("The notification mail could not be sent, will try to redeliver up to 5 times.");
        }
    }

    private NotificationEmail jsonToNotificationEmail(@Body String notificationEmailJson) throws PermanentException {
        try {
            return objectMapper.readValue(notificationEmailJson, NotificationEmail.class);
        } catch (Exception e) {
            LOG.error("Error deserializing email notification TextMessage: " + e.getMessage());
            throw new PermanentException("Unable to parse JSON TextMessage, this is a permanent error.");
        }
    }
}
