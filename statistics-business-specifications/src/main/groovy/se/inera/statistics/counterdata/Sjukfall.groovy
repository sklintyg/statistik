package se.inera.statistics.counterdata

import com.fasterxml.jackson.databind.JsonNode
import se.inera.statistics.queue.IntygSender
import se.inera.statistics.service.testsupport.TestData

class Sjukfall {

    String tabell

    Sjukfall(String tabell) {
        this.tabell = tabell
    }

    public List<Object> query() {
        TestData data = IntygSender.testResult.get(tabell)
        JsonNode testResult = data.jsonNode
        Iterator<JsonNode> rows = testResult.findPath("rows").iterator();
        List<List<List<String>>> rowList = new ArrayList<>()
        while(rows.hasNext()) {
            List<List<String>> cols = new ArrayList<>()
            JsonNode row = rows.next().get("SimpleDualSexDataRow")
            List<String> name = new ArrayList<>()
            name.add("period")
            name.add(row.path("name").textValue())
            cols.add(name)
            List<String> female = new ArrayList<String>()
            female.add("kvinnor")
            female.add(row.get("data").get("DualSexField").get("female").asText())
            cols.add(female);
            List<String> male = new ArrayList<String>()
            male.add("män")
            male.add(row.get("data").get("DualSexField").get("male").toString())
            cols.add(male);
            rowList.add(cols)
        }
        rowList
    }
}
