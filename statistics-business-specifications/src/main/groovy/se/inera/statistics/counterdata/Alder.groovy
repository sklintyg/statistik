package se.inera.statistics.counterdata

import com.fasterxml.jackson.databind.JsonNode
import se.inera.statistics.queue.IntygSender
import se.inera.statistics.service.testsupport.TestData

class Alder {

    String tabell

    Alder(String tabell) {
        this.tabell = tabell
    }

    // {"AgeGroupsResponse":{"ageGroupsRows":[{"AgeGroupsRow":{"key":{"AldersgruppKey":{"period":"2013-11", "hsaId":"ENVE", "grupp":"31-35", "periods":12}}, "male":1, "female":0, "typ":"ENHET"}}], "months":12}}
    public List<Object> query() {
        TestData data = IntygSender.testResult.get(tabell)
        JsonNode testResult = data.jsonNode
        Iterator<JsonNode> rows = testResult.findPath("ageGroupsRows").iterator();
        List<List<List<String>>> rowList = new ArrayList<>()
        while(rows.hasNext()) {
            List<List<String>> cols = new ArrayList<>()
            JsonNode row = rows.next().get("AgeGroupsRow")
            List<String> name = new ArrayList<>()
            name.add("period")
            name.add(row.path("key").path("AldersgruppKey").path("period").textValue())
            cols.add(name)
            List<String> hsaId = new ArrayList<>()
            hsaId.add("hsaId")
            hsaId.add(row.path("key").path("AldersgruppKey").path("hsaId").textValue())
            cols.add(hsaId)
            List<String> grupp = new ArrayList<>()
            grupp.add("grupp")
            grupp.add(row.path("key").path("AldersgruppKey").path("grupp").textValue())
            cols.add(grupp)
            List<String> periods = new ArrayList<>()
            periods.add("periods")
            periods.add(row.path("key").path("AldersgruppKey").path("periods").textValue())
            cols.add(periods)
            List<String> typ = new ArrayList<String>()
            typ.add("typ")
            typ.add(row.get("typ").asText())
            cols.add(typ)
            List<String> female = new ArrayList<String>()
            female.add("kvinnor")
            female.add(row.get("female").asText())
            cols.add(female)
            List<String> male = new ArrayList<String>()
            male.add("män")
            male.add(row.get("male").toString())
            cols.add(male)
            rowList.add(cols)
        }
        rowList
    }
}
