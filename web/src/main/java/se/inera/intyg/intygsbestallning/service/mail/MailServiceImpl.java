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
package se.inera.intyg.intygsbestallning.service.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import se.inera.intyg.intygsbestallning.common.exception.IbErrorCodeEnum;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.common.model.NotificationEmail;

/**
 * Puts an ObjectMessage representing the email on a queue, that will be picked up by the mail-sender Camel route.
 *
 * @author eriklupander
 */
@Service
public class MailServiceImpl implements MailService {

    @Value("${mail.admin}")
    private String adminMailAddress;

    @Value("${mail.from}")
    private String fromAddress;

    @Autowired
    @Qualifier("jmsNotificationMailTemplate")
    private JmsTemplate jmsTemplate;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void sendNotificationToUnit(String mailAddress, String subject, String body) {
        jmsTemplate.send(session -> {
            try {
                return session
                        .createTextMessage(objectMapper.writeValueAsString(NotificationEmail.NotificationEmailBuilder.aNotificationEmail()
                                .withToAddress(mailAddress)
                                .withSubject(subject)
                                .withBody(body)
                                .build()));
            } catch (JsonProcessingException e) {
                throw new IbServiceException(IbErrorCodeEnum.UNKNOWN_INTERNAL_PROBLEM,
                        "Unable to marshal notification message, reason: " + e.getMessage());
            }
        });
    }
}
