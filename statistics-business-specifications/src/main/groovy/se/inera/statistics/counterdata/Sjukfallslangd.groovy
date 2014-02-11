package se.inera.statistics.counterdata

import com.fasterxml.jackson.databind.JsonNode
import se.inera.statistics.queue.IntygSender
import se.inera.statistics.service.testsupport.TestData

class Sjukfallslangd {

    String tabell

    Sjukfallslangd(String tabell) {
        this.tabell = tabell
    }

    // {"SickLeaveLengthResponse":{"sickLeaveGroupsRows":[{"SickLeaveLengthRow":{"key":{"SickLeaveLengthKey":{"period":"2013-11", "hsaId":"ENVE", "grupp":"<15 dagar", "periods":12}}, "typ":"ENHET", "male":1, "female":1}}, {"SickLeaveLengthRow":{"key":{"SickLeaveLengthKey":{"period":"2013-11", "hsaId":"ENVE", "grupp":"15-30 dagar", "periods":12}}, "typ":"ENHET", "male":2, "female":0}}, {"SickLeaveLengthRow":{"key":{"SickLeaveLengthKey":{"period":"2013-11", "hsaId":"ENVE", "grupp":"31-90 dagar", "periods":12}}, "typ":"ENHET", "male":0, "female":1}}, {"SickLeaveLengthRow":{"key":{"SickLeaveLengthKey":{"period":"2013-11", "hsaId":"ENVE", "grupp":">365 dagar", "periods":12}}, "typ":"ENHET", "male":0, "female":1}}], "months":12}}
    public List<Object> query() {
        TestData data = IntygSender.testResult.get(tabell)
        JsonNode testResult = data.jsonNode
        Iterator<JsonNode> rows = testResult.findPath("sickLeaveGroupsRows").iterator();
        List<List<List<String>>> rowList = new ArrayList<>()
        while(rows.hasNext()) {
            List<List<String>> cols = new ArrayList<>()
            JsonNode row = rows.next().get("SickLeaveLengthRow")
            List<String> period = new ArrayList<>()
            period.add("period")
            period.add(row.path("key").path("SickLeaveLengthKey").path("period").textValue())
            cols.add(period)
            List<String> hsaId = new ArrayList<>()
            hsaId.add("hsaId")
            hsaId.add(row.path("key").path("SickLeaveLengthKey").path("hsaId").textValue())
            cols.add(hsaId)
            List<String> grupp = new ArrayList<>()
            grupp.add("grupp")
            grupp.add(row.path("key").path("SickLeaveLengthKey").path("grupp").textValue())
            cols.add(grupp)
            List<String> typ = new ArrayList<String>()
            typ.add("typ")
            typ.add(row.get("typ").asText())
            cols.add(typ);
            List<String> female = new ArrayList<String>()
            female.add("kvinnor")
            female.add(row.get("female").asText())
            cols.add(female);
            List<String> male = new ArrayList<String>()
            male.add("män")
            male.add(row.get("male").toString())
            cols.add(male);
            rowList.add(cols)
        }
        rowList
    }
}
