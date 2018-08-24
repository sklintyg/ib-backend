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
package se.inera.intyg.intygsbestallning.mailsender;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import org.apache.camel.test.spring.CamelSpringJUnit4ClassRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import se.inera.intyg.intygsbestallning.common.model.NotificationEmail;
import se.inera.intyg.intygsbestallning.mailsender.config.MailSenderTestConfig;
import se.inera.intyg.intygsbestallning.mailsender.service.stub.MailServiceStub;

import java.util.concurrent.TimeUnit;

import static com.jayway.awaitility.Awaitility.await;

@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = MailSenderTestConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class MailSenderIntegrationTest {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private MailServiceStub mailServiceStub;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void init() {
        jmsTemplate.setDefaultDestinationName("ib.mailsender.queue");
        mailServiceStub.clear();
    }

    @Test
    public void testSendMessage() {
        sendMessage(buildNotificationEmail());
        await().atMost(1, TimeUnit.SECONDS).until(() -> 1 == mailServiceStub.getMailStore().size());
    }

    @Test
    public void testSendMessageRetries() {
        mailServiceStub.setFailWithTemporaryException(true);

        sendMessage(buildNotificationEmail());

        await().atMost(10, TimeUnit.SECONDS).until(() -> 6 == mailServiceStub.getAttempts());
    }

    @Test
    public void testSendMessageRetriesForMany() {
        mailServiceStub.setFailWithTemporaryException(true);

        sendMessage(buildNotificationEmail());
        mailServiceStub.setFailWithTemporaryException(false);
        sendMessage(buildNotificationEmail());

        await().atMost(2, TimeUnit.SECONDS).until(() -> 2 == mailServiceStub.getMailStore().size());
    }

    private NotificationEmail buildNotificationEmail() {
        return NotificationEmail.NotificationEmailBuilder.aNotificationEmail()
                .withToAddress("some@inera.se")
                .withSubject("Subject")
                .withBody("Body").build();
    }

    private void sendMessage(final NotificationEmail message) {
        jmsTemplate.send(session -> {
            try {
                return session.createTextMessage(objectMapper.writeValueAsString(message));
            } catch (Exception e) {
                throw Throwables.propagate(e);
            }
        });
    }

}
