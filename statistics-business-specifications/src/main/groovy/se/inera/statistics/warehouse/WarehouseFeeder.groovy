package se.inera.statistics.warehouse
import com.fasterxml.jackson.databind.JsonNode
import org.joda.time.LocalDate
import se.inera.statistics.context.StartUp
import se.inera.statistics.service.helper.UtlatandeBuilder
import se.inera.statistics.service.hsa.HSAKey
import se.inera.statistics.service.hsa.HSAService
import se.inera.statistics.service.processlog.EventType
import se.inera.statistics.service.warehouse.FactPopulator
import se.inera.statistics.service.warehouse.Warehouse
import se.inera.statistics.service.warehouse.WidelineConverter
import se.inera.statistics.service.warehouse.model.db.WideLine

import static se.inera.statistics.service.helper.DocumentHelper.*

class WarehouseFeeder {
    static int logId

    UtlatandeBuilder builder1 = new UtlatandeBuilder("/json/integration/intyg1.json")
    UtlatandeBuilder builder2 = new UtlatandeBuilder("/json/integration/intyg2.json")
    UtlatandeBuilder builder3 = new UtlatandeBuilder("/json/integration/intyg3.json")
    UtlatandeBuilder builder4 = new UtlatandeBuilder("/json/integration/intyg4.json")
    UtlatandeBuilder[] builders = [builder1, builder2, builder3, builder4]

    public boolean insertIntyg(String person, String diagnos, String start1, String stop1, String grad1, String start2, String stop2, String grad2, String start3, String stop3, String grad3, String start4, String stop4, String grad4, String enhet, String vardgivare, String trackingId) {
        insertIntyg(EventType.CREATED.name(), person, diagnos, start1, stop1, grad1, start2, stop2, grad2, start3, stop3, grad3, start4, stop4, grad4, enhet, vardgivare, trackingId)
    }

    public boolean insertIntyg(String typ, String person, String diagnos, String start1, String stop1, String grad1, String start2, String stop2, String grad2, String start3, String stop3, String grad3, String start4, String stop4, String grad4, String enhet, String vardgivare, String trackingId) {
        Warehouse bean = (Warehouse) StartUp.context.getBean("warehouse")
        WidelineConverter widelineConverter = (WidelineConverter) StartUp.context.getBean(WidelineConverter.class)
        FactPopulator factPopulator = (FactPopulator) StartUp.context.getBean(FactPopulator.class)
        HSAService hsaService = (HSAService) StartUp.context.getBean(HSAService.class)
        List<LocalDate> starts = new ArrayList<>()
        List<LocalDate> stops = new ArrayList<>()
        List<String> grads = new ArrayList<>()
        if (!"".equals(start1)) {
            starts.add(new LocalDate(start1))
            stops.add(new LocalDate(stop1))
            grads.add(toGrad(grad1))
        }
        if (!"".equals(start2)) {
            starts.add(new LocalDate(start2))
            stops.add(new LocalDate(stop2))
            grads.add(toGrad(grad2))
        }
        if (!"".equals(start3)) {
            starts.add(new LocalDate(start3));
            stops.add(new LocalDate(stop3));
            grads.add(toGrad(grad3))
        }
        if (!"".equals(start4)) {
            starts.add(new LocalDate(start4));
            stops.add(new LocalDate(stop4));
            grads.add(toGrad(grad4))
        }
        if (starts.size() < 1) {
            false
        } else {
            if (EventType.CREATED.name().equals(typ)) {
                JsonNode document = builders[starts.size() - 1].build(person, starts, stops, enhet, vardgivare, diagnos, grads)
                HSAKey hsaKey = extractHSAKey(document)
                JsonNode hsaInfo = hsaService.getHSAInfo(hsaKey)
                document = prepare(document, hsaInfo)
                println(document)
                WideLine line = widelineConverter.toWideline(document, hsaInfo, logId++)
                bean.accept(factPopulator.toFact(line), hsaKey.vardgivareId)
            }
            true
        }
    }
    private static HSAKey extractHSAKey(JsonNode document) {
        String vardgivareId = getVardgivareId(document)
        String enhetId = getEnhetId(document)
        String lakareId = getLakarId(document)
        return new HSAKey(vardgivareId, enhetId, lakareId)
    }


    private static String toGrad(String s) {
        if (s.endsWith("%")) {
            s = s.substring(0, s.size() - 1);
        }
        int i = 100 - Integer.parseInt(s);
        return "" + i;
    }

}
