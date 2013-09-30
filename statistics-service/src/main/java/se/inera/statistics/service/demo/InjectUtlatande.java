package se.inera.statistics.service.demo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import se.inera.statistics.service.helper.JSONParser;

import javax.annotation.PostConstruct;
import javax.jms.*;
import java.io.*;

public class InjectUtlatande {
    private static final Logger LOG = LoggerFactory.getLogger(InjectUtlatande.class);

    private static String[] personNummers = { "19500118-2046", "19500118-2061", "19500123-2296", "19500213-2461", "19500219-2382", "19500228-1839",
            "19500304-2222", "19500307-2260", "19500315-1858", "19500328-2265", "19500411-2354", "19500430-2120", "19500505-0975", "19500520-2360",
            "19500525-1599", "19500614-2524", "19500626-2546", "19500627-2354", "19500728-2162", "19500729-2435", "19500731-2589", "19500807-2307",
            "19500812-1856", "19500818-2304", "19500821-2226", "19500907-2553", "19500910-1824", "19500916-2040", "19500930-1770", "19501017-2483",
            "19501023-2618", "19501101-2365", "19501105-2478", "19501113-2098" };

    JmsTemplate jmsTemplate;
    private ActiveMQQueue destination;

    public void setup() {
        BrokerService broker = new BrokerService();
        broker.setPersistent(false);
        try {
            broker.addConnector("tcp://localhost:61616");
            broker.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ConnectionFactory cf = new ActiveMQConnectionFactory("tcp://localhost:61616");
        this.jmsTemplate = new JmsTemplate(cf);
        destination = new ActiveMQQueue("intyg.queue");

    }

    @PostConstruct
    public void init() {
        setup();
        publishUtlatanden();
    }

    private void publishUtlatanden() {
        String intyg = readTemplate("/json/fk7263_M_template.json");
        JsonNode intygTree = JSONParser.parse(intyg);
        for (String id : personNummers) {
            JsonNode newPermutation = permutate(intygTree, id);


            simpleSend(newPermutation.toString(), "C" + id);
        }
    }

    public static JsonNode permutate(JsonNode intyg, String id) {
        ObjectNode newPermutation = intyg.deepCopy();
        ObjectNode patientIdNode = (ObjectNode) newPermutation.path("patient").path("id");
        patientIdNode.remove("extension");
        patientIdNode.put("extension", id);
        LOG.info("New permutation" + newPermutation.toString());
        return newPermutation;
    }

    private void simpleSend(final String intyg, final String correlationId) {

        this.jmsTemplate.send(destination, new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                TextMessage message = session.createTextMessage(intyg);
                message.setJMSCorrelationID(correlationId);
                return message;
            }
        });
    }

    private String readTemplate(String path) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(path), "utf8"));
            StringBuilder sb = new StringBuilder();
            for (String line = in.readLine(); line != null; line = in.readLine()) {
                sb.append(line).append('\n');
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
