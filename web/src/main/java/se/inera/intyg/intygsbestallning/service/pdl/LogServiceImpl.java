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
package se.inera.intyg.intygsbestallning.service.pdl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.logmessages.ActivityType;
import se.inera.intyg.infra.logmessages.PdlLogMessage;
import se.inera.intyg.infra.logmessages.ResourceType;
import se.inera.intyg.intygsbestallning.common.integration.json.CustomObjectMapper;
import se.inera.intyg.intygsbestallning.service.pdl.dto.PDLLoggable;
import se.inera.intyg.intygsbestallning.service.user.UserService;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.List;

/**
 * Implementation of service for logging user actions according to PDL requirements.
 *
 * @author eriklupander
 */
@Service
public class LogServiceImpl implements LogService {

    private static final Logger LOG = LoggerFactory.getLogger(LogServiceImpl.class);

    @Autowired(required = false)
    @Qualifier("jmsPDLLogTemplate")
    private JmsTemplate jmsTemplate;

    @Autowired
    private PdlLogMessageFactory pdlLogMessageFactory;

    @Autowired
    private UserService userService;

    @PostConstruct
    public void checkJmsTemplate() {
        if (jmsTemplate == null) {
            LOG.error("PDL logging is disabled!");
        }
    }


    @Override
    public void logVisaBestallningarLista(List<? extends PDLLoggable> utredningListItems, ActivityType activityType,
                                          ResourceType resourceType) {
        if (utredningListItems == null) {
            LOG.debug("No utredningar for PDL logging, not logging.");
            return;
        }
        PdlLogMessage pdlLogMessage =
            pdlLogMessageFactory.buildLogMessage(utredningListItems, activityType, resourceType, userService.getUser());

        send(pdlLogMessage);
    }

    private void send(PdlLogMessage pdlLogMessage) {

        if (jmsTemplate == null) {
            LOG.error("Could not log list of IntygsData, PDL logging is disabled or JMS sender template is null.");
            return;
        }

        LOG.info("Logging SjukfallIntygsData for activityType {} having {}", pdlLogMessage.getActivityType().name(),
                pdlLogMessage.getPdlResourceList().size());
        try {
            jmsTemplate.send(new MC(pdlLogMessage));
        } catch (JmsException e) {
            LOG.error("Could not log list of IntygsData", e);
            throw e;
        }

    }

    private static final class MC implements MessageCreator {
        private final PdlLogMessage logMsg;

        private final ObjectMapper objectMapper = new CustomObjectMapper();

        private MC(PdlLogMessage logMsg) {
            this.logMsg = logMsg;
        }

        @Override
        public Message createMessage(Session session) throws JMSException {
            try {
                return session.createTextMessage(objectMapper.writeValueAsString(this.logMsg));
            } catch (JsonProcessingException e) {
                throw new IllegalArgumentException("Could not serialize log message of type '" + logMsg.getClass().getName()
                        + "' into JSON, message: " + e.getMessage(), e);
            }
        }
    }
}
