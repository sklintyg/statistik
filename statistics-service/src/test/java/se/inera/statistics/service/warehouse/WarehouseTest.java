package se.inera.statistics.service.warehouse;

import com.fasterxml.jackson.databind.JsonNode;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.junit.Test;
import se.inera.statistics.service.JSONSource;
import se.inera.statistics.service.helper.DocumentHelper;
import se.inera.statistics.service.helper.HSAServiceHelper;
import se.inera.statistics.service.helper.JSONParser;
import se.inera.statistics.service.hsa.HSAService;
import se.inera.statistics.service.hsa.HSAServiceMock;

public class WarehouseTest {
    private static final int WAREHOUSE_SIZE = 200000;
    private HSAService hsaService = new HSAServiceMock();

    private JsonNode document = JSONParser.parse(JSONSource.readTemplateAsString());
    private JsonNode hsaInfo = hsaService.getHSAInfo(null);

    @Test
    public void createLineFromIntygWith1Period() {
        document = DocumentHelper.anonymize(document);
        int lan = Integer.parseInt(HSAServiceHelper.getLan(hsaInfo));
        int kommun = HSAServiceHelper.getKommun(document);
        int forsamling = 0;
        int enhet = DocumentHelper.getEnhetAndRemember(document);
        int lakarintyg = DocumentHelper.getLakarIntyg(document);
        int patient = DocumentHelper.getPatientAndRemember(document);
        LocalDate kalenderStart = new LocalDate(DocumentHelper.getForstaNedsattningsdag(document));
        LocalDate kalenderEnd = new LocalDate(DocumentHelper.getSistaNedsattningsdag(document));
        int kalenderperiod = Days.daysBetween(kalenderStart, kalenderEnd).getDays();
        int kon = DocumentHelper.getKon(document).indexOf('k');
        int alder = DocumentHelper.getAge(document);
        int diagnoskapitel = 0;
        int diagnosavsnitt = 0;
        int diagnoskategori = 0;
        int sjukskrivningsgrad = 100 - Integer.parseInt(DocumentHelper.getArbetsformaga(document).get(0));
        int sjukskrivningslangd = kalenderperiod;
        int lakarkon = -1;
        int lakaralder = 0;
        int lakarbefattning = 0;

        WideLine result = new WideLine(lan, kommun, forsamling, enhet, lakarintyg, patient, kalenderperiod, kon, alder, diagnoskapitel, diagnosavsnitt, diagnoskategori, sjukskrivningsgrad, sjukskrivningslangd, lakarkon, lakaralder, lakarbefattning);

        System.out.println(result);
    }

    @Test
    public void generateALotOfSimpleLines() {
        Warehouse warehouse = new Warehouse(WAREHOUSE_SIZE);
        document = DocumentHelper.anonymize(document);
        for (int i = 0; i < WAREHOUSE_SIZE; i++) {
            int lan = Integer.parseInt(HSAServiceHelper.getLan(hsaInfo));
            int kommun = HSAServiceHelper.getKommun(document);
            int forsamling = 0;
            int enhet = DocumentHelper.getEnhetAndRemember(document);
            int lakarintyg = DocumentHelper.getLakarIntyg(document);
            int patient = DocumentHelper.getPatientAndRemember(document);
            LocalDate kalenderStart = new LocalDate(DocumentHelper.getForstaNedsattningsdag(document));
            LocalDate kalenderEnd = new LocalDate(DocumentHelper.getSistaNedsattningsdag(document));
            int kalenderperiod = Days.daysBetween(kalenderStart, kalenderEnd).getDays();
            int kon = DocumentHelper.getKon(document).indexOf('k');
            int alder = DocumentHelper.getAge(document);
            int diagnoskapitel = 0;
            int diagnosavsnitt = 0;
            int diagnoskategori = 0;
            int sjukskrivningsgrad = 100 - Integer.parseInt(DocumentHelper.getArbetsformaga(document).get(0));
            int sjukskrivningslangd = kalenderperiod;
            int lakarkon = -1;
            int lakaralder = 0;
            int lakarbefattning = 0;

            WideLine line = new WideLine(lan, kommun, forsamling, enhet, lakarintyg, patient, kalenderperiod, kon, alder, diagnoskapitel, diagnosavsnitt, diagnoskategori, sjukskrivningsgrad, sjukskrivningslangd, lakarkon, lakaralder, lakarbefattning);
            warehouse.setLine(i, line);
        }

        try {
            System.out.println("sleeping");
            Thread.sleep(1000000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
