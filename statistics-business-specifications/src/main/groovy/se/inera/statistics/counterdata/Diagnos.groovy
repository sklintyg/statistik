/*
 * Copyright (C) 2013 - 2014 by Inera AB. All rights reserved.
 * Released under the terms of the CPL Common Public License version 1.0.
 */

package se.inera.statistics.counterdata

import com.fasterxml.jackson.databind.JsonNode
import se.inera.statistics.queue.IntygSender
import se.inera.statistics.service.testsupport.TestData

class Diagnos {

    String tabell

    Diagnos(String tabell) {
        this.tabell = tabell
    }

    // {"DiagnosgruppResponse":{"diagnosgrupp":[{"Avsnitt":{"id":"A00-B99", "name":"Vissa infektionssjukdomar och parasitsjukdomar",...}}], "rows":[{"KonDataRow":{"name":"jan 2012", "data":[{"KonField":{"female":0, "male":0}},...]}}, ...]}}
    public List<Object> query() {
        TestData data = IntygSender.testResult.get(tabell)
        JsonNode testResult = data.jsonNode
        Iterator<JsonNode> grupps = testResult.findPath("avsnitts").iterator()
        List<String> gruppList = new ArrayList<>();
        while (grupps.hasNext()) {
            gruppList.add(grupps.next().path("Avsnitt").path("id").textValue())
        }
        Iterator<JsonNode> rows = testResult.findPath("rows").iterator();
        List<List<List<String>>> rowList = new ArrayList<>()
        while (rows.hasNext()) {
            JsonNode row = rows.next().get("KonDataRow")
            String periodString = row.path("name").textValue()
            int i = 0;
            Iterator<JsonNode> gruppData = row.path("data").iterator()
            while (gruppData.hasNext()) {
                List<List<String>> cols = new ArrayList<>()
                JsonNode gruppNode = gruppData.next()
                List<String> period = new ArrayList<>()
                period.add("period")
                period.add(periodString)
                cols.add(period)
                List<String> grupp = new ArrayList<>()
                grupp.add("grupp")
                grupp.add(gruppList.get(i))
                cols.add(grupp)
                List<String> female = new ArrayList<String>()
                female.add("kvinnor")
                female.add(gruppNode.get("KonField").get("female").asText())
                cols.add(female);
                List<String> male = new ArrayList<String>()
                male.add("män")
                male.add(gruppNode.get("KonField").get("male").toString())
                cols.add(male);
                rowList.add(cols)
                i++
            }
        }
        rowList
    }
}
