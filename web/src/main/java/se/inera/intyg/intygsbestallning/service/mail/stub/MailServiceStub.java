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
package se.inera.intyg.intygsbestallning.service.mail.stub;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import se.inera.intyg.intygsbestallning.service.mail.MailService;

import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.List;

@Service
@Profile(value = {"dev", "test", "mail-stub", "ib-all-stubs"})
public class MailServiceStub implements MailService {

    private List<StubbedEmail> mailStore = new ArrayList<>();

    @Override
    public void sendNotificationToUnit(String mailAddress, String subject, String body) throws MessagingException {
        mailStore.add(new StubbedEmail(mailAddress, subject, body));
    }

    public List<StubbedEmail> getMailStore() {
        return mailStore;
    }

    public void clear() {
        mailStore.clear();
    }
}
