/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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
package se.inera.statistics.config.jms;


import javax.jms.ConnectionFactory;
import javax.jms.MessageListener;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListenerConfigurer;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerEndpointRegistrar;
import org.springframework.jms.config.SimpleJmsListenerEndpoint;
import org.springframework.jms.connection.JmsTransactionManager;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.destination.DestinationResolver;
import org.springframework.jms.support.destination.DynamicDestinationResolver;
import se.inera.statistics.service.processlog.Receiver;
import se.inera.statistics.service.queue.JmsReceiver;

@Configuration
@EnableJms
public class JmsConfig implements JmsListenerConfigurer {

    @Value("${activemq.broker.url}")
    private String brokerUrl;

    @Value("${activemq.broker.username}")
    private String brokerUsername;

    @Value("${activemq.broker.password}")
    private String brokerPassword;

    @Value("${activemq.receiver.queue.name}")
    private String queueName;

    @Bean
    public JmsListenerContainerFactory jmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        factory.setDestinationResolver(destinationResolver());
        factory.setSessionTransacted(true);
        factory.setTransactionManager(jmsTransactionManager());
        factory.setCacheLevelName("CACHE_CONNECTION");
        factory.setConcurrency("1-10");
        return factory;
    }

    @Bean
    public DestinationResolver destinationResolver() {
        return new DynamicDestinationResolver();
    }

    @Bean
    public JmsTransactionManager jmsTransactionManager() {
        return new JmsTransactionManager(connectionFactory());
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        return new ActiveMQConnectionFactory(brokerUsername, brokerPassword, brokerUrl);
    }

    @Bean
    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
        return new JmsTemplate(connectionFactory);
    }

    @Bean
    MessageListener jmsReceiver() {
        return new JmsReceiver();
    }

    @Bean
    Receiver receiver() {
        return new Receiver();
    }

    @Override
    public void configureJmsListeners(JmsListenerEndpointRegistrar registrar) {
        final SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();
        endpoint.setId("jmsEndpoint");
        endpoint.setDestination(queueName);
        endpoint.setMessageListener(jmsReceiver());
        registrar.registerEndpoint(endpoint);
    }
}
