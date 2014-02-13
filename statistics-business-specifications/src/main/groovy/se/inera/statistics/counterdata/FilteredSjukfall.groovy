/*
 * Copyright (C) 2013 - 2014 by Inera AB. All rights reserved.
 * Released under the terms of the CPL Common Public License version 1.0.
 */

package se.inera.statistics.counterdata

import com.fasterxml.jackson.databind.JsonNode
import se.inera.statistics.queue.IntygSender
import se.inera.statistics.service.testsupport.TestData

class FilteredSjukfall {

    String tabell

    FilteredSjukfall(String tabell) {
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
            female.add(row.get("data").get("KonField").get("female").asText())
            cols.add(female);
            List<String> male = new ArrayList<String>()
            male.add("män")
            male.add(row.get("data").get("KonField").get("male").toString())
            cols.add(male);
            if (row.get("data").get("KonField").get("male").intValue() > 0 || row.get("data").get("KonField").get("female").intValue() > 0) {
                rowList.add(cols)
            }
        }
        rowList
    }
}
