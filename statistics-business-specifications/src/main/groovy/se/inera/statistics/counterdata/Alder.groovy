/*
 * Copyright (C) 2013 - 2014 by Inera AB. All rights reserved.
 * Released under the terms of the CPL Common Public License version 1.0.
 */

package se.inera.statistics.counterdata

import com.fasterxml.jackson.databind.JsonNode
import se.inera.statistics.queue.IntygSender
import se.inera.statistics.service.testsupport.TestData

class Alder {

    String tabell

    Alder(String tabell) {
        this.tabell = tabell
    }

    // {"AgeGroupsResponse":{"ageGroupsRows":[{"AldersgruppRow":{"key":{"AldersgruppKey":{"period":"2013-11", "hsaId":"ENVE", "grupp":"31-35", "periods":12}}, "male":1, "female":0, "typ":"ENHET"}}], "months":12}}
    public List<Object> query() {
        TestData data = IntygSender.testResult.get(tabell)
        JsonNode testResult = data.jsonNode
        Iterator<JsonNode> rows = testResult.findPath("rows").iterator();
        List<List<List<String>>> rowList = new ArrayList<>()
        while(rows.hasNext()) {
            JsonNode row = rows.next().path("SimpleKonDataRow")
            int femaleNum = row.path("data").path("KonField").path("female").asInt()
            int maleNum = row.path("data").path("KonField").path("male").asInt()
            if (femaleNum > 0 || maleNum > 0) {
                List<List<String>> cols = new ArrayList<>()
                cols.add(["grupp", row.path("name").textValue()]);
                cols.add(["kvinnor", femaleNum.toString()]);
                cols.add(["män", maleNum.toString()])
                rowList.add(cols)
            }
        }
        rowList
    }
}
