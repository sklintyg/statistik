/*
 * Copyright (C) 2013 - 2014 by Inera AB. All rights reserved.
 * Released under the terms of the CPL Common Public License version 1.0.
 */

package se.inera.statistics.counterdata

import com.fasterxml.jackson.databind.JsonNode
import se.inera.statistics.queue.IntygSender
import se.inera.statistics.service.testsupport.TestData

class Sjukskrivningsgrad {
    String tabell

    Sjukskrivningsgrad(String tabell) {
        this.tabell = tabell
    }

    // {"SjukskrivningsgradResponse":{"degreesOfSickLeave":["Antal sjukfall per 25%", "Antal sjukfall per 50%", "Antal sjukfall per 75%", "Antal sjukfall per 100%" ], "rows":[{"KonDataRow":{"name":"jan 2012", "data":[{"KonField":{"female":0, "male":0}}, ...]}}, ...]}}
    public List<Object> query() {
        TestData data = IntygSender.testResult.get(tabell)
        JsonNode testResult = data.jsonNode
        Iterator<JsonNode> grads = testResult.findPath("degreesOfSickLeave").iterator()
        List<String> gradList = new ArrayList<>();
        while(grads.hasNext()) {
            gradList.add(grads.next().textValue())
        }
        Iterator<JsonNode> rows = testResult.findPath("rows").iterator()
        List<List<List<String>>> rowList = new ArrayList<>()
        while(rows.hasNext()) {
            JsonNode row = rows.next().get("KonDataRow")
            String periodString = row.path("name").textValue()
            int i = 0;
            Iterator<JsonNode> gradData = row.path("data").iterator()
            while (gradData.hasNext()) {
                List<List<String>> cols = new ArrayList<>()
                JsonNode gradNode = gradData.next()
                List<String> period = new ArrayList<>()
                period.add("period")
                period.add(periodString)
                cols.add(period)
                List<String> grad = new ArrayList<>()
                grad.add("grad")
                grad.add(gradList.get(i))
                cols.add(grad)
                List<String> female = new ArrayList<String>()
                female.add("kvinnor")
                female.add(gradNode.get("KonField").get("female").asText())
                cols.add(female);
                List<String> male = new ArrayList<String>()
                male.add("män")
                male.add(gradNode.get("KonField").get("male").toString())
                cols.add(male);
                rowList.add(cols)
                i++
            }
        }
        rowList
    }

}
