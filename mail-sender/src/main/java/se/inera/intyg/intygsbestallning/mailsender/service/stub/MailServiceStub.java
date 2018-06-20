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
package se.inera.intyg.intygsbestallning.mailsender.service.stub;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import se.inera.intyg.intygsbestallning.mailsender.exception.PermanentException;
import se.inera.intyg.intygsbestallning.mailsender.service.IbMailSender;
import se.inera.intyg.intygsbestallning.common.model.NotificationEmail;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Service
@Profile(value = { "dev", "test", "mail-stub", "ib-all-stubs" })
public class MailServiceStub implements IbMailSender {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private List<NotificationEmail> mailStore = new ArrayList<>();

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void process(String notificationEmailJson) throws Exception {
        LOG.info("ENTER - MailServiceStub");
        NotificationEmail notificationEmail = null;
        try {
            notificationEmail = objectMapper.readValue(notificationEmailJson, NotificationEmail.class);
            mailStore.add(notificationEmail);
        } catch (IOException e) {
            LOG.error("Error deserializing email notification TextMessage: " + e.getMessage());
            throw new PermanentException("Unable to parse JSON TextMessage, this is a permanent error.");
        }
    }

    public List<NotificationEmail> getMailStore() {
        return mailStore;
    }

    public void clear() {
        mailStore.clear();
    }
}
