package se.inera.statistics.spec

import se.inera.statistics.service.countypopulation.CountyPopulation
import se.inera.statistics.service.report.model.KonField
import se.inera.statistics.web.reports.ReportsUtil

import java.time.LocalDate

class AngivnaInvanarantal {

    private final ReportsUtil reportsUtil = new ReportsUtil()

    String länkod
    int kvinnor
    int män
    String datum

    def values = [:]

    public void beginTable() {
    }

    public void reset() {
        länkod = null
        kvinnor = 0
        män = 0
    }

    public void setKommentar(String kommentar) {}

    public void execute() {
        def valuesPerDate = values.get(datum, [:])
        valuesPerDate[länkod] = new KonField(kvinnor, män)
    }

    public void endTable() {
        values.each { k, v ->
            reportsUtil.insertCountyPopulation(v, k)
        }
    }

}
