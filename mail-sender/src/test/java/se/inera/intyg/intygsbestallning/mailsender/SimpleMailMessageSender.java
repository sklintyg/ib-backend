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

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

/**
 * Stand-alone "application" that can connect to a local running ActiveMQ and send
 * a MailMessage to localhost:61616. Useful for debugging / troubleshooting
 * purposes.
 */
public class SimpleMailMessageSender {

    private static String url = "tcp://localhost:61616?jms.nonBlockingRedelivery=true&jms.redeliveryPolicy.maximumRedeliveries=6&jms.redeliveryPolicy.maximumRedeliveryDelay=6000&jms.redeliveryPolicy.initialRedeliveryDelay=4000&jms.redeliveryPolicy.useExponentialBackOff=true&jms.redeliveryPolicy.backOffMultiplier=2"; //ActiveMQConnection.DEFAULT_BROKER_URL;
   // private static String url = "tcp://activemq-test-61616-intygstjanster-test.192.168.99.100.nip.io:61616";

    private static String subject = "ib.mailsender.queue"; // Queue Name

    public static void main(String[] args) throws JMSException, JsonProcessingException {
        String jsonMessage = "{\"key\":\"value\"}";
        System.out.println("Built message: " + jsonMessage);

        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        Connection connection = connectionFactory.createConnection();
        connection.start();

        Session session = connection.createSession(false,
                Session.AUTO_ACKNOWLEDGE);

        Destination destination = session.createQueue(subject);
        MessageProducer producer = session.createProducer(destination);


        // Here we are sending the message!
        TextMessage message = session.createTextMessage(jsonMessage);

        producer.send(message);
        System.out.println("Sent message: '" + message.getText() + "'");

        connection.close();
    }

}
