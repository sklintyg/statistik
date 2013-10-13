package se.inera.statistics.service.demo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.command.ActiveMQQueue;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import se.inera.statistics.service.helper.DocumentHelper;
import se.inera.statistics.service.helper.JSONParser;
import se.inera.statistics.service.report.model.DiagnosisGroup;
import se.inera.statistics.service.report.util.DiagnosisGroupsUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class InjectUtlatande {
    private static final Logger LOG = LoggerFactory.getLogger(InjectUtlatande.class);

    private static final DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd");
    private static final LocalDate BASE = new LocalDate("2012-03-01");

    private static final List<String> DIAGNOSER = new ArrayList<>();
    private static final List<String> VARDENHETER = Arrays.asList("verksamhet1", "verksamhet2");

    private static Random random = new Random(1234);

    @Autowired
    private ActiveMQQueue destination;

    @Autowired
    private ConnectionFactory connectionFactory;

    private JmsTemplate jmsTemplate;

    public void setup() {
        this.jmsTemplate = new JmsTemplate(connectionFactory);
    }

    static {
        for(DiagnosisGroup mainGroup: DiagnosisGroupsUtil.getAllDiagnosisGroups()) {
            for (DiagnosisGroup group: DiagnosisGroupsUtil.getSubGroups(mainGroup.getId())) {
                DIAGNOSER.add(group.getId().split("-")[0]);
            }
        }
    }

    @PostConstruct
    public void init() {
        setup();
        publishUtlatanden();
    }

    private void publishUtlatanden() {
        String intyg = readTemplate("/json/fk7263_M_template.json");
        JsonNode intygTree = JSONParser.parse(intyg);

        List<String> personNummers = readList("/personnr/testpersoner.log");

        for (String id : personNummers) {
            JsonNode newPermutation = permutate(intygTree, id);
            simpleSend(newPermutation.toString(), "C" + id);
        }
    }

    public static JsonNode permutate(JsonNode intyg, String id) {
        ObjectNode newPermutation = intyg.deepCopy();
        ObjectNode patientIdNode = (ObjectNode) newPermutation.path("patient").path("id");
        patientIdNode.put("extension", id);

        // CHECKSTYLE:OFF MagicNumber
        LocalDate start = BASE.plusMonths((int) (Math.random() * 19)).plusDays((int) (Math.random() * 30));
        LocalDate end = start.plusDays((int) (Math.random() * 30 + 7));
        // CHECKSTYLE:ON MagicNumber

        newPermutation.put("validFromDate", FORMATTER.print(start));
        newPermutation.put("validToDate", FORMATTER.print(end));

        ((ObjectNode) newPermutation.path("skapadAv").path("vardenhet").path("id")).put("extension",random(VARDENHETER));
        for (JsonNode observation: newPermutation.path("observations")) {
            if (DocumentHelper.DIAGNOS_MATCHER.match(observation)) {
                ((ObjectNode)observation.path("observationsKod")).put("code", random(DIAGNOSER));
            }
        }

        for (JsonNode observation: newPermutation.path("observations")) {
            if (DocumentHelper.ARBETSFORMAGA_MATCHER.match(observation)) {
                int arbetsformaga = random.nextInt(3) * 25;
                ((ObjectNode)observation.path("varde").path(0)).put("quantity", arbetsformaga);
            }
        }

        LOG.info("New permutation" + newPermutation.toString());
        return newPermutation;
    }

    private static <T> T random(List<T> list) {
        return list.get(random.nextInt(list.size()));
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

    private List<String> readList(String path) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(path), "utf8"));
            List<String> list = new ArrayList<>();
            for (String line = in.readLine(); line != null; line = in.readLine()) {
                list.add(line);
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
