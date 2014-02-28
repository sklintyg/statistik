package se.inera.statistics.service.warehouse;

import com.fasterxml.jackson.databind.JsonNode;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.junit.Ignore;
import org.junit.Test;
import se.inera.statistics.service.JSONSource;
import se.inera.statistics.service.helper.DocumentHelper;
import se.inera.statistics.service.helper.HSAServiceHelper;
import se.inera.statistics.service.helper.JSONParser;
import se.inera.statistics.service.hsa.HSAService;
import se.inera.statistics.service.hsa.HSAServiceMock;

import static org.junit.Assert.assertEquals;

public class WarehouseTest {
//    private static final LocalDate BASE_DATE = new LocalDate("2000-01-01");
    private static final int WAREHOUSE_SIZE = 200000;
    private HSAService hsaService = new HSAServiceMock();

    private JsonNode rawDocument = JSONParser.parse(JSONSource.readTemplateAsString());
    private JsonNode hsaInfo = hsaService.getHSAInfo(null);

    @Test
    public void createLineFromIntygWith1Period() {
        JsonNode document = DocumentHelper.anonymize(rawDocument);
        WideLine result = buildLine(rawDocument, document);

        System.out.println(result);
    }

    @Test
    public void generateALotOfSimpleLines() {
        Warehouse lines = generateLines(WAREHOUSE_SIZE);
        assertEquals(WAREHOUSE_SIZE, lines.getSize());
    }

    @Test
    public void wideLineHasExpectedValues() {
        WideLine line = buildLine(rawDocument);
        assertEquals(33, line.alder);
        assertEquals(770430291, line.patient);
        assertEquals(1, line.enhet);
        assertEquals(10, line.lan);
        assertEquals(80, line.kommun);
    }

    @Test
    @Ignore
    public void iterateOverLines() {
        Warehouse warehouse = generateLines(WAREHOUSE_SIZE);
        for (int i = 0; i < 10; i++) {
            long start = System.currentTimeMillis();
            long sum = 0;
            for (WideLine line: warehouse) {
                sum += line.alder;
            }
            long end = System.currentTimeMillis();
            System.err.println((end - start) + " " + sum);
        }
        try {
            System.out.println("sleeping");
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private Warehouse generateLines(int warehouseSize) {
        Warehouse warehouse = new Warehouse(warehouseSize);
        for (int i = 0; i < WAREHOUSE_SIZE; i++) {
            WideLine line = buildLine(rawDocument);
            warehouse.setLine(i, line);
        }
        return warehouse;
    }

    private WideLine buildLine(JsonNode rawDocument) {
        return buildLine(rawDocument, DocumentHelper.anonymize(rawDocument));
    }

    private WideLine buildLine(JsonNode rawDocument, JsonNode document) {
        int lan = Integer.parseInt(HSAServiceHelper.getLan(hsaInfo));
        int kommun = HSAServiceHelper.getKommun(hsaInfo);
        int forsamling = 0;
        int enhet = DocumentHelper.getEnhetAndRemember(document);
        int lakarintyg = DocumentHelper.getLakarIntyg(document);
        int patient = DocumentHelper.getPatient(rawDocument);
//        int forstaNedsattningsdag = Days.daysBetween(BASE_DATE, new LocalDate(DocumentHelper.getForstaNedsattningsdag(document))).getDays();
//        int sistaNedsattningsdag = Days.daysBetween(BASE_DATE, new LocalDate(DocumentHelper.getForstaNedsattningsdag(document))).getDays();
        LocalDate kalenderStart = new LocalDate(DocumentHelper.getForstaNedsattningsdag(document));
        LocalDate kalenderEnd = new LocalDate(DocumentHelper.getSistaNedsattningsdag(document));
        int kalenderperiod = Days.daysBetween(kalenderStart, kalenderEnd).getDays();
        int kon = DocumentHelper.getKon(document).indexOf('k');
        int alder = DocumentHelper.getAge(document);
        int diagnoskapitel = Convert.toInt(DocumentHelper.getDiagnos(document));
        int diagnosavsnitt = Convert.toInt(DocumentHelper.getDiagnos(document));;
        int diagnoskategori = Convert.toInt(DocumentHelper.getDiagnos(document));;
        int sjukskrivningsgrad = 100 - Integer.parseInt(DocumentHelper.getArbetsformaga(document).get(0));
        int sjukskrivningslangd = kalenderperiod;
        int lakarkon = -1;
        int lakaralder = 0;
        int lakarbefattning = 0;

        WideLine line = new WideLine(lan, kommun, forsamling, enhet, lakarintyg, patient, kalenderperiod, kon, alder, diagnoskapitel, diagnosavsnitt, diagnoskategori, sjukskrivningsgrad, sjukskrivningslangd, lakarkon, lakaralder, lakarbefattning);
        return line;
    }
}
