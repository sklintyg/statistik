package se.inera.statistics.warehouse

import se.inera.statistics.context.StartUp
import se.inera.statistics.service.helper.DocumentHelper
import se.inera.statistics.service.warehouse.Aisle
import se.inera.statistics.service.warehouse.Warehouse
import se.inera.statistics.service.warehouse.WideLine

class Info {

    String vardgivareId
    String personId

    String getPersonLines() {
        int person = DocumentHelper.patientIdToInt(personId)
        Warehouse bean = (Warehouse) StartUp.context.getBean(se.inera.statistics.service.warehouse.Warehouse.class)
        Aisle aisle = bean.get(vardgivareId)
        Iterator lines = aisle.iterator()
        StringBuilder sb = new StringBuilder()
        while (lines.hasNext()) {
            WideLine line = lines.next()
            if (line.patient == person) {
                sb.append(line.toString()).append('\n')
            }
        }
        sb.toString()
    }

}
